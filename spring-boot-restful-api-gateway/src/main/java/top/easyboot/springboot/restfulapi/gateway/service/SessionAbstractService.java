package top.easyboot.springboot.restfulapi.gateway.service;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSessionAbstract;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSessionBase;
import top.easyboot.springboot.restfulapi.gateway.exception.SessionException;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.util.*;

public abstract class SessionAbstractService extends HashMap<String, WebSocketSessionBase> implements ISessionService {

    abstract void onWebSocketMessage(String connectionId, WebSocketMessage message);



    @Override
    public WebSocketSessionBase remove(Object key) {
        if (!containsKey(key)){
            return null;
        }
        WebSocketSessionBase session = super.remove(key);
        session.close();
        return session;
    }
    @Override
    public WebSocketSessionAbstract createSession(final WebSocketSession webSocketSession) throws SessionException {
        try {
            // 创建连接id
            String connectionId = generateConnectionId();
            // 会话服务
            SessionAbstractService sessionService = this;
            // 创建会话
            WebSocketSessionAbstract session = new WebSocketSessionAbstract(connectionId, webSocketSession){
                @Override
                public void onWebSocketMessage(WebSocketMessage message) {
                    if (sessionService.containsKey(connectionId)){
                        sessionService.get(connectionId).setUpdatedAt(new Date());
                        sessionService.onWebSocketMessage(connectionId, message);
                    }
                }
            };
            // 保存会话
            put(connectionId, session);
            // 绑定关闭事件
            session.onClose(()->{
                if (containsKey(connectionId)){
                    remove(connectionId);
                }
            });
            // 返回会话
            return session;
        }catch (ConnectionIdUtil.Exception e){
            throw new SessionException(e.getMessage(), e);
        }
    }

}
