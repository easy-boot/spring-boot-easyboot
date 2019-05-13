package top.easyboot.springboot.restfulapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import top.easyboot.springboot.restfulapi.utils.Jackson;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Authorization {
    /**
     * 会话id
     */
    protected String accessKeyId;
    /**
     * 设备id
     */
    protected String clientCard;
    /**
     * 通过授权
     */
    protected boolean isPassAuth;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getClientCard() {
        return clientCard;
    }

    public void setClientCard(String clientCard) {
        this.clientCard = clientCard;
    }

    public boolean isPassAuth() {
        return isPassAuth;
    }

    public void setPassAuth(boolean passAuth) {
        isPassAuth = passAuth;
    }

    @Override
    public String toString() {
        try {
            return Jackson.toJson(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
