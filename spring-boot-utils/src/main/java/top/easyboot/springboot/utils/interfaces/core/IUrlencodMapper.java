package top.easyboot.springboot.utils.interfaces.core;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface IUrlencodMapper {
    <T> T readUrlencod(String content, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException;
    <T> T readUrlencod(String content, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException;
    <T> T readUrlencod(String content, JavaType valueType) throws IOException, JsonParseException, JsonMappingException;

    <T> T readUrlencod(InputStream src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException;
    <T> T readUrlencod(InputStream src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException;
    <T> T readUrlencod(InputStream src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException;

    <T> T readUrlencod(byte[] src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException;
    <T> T readUrlencod(byte[] src, int offset, int len, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException;
    <T> T readUrlencod(byte[] src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException;
    <T> T readUrlencod(byte[] src, int offset, int len, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException;
    <T> T readUrlencod(byte[] src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException;
    <T> T readUrlencod(byte[] src, int offset, int len, JavaType valueType) throws IOException, JsonParseException, JsonMappingException;

    Map readUrlencod(byte[] bytes) throws IOException;
    Map readUrlencod(String content) throws IOException;
    Map readUrlencod(InputStream src) throws IOException;

    /**
     * Urlencod编码对象
     * @param data 数据对象
     * @param out 输出流
     * @throws IOException io异常
     */
    void writeAsUrlencod(Object data, OutputStream out) throws IOException;

    /**
     *
     * Urlencod编码对象
     * @param data 数据对象
     * @return 编码后的二进制字节
     * @throws JsonProcessingException json转换异常
     */
    byte[] writeAsUrlencod(Object data) throws JsonProcessingException;

    /**
     * Urlencod编码对象
     * @param data 数据对象
     * @return 编码后的字符串
     * @throws JsonProcessingException json转换异常
     */
    String writeAsUrlencodString(Object data) throws JsonProcessingException;
}
