package com.unboundid.util;

import java.text.ParseException;
import java.io.IOException;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Base32
{
    private static final char[] BASE32_ALPHABET;
    
    private Base32() {
    }
    
    public static String encode(final String data) {
        Validator.ensureNotNull(data);
        return encode(StaticUtils.getBytes(data));
    }
    
    public static String encode(final byte[] data) {
        Validator.ensureNotNull(data);
        final StringBuilder buffer = new StringBuilder(4 * data.length / 3 + 1);
        encodeInternal(data, 0, data.length, buffer);
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
        encodeInternal(data, 0, data.length, buffer);
    }
    
    public static void encode(final byte[] data, final int off, final int length, final StringBuilder buffer) {
        encodeInternal(data, off, length, buffer);
    }
    
    public static void encode(final byte[] data, final ByteStringBuffer buffer) {
        encodeInternal(data, 0, data.length, buffer);
    }
    
    public static void encode(final byte[] data, final int off, final int length, final ByteStringBuffer buffer) {
        encodeInternal(data, off, length, buffer);
    }
    
    private static void encodeInternal(final byte[] data, final int off, final int length, final Appendable buffer) {
        Validator.ensureNotNull(data);
        Validator.ensureTrue(data.length >= off);
        Validator.ensureTrue(data.length >= off + length);
        if (length == 0) {
            return;
        }
        try {
            int pos = off;
            for (int i = 0; i < length / 5; ++i) {
                final long longValue = ((long)data[pos++] & 0xFFL) << 32 | ((long)data[pos++] & 0xFFL) << 24 | ((long)data[pos++] & 0xFFL) << 16 | ((long)data[pos++] & 0xFFL) << 8 | ((long)data[pos++] & 0xFFL);
                buffer.append(Base32.BASE32_ALPHABET[(int)(longValue >> 35 & 0x1FL)]);
                buffer.append(Base32.BASE32_ALPHABET[(int)(longValue >> 30 & 0x1FL)]);
                buffer.append(Base32.BASE32_ALPHABET[(int)(longValue >> 25 & 0x1FL)]);
                buffer.append(Base32.BASE32_ALPHABET[(int)(longValue >> 20 & 0x1FL)]);
                buffer.append(Base32.BASE32_ALPHABET[(int)(longValue >> 15 & 0x1FL)]);
                buffer.append(Base32.BASE32_ALPHABET[(int)(longValue >> 10 & 0x1FL)]);
                buffer.append(Base32.BASE32_ALPHABET[(int)(longValue >> 5 & 0x1FL)]);
                buffer.append(Base32.BASE32_ALPHABET[(int)(longValue & 0x1FL)]);
            }
            switch (off + length - pos) {
                case 1: {
                    final long longValue2 = ((long)data[pos] & 0xFFL) << 32;
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 35 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 30 & 0x1FL)]);
                    buffer.append("======");
                    return;
                }
                case 2: {
                    final long longValue2 = ((long)data[pos++] & 0xFFL) << 32 | ((long)data[pos] & 0xFFL) << 24;
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 35 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 30 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 25 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 20 & 0x1FL)]);
                    buffer.append("====");
                    return;
                }
                case 3: {
                    final long longValue2 = ((long)data[pos++] & 0xFFL) << 32 | ((long)data[pos++] & 0xFFL) << 24 | ((long)data[pos] & 0xFFL) << 16;
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 35 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 30 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 25 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 20 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 15 & 0x1FL)]);
                    buffer.append("===");
                    return;
                }
                case 4: {
                    final long longValue2 = ((long)data[pos++] & 0xFFL) << 32 | ((long)data[pos++] & 0xFFL) << 24 | ((long)data[pos++] & 0xFFL) << 16 | ((long)data[pos] & 0xFFL) << 8;
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 35 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 30 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 25 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 20 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 15 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 10 & 0x1FL)]);
                    buffer.append(Base32.BASE32_ALPHABET[(int)(longValue2 >> 5 & 0x1FL)]);
                    buffer.append("=");
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
        if (length % 8 != 0) {
            throw new ParseException(UtilityMessages.ERR_BASE32_DECODE_INVALID_LENGTH.get(), length);
        }
        final ByteStringBuffer buffer = new ByteStringBuffer(5 * (length / 8));
        int stringPos = 0;
        while (stringPos < length) {
            long longValue = 0L;
            for (int i = 0; i < 8; ++i) {
                longValue <<= 5;
                switch (data.charAt(stringPos++)) {
                    case 'A':
                    case 'a': {
                        longValue |= 0x0L;
                        break;
                    }
                    case 'B':
                    case 'b': {
                        longValue |= 0x1L;
                        break;
                    }
                    case 'C':
                    case 'c': {
                        longValue |= 0x2L;
                        break;
                    }
                    case 'D':
                    case 'd': {
                        longValue |= 0x3L;
                        break;
                    }
                    case 'E':
                    case 'e': {
                        longValue |= 0x4L;
                        break;
                    }
                    case 'F':
                    case 'f': {
                        longValue |= 0x5L;
                        break;
                    }
                    case 'G':
                    case 'g': {
                        longValue |= 0x6L;
                        break;
                    }
                    case 'H':
                    case 'h': {
                        longValue |= 0x7L;
                        break;
                    }
                    case 'I':
                    case 'i': {
                        longValue |= 0x8L;
                        break;
                    }
                    case 'J':
                    case 'j': {
                        longValue |= 0x9L;
                        break;
                    }
                    case 'K':
                    case 'k': {
                        longValue |= 0xAL;
                        break;
                    }
                    case 'L':
                    case 'l': {
                        longValue |= 0xBL;
                        break;
                    }
                    case 'M':
                    case 'm': {
                        longValue |= 0xCL;
                        break;
                    }
                    case 'N':
                    case 'n': {
                        longValue |= 0xDL;
                        break;
                    }
                    case 'O':
                    case 'o': {
                        longValue |= 0xEL;
                        break;
                    }
                    case 'P':
                    case 'p': {
                        longValue |= 0xFL;
                        break;
                    }
                    case 'Q':
                    case 'q': {
                        longValue |= 0x10L;
                        break;
                    }
                    case 'R':
                    case 'r': {
                        longValue |= 0x11L;
                        break;
                    }
                    case 'S':
                    case 's': {
                        longValue |= 0x12L;
                        break;
                    }
                    case 'T':
                    case 't': {
                        longValue |= 0x13L;
                        break;
                    }
                    case 'U':
                    case 'u': {
                        longValue |= 0x14L;
                        break;
                    }
                    case 'V':
                    case 'v': {
                        longValue |= 0x15L;
                        break;
                    }
                    case 'W':
                    case 'w': {
                        longValue |= 0x16L;
                        break;
                    }
                    case 'X':
                    case 'x': {
                        longValue |= 0x17L;
                        break;
                    }
                    case 'Y':
                    case 'y': {
                        longValue |= 0x18L;
                        break;
                    }
                    case 'Z':
                    case 'z': {
                        longValue |= 0x19L;
                        break;
                    }
                    case '2': {
                        longValue |= 0x1AL;
                        break;
                    }
                    case '3': {
                        longValue |= 0x1BL;
                        break;
                    }
                    case '4': {
                        longValue |= 0x1CL;
                        break;
                    }
                    case '5': {
                        longValue |= 0x1DL;
                        break;
                    }
                    case '6': {
                        longValue |= 0x1EL;
                        break;
                    }
                    case '7': {
                        longValue |= 0x1FL;
                        break;
                    }
                    case '=': {
                        switch (length - stringPos) {
                            case 0: {
                                buffer.append((byte)(longValue >> 32 & 0xFFL));
                                buffer.append((byte)(longValue >> 24 & 0xFFL));
                                buffer.append((byte)(longValue >> 16 & 0xFFL));
                                buffer.append((byte)(longValue >> 8 & 0xFFL));
                                return buffer.toByteArray();
                            }
                            case 2: {
                                longValue <<= 10;
                                buffer.append((byte)(longValue >> 32 & 0xFFL));
                                buffer.append((byte)(longValue >> 24 & 0xFFL));
                                buffer.append((byte)(longValue >> 16 & 0xFFL));
                                return buffer.toByteArray();
                            }
                            case 3: {
                                longValue <<= 15;
                                buffer.append((byte)(longValue >> 32 & 0xFFL));
                                buffer.append((byte)(longValue >> 24 & 0xFFL));
                                return buffer.toByteArray();
                            }
                            case 5: {
                                longValue <<= 25;
                                buffer.append((byte)(longValue >> 32 & 0xFFL));
                                return buffer.toByteArray();
                            }
                            default: {
                                throw new ParseException(UtilityMessages.ERR_BASE32_DECODE_UNEXPECTED_EQUAL.get(stringPos - 1), stringPos - 1);
                            }
                        }
                        break;
                    }
                    default: {
                        throw new ParseException(UtilityMessages.ERR_BASE32_DECODE_UNEXPECTED_CHAR.get(data.charAt(stringPos - 1)), stringPos - 1);
                    }
                }
            }
            buffer.append((byte)(longValue >> 32 & 0xFFL));
            buffer.append((byte)(longValue >> 24 & 0xFFL));
            buffer.append((byte)(longValue >> 16 & 0xFFL));
            buffer.append((byte)(longValue >> 8 & 0xFFL));
            buffer.append((byte)(longValue & 0xFFL));
        }
        return buffer.toByteArray();
    }
    
    public static String decodeToString(final String data) throws ParseException {
        Validator.ensureNotNull(data);
        final byte[] decodedBytes = decode(data);
        return StaticUtils.toUTF8String(decodedBytes);
    }
    
    static {
        BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();
    }
}
