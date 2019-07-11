package top.easyboot.springboot.restfulapi.exception;

import top.easyboot.springboot.restfulapi.interfaces.exception.IApiException;
import top.easyboot.springboot.restfulapi.interfaces.exception.IApiExceptionEntity;
import top.easyboot.springboot.restfulapi.annotation.ExampleMessage;
import top.easyboot.springboot.utils.exception.BaseException;

import java.lang.reflect.Field;
import java.util.HashMap;

public class ApiException extends BaseException implements IApiException {
    private Integer statsCode;

    public ApiException(long inputId, HashMap messageData, Throwable cause){
        this(inputId, cause);
        this.setMessageData(messageData);
    }
    public ApiException(long inputId, HashMap messageData){
        this(inputId);
        this.setMessageData(messageData);
    }
    public ApiException(Entity e){
        super(e);
    }
    public ApiException(long inputId){
        super(inputId);
    }
    public ApiException(long inputId, Throwable cause){
        super(inputId, cause);
    }

    @Override
    public BaseException.Entity createExceptionEntity(long id, String exceptionId) {
        return new Entity(id, exceptionId);
    }

    @Override
    public BaseException.Entity createExceptionEntity(String message, String exceptionId) {
        return new Entity(message, exceptionId);
    }

    public int getStatsCode() {
        if (statsCode == null && entity instanceof Entity){
            statsCode = ((Entity)entity).getStatsCode();
        }
        return statsCode;
    }

    public void setStatsCode(int statsCode) {
        this.statsCode = statsCode;
    }

    @Override
    protected void initMessageTemplate(BaseException.Entity entity, Field field) {
        ExampleMessage em = field.getAnnotation(ExampleMessage.class);
        if (em == null){
            super.initMessageTemplate(entity, field);
        }else{
            entity.setMessageTemplate(em.value());
        }
    }

    public static class Entity extends BaseException.Entity implements IApiExceptionEntity {
        private int statsCode = 500;

        public Entity(long id, String exceptionId) {
            super(id, exceptionId);
        }

        public Entity(String message, String exceptionId) {
            super(message, exceptionId);
        }

        public int getStatsCode() {
            return statsCode;
        }

        public void setStatsCode(int statsCode) {
            this.statsCode = statsCode;
        }

    }
}
