package top.easyboot.springboot.authorization.interceptor;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.easyboot.springboot.authorization.annotation.EnableAuthorization;
import top.easyboot.springboot.authorization.annotation.VerifyAuthorization;
import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.exception.AuthSignException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorizationInterceptor implements HandlerInterceptor {
    // Spring应用上下文环境
    private static ApplicationContext applicationContext;
    private static boolean isAllVerify = false;
    /*
     * 实现了ApplicationContextAware 接口，必须实现该方法；
     *通过传递applicationContext参数初始化成员变量applicationContext
     */

    public AuthorizationInterceptor(ApplicationContext context) {
        applicationContext = context;
        //获取@EnableAuthorization注解的所有bean
        EnableAuthorization enableAuthorization = null;
        for (String className : context.getBeanNamesForAnnotation(EnableAuthorization.class)) {
            Object app = context.getBean(className);
            if (app!= null && app instanceof Object){
                enableAuthorization = AnnotationUtils.findAnnotation(app.getClass(),EnableAuthorization.class);
                if (enableAuthorization !=null){
                    isAllVerify = enableAuthorization.isAllVerify();
                    break;
                }
            }
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1.不是HandlerMethod类型，则无需检查"
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod method = (HandlerMethod)handler;
        boolean hasAuthAnnotation = isAllVerify || (method.getMethod().isAnnotationPresent(VerifyAuthorization.class) || method.getMethod().getDeclaringClass().isAnnotationPresent(VerifyAuthorization.class));

        // 不存在OperateLoginRequired注解，则直接通过，放弃切面
        if(!hasAuthAnnotation){
            return true;
        }
        // 获取授权对象
        Authorization authorization = getAuthorization(request);

        if (!authorization.isPassAuth()){
            // 抛出异常
            throw new AuthSignException(AuthSignException.E_NOT_AUTH_SIGN);
        }
        return true;
    }
    private static Authorization getAuthorization(HttpServletRequest request){
        /**
         * 操作者信息
         */
        Authorization authorization;
        Object tryAuthorization = request.getAttribute("easyboot-authorization");
        if (tryAuthorization instanceof Authorization){
            authorization = (Authorization)tryAuthorization;
        }else{
            authorization = Authorization.create(request.getHeader("x-easyboot-authorization"));
            /**
             * 存储操作者信息
             */
            request.setAttribute("easyboot-authorization", authorization);
        }
        return authorization;
    }
}
