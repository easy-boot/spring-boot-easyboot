package top.easyboot.springboot.restfulapi.exception.restTemplate;

import org.springframework.web.client.RestClientException;
import top.easyboot.springboot.restfulapi.interfaces.exception.IApiExceptionEntity;
import top.easyboot.springboot.restfulapi.interfaces.exception.IRpcException;

import java.util.Map;

public class RpcException extends RestClientException implements IRpcException {
    private IApiExceptionEntity entity;
    public RpcException(IApiExceptionEntity e, Throwable throwable){
        super(e.getMessage(), throwable);
        entity = e;
    }
    public RpcException(IApiExceptionEntity e){
        super(e.getMessage());
        entity = e;
    }
    @Override
    public String getMessage() {
        String message = entity.getMessage();
        try {
            if (message == null || message.isEmpty()){
                message = super.getMessage();
            }
        }catch (NullPointerException e){
            message = "unknow error";
        }
        return message;
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

    @Override
    public String getExceptionId(){
        return entity.getExceptionId();
    }

    @Override
    public void setExceptionId(String exceptionId) {
        entity.setExceptionId(exceptionId);
    }

    @Override
    public int getStatsCode() {
        return entity.getStatsCode();
    }

    @Override
    public void setStatsCode(int statsCode) {
        entity.setStatsCode(statsCode);
    }

    @Override
    public Map getData() {
        return entity.getData();
    }

    @Override
    public void setData(Map data) {
        entity.setData(data);
    }

    @Override
    public IApiExceptionEntity getEntity() {
        return entity;
    }
}
