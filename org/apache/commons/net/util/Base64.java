package org.apache.commons.net.util;

import java.math.BigInteger;
import java.io.UnsupportedEncodingException;

public class Base64
{
    private static final int DEFAULT_BUFFER_RESIZE_FACTOR = 2;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    static final int CHUNK_SIZE = 76;
    private static final byte[] CHUNK_SEPARATOR;
    private static final byte[] EMPTY_BYTE_ARRAY;
    private static final byte[] STANDARD_ENCODE_TABLE;
    private static final byte[] URL_SAFE_ENCODE_TABLE;
    private static final byte PAD = 61;
    private static final byte[] DECODE_TABLE;
    private static final int MASK_6BITS = 63;
    private static final int MASK_8BITS = 255;
    private final byte[] encodeTable;
    private final int lineLength;
    private final byte[] lineSeparator;
    private final int decodeSize;
    private final int encodeSize;
    private byte[] buffer;
    private int pos;
    private int readPos;
    private int currentLinePos;
    private int modulus;
    private boolean eof;
    private int x;
    
    public Base64() {
        this(false);
    }
    
    public Base64(final boolean urlSafe) {
        this(76, Base64.CHUNK_SEPARATOR, urlSafe);
    }
    
    public Base64(final int lineLength) {
        this(lineLength, Base64.CHUNK_SEPARATOR);
    }
    
    public Base64(final int lineLength, final byte[] lineSeparator) {
        this(lineLength, lineSeparator, false);
    }
    
    public Base64(int lineLength, byte[] lineSeparator, final boolean urlSafe) {
        if (lineSeparator == null) {
            lineLength = 0;
            lineSeparator = Base64.EMPTY_BYTE_ARRAY;
        }
        this.lineLength = ((lineLength > 0) ? (lineLength / 4 * 4) : 0);
        System.arraycopy(lineSeparator, 0, this.lineSeparator = new byte[lineSeparator.length], 0, lineSeparator.length);
        if (lineLength > 0) {
            this.encodeSize = 4 + lineSeparator.length;
        }
        else {
            this.encodeSize = 4;
        }
        this.decodeSize = this.encodeSize - 1;
        if (containsBase64Byte(lineSeparator)) {
            final String sep = newStringUtf8(lineSeparator);
            throw new IllegalArgumentException("lineSeperator must not contain base64 characters: [" + sep + "]");
        }
        this.encodeTable = (urlSafe ? Base64.URL_SAFE_ENCODE_TABLE : Base64.STANDARD_ENCODE_TABLE);
    }
    
    public boolean isUrlSafe() {
        return this.encodeTable == Base64.URL_SAFE_ENCODE_TABLE;
    }
    
    boolean hasData() {
        return this.buffer != null;
    }
    
    int avail() {
        return (this.buffer != null) ? (this.pos - this.readPos) : 0;
    }
    
    private void resizeBuffer() {
        if (this.buffer == null) {
            this.buffer = new byte[8192];
            this.pos = 0;
            this.readPos = 0;
        }
        else {
            final byte[] b = new byte[this.buffer.length * 2];
            System.arraycopy(this.buffer, 0, b, 0, this.buffer.length);
            this.buffer = b;
        }
    }
    
    int readResults(final byte[] b, final int bPos, final int bAvail) {
        if (this.buffer != null) {
            final int len = Math.min(this.avail(), bAvail);
            if (this.buffer != b) {
                System.arraycopy(this.buffer, this.readPos, b, bPos, len);
                this.readPos += len;
                if (this.readPos >= this.pos) {
                    this.buffer = null;
                }
            }
            else {
                this.buffer = null;
            }
            return len;
        }
        return this.eof ? -1 : 0;
    }
    
    void setInitialBuffer(final byte[] out, final int outPos, final int outAvail) {
        if (out != null && out.length == outAvail) {
            this.buffer = out;
            this.pos = outPos;
            this.readPos = outPos;
        }
    }
    
