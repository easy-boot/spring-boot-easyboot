package top.easyboot.springboot.restfulapi.gateway.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.session.WebSessionManager;
import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.entity.RestfulApiException;
import top.easyboot.springboot.restfulapi.gateway.core.RowRawApiExchange;
import top.easyboot.springboot.restfulapi.gateway.core.RowRawApiRequest;
import top.easyboot.springboot.restfulapi.gateway.core.RowRawApiResponse;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IRowRawApiService;
import top.easyboot.springboot.restfulapi.exception.Exception;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class RowRawApiService implements IRowRawApiService {
    /**
     * 协议
     */
    protected String restfulProtocol = "EASYBOOTRESTFUL";
    /**
     * 请求id的头的key
     */
    protected String requestIdHeaderKey;

    protected DispatcherHandler dispatcherHandler;

    protected ISessionService sessionService;

    public RowRawApiService(DispatcherHandler dispatcherHandler, ISessionService sessionService, String requestIdHeaderKey) {
        this.sessionService = sessionService;
        this.dispatcherHandler = dispatcherHandler;
        this.requestIdHeaderKey = requestIdHeaderKey;
    }
    @Override
    public void rpcApi(RowRawEntity entity, String connectionId, InetSocketAddress remoteAddress, @Nullable SslInfo sslInfo, WebSessionManager sessionManager, boolean isByte) {
        String requestId = null;
        try{
            RowRawApiRequest request = RowRawApiRequest.create(connectionId, entity, remoteAddress, sslInfo);
            RowRawApiResponse response = new RowRawApiResponse();
            RowRawApiExchange exchange = RowRawApiExchange.create(request, response, sessionManager);

            requestId = request.getHeaders().getFirst(requestIdHeaderKey);
            response.setRequestId(requestId);
            dispatcherHandler
                    .handle(exchange)
                    .doOnSuccess((res2)->{
                        try{
                            RowRawEntity resEntity = exchange.getResponse().getRawEntity();
                            resEntity.setProtocol(restfulProtocol);
                            sendResponse(resEntity, connectionId, response.getRequestId());
                        }catch (Throwable e){
                            e.printStackTrace();
                        }
                    })
                    .doOnError(e->exceptionHandler(e, connectionId, response.getRequestId()))
                    .subscribe();
        }catch (URISyntaxException e1){
            exceptionHandler(e1, connectionId, requestId);
        }catch (MalformedURLException e2){
            exceptionHandler(e2, connectionId, requestId);

        }
    }
    protected void sendResponse(RowRawEntity entity, String connectionId, String requestId){
        if (sessionService.containsKey(connectionId)){
            if (requestId != null && !requestId.isEmpty()){
                Map<String, String> headers = new HashMap<>(entity.getHeaders());
                entity.setHeaders(headers);
                headers.put(requestIdHeaderKey, requestId);
            }
            sessionService.get(connectionId).textMessage(new String(RowRawUtil.stringify(entity)));
        }
    }

    protected void exceptionHandler(Throwable throwable, String connectionId, String requestId){
        throwable.printStackTrace();

        final RowRawEntity resEntity = new RowRawEntity();
        final RestfulApiException res = new RestfulApiException();
        final HttpStatus httpStatus = HttpStatus.resolve(500);
        res.setStatsCode(httpStatus.value());
        res.setMessage(throwable.getMessage());
        if (throwable instanceof Exception){
            Exception et = (Exception) throwable;
            res.setExceptionId(et.getExceptionId());
            res.setStatsCode(et.getStatsCode());
        }
        resEntity.setProtocol(restfulProtocol);
        resEntity.setStatus(String.valueOf(res.getStatsCode()));
        resEntity.setStatusText(httpStatus.getReasonPhrase());
        resEntity.setBody(res.toString().getBytes());

        sendResponse(resEntity, connectionId, requestId);
    }

    protected String getRequestId(Map<String, String> headers){
        String requestIdKeyLower = requestIdHeaderKey.toLowerCase();
        String requestId = headers.get(requestIdHeaderKey);
        String authorizationStr = "authorization";
        String authorization = null;

        if (requestId == null || requestId.isEmpty()){
            requestId = headers.get(requestIdKeyLower);
        }

        if (requestId == null || requestId.isEmpty()){
            for (String key : headers.keySet()) {
                String keyLower = key.toLowerCase();
                if (requestIdKeyLower.equals(keyLower)){
                    requestId = headers.get(key);
                }else if(keyLower.equals(authorizationStr)){
                    authorization = headers.get(key);
                }
            }
        }

        if ((requestId == null || requestId.isEmpty()) && authorization != null && !authorization.isEmpty()){

        }


        return requestId;
    }
}
