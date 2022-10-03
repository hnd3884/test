package com.unboundid.util;

import java.text.ParseException;
import java.io.IOException;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Base64
{
    private static final char[] BASE64_ALPHABET;
    private static final char[] BASE64URL_ALPHABET;
    
    private Base64() {
    }
    
    public static String encode(final String data) {
        Validator.ensureNotNull(data);
        return encode(StaticUtils.getBytes(data));
    }
    
    public static String encode(final byte[] data) {
        Validator.ensureNotNull(data);
        final StringBuilder buffer = new StringBuilder(4 * data.length / 3 + 1);
        encode(Base64.BASE64_ALPHABET, data, 0, data.length, buffer, "=");
        return buffer.toString();
    }
    
    public static void encode(final String data, final StringBuilder buffer) {
        Validator.ensureNotNull(data);
        encode(StaticUtils.getBytes(data), buffer);
    }
    
    public static void encode(final String data, final ByteStringBuffer buffer) {
        Validator.ensureNotNull(data);
        encode(StaticUtils.getBytes(data), buffer);
    }
    
    public static void encode(final byte[] data, final StringBuilder buffer) {
        encode(Base64.BASE64_ALPHABET, data, 0, data.length, buffer, "=");
    }
    
    public static void encode(final byte[] data, final int off, final int length, final StringBuilder buffer) {
        encode(Base64.BASE64_ALPHABET, data, off, length, buffer, "=");
    }
    
    public static void encode(final byte[] data, final ByteStringBuffer buffer) {
        encode(Base64.BASE64_ALPHABET, data, 0, data.length, buffer, "=");
    }
    
    public static void encode(final byte[] data, final int off, final int length, final ByteStringBuffer buffer) {
        encode(Base64.BASE64_ALPHABET, data, off, length, buffer, "=");
    }
    
    public static String urlEncode(final String data, final boolean pad) {
        return urlEncode(StaticUtils.getBytes(data), pad);
    }
    
    public static void urlEncode(final String data, final StringBuilder buffer, final boolean pad) {
        final byte[] dataBytes = StaticUtils.getBytes(data);
        encode(Base64.BASE64_ALPHABET, dataBytes, 0, dataBytes.length, buffer, pad ? "%3d" : null);
    }
    
    public static void urlEncode(final String data, final ByteStringBuffer buffer, final boolean pad) {
        final byte[] dataBytes = StaticUtils.getBytes(data);
        encode(Base64.BASE64_ALPHABET, dataBytes, 0, dataBytes.length, buffer, pad ? "%3d" : null);
    }
    
    public static String urlEncode(final byte[] data, final boolean pad) {
        final StringBuilder buffer = new StringBuilder(4 * data.length / 3 + 6);
        encode(Base64.BASE64URL_ALPHABET, data, 0, data.length, buffer, pad ? "%3d" : null);
        return buffer.toString();
    }
    
    public static void urlEncode(final byte[] data, final int off, final int length, final StringBuilder buffer, final boolean pad) {
        encode(Base64.BASE64URL_ALPHABET, data, off, length, buffer, pad ? "%3d" : null);
    }
    
    public static void urlEncode(final byte[] data, final int off, final int length, final ByteStringBuffer buffer, final boolean pad) {
        encode(Base64.BASE64URL_ALPHABET, data, off, length, buffer, pad ? "%3d" : null);
    }
    
    private static void encode(final char[] alphabet, final byte[] data, final int off, final int length, final Appendable buffer, final String padStr) {
        Validator.ensureNotNull(data);
        Validator.ensureTrue(data.length >= off);
        Validator.ensureTrue(data.length >= off + length);
        if (length == 0) {
            return;
        }
        try {
            int pos = off;
            for (int i = 0; i < length / 3; ++i) {
                final int intValue = (data[pos++] & 0xFF) << 16 | (data[pos++] & 0xFF) << 8 | (data[pos++] & 0xFF);
                buffer.append(alphabet[intValue >> 18 & 0x3F]);
                buffer.append(alphabet[intValue >> 12 & 0x3F]);
                buffer.append(alphabet[intValue >> 6 & 0x3F]);
                buffer.append(alphabet[intValue & 0x3F]);
            }
            switch (off + length - pos) {
                case 1: {
                    final int intValue2 = (data[pos] & 0xFF) << 16;
                    buffer.append(alphabet[intValue2 >> 18 & 0x3F]);
                    buffer.append(alphabet[intValue2 >> 12 & 0x3F]);
                    if (padStr != null) {
                        buffer.append(padStr);
                        buffer.append(padStr);
                    }
                    return;
                }
                case 2: {
                    final int intValue2 = (data[pos++] & 0xFF) << 16 | (data[pos] & 0xFF) << 8;
                    buffer.append(alphabet[intValue2 >> 18 & 0x3F]);
                    buffer.append(alphabet[intValue2 >> 12 & 0x3F]);
                    buffer.append(alphabet[intValue2 >> 6 & 0x3F]);
                    if (padStr != null) {
                        buffer.append(padStr);
                    }
                }
            }
        }
        catch (final IOException ioe) {
            Debug.debugException(ioe);
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
    }
    
    public static byte[] decode(final String data) throws ParseException {
        Validator.ensureNotNull(data);
        final int length = data.length();
        if (length == 0) {
            return StaticUtils.NO_BYTES;
        }
        if (length % 4 != 0) {
            throw new ParseException(UtilityMessages.ERR_BASE64_DECODE_INVALID_LENGTH.get(), length);
        }
        int numBytes = 3 * (length / 4);
        if (data.charAt(length - 2) == '=') {
            numBytes -= 2;
        }
        else if (data.charAt(length - 1) == '=') {
            --numBytes;
        }
        final byte[] b = new byte[numBytes];
        int stringPos = 0;
        int arrayPos = 0;
        while (stringPos < length) {
            int intValue = 0;
            for (int i = 0; i < 4; ++i) {
                intValue <<= 6;
                switch (data.charAt(stringPos++)) {
                    case 'A': {
                        intValue |= 0x0;
                        break;
                    }
                    case 'B': {
                        intValue |= 0x1;
                        break;
                    }
                    case 'C': {
                        intValue |= 0x2;
                        break;
                    }
                    case 'D': {
                        intValue |= 0x3;
                        break;
                    }
                    case 'E': {
                        intValue |= 0x4;
                        break;
                    }
                    case 'F': {
                        intValue |= 0x5;
                        break;
                    }
                    case 'G': {
                        intValue |= 0x6;
                        break;
                    }
                    case 'H': {
                        intValue |= 0x7;
                        break;
                    }
                    case 'I': {
                        intValue |= 0x8;
                        break;
                    }
                    case 'J': {
                        intValue |= 0x9;
                        break;
                    }
                    case 'K': {
                        intValue |= 0xA;
                        break;
                    }
                    case 'L': {
                        intValue |= 0xB;
                        break;
                    }
                    case 'M': {
                        intValue |= 0xC;
                        break;
                    }
                    case 'N': {
                        intValue |= 0xD;
                        break;
                    }
                    case 'O': {
                        intValue |= 0xE;
                        break;
                    }
                    case 'P': {
                        intValue |= 0xF;
                        break;
                    }
                    case 'Q': {
                        intValue |= 0x10;
                        break;
                    }
                    case 'R': {
                        intValue |= 0x11;
                        break;
                    }
                    case 'S': {
                        intValue |= 0x12;
                        break;
                    }
                    case 'T': {
                        intValue |= 0x13;
                        break;
                    }
                    case 'U': {
                        intValue |= 0x14;
                        break;
                    }
                    case 'V': {
                        intValue |= 0x15;
                        break;
                    }
                    case 'W': {
                        intValue |= 0x16;
                        break;
                    }
                    case 'X': {
                        intValue |= 0x17;
                        break;
                    }
                    case 'Y': {
                        intValue |= 0x18;
                        break;
                    }
                    case 'Z': {
                        intValue |= 0x19;
                        break;
                    }
                    case 'a': {
                        intValue |= 0x1A;
                        break;
                    }
                    case 'b': {
                        intValue |= 0x1B;
                        break;
                    }
                    case 'c': {
                        intValue |= 0x1C;
                        break;
                    }
                    case 'd': {
                        intValue |= 0x1D;
                        break;
                    }
                    case 'e': {
                        intValue |= 0x1E;
                        break;
                    }
                    case 'f': {
                        intValue |= 0x1F;
                        break;
                    }
                    case 'g': {
                        intValue |= 0x20;
                        break;
                    }
                    case 'h': {
                        intValue |= 0x21;
                        break;
                    }
                    case 'i': {
                        intValue |= 0x22;
                        break;
                    }
                    case 'j': {
                        intValue |= 0x23;
                        break;
                    }
                    case 'k': {
                        intValue |= 0x24;
                        break;
                    }
                    case 'l': {
                        intValue |= 0x25;
                        break;
                    }
                    case 'm': {
                        intValue |= 0x26;
                        break;
                    }
                    case 'n': {
                        intValue |= 0x27;
                        break;
                    }
                    case 'o': {
                        intValue |= 0x28;
                        break;
                    }
                    case 'p': {
                        intValue |= 0x29;
                        break;
                    }
                    case 'q': {
                        intValue |= 0x2A;
                        break;
                    }
                    case 'r': {
                        intValue |= 0x2B;
                        break;
                    }
                    case 's': {
                        intValue |= 0x2C;
                        break;
                    }
                    case 't': {
                        intValue |= 0x2D;
                        break;
                    }
                    case 'u': {
                        intValue |= 0x2E;
                        break;
                    }
                    case 'v': {
                        intValue |= 0x2F;
                        break;
                    }
                    case 'w': {
                        intValue |= 0x30;
                        break;
                    }
                    case 'x': {
                        intValue |= 0x31;
                        break;
                    }
                    case 'y': {
                        intValue |= 0x32;
                        break;
                    }
                    case 'z': {
                        intValue |= 0x33;
                        break;
                    }
                    case '0': {
                        intValue |= 0x34;
                        break;
                    }
                    case '1': {
                        intValue |= 0x35;
                        break;
                    }
                    case '2': {
                        intValue |= 0x36;
                        break;
                    }
                    case '3': {
                        intValue |= 0x37;
                        break;
                    }
                    case '4': {
                        intValue |= 0x38;
                        break;
                    }
                    case '5': {
                        intValue |= 0x39;
                        break;
                    }
                    case '6': {
                        intValue |= 0x3A;
                        break;
                    }
                    case '7': {
                        intValue |= 0x3B;
                        break;
                    }
                    case '8': {
                        intValue |= 0x3C;
                        break;
                    }
                    case '9': {
                        intValue |= 0x3D;
                        break;
                    }
                    case '+': {
                        intValue |= 0x3E;
                        break;
                    }
                    case '/': {
                        intValue |= 0x3F;
                        break;
                    }
                    case '=': {
                        switch (length - stringPos) {
                            case 0: {
                                intValue >>= 8;
                                b[arrayPos++] = (byte)(intValue >> 8 & 0xFF);
                                b[arrayPos] = (byte)(intValue & 0xFF);
                                return b;
                            }
                            case 1: {
                                intValue >>= 10;
                                b[arrayPos] = (byte)(intValue & 0xFF);
                                return b;
                            }
                            default: {
                                throw new ParseException(UtilityMessages.ERR_BASE64_DECODE_UNEXPECTED_EQUAL.get(stringPos - 1), stringPos - 1);
                            }
                        }
                        break;
                    }
                    default: {
                        throw new ParseException(UtilityMessages.ERR_BASE64_DECODE_UNEXPECTED_CHAR.get(data.charAt(stringPos - 1)), stringPos - 1);
                    }
                }
            }
            b[arrayPos++] = (byte)(intValue >> 16 & 0xFF);
            b[arrayPos++] = (byte)(intValue >> 8 & 0xFF);
            b[arrayPos++] = (byte)(intValue & 0xFF);
        }
        return b;
    }
    
    public static String decodeToString(final String data) throws ParseException {
        Validator.ensureNotNull(data);
        final byte[] decodedBytes = decode(data);
        return StaticUtils.toUTF8String(decodedBytes);
    }
    
    public static byte[] urlDecode(final String data) throws ParseException {
        Validator.ensureNotNull(data);
        final int length = data.length();
        if (length == 0) {
            return StaticUtils.NO_BYTES;
        }
        int stringPos = 0;
        final ByteStringBuffer buffer = new ByteStringBuffer(length);
    Label_1261:
        while (stringPos < length) {
            int intValue = 0;
            for (int i = 0; i < 4; ++i) {
                char c;
                if (stringPos >= length) {
                    c = '=';
                    ++stringPos;
                }
                else {
                    c = data.charAt(stringPos++);
                }
                intValue <<= 6;
                switch (c) {
                    case 'A': {
                        intValue |= 0x0;
                        break;
                    }
                    case 'B': {
                        intValue |= 0x1;
                        break;
                    }
                    case 'C': {
                        intValue |= 0x2;
                        break;
                    }
                    case 'D': {
                        intValue |= 0x3;
                        break;
                    }
                    case 'E': {
                        intValue |= 0x4;
                        break;
                    }
                    case 'F': {
                        intValue |= 0x5;
                        break;
                    }
                    case 'G': {
                        intValue |= 0x6;
                        break;
                    }
                    case 'H': {
                        intValue |= 0x7;
                        break;
                    }
                    case 'I': {
                        intValue |= 0x8;
                        break;
                    }
                    case 'J': {
                        intValue |= 0x9;
                        break;
                    }
                    case 'K': {
                        intValue |= 0xA;
                        break;
                    }
                    case 'L': {
                        intValue |= 0xB;
                        break;
                    }
                    case 'M': {
                        intValue |= 0xC;
                        break;
                    }
                    case 'N': {
                        intValue |= 0xD;
                        break;
                    }
                    case 'O': {
                        intValue |= 0xE;
                        break;
                    }
                    case 'P': {
                        intValue |= 0xF;
                        break;
                    }
                    case 'Q': {
                        intValue |= 0x10;
                        break;
                    }
                    case 'R': {
                        intValue |= 0x11;
                        break;
                    }
                    case 'S': {
                        intValue |= 0x12;
                        break;
                    }
                    case 'T': {
                        intValue |= 0x13;
                        break;
                    }
                    case 'U': {
                        intValue |= 0x14;
                        break;
                    }
                    case 'V': {
                        intValue |= 0x15;
                        break;
                    }
                    case 'W': {
                        intValue |= 0x16;
                        break;
                    }
                    case 'X': {
                        intValue |= 0x17;
                        break;
                    }
                    case 'Y': {
                        intValue |= 0x18;
                        break;
                    }
                    case 'Z': {
                        intValue |= 0x19;
                        break;
                    }
                    case 'a': {
                        intValue |= 0x1A;
                        break;
                    }
                    case 'b': {
                        intValue |= 0x1B;
                        break;
                    }
                    case 'c': {
                        intValue |= 0x1C;
                        break;
                    }
                    case 'd': {
                        intValue |= 0x1D;
                        break;
                    }
                    case 'e': {
                        intValue |= 0x1E;
                        break;
                    }
                    case 'f': {
                        intValue |= 0x1F;
                        break;
                    }
                    case 'g': {
                        intValue |= 0x20;
                        break;
                    }
                    case 'h': {
                        intValue |= 0x21;
                        break;
                    }
                    case 'i': {
                        intValue |= 0x22;
                        break;
                    }
                    case 'j': {
                        intValue |= 0x23;
                        break;
                    }
                    case 'k': {
                        intValue |= 0x24;
                        break;
                    }
                    case 'l': {
                        intValue |= 0x25;
                        break;
                    }
                    case 'm': {
                        intValue |= 0x26;
                        break;
                    }
                    case 'n': {
                        intValue |= 0x27;
                        break;
                    }
                    case 'o': {
                        intValue |= 0x28;
                        break;
                    }
                    case 'p': {
                        intValue |= 0x29;
                        break;
                    }
                    case 'q': {
                        intValue |= 0x2A;
                        break;
                    }
                    case 'r': {
                        intValue |= 0x2B;
                        break;
                    }
                    case 's': {
                        intValue |= 0x2C;
                        break;
                    }
                    case 't': {
                        intValue |= 0x2D;
                        break;
                    }
                    case 'u': {
                        intValue |= 0x2E;
                        break;
                    }
                    case 'v': {
                        intValue |= 0x2F;
                        break;
                    }
                    case 'w': {
                        intValue |= 0x30;
                        break;
                    }
                    case 'x': {
                        intValue |= 0x31;
                        break;
                    }
                    case 'y': {
                        intValue |= 0x32;
                        break;
                    }
                    case 'z': {
                        intValue |= 0x33;
                        break;
                    }
                    case '0': {
                        intValue |= 0x34;
                        break;
                    }
                    case '1': {
                        intValue |= 0x35;
                        break;
                    }
                    case '2': {
                        intValue |= 0x36;
                        break;
                    }
                    case '3': {
                        intValue |= 0x37;
                        break;
                    }
                    case '4': {
                        intValue |= 0x38;
                        break;
                    }
                    case '5': {
                        intValue |= 0x39;
                        break;
                    }
                    case '6': {
                        intValue |= 0x3A;
                        break;
                    }
                    case '7': {
                        intValue |= 0x3B;
                        break;
                    }
                    case '8': {
                        intValue |= 0x3C;
                        break;
                    }
                    case '9': {
                        intValue |= 0x3D;
                        break;
                    }
                    case '-': {
                        intValue |= 0x3E;
                        break;
                    }
                    case '_': {
                        intValue |= 0x3F;
                        break;
                    }
                    case '%':
                    case '=': {
                        switch ((stringPos - 1) % 4) {
                            case 2: {
                                intValue >>= 10;
                                buffer.append((byte)(intValue & 0xFF));
                                break Label_1261;
                            }
                            case 3: {
                                intValue >>= 8;
                                buffer.append((byte)(intValue >> 8 & 0xFF));
                                buffer.append((byte)(intValue & 0xFF));
                                break Label_1261;
                            }
                            default: {
                                throw new ParseException(UtilityMessages.ERR_BASE64_URLDECODE_INVALID_LENGTH.get(), stringPos - 1);
                            }
                        }
                        break;
                    }
                    default: {
                        throw new ParseException(UtilityMessages.ERR_BASE64_DECODE_UNEXPECTED_CHAR.get(data.charAt(stringPos - 1)), stringPos - 1);
                    }
                }
            }
            buffer.append((byte)(intValue >> 16 & 0xFF));
            buffer.append((byte)(intValue >> 8 & 0xFF));
            buffer.append((byte)(intValue & 0xFF));
        }
        return buffer.toByteArray();
    }
    
    public static String urlDecodeToString(final String data) throws ParseException {
        Validator.ensureNotNull(data);
        final byte[] decodedBytes = urlDecode(data);
        return StaticUtils.toUTF8String(decodedBytes);
    }
    
    static {
        BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        BASE64URL_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
    }
}
