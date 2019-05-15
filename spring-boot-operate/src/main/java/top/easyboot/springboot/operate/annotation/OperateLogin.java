package top.easyboot.springboot.operate.annotation;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLogin {
    boolean required() default true;
}
