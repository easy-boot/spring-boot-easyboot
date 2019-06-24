package top.easyboot.springboot.restfulapi.component;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import top.easyboot.springboot.restfulapi.entity.RestfulApiException;
import top.easyboot.springboot.restfulapi.exception.restTemplate.RpcException;
import top.easyboot.springboot.restfulapi.http.converter.UrlencodedHttpMessageConverter;
import top.easyboot.springboot.restfulapi.util.Jackson;

import java.io.IOException;
import java.net.URI;
import java.util.List;


public class RestTemplate extends org.springframework.web.client.RestTemplate {
    public RestTemplate() {
        super();
        this.addUrlencodedHttpMessageConverter();
    }
    public RestTemplate(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
        this.addUrlencodedHttpMessageConverter();
    }
    public RestTemplate(List<HttpMessageConverter<?>> messageConverters) {
        super(messageConverters);
        this.addUrlencodedHttpMessageConverter();
    }
    protected void addUrlencodedHttpMessageConverter(){
        List<HttpMessageConverter<?>> messageConverters = getMessageConverters();
        messageConverters.add(new UrlencodedHttpMessageConverter());
        setMessageConverters(messageConverters);
    }

    @Override
    protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().isError()){
            try {
                throw new RpcException(Jackson.getObjectMapper().readValue(response.getBody(), RestfulApiException.class));
            }catch (IOException e){
                RestfulApiException ae = new RestfulApiException();
                ae.setMessage(e.getMessage());
                ae.setExceptionId("RPC_FAIL");
                ae.setStatsCode(500);
                throw new RpcException(ae, e);
            }
        }
        super.handleResponse(url, method, response);
    }

    @Override
    protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) throws RestClientException {
        try {
            return super.doExecute(url, method, requestCallback, responseExtractor);
        }catch (ResourceAccessException e){
            Throwable cause = e.getCause();
            if (cause != null && cause instanceof RpcException){
                throw (RpcException)cause;
            }
            throw e;
        }
    }
}
