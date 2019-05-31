package top.easyboot.springboot.restfulapi.gateway.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.easyboot.springboot.authorization.component.Client;
import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.entity.AuthorizationInput;
import top.easyboot.springboot.authorization.exception.AuthSignException;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory.Factory;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory.UidFactory;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory.UidStorageFactory;
import top.easyboot.springboot.restfulapi.gateway.property.RestfulApiFilterProperties;

@Configuration
@EnableConfigurationProperties(RestfulApiFilterProperties.class)
@ConditionalOnProperty(name = "easyboot.restfulapi.gateway.enabled", matchIfMissing = true, havingValue = "true")
public class RestfulApiFilterConfiguration implements ApplicationContextAware {
    // Spring应用上下文环境
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("setApplicationContext");
        context = applicationContext;
    }
    @Bean
    public RestfulApiGatewayFilterFactory restfulApiGatewayFilterFactory(){
        return new RestfulApiGatewayFilterFactory(getRestfulApiFactory());
    }
    public Factory getRestfulApiFactory(){
        try {
            return context.getBean(Factory.class);
        }catch (NoSuchBeanDefinitionException e1){
        }
        try {
            UidStorageFactory uidStorageFactory = context.getBean(UidStorageFactory.class);
            Client client = new Client(uidStorageFactory);
            return new Factory() {
                @Override
                public Authorization getAuthorization(AuthorizationInput authorizationInput) throws AuthSignException {
                    return client.getAuthorization(authorizationInput);
                }

                @Override
                public int getUid(String accessKeyId) {
                    return uidStorageFactory.getUid(accessKeyId);
                }
            };
        }catch (NoSuchBeanDefinitionException e2){
        }
        try {
            UidFactory uidFactory = context.getBean(UidFactory.class);
            Client clientTemp = null;
            try{
                clientTemp = context.getBean(Client.class);
            }catch (NoSuchBeanDefinitionException e3){
                Client.Storage storage = context.getBean(Client.Storage.class);
                clientTemp = new Client(storage);
            }
            Client client = clientTemp;
            return new Factory() {
                @Override
                public Authorization getAuthorization(AuthorizationInput authorizationInput) throws AuthSignException {
                    return client.getAuthorization(authorizationInput);
                }

                @Override
                public int getUid(String accessKeyId) {
                    return uidFactory.getUid(accessKeyId);
                }
            };
        }catch (NoSuchBeanDefinitionException e4){
        }
        return null;
    }
}
