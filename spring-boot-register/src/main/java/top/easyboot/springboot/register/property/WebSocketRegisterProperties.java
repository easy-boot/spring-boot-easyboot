package top.easyboot.springboot.register.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("easyboot.gateway.websocket.register")
public class WebSocketRegisterProperties {
    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 秘钥
     */
    private String secretKey;
    /**
     * 长连接地址
     */
    private String serverEndpoint = "/easyboot-register";

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

    public String getServerEndpoint() {
        return serverEndpoint;
    }

    public void setServerEndpoint(String serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
    }
}
