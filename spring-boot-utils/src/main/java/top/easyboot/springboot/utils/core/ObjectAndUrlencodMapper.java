package top.easyboot.springboot.utils.core;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.util.ClassUtil;
import top.easyboot.springboot.utils.interfaces.core.IUrlencodMapper;

import java.io.*;
import java.util.*;

public class ObjectAndUrlencodMapper extends ObjectMapper implements IUrlencodMapper {
    private final byte separator = '&';
    private final byte equal = '=';
    @Override
    public <T> T readUrlencod(String content, Class<T> valueType) throws IOException {
        return convertValue(readUrlencod(content), valueType);
    }

    @Override
    public <T> T readUrlencod(InputStream src, Class<T> valueType) throws IOException {
        return convertValue(readUrlencod(src), valueType);
    }

    @Override
    public <T> T readUrlencod(String content, JavaType valueType) throws IOException {
        return convertValue(readUrlencod(content), valueType);
    }

    @Override
    public <T> T readUrlencod(InputStream src, JavaType valueType) throws IOException {
        return convertValue(readUrlencod(src), valueType);
    }

    @Override
    public <T> T readUrlencod(String content, TypeReference valueTypeRef) throws IOException {
        return convertValue(readUrlencod(content), valueTypeRef);
    }

    @Override
    public <T> T readUrlencod(InputStream src, TypeReference valueTypeRef) throws IOException {
        return convertValue(readUrlencod(src), valueTypeRef);
    }

    @Override
    public <T> T readUrlencod(byte[] src, Class<T> valueType) throws IOException {
        return readUrlencod(src, valueType);
    }

    @Override
    public <T> T readUrlencod(byte[] src, JavaType valueType) throws IOException {
        return readUrlencod(src, valueType);
    }

    @Override
    public <T> T readUrlencod(byte[] src, TypeReference valueTypeRef) throws IOException {
        return readUrlencod(src, valueTypeRef);
    }

    @Override
    public <T> T readUrlencod(byte[] src, int offset, int len, Class<T> valueType) throws IOException {
        byte[] dest = new byte[len];
        System.arraycopy(src, offset, dest, 0, len);
        return readUrlencod(dest, valueType);
    }

    @Override
    public <T> T readUrlencod(byte[] src, int offset, int len, JavaType valueType) throws IOException {
        byte[] dest = new byte[len];
        System.arraycopy(src, offset, dest, 0, len);
        return readUrlencod(dest, valueType);
    }

    @Override
    public <T> T readUrlencod(byte[] src, int offset, int len, TypeReference valueTypeRef) throws IOException {
        byte[] dest = new byte[len];
        System.arraycopy(src, offset, dest, 0, len);
        return readUrlencod(dest, valueTypeRef);
    }

    @Override
    public Map readUrlencod(String content) throws IOException {
        return readUrlencod(content.getBytes());
    }

    @Override
    public Map readUrlencod(byte[] bytes) throws IOException {
        return readUrlencod(new ByteArrayInputStream(bytes));
    }

    @Override
    public Map readUrlencod(InputStream input) throws IOException {
        final Map res = new HashMap();
        final int addLen = 10;
        int maxLen = addLen;
        // 数组大小由文件决定
        byte[] bytes = new byte[maxLen] ;
        int len = 0 ;
        int temp = 0 ;
        while ((temp=input.read())!=-1){
            // 保证缓冲区
            if (len == maxLen || len > maxLen){
                maxLen = maxLen + addLen;
                byte[] newBytes = new byte[maxLen] ;
                System.arraycopy(bytes, 0, newBytes, 0, len);
                bytes = newBytes;
            }
            // 查找 &
            if (temp == separator){
                byte[] lineBytes = new byte[len];
                System.arraycopy(bytes, 0, lineBytes, 0, len);
                _readUrlencod(res, lineBytes);
                len = 0 ;
                maxLen = addLen;
                bytes = new byte[maxLen] ;
                // 跳过本次追加
                continue;
            }
            // 追加内容到缓冲区
            bytes[len] = (byte)temp ;
            len++ ;
        }
        // 关闭输出流
        input.close();
        byte[] lineBytes = new byte[len];
        System.arraycopy(bytes, 0, lineBytes, 0, len);
        bytes = null;
        _readUrlencod(res, lineBytes);
        return res;
    }

