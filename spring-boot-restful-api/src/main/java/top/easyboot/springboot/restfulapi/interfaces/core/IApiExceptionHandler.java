package top.easyboot.springboot.restfulapi.interfaces.core;

import org.springframework.web.context.request.NativeWebRequest;
import top.easyboot.springboot.restfulapi.entity.RestfulApiException;

public interface IApiExceptionHandler {
    RestfulApiException exceptionHandler(Throwable throwable, NativeWebRequest request);
}
