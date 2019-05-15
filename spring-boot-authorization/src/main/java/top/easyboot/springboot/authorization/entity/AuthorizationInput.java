package top.easyboot.springboot.authorization.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import top.easyboot.springboot.authorization.utils.Jackson;

import java.net.URI;
import java.util.Map;

public class AuthorizationInput {
    /**
     * 请求uri
     */
    protected URI uri;
    /**
     * 请求方式
     */
    protected String method = "";
    /**
     * 请求头
     */
    protected Map<String, String> headers;
    /**
     * 自定义头前缀-授权签名
     */
    protected String authSignHeadersPrefix;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getAuthSignHeadersPrefix() {
        return authSignHeadersPrefix;
    }

    public void setAuthSignHeadersPrefix(String authSignHeadersPrefix) {
        this.authSignHeadersPrefix = authSignHeadersPrefix;
    }

    @Override
    public String toString() {
        try {
            return Jackson.toJson(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
