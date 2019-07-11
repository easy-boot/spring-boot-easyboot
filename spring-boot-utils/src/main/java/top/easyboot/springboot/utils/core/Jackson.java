package top.easyboot.springboot.utils.core;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jackson {
    private static ObjectMapper objectMapper = null;
    public final static ObjectMapper getObjectMapper(){
        if (objectMapper==null){
            objectMapper = new ObjectMapper();
        }
        return objectMapper;
    }
    public final static String toJson(Object object) throws JsonProcessingException {
        return getObjectMapper().writeValueAsString(object);
    }
}