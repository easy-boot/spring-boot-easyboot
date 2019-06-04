package top.easyboot.springboot.restfulapi.gateway.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URL;

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
    private Integer order;
    /**
     * 链接id前缀
     */
    private String connectionIdPrefix;
    /**
     * 远程调用基本地址
     */
    private URL restfulBaseUrl;
    /**
     * 请求id的头的key
     */
    private String requestIdKey = "x-request-id";
    /**
     * 链接id的头的key
     */
    private String connectionIdKey = "x-easyboot-connection-id";

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

    public URL getRestfulBaseUrl() {
        return restfulBaseUrl;
    }

    public void setRestfulBaseUrl(URL restfulBaseUrl) {
        this.restfulBaseUrl = restfulBaseUrl;
    }

    public String getRequestIdKey() {
        return requestIdKey;
    }

    public void setRequestIdKey(String requestIdKey) {
        this.requestIdKey = requestIdKey;
    }

    public String getConnectionIdKey() {
        return connectionIdKey;
    }

    public void setConnectionIdKey(String connectionIdKey) {
        this.connectionIdKey = connectionIdKey;
    }
}
