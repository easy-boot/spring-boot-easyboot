package top.easyboot.springboot.register.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import top.easyboot.springboot.register.property.WebSocketRegisterProperties;
import top.easyboot.springboot.register.service.WebSocketRegisterService;

@Configuration
@EnableConfigurationProperties(WebSocketRegisterProperties.class)
@ConditionalOnProperty(name = "easyboot.gateway.websocket.register.enabled", matchIfMissing = true)
@Import(WebSocketRegisterService.class)
@EnableWebSocket
public class WebSocketRegisterConfiguration implements WebSocketConfigurer {
    @Autowired
    private WebSocketRegisterProperties properties;
    @Autowired
    private WebSocketRegisterService webSocketRegisterService;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketRegisterService, properties.getServerEndpoint());
    }
}
