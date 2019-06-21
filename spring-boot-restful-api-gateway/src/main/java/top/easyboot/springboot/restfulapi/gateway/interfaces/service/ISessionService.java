package top.easyboot.springboot.restfulapi.gateway.interfaces.service;

import org.springframework.web.reactive.socket.WebSocketSession;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSessionBase;
import top.easyboot.springboot.restfulapi.gateway.exception.SessionException;
import java.util.Map;

public interface ISessionService extends Map<String, WebSocketSessionBase> {
    /**
     * 创建会话
     * @param session reactiveWebSocketSession
     * @return easybootWebSocketSession
     */
    WebSocketSessionBase createSession(WebSocketSession session) throws SessionException;

    /**
     * 创建连接
     * @return 连接id
     * @throws Throwable
     */
    String generateConnectionId() throws SessionException;

    /**
     * 绑定用户uid与连接id关系
     * @param connectionId 连接id
     * @param uid 用户uid
     */
    void refreshBindUid(String connectionId, String uid);
}
