package top.easyboot.springboot.restfulapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import top.easyboot.springboot.restfulapi.utils.Jackson;

import java.util.HashMap;
import java.util.Iterator;

public class RestfulApiException {
    @JsonIgnore
    private int id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnore
    private String messageTemplate;
    private String message;
    private String exceptionId;
    private int statsCode = 500;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatsCode() {
        return statsCode;
    }

    public void setStatsCode(int statsCode) {
        this.statsCode = statsCode;
    }

    public String getMessage() {
        String message = this.message;
        if (message == null || message.isEmpty()){
            message = messageTemplate;
        }
        if (message == null || message.isEmpty()){
            message = "unknow error";
        }
        return message;
    }
    public String getMessage(HashMap messageData) {
        String message = getMessage();
        if(messageData != null){
            Iterator iter = messageData.entrySet().iterator();
            while (iter.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) iter.next();

                message = message.replaceAll("\\{\\$"+entry.getKey().toString()+"\\}", entry.getValue().toString());
            }
        }
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
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
