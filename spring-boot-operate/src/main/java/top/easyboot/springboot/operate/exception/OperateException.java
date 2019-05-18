package top.easyboot.springboot.operate.exception;

import top.easyboot.springboot.operate.interceptor.IOperateException;
import top.easyboot.springboot.restfulapi.exception.Exception;


public class OperateException extends Exception implements IOperateException {
    public OperateException(int inputId){
        super(inputId);
        this.setStatsCode(400);
    }
}
