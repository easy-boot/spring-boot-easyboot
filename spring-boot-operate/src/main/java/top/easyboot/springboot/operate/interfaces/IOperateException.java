package top.easyboot.springboot.operate.interfaces;

import top.easyboot.springboot.restfulapi.annotation.ExampleMessage;
import static top.easyboot.springboot.restfulapi.exception.ApiException.id;

public interface IOperateException {
    @ExampleMessage("没有登录")
    int E_NO_ACCOUNT_LOGIN = id();
}
