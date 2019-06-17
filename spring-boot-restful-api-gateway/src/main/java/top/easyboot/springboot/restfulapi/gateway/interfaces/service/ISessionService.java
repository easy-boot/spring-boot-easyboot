package top.easyboot.springboot.restfulapi.gateway.interfaces.service;

import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSession;

import java.util.Map;

public interface ISessionService extends Map<String, WebSocketSession> {
    /**
     * pong 客户端
     * @param connectionId 连接id
     */
    void pong(String connectionId);
    /**
     * ping 客户端
     * @param connectionId 连接id
     */
    void ping(String connectionId);
    /**
     * ping 客户端授权状态，可以为空实现
     * @param connectionId 连接id
     */
    void pingAuth(String connectionId);
    /**
     * 创建会话
     * @param connectionId 连接id
     * @param session reactiveWebSocketSession
     * @return easybootWebSocketSession
     */
    WebSocketSession createSession(String connectionId, final org.springframework.web.reactive.socket.WebSocketSession session);
}
