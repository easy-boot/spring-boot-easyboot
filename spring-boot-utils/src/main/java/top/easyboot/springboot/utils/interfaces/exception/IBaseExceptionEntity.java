package top.easyboot.springboot.utils.interfaces.exception;

public interface IBaseExceptionEntity {
    String getExceptionId();
    void setExceptionId(String exceptionId);
    String getMessage();
    String getMessage(Object messageData);
}
