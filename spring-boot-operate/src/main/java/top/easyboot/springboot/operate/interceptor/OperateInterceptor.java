package top.easyboot.springboot.operate.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.easyboot.springboot.operate.annotation.OperateVerifyLogin;
import top.easyboot.springboot.operate.entity.Operate;
import top.easyboot.springboot.operate.exception.NotLoginException;
import top.easyboot.springboot.operate.property.RestfulApiOperateProperties;
import top.easyboot.springboot.operate.utils.GetOperate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OperateInterceptor implements HandlerInterceptor {
    private RestfulApiOperateProperties properties;
    public OperateInterceptor(RestfulApiOperateProperties operateProperties){
        properties = operateProperties;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!properties.isEnabled()){
            return true;
        }
        /**
         * 操作者信息
         */
        Operate operate = GetOperate.get(request);
        //1.不是HandlerMethod类型，则无需检查"
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod method = (HandlerMethod)handler;
        boolean hasLoginAnnotation=method.getMethod().isAnnotationPresent(OperateVerifyLogin.class);
        /**
         * 不存在OperateLoginRequired注解，则直接通过
         */
        if(!hasLoginAnnotation){
            return true;
        }
        OperateVerifyLogin loginRequired=method.getMethod().getAnnotation(OperateVerifyLogin.class);

        /**
         * 2.required=false,则无需检查是否登录
         */
        if(!loginRequired.required()){
            return true;
        }

        /**
         * 3.判断是否已经登录，是放行通过
         */
        if (operate.isLogin()){
           return true;
        }
        /**
         * 没有登录，需要登录
         */
        throw NotLoginException.create(null);
    }
}
