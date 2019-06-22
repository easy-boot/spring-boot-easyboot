package top.easyboot.springboot.restfulapi.gateway.handler;

import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSessionBase;
import top.easyboot.springboot.restfulapi.gateway.interfaces.handler.ISessionMessageHandler;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RowRawPingHandler implements ISessionMessageHandler {
    protected String signalProtocol = "EASYBOOTSIGNAL";
    protected final String SIGNAL = "SIGNAL";
    protected final String pingPath = "/ping";
    /**
     * 每秒任务定时器
     */
    protected Timer taskTimer;

    /**
     * 会话服务
     */
    protected ISessionService sessionService;
    public RowRawPingHandler(ISessionService service, WebSocket webSocket){
        sessionService = service;
        taskStart();
    }
    @Override
    public boolean onRowRawMessage(String connectionId, RowRawEntity entity, boolean isBinary) {
        if (entity.getProtocol() == null || entity.getMethod() == null || !signalProtocol.equals(entity.getProtocol())){
            return false;
        }
        //
        // 判断是否为一个ping
        if (!SIGNAL.equals(entity.getMethod()) || !pingPath.equals(entity.getPath())){
            return false;
        }
        // 调用pong回应
        pong(connectionId);
        return true;
    }
    @Override
    public void create(String connectionId) {
        pingAuth(connectionId);
    }



    protected void pong(String connectionId) {
        // pong
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setStatus("200");
        rawEntity.setStatusText("OK");
        if (sessionService.containsKey(connectionId)){
            sessionService.get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }

    protected void ping(String connectionId) {
        // ping
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setMethod(SIGNAL);
        rawEntity.setPath("/ping");
        if (sessionService.containsKey(connectionId)){
            sessionService.get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }

    protected void pingAuth(String connectionId) {
        // ping
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setMethod(SIGNAL);
        rawEntity.setPath("/ping/auth");
        if (sessionService.containsKey(connectionId)){
            sessionService.get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }
    protected void taskRun(){
        long now = new Date().getTime()/1000;
        for (String connectionId : sessionService.keySet()) {
            WebSocketSessionBase session = sessionService.get(connectionId);
            long pingInterval = now - (session.getUpdatedAt().getTime()/1000);
            if (pingInterval>45){
                ping(connectionId);
            } else if (pingInterval>60*5){
                session.close();
                continue;
            }
            Date authAccessAt = session.getAuthAccessAt();
            // 每2分钟刷新一次授权信息
            if (authAccessAt == null || (now - (authAccessAt.getTime()/1000))>120){
                pingAuth(connectionId);
            }
        }
    }

    protected void taskStart(){
        taskStop();
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

    protected void taskStop(){
        try {
            if (taskTimer!=null){
                taskTimer.cancel();
            }
        }catch (Throwable e){
        }
    }
}
