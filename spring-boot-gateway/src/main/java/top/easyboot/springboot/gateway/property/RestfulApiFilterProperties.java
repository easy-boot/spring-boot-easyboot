package top.easyboot.springboot.gateway.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("easyboot.gateway.restfulapi")
public class RestfulApiFilterProperties {
    /**
     * 是否启用
     */
    private boolean enabled;
    /**
     * 操作者头信息key
     */
    private String operateHeaderKey = "x-easyboot-operate-info";
    /**
     * 授权签名头信息key
     */
    private String authSignHeaderKey = "x-easyboot-authorization";
    /**
     * 签名头前缀
     */
    private String authSignHeaderPrefix = "x-easycms-";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public void setOperateHeaderKey(String operateHeaderKey) {
        this.operateHeaderKey = operateHeaderKey;
    }

    public String getAuthSignHeaderKey() {
        return authSignHeaderKey;
    }

    public void setAuthSignHeaderKey(String authSignHeaderKey) {
        this.authSignHeaderKey = authSignHeaderKey;
    }
}
