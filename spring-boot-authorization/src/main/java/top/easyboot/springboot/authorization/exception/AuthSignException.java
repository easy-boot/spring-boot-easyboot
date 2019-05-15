package top.easyboot.springboot.authorization.exception;

import top.easyboot.springboot.authorization.interfaces.exception.AuthSign;
import top.easyboot.springboot.restfulapi.annotation.ExampleMessage;
import top.easyboot.springboot.restfulapi.exception.Exception;

import java.util.HashMap;

public class AuthSignException extends Exception implements AuthSign {
    @ExampleMessage("还没有授权签名")
    public final static int E_NOT_AUTH_SIGN = id();
    @ExampleMessage("Authentication信息错误")
    public final static int E_AUTHENTICATION_INFO_ERROR = id();
    @ExampleMessage("Authentication格式错误")
    public final static int E_AUTHENTICATION_FORMAT_ERROR = id();
    @ExampleMessage("Authentication版本错误")
    public final static int E_AUTHENTICATION_VERSION_ERROR = id();
    @ExampleMessage("Authentication版本对应的{$className}类没有找到")
    public final static int E_AUTHENTICATION_VERSION_CLASS_NOT_FIND = id();
    @ExampleMessage("Authentication的{$className}类没有符合AuthSign接口")
    public final static int E_AUTHENTICATION_CLASS_INSTANCE_ERROR = id();
    @ExampleMessage("该存储驱动没有符合实现")
    public final static int E_AUTH_DATA_DRIVER_INSTANCE_ERROR = id();
    @ExampleMessage("找不到授权数据")
    public final static int E_AUTHENTICATION_DATA_NOT_FIND = id();
    @ExampleMessage("该签名类不支持 getSignPath 方法")
    public final static int E_AUTH_CLASS_NOT_GET_SIGN_PATH = id();
    @ExampleMessage("客户端识别号非法")
    public final static int E_AUTHORIZATION_CLIENT_CARD_NOT_SELF = id();
    @ExampleMessage("授权签名错误")
    public final static int E_AUTHORIZATION_SIGN_ERROR = id();
    @ExampleMessage("请求过期，请重新签名提交")
    public final static int E_AUTHORIZATION_REQUEST_EXPIRED = id();
    @ExampleMessage("签名期限还没生效")
    public final static int E_AUTHORIZATION_REQUEST_NOT_ENABLE = id();
    @ExampleMessage("请求id格式错误")
    public final static int E_AUTHORIZATION_ERROR_FORMAT_REQUEST_ID = id();
    @ExampleMessage("存在没有签名的头{$headers}")
    public final static int E_AUTHORIZATION_HEADERS_NOT_FIND = id();
    @ExampleMessage("必须签名Host")
    public final static int E_AUTHORIZATION_HEADERS_MUST_HOST = id();
    @ExampleMessage("授权数据为空")
    public final static int E_AUTHENTICATION_AUTH_DATA_EMPTY = id();
    @ExampleMessage("生成签名失败")
    public final static int E_AUTHENTICATION_GENERATE_SIGNATURE = id();
    public AuthSignException(int inputId, Throwable cause){
        super(inputId, cause);
        this.setStatsCode(403);
    }
    public AuthSignException(int inputId){
        super(inputId);
        this.setStatsCode(403);
    }
    public AuthSignException(int inputId, HashMap messageData){
        this(inputId);
        this.setMessageData(messageData);
    }
}
