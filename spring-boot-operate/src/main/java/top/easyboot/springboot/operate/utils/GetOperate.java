package top.easyboot.springboot.operate.utils;

import org.springframework.web.context.request.NativeWebRequest;
import top.easyboot.springboot.operate.entity.Operate;

import javax.servlet.http.HttpServletRequest;

public class GetOperate {
    public static Operate get(HttpServletRequest request){
        /**
         * 操作者信息
         */
        Operate operate;
        Object tryOperateInfo = request.getAttribute("easyboot-operate");
        if (tryOperateInfo instanceof Operate){
            operate = (Operate)tryOperateInfo;
        }else{
            operate = Operate.create(request.getHeader("x-easyboot-operate-info"));
            /**
             * 存储操作者信息
             */
            request.setAttribute("easyboot-operate", operate);
        }
        return operate;
    }
    public static Operate get(NativeWebRequest request){
        /**
         * 操作者信息
         */
        Operate operate;
        Object tryOperateInfo = request.getAttribute("easyboot-operate", NativeWebRequest.SCOPE_REQUEST);
        if (tryOperateInfo instanceof Operate){
            operate = (Operate)tryOperateInfo;
        }else{
            operate = Operate.create(request.getHeader("x-easyboot-operate-info"));
            /**
             * 存储操作者信息
             */
            request.setAttribute("easyboot-operate", operate, NativeWebRequest.SCOPE_REQUEST);
        }
        return operate;
    }
}
