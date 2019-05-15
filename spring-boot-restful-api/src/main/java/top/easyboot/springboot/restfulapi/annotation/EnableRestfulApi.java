package top.easyboot.springboot.restfulapi.annotation;

import org.springframework.context.annotation.Import;
import top.easyboot.springboot.restfulapi.component.RestfulApiExceptionHandler;
import top.easyboot.springboot.restfulapi.configuration.RestfulApiConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RestfulApiConfiguration.class, RestfulApiExceptionHandler.class})
public @interface EnableRestfulApi {
}
