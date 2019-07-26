package top.easyboot.springboot.utils.core;


import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Jackson {
    private static ObjectAndUrlencodMapper objectMapper = null;
    public final static ObjectAndUrlencodMapper getObjectMapper(){
        if (objectMapper==null){
            objectMapper = new ObjectAndUrlencodMapper();
        }
        return objectMapper;
    }
    public final static String toJson(Object object) throws JsonProcessingException {
        return getObjectMapper().writeValueAsString(object);
    }
    public final static void writeAsUrlencod(Object data, OutputStream out) throws IOException {
        getObjectMapper().writeAsUrlencod(data, out);
    }
    public final static byte[] writeAsUrlencod(Object data) throws JsonProcessingException{
        return getObjectMapper().writeAsUrlencod(data);
    }
    public final static String writeAsUrlencodString(Object data) throws JsonProcessingException{
        return getObjectMapper().writeAsUrlencodString(data);
    }
    public final static Map readUrlencod(String content) throws IOException{
        return getObjectMapper().readUrlencod(content);
    }
    public final static <T> T readUrlencod(String content, Class<T> valueType) throws IOException {
        return getObjectMapper().readUrlencod(content, valueType);
    }
}