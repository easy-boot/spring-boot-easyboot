package top.easyboot.springboot.restfulapi.core;

import org.springframework.web.context.request.NativeWebRequest;
import top.easyboot.springboot.restfulapi.exception.ApiException;
import top.easyboot.springboot.restfulapi.exception.restTemplate.RpcException;
import top.easyboot.springboot.restfulapi.interfaces.core.IApiExceptionHandler;
import top.easyboot.springboot.restfulapi.interfaces.exception.IApiException;
import top.easyboot.springboot.restfulapi.interfaces.exception.IApiExceptionEntity;

import javax.servlet.http.HttpServletResponse;

public class ApiExceptionHandler implements IApiExceptionHandler {
    public IApiExceptionEntity exceptionHandler(Throwable throwable, NativeWebRequest request){
        final IApiExceptionEntity res;
        final HttpServletResponse response = request.getNativeResponse(HttpServletResponse.class);
        if (throwable instanceof RpcException){
            res = ((RpcException) throwable).getEntity();
        } else {
            res = new ApiException.Entity(throwable.getMessage(), "UNKNOW_ERROR");

            if (throwable instanceof IApiException){
                final IApiException et = (IApiException) throwable;

                res.setExceptionId(et.getExceptionId());
                res.setStatsCode(et.getStatsCode());
            }
            saveStackTrace(throwable);
        }
        response.setStatus(res.getStatsCode());

        return res;
    }
    protected void saveStackTrace(Throwable throwable){
        // ToDo
        // 保存错误栈
        throwable.printStackTrace();
    }
}