    protected void _readUrlencod(Map data, byte[] lines) throws IOException {
        int nameLen = 0;
        boolean isHasEqual = false;
        for (byte line : lines) {
            if (line == equal){
                isHasEqual = true;
                break;
            }
            nameLen++;
        }
        byte[] nameBytes = new byte[nameLen];
        System.arraycopy(lines, 0, nameBytes, 0, nameLen);
        final int valueLen = lines.length - nameLen - 1;
        final String value;
        if (isHasEqual && valueLen>0){
            final byte[] valueBytes = new byte[valueLen];
            System.arraycopy(lines, nameLen + 1, valueBytes, 0, valueLen);
            value = new String(valueBytes);
        }else{
            value = "";
        }
        UrlencodDataStorage.save(new String(nameBytes), value, data);

    }
    public String writeAsUrlencodString(Object data) throws JsonProcessingException {
        // alas, we have to pull the recycler directly here...
        SegmentedStringWriter sw = new SegmentedStringWriter(_jsonFactory._getBufferRecycler());
        try {
            _configAndWriteData(_jsonFactory.createGenerator(sw), data);
        } catch (JsonProcessingException e) {
            throw e;
        } catch (IOException e) { // shouldn't really happen, but is declared as possibility so:
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
        return sw.getAndClear();
    }
    public byte[] writeAsUrlencod(Object data) throws JsonProcessingException {

        ByteArrayBuilder bb = new ByteArrayBuilder(_jsonFactory._getBufferRecycler());
        try {
            _configAndWriteData(_jsonFactory.createGenerator(bb), data);
        } catch (JsonProcessingException e) { // to support [JACKSON-758]
            throw e;
        } catch (IOException e) { // shouldn't really happen, but is declared as possibility so:
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
        byte[] result = bb.toByteArray();
        bb.release();
        return result;
    }
    public void writeAsUrlencod(Object data, OutputStream out) throws IOException {
        _configAndWriteData(_jsonFactory.createGenerator(out, JsonEncoding.UTF8), data);
    }
    protected void _configAndWriteData(JsonGenerator g, Object data) throws IOException{

        SerializationConfig cfg = getSerializationConfig();
        cfg.initialize(g); // since 2.5
        if (cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && (data instanceof Closeable)) {
            Closeable toClose = (Closeable) data;
            try {
                _serializerUrlencod(g, data);
                Closeable tmpToClose = toClose;
                toClose = null;
                tmpToClose.close();
            } catch (Exception e) {
                ClassUtil.closeOnFailAndThrowAsIOE(g, toClose, e);
                return;
            }
        }else{
            try {
                _serializerUrlencod(g, data);
            } catch (Exception e) {
                ClassUtil.closeOnFailAndThrowAsIOE(g, e);
                return;
            }
        }
        g.close();
    }
    protected void _serializerUrlencod(JsonGenerator gen, Object data) throws IOException {
        _serializerUrlencod(gen, data, true, "");
    }
    protected void _serializerUrlencod(JsonGenerator gen, Object data, boolean isFirst, String prefix) throws IOException {
        boolean isEmptyPrefix = prefix == null || prefix.isEmpty();
        String name = "";
        if (data == null || data instanceof String || data instanceof Number || data instanceof Boolean) {
            final String v;
            if (data == null){
                v = "";
            }else if (data instanceof String){
                v = (String)data;
            }else if (data instanceof Number){
                v = String.valueOf(data);
            }else if (data instanceof Boolean){
                v = (Boolean)data ? "true" : "false";
            }else{
                v = "";
            }
            if (!isFirst){
                gen.writeRaw("&");
            }
            gen.writeRaw(URLUtil.urlEncode(prefix) + "=" + URLUtil.urlEncode(v));
            return;
        }else if(data instanceof List || data instanceof Set || data.getClass().isArray()){
            final List list;
            if (data instanceof Set){
                final List<Object> newData = new ArrayList<>();
                for (Object item : ((Set)data)) {
                    newData.add(item);
                }
                list = newData;
            }else if (data.getClass().isArray()){
                final List<Object> newData = new ArrayList<>();
                for (Object item : ((Object[])data)) {
                    newData.add(item);
                }
                list = newData;
            }else{
                list = (List)data;
            }

            for (int i = 0; i < list.size(); i++) {
                final Object value = list.get(i);
                _serializerUrlencod(gen, value, isFirst, isEmptyPrefix ? String.valueOf(i) : (prefix + "[" + ((value instanceof Map|value instanceof Set|value instanceof List) ? i : "") + "]"));
                if (isFirst){
                    isFirst = false;
                }
            }
        }else if(data instanceof Map){
            final Map map = (Map)data;
            for (Object key : map.keySet()) {
                _serializerUrlencod(gen, map.get(key), isFirst, isEmptyPrefix ? key.toString() : (prefix + "[" + key + "]"));
                if (isFirst){
                    isFirst = false;
                }
            }

        }else if (data instanceof Object){
            _serializerUrlencod(gen, convertValue(data, Map.class), isFirst, prefix);
        }
    }
}
