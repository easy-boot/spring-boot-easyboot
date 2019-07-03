package top.easyboot.springboot.operate.exception;

import top.easyboot.springboot.operate.interfaces.exception.IOperateException;
import top.easyboot.springboot.restfulapi.exception.ApiException;


public class OperateException extends ApiException implements IOperateException {
    public OperateException(long inputId){
        super(inputId);
        this.setStatsCode(400);
    }
    public OperateException(long inputId, Throwable cause){
        super(inputId, cause);
        this.setStatsCode(400);
    }
}
