package top.easyboot.springboot.restfulapi.gateway.service;

import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSessionAbstract;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketSessionBase;
import top.easyboot.springboot.restfulapi.gateway.exception.SessionException;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.utils.exception.BaseException;

import java.util.*;

public abstract class SessionAbstractService extends HashMap<String, WebSocketSessionBase> implements ISessionService {

    abstract void onWebSocketMessage(String connectionId, WebSocketMessage message);

    protected abstract void onCreate(String connectionId);

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
    public WebSocketSessionAbstract createSession(final WebSocketSession webSocketSession) throws BaseException {
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
        // 创建事件
        onCreate(connectionId);
        // 绑定关闭事件
        session.onClose(()->{
            if (containsKey(connectionId)){
                remove(connectionId);
            }
        });
        // 返回会话
        return session;
    }

}
