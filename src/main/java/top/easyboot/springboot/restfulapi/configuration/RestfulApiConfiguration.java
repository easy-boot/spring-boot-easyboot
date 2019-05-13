package top.easyboot.springboot.restfulapi.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import top.easyboot.springboot.restfulapi.component.RestfulApiOperateInfoResolver;
import top.easyboot.springboot.restfulapi.http.converter.UrlencodedHttpMessageConverter;
import top.easyboot.springboot.restfulapi.interceptor.RestfulApi;

import java.util.List;

@Configuration
public class RestfulApiConfiguration extends WebMvcConfigurationSupport {
    /**
     * 添加自定义的httpMessageConverter
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new UrlencodedHttpMessageConverter());
    }

    /**
     * 判断是否存在 OperateInfo 注解，自动注入 操作者信息
     * @param argumentResolvers
     */
    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new RestfulApiOperateInfoResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RestfulApi());
    }
}
