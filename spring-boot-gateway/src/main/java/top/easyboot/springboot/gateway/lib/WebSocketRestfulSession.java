package top.easyboot.springboot.gateway.lib;

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

public class WebSocketRestfulSession {
    private WebSocketSession session;
    private FluxSink<WebSocketMessage> sink;
    private final Disposable.Composite compositeClose = Disposables.composite();
    private final Flux<WebSocketMessage> flux = Flux.create(sink -> sinkInit(sink));

    /**
     * 长连接会话
     * @param webSocketSession
     */
    public WebSocketRestfulSession(WebSocketSession webSocketSession){
        session = webSocketSession;

        session.receive().subscribe(message-> onWebSocketMessage(message), e->{
            e.printStackTrace();
            close();
        }, () -> close());
    }

    public static WebSocketRestfulSession createSession(WebSocketSession session){
        return new WebSocketRestfulSession(session);
    }
    public WebSocketSession getSession() {
        return session;
    }
    public Flux<WebSocketMessage> getFlux(){
        return flux;
    }
    public static Flux<WebSocketMessage> getFlux(WebSocketSession session){
        return createSession(session).getFlux();
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
    public WebSocketRestfulSession onClose(Disposable d){
        compositeClose.add(d);
        return this;
    }
    public WebSocketRestfulSession close(){
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
    public WebSocketRestfulSession textMessage(String message){
        if (!isClose()){
            sink.next(session.textMessage(message));
        }
        return this;
    }
    public WebSocketRestfulSession binaryMessage(Function<DataBufferFactory, DataBuffer> payloadFactory){
        if (!isClose()){
            sink.next(session.binaryMessage(payloadFactory));
        }
        return this;
    }
    public WebSocketRestfulSession pingMessage(Function<DataBufferFactory, DataBuffer> payloadFactory){
        if (!isClose()){
            sink.next(session.pingMessage(payloadFactory));
        }
        return this;
    }
    public WebSocketRestfulSession pongMessage(Function<DataBufferFactory, DataBuffer> payloadFactory){
        if (!isClose()){
            sink.next(session.pongMessage(payloadFactory));
        }
        return this;
    }
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
    private void onWebSocketMessage(WebSocketMessage webSocketMessage){

        System.out.println("ss");
        System.out.println(webSocketMessage.getType());
        String aa = webSocketMessage.getPayloadAsText();
        System.out.println(aa);
        System.out.println(webSocketMessage.getPayloadAsText());
        if (aa.equals("close1")){
            close();
        }else{
            this.textMessage("sss+++:"+aa);
        }
    }
}