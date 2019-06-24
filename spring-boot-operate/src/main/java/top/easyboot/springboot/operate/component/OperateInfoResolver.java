package top.easyboot.springboot.operate.component;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import top.easyboot.springboot.operate.annotation.OperateInfo;
import top.easyboot.springboot.operate.annotation.OperateUid;
import top.easyboot.springboot.operate.entity.Operate;
import top.easyboot.springboot.operate.exception.OperateException;
import top.easyboot.springboot.operate.utils.GetOperate;

import java.lang.reflect.Method;


public class OperateInfoResolver implements HandlerMethodArgumentResolver {
    /**
     * 是否支持在参数调用前处理
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(OperateUid.class)) {
            return parameter.getParameterType().isAssignableFrom(int.class) || parameter.getParameterType().isAssignableFrom(Integer.class) || parameter.getParameterType().isAssignableFrom(long.class) || parameter.getParameterType().isAssignableFrom(String.class);
        }else if (parameter.hasParameterAnnotation(OperateInfo.class)) {
            return parameter.getParameterType().isAssignableFrom(Operate.class);
        }
        return false;
    }

    /**
     * 注入控制器参数
     * @param parameter
     * @param mavContainer
     * @param request
     * @param binderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
        /**
         * 操作者信息
         */
        Operate operate = GetOperate.get(request);

        if (parameter.hasParameterAnnotation(OperateUid.class)) {
            if (parameter.getParameterType().isAssignableFrom(int.class) || parameter.getParameterType().isAssignableFrom(Integer.class) || parameter.getParameterType().isAssignableFrom(long.class) || parameter.getParameterType().isAssignableFrom(String.class)){

                OperateUid operateUidAnnotation = parameter.getParameterAnnotation(OperateUid.class);
                if (operate.isLogin()){
                    return operate.getUid();
                }else if(operateUidAnnotation.isCheck()){
                    throw createNoLoginException(parameter);
                }
                if (parameter.getParameterType().isAssignableFrom(int.class) || parameter.getParameterType().isAssignableFrom(Integer.class)){
                    return Integer.valueOf(operateUidAnnotation.uid());
                }else if (parameter.getParameterType().isAssignableFrom(long.class)){
                    return Long.valueOf(operateUidAnnotation.uid());
                }
                return operateUidAnnotation.uid();
            }
            return null;
        }else if (parameter.getParameterType().isAssignableFrom(Operate.class) && parameter.hasParameterAnnotation(OperateInfo.class)) {
            OperateInfo operateInfoAnnotation = parameter.getParameterAnnotation(OperateInfo.class);
            if (!operate.isLogin()){
                if (operateInfoAnnotation.isCheckLogin()){
                    throw createNoLoginException(parameter);
                }else{
                    operate.setUid(operateInfoAnnotation.uid());
                }
            }
            if (operate.getClientIpV4().isEmpty() || operate.getClientIpV4() == null){
                operate.setClientIpV4(operateInfoAnnotation.clientIpV4());
            }
            if (operate.getLanguageId() == 0){
                operate.setLanguageId(operateInfoAnnotation.languageId());
            }
            return operate;
        }else {
            return null;
        }
    }
    public static OperateException createNoLoginException(MethodParameter parameter){
        OperateException e = new OperateException(OperateException.E_NO_ACCOUNT_LOGIN);

        Method method = parameter.getMethod();
        Class aClass = parameter.getDeclaringClass();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        stackTraceElements[0] = new StackTraceElement(aClass.getName(), method.getName(), aClass.getSimpleName() + ".java", 1);

        e.setStackTrace(stackTraceElements);
        return e;
    }
}
