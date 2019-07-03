package top.easyboot.springboot.restfulapi.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import top.easyboot.springboot.restfulapi.core.ApiExceptionHandler;
import top.easyboot.springboot.restfulapi.exception.restTemplate.RpcException;
import top.easyboot.springboot.restfulapi.interfaces.exception.IApiExceptionEntity;

import javax.annotation.PostConstruct;

@RestControllerAdvice
@ResponseBody
public class RestfulApiExceptionHandler {
    @Autowired(required = false)
    private ApiExceptionHandler handler;
    @PostConstruct
    public void init(){
       if (handler == null){
           handler = new ApiExceptionHandler();
       }
    }
    @ExceptionHandler(Exception.class)
    public IApiExceptionEntity exceptionHandler(Exception e, NativeWebRequest request){
        return handler.exceptionHandler(e, request);
    }
    @ExceptionHandler(RpcException.class)
    public IApiExceptionEntity rpcExceptionHandler(RpcException e, NativeWebRequest request){
        return handler.exceptionHandler(e, request);
    }
}
