package top.easyboot.springboot.easyboot.annotation;

import top.easyboot.springboot.restfulapi.annotation.EnableRestfulApi;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableRestfulApi
public @interface EnableEasyboot {
}
