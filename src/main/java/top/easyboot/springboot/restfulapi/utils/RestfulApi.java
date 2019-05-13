package top.easyboot.springboot.restfulapi.utils;

import top.easyboot.springboot.restfulapi.entity.OperateInfo;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;

public class RestfulApi {
    public static OperateInfo getOperateInfo(HttpServletRequest request){
        /**
         * 操作者信息
         */
        OperateInfo operateInfo;
        Object tryOperateInfo = request.getAttribute("operateInfo");
        if (tryOperateInfo instanceof OperateInfo){
            operateInfo = (OperateInfo)tryOperateInfo;
        }else{
            operateInfo = OperateInfo.create(request.getHeader("x-restful-operate-info"));
            /**
             * 存储操作者信息
             */
            request.setAttribute("operateInfo", operateInfo);
        }
        return operateInfo;
    }
    public static OperateInfo getOperateInfo(NativeWebRequest request){
        /**
         * 操作者信息
         */
        OperateInfo operateInfo;
        Object tryOperateInfo = request.getAttribute("operateInfo", NativeWebRequest.SCOPE_REQUEST);
        if (tryOperateInfo instanceof OperateInfo){
            operateInfo = (OperateInfo)tryOperateInfo;
        }else{
            operateInfo = OperateInfo.create(request.getHeader("x-restful-operate-info"));
            /**
             * 存储操作者信息
             */
            request.setAttribute("operateInfo", operateInfo, NativeWebRequest.SCOPE_REQUEST);
        }
        return operateInfo;
    }
}
