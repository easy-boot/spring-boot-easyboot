package top.easyboot.springboot.operate.interfaces.exception;

import top.easyboot.springboot.restfulapi.annotation.ExampleMessage;
import static top.easyboot.springboot.restfulapi.exception.ApiException.id;

public interface IOperateException {
    @ExampleMessage("没有登录")
    int E_NO_ACCOUNT_LOGIN = id();
    @ExampleMessage("没有访问权限")
    int E_ILLEGAL_ACCESS_EXCEPTION = id();
    @ExampleMessage("抽象类和接口是不能被实例化")
    int E_INSTANTIATION_EXCEPTION = id();
}
