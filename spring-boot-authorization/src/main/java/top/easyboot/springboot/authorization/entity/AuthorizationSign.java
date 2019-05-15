package top.easyboot.springboot.authorization.entity;


import top.easyboot.springboot.authorization.component.Client;

public class AuthorizationSign {
    /**
     * 分隔符
     */
    private String delimiter;
    /**
     * 处理驱动类名
     */
    private String className;
    /**
     * 授权版本号
     */
    private String authVersion;
    /**
     * 授权字符串
     */
    private String authValue;
    /**
     * 授权输入信息
     */
    private AuthorizationInput input;
    /**
     * 授权对象
     */
    private Authorization authorization;
    /**
     * 存储
     */
    Client.Storage storage;

    public AuthorizationInput getInput() {
        return input;
    }

    public void setInput(AuthorizationInput input) {
        this.input = input;
    }

    public Client.Storage getStorage() {
        return storage;
    }

    public void setStorage(Client.Storage storage) {
        this.storage = storage;
    }

    public Authorization getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getAuthVersion() {
        return authVersion;
    }

    public void setAuthVersion(String authVersion) {
        this.authVersion = authVersion;
    }

    public String getAuthValue() {
        return authValue;
    }

    public void setAuthValue(String authValue) {
        this.authValue = authValue;
    }
}
