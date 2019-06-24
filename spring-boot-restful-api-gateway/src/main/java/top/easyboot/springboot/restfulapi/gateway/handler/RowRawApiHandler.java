package top.easyboot.springboot.restfulapi.gateway.handler;

import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.server.session.DefaultWebSessionManager;
import org.springframework.web.server.session.WebSessionManager;
import reactor.core.publisher.Flux;
import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.entity.RestfulApiException;
import top.easyboot.springboot.restfulapi.exception.ApiException;
import top.easyboot.springboot.restfulapi.gateway.core.RowRawApiExchange;
import top.easyboot.springboot.restfulapi.gateway.core.RowRawApiRequest;
import top.easyboot.springboot.restfulapi.gateway.core.RowRawApiResponse;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSessionBase;
import top.easyboot.springboot.restfulapi.gateway.interfaces.handler.ISessionMessageHandler;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;

import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class RowRawApiHandler implements ISessionMessageHandler {
    /**
     * 协议
     */
    protected String restfulProtocol = "EASYBOOTRESTFUL";
    /**
     * 请求id的头的key
     */
    protected String requestIdHeaderKey;
    /**
     * 监听器
     */
    protected DispatcherHandler dispatcherHandler;
    /**
     * 会话服务
     */
    protected ISessionService sessionService;


    public RowRawApiHandler(ISessionService sessionService, WebSocket webSocket) {
        this.sessionService = sessionService;
        this.requestIdHeaderKey = webSocket.getRequestIdHeaderKey();
    }

    public void setDispatcherHandler(DispatcherHandler dispatcherHandler) {
        this.dispatcherHandler = dispatcherHandler;
    }

    @Override
    public void create(String connectionId) {
    }

    @Override
    public boolean onRowRawMessage(String connectionId, RowRawEntity entity, boolean isBinary) {

        if (entity.getProtocol() == null || entity.getMethod() == null || !restfulProtocol.equals(entity.getProtocol())){
            return false;
        }
        if (!restfulProtocol.equals(entity.getProtocol())){
            return false;
        }
        rpcApi(entity, connectionId , isBinary);
        return true;
    }

    protected void rpcApi(RowRawEntity reqEntity, String connectionId, boolean isByte) {
        if (!sessionService.containsKey(connectionId)){
            return;
        }
        WebSocketSessionBase session = sessionService.get(connectionId);
        InetSocketAddress remoteAddress = session.getRemoteAddress();
        SslInfo sslInfo = null;
        WebSessionManager sessionManager = null;

        String protocol = sslInfo == null ? "http" : "https";
        String method = reqEntity.getMethod();

        HttpMethod httpMethod = HttpMethod.valueOf(method);

        Map<String, String> headers = reqEntity.getHeaders();
        HttpHeaders httpHeaders = new HttpHeaders();
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();

        for (String key : headers.keySet()) {
            httpHeaders.put(key, Arrays.asList(headers.get(key).split(",")));
        }

        String requestId = httpHeaders.getFirst(requestIdHeaderKey);

        InetSocketAddress host = httpHeaders.getHost();
        int port;
        String hostName;
        if (host != null){
            hostName = host.getHostName();
            if (host.getPort() == 0){
                port = sslInfo == null ? 80 : 443;
            }else{
                port = host.getPort();
            }
        }else{
            port = 0;
            hostName = null;
        }
        URI uri;
        try{
            uri = new URL(protocol, hostName==null?null:hostName.trim(), port, reqEntity.getPath().trim()).toURI();
        }catch (URISyntaxException e1){
            // 手动回收内存
            exceptionHandler(e1, connectionId, requestId);
            return;
        }catch (MalformedURLException e2){
            // 手动回收内存
            exceptionHandler(e2, connectionId, requestId);
            return;
        }


        final NettyDataBufferFactory requestBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false,false,false));
        final NettyDataBufferFactory responseBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false,false,false));

        DataBuffer requestBody = requestBufferFactory.wrap(reqEntity.getBody());

        RowRawApiRequest request =  new RowRawApiRequest(requestId, connectionId, httpMethod, uri, "", httpHeaders, cookies, remoteAddress, sslInfo, Flux.just(requestBody));

        RowRawApiResponse response = new RowRawApiResponse(responseBufferFactory);

        RowRawApiExchange exchange = new RowRawApiExchange(request, response,
                sessionManager != null ? sessionManager : new DefaultWebSessionManager());

        response.setRequestId(requestId);
        dispatcherHandler
                .handle(exchange)
                .doOnSuccess((res2)->{
                    // 手动回收内存
                    ReferenceCountUtil.release(requestBody);
                    DataBufferUtils.release(requestBody);
                    try{
                        RowRawEntity resEntity = exchange.getResponse().getRawEntity();
                        resEntity.setProtocol(restfulProtocol);
                        sendResponse(resEntity, connectionId, response.getRequestId());
                    }catch (Throwable e){
                        e.printStackTrace();
                    }
                })
                .doOnError(e->{
                    // 手动回收内存
                    ReferenceCountUtil.release(requestBody);
                    DataBufferUtils.release(requestBody);
                    exceptionHandler(e, connectionId, response.getRequestId());
                })
                .subscribe();
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
        if (throwable instanceof ApiException){
            ApiException et = (ApiException) throwable;
            res.setExceptionId(et.getExceptionId());
            res.setStatsCode(et.getStatsCode());
        }
        resEntity.setProtocol(restfulProtocol);
        resEntity.setStatus(String.valueOf(res.getStatsCode()));
        resEntity.setStatusText(httpStatus.getReasonPhrase());
        resEntity.setBody(res.toString().getBytes());

        sendResponse(resEntity, connectionId, requestId);
    }
}
