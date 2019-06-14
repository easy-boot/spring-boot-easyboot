package top.easyboot.springboot.restfulapi.gateway.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import top.easyboot.springboot.restfulapi.gateway.interfaces.WebSocketGatewayIHandler;
import top.easyboot.springboot.restfulapi.gateway.property.WebSocketGatewayProperties;
import top.easyboot.springboot.restfulapi.gateway.service.WebSocketGatewaySessionService;

import java.util.HashMap;
import java.util.Map;

@Component
public abstract class WebSocketGatewayBaseHandler implements WebSocketGatewayIHandler {
    /**
     * 配置文件
     */
    @Autowired
    protected WebSocketGatewayProperties properties;
    @Autowired
    protected WebSocketHandler easybootRestfulApiWebSocketHandler;
    /**
     * 会话连接池
     */
    @Autowired
    protected WebSocketGatewaySessionService sessionService;

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 创建连接id
     * @return 连接id
     * @throws Exception
     */
    protected abstract String generateConnectionId() throws Exception;

    /**
     * ping对方
     * @param connectionId 连接id
     */
    protected abstract void ping(String connectionId);

    /**
     * pong对方
     * @param connectionId 连接id
     */
    protected abstract void pong(String connectionId);

    /**
     * 收到信息
     * @param connectionId
     * @param message
     */
    protected abstract void onWebSocketMessage(String connectionId, WebSocketMessage message);

    /**
     * 请求客户端绑定用户信息
     * @param connectionId 连接id
     */
    public abstract void requestBindUser(String connectionId);

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
        // 初始化基本类
        init();
        // 初始化WebSocketHandler的路由地图
        Map<String, WebSocketHandler> map = new HashMap<String, WebSocketHandler>();
        /**
         * 默认地址
         */
        if (properties.getPath() == null){
            properties.setPath(new String[]{"/easyboot-restful-api/websocket"});
        }
        /**
         * 循环加入监听器
         */
        for (String path : properties.getPath()) {
            map.put(path, easybootRestfulApiWebSocketHandler);
        }
        /**
         * 创建
         */
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        /**
         * 设置地图
         */
        mapping.setUrlMap(map);
        if (properties.getOrder() == null){
            mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        }else{
            mapping.setOrder(properties.getOrder());
        }

        return mapping;
    }

    @Override
    public WebSocketHandler getWebSocketHandler() {

        return (final WebSocketSession session) -> {
            String connectionId;
            try {
                connectionId = generateConnectionId();
            }catch (Throwable e){
                return session.close();
            }

            WebSocketRestfulSession restfulSession = sessionService.createSession(connectionId, session);

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
