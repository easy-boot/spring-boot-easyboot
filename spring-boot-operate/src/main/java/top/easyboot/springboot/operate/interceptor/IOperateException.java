package top.easyboot.springboot.operate.interceptor;

import top.easyboot.springboot.restfulapi.annotation.ExampleMessage;
import static top.easyboot.springboot.restfulapi.exception.Exception.id;

public interface IOperateException {
    @ExampleMessage("没有登录")
    int E_NO_ACCOUNT_LOGIN = id();
}
