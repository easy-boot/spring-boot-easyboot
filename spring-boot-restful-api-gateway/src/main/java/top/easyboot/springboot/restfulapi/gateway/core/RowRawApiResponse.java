package top.easyboot.springboot.restfulapi.gateway.core;

import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import top.easyboot.core.rowraw.RowRawEntity;

import java.util.Collection;

public class RowRawApiResponse extends AbstractServerHttpResponse {
    private String requestId;
    protected RowRawEntity rawEntity;
    public RowRawApiResponse(RowRawEntity rawEntity) {
        this(rawEntity, new NettyDataBufferFactory(new UnpooledByteBufAllocator(false,false,false)));
    }
    public RowRawApiResponse(DataBufferFactory dataBufferFactory) {
        this(new RowRawEntity(), dataBufferFactory);
    }
    public RowRawApiResponse() {
        this(new RowRawEntity(), new NettyDataBufferFactory(new UnpooledByteBufAllocator(false,false,false)));
    }
    public RowRawApiResponse(RowRawEntity rawEntity, DataBufferFactory dataBufferFactory) {
        super(dataBufferFactory);
        this.rawEntity = rawEntity;
    }


    @Override
    protected void applyStatusCode() {
        HttpStatus httpStatus = getStatusCode();
        if (httpStatus == null){
            if (rawEntity.getStatus() == null || rawEntity.getStatus().isEmpty()){
                httpStatus = HttpStatus.resolve(200);
            }
        }
        if (httpStatus != null){
            rawEntity.setStatus(String.valueOf(httpStatus.value()));
            rawEntity.setStatusText(httpStatus.getReasonPhrase());
        }
    }

    @Override
    protected void applyHeaders() {
        HttpHeaders httpHeaders = getHeaders();
        if (httpHeaders!=null){
            rawEntity.setHeaders(httpHeaders.toSingleValueMap());
        }
    }

    @Override
    protected void applyCookies() {
        getCookies().values().stream().flatMap(Collection::stream)
                .forEach(cookie -> getHeaders().add(HttpHeaders.SET_COOKIE, cookie.toString()));
        applyHeaders();
    }

    private Mono<Void> writeHandler(Flux<DataBuffer> body){
        // Avoid .then() which causes data buffers to be released
        MonoProcessor<Void> completion = MonoProcessor.create();

        body.subscribe(buffer -> {
            byte[] pos = new byte[buffer.readableByteCount()];
            buffer.read(pos);
            rawEntity.setBody(pos);
            ReferenceCountUtil.release(buffer);
            DataBufferUtils.release(buffer);
            completion.onComplete();
        }, throwable->{
            completion.onError(throwable);
        });

        return completion;
    }
    @Override
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> body) {
        return writeHandler(Flux.from(body));
    }

    @Override
    protected Mono<Void> writeAndFlushWithInternal(
            Publisher<? extends Publisher<? extends DataBuffer>> body) {

        return writeHandler(Flux.from(body).concatMap(Flux::from));
    }

    @Override
    public Mono<Void> setComplete() {
        return doCommit(() -> Mono.defer(() -> writeHandler(Flux.empty())));
    }

    @Override
    public <T> T getNativeResponse() {
        throw new IllegalStateException("This is a easyboot restful api request. No running server, no native request.");
    }

    public RowRawEntity getRawEntity() {
        applyCookies();
        applyHeaders();
        applyStatusCode();
        return rawEntity;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


}