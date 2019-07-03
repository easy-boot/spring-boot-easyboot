package top.easyboot.springboot.utils.exception;

import top.easyboot.springboot.utils.interfaces.exception.IGlobalUniqueIdException;

public class GlobalUniqueIdException extends BaseException implements IGlobalUniqueIdException {
    public GlobalUniqueIdException(long inputId){
        super(inputId);
    }
}
