package top.easyboot.springboot.restfulapi.interfaces.exception;

public interface IRpcException extends IApiException{
    IApiExceptionEntity getEntity();
}
