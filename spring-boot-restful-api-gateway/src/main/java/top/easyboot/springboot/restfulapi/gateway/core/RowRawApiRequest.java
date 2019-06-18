package top.easyboot.springboot.restfulapi.gateway.core;

import io.netty.buffer.PooledByteBufAllocator;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.*;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import top.easyboot.core.rowraw.RowRawEntity;

import java.net.*;
import java.util.*;

public class RowRawApiRequest extends AbstractServerHttpRequest {

    private final HttpMethod httpMethod;

    private final String connectionId;

    private final MultiValueMap<String, HttpCookie> cookies;

    @Nullable
    private final InetSocketAddress remoteAddress;

    @Nullable
    private final SslInfo sslInfo;

    private final Flux<DataBuffer> body;
    /**
     * 请求id的头的key
     */
    private static String requestIdHeaderKey = "x-request-id";


    private RowRawApiRequest(String connectionId, HttpMethod httpMethod, URI uri, @Nullable String contextPath,
                                  HttpHeaders headers, MultiValueMap<String, HttpCookie> cookies,
                                  @Nullable InetSocketAddress remoteAddress, @Nullable SslInfo sslInfo,
                                  Publisher<? extends DataBuffer> body) {

        super(uri, contextPath, headers);
        this.httpMethod = httpMethod;
        this.cookies = cookies;
        this.remoteAddress = remoteAddress;
        this.sslInfo = sslInfo;
        this.body = Flux.from(body);
        this.connectionId = connectionId;
    }

    public static void setRequestIdHeaderKey(String requestIdHeaderKey) {
        RowRawApiRequest.requestIdHeaderKey = requestIdHeaderKey;
    }

    public static RowRawApiRequest create(String connectionId, RowRawEntity entity,
                                          @Nullable InetSocketAddress remoteAddress, @Nullable SslInfo sslInfo) throws URISyntaxException, MalformedURLException{
        String protocol = sslInfo == null ? "http" : "https";
        String method = entity.getMethod();

        HttpMethod httpMethod = HttpMethod.valueOf(method);

        Map<String, String> headers = entity.getHeaders();
        HttpHeaders httpHeaders = new HttpHeaders();
        MultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<>();

        for (String key : headers.keySet()) {
            httpHeaders.put(key, Arrays.asList(headers.get(key).split(",")));
        }

        InetSocketAddress host = httpHeaders.getHost();
        int port;
        String hostName;
        if (host != null){
            hostName = host.getHostName();
            if (host.getPort() == 0){
                port = sslInfo == null ? 80 : 443;
            }else{
                port = host.getPort();
            }
        }else{
            port = 0;
            hostName = null;
        }

        URI uri = new URL(protocol, hostName==null?null:hostName.trim(), port, entity.getPath().trim()).toURI();

//        final DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        final DataBufferFactory bufferFactory = new NettyDataBufferFactory(new PooledByteBufAllocator(true));

        Publisher<? extends DataBuffer> body = Flux.just(bufferFactory.wrap(entity.getBody()));

        return new RowRawApiRequest(connectionId, httpMethod, uri, "", httpHeaders, cookies, remoteAddress, sslInfo, body);
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
        return getRequestId();
    }

    private String getRequestId(){
        String requestId = getHeaders().getFirst(requestIdHeaderKey);

        return requestId;
    }

    public String getConnectionId() {
        return connectionId;
    }
}
