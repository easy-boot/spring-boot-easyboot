package top.easyboot.springboot.authorization.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("easyboot.restfulapi.authorization")
public class RestfulApiAuthProperties {
    /**
     * 授权签名头信息key
     */
    private String signHeaderKey = "x-easyboot-authorization";
    /**
     * 签名头前缀
     */
    private String signHeaderPrefix = "x-easyboot-";
    /**
     * [内部使用]授权对象存储在请求参数中
     */
    private String requestAttribute = "easyboot-authorization";
    /**
     * 全部路由都验证-仅对下游微服务生效，网关无效
     */
    private boolean allVerify = true;
    /**
     * 是否启用
     */
    private boolean enabled = false;

    public boolean isAllVerify() {
        return allVerify;
    }

    public void setAllVerify(boolean allVerify) {
        this.allVerify = allVerify;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getSignHeaderKey() {
        return signHeaderKey;
    }

    public void setSignHeaderKey(String signHeaderKey) {
        this.signHeaderKey = signHeaderKey;
    }

    public String getSignHeaderPrefix() {
        return signHeaderPrefix;
    }

    public void setSignHeaderPrefix(String signHeaderPrefix) {
        this.signHeaderPrefix = signHeaderPrefix;
    }

    public String getRequestAttribute() {
        return requestAttribute;
    }

    public void setRequestAttribute(String requestAttribute) {
        this.requestAttribute = requestAttribute;
    }
}
