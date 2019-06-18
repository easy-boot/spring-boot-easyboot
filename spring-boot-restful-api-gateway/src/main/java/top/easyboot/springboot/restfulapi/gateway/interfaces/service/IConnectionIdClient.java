package top.easyboot.springboot.restfulapi.gateway.interfaces.service;

import java.util.Set;

public interface IConnectionIdClient {

    /**
     * 获取所有的连接id
     * @return 连接id集合
     */
    Set<String> getConnectionIds();

    /**
     * 获取某个用户的所有连接id
     * @param uid 用户uid
     * @return 连接id集合
     */
    Set<String> getConnectionIds(String uid);

    /**
     * 获取用户uid，通过连接id
     * @param connectionId 连接id
     * @return 用户uid
     */
    String getUid(String connectionId);
}
