package top.easyboot.springboot.restfulapi.gateway.core;

import org.springframework.web.reactive.socket.WebSocketMessage;
import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.gateway.interfaces.handler.WebSocketGatewayIHandler;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IRowRawApiService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;

import java.net.*;
import java.util.*;

public class WebSocketGatewayHandler extends WebSocketGatewayBaseHandler implements WebSocketGatewayIHandler {
    /**
     * 定时器
     */
    private Timer taskTimer;
    private String restfulProtocol = "EASYBOOTRESTFUL";
    private final String pingPath = "/ping";
    private String signalProtocol = "EASYBOOTSIGNAL";
    private final String SIGNAL = "SIGNAL";

    private IRowRawApiService rowRawApiService;

    public WebSocketGatewayHandler(IRowRawApiService rowRawApiService){
        this.rowRawApiService = rowRawApiService;
    }

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
        // 启动定时器
        taskInit();
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
                org.springframework.web.reactive.socket.WebSocketSession s = sessionService.get(connectionId).getSession();
                InetSocketAddress remoteAddress = s.getHandshakeInfo().getRemoteAddress();

                rowRawApiService.rpcApi(entity, connectionId, remoteAddress, null, null, type != WebSocketMessage.Type.TEXT);
//                rpcApi(entity, connectionId, type);
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