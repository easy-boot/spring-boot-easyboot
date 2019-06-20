package top.easyboot.springboot.restfulapi.gateway.core;

import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.util.Date;

public class WebSocketSessionEntity extends ConnectionIdUtil.Entity {
    /**
     * 当前登录用户
     */
    protected String uid;
    /**
     * 开始时间
     */
    protected final Date startedAt;
    /**
     * 更新时间
     */
    protected Date updatedAt;
    /**
     * 授权更新时间
     */
    protected Date authAccessAt;

    /**
     * 实例化
     * @param connectionId 连接id
     * @throws ConnectionIdUtil.Exception 连接异常
     */
    protected WebSocketSessionEntity(String connectionId) throws ConnectionIdUtil.Exception {
        super(connectionId);
        startedAt = new Date();
        updatedAt = new Date();

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getAuthAccessAt() {
        return authAccessAt;
    }

    public void setAuthAccessAt(Date authAccessAt) {
        this.authAccessAt = authAccessAt;
    }
}
