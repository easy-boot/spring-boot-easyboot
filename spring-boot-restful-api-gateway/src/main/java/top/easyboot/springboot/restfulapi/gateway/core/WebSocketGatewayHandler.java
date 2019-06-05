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
import reactor.core.publisher.Mono;
import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.gateway.interfaces.WebSocketGatewayIHandler;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class WebSocketGatewayHandler extends WebSocketGatewayClientHandler implements WebSocketGatewayIHandler, ApplicationListener<WebServerInitializedEvent> {

    @Override
    protected void onWebSocketMessage(String connectionId, WebSocketMessage message) {
        System.out.println(connectionId);
        System.out.println(message);
        if (isRowRawEntityAndHandler(connectionId, message)){
            sessionService.get(connectionId).setUpdateAt(new Date());
            return;
        }
        System.out.println("webSocketMessage");
        System.out.println(message);
    }
    private void response(String connectionId, String requestId, HttpResponse response, Throwable e, WebSocketMessage.Type type){

        if (!sessionService.containsKey(connectionId)){
            return;
        }
        WebSocketRestfulSession session = sessionService.get(connectionId);
        RowRawEntity resEntity = new RowRawEntity();
        resEntity.setProtocol("EASYBOOTRESTFUL/1.0");

        if (response != null){
            StatusLine statusLine = response.getStatusLine();
            resEntity.setStatus(String.valueOf(statusLine.getStatusCode()));
            resEntity.setStatusText(statusLine.getReasonPhrase());

            Map<String, String> headers = new HashMap<>();
            for (Header header : response.getAllHeaders()) {
                headers.put(header.getName(), header.getValue());
            }
            if (requestId != null && !requestId.isEmpty()){
                headers.put(properties.getRequestIdKey(), requestId);
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
        Map<String, String> headers = entity.getHeaders();
        headers.put(properties.getConnectionIdKey(), connectionId);
        String requestId = getRequestId(entity.getHeaders());
        if (method!=null){
            System.out.println(entity.getPath());
            System.out.println(entity.getMethod());


            URL baseUrl = properties.getRestfulBaseUrl();

            //构造请求
            HttpEntityEnclosingRequestBase httpRequest = new HttpEntityEnclosingRequestBase() {
                @Override
                public String getMethod() {
                    return method;
                }
            };

            try {
                httpRequest.setURI(new URL(baseUrl.getProtocol(), baseUrl.getHost(), baseUrl.getPort(), entity.getPath()).toURI());
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

        }else if (status!=null){
            System.out.println("响应");

        }




        return true;
    }
    protected String getRequestId(Map<String, String> headers){
        String requestIdKey = properties.getRequestIdKey();
        String requestIdKeyLower = requestIdKey.toLowerCase();
        String requestId = headers.get(requestIdKey);
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
        URL baseUrl = properties.getRestfulBaseUrl();
        if (baseUrl == null){
            String host;
            // webClient初始化
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            }catch (UnknownHostException e){
                host = "127.0.0.1";
            }
            String protocol = "http";
            try {
                properties.setRestfulBaseUrl(new URL(protocol, host, event.getWebServer().getPort(), "/"));
            }catch (MalformedURLException e){

            }
        }
    }

}