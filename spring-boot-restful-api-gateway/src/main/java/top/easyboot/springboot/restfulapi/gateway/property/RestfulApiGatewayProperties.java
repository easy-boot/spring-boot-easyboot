package top.easyboot.springboot.restfulapi.gateway.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
@ConfigurationProperties("easyboot.restfulapi.gateway")
public class RestfulApiGatewayProperties {
    /**
     * 是否启用
     */
    private boolean enabled = false;
    /**
     * 操作者头信息key
     */
    private String operateHeaderKey = "x-easyboot-operate-info";
    /**
     * 授权签名头信息key
     */
    private String authSignHeaderKey = "x-easyboot-authorization";
    /**
     * uid更新输出头信息key
     */
    private String uidUpdateHeaderKey = "x-easyboot-update-operate-uid";
    /**
     * 自动移除uid更新头
     */
    private boolean uidUpdateHeaderAutoRemove = true;
    /**
     * 签名头前缀
     */
    private String authSignHeaderPrefix = "x-easycms-";

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
         * 链接id前缀
         */
        private String connectionIdPrefix;
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

        public String getConnectionIdPrefix() {
            return connectionIdPrefix;
        }

        public void setConnectionIdPrefix(String connectionIdPrefix) {
            this.connectionIdPrefix = connectionIdPrefix;
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

    public boolean isUidUpdateHeaderAutoRemove() {
        return uidUpdateHeaderAutoRemove;
    }

    public void setUidUpdateHeaderAutoRemove(boolean uidUpdateHeaderAutoRemove) {
        this.uidUpdateHeaderAutoRemove = uidUpdateHeaderAutoRemove;
    }

    public String getAuthSignHeaderPrefix() {
        return authSignHeaderPrefix;
    }

    public void setAuthSignHeaderPrefix(String authSignHeaderPrefix) {
        this.authSignHeaderPrefix = authSignHeaderPrefix;
    }

    public String getOperateHeaderKey() {
        return operateHeaderKey;
    }

    public String getUidUpdateHeaderKey() {
        return uidUpdateHeaderKey;
    }

    public void setUidUpdateHeaderKey(String uidUpdateHeaderKey) {
        this.uidUpdateHeaderKey = uidUpdateHeaderKey;
    }

    public void setOperateHeaderKey(String operateHeaderKey) {
        this.operateHeaderKey = operateHeaderKey;
    }

    public String getAuthSignHeaderKey() {
        return authSignHeaderKey;
    }

    public void setAuthSignHeaderKey(String authSignHeaderKey) {
        this.authSignHeaderKey = authSignHeaderKey;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }
}
