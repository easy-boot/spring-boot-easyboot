package top.easyboot.springboot.authorization.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Hex {
    private static final Encoder encoder = new HexEncoder();

    public Hex() {
    }

    public static String toHexString(byte[] paramArrayOfByte) {
        return toHexString(paramArrayOfByte, 0, paramArrayOfByte.length);
    }

    public static String toHexString(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        byte[] arrayOfByte = encode(paramArrayOfByte, paramInt1, paramInt2);
        return fromByteArray(arrayOfByte);
    }

    public static String fromByteArray(byte[] paramArrayOfByte) {
        return new String(asCharArray(paramArrayOfByte));
    }

    public static char[] asCharArray(byte[] paramArrayOfByte) {
        char[] arrayOfChar = new char[paramArrayOfByte.length];

        for(int i = 0; i != arrayOfChar.length; ++i) {
            arrayOfChar[i] = (char)(paramArrayOfByte[i] & 255);
        }

        return arrayOfChar;
    }

    public static byte[] encode(byte[] paramArrayOfByte) {
        return encode(paramArrayOfByte, 0, paramArrayOfByte.length);
    }

    public static byte[] encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();

        try {
            encoder.encode(paramArrayOfByte, paramInt1, paramInt2, localByteArrayOutputStream);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return localByteArrayOutputStream.toByteArray();
    }

    public static int encode(byte[] paramArrayOfByte, OutputStream paramOutputStream) throws IOException {
        return encoder.encode(paramArrayOfByte, 0, paramArrayOfByte.length, paramOutputStream);
    }

    public static int encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, OutputStream paramOutputStream) throws IOException {
        return encoder.encode(paramArrayOfByte, paramInt1, paramInt2, paramOutputStream);
    }

    public static byte[] decode(byte[] paramArrayOfByte) {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();

        try {
            encoder.decode(paramArrayOfByte, 0, paramArrayOfByte.length, localByteArrayOutputStream);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return localByteArrayOutputStream.toByteArray();
    }

    public static byte[] decode(String paramString) {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();

        try {
            encoder.decode(paramString, localByteArrayOutputStream);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return localByteArrayOutputStream.toByteArray();
    }

    public static int decode(String paramString, OutputStream paramOutputStream) throws IOException {
        return encoder.decode(paramString, paramOutputStream);
    }
}
