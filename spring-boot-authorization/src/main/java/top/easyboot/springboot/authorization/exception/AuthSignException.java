package top.easyboot.springboot.authorization.exception;

import top.easyboot.springboot.authorization.interfaces.exception.IAuthSignException;
import top.easyboot.springboot.restfulapi.exception.ApiException;

import java.util.HashMap;

public class AuthSignException extends ApiException implements IAuthSignException {
    public AuthSignException(long inputId, Throwable cause){
        super(inputId, cause);
        this.setStatsCode(403);
    }
    public AuthSignException(long inputId){
        super(inputId);
        this.setStatsCode(403);
    }
    public AuthSignException(long inputId, HashMap messageData){
        this(inputId);
        this.setMessageData(messageData);
    }
}
