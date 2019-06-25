package top.easyboot.springboot.restfulapi.gateway.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketGatewayHandler;
import top.easyboot.springboot.restfulapi.gateway.interfaces.handler.IWebSocketGatewayHandler;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.ISessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;
import top.easyboot.springboot.restfulapi.gateway.service.SessionService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties.WebSocket;

@Configuration
@ConditionalOnProperty(prefix = "easyboot.restfulapi.gateway", name = {"enabled", "web-socket.enabled"}, havingValue = "true")
public class WebSocketGatewayConfiguration {
    @Autowired
    protected WebSocket easybootWebSocketGatewayProperties;


    @Bean
    @ConditionalOnMissingBean(name = "easybootRestfulApiHandlerMapping")
    public HandlerMapping easybootRestfulApiHandlerMapping(IWebSocketGatewayHandler handler) {
        return handler.getHandlerMapping();
    }
    @Bean
    @ConditionalOnMissingBean(name = "easybootRestfulApiWebSocketHandler")
    public WebSocketHandler easybootRestfulApiWebSocketHandler(IWebSocketGatewayHandler handler) {
        return handler.getWebSocketHandler();
    }

    @Bean("easybootWebSocketHandlerAdapter")
    @ConditionalOnMissingBean(WebSocketHandlerAdapter.class)
    public WebSocketHandlerAdapter easybootWebSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
    @Bean
    @ConditionalOnMissingBean(IWebSocketGatewayHandler.class)
    public IWebSocketGatewayHandler easybootWebSocketHandler(ISessionService sessionService) {
        return new WebSocketGatewayHandler(sessionService);
    }

    @Bean(name = "easybootWebSocketSessionService")
    @Description("Auto use easyboot WebSocketSessionService")
    @ConditionalOnMissingBean(ISessionService.class)
    public ISessionService easybootWebSocketSessionService(WebSocket easybootWebSocketGatewayProperties){
        return new SessionService(easybootWebSocketGatewayProperties);
    }

    @Bean(name = "easybootWebSocketGatewayProperties")
    @Description("Auto load WebSocket Properties")
    @ConditionalOnMissingBean(WebSocket.class)
    public WebSocket easybootWebSocketGatewayProperties(RestfulApiGatewayProperties properties){
        return properties.getWebSocket();
    }
}
