package top.easyboot.springboot.restfulapi.gateway.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import top.easyboot.springboot.restfulapi.gateway.interfaces.handler.WebSocketGatewayIHandler;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IConnectionIdService;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;

import java.util.HashMap;
import java.util.Map;

@Component
public abstract class WebSocketGatewayBaseHandler implements WebSocketGatewayIHandler {
    /**
     * 配置文件
     */
    @Autowired
    protected RestfulApiGatewayProperties properties;
    @Autowired
    protected WebSocketHandler easybootRestfulApiWebSocketHandler;
    @Autowired
    protected IConnectionIdService connectionIdService;
    /**
     * 会话连接池
     */
    @Autowired
    protected ISessionService sessionService;

    /**
     * 初始化
     */
    protected abstract void init(WebSocket webSocket);

    /**
     * 收到信息
     * @param connectionId
     * @param message
     */
    protected abstract void onWebSocketMessage(String connectionId, WebSocketMessage message);

    /**
     * 判断连接id是否存在
     * @param connectionId
     * @return
     */
    public boolean containsConnectionId(String connectionId) {
        return sessionService.containsKey(connectionId);
    }

    /**
     * 获取监听地图
     * @return
     */
    @Override
    public HandlerMapping getHandlerMapping() {
        /**
         * 创建
         */
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        // 初始化
        WebSocket webSocket = properties.getWebSocket();
        if (webSocket == null || !properties.isEnabled() || !webSocket.isEnabled()){
            return mapping;
        }
        // 初始化基本类
        init(webSocket);
        // 初始化WebSocketHandler的路由地图
        Map<String, WebSocketHandler> map = new HashMap<String, WebSocketHandler>();
        /**
         * 默认地址
         */
        if (webSocket.getPath() == null){
            webSocket.setPath(new String[]{"/easyboot-restful-api/websocket"});
        }
        /**
         * 循环加入监听器
         */
        for (String path : webSocket.getPath()) {
            map.put(path, easybootRestfulApiWebSocketHandler);
        }
        /**
         * 设置地图
         */
        mapping.setUrlMap(map);
        if (webSocket.getOrder() == null){
            mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        }else{
            mapping.setOrder(webSocket.getOrder());
        }

        return mapping;
    }

    @Override
    public WebSocketHandler getWebSocketHandler() {

        return (final org.springframework.web.reactive.socket.WebSocketSession session) -> {
            String connectionId;
            try {
                // 创建连接id
                connectionId = connectionIdService.generateConnectionId();
            }catch (Throwable e){
                return session.close();
            }

            WebSocketSession restfulSession = sessionService.createSession(connectionId, session);

            session.receive().subscribe(message-> onWebSocketMessage(connectionId, message), e->{
                e.printStackTrace();
                restfulSession.close();
            }, () -> restfulSession.close());

            restfulSession.onClose(()->{
                if (sessionService.containsKey(connectionId)){
                    sessionService.remove(connectionId);
                }
            });

            return session.send(restfulSession.getFlux()).doAfterSuccessOrError((res, throwable)->{
                if (sessionService.containsKey(connectionId)){
                    sessionService.remove(connectionId);
                }
                if (throwable != null){
                    System.out.println("throwable");
                    throwable.printStackTrace();
                }
            });
        };
    }
}
