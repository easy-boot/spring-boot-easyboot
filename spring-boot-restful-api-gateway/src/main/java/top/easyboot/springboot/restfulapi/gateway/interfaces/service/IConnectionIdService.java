package top.easyboot.springboot.restfulapi.gateway.interfaces.service;


import java.util.Set;

public interface IConnectionIdService {
    /**
     * 生成连接id
     * @return
     * @throws Exception
     */
    String generateConnectionId() throws Exception;

    /**
     * 添加连接id
     * @param connectionId 连接id
     */
    void add(String connectionId);

    /**
     * 移除连接id
     * @param connectionId 连接id
     */
    void remove(String connectionId);

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

    /**
     * 绑定用户uid与连接id关系
     * @param connectionId 连接id
     * @param uid 用户uid
     */
    void bind(String connectionId, String uid);

    /**
     * 解绑用户uid与连接id关系
     * @param connectionId 连接id
     * @param uid 用户uid
     */
    void unbind(String connectionId, String uid);

}
