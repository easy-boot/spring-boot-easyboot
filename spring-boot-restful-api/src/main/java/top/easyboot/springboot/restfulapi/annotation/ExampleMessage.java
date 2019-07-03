package top.easyboot.springboot.restfulapi.annotation;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExampleMessage {
    @AliasFor(annotation = top.easyboot.springboot.utils.annotation.ExampleMessage.class, attribute = "value")
    String value() default "";
}
