package net.oauth.signature;

import java.math.BigInteger;
import java.io.UnsupportedEncodingException;

class Base64
{
    static final int CHUNK_SIZE = 76;
    static final byte[] CHUNK_SEPARATOR;
    private static final byte[] intToBase64;
    private static final byte PAD = 61;
    private static final byte[] base64ToInt;
    private static final int MASK_6BITS = 63;
    private static final int MASK_8BITS = 255;
    private final int lineLength;
    private final byte[] lineSeparator;
    private final int decodeSize;
    private final int encodeSize;
    private byte[] buf;
    private int pos;
    private int readPos;
    private int currentLinePos;
    private int modulus;
    private boolean eof;
    private int x;
    
    public Base64() {
        this(76, Base64.CHUNK_SEPARATOR);
    }
    
    public Base64(final int lineLength) {
        this(lineLength, Base64.CHUNK_SEPARATOR);
    }
    
    public Base64(final int lineLength, final byte[] lineSeparator) {
        this.lineLength = lineLength;
        System.arraycopy(lineSeparator, 0, this.lineSeparator = new byte[lineSeparator.length], 0, lineSeparator.length);
        if (lineLength > 0) {
            this.encodeSize = 4 + lineSeparator.length;
        }
        else {
            this.encodeSize = 4;
        }
        this.decodeSize = this.encodeSize - 1;
        if (containsBase64Byte(lineSeparator)) {
            String sep;
            try {
                sep = new String(lineSeparator, "UTF-8");
            }
            catch (final UnsupportedEncodingException uee) {
                sep = new String(lineSeparator);
            }
            throw new IllegalArgumentException("lineSeperator must not contain base64 characters: [" + sep + "]");
        }
    }
    
    boolean hasData() {
        return this.buf != null;
    }
    
    int avail() {
        return (this.buf != null) ? (this.pos - this.readPos) : 0;
    }
    
    private void resizeBuf() {
        if (this.buf == null) {
            this.buf = new byte[8192];
            this.pos = 0;
            this.readPos = 0;
        }
        else {
            final byte[] b = new byte[this.buf.length * 2];
            System.arraycopy(this.buf, 0, b, 0, this.buf.length);
            this.buf = b;
        }
    }
    
    int readResults(final byte[] b, final int bPos, final int bAvail) {
        if (this.buf != null) {
            final int len = Math.min(this.avail(), bAvail);
            if (this.buf != b) {
                System.arraycopy(this.buf, this.readPos, b, bPos, len);
                this.readPos += len;
                if (this.readPos >= this.pos) {
                    this.buf = null;
                }
            }
            else {
                this.buf = null;
            }
            return len;
        }
        return this.eof ? -1 : 0;
    }
    
