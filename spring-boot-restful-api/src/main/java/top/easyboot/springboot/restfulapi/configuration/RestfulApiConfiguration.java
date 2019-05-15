package top.easyboot.springboot.restfulapi.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.easyboot.springboot.restfulapi.http.converter.UrlencodedHttpMessageConverter;

import java.util.List;

@Configuration
public class RestfulApiConfiguration implements WebMvcConfigurer {
    /**
     * 添加自定义的httpMessageConverter
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new UrlencodedHttpMessageConverter());
    }
}
