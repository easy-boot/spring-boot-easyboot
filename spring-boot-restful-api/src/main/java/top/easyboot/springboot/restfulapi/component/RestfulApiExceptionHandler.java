package top.easyboot.springboot.restfulapi.component;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import top.easyboot.springboot.restfulapi.core.ApiExceptionHandler;
import top.easyboot.springboot.restfulapi.entity.RestfulApiException;
import top.easyboot.springboot.restfulapi.exception.restTemplate.RpcException;
import top.easyboot.springboot.restfulapi.interfaces.core.IApiExceptionHandler;

@RestControllerAdvice
@ResponseBody
public class RestfulApiExceptionHandler {
    @ExceptionHandler(Exception.class)
    public RestfulApiException exceptionHandler(Exception e, IApiExceptionHandler handler, NativeWebRequest request){
        return handler.exceptionHandler(e, request);
    }
    @ExceptionHandler(RpcException.class)
    public RestfulApiException rpcExceptionHandler(RpcException e, IApiExceptionHandler handler, NativeWebRequest request){
        return handler.exceptionHandler(e, request);
    }

    @Bean(name = "easybootApiExceptionHandler")
    @Description("Auto use easyboot apiExceptionHandler")
    @ConditionalOnMissingBean(IApiExceptionHandler.class)
    public IApiExceptionHandler easybootApiExceptionHandler(){
        return new ApiExceptionHandler();
    }
}
