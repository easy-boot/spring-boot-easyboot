package top.easyboot.springboot.authorization.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.easyboot.springboot.authorization.annotation.VerifyAuthorization;
import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.exception.AuthSignException;
import top.easyboot.springboot.authorization.property.RestfulApiAuthProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthorizationInterceptor implements HandlerInterceptor {
    // 配置
    private static RestfulApiAuthProperties authProperties;

    public AuthorizationInterceptor(RestfulApiAuthProperties properties) {
        authProperties = properties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 不验证
        if (!authProperties.isEnabled()){
            return true;
        }
        //1.不是HandlerMethod类型，则无需检查"
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod method = (HandlerMethod)handler;
        boolean hasAuthAnnotation = authProperties.isAllVerify() || (method.getMethod().isAnnotationPresent(VerifyAuthorization.class) || method.getMethod().getDeclaringClass().isAnnotationPresent(VerifyAuthorization.class));

        // 不存在@VerifyAuthorization注解，则直接通过，放弃切面
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
        final String requestAttribute = authProperties.getRequestAttribute();
        final Object tryAuthorization = request.getAttribute(requestAttribute);
        if (tryAuthorization instanceof Authorization){
            authorization = (Authorization)tryAuthorization;
        }else{
            authorization = Authorization.create(request.getHeader(authProperties.getSignHeaderKey()));
            /**
             * 存储操作者信息
             */
            request.setAttribute(requestAttribute, authorization);
        }
        return authorization;
    }
}
