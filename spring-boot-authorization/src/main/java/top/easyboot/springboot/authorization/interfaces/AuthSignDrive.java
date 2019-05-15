package top.easyboot.springboot.authorization.interfaces;

import top.easyboot.springboot.authorization.entity.AuthorizationSign;
import top.easyboot.springboot.authorization.exception.AuthSignException;

public interface AuthSignDrive {
    void runAuthSign(AuthorizationSign authorizationSign) throws AuthSignException;
}
