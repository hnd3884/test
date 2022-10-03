package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

public class HexEncoder implements Encoder
{
    protected final byte[] encodingTable;
    protected final byte[] decodingTable;
    
    protected void initialiseDecodingTable() {
        for (int i = 0; i < this.decodingTable.length; ++i) {
            this.decodingTable[i] = -1;
        }
        for (int j = 0; j < this.encodingTable.length; ++j) {
            this.decodingTable[this.encodingTable[j]] = (byte)j;
        }
        this.decodingTable[65] = this.decodingTable[97];
        this.decodingTable[66] = this.decodingTable[98];
        this.decodingTable[67] = this.decodingTable[99];
        this.decodingTable[68] = this.decodingTable[100];
        this.decodingTable[69] = this.decodingTable[101];
        this.decodingTable[70] = this.decodingTable[102];
    }
    
    public HexEncoder() {
        this.encodingTable = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
        this.decodingTable = new byte[128];
        this.initialiseDecodingTable();
    }
    
    public int encode(final byte[] array, final int n, final int n2, final OutputStream outputStream) throws IOException {
        for (int i = n; i < n + n2; ++i) {
            final int n3 = array[i] & 0xFF;
            outputStream.write(this.encodingTable[n3 >>> 4]);
            outputStream.write(this.encodingTable[n3 & 0xF]);
        }
        return n2 * 2;
    }
    
    private static boolean ignore(final char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }
    
    public int decode(final byte[] array, final int n, final int n2, final OutputStream outputStream) throws IOException {
        int n3 = 0;
        int n4;
        for (n4 = n + n2; n4 > n && ignore((char)array[n4 - 1]); --n4) {}
        int i = n;
        while (i < n4) {
            while (i < n4 && ignore((char)array[i])) {
                ++i;
            }
            byte b;
            for (b = this.decodingTable[array[i++]]; i < n4 && ignore((char)array[i]); ++i) {}
            final byte b2 = this.decodingTable[array[i++]];
            if ((b | b2) < 0) {
                throw new IOException("invalid characters encountered in Hex data");
            }
            outputStream.write(b << 4 | b2);
            ++n3;
        }
        return n3;
    }
    
    public int decode(final String s, final OutputStream outputStream) throws IOException {
        int n = 0;
        int length;
        for (length = s.length(); length > 0 && ignore(s.charAt(length - 1)); --length) {}
        int i = 0;
        while (i < length) {
            while (i < length && ignore(s.charAt(i))) {
                ++i;
            }
            byte b;
            for (b = this.decodingTable[s.charAt(i++)]; i < length && ignore(s.charAt(i)); ++i) {}
            final byte b2 = this.decodingTable[s.charAt(i++)];
            if ((b | b2) < 0) {
                throw new IOException("invalid characters encountered in Hex string");
            }
            outputStream.write(b << 4 | b2);
            ++n;
        }
        return n;
    }
}
