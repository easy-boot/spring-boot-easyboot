package top.easyboot.springboot.authorization.annotation;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VerifyAuthorization {
}
