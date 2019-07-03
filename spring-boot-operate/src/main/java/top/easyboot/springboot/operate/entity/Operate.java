package top.easyboot.springboot.operate.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import top.easyboot.springboot.operate.exception.OperateException;
import top.easyboot.springboot.utils.core.Jackson;

import java.lang.reflect.Method;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Operate {
    static Class<Operate> operateClass = Operate.class;
    /**
     * 操作uid
     */
    protected String uid = "";
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
        if (uid == null || uid.isEmpty()){
            return false;
        }
        return !this.uid.equals("0");
    }

    public static Operate create(String infoStr) throws OperateException {
        Operate operate;
        try{
            try {
                if (infoStr != null && !infoStr.isEmpty() && infoStr != ""){
                    operate = Jackson.getObjectMapper().readValue(infoStr, operateClass);
                }else{
                    operate = operateClass.newInstance();
                }
            }catch (Exception e){
                operate = operateClass.newInstance();
            }
        }catch (InstantiationException ei){
            throw new OperateException(OperateException.E_INSTANTIATION_EXCEPTION, ei);
        }catch (IllegalAccessException ea){
            throw new OperateException(OperateException.E_ILLEGAL_ACCESS_EXCEPTION, ea);
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
