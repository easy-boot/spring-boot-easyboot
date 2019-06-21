package top.easyboot.springboot.restfulapi.gateway.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import top.easyboot.springboot.authorization.component.AuthClient;
import top.easyboot.springboot.authorization.interfaces.core.IAuthClient;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IUserAuthAccessService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;

@Configuration
@EnableConfigurationProperties(RestfulApiGatewayProperties.class)
@ConditionalOnProperty(name = "easyboot.restfulapi.gateway.enabled", matchIfMissing = true, havingValue = "true")
public class RestfulApiFilterConfiguration{
    @Bean
    public RestfulApiGatewayFilterFactory restfulApiGatewayFilterFactory(){
        return new RestfulApiGatewayFilterFactory();
    }

    @Bean(name = "easybootAuthClient")
    @Description("Auto use easyboot auth client")
    @ConditionalOnMissingBean(IAuthClient.class)
    @ConditionalOnBean(IUserAuthAccessService.class)
    public IAuthClient easybootAuthClient(IUserAuthAccessService accessService){
        return new AuthClient(new AuthClient.Storage(){
            @Override
            public String get(String accessKeyId) {
                return accessService.get(accessKeyId);
            }

            @Override
            public void put(String accessKeyId, String data) {
                accessService.put(accessKeyId, data);
            }
        });
    }



}