    void setInitialBuffer(final byte[] out, final int outPos, final int outAvail) {
        if (out != null && out.length == outAvail) {
            this.buf = out;
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
            if (this.buf == null || this.buf.length - this.pos < this.encodeSize) {
                this.resizeBuf();
            }
            switch (this.modulus) {
                case 1: {
                    this.buf[this.pos++] = Base64.intToBase64[this.x >> 2 & 0x3F];
                    this.buf[this.pos++] = Base64.intToBase64[this.x << 4 & 0x3F];
                    this.buf[this.pos++] = 61;
                    this.buf[this.pos++] = 61;
                    break;
                }
                case 2: {
                    this.buf[this.pos++] = Base64.intToBase64[this.x >> 10 & 0x3F];
                    this.buf[this.pos++] = Base64.intToBase64[this.x >> 4 & 0x3F];
                    this.buf[this.pos++] = Base64.intToBase64[this.x << 2 & 0x3F];
                    this.buf[this.pos++] = 61;
                    break;
                }
            }
            if (this.lineLength > 0) {
                System.arraycopy(this.lineSeparator, 0, this.buf, this.pos, this.lineSeparator.length);
                this.pos += this.lineSeparator.length;
            }
        }
        else {
            for (int i = 0; i < inAvail; ++i) {
                if (this.buf == null || this.buf.length - this.pos < this.encodeSize) {
                    this.resizeBuf();
                }
                this.modulus = ++this.modulus % 3;
                int b = in[inPos++];
                if (b < 0) {
                    b += 256;
                }
                this.x = (this.x << 8) + b;
                if (0 == this.modulus) {
                    this.buf[this.pos++] = Base64.intToBase64[this.x >> 18 & 0x3F];
                    this.buf[this.pos++] = Base64.intToBase64[this.x >> 12 & 0x3F];
                    this.buf[this.pos++] = Base64.intToBase64[this.x >> 6 & 0x3F];
                    this.buf[this.pos++] = Base64.intToBase64[this.x & 0x3F];
                    this.currentLinePos += 4;
                    if (this.lineLength > 0 && this.lineLength <= this.currentLinePos) {
                        System.arraycopy(this.lineSeparator, 0, this.buf, this.pos, this.lineSeparator.length);
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
            if (this.buf == null || this.buf.length - this.pos < this.decodeSize) {
                this.resizeBuf();
            }
            final byte b = in[inPos++];
            if (b == 61) {
                this.x <<= 6;
                switch (this.modulus) {
                    case 2: {
                        this.x <<= 6;
                        this.buf[this.pos++] = (byte)(this.x >> 16 & 0xFF);
                        break;
                    }
                    case 3: {
                        this.buf[this.pos++] = (byte)(this.x >> 16 & 0xFF);
                        this.buf[this.pos++] = (byte)(this.x >> 8 & 0xFF);
                        break;
                    }
                }
                this.eof = true;
                return;
            }
            if (b >= 0 && b < Base64.base64ToInt.length) {
                final int result = Base64.base64ToInt[b];
                if (result >= 0) {
                    this.modulus = ++this.modulus % 4;
                    this.x = (this.x << 6) + result;
                    if (this.modulus == 0) {
                        this.buf[this.pos++] = (byte)(this.x >> 16 & 0xFF);
                        this.buf[this.pos++] = (byte)(this.x >> 8 & 0xFF);
                        this.buf[this.pos++] = (byte)(this.x & 0xFF);
                    }
                }
            }
        }
    }
    
    public static boolean isBase64(final byte octet) {
        return octet == 61 || (octet >= 0 && octet < Base64.base64ToInt.length && Base64.base64ToInt[octet] != -1);
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
        for (int i = 0; i < arrayOctet.length; ++i) {
            if (isBase64(arrayOctet[i])) {
                return true;
            }
        }
        return false;
    }
    
    public static byte[] encodeBase64(final byte[] binaryData) {
        return encodeBase64(binaryData, false);
    }
    
    public static byte[] encodeBase64Chunked(final byte[] binaryData) {
        return encodeBase64(binaryData, true);
    }
    
    public byte[] decode(final byte[] pArray) {
        return decodeBase64(pArray);
    }
    
    public static byte[] encodeBase64(final byte[] binaryData, final boolean isChunked) {
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }
        final Base64 b64 = isChunked ? new Base64() : new Base64(0);
        long len = binaryData.length * 4 / 3;
        final long mod = len % 4L;
        if (mod != 0L) {
            len += 4L - mod;
        }
        if (isChunked) {
            len += (1L + len / 76L) * Base64.CHUNK_SEPARATOR.length;
        }
        if (len > 2147483647L) {
            throw new IllegalArgumentException("Input array too big, output array would be bigger than Integer.MAX_VALUE=2147483647");
        }
        final byte[] buf = new byte[(int)len];
        b64.setInitialBuffer(buf, 0, buf.length);
        b64.encode(binaryData, 0, binaryData.length);
        b64.encode(binaryData, 0, -1);
        if (b64.buf != buf) {
            b64.readResults(buf, 0, buf.length);
        }
        return buf;
    }
    
    public static byte[] decodeBase64(final byte[] base64Data) {
        if (base64Data == null || base64Data.length == 0) {
            return base64Data;
        }
        final Base64 b64 = new Base64();
        final long len = base64Data.length * 3 / 4;
        final byte[] buf = new byte[(int)len];
        b64.setInitialBuffer(buf, 0, buf.length);
        b64.decode(base64Data, 0, base64Data.length);
        b64.decode(base64Data, 0, -1);
        final byte[] result = new byte[b64.pos];
        b64.readResults(result, 0, result.length);
        return result;
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
    
    static byte[] discardNonBase64(final byte[] data) {
        final byte[] groomedData = new byte[data.length];
        int bytesCopied = 0;
        for (int i = 0; i < data.length; ++i) {
            if (isBase64(data[i])) {
                groomedData[bytesCopied++] = data[i];
            }
        }
        final byte[] packedData = new byte[bytesCopied];
        System.arraycopy(groomedData, 0, packedData, 0, bytesCopied);
        return packedData;
    }
    
    public byte[] encode(final byte[] pArray) {
        return encodeBase64(pArray, false);
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
    
    static {
        CHUNK_SEPARATOR = new byte[] { 13, 10 };
        intToBase64 = new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
        base64ToInt = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
    }
}
