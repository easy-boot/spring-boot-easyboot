package top.easyboot.springboot.restfulapi.gateway.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketRestfulSession;

import java.util.HashMap;

@Service
public class WebSocketGatewaySessionService extends HashMap<String, WebSocketRestfulSession> {
    public WebSocketRestfulSession createSession(String connectionId, final WebSocketSession session){
        WebSocketRestfulSession restfulSession = new WebSocketRestfulSession(session);
        put(connectionId, restfulSession);
        return restfulSession;
    }
}
