package top.easyboot.springboot.authorization.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.easyboot.springboot.authorization.interceptor.AuthorizationInterceptor;
import top.easyboot.springboot.authorization.property.RestfulApiAuthProperties;

@Configuration
@EnableConfigurationProperties(RestfulApiAuthProperties.class)
public class AuthorizationConfiguration implements WebMvcConfigurer {
    @Autowired
    private RestfulApiAuthProperties authProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizationInterceptor(authProperties));
    }
}
