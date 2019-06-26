package top.easyboot.springboot.operate.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.easyboot.springboot.operate.component.OperateInfoResolver;
import top.easyboot.springboot.operate.exception.NotLoginException;
import top.easyboot.springboot.operate.interceptor.OperateInterceptor;
import top.easyboot.springboot.operate.interfaces.exception.INotLoginException;
import top.easyboot.springboot.operate.property.RestfulApiOperateProperties;

import java.util.List;

@Configuration
public class OperateConfiguration implements WebMvcConfigurer {
    /**
     * 判断是否存在 Operate 注解，自动注入 操作者信息
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new OperateInfoResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new OperateInterceptor());
    }

    @Bean(name = "easybootNotLoginException")
    @Description("Auto use easyboot WebSocketSessionService")
    @ConditionalOnMissingBean(INotLoginException.class)
    public INotLoginException easybootNotLoginException(){
        return new NotLoginException();
    }
}
