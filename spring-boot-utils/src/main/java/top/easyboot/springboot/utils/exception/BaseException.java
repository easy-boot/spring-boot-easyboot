package top.easyboot.springboot.utils.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import top.easyboot.springboot.utils.annotation.ExampleMessage;
import top.easyboot.springboot.utils.core.Jackson;
import top.easyboot.springboot.utils.interfaces.exception.IBaseException;
import top.easyboot.springboot.utils.interfaces.exception.IBaseExceptionEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BaseException extends java.lang.Exception implements IBaseException{
    private Object messageData;
    private static long lastExceptionId = 0;
    private static HashMap<String, Entity> messageMap = new HashMap();
    protected final Entity entity;
    public final static long id(){
        return ++lastExceptionId;
    }

    public BaseException(long inputId, Object messageData, Throwable cause){
        this(inputId, cause);
        this.setMessageData(messageData);
    }
    public BaseException(long inputId, Object messageData){
        this(inputId);
        this.setMessageData(messageData);
    }
    public BaseException(Entity e){
        entity = e;
    }
    public BaseException(long inputId){
        super();
        String id = String.valueOf(inputId);
        if (!messageMap.containsKey(id)){
            initExceptionIdMap();
        }
        entity = messageMap.containsKey(id)?messageMap.get(id) : createExceptionEntity("unknow error", "UNKNOW_ERROR");
    }
    public BaseException(long inputId, Throwable cause){
        super(cause);
        String id = String.valueOf(inputId);
        if (!messageMap.containsKey(id)){
            initExceptionIdMap();
        }
        entity = messageMap.containsKey(id)?messageMap.get(id) : createExceptionEntity("unknow error", "UNKNOW_ERROR");
    }

    public long getId(){
        return entity.getId();
    }
    @Override
    public String getMessage() {
        return getMessage(messageData);
    }

    @Override
    public String getMessage(Object messageData) {
        String message = entity.getMessage(messageData);
        try {
            if (message == null || message.isEmpty()){
                message = super.getMessage();
            }
        }catch (NullPointerException e){
            message = "unknow error";
        }
        return message;
    }

    public Object getMessageData() {
        return messageData;
    }

    public void setMessageData(Object messageData) {
        this.messageData = messageData;
    }

    @Override
    public String getExceptionId(){
        return entity.getExceptionId();
    }

    @Override
    public void setExceptionId(String exceptionId) {
        entity.setExceptionId(exceptionId);
    }

    protected void initExceptionIdMap(){
        Field[] fields = this.getClass().getFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                final String type = field.getType().toString();
                if((type.endsWith("int")||type.endsWith("long")) && Modifier.isStatic(field.getModifiers())){
                    Object id = field.get(this);
                    String exceptionId = field.getName();
                    if (id instanceof Number && exceptionId.startsWith("E_")){
                        Entity re = createExceptionEntity((long)id, exceptionId.substring(2));
                        initMessageTemplate(re, field);
                        messageMap.put(String.valueOf(id), re);
                    }
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }
    protected void initMessageTemplate(Entity entity, Field field){
        ExampleMessage em = field.getAnnotation(ExampleMessage.class);
        if (em != null){
            entity.setMessageTemplate(em.value());
        }
    }
    public Entity createExceptionEntity(long id, String exceptionId){
        return new Entity(id, exceptionId);
    }
    public Entity createExceptionEntity(String message, String exceptionId){
        return new Entity(message, exceptionId);
    }


    public static class Entity implements IBaseExceptionEntity {
        @JsonIgnore
        private long id;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @JsonIgnore
        private String messageTemplate;
        private String message;
        private String exceptionId;

        public Entity(long id, String exceptionId) {
            this.id = id;
            this.exceptionId = exceptionId;
        }

        public Entity(String message, String exceptionId) {
            this.message = message;
            this.exceptionId = exceptionId;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }


        @Override
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
        @Override
        public String getMessage(Object messageData) {
            String message = getMessage();
            if(messageData != null){
                if (messageData instanceof Map){
                    Iterator iter = ((Map)messageData).entrySet().iterator();
                    while (iter.hasNext()) {
                        HashMap.Entry entry = (HashMap.Entry) iter.next();
                        message = message.replaceAll("\\{\\$"+entry.getKey().toString()+"\\}", entry.getValue().toString());
                    }
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

        @Override
        public String getExceptionId() {
            return exceptionId;
        }

        @Override
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
}
