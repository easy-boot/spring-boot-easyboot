package top.easyboot.springboot.operate.exception;

import org.springframework.core.MethodParameter;
import top.easyboot.springboot.operate.interfaces.exception.INotLoginException;

import java.lang.reflect.Method;

public class NotLoginException extends OperateException implements INotLoginException {
    static Class<NotLoginException> notLoginExceptionClass = NotLoginException.class;
    public NotLoginException(){
        super(E_NO_ACCOUNT_LOGIN);
    }
    public NotLoginException(Throwable cause){
        super(E_NO_ACCOUNT_LOGIN, cause);
    }

    public static NotLoginException create(MethodParameter parameter) throws OperateException {
        try{
            NotLoginException e = notLoginExceptionClass.newInstance();
            if (parameter != null){
                Method method = parameter.getMethod();
                Class aClass = parameter.getDeclaringClass();
                StackTraceElement[] stackTraceElements = e.getStackTrace();
                stackTraceElements[0] = new StackTraceElement(aClass.getName(), method.getName(), aClass.getSimpleName() + ".java", 1);

                e.setStackTrace(stackTraceElements);
            }
            return e;
        }catch (InstantiationException ei){
            throw new OperateException(E_INSTANTIATION_EXCEPTION, ei);
        }catch (IllegalAccessException ea){
            throw new OperateException(E_ILLEGAL_ACCESS_EXCEPTION, ea);
        }
    }

    public static Class<NotLoginException> getNotLoginExceptionClass() {
        return notLoginExceptionClass;
    }

    public static void setNotLoginExceptionClass(Class<NotLoginException> notLoginExceptionClass) {
        NotLoginException.notLoginExceptionClass = notLoginExceptionClass;
    }
}
