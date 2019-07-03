package top.easyboot.springboot.utils.interfaces.exception;

import top.easyboot.springboot.utils.annotation.ExampleMessage;

import static top.easyboot.springboot.utils.exception.BaseException.id;

public interface IGlobalUniqueIdException {
    @ExampleMessage("create unique id fail")
    long E_CREATE_UNIQUE_ID_FAIL = id();
}
