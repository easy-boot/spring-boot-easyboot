package top.easyboot.springboot.restfulapi.gateway.exception;

import top.easyboot.springboot.restfulapi.gateway.interfaces.exception.ISessionException;

public class SessionException extends Exception implements ISessionException {
    public SessionException(String message){
        super(message);
    }
    public SessionException(String message, Throwable cause){
        super(message, cause);
    }
}
