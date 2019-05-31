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

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.function.Function;

public abstract class WebSocketRestfulSessionBase {

    private WebSocketSession session;
    private FluxSink<WebSocketMessage> sink;
    private final Disposable.Composite compositeClose = Disposables.composite();
    private final Flux<WebSocketMessage> flux = Flux.create(sink -> sinkInit(sink));

    abstract void onWebSocketMessage(WebSocketMessage webSocketMessage);
    abstract void init();
    /**
     * 长连接会话
     * @param webSocketSession
     */
    public WebSocketRestfulSessionBase(WebSocketSession webSocketSession){
        session = webSocketSession;

        session.receive().subscribe(message-> onWebSocketMessage(message), e->{
            e.printStackTrace();
            close();
        }, () -> close());
        init();
    }
    public WebSocketSession getSession() {
        return session;
    }
    public Flux<WebSocketMessage> getFlux(){
        return flux;
    }
    private void sinkInit(FluxSink<WebSocketMessage> sink){
        this.sink = sink.onCancel(()-> close()).onDispose(()-> close());
    }
    public boolean isClose(){
        boolean is = sink==null || sink.isCancelled();
        if (is){
            close();
        }
        return is;
    }
    public WebSocketRestfulSessionBase onClose(Disposable d){
        compositeClose.add(d);
        return this;
    }
    public WebSocketRestfulSessionBase close(){
        compositeClose.dispose();
        try {
            sink.complete();
        }catch (Throwable e){

        }
        try {
            session.close();
        }catch (Throwable e){

        }

        return this;
    }
    public WebSocketRestfulSessionBase textMessage(String message){
        if (!isClose()){
            sink.next(session.textMessage(message));
        }
        return this;
    }
    public WebSocketRestfulSessionBase binaryMessage(Function<DataBufferFactory, DataBuffer> payloadFactory){
        if (!isClose()){
            sink.next(session.binaryMessage(payloadFactory));
        }
        return this;
    }
    public WebSocketRestfulSessionBase pingMessage(Function<DataBufferFactory, DataBuffer> payloadFactory){
        if (!isClose()){
            sink.next(session.pingMessage(payloadFactory));
        }
        return this;
    }
    public WebSocketRestfulSessionBase pongMessage(Function<DataBufferFactory, DataBuffer> payloadFactory){
        if (!isClose()){
            sink.next(session.pongMessage(payloadFactory));
        }
        return this;
    }

    /**
     * Return information from the handshake request.
     * @return handshake request
     */
    public HandshakeInfo getHandshakeInfo(){
        return session.getHandshakeInfo();
    }
    @Nullable
    public InetSocketAddress getRemoteAddress(){
        return getHandshakeInfo().getRemoteAddress();
    }
    /**
     * Return the URL for the WebSocket endpoint.
     */
    public URI getUri() {
        return getHandshakeInfo().getUri();
    }

    /**
     * Return the handshake HTTP headers. Those are the request headers for a
     * server session and the response headers for a client session.
     */
    public HttpHeaders getHeaders() {
        return getHandshakeInfo().getHeaders();
    }

}
