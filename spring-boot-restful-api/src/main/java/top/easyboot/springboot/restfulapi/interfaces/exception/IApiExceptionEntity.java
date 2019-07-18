package top.easyboot.springboot.restfulapi.interfaces.exception;

import top.easyboot.springboot.utils.interfaces.exception.IBaseExceptionEntity;

import java.util.Map;

public interface IApiExceptionEntity extends IBaseExceptionEntity {
    int getStatsCode();
    void setStatsCode(int statsCode);
    Map getData();
    void setData(Map data);
}
