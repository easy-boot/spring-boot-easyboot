package top.easyboot.springboot.operate.exception;

import top.easyboot.springboot.restfulapi.annotation.ExampleMessage;
import top.easyboot.springboot.restfulapi.exception.Exception;

import java.util.HashMap;

public class OperateException extends Exception {
    @ExampleMessage("没有登录")
    public final static int E_NO_ACCOUNT_LOGIN = id();
    public OperateException(int inputId){
        super(inputId);
        this.setStatsCode(400);
    }
    public OperateException(int inputId, HashMap messageData){
        this(inputId);
        this.setMessageData(messageData);
    }
}
