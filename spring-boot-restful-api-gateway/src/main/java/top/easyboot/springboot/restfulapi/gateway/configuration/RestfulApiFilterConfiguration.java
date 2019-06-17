package top.easyboot.springboot.restfulapi.gateway.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import top.easyboot.springboot.authorization.component.AuthClient;
import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.entity.AuthorizationInput;
import top.easyboot.springboot.authorization.exception.AuthSignException;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory.Factory;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory.UidFactory;
import top.easyboot.springboot.restfulapi.gateway.filter.RestfulApiGatewayFilterFactory.UidStorageFactory;
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
        AuthClient authClient = null;
        UidFactory uidFactory = null;
        try {
            UidStorageFactory uidStorageFactory = context.getBean(UidStorageFactory.class);
            authClient = new AuthClient(uidStorageFactory);
            uidFactory = uidStorageFactory;
        }catch (NoSuchBeanDefinitionException e2){
        }

        if (uidFactory == null){
            try {
                uidFactory = context.getBean(UidFactory.class);
            }catch (NoSuchBeanDefinitionException e3){
            }
        }
        if (authClient == null){
            try {
                authClient = context.getBean(AuthClient.class);
            }catch (NoSuchBeanDefinitionException e3){
            }
        }
        if (authClient == null){
            try {
                AuthClient.Storage storage = context.getBean(AuthClient.Storage.class);
                if (storage!=null){
                    authClient = new AuthClient(storage);
                }
            }catch (NoSuchBeanDefinitionException e3){
            }
        }
        if (authClient == null || uidFactory == null){
            return null;
        }

        final AuthClient authClientFinal = authClient;
        final UidFactory uidFactoryFinal = uidFactory;

        return new Factory() {
            @Override
            public Authorization getAuthorization(AuthorizationInput authorizationInput) throws AuthSignException {
                return authClientFinal.getAuthorization(authorizationInput);
            }
            @Override
            public int getUid(String accessKeyId) {
                return uidFactoryFinal.getUid(accessKeyId);
            }
            @Override
            public void putUid(String accessKeyId, int uid) {
                uidFactoryFinal.putUid(accessKeyId, uid);
            }
        };
    }
}
