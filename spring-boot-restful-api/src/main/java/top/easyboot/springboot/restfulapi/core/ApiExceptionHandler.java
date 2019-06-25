package top.easyboot.springboot.restfulapi.core;

import org.springframework.web.context.request.NativeWebRequest;
import top.easyboot.springboot.restfulapi.entity.RestfulApiException;
import top.easyboot.springboot.restfulapi.exception.ApiException;
import top.easyboot.springboot.restfulapi.exception.restTemplate.RpcException;
import top.easyboot.springboot.restfulapi.interfaces.core.IApiExceptionHandler;

import javax.servlet.http.HttpServletResponse;

public class ApiExceptionHandler implements IApiExceptionHandler {
    public RestfulApiException exceptionHandler(Throwable throwable, NativeWebRequest request){
        final RestfulApiException res;
        final HttpServletResponse response = request.getNativeResponse(HttpServletResponse.class);
        if (throwable instanceof RpcException){
            res = ((RpcException) throwable).getRestfulApiException();
        } else {
            res = new RestfulApiException();
            res.setMessage(throwable.getMessage());

            if (throwable instanceof ApiException){
                final ApiException et = (ApiException) throwable;

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
