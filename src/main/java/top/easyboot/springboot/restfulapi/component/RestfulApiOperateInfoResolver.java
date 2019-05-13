package top.easyboot.springboot.restfulapi.component;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import top.easyboot.springboot.restfulapi.annotation.OperateInfo;
import top.easyboot.springboot.restfulapi.annotation.OperateUid;
import top.easyboot.springboot.restfulapi.utils.RestfulApi;


public class RestfulApiOperateInfoResolver implements HandlerMethodArgumentResolver {
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
            return parameter.getParameterType().isAssignableFrom(top.easyboot.springboot.restfulapi.entity.OperateInfo.class);
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
        top.easyboot.springboot.restfulapi.entity.OperateInfo operateInfo = RestfulApi.getOperateInfo(request);

        if (parameter.hasParameterAnnotation(OperateUid.class)) {
            if (parameter.getParameterType().isAssignableFrom(int.class) || parameter.getParameterType().isAssignableFrom(Integer.class) || parameter.getParameterType().isAssignableFrom(long.class) || parameter.getParameterType().isAssignableFrom(String.class)){

                OperateUid operateUidAnnotation = parameter.getParameterAnnotation(OperateUid.class);
                if (operateInfo.isLogin()){
                    return operateInfo.getUid();
                }else if(operateUidAnnotation.isCheck()){
                    throw new Exception("没有登录");
                }
                if (parameter.getParameterType().isAssignableFrom(String.class)){
                    return String.valueOf(operateUidAnnotation.uid());
                }
                return operateUidAnnotation.uid();
            }
            return null;
        }else if (parameter.getParameterType().isAssignableFrom(top.easyboot.springboot.restfulapi.entity.OperateInfo.class) && parameter.hasParameterAnnotation(OperateInfo.class)) {
            OperateInfo operateInfoAnnotation = parameter.getParameterAnnotation(OperateInfo.class);
            if (!operateInfo.isLogin()){
                if (operateInfoAnnotation.isCheckLogin()){
                    throw new Exception("没有登录");
                }else{
                    operateInfo.setUid(operateInfoAnnotation.uid());
                }
            }
            if (operateInfo.getClientIpV4().isEmpty() || operateInfo.getClientIpV4() == null){
                operateInfo.setClientIpV4(operateInfoAnnotation.clientIpV4());
            }
            if (operateInfo.getLanguageId() == 0){
                operateInfo.setLanguageId(operateInfoAnnotation.languageId());
            }
            return operateInfo;
        }else {
            return null;
        }
    }
}
