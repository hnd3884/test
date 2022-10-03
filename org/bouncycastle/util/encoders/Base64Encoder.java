package org.bouncycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

public class Base64Encoder implements Encoder
{
    protected final byte[] encodingTable;
    protected byte padding;
    protected final byte[] decodingTable;
    
    protected void initialiseDecodingTable() {
        for (int i = 0; i < this.decodingTable.length; ++i) {
            this.decodingTable[i] = -1;
        }
        for (int j = 0; j < this.encodingTable.length; ++j) {
            this.decodingTable[this.encodingTable[j]] = (byte)j;
        }
    }
    
    public Base64Encoder() {
        this.encodingTable = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        this.padding = 61;
        this.decodingTable = new byte[128];
        this.initialiseDecodingTable();
    }
    
    public int encode(final byte[] array, final int n, final int n2, final OutputStream outputStream) throws IOException {
        final int n3 = n2 % 3;
        final int n4 = n2 - n3;
        for (int i = n; i < n + n4; i += 3) {
            final int n5 = array[i] & 0xFF;
            final int n6 = array[i + 1] & 0xFF;
            final int n7 = array[i + 2] & 0xFF;
            outputStream.write(this.encodingTable[n5 >>> 2 & 0x3F]);
            outputStream.write(this.encodingTable[(n5 << 4 | n6 >>> 4) & 0x3F]);
            outputStream.write(this.encodingTable[(n6 << 2 | n7 >>> 6) & 0x3F]);
            outputStream.write(this.encodingTable[n7 & 0x3F]);
        }
        switch (n3) {
            case 1: {
                final int n8 = array[n + n4] & 0xFF;
                final int n9 = n8 >>> 2 & 0x3F;
                final int n10 = n8 << 4 & 0x3F;
                outputStream.write(this.encodingTable[n9]);
                outputStream.write(this.encodingTable[n10]);
                outputStream.write(this.padding);
                outputStream.write(this.padding);
                break;
            }
            case 2: {
                final int n11 = array[n + n4] & 0xFF;
                final int n12 = array[n + n4 + 1] & 0xFF;
                final int n13 = n11 >>> 2 & 0x3F;
                final int n14 = (n11 << 4 | n12 >>> 4) & 0x3F;
                final int n15 = n12 << 2 & 0x3F;
                outputStream.write(this.encodingTable[n13]);
                outputStream.write(this.encodingTable[n14]);
                outputStream.write(this.encodingTable[n15]);
                outputStream.write(this.padding);
                break;
            }
        }
        return n4 / 3 * 4 + ((n3 == 0) ? 0 : 4);
    }
    
    private boolean ignore(final char c) {
        return c == '\n' || c == '\r' || c == '\t' || c == ' ';
    }
    
    public int decode(final byte[] array, final int n, final int n2, final OutputStream outputStream) throws IOException {
        int n3 = 0;
        int n4;
        for (n4 = n + n2; n4 > n && this.ignore((char)array[n4 - 1]); --n4) {}
        int n5;
        int n6;
        for (n5 = 0, n6 = n4; n6 > n && n5 != 4; --n6) {
            if (!this.ignore((char)array[n6 - 1])) {
                ++n5;
            }
        }
        int i;
        int nextI3;
        for (i = this.nextI(array, n, n6); i < n6; i = this.nextI(array, nextI3, n6)) {
            final byte b = this.decodingTable[array[i++]];
            int nextI = this.nextI(array, i, n6);
            final byte b2 = this.decodingTable[array[nextI++]];
            int nextI2 = this.nextI(array, nextI, n6);
            final byte b3 = this.decodingTable[array[nextI2++]];
            nextI3 = this.nextI(array, nextI2, n6);
            final byte b4 = this.decodingTable[array[nextI3++]];
            if ((b | b2 | b3 | b4) < 0) {
                throw new IOException("invalid characters encountered in base64 data");
            }
            outputStream.write(b << 2 | b2 >> 4);
            outputStream.write(b2 << 4 | b3 >> 2);
            outputStream.write(b3 << 6 | b4);
            n3 += 3;
        }
        final int nextI4 = this.nextI(array, i, n4);
        final int nextI5 = this.nextI(array, nextI4 + 1, n4);
        final int nextI6 = this.nextI(array, nextI5 + 1, n4);
        return n3 + this.decodeLastBlock(outputStream, (char)array[nextI4], (char)array[nextI5], (char)array[nextI6], (char)array[this.nextI(array, nextI6 + 1, n4)]);
    }
    
