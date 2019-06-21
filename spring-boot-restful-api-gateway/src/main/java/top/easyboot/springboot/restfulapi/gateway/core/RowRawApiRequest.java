package top.easyboot.springboot.restfulapi.gateway.core;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.*;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

import java.net.*;

public class RowRawApiRequest extends AbstractServerHttpRequest {

    private final HttpMethod httpMethod;

    private final String requestId;
    private final String connectionId;

    private final MultiValueMap<String, HttpCookie> cookies;

    @Nullable
    private final InetSocketAddress remoteAddress;

    @Nullable
    private final SslInfo sslInfo;

    private final Flux<DataBuffer> body;


    public RowRawApiRequest(String requestId, String connectionId, HttpMethod httpMethod, URI uri, @Nullable String contextPath,
                                  HttpHeaders headers, MultiValueMap<String, HttpCookie> cookies,
                                  @Nullable InetSocketAddress remoteAddress, @Nullable SslInfo sslInfo,
                                  Publisher<? extends DataBuffer> body) {

        super(uri, contextPath, headers);
        this.requestId = requestId;
        this.httpMethod = httpMethod;
        this.cookies = cookies;
        this.remoteAddress = remoteAddress;
        this.sslInfo = sslInfo;
        this.body = Flux.from(body);
        this.connectionId = connectionId;
    }

    @Override
    public HttpMethod getMethod() {
        return this.httpMethod;
    }

    @Override
    public String getMethodValue() {
        return this.httpMethod.name();
    }

    @Override
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    @Nullable
    @Override
    protected SslInfo initSslInfo() {
        return this.sslInfo;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return this.body;
    }

    @Override
    protected MultiValueMap<String, HttpCookie> initCookies() {
        return this.cookies;
    }

    @Override
    public <T> T getNativeRequest() {
        throw new IllegalStateException("This is a easyboot restful api request. No running server, no native request.");
    }

    @Override
    protected String initId() {
        return requestId;
    }

    private String getRequestId(){
        return requestId;
    }

    public String getConnectionId() {
        return connectionId;
    }
}
