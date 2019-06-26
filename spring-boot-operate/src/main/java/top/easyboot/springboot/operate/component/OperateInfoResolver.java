package top.easyboot.springboot.operate.component;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import top.easyboot.springboot.operate.annotation.OperateInfo;
import top.easyboot.springboot.operate.annotation.OperateUid;
import top.easyboot.springboot.operate.entity.Operate;
import top.easyboot.springboot.operate.exception.NotLoginException;
import top.easyboot.springboot.operate.utils.GetOperate;


public class OperateInfoResolver implements HandlerMethodArgumentResolver {
    /**
     * 是否支持在参数调用前处理
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        final Class type = parameter.getParameterType();
        if (parameter.hasParameterAnnotation(OperateUid.class)) {
            return type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class) || type.isAssignableFrom(long.class) || type.isAssignableFrom(String.class);
        }else if (parameter.hasParameterAnnotation(OperateInfo.class)) {
            return type.isAssignableFrom(Operate.class);
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
        final Operate operate = GetOperate.get(request);
        final Class type = parameter.getParameterType();

        if (parameter.hasParameterAnnotation(OperateUid.class)) {
            if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class) || type.isAssignableFrom(long.class) || type.isAssignableFrom(String.class)){

                final OperateUid operateUidAnnotation = parameter.getParameterAnnotation(OperateUid.class);
                if (operate.isLogin()){
                    return operate.getUid();
                }else if(operateUidAnnotation.isCheck()){
                    throw NotLoginException.create(parameter);
                }
                if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)){
                    return Integer.valueOf(operateUidAnnotation.uid());
                }else if (type.isAssignableFrom(long.class)){
                    return Long.valueOf(operateUidAnnotation.uid());
                }
                return operateUidAnnotation.uid();
            }
            return null;
        }else if (type.isAssignableFrom(Operate.class) && parameter.hasParameterAnnotation(OperateInfo.class)) {
            final OperateInfo operateInfoAnnotation = parameter.getParameterAnnotation(OperateInfo.class);
            if (!operate.isLogin()){
                if (operateInfoAnnotation.isCheckLogin()){
                    throw NotLoginException.create(parameter);
                }else{
                    operate.setUid(operateInfoAnnotation.uid());
                }
            }
            if (operate.getClientIpV4().isEmpty() || operate.getClientIpV4() == null){
                operate.setClientIpV4(operateInfoAnnotation.clientIpV4());
            }
            return operate;
        }else {
            return null;
        }
    }
}
