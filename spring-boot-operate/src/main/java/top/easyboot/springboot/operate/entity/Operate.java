package top.easyboot.springboot.operate.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import top.easyboot.springboot.operate.utils.Jackson;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Operate {
    /**
     * 操作uid
     */
    protected int uid = 0;
    /**
     * 语言id
     */
    protected int languageId = 0;
    /**
     * 获取客户端ip
     */
    protected String clientIpV4 = "0.0.0.0";
    /**
     * 获取开始时间
     */
    protected long startTime = System.currentTimeMillis();

    /**
     * 长连接id
     */
    protected String connectionId;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }
    public String getClientIpV4() {
        return clientIpV4;
    }

    public void setClientIpV4(String clientIpV4) {
        this.clientIpV4 = clientIpV4;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * 是否登录
     * @return
     */
    @JsonProperty("isLogin")
    public boolean isLogin(){
        return this.uid != 0;
    }

    public static Operate create(String infoStr){
        Operate operate = null;
        if (infoStr != null && !infoStr.isEmpty() && infoStr != ""){
            try {
                operate = Jackson.getObjectMapper().readValue(infoStr, Operate.class);
            }catch (Exception e){
                // todo
                e.printStackTrace();
            }
        }
        if (operate == null){
            operate = new Operate();
        }
        return operate;
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
