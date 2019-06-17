package top.easyboot.springboot.restfulapi.gateway.core;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.reactive.socket.WebSocketMessage;
import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.gateway.interfaces.handler.WebSocketGatewayIHandler;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class WebSocketGatewayHandler extends WebSocketGatewayClientHandler implements WebSocketGatewayIHandler, ApplicationListener<WebServerInitializedEvent> {
    /**
     * 定时器
     */
    private Timer taskTimer;
    private String restfulProtocol = "EASYBOOTRESTFUL";
    private final String pingPath = "/ping";
    private String signalProtocol = "EASYBOOTSIGNAL";
    private final String SIGNAL = "SIGNAL";

    /**
     * 远程调用基本地址
     */
    protected static URL restfulBaseUrl;
    /**
     * 请求id的头的key
     */
    protected static String requestIdHeaderKey;
    /**
     * 链接id的头的key
     */
    protected static String connectionIdHeaderKey;

    @Override
    protected void onWebSocketMessage(String connectionId, WebSocketMessage message) {
        if (sessionService.containsKey(connectionId)){
            sessionService.get(connectionId).setUpdateAt(new Date());
        }
        if (isRowRawEntityAndHandler(connectionId, message)){
            return;
        }
    }

    @Override
    protected void init(WebSocket webSocket) {
        super.init(webSocket);
        propertiesInit(webSocket);
        // 启动定时器
        taskInit();
    }
    protected void propertiesInit(WebSocket webSocket){
        restfulBaseUrl = webSocket.getRestfulBaseUrl();
        requestIdHeaderKey = webSocket.getRequestIdHeaderKey();
        connectionIdHeaderKey = webSocket.getConnectionIdHeaderKey();
    }
    /**
     * 处理心跳问题
     */
    protected void taskRun() {
        long now = new Date().getTime()/1000;
        for (String connectionId : sessionService.keySet()) {
            WebSocketSession session = sessionService.get(connectionId);
            long pingInterval = now - (session.getUpdateAt().getTime()/1000);
            if (pingInterval>45){
                sessionService.ping(connectionId);
            } else if (pingInterval>60*5){
                session.close();
                continue;
            }
            Date authAccessAt = session.getAuthAccessAt();
            // 每2分钟刷新一次授权信息
            if (authAccessAt == null || (now - (authAccessAt.getTime()/1000))>120){
                sessionService.pingAuth(connectionId);
            }
        }
    }

    private void response(String connectionId, String requestId, HttpResponse response, Throwable e, WebSocketMessage.Type type){

        if (!sessionService.containsKey(connectionId)){
            return;
        }
        WebSocketSession session = sessionService.get(connectionId);
        RowRawEntity resEntity = new RowRawEntity();
        resEntity.setProtocol(restfulProtocol);

        if (response != null){
            StatusLine statusLine = response.getStatusLine();
            resEntity.setStatus(String.valueOf(statusLine.getStatusCode()));
            resEntity.setStatusText(statusLine.getReasonPhrase());

            Map<String, String> headers = new HashMap<>();
            for (Header header : response.getAllHeaders()) {
                headers.put(header.getName(), header.getValue());
            }
            if (requestId != null && !requestId.isEmpty()){
                headers.put(requestIdHeaderKey, requestId);
            }
            resEntity.setHeaders(headers);

            try {
                HttpEntity httpEntity = response.getEntity();
                byte[] content = new byte[httpEntity.getContent().available()];
                httpEntity.getContent().read(content);
                resEntity.setBody(content);
            }catch (IOException e1){
                e = e1;
            }catch (UnsupportedOperationException e1){
                e = e1;
            }
        }
        if (e != null && e instanceof Throwable){
            resEntity.setStatus("500");
            resEntity.setStatusText("SERVER ERROR");
            resEntity.setBody(e.getMessage().getBytes());
        }

        if (type == WebSocketMessage.Type.BINARY){
//                                session.binaryMessage(RowRawUtil.stringify(resEntity).);
            session.textMessage(new String(RowRawUtil.stringify(resEntity)));
        } else {
            session.textMessage(new String(RowRawUtil.stringify(resEntity)));
        }
    }
    protected boolean isRowRawEntityAndHandler(String connectionId, WebSocketMessage webSocketMessage){
        WebSocketMessage.Type type = webSocketMessage.getType();
        RowRawEntity entity;
        if (type == WebSocketMessage.Type.TEXT){
            entity = RowRawUtil.parse(webSocketMessage.getPayloadAsText().getBytes());
        }else if (type == WebSocketMessage.Type.BINARY){
            byte[] pos = new byte[webSocketMessage.getPayload().capacity()];
            webSocketMessage.getPayload().read(pos);
            entity = RowRawUtil.parse(pos);
        }else{
            return false;
        }
        if (entity.getProtocol() == null){
            return false;
        }
        String method = entity.getMethod();
        String status = entity.getStatus();
        if (method!=null){
            if (restfulProtocol.equals(entity.getProtocol())){
                rpcApi(entity, connectionId, type);
            }else if (signalProtocol.equals(entity.getProtocol())){
                // 如果是一个ping
                if (SIGNAL.equals(entity.getMethod()) && pingPath.equals(entity.getPath())){
                    // 调用pong回应
                    sessionService.pong(connectionId);
                }
            }

        }else if (status!=null){
            System.out.println("响应");

        }

        return true;
    }
    protected void rpcApi(RowRawEntity entity, String connectionId, WebSocketMessage.Type type){
        String method = entity.getMethod();
        Map<String, String> headers = entity.getHeaders();

        headers.put(connectionIdHeaderKey, connectionId);
        String requestId = getRequestId(entity.getHeaders());

        //构造请求
        HttpEntityEnclosingRequestBase httpRequest = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return method;
            }
        };

        try {
            httpRequest.setURI(new URL(restfulBaseUrl.getProtocol(), restfulBaseUrl.getHost(), restfulBaseUrl.getPort(), entity.getPath()).toURI());
        }catch (URISyntaxException e){
            response(connectionId, requestId, null, e, type);
        }catch (MalformedURLException e){
            response(connectionId, requestId, null, e, type);
        }
        if (headers != null && !headers.isEmpty()){
            List<Header> headerList = new ArrayList<>();
            for (String name : headers.keySet()) {
                if (name.toLowerCase().equals("content-length")){
                    continue;
                }
                headerList.add(new BasicHeader(name, headers.get(name)));
            }
            Header[] headerArray = new Header[headerList.size()];
            headerList.toArray(headerArray);
            httpRequest.setHeaders(headerArray);
        }
        if (entity.getBody() !=null && entity.getBody().length>0){
            httpRequest.setEntity(new ByteArrayEntity(entity.getBody()));
        }


        client.execute(httpRequest, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                response(connectionId, requestId, result, null, type);
            }

            @Override
            public void failed(Exception e) {
                response(connectionId, requestId, null, e, type);
            }

            @Override
            public void cancelled() {
                response(connectionId, requestId, null, new Exception("cancelled"), type);
            }
        });
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
    /**
     * 服务器初始化事件-webClient初始化
     * @param event
     */
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        if (restfulBaseUrl == null){
            String host;
            // webClient初始化
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            }catch (UnknownHostException e){
                host = "127.0.0.1";
            }
            String protocol = "http";
            try {
                restfulBaseUrl = new URL(protocol, host, event.getWebServer().getPort(), "/");
            }catch (MalformedURLException e){

            }
        }
    }

    protected void taskInit(){
        try {
            if (taskTimer!=null){
                taskTimer.cancel();
            }
        }catch (Throwable e){
        }
        taskTimer = new Timer();
        taskTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    taskRun();
                }catch (Throwable e){
                    e.printStackTrace();
                }
            }
        }, new Date(), 5000);
    }
}