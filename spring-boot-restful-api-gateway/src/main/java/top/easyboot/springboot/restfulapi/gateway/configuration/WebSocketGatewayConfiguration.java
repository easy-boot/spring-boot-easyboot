package top.easyboot.springboot.restfulapi.gateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.reactive.DispatcherHandler;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketGatewayHandler;
import top.easyboot.springboot.restfulapi.gateway.interfaces.handler.WebSocketGatewayIHandler;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IConnectionIdService;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IRowRawApiService;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;
import top.easyboot.springboot.restfulapi.gateway.service.ConnectionIdDemoService;
import top.easyboot.springboot.restfulapi.gateway.service.RowRawApiService;
import top.easyboot.springboot.restfulapi.gateway.service.SessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;

@Configuration
@ConditionalOnProperty(prefix = "easyboot.restfulapi.gateway", name = {"enabled", "web-socket.enabled"}, havingValue = "true")
public class WebSocketGatewayConfiguration  {
    @Autowired
    private WebSocket easybootWebSocketGatewayProperties;

    @Bean
    @ConditionalOnMissingBean(name = "easybootRestfulApiHandlerMapping")
    public HandlerMapping easybootRestfulApiHandlerMapping(WebSocketGatewayIHandler handler) {
        return handler.getHandlerMapping();
    }
    @Bean
    @ConditionalOnMissingBean(name = "easybootRestfulApiWebSocketHandler")
    public WebSocketHandler easybootRestfulApiWebSocketHandler(WebSocketGatewayIHandler handler) {
        return handler.getWebSocketHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name = "webSocketHandlerAdapter")
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
    @Bean
    @ConditionalOnMissingBean(WebSocketGatewayIHandler.class)
    public WebSocketGatewayIHandler easybootWebSocketHandler(IRowRawApiService easybootRowRawApiService) {
        return new WebSocketGatewayHandler(easybootRowRawApiService);
    }

    @Bean(name = "easybootConnectionIdService")
    @Description("Auto use easyboot ConnectionIdDemoService")
    @ConditionalOnMissingBean(IConnectionIdService.class)
    public IConnectionIdService easybootConnectionIdService(ISessionService sessionService){
        return new ConnectionIdDemoService(easybootWebSocketGatewayProperties, sessionService);
    }

    @Bean(name = "easybootWebSocketSessionService")
    @Description("Auto use easyboot WebSocketSessionService")
    @ConditionalOnMissingBean(ISessionService.class)
    public ISessionService easybootWebSocketSessionService(WebSocket easybootWebSocketGatewayProperties){
        return new SessionService();
    }

    @Bean(name = "easybootRowRawApiService")
    @Description("Auto use easyboot rowRawApiService")
    @ConditionalOnMissingBean(IRowRawApiService.class)
    public IRowRawApiService easybootRowRawApiService(DispatcherHandler dispatcherHandler, ISessionService sessionService, WebSocket webSocket){
        RowRawApiService rowRawApiService =  new RowRawApiService(dispatcherHandler, sessionService, webSocket.getRequestIdHeaderKey());
        return rowRawApiService;
    }

    @Bean(name = "easybootWebSocketGatewayProperties")
    @Description("Auto load WebSocket Properties")
    @ConditionalOnMissingBean(WebSocket.class)
    public WebSocket easybootWebSocketGatewayProperties(RestfulApiGatewayProperties properties){
        return properties.getWebSocket();
    }
}
