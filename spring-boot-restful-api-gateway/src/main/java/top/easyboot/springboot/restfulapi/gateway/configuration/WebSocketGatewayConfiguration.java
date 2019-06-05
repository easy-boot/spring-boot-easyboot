package top.easyboot.springboot.restfulapi.gateway.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import top.easyboot.springboot.restfulapi.gateway.core.WebSocketGatewayHandler;
import top.easyboot.springboot.restfulapi.gateway.filter.WebSocketGatewayGlobalFilter;
import top.easyboot.springboot.restfulapi.gateway.interfaces.WebSocketGatewayIHandler;
import top.easyboot.springboot.restfulapi.gateway.property.WebSocketGatewayProperties;
import top.easyboot.springboot.restfulapi.gateway.service.WebSocketGatewaySessionService;

@Configuration
@EnableConfigurationProperties(WebSocketGatewayProperties.class)
@Import(WebSocketGatewaySessionService.class)
@ConditionalOnProperty(prefix = "easyboot.restfulapi.gateway", name = {"enabled", "websocket.enabled"}, havingValue = "true")
public class WebSocketGatewayConfiguration  {
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
    public WebSocketGatewayGlobalFilter webSocketGatewayGlobalFilter(){
        return new WebSocketGatewayGlobalFilter();
    }

    @Bean
    @ConditionalOnMissingBean(name = "webSocketHandlerAdapter")
    public WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
    @Bean
    @ConditionalOnMissingBean(WebSocketGatewayIHandler.class)
    public WebSocketGatewayIHandler easybootWebSocketHandler() {
        return new WebSocketGatewayHandler();
    }

}
