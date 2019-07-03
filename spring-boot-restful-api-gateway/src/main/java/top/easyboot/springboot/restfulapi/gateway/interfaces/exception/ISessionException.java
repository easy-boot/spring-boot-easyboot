package top.easyboot.springboot.restfulapi.gateway.interfaces.exception;

import top.easyboot.springboot.utils.annotation.ExampleMessage;

import static top.easyboot.springboot.utils.exception.BaseException.id;

public interface ISessionException {
    @ExampleMessage("No available connection id found")
    long E_NOT_AVAILABLE_CONNECTION_ID = id();
}
