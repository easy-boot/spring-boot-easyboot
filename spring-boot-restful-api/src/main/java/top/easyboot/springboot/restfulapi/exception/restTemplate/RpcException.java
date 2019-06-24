package top.easyboot.springboot.restfulapi.exception.restTemplate;

import org.springframework.web.client.RestClientException;
import top.easyboot.springboot.restfulapi.entity.RestfulApiException;

public class RpcException extends RestClientException {
    private RestfulApiException restfulApiException;
    public RpcException(RestfulApiException e, Throwable throwable){
        super(e.getMessage(), throwable);
        restfulApiException = e;
    }
    public RpcException(RestfulApiException e){
        super(e.getMessage());
        restfulApiException = e;
    }
    @Override
    public String getMessage() {
        String message = restfulApiException.getMessage();
        try {
            if (message == null || message.isEmpty()){
                message = super.getMessage();
            }
        }catch (NullPointerException e){
            message = "unknow error";
        }
        return message;
    }

    public String getExceptionId(){
        return restfulApiException.getExceptionId();
    }

    public int getStatsCode() {
        return restfulApiException.getStatsCode();
    }

    public void setStatsCode(int statsCode) {
        restfulApiException.setStatsCode(statsCode);
    }

    public RestfulApiException getRestfulApiException() {
        return restfulApiException;
    }
}