    private int nextI(final byte[] array, int n, final int n2) {
        while (n < n2 && this.ignore((char)array[n])) {
            ++n;
        }
        return n;
    }
    
    public int decode(final String s, final OutputStream outputStream) throws IOException {
        int n = 0;
        int length;
        for (length = s.length(); length > 0 && this.ignore(s.charAt(length - 1)); --length) {}
        int n2;
        int n3;
        for (n2 = 0, n3 = length; n3 > 0 && n2 != 4; --n3) {
            if (!this.ignore(s.charAt(n3 - 1))) {
                ++n2;
            }
        }
        int i;
        int nextI3;
        for (i = this.nextI(s, 0, n3); i < n3; i = this.nextI(s, nextI3, n3)) {
            final byte b = this.decodingTable[s.charAt(i++)];
            int nextI = this.nextI(s, i, n3);
            final byte b2 = this.decodingTable[s.charAt(nextI++)];
            int nextI2 = this.nextI(s, nextI, n3);
            final byte b3 = this.decodingTable[s.charAt(nextI2++)];
            nextI3 = this.nextI(s, nextI2, n3);
            final byte b4 = this.decodingTable[s.charAt(nextI3++)];
            if ((b | b2 | b3 | b4) < 0) {
                throw new IOException("invalid characters encountered in base64 data");
            }
            outputStream.write(b << 2 | b2 >> 4);
            outputStream.write(b2 << 4 | b3 >> 2);
            outputStream.write(b3 << 6 | b4);
            n += 3;
        }
        final int nextI4 = this.nextI(s, i, length);
        final int nextI5 = this.nextI(s, nextI4 + 1, length);
        final int nextI6 = this.nextI(s, nextI5 + 1, length);
        return n + this.decodeLastBlock(outputStream, s.charAt(nextI4), s.charAt(nextI5), s.charAt(nextI6), s.charAt(this.nextI(s, nextI6 + 1, length)));
    }
    
    private int decodeLastBlock(final OutputStream outputStream, final char c, final char c2, final char c3, final char c4) throws IOException {
        if (c3 == this.padding) {
            if (c4 != this.padding) {
                throw new IOException("invalid characters encountered at end of base64 data");
            }
            final byte b = this.decodingTable[c];
            final byte b2 = this.decodingTable[c2];
            if ((b | b2) < 0) {
                throw new IOException("invalid characters encountered at end of base64 data");
            }
            outputStream.write(b << 2 | b2 >> 4);
            return 1;
        }
        else if (c4 == this.padding) {
            final byte b3 = this.decodingTable[c];
            final byte b4 = this.decodingTable[c2];
            final byte b5 = this.decodingTable[c3];
            if ((b3 | b4 | b5) < 0) {
                throw new IOException("invalid characters encountered at end of base64 data");
            }
            outputStream.write(b3 << 2 | b4 >> 4);
            outputStream.write(b4 << 4 | b5 >> 2);
            return 2;
        }
        else {
            final byte b6 = this.decodingTable[c];
            final byte b7 = this.decodingTable[c2];
            final byte b8 = this.decodingTable[c3];
            final byte b9 = this.decodingTable[c4];
            if ((b6 | b7 | b8 | b9) < 0) {
                throw new IOException("invalid characters encountered at end of base64 data");
            }
            outputStream.write(b6 << 2 | b7 >> 4);
            outputStream.write(b7 << 4 | b8 >> 2);
            outputStream.write(b8 << 6 | b9);
            return 3;
        }
    }
    
    private int nextI(final String s, int n, final int n2) {
        while (n < n2 && this.ignore(s.charAt(n))) {
            ++n;
        }
        return n;
    }
}
