package top.easyboot.springboot.authorization.annotation;

import org.springframework.context.annotation.Import;
import top.easyboot.springboot.authorization.configuration.AuthorizationConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AuthorizationConfiguration.class)
public @interface EnableAuthorization {
}