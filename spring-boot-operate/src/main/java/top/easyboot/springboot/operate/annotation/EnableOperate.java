package top.easyboot.springboot.operate.annotation;

import org.springframework.context.annotation.Import;
import top.easyboot.springboot.operate.configuration.OperateConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OperateConfiguration.class)
public @interface EnableOperate {
}
