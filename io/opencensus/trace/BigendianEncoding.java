package io.opencensus.trace;

import io.opencensus.internal.Utils;
import java.util.Arrays;

final class BigendianEncoding
{
    static final int LONG_BYTES = 8;
    static final int BYTE_BASE16 = 2;
    static final int LONG_BASE16 = 16;
    private static final String ALPHABET = "0123456789abcdef";
    private static final int ASCII_CHARACTERS = 128;
    private static final char[] ENCODING;
    private static final byte[] DECODING;
    
    private static char[] buildEncodingArray() {
        final char[] encoding = new char[512];
        for (int i = 0; i < 256; ++i) {
            encoding[i] = "0123456789abcdef".charAt(i >>> 4);
            encoding[i | 0x100] = "0123456789abcdef".charAt(i & 0xF);
        }
        return encoding;
    }
    
    private static byte[] buildDecodingArray() {
        final byte[] decoding = new byte[128];
        Arrays.fill(decoding, (byte)(-1));
        for (int i = 0; i < "0123456789abcdef".length(); ++i) {
            final char c = "0123456789abcdef".charAt(i);
            decoding[c] = (byte)i;
        }
        return decoding;
    }
    
    static long longFromByteArray(final byte[] bytes, final int offset) {
        Utils.checkArgument(bytes.length >= offset + 8, (Object)"array too small");
        return ((long)bytes[offset] & 0xFFL) << 56 | ((long)bytes[offset + 1] & 0xFFL) << 48 | ((long)bytes[offset + 2] & 0xFFL) << 40 | ((long)bytes[offset + 3] & 0xFFL) << 32 | ((long)bytes[offset + 4] & 0xFFL) << 24 | ((long)bytes[offset + 5] & 0xFFL) << 16 | ((long)bytes[offset + 6] & 0xFFL) << 8 | ((long)bytes[offset + 7] & 0xFFL);
    }
    
    static void longToByteArray(final long value, final byte[] dest, final int destOffset) {
        Utils.checkArgument(dest.length >= destOffset + 8, (Object)"array too small");
        dest[destOffset + 7] = (byte)(value & 0xFFL);
        dest[destOffset + 6] = (byte)(value >> 8 & 0xFFL);
        dest[destOffset + 5] = (byte)(value >> 16 & 0xFFL);
        dest[destOffset + 4] = (byte)(value >> 24 & 0xFFL);
        dest[destOffset + 3] = (byte)(value >> 32 & 0xFFL);
        dest[destOffset + 2] = (byte)(value >> 40 & 0xFFL);
        dest[destOffset + 1] = (byte)(value >> 48 & 0xFFL);
        dest[destOffset] = (byte)(value >> 56 & 0xFFL);
    }
    
    static long longFromBase16String(final CharSequence chars, final int offset) {
        Utils.checkArgument(chars.length() >= offset + 16, (Object)"chars too small");
        return ((long)decodeByte(chars.charAt(offset), chars.charAt(offset + 1)) & 0xFFL) << 56 | ((long)decodeByte(chars.charAt(offset + 2), chars.charAt(offset + 3)) & 0xFFL) << 48 | ((long)decodeByte(chars.charAt(offset + 4), chars.charAt(offset + 5)) & 0xFFL) << 40 | ((long)decodeByte(chars.charAt(offset + 6), chars.charAt(offset + 7)) & 0xFFL) << 32 | ((long)decodeByte(chars.charAt(offset + 8), chars.charAt(offset + 9)) & 0xFFL) << 24 | ((long)decodeByte(chars.charAt(offset + 10), chars.charAt(offset + 11)) & 0xFFL) << 16 | ((long)decodeByte(chars.charAt(offset + 12), chars.charAt(offset + 13)) & 0xFFL) << 8 | ((long)decodeByte(chars.charAt(offset + 14), chars.charAt(offset + 15)) & 0xFFL);
    }
    
    static void longToBase16String(final long value, final char[] dest, final int destOffset) {
        byteToBase16((byte)(value >> 56 & 0xFFL), dest, destOffset);
        byteToBase16((byte)(value >> 48 & 0xFFL), dest, destOffset + 2);
        byteToBase16((byte)(value >> 40 & 0xFFL), dest, destOffset + 4);
        byteToBase16((byte)(value >> 32 & 0xFFL), dest, destOffset + 6);
        byteToBase16((byte)(value >> 24 & 0xFFL), dest, destOffset + 8);
        byteToBase16((byte)(value >> 16 & 0xFFL), dest, destOffset + 10);
        byteToBase16((byte)(value >> 8 & 0xFFL), dest, destOffset + 12);
        byteToBase16((byte)(value & 0xFFL), dest, destOffset + 14);
    }
    
    static void byteToBase16String(final byte value, final char[] dest, final int destOffset) {
        byteToBase16(value, dest, destOffset);
    }
    
    static byte byteFromBase16String(final CharSequence chars, final int offset) {
        Utils.checkArgument(chars.length() >= offset + 2, (Object)"chars too small");
        return decodeByte(chars.charAt(offset), chars.charAt(offset + 1));
    }
    
    private static byte decodeByte(final char hi, final char lo) {
        Utils.checkArgument(lo < '\u0080' && BigendianEncoding.DECODING[lo] != -1, (Object)("invalid character " + lo));
        Utils.checkArgument(hi < '\u0080' && BigendianEncoding.DECODING[hi] != -1, (Object)("invalid character " + hi));
        final int decoded = BigendianEncoding.DECODING[hi] << 4 | BigendianEncoding.DECODING[lo];
        return (byte)decoded;
    }
    
    private static void byteToBase16(final byte value, final char[] dest, final int destOffset) {
        final int b = value & 0xFF;
        dest[destOffset] = BigendianEncoding.ENCODING[b];
        dest[destOffset + 1] = BigendianEncoding.ENCODING[b | 0x100];
    }
    
    private BigendianEncoding() {
    }
    
    static {
        ENCODING = buildEncodingArray();
        DECODING = buildDecodingArray();
    }
}
