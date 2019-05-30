package top.easyboot.springboot.gateway.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import top.easyboot.springboot.gateway.property.WebSocketRegisterProperties;

@Configuration
@EnableConfigurationProperties(WebSocketRegisterProperties.class)
@ConditionalOnProperty(name = "easyboot.gateway.websocket.register.enabled", matchIfMissing = true)
public class WebSocketRegisterConfiguration implements ApplicationContextAware {
    // Spring应用上下文环境
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
    @Autowired
    private WebSocketRegisterProperties properties;


}
