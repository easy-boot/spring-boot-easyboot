package top.easyboot.springboot.authorization.interfaces.core;

import top.easyboot.springboot.authorization.entity.Authorization;
import top.easyboot.springboot.authorization.entity.AuthorizationInput;
import top.easyboot.springboot.authorization.exception.AuthSignException;

public interface IAuthClient {
    /**
     * 试图授权
     * @param authorizationInput
     * @return
     */
    Authorization getAuthorization(AuthorizationInput authorizationInput) throws AuthSignException;
}
