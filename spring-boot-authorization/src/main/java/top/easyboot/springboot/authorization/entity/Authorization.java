package top.easyboot.springboot.authorization.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import top.easyboot.springboot.restfulapi.util.Jackson;

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

    public static Authorization create(String infoStr){
        Authorization authorization = null;
        if (infoStr != null && !infoStr.isEmpty() && infoStr != ""){
            try {
                authorization = Jackson.getObjectMapper().readValue(infoStr, Authorization.class);
            }catch (Exception e){
            }
        }
        if (authorization == null){
            authorization = new Authorization();
            authorization.setPassAuth(false);
        }
        return authorization;
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
