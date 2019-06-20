package top.easyboot.springboot.restfulapi.gateway.core;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.function.Function;

public abstract class WebSocketSessionBase extends WebSocketSessionEntity {
    /**
     * 连接id
     */
    protected final String connectionId;
    /**
     * 长连接会话
     */
    protected final WebSocketSession session;
    /**
     * flux
     */
    protected final Flux<WebSocketMessage> flux;
    /**
     * sink
     */
    protected FluxSink<WebSocketMessage> sink;
    protected WebSocketSessionBase(String connectionId, WebSocketSession webSocketSession) throws ConnectionIdUtil.Exception {
        super(connectionId);
        this.connectionId = connectionId;
        flux = Flux.create(sink ->fluxSinkInit(sink));
        session = webSocketSession;
        sessionInit();

    }

    protected void fluxSinkInit(FluxSink fluxSink){
        this.sink = fluxSink;
        sink.onCancel(()-> close()).onDispose(()-> close());
    }
    protected void sessionInit(){
        session.receive().subscribe(message-> onWebSocketMessage(message), e->{
            e.printStackTrace();
            close();
        }, () -> close());
    }

    public abstract void nextMessage(WebSocketMessage message);
    public abstract void textMessage(String message);
    public abstract void binaryMessage(Function<DataBufferFactory, DataBuffer> payloadFactory);
    public abstract void close();
    public abstract void onWebSocketMessage(WebSocketMessage message);

    public Flux<WebSocketMessage> getFlux() {
        return flux;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public URI getUri() {
        return getHandshakeInfo().getUri();
    }

    @Nullable
    public InetSocketAddress getRemoteAddress(){
        return getHandshakeInfo().getRemoteAddress();
    }

    public HttpHeaders getHeaders() {
        return getHandshakeInfo().getHeaders();
    }

    /**
     * Return information from the handshake request.
     * @return handshake request
     */
    public HandshakeInfo getHandshakeInfo(){
        return session.getHandshakeInfo();
    }
}
