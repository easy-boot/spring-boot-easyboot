package top.easyboot.springboot.authorization.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Jackson {
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