package top.easyboot.springboot.restfulapi.component;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import top.easyboot.springboot.restfulapi.entity.RestfulApiException;
import top.easyboot.springboot.restfulapi.exception.restTemplate.RpcException;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
@ResponseBody
public class RestfulApiExceptionHandler {
    /**
     *  拦截Exception类的异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public RestfulApiException exceptionHandler(Exception e, HandlerMethod m, NativeWebRequest request){
        if (!(e instanceof RpcException)){
            System.out.println("存储异常");
            // ToDo
            e.printStackTrace();
        }

        RestfulApiException res = new RestfulApiException();

        HttpServletResponse response = request.getNativeResponse(HttpServletResponse.class);

        res.setMessage(e.getMessage());

        if (e instanceof top.easyboot.springboot.restfulapi.exception.Exception){
            top.easyboot.springboot.restfulapi.exception.Exception et = (top.easyboot.springboot.restfulapi.exception.Exception) e;

            res.setExceptionId(et.getExceptionId());
            res.setStatsCode(et.getStatsCode());
        }
        response.setStatus(res.getStatsCode());

        return res;
    }
    @ExceptionHandler(RpcException.class)
    public RestfulApiException rpcExceptionHandler(RpcException e){
        return e.getRestfulApiException();
    }
}
