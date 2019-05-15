package top.easyboot.springboot.operate.annotation;

import java.lang.annotation.*;


@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateInfo {
    /**
     * 在没有登录的情况下，如果没有强制验证，就返回默认值
     * @return
     */
    int uid() default 0;

    /**
     * 默认ip
     * @return
     */
    String clientIpV4() default "0.0.0.0";

    /**
     * 默认语言id
     * @return
     */
    int languageId() default 0;

    /**
     * 是否强制验证登录状态
     * @return
     */
    boolean isCheckLogin() default false;
}
