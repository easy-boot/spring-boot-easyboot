package top.easyboot.springboot.restfulapi.gateway.service;

import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;
import top.easyboot.springboot.restfulapi.gateway.core.RowRawWebSocketSession;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;

import java.util.HashMap;

public class SessionService extends HashMap<String, RowRawWebSocketSession> implements ISessionService {
    private String signalProtocol = "EASYBOOTSIGNAL";
    private final String SIGNAL = "SIGNAL";

    @Override
    public void pong(String connectionId) {
        // pong
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setStatus("200");
        rawEntity.setStatusText("OK");
        if (containsKey(connectionId)){
            get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }

    @Override
    public void ping(String connectionId) {
        // ping
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setMethod(SIGNAL);
        rawEntity.setPath("/ping");
        if (containsKey(connectionId)){
            get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }

    @Override
    public void pingAuth(String connectionId) {
        // ping
        RowRawEntity rawEntity = new RowRawEntity();
        rawEntity.setProtocol(signalProtocol);
        rawEntity.setMethod(SIGNAL);
        rawEntity.setPath("/ping/auth");
        if (containsKey(connectionId)){
            get(connectionId).textMessage(new String(RowRawUtil.stringify(rawEntity)));
        }
    }

    @Override
    public RowRawWebSocketSession createSession(String connectionId, final org.springframework.web.reactive.socket.WebSocketSession session){
        RowRawWebSocketSession restfulSession = new RowRawWebSocketSession(session);
        put(connectionId, restfulSession);
        return restfulSession;
    }
}
