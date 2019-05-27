package top.easyboot.springboot.gateway.annotation;

import org.springframework.context.annotation.Import;
import top.easyboot.springboot.gateway.configuration.RestfulApiFilterConfigurer;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RestfulApiFilterConfigurer.class)
public @interface EnableRestfulApiFilter {
    String authSignHeadersPrefix() default "x-easycms-";
}
