package jcifs.util;

public class Base64
{
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    
    public static String encode(final byte[] bytes) {
        int length = bytes.length;
        if (length == 0) {
            return "";
        }
        final StringBuffer buffer = new StringBuffer((int)Math.ceil(length / 3.0) * 4);
        final int remainder = length % 3;
        length -= remainder;
        int i = 0;
        while (i < length) {
            final int block = (bytes[i++] & 0xFF) << 16 | (bytes[i++] & 0xFF) << 8 | (bytes[i++] & 0xFF);
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 18));
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 12 & 0x3F));
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 6 & 0x3F));
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block & 0x3F));
        }
        if (remainder == 0) {
            return buffer.toString();
        }
        if (remainder == 1) {
            final int block = (bytes[i] & 0xFF) << 4;
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 6));
            buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block & 0x3F));
            buffer.append("==");
            return buffer.toString();
        }
        final int block = ((bytes[i++] & 0xFF) << 8 | (bytes[i] & 0xFF)) << 2;
        buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 12));
        buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block >>> 6 & 0x3F));
        buffer.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(block & 0x3F));
        buffer.append("=");
        return buffer.toString();
    }
    
    public static byte[] decode(final String string) {
        final int length = string.length();
        if (length == 0) {
            return new byte[0];
        }
        final int pad = (string.charAt(length - 2) == '=') ? 2 : ((string.charAt(length - 1) == '=') ? 1 : 0);
        final int size = length * 3 / 4 - pad;
        final byte[] buffer = new byte[size];
        int i = 0;
        int index = 0;
        while (i < length) {
            final int block = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(string.charAt(i++)) & 0xFF) << 18 | ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(string.charAt(i++)) & 0xFF) << 12 | ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(string.charAt(i++)) & 0xFF) << 6 | ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".indexOf(string.charAt(i++)) & 0xFF);
            buffer[index++] = (byte)(block >>> 16);
            if (index < size) {
                buffer[index++] = (byte)(block >>> 8 & 0xFF);
            }
            if (index < size) {
                buffer[index++] = (byte)(block & 0xFF);
            }
        }
        return buffer;
    }
}
