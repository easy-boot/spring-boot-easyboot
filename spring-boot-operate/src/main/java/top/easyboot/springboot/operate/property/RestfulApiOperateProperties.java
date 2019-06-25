package top.easyboot.springboot.operate.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("easyboot.restfulapi.operate")
public class RestfulApiOperateProperties {
    /**
     * 是否启用
     */
    private boolean enabled = false;
    /**
     * 操作者头信息key
     */
    private String headerKey = "x-easyboot-operate-info";
    /**
     * [网关专用]uid更新输出头信息key
     */
    private String uidUpdateHeaderKey = "x-easyboot-update-operate-uid";
    /**
     * [网关专用]自动移除uid更新头
     */
    private boolean uidUpdateHeaderAutoRemove = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHeaderKey() {
        return headerKey;
    }

    public void setHeaderKey(String headerKey) {
        this.headerKey = headerKey;
    }

    public String getUidUpdateHeaderKey() {
        return uidUpdateHeaderKey;
    }

    public void setUidUpdateHeaderKey(String uidUpdateHeaderKey) {
        this.uidUpdateHeaderKey = uidUpdateHeaderKey;
    }

    public boolean isUidUpdateHeaderAutoRemove() {
        return uidUpdateHeaderAutoRemove;
    }

    public void setUidUpdateHeaderAutoRemove(boolean uidUpdateHeaderAutoRemove) {
        this.uidUpdateHeaderAutoRemove = uidUpdateHeaderAutoRemove;
    }
}