    void encode(final byte[] in, int inPos, final int inAvail) {
        if (this.eof) {
            return;
        }
        if (inAvail < 0) {
            this.eof = true;
            if (this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
                this.resizeBuffer();
            }
            switch (this.modulus) {
                case 1: {
                    this.buffer[this.pos++] = this.encodeTable[this.x >> 2 & 0x3F];
                    this.buffer[this.pos++] = this.encodeTable[this.x << 4 & 0x3F];
                    if (this.encodeTable == Base64.STANDARD_ENCODE_TABLE) {
                        this.buffer[this.pos++] = 61;
                        this.buffer[this.pos++] = 61;
                        break;
                    }
                    break;
                }
                case 2: {
                    this.buffer[this.pos++] = this.encodeTable[this.x >> 10 & 0x3F];
                    this.buffer[this.pos++] = this.encodeTable[this.x >> 4 & 0x3F];
                    this.buffer[this.pos++] = this.encodeTable[this.x << 2 & 0x3F];
                    if (this.encodeTable == Base64.STANDARD_ENCODE_TABLE) {
                        this.buffer[this.pos++] = 61;
                        break;
                    }
                    break;
                }
            }
            if (this.lineLength > 0 && this.pos > 0) {
                System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
                this.pos += this.lineSeparator.length;
            }
        }
        else {
            for (int i = 0; i < inAvail; ++i) {
                if (this.buffer == null || this.buffer.length - this.pos < this.encodeSize) {
                    this.resizeBuffer();
                }
                this.modulus = ++this.modulus % 3;
                int b = in[inPos++];
                if (b < 0) {
                    b += 256;
                }
                this.x = (this.x << 8) + b;
                if (0 == this.modulus) {
                    this.buffer[this.pos++] = this.encodeTable[this.x >> 18 & 0x3F];
                    this.buffer[this.pos++] = this.encodeTable[this.x >> 12 & 0x3F];
                    this.buffer[this.pos++] = this.encodeTable[this.x >> 6 & 0x3F];
                    this.buffer[this.pos++] = this.encodeTable[this.x & 0x3F];
                    this.currentLinePos += 4;
                    if (this.lineLength > 0 && this.lineLength <= this.currentLinePos) {
                        System.arraycopy(this.lineSeparator, 0, this.buffer, this.pos, this.lineSeparator.length);
                        this.pos += this.lineSeparator.length;
                        this.currentLinePos = 0;
                    }
                }
            }
        }
    }
    
    void decode(final byte[] in, int inPos, final int inAvail) {
        if (this.eof) {
            return;
        }
        if (inAvail < 0) {
            this.eof = true;
        }
        for (int i = 0; i < inAvail; ++i) {
            if (this.buffer == null || this.buffer.length - this.pos < this.decodeSize) {
                this.resizeBuffer();
            }
            final byte b = in[inPos++];
            if (b == 61) {
                this.eof = true;
                break;
            }
            if (b >= 0 && b < Base64.DECODE_TABLE.length) {
                final int result = Base64.DECODE_TABLE[b];
                if (result >= 0) {
                    this.modulus = ++this.modulus % 4;
                    this.x = (this.x << 6) + result;
                    if (this.modulus == 0) {
                        this.buffer[this.pos++] = (byte)(this.x >> 16 & 0xFF);
                        this.buffer[this.pos++] = (byte)(this.x >> 8 & 0xFF);
                        this.buffer[this.pos++] = (byte)(this.x & 0xFF);
                    }
                }
            }
        }
        if (this.eof && this.modulus != 0) {
            this.x <<= 6;
            switch (this.modulus) {
                case 2: {
                    this.x <<= 6;
                    this.buffer[this.pos++] = (byte)(this.x >> 16 & 0xFF);
                    break;
                }
                case 3: {
                    this.buffer[this.pos++] = (byte)(this.x >> 16 & 0xFF);
                    this.buffer[this.pos++] = (byte)(this.x >> 8 & 0xFF);
                    break;
                }
            }
        }
    }
    
    public static boolean isBase64(final byte octet) {
        return octet == 61 || (octet >= 0 && octet < Base64.DECODE_TABLE.length && Base64.DECODE_TABLE[octet] != -1);
    }
    
