package top.easyboot.springboot.restfulapi.gateway.core;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.Disposable;
import reactor.core.Disposables;
import top.easyboot.springboot.restfulapi.util.ConnectionIdUtil;

import java.util.function.Function;

public abstract class WebSocketSessionAbstract extends WebSocketSessionBase {
    private boolean isCloseable;
    /**
     * compositeClose
     */
    protected final Disposable.Composite compositeClose;
    protected WebSocketSessionAbstract(String connectionId, WebSocketSession webSocketSession) throws ConnectionIdUtil.Exception {
        super(connectionId, webSocketSession);
        isCloseable = true;
        compositeClose = Disposables.composite();
    }

    public void onClose(Disposable d){
        compositeClose.add(d);
    }

    public void close(){
        if (!isCloseable){
            return;
        }
        isCloseable = false;
        try {
            compositeClose.dispose();
        }catch (Throwable e){

        }
        try {
            if (!sink.isCancelled()){
                sink.complete();
            }
        }catch (Throwable e){

        }
        try {
            session.close();
        }catch (Throwable e){

        }
    }

    public void nextMessage(WebSocketMessage message){
        if (sink.isCancelled()){
            close();
        }else if(sink!=null){
            sink.next(message);
        }
    }
    public void textMessage(String message){
        nextMessage(session.textMessage(message));
    }
    public void binaryMessage(Function<DataBufferFactory, DataBuffer> payloadFactory){
        nextMessage(session.binaryMessage(payloadFactory));
    }
}
