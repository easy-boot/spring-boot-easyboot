package top.easyboot.springboot.restfulapi.gateway.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties("easyboot.restfulapi.gateway")
public class RestfulApiGatewayProperties {
    /**
     * 是否启用
     */
    private boolean enabled = false;

    private WebSocket webSocket;

    public static class WebSocket{
        /**
         * 是否启用
         */
        private boolean enabled = false;
        /**
         * 秘钥
         */
        private String secretKey;
        /**
         * 监听地址
         */
        private String[] path;
        /**
         * default: same as non-Ordered
         */
        private Integer order;
        /**
         * 远程调用ip
         */
        private String rpcIp;
        /**
         * 请求id的头的key
         */
        private String requestIdHeaderKey = "x-request-id";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String[] getPath() {
            return path;
        }

        public void setPath(String[] path) {
            this.path = path;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public String getRpcIp() {
            return rpcIp;
        }

        public void setRpcIp(String rpcIp) {
            this.rpcIp = rpcIp;
        }

        public String getRequestIdHeaderKey() {
            return requestIdHeaderKey;
        }

        public void setRequestIdHeaderKey(String requestIdHeaderKey) {
            this.requestIdHeaderKey = requestIdHeaderKey;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }
}
