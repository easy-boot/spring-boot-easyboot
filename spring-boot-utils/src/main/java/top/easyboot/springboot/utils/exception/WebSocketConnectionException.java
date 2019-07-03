package top.easyboot.springboot.utils.exception;

import top.easyboot.springboot.utils.interfaces.exception.IWebSocketConnectionException;

public class WebSocketConnectionException extends BaseException implements IWebSocketConnectionException {
    public WebSocketConnectionException(long inputId){
        super(inputId);
    }
}
