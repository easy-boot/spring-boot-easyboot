package top.easyboot.springboot.authorization.exception;

import top.easyboot.springboot.authorization.interfaces.exception.IAuthSignException;
import top.easyboot.springboot.restfulapi.exception.ApiException;

import java.util.HashMap;

public class AuthSignException extends ApiException implements IAuthSignException {
    public AuthSignException(int inputId, Throwable cause){
        super(inputId, cause);
        this.setStatsCode(403);
    }
    public AuthSignException(int inputId){
        super(inputId);
        this.setStatsCode(403);
    }
    public AuthSignException(int inputId, HashMap messageData){
        this(inputId);
        this.setMessageData(messageData);
    }
}
