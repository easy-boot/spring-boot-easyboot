package top.easyboot.springboot.authorization.exception;

import top.easyboot.springboot.authorization.interfaces.exception.AuthSign;
import top.easyboot.springboot.restfulapi.annotation.ExampleMessage;
import top.easyboot.springboot.restfulapi.exception.Exception;

import java.util.HashMap;

public class AuthSignException extends Exception implements AuthSign {
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
