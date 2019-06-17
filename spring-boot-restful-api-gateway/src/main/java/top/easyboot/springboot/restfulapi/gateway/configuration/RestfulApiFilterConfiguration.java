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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import top.easyboot.springboot.authorization.component.AuthClient;
import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.entity.AuthorizationInput;
import top.easyboot.springboot.authorization.exception.AuthSignException;
import top.easyboot.springboot.authorization.interfaces.core.IAuthClient;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory.Factory;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory.UidFactory;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory.UidStorageFactory;
import top.easyboot.springboot.restfulapi.gateway.interfaces.service.IUserAuthAccessService;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiGatewayProperties;

@Configuration
@EnableConfigurationProperties(RestfulApiGatewayProperties.class)
@ConditionalOnProperty(name = "easyboot.restfulapi.gateway.enabled", matchIfMissing = true, havingValue = "true")
public class RestfulApiFilterConfiguration implements ApplicationContextAware {
    // Spring应用上下文环境
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("setApplicationContext");
        context = applicationContext;

        try {
            context.getBean(IUserAuthAccessService.class);
        }catch (NoSuchBeanDefinitionException e3){
            throw new BeansException("must interface IUserAuthAccessService"){

            };
        }
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



    @Bean
    public RestfulApiGatewayFilterFactory restfulApiGatewayFilterFactory(){
        return new RestfulApiGatewayFilterFactory();
    }
}
