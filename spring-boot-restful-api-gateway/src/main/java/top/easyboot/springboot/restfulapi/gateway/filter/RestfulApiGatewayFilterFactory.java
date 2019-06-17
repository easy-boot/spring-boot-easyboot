package top.easyboot.springboot.restfulapi.gateway.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import top.easyboot.springboot.authorization.component.AuthClient;
import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.entity.AuthorizationInput;
import top.easyboot.springboot.authorization.exception.AuthSignException;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;
import top.easyboot.springboot.operate.entity.Operate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.buffer.DataBuffer;
import top.easyboot.springboot.restfulapi.entity.RestfulApiException;


@Component
public class RestfulApiGatewayFilterFactory extends AbstractGatewayFilterFactory<RestfulApiGatewayFilterFactory.Config> {
    private Factory factory;
    private static final Log logger = LogFactory.getLog(RestfulApiGatewayFilterFactory.class);
    @Autowired
    private RestfulApiGatewayProperties properties;
    /**
     * 链接id的头的key
     */
    private String connectionIdHeaderKey;
    /**
     * 原始处理工厂
     * @param factory
     */
    public RestfulApiGatewayFilterFactory(Factory factory) {
        super(Config.class);
        this.factory = factory;
    }


    @Override
    public GatewayFilter apply(Config config) {
        if (factory == null){
            return (exchange, chain) -> chain.filter(exchange);
        }
        return (exchangeOrigin, chain) -> {
            /**
             * 得到原始请求对象
             */
            ServerHttpRequest requestOrigin = exchangeOrigin.getRequest();
            /**
             * 得到一个请求的构建器对象
             */
            ServerHttpRequest.Builder requestBuilder = requestOrigin.mutate();
            /**
             * 得到一个exchange构建器对象
             */
            ServerWebExchange.Builder exchangeBuilder = exchangeOrigin.mutate();
            /**
             * 清理操作者信息，防止注入
             */
            requestBuilder.headers(httpHeaders -> httpHeaders.remove(properties.getOperateHeaderKey()).remove(properties.getAuthSignHeaderKey()));
            /**
             * 实例化一个操作信息对象
             */
            Operate operate = new Operate();
            /**
             * 设置uid为0
             * 也就是没有登录的意思
             */
            operate.setUid(0);
            operate.setLanguageId(0);
            operate.setClientIpV4(getIpAddr(requestOrigin));


            /************************* 鉴权模块开始 *************************/

            HttpHeaders httpHeaders = requestOrigin.getHeaders();

            if (getConnectionIdKey()!=null){
                System.out.println(getConnectionIdKey());
                String connectionId = httpHeaders.getFirst(getConnectionIdKey());
                System.out.println("---*-*-*-*connectionId");
                System.out.println(connectionId);
            }

            AuthorizationInput ai = new AuthorizationInput();
            /**
             * 试图获取授权头
             */
            ai.setUri(requestOrigin.getURI());
            /**
             * 试图获取请求方式
             */
            ai.setMethod(requestOrigin.getMethodValue());
            /**
             * 试图获取请求头
             */
            ai.setHeaders(httpHeaders.toSingleValueMap());
            /**
             * 授权头签字
             */
            ai.setAuthSignHeadersPrefix(properties.getAuthSignHeaderPrefix());

            /**
             * 先定义一个变量来接收授权数据
             */
            Authorization authorizationTemp =null;

            AuthSignException authSignExceptionTemp = null;

            try{
                /**
                 * 试图获取授权会话信息
                 */
                authorizationTemp = factory.getAuthorization(ai);
            }catch (AuthSignException e){
                authSignExceptionTemp = e;
            }
            /**
             * 保证存在一个授权对象
             */
            if (authorizationTemp == null){
                /**
                 * 实例化授权对象
                 */
                authorizationTemp = new Authorization();
                /**
                 * 重置授权状态通过为空
                 */
                authorizationTemp.setPassAuth(false);
            }

            final Authorization authorization = authorizationTemp;
            final AuthSignException authSignException = authSignExceptionTemp;

            /**
             * 不同的授权，会清理授权keyid和客户端card
             */
            if (!authorization.isPassAuth()){
                /**
                 * 重置授权Id为空
                 */
                authorization.setAccessKeyId("");
                /**
                 * 重置客户端id为空
                 */
                authorization.setClientCard("");
            }

            /**
             * 如果授权通过，就试图获取登录uid
             */
            if (authorization.isPassAuth()){
                operate.setUid(factory.getUid(authorization.getAccessKeyId()));
            }
            /************************* 鉴权模块结束 *************************/

            /**
             * 序列化操作者信息并且设置到请求构建器中
             */
            requestBuilder.header(properties.getOperateHeaderKey(), operate.toString());
            /**
             * authorization信息
             */
            requestBuilder.header(properties.getAuthSignHeaderKey(), authorization.toString());
            /**
             * 构建请求对象，并且构建一个exchange传递到下一个过滤器
             */
            ServerWebExchange exchange = exchangeBuilder.request(requestBuilder.build()).build();
            /**
             * 下一个过滤器
             */
            return chain.filter(exchange).then(Mono.defer(() -> {
                /**
                 * 得到响应
                 */
                ServerHttpResponse response = exchange.getResponse();
                HttpHeaders responseHeaders = response.getHeaders();
                List<String> uids = responseHeaders.get(properties.getUidUpdateHeaderKey());
                System.out.println("uids");
                System.out.println(uids);
                if (uids != null && uids.size() > 0){
                    String uidInput = String.valueOf(operate.getUid());
                    String uidOutput = uids.get(uids.size()-1);
                    System.out.println("uidInput");
                    System.out.println(uidInput);
                    System.out.println("uidOutput");
                    System.out.println(uidOutput);
                    if (properties.isUidUpdateHeaderAutoRemove()){
                        responseHeaders.remove(properties.getUidUpdateHeaderKey());
                    }
                    if ((uidInput.isEmpty()||uidInput.equals("0"))&&!uidOutput.isEmpty()&&!uidOutput.equals("0")){
                        factory.putUid(authorization.getAccessKeyId(), Integer.valueOf(uidOutput));
                    }else if(!uidOutput.isEmpty()&&!uidOutput.equals("0")&&(uidOutput.isEmpty()||uidOutput.equals("0"))){
                        factory.putUid(authorization.getAccessKeyId(), 0);
                    }
                }
                /**
                 * 如何微服务返回了403，就把授权签名的错误直接传送到客户端
                 */
                HttpStatus httpStatus = response.getStatusCode();
                if (authSignException!=null && httpStatus != null && httpStatus.equals(HttpStatus.FORBIDDEN)){
                    /**
                     * 构建一个异常
                     */
                    RestfulApiException res = new RestfulApiException();

                    /**
                     * 403状态错误
                     */
                    res.setStatsCode(403);
                    /**
                     * 提示消息
                     */
                    res.setMessage(authSignException.getMessage());
                    /**
                     * 异常id
                     */
                    res.setExceptionId(authSignException.getExceptionId());

                    /**
                     * 转换为bits
                     */
                    byte[] bits = res.toString().getBytes(StandardCharsets.UTF_8);
                    /**
                     * 转换为buffer
                     */
                    DataBuffer buffer = response.bufferFactory().wrap(bits);
                    /**
                     * 直接返回buffer
                     */
                    response.getHeaders().set("Content-Length", String.valueOf(bits.length));
                    return response.writeWith(Mono.just(buffer));
                }
                /**
                 * 下一个过滤器
                 */
                return chain.filter(exchange);
            }));
        };
    }

