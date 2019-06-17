package top.easyboot.springboot.restfulapi.gateway.interfaces.handler;

import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

public interface WebSocketGatewayIHandler {
    HandlerMapping getHandlerMapping();
    WebSocketHandler getWebSocketHandler();
}
