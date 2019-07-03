package top.easyboot.springboot.restfulapi.gateway.exception;

import top.easyboot.springboot.restfulapi.gateway.interfaces.exception.ISessionException;
import top.easyboot.springboot.utils.exception.BaseException;

public class SessionException extends BaseException implements ISessionException {
    public SessionException(long inputId){
        super(inputId);
    }
}
