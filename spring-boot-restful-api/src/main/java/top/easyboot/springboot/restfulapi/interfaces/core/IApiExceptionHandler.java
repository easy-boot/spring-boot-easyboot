package top.easyboot.springboot.restfulapi.interfaces.core;

import org.springframework.web.context.request.NativeWebRequest;
import top.easyboot.springboot.restfulapi.interfaces.exception.IApiExceptionEntity;

public interface IApiExceptionHandler {
    IApiExceptionEntity exceptionHandler(Throwable throwable, NativeWebRequest request);
}
