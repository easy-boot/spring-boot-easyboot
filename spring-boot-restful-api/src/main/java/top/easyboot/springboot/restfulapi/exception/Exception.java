package top.easyboot.springboot.restfulapi.exception;


import top.easyboot.springboot.restfulapi.annotation.ExampleMessage;
import top.easyboot.springboot.restfulapi.entity.RestfulApiException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

public class Exception extends java.lang.Exception {
    private int statsCode = 0;
    private HashMap messageData;
    private RestfulApiException restfulApiException;
    private static int lastExceptionId = 0;
    private static HashMap<String, RestfulApiException> messageMap = new HashMap();
    public final static int id(){
        return ++lastExceptionId;
    }

    public Exception(int inputId, HashMap messageData, Throwable cause){
        this(inputId, cause);
        this.setMessageData(messageData);
    }
    public Exception(int inputId, HashMap messageData){
        this(inputId);
        this.setMessageData(messageData);
    }
    public Exception(RestfulApiException e){
        restfulApiException = e;
    }
    public Exception(int inputId){
        super();
        String id = String.valueOf(inputId);
        if (!messageMap.containsKey(id)){
            initExceptionIdMap();
        }
        restfulApiException = messageMap.containsKey(id)?messageMap.get(id) : new RestfulApiException();
    }
    public Exception(int inputId, Throwable cause){
        super(cause);
        String id = String.valueOf(inputId);
        if (!messageMap.containsKey(id)){
            initExceptionIdMap();
        }
        restfulApiException = messageMap.containsKey(id)?messageMap.get(id) : new RestfulApiException();
    }

    public int getId(){
        return restfulApiException.getId();
    }
    @Override
    public String getMessage() {
        String message = restfulApiException.getMessage(messageData);
        try {
            if (message == null || message.isEmpty()){
                message = super.getMessage();
            }
        }catch (NullPointerException e){
            message = "unknow error";
        }
        return message;
    }

    public HashMap getMessageData() {
        return messageData;
    }

    public void setMessageData(HashMap messageData) {
        this.messageData = messageData;
    }

    public String getExceptionId(){
        return restfulApiException.getExceptionId();
    }

    public int getStatsCode() {
        int statsCode = this.statsCode;
        if (statsCode == 0){
            statsCode = restfulApiException.getStatsCode();
        }
        return statsCode;
    }

    public void setStatsCode(int statsCode) {
        this.statsCode = statsCode;
    }

    protected void initExceptionIdMap(){
        Field[] fields = this.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if(field.getType().toString().endsWith("int") && Modifier.isStatic(field.getModifiers())){
                    Object id = field.get(this);
                    String exceptionId = field.getName();
                    if (id instanceof Number && exceptionId.startsWith("E_")){
                        RestfulApiException re = new RestfulApiException();
                        re.setId((int)id);
                        re.setExceptionId(exceptionId.substring(2));
                        ExampleMessage em = field.getAnnotation(ExampleMessage.class);
                        if (em != null){
                            re.setMessageTemplate(em.value());
                        }
                        messageMap.put(String.valueOf(id), re);
                    }
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }
}
