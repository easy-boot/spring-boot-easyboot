package top.easyboot.springboot.utils.interfaces.service;

import top.easyboot.springboot.utils.exception.GlobalUniqueIdException;

public interface IGlobalUniqueIdService {
    /**
     * 创建唯一id
     * @return 全局唯一id
     * @throws GlobalUniqueIdException
     */
    String createUniqueId() throws GlobalUniqueIdException;
}
