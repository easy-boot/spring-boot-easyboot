package top.easyboot.springboot.utils.interfaces.exception;

import top.easyboot.springboot.utils.annotation.ExampleMessage;

import static top.easyboot.springboot.utils.exception.BaseException.id;

public interface IWebSocketConnectionException{
    @ExampleMessage("connectionId empty")
    long E_EMPTY_CONNECTION_ID = id();
    @ExampleMessage("ConnectionId format error")
    long E_FORMAT_CONNECTION_ID = id();
}
