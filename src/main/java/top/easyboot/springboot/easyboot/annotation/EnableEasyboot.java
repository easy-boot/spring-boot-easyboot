package top.easyboot.springboot.easyboot.annotation;

import top.easyboot.springboot.operate.annotation.EnableOperate;
import top.easyboot.springboot.restfulapi.annotation.EnableRestfulApi;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableRestfulApi
@EnableOperate
public @interface EnableEasyboot {
}