    public static boolean isArrayByteBase64(final byte[] arrayOctet) {
        for (int i = 0; i < arrayOctet.length; ++i) {
            if (!isBase64(arrayOctet[i]) && !isWhiteSpace(arrayOctet[i])) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean containsBase64Byte(final byte[] arrayOctet) {
        for (final byte element : arrayOctet) {
            if (isBase64(element)) {
                return true;
            }
        }
        return false;
    }
    
    public static byte[] encodeBase64(final byte[] binaryData) {
        return encodeBase64(binaryData, false);
    }
    
    public static String encodeBase64String(final byte[] binaryData) {
        return newStringUtf8(encodeBase64(binaryData, true));
    }
    
    public static String encodeBase64StringUnChunked(final byte[] binaryData) {
        return newStringUtf8(encodeBase64(binaryData, false));
    }
    
    public static String encodeBase64String(final byte[] binaryData, final boolean useChunking) {
        return newStringUtf8(encodeBase64(binaryData, useChunking));
    }
    
    public static byte[] encodeBase64URLSafe(final byte[] binaryData) {
        return encodeBase64(binaryData, false, true);
    }
    
    public static String encodeBase64URLSafeString(final byte[] binaryData) {
        return newStringUtf8(encodeBase64(binaryData, false, true));
    }
    
    public static byte[] encodeBase64Chunked(final byte[] binaryData) {
        return encodeBase64(binaryData, true);
    }
    
    public byte[] decode(final String pArray) {
        return this.decode(this.getBytesUtf8(pArray));
    }
    
    private byte[] getBytesUtf8(final String pArray) {
        try {
            return pArray.getBytes("UTF8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public byte[] decode(final byte[] pArray) {
        this.reset();
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        final long len = pArray.length * 3 / 4;
        final byte[] buf = new byte[(int)len];
        this.setInitialBuffer(buf, 0, buf.length);
        this.decode(pArray, 0, pArray.length);
        this.decode(pArray, 0, -1);
        final byte[] result = new byte[this.pos];
        this.readResults(result, 0, result.length);
        return result;
    }
    
    public static byte[] encodeBase64(final byte[] binaryData, final boolean isChunked) {
        return encodeBase64(binaryData, isChunked, false);
    }
    
    public static byte[] encodeBase64(final byte[] binaryData, final boolean isChunked, final boolean urlSafe) {
        return encodeBase64(binaryData, isChunked, urlSafe, Integer.MAX_VALUE);
    }
    
    public static byte[] encodeBase64(final byte[] binaryData, final boolean isChunked, final boolean urlSafe, final int maxResultSize) {
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }
        final long len = getEncodeLength(binaryData, isChunked ? 76 : 0, isChunked ? Base64.CHUNK_SEPARATOR : Base64.EMPTY_BYTE_ARRAY);
        if (len > maxResultSize) {
            throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maxium size of " + maxResultSize);
        }
        final Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, Base64.CHUNK_SEPARATOR, urlSafe);
        return b64.encode(binaryData);
    }
    
    public static byte[] decodeBase64(final String base64String) {
        return new Base64().decode(base64String);
    }
    
    public static byte[] decodeBase64(final byte[] base64Data) {
        return new Base64().decode(base64Data);
    }
    
    private static boolean isWhiteSpace(final byte byteToCheck) {
        switch (byteToCheck) {
            case 9:
            case 10:
            case 13:
            case 32: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public String encodeToString(final byte[] pArray) {
        return newStringUtf8(this.encode(pArray));
    }
    
    private static String newStringUtf8(final byte[] encode) {
        String str = null;
        try {
            str = new String(encode, "UTF8");
        }
        catch (final UnsupportedEncodingException ue) {
            throw new RuntimeException(ue);
        }
        return str;
    }
    
    public byte[] encode(final byte[] pArray) {
        this.reset();
        if (pArray == null || pArray.length == 0) {
            return pArray;
        }
        final long len = getEncodeLength(pArray, this.lineLength, this.lineSeparator);
        byte[] buf = new byte[(int)len];
        this.setInitialBuffer(buf, 0, buf.length);
        this.encode(pArray, 0, pArray.length);
        this.encode(pArray, 0, -1);
        if (this.buffer != buf) {
            this.readResults(buf, 0, buf.length);
        }
        if (this.isUrlSafe() && this.pos < buf.length) {
            final byte[] smallerBuf = new byte[this.pos];
            System.arraycopy(buf, 0, smallerBuf, 0, this.pos);
            buf = smallerBuf;
        }
        return buf;
    }
    
    private static long getEncodeLength(final byte[] pArray, int chunkSize, final byte[] chunkSeparator) {
        chunkSize = chunkSize / 4 * 4;
        long len = pArray.length * 4 / 3;
        final long mod = len % 4L;
        if (mod != 0L) {
            len += 4L - mod;
        }
        if (chunkSize > 0) {
            final boolean lenChunksPerfectly = len % chunkSize == 0L;
            len += len / chunkSize * chunkSeparator.length;
            if (!lenChunksPerfectly) {
                len += chunkSeparator.length;
            }
        }
        return len;
    }
    
    public static BigInteger decodeInteger(final byte[] pArray) {
        return new BigInteger(1, decodeBase64(pArray));
    }
    
    public static byte[] encodeInteger(final BigInteger bigInt) {
        if (bigInt == null) {
            throw new NullPointerException("encodeInteger called with null parameter");
        }
        return encodeBase64(toIntegerBytes(bigInt), false);
    }
    
    static byte[] toIntegerBytes(final BigInteger bigInt) {
        int bitlen = bigInt.bitLength();
        bitlen = bitlen + 7 >> 3 << 3;
        final byte[] bigBytes = bigInt.toByteArray();
        if (bigInt.bitLength() % 8 != 0 && bigInt.bitLength() / 8 + 1 == bitlen / 8) {
            return bigBytes;
        }
        int startSrc = 0;
        int len = bigBytes.length;
        if (bigInt.bitLength() % 8 == 0) {
            startSrc = 1;
            --len;
        }
        final int startDst = bitlen / 8 - len;
        final byte[] resizedBytes = new byte[bitlen / 8];
        System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
        return resizedBytes;
    }
    
    private void reset() {
        this.buffer = null;
        this.pos = 0;
        this.readPos = 0;
        this.currentLinePos = 0;
        this.modulus = 0;
        this.eof = false;
    }
    
    int getLineLength() {
        return this.lineLength;
    }
    
    byte[] getLineSeparator() {
        return this.lineSeparator.clone();
    }
    
    static {
        CHUNK_SEPARATOR = new byte[] { 13, 10 };
        EMPTY_BYTE_ARRAY = new byte[0];
        STANDARD_ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        URL_SAFE_ENCODE_TABLE = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
        DECODE_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
    }
}