    /**
     * 取得连接id
     * @return 连接id
     */
    protected String getConnectionIdKey(){
        if (connectionIdHeaderKey == null){
            RestfulApiGatewayProperties.WebSocket webSocket = properties.getWebSocket();
            if (webSocket != null){
                connectionIdHeaderKey = webSocket.getConnectionIdHeaderKey();
            }
        }
        return connectionIdHeaderKey;
    }
    public interface UidStorageFactory extends UidFactory, AuthClient.Storage{

    }
    public interface Factory extends UidFactory {
        /**
         * 试图授权
         * @param authorizationInput
         * @return
         */
        Authorization getAuthorization(AuthorizationInput authorizationInput) throws AuthSignException;
    }
    public interface UidFactory {
        /**
         * 取得用户uid
         * @param accessKeyId
         * @return
         */
        int getUid(String accessKeyId);
        void putUid(String accessKeyId, int uid);
    }
    public static class Config {
        // 控制是否开启认证
        private boolean enabled;

        public Config() {}

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    public static String getIpAddr(ServerHttpRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeaders().getFirst("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeaders().getFirst("Proxy-AuthClient-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeaders().getFirst("WL-Proxy-AuthClient-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddress().getHostString();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }
        // ipAddress = this.getRequest().getRemoteAddr();

        return ipAddress;
    }
}
