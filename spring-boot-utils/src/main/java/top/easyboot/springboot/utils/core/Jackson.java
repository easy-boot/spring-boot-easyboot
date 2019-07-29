package top.easyboot.springboot.utils.core;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class Jackson {
    private static ObjectAndUrlencodMapper objectMapper = null;
    public static ObjectAndUrlencodMapper getObjectMapper(){
        if (objectMapper==null){
            objectMapper = new ObjectAndUrlencodMapper();
        }
        return objectMapper;
    }
    public static String toJson(Object object) throws JsonProcessingException {
        return getObjectMapper().writeValueAsString(object);
    }
    public static void writeAsUrlencod(Object data, OutputStream out) throws IOException {
        getObjectMapper().writeAsUrlencod(data, out);
    }
    public static byte[] writeAsUrlencod(Object data) throws JsonProcessingException{
        return getObjectMapper().writeAsUrlencod(data);
    }
    public static String writeAsUrlencodString(Object data) throws JsonProcessingException{
        return getObjectMapper().writeAsUrlencodString(data);
    }
    public static Map readUrlencod(String content) throws IOException{
        return getObjectMapper().readUrlencod(content);
    }
    public static <T> T readUrlencod(String content, Class<T> valueType) throws IOException {
        return getObjectMapper().readUrlencod(content, valueType);
    }
    public static Map readValue(String content)
            throws IOException, JsonParseException, JsonMappingException
    {
        return getObjectMapper().readValue(content, Map.class);
    }
    public static <T> T readValue(String content, Class<T> valueType)
            throws IOException, JsonParseException, JsonMappingException
    {
        return getObjectMapper().readValue(content, valueType);
    }
}