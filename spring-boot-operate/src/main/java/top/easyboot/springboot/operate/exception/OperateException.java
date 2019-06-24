package top.easyboot.springboot.operate.exception;

import top.easyboot.springboot.operate.interfaces.IOperateException;
import top.easyboot.springboot.restfulapi.exception.ApiException;


public class OperateException extends ApiException implements IOperateException {
    public OperateException(int inputId){
        super(inputId);
        this.setStatsCode(400);
    }
}
