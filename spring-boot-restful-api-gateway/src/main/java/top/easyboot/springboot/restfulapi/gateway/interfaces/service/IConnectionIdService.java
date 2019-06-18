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
     * 绑定用户uid与连接id关系
     * @param connectionId 连接id
     * @param uid 用户uid
     */
    void refresh(String connectionId, String uid);

}
