package top.easyboot.springboot.restfulapi.gateway.core;

import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.gateway.interfaces.WebSocketGatewayIHandler;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WebSocketGatewayPingHandler extends WebSocketGatewayBaseHandler implements WebSocketGatewayIHandler {

    protected String signalProtocol = "EASYBOOTSIGNAL";
    protected final String SIGNAL = "SIGNAL";
    protected void ping(String connectionId){
        // ping
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setMethod(SIGNAL);
        rawEntity.setPath("/ping");
        if (sessionService.containsKey(connectionId)){
            sessionService.get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }
    protected void pong(String connectionId){
        // pong
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setStatus("200");
        rawEntity.setStatusText("OK");
        if (sessionService.containsKey(connectionId)){
            sessionService.get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }
}
