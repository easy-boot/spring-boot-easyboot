package top.easyboot.springboot.gateway.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import top.easyboot.springboot.authorization.component.Client;
import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.entity.AuthorizationInput;
import top.easyboot.springboot.authorization.exception.AuthSignException;
import top.easyboot.springboot.gateway.annotation.EnableRestfulApiFilter;
import top.easyboot.springboot.gateway.filter.RestfulApiGatewayFilterFactory;
import top.easyboot.springboot.gateway.filter.RestfulApiGatewayFilterFactory.Factory;
import top.easyboot.springboot.gateway.filter.RestfulApiGatewayFilterFactory.UidFactory;
import top.easyboot.springboot.gateway.filter.RestfulApiGatewayFilterFactory.UidStorageFactory;

@Configuration
public class RestfulApiFilterConfigurer implements ApplicationContextAware {
    // Spring应用上下文环境
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
    @Bean
    public RestfulApiGatewayFilterFactory restfulApiGatewayFilterFactory(){

        //获取@EnableRestfulApi注解的所有bean
        EnableRestfulApiFilter enableRestfulApi = null;
        for (String className : context.getBeanNamesForAnnotation(EnableRestfulApiFilter.class)) {
            Object app = context.getBean(className);
            if (app!= null && app instanceof Object){
                enableRestfulApi = AnnotationUtils.findAnnotation(app.getClass(), EnableRestfulApiFilter.class);
                if (enableRestfulApi !=null){
                    break;
                }
            }
        }
        RestfulApiGatewayFilterFactory filterFactory = new RestfulApiGatewayFilterFactory(getRestfulApiFactory());
        filterFactory.setAuthSignHeadersPrefix(enableRestfulApi.authSignHeadersPrefix());
        return filterFactory;
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
