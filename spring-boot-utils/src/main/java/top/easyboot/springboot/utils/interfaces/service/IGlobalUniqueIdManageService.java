package top.easyboot.springboot.utils.interfaces.service;

import top.easyboot.springboot.utils.service.GlobalUniqueIdService;

public interface IGlobalUniqueIdManageService extends GlobalUniqueIdService.Manage {
    void unregister();
}
