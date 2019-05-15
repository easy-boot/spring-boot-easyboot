package top.easyboot.springboot.authorization.utils;


import java.io.IOException;
import java.io.OutputStream;

public class HexEncoder implements Encoder {
    protected final byte[] encodingTable = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    protected final byte[] decodingTable = new byte[128];

    protected void initialiseDecodingTable() {
        int i;
        for(i = 0; i < this.decodingTable.length; ++i) {
            this.decodingTable[i] = -1;
        }

        for(i = 0; i < this.encodingTable.length; ++i) {
            this.decodingTable[this.encodingTable[i]] = (byte)i;
        }

        this.decodingTable[65] = this.decodingTable[97];
        this.decodingTable[66] = this.decodingTable[98];
        this.decodingTable[67] = this.decodingTable[99];
        this.decodingTable[68] = this.decodingTable[100];
        this.decodingTable[69] = this.decodingTable[101];
        this.decodingTable[70] = this.decodingTable[102];
    }

    public HexEncoder() {
        this.initialiseDecodingTable();
    }

    public int encode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, OutputStream paramOutputStream) throws IOException {
        for(int i = paramInt1; i < paramInt1 + paramInt2; ++i) {
            int j = paramArrayOfByte[i] & 255;
            paramOutputStream.write(this.encodingTable[j >>> 4]);
            paramOutputStream.write(this.encodingTable[j & 15]);
        }

        return paramInt2 * 2;
    }

    private static boolean ignore(char paramChar) {
        return paramChar == '\n' || paramChar == '\r' || paramChar == '\t' || paramChar == ' ';
    }

    public int decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2, OutputStream paramOutputStream) throws IOException {
        int k = 0;

        for(int m = paramInt1 + paramInt2; m > paramInt1 && ignore((char)paramArrayOfByte[m - 1]); --m) {
            for(int n = paramInt1; n < m; ++k) {
                while(n < m && ignore((char)paramArrayOfByte[n])) {
                    ++n;
                }

                byte i;
                for(i = this.decodingTable[paramArrayOfByte[n++]]; n < m && ignore((char)paramArrayOfByte[n]); ++n) {
                }

                int j = this.decodingTable[paramArrayOfByte[n++]];
                if ((i | j) < 0) {
                    throw new IOException("invalid characters encountered in Hex data");
                }

                paramOutputStream.write(i << 4 | j);
            }
        }

        return k;
    }

    public int decode(String paramString, OutputStream paramOutputStream) throws IOException {
        int k = 0;

        for(int m = paramString.length(); m > 0 && ignore(paramString.charAt(m - 1)); --m) {
            for(int n = 0; n < m; ++k) {
                while(n < m && ignore(paramString.charAt(n))) {
                    ++n;
                }

                byte i;
                for(i = this.decodingTable[paramString.charAt(n++)]; n < m && ignore(paramString.charAt(n)); ++n) {
                }

                int j = this.decodingTable[paramString.charAt(n++)];
                if ((i | j) < 0) {
                    throw new IOException("invalid characters encountered in Hex string");
                }

                paramOutputStream.write(i << 4 | j);
            }
        }

        return k;
    }
}
