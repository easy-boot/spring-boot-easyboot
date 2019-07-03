package top.easyboot.springboot.restfulapi.interfaces.exception;

import top.easyboot.springboot.utils.interfaces.exception.IBaseExceptionEntity;

public interface IApiExceptionEntity extends IBaseExceptionEntity {
    int getStatsCode();
    void setStatsCode(int statsCode);
}
