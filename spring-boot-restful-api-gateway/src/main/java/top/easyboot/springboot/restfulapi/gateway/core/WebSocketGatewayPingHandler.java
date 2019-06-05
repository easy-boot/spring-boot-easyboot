package top.easyboot.springboot.restfulapi.gateway.core;

import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.gateway.interfaces.WebSocketGatewayIHandler;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WebSocketGatewayPingHandler extends WebSocketGatewayBaseHandler implements WebSocketGatewayIHandler {
    /**
     * ping定时器
     */
    private Timer pingTimer;

    @Override
    protected void init() {
        // 启动定时器
        pingTaskInit();
    }
    /**
     * 处理心跳问题
     */
    protected void ping() {
        long now = new Date().getTime()/1000;
        for (String connectionId : sessionService.keySet()) {
            WebSocketRestfulSession session = sessionService.get(connectionId);
            long interval = now - (session.getUpdateAt().getTime()/1000);
            if (interval>45){
                RowRawEntity rawEntity = new RowRawEntity();
                rawEntity.setMethod("PING");
                rawEntity.setPath("/");
                session.textMessage(new String(RowRawUtil.stringify(rawEntity)));
            } else if (interval>60*5){
                session.close();
            }
        }
    }
    protected void pingTaskInit(){
        try {
            if (pingTimer!=null){
                pingTimer.cancel();
            }
        }catch (Throwable e){
        }
        pingTimer = new Timer();
        pingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ping();
            }
        }, new Date(), 5000);
    }
}
