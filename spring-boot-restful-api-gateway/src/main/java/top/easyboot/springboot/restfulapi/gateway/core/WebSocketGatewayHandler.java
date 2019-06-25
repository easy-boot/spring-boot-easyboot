package top.easyboot.springboot.restfulapi.gateway.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import top.easyboot.springboot.restfulapi.gateway.exception.SessionException;
import top.easyboot.springboot.restfulapi.gateway.interfaces.handler.IWebSocketGatewayHandler;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;
import org.springframework.web.reactive.socket.WebSocketSession;
import java.util.*;

@Component
public class WebSocketGatewayHandler implements IWebSocketGatewayHandler {
    /**
     * 配置文件
     */
    @Autowired
    protected RestfulApiGatewayProperties properties;
    @Autowired
    protected WebSocketHandler easybootRestfulApiWebSocketHandler;
    /**
     * 会话连接池
     */
    protected ISessionService sessionService;


    public WebSocketGatewayHandler(ISessionService sessionService){
        this.sessionService = sessionService;
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

        return (final WebSocketSession session) -> {
            try {
                final WebSocketSessionBase sessionAbstract = sessionService.createSession(session);
                final String connectionId = sessionAbstract.getConnectionId();
                return session.send(sessionAbstract.getFlux()).doAfterSuccessOrError((res, throwable)->{
                    if (sessionService.containsKey(connectionId)){
                        sessionService.remove(connectionId);
                    }
                    if (throwable != null){
                        System.out.println("throwable");
                        throwable.printStackTrace();
                    }
                });
            }catch (SessionException e){
                e.printStackTrace();
                return session.close();
            }

        };
    }



}
