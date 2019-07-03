package top.easyboot.springboot.authorization.drives;

import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.entity.AuthorizationData;
import top.easyboot.springboot.authorization.entity.AuthorizationInput;
import top.easyboot.springboot.authorization.entity.AuthorizationSign;
import top.easyboot.springboot.authorization.interfaces.AuthSignDrive;
import top.easyboot.springboot.authorization.interfaces.http.Headers;
import top.easyboot.springboot.authorization.signer.Sha256Signer;
import top.easyboot.springboot.authorization.exception.AuthSignException;
import top.easyboot.springboot.utils.core.Jackson;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EasyBootAuthV1 implements AuthSignDrive {
    private final static String regAuth = "^([\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12})\\/([0-9a-zA-Z,-]+)\\/([\\da-f]{4}-[\\da-f]{8}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{4}-[\\da-f]{12}-[\\da-f]{8})\\/([\\d]{4}-[\\d]{2}-[\\d]{2}T[\\d]{2}:[\\d]{2}:[\\d]{2}Z)\\/(\\d+)\\/([\\w\\-\\;]+|)\\/([\\da-f]{64})$";
    private final static Pattern patternAuth = Pattern.compile(regAuth, Pattern.CASE_INSENSITIVE);

    @Override
    public void runAuthSign(AuthorizationSign authSign) throws AuthSignException {
        Matcher matcherAuth = patternAuth.matcher(authSign.getAuthValue());
        if (!matcherAuth.find()){
            throw new AuthSignException(AuthSignException.E_AUTHENTICATION_FORMAT_ERROR);
        }
        if (matcherAuth.groupCount()!=7){
            throw new AuthSignException(AuthSignException.E_AUTHENTICATION_INFO_ERROR);
        }
        // 实例化authSha256校验对象
        Sha256Signer sha256Signer = new Sha256Signer();

        // 签名时间 + 过期时间 + 检测签名时间是否合法,并且抛出异常
        sha256Signer.setSignTimeString(matcherAuth.group(4));
        sha256Signer.setExpiredTimeOffset(Integer.valueOf(matcherAuth.group(5)));
        sha256Signer.checkSignTime();

        // 如果有请求id就传入
        sha256Signer.setRequestId(matcherAuth.group(1));

        // 客户端id
        sha256Signer.setClientCard(matcherAuth.group(3));

        // 授权秘钥的id
        sha256Signer.setAccessKeyId(matcherAuth.group(2));

        // 试图获取授权秘钥
        try {
            String dataStr = authSign.getStorage().get(sha256Signer.getAccessKeyId());
            if (dataStr == null || dataStr.isEmpty()){
                throw new AuthSignException(AuthSignException.E_AUTHENTICATION_DATA_NOT_FIND);
            }
            AuthorizationData data = Jackson.getObjectMapper().readValue(dataStr, AuthorizationData.class);
            String key = data.getKey();
            String card = data.getCard();
            if (key==null || key.isEmpty()){
                throw new AuthSignException(AuthSignException.E_AUTHENTICATION_DATA_NOT_FIND);
            }
            if (card== null || card.isEmpty() || !card.equals(sha256Signer.getClientCard())){
                throw new AuthSignException(AuthSignException.E_AUTHORIZATION_CLIENT_CARD_NOT_SELF);
            }
            sha256Signer.setAccessKey(key);
        }catch (Exception e){
            if (e instanceof AuthSignException){
                throw (AuthSignException)e;
            }else{
                throw new AuthSignException(AuthSignException.E_AUTHENTICATION_DATA_NOT_FIND, e);
            }
        }

        AuthorizationInput authInput = authSign.getInput();

        // 签名使用到的 uri + method + AuthVersion
        sha256Signer.setUri(authInput.getUri());
        sha256Signer.setMethod(authInput.getMethod());
        sha256Signer.setAuthVersion(authSign.getAuthVersion());

        // 需要签名的头的key
        String signHeaderKeysStr = matcherAuth.group(6);
        // 客户端签名
        String clientSign = matcherAuth.group(7);

        sha256Signer.setHeaders(getSignHeaders(authInput.getHeaders(), signHeaderKeysStr, authInput.getAuthSignHeadersPrefix()));

        // 试图获取签名
        sha256Signer.getAuthorization();

        // 授权签名判断
        if (clientSign==null || sha256Signer.getSignature() ==null || !clientSign.equals(sha256Signer.getSignature())){
            throw new AuthSignException(AuthSignException.E_AUTHORIZATION_SIGN_ERROR);
        }
        Authorization authorization = new Authorization();

        // 通过签名之后，保存accessKeyId和clientCard
        authorization.setPassAuth(true);
        authorization.setAccessKeyId(sha256Signer.getAccessKeyId());
        authorization.setClientCard(sha256Signer.getClientCard());

        // 保存授权对象
        authSign.setAuthorization(authorization);
    }
    protected Map<String, String> getSignHeaders(Map<String, String> headers,String signHeaderKeysStr, String authSignHeadersPrefix) throws AuthSignException {

        // 授权签名头前缀
        String prefixLower = authSignHeadersPrefix != null && !authSignHeadersPrefix.isEmpty() ? authSignHeadersPrefix.toLowerCase() : null;

        // 签名头地图
        Map<String, String> signHeaderMap = new HashMap<>();

        // 签名头不能为空
        if (signHeaderKeysStr == null || signHeaderKeysStr.isEmpty()){
            throw new AuthSignException(AuthSignException.E_AUTHORIZATION_HEADERS_MUST_HOST);
        }

        // 以;为分隔符拆分头key
        List<String> signHeaderKeys =  new ArrayList<>(Arrays.asList(signHeaderKeysStr.toLowerCase().split(";")));

        // 必须签名host
        if (!signHeaderKeys.contains("host")){
            throw new AuthSignException(AuthSignException.E_AUTHORIZATION_HEADERS_MUST_HOST);
        }
        // 移除 AUTHORIZATION
        if (signHeaderKeys.contains(Headers.AUTHORIZATION.toLowerCase())){
            signHeaderKeys.remove(Headers.AUTHORIZATION.toLowerCase());
        }
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            // 跳过key为空，key为授权key的问题
            if (key==null || key.isEmpty() || key.equals(Headers.AUTHORIZATION.toLowerCase())){
                continue;
            }
            /**
             * 转小写去空格
             */
            String keyLower = key.toLowerCase().trim();
            if (signHeaderKeys.contains(keyLower)){
                /**
                 * 清理
                 */
                signHeaderKeys.remove(keyLower);
                if (value == null || value.isEmpty()){
                    signHeaderMap.put(key, "");
                }else{
                    signHeaderMap.put(key, value);
                }
            }else if (prefixLower!=null && keyLower.startsWith(prefixLower)){
                signHeaderKeys.add(keyLower);
            }
        }

        // 存在没有获取到的头，需要抛出异常
        if (signHeaderKeys.size()>0){
            HashMap<String, String> edata = new HashMap<>();
            edata.put("headers", String.join(";", signHeaderKeys));
            throw new AuthSignException(AuthSignException.E_AUTHORIZATION_HEADERS_NOT_FIND, edata);
        }
        return signHeaderMap;
    }
}
