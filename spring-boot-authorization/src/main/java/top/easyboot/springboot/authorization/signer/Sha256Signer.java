package top.easyboot.springboot.authorization.signer;

import top.easyboot.springboot.authorization.exception.AuthSignException;
import top.easyboot.springboot.authorization.utils.DateUtils;
import top.easyboot.springboot.authorization.utils.Hex;
import top.easyboot.springboot.authorization.utils.HttpUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.*;

public class Sha256Signer {
    /**
     * 请求uri
     */
    protected URI uri;
    /**
     * 请求方式
     */
    protected String method = "";
    /**
     * 请求头
     */
    protected Map<String, String> headers;
    /**
     * 授权版本
     */
    protected String authVersion = "app-auth-v2";
    /**
     * 授权id
     */
    protected String accessKeyId = "";
    /**
     * 授权key
     */
    protected String accessKey = "";
    /**
     * 请求id，可以为空，仅仅是加强破解难度
     */
    protected String requestId = "";
    /**
     * 客户端id，可以为空，为了加强识别
     */
    protected String clientCard = "";
    /**
     * 签名时间
     */
    protected String signTimeString = "";
    /**
     * 签名过期时间
     */
    protected int expiredTimeOffset = 1800;
    /**
     * 不签名的QueryKey
     */
    protected Set<String> noSignQueryKeys = new HashSet<>();
    /**
     * 不签名的HeaderKey
     */
    protected Set<String> noSignHeadersKeys = new HashSet<>();

    /**
     * 签名后才会出现
     */
    protected String signature = null;


    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final Charset UTF8 = Charset.forName(DEFAULT_ENCODING);

    public Set<String> getNoSignQueryKeys() {
        return noSignQueryKeys;
    }

    public void setNoSignQueryKeys(Set<String> noSignQueryKeys) {
        this.noSignQueryKeys = noSignQueryKeys;
    }

    public Set<String> getNoSignHeadersKeys() {
        return noSignHeadersKeys;
    }

    public void setNoSignHeadersKeys(Set<String> noSignHeadersKeys) {
        this.noSignHeadersKeys = noSignHeadersKeys;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getAuthVersion() {
        return authVersion;
    }

    public void setAuthVersion(String authVersion) {
        this.authVersion = authVersion;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClientCard() {
        return clientCard;
    }

    public void setClientCard(String clientCard) {
        this.clientCard = clientCard;
    }

    public String getSignTimeString() {
        return signTimeString;
    }

    public void setSignTimeString(String signTimeString) {
        this.signTimeString = signTimeString;
    }

    public int getExpiredTimeOffset() {
        return expiredTimeOffset;
    }

    public void setExpiredTimeOffset(int expiredTimeOffset) {
        this.expiredTimeOffset = expiredTimeOffset;
    }

    /**
     * 检测签名时间，不符合要求会抛出异常
     */
    public void checkSignTime() throws AuthSignException {
        /**
         * 签名时间
         */
        long signTime = DateUtils.parseAlternateIso8601Date(signTimeString).getTime() / 1000;
        /**
         * 现在时间
         */
        long nowTime = new Date().getTime()/1000;

        if (nowTime > (signTime + expiredTimeOffset)) {
            //抛出过期
            throw new AuthSignException(AuthSignException.E_AUTHORIZATION_REQUEST_EXPIRED);
        } else if ((signTime - expiredTimeOffset) > nowTime) {
            //签名期限还没有到
            throw new AuthSignException(AuthSignException.E_AUTHORIZATION_REQUEST_NOT_ENABLE);
        }
    }
    public String getAuthorization() throws AuthSignException {

        // 获取auth
        String authString = getAuthStringPrefix();
        // 生成临时key
        String signingKey = getSigningKey(authString);


        // 获取path
        String canonicalURI = HttpUtils.getCanonicalURIPath(uri.getPath());

        // 获取签名的queryString
        String canonicalQueryString = HttpUtils.getCanonicalQueryString(uri.getQuery(), noSignQueryKeys);

        // 获取签名
        Set<String> signedHeaders = new HashSet<>();
        // 获取签名头
        String canonicalHeader = HttpUtils.getCanonicalHeaders(headers, noSignHeadersKeys, signedHeaders);

        //
        String canonicalRequest = method + "\n" + canonicalURI + "\n" + canonicalQueryString + "\n" + canonicalHeader;

        // Signing the canonical request using key with sha-256 algorithm.
        signature = sha256Hex(signingKey, canonicalRequest);

        return authString + "/" + String.join(";", signedHeaders) + "/" + signature;
    }

    protected String getSigningKey(String authString) throws AuthSignException {
        /**
         * 生成加密key
         */
        return sha256Hex(accessKey, authString);
    }
    protected String getAuthStringPrefix()
    {
        List<String> auth = new ArrayList();

        /**
         * 授权版本
         */
        auth.add(authVersion);
        /**
         * 试图加入请求id
         */
        if (requestId != null && !requestId.isEmpty()) {
            auth.add(requestId);
        }
        /**
         * 加入授权accessKeyId
         */
        auth.add(accessKeyId);
        /**
         * 试图加入客户端id
         */
        if (clientCard != null && !clientCard.isEmpty()) {
            auth.add(clientCard);
        }
        /**
         * 加入签名时间
         */
        auth.add(signTimeString);
        /**
         * 加入过期时间
         */
        auth.add(String.valueOf(expiredTimeOffset));
        /**
         * 连接字符串
         */
        return String.join("/", auth);
    }



    public static String sha256Hex(String signingKey, String stringToSign) throws AuthSignException {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingKey.getBytes(UTF8), "HmacSHA256"));
            return new String(Hex.toHexString(mac.doFinal(stringToSign.getBytes(UTF8))));
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthSignException(AuthSignException.E_AUTHENTICATION_GENERATE_SIGNATURE);
        }
    }

}
