package top.easyboot.springboot.restfulapi.gateway.core;

import org.springframework.core.io.buffer.DataBuffer;
import top.easyboot.core.rowraw.RowRawEntity;
import top.easyboot.core.rowraw.RowRawUtil;

public class RowRawDataBufferUtil {
    public static RowRawEntity pase(DataBuffer buffer){
        byte[] pos = new byte[buffer.readableByteCount()];
        buffer.read(pos);
        return RowRawUtil.parse(pos);
    }
}
