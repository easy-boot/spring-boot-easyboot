package top.easyboot.springboot.restfulapi.gateway.interfaces.service;

public interface IUserAuthAccessService {
    /**
     * 获取授权数据，返回字符串
     * @param accessKeyId 授权id
     * @return 授权数据
     */
    String get(String accessKeyId);
    /**
     * 存储授权数据
     * @param accessKeyId 授权id
     * @param data 授权数据
     */
    void put(String accessKeyId, String data);
    /**
     * 根据授权Id的数据,获取uid
     * @param accessKeyId 授权Id
     * @return 用户uid
     */
    int getUid(String accessKeyId);
    /**
     * 修改授权Id关联的uid
     * @param accessKeyId 授权id
     * @param uid 用户uid
     */
    void putUid(String accessKeyId, int uid);

    /**
     * 判断是否符合accessKeyId
     * @param accessKeyId 授权id
     * @return 是否满足accessKeyId标准
     */
    boolean isAccessKeyId(String accessKeyId);

    /**
     * 创建一个accessKeyId
     * @return accessKeyId
     */
    String createAccessKeyId();
}
