package top.easyboot.springboot.restfulapi.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import top.easyboot.springboot.restfulapi.utils.Jackson;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

public class AuthorizationInput {
    /**
     * 请求uri
     */
    protected URI uri;
    /**
     * 请求的主机头
     */
    protected InetSocketAddress host;
    /**
     * 请求方式
     */
    protected String method = "";
    /**
     * 请求头
     */
    protected Map<String, String> headers;
    /**
     * 授权头
     */
    protected String authorization = "";

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public InetSocketAddress getHost() {
        return host;
    }

    public void setHost(InetSocketAddress host) {
        this.host = host;
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

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
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
