package top.easyboot.springboot.restfulapi.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import top.easyboot.springboot.restfulapi.entity.AuthorizationInput;
import top.easyboot.springboot.restfulapi.entity.OperateInfo;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;


@Component
public class RestfulApiGatewayFilterFactory extends AbstractGatewayFilterFactory<RestfulApiGatewayFilterFactory.Config> {
    private Handler handler;
    private static final Log logger = LogFactory.getLog(RestfulApiGatewayFilterFactory.class);
    public RestfulApiGatewayFilterFactory(Handler handler) {
        super(Config.class);
        this.handler = handler;
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            /**
             * 得到原始请求对象
             */
            ServerHttpRequest requestOrigin = exchange.getRequest();
            /**
             * 得到一个请求的构建器对象
             */
            ServerHttpRequest.Builder requestBuilder = requestOrigin.mutate();
            /**
             * 得到一个exchange构建器对象
             */
            ServerWebExchange.Builder exchangeBuilder = exchange.mutate();
            /**
             * 清理操作者信息，防止注入
             */
            requestBuilder.headers(httpHeaders -> httpHeaders.remove("x-restful-operate-info"));
            /**
             * 实例化一个操作信息对象
             */
            OperateInfo operateInfo = new OperateInfo();
            /**
             * 设置uid为0
             * 也就是没有登录的意思
             */
            operateInfo.setUid(0);
            /**
             * 没有授权id
             */
            operateInfo.setAccessKeyId("");
            /**
             * 没有设备id
             */
            operateInfo.setClientCard("");


            /************************* 鉴权模块开始 *************************/

            HttpHeaders httpHeaders = requestOrigin.getHeaders();

            AuthorizationInput ai = new AuthorizationInput();
            /**
             * 试图获取授权头
             */
            ai.setUri(requestOrigin.getURI());
            /**
             * 试图获取请求的主机头
             */
            ai.setHost(httpHeaders.getHost());
            /**
             * 试图获取请求方式
             */
            ai.setMethod(requestOrigin.getMethodValue());
            /**
             * 试图获取请求头
             */
            ai.setHeaders(httpHeaders.toSingleValueMap());
            /**
             * 试图获取授权头
             */
            ai.setAuthorization(httpHeaders.getFirst("Authorization"));

            /**
             * 先定义一个变量来接收授权数据
             */
            top.easyboot.springboot.restfulapi.entity.Authorization authorization =null;

            try{
                /**
                 * 试图获取授权会话信息
                 */
                authorization = handler.getAuthorization(ai);
            }catch (Exception e){
                /**
                 * 打印日志
                 */
                logger.error(e);
            }

            /**
             * 保证存在一个授权对象
             */
            if (authorization == null){
                /**
                 * 实例化授权对象
                 */
                authorization = new top.easyboot.springboot.restfulapi.entity.Authorization();
                /**
                 * 重置授权Id为空
                 */
                authorization.setAccessKeyId("");
                /**
                 * 重置客户端id为空
                 */
                authorization.setClientCard("");
                /**
                 * 重置授权状态通过为空
                 */
                authorization.setPassAuth(false);
            }


            /**
             * 如果授权通过，就试图获取登录uid
             */
            if (authorization.isPassAuth()){
                operateInfo.setUid(handler.getUid(authorization.getAccessKeyId()));
            }
            /************************* 鉴权模块结束 *************************/

            /**
             * 序列化操作者信息并且设置到请求构建器中
             */
            requestBuilder.header("x-restful-operate-info", operateInfo.toString());
            /**
             * 构建请求对象，并且构建一个exchange传递到下一个过滤器
             */
            return chain.filter(exchangeBuilder.request(requestBuilder.build()).build());
        };
    }

    public interface Handler {
        /**
         * 试图授权
         * @param authorizationInput
         * @return
         */
        top.easyboot.springboot.restfulapi.entity.Authorization getAuthorization(AuthorizationInput authorizationInput);

        /**
         * 取得用户uid
         * @param accessKeyId
         * @return
         */
        int getUid(String accessKeyId);
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
}
