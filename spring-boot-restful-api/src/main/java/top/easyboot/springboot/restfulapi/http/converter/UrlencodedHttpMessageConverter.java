package top.easyboot.springboot.restfulapi.http.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class UrlencodedHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
    private Logger LOGGER = LoggerFactory.getLogger(UrlencodedHttpMessageConverter.class);

    public UrlencodedHttpMessageConverter() {
        // 构造方法中指明consumes（req）和produces（resp）的类型，指明这个类型才会使用这个converter
        super(MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        // 使用Serializable，这里可以直接返回true
        // 使用object，这里还要加上Serializable接口实现类判断
        // 根据自己的业务需求加上其他判断
        return false;
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        System.out.println("-*readInternal");
        try{

            Object a = clazz.newInstance();
            return a;
        }catch (Exception e){

        }
        return null;
//        byte[] bytes = StreamUtils.copyToByteArray(inputMessage.getBody());
//        // base64使得二进制数据可视化，便于测试
//        ByteArrayInputStream bytesInput = new ByteArrayInputStream(Base64.getDecoder().decode(bytes));
//        ObjectInputStream objectInput = new ObjectInputStream(bytesInput);
//        try {
//            System.out.println("545");
//            System.out.println(objectInput.readObject());
//            return (Serializable) objectInput.readObject();
//        } catch (ClassNotFoundException e) {
//            LOGGER.error("exception when java deserialize, the input is:{}", new String(bytes, "UTF-8"), e);
//            return null;
//        }
    }

    @Override
    protected void writeInternal(Object t, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        ByteArrayOutputStream bytesOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectOutput = new ObjectOutputStream(bytesOutput);
        objectOutput.writeObject(t);
        // base64使得二进制数据可视化，便于测试
        outputMessage.getBody().write(Base64.getEncoder().encode(bytesOutput.toByteArray()));
    }
}
