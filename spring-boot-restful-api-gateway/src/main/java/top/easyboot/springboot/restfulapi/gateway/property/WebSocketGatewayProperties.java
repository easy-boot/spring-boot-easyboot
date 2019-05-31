package top.easyboot.springboot.restfulapi.gateway.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("easyboot.restfulapi.gateway.websocket")
public class WebSocketGatewayProperties {
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
    private Integer order = null;
    /**
     * rpc调用信息
     */
    private RpcCall rpcCall;

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

    public RpcCall getRpcCall() {
        return rpcCall;
    }

    public void setRpcCall(RpcCall rpcCall) {
        this.rpcCall = rpcCall;
    }

    public static class RpcCall{
        /**
         * 远程调用网关代理端口
         * 默认，本网关的启动端口
         */
        private int port = 0;
        /**
         * 远程调用网关代理地址
         * 默认自动获取，当前网关的ip地址
         */
        private String address;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
