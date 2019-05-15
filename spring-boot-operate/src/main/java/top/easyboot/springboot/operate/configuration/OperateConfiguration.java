package top.easyboot.springboot.operate.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.easyboot.springboot.operate.component.OperateInfoResolver;
import top.easyboot.springboot.operate.interceptor.OperateInterceptor;

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
}
