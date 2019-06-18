package top.easyboot.springboot.authorization.interfaces.exception;

import top.easyboot.springboot.restfulapi.annotation.ExampleMessage;
import static top.easyboot.springboot.restfulapi.exception.Exception.id;

public interface IAuthSignException {
    @ExampleMessage("还没有授权签名")
    int E_NOT_AUTH_SIGN = id();
    @ExampleMessage("Authentication信息错误")
    int E_AUTHENTICATION_INFO_ERROR = id();
    @ExampleMessage("Authentication格式错误")
    int E_AUTHENTICATION_FORMAT_ERROR = id();
    @ExampleMessage("Authentication版本错误")
    int E_AUTHENTICATION_VERSION_ERROR = id();
    @ExampleMessage("Authentication版本对应的{$className}类没有找到")
    int E_AUTHENTICATION_VERSION_CLASS_NOT_FIND = id();
    @ExampleMessage("Authentication的{$className}类没有符合AuthSign接口")
    int E_AUTHENTICATION_CLASS_INSTANCE_ERROR = id();
    @ExampleMessage("该存储驱动没有符合实现")
    int E_AUTH_DATA_DRIVER_INSTANCE_ERROR = id();
    @ExampleMessage("找不到授权数据")
    int E_AUTHENTICATION_DATA_NOT_FIND = id();
    @ExampleMessage("该签名类不支持 getSignPath 方法")
    int E_AUTH_CLASS_NOT_GET_SIGN_PATH = id();
    @ExampleMessage("客户端识别号非法")
    int E_AUTHORIZATION_CLIENT_CARD_NOT_SELF = id();
    @ExampleMessage("授权签名错误")
    int E_AUTHORIZATION_SIGN_ERROR = id();
    @ExampleMessage("请求过期，请重新签名提交")
    int E_AUTHORIZATION_REQUEST_EXPIRED = id();
    @ExampleMessage("签名期限还没生效")
    int E_AUTHORIZATION_REQUEST_NOT_ENABLE = id();
    @ExampleMessage("请求id格式错误")
    int E_AUTHORIZATION_ERROR_FORMAT_REQUEST_ID = id();
    @ExampleMessage("存在没有签名的头{$headers}")
    int E_AUTHORIZATION_HEADERS_NOT_FIND = id();
    @ExampleMessage("必须签名Host")
    int E_AUTHORIZATION_HEADERS_MUST_HOST = id();
    @ExampleMessage("授权数据为空")
    int E_AUTHENTICATION_AUTH_DATA_EMPTY = id();
    @ExampleMessage("生成签名失败")
    int E_AUTHENTICATION_GENERATE_SIGNATURE = id();
}
