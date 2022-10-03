package org.apache.lucene.analysis.payloads;

public class PayloadHelper
{
    public static byte[] encodeFloat(final float payload) {
        return encodeFloat(payload, new byte[4], 0);
    }
    
    public static byte[] encodeFloat(final float payload, final byte[] data, final int offset) {
        return encodeInt(Float.floatToIntBits(payload), data, offset);
    }
    
    public static byte[] encodeInt(final int payload) {
        return encodeInt(payload, new byte[4], 0);
    }
    
    public static byte[] encodeInt(final int payload, final byte[] data, final int offset) {
        data[offset] = (byte)(payload >> 24);
        data[offset + 1] = (byte)(payload >> 16);
        data[offset + 2] = (byte)(payload >> 8);
        data[offset + 3] = (byte)payload;
        return data;
    }
    
    public static float decodeFloat(final byte[] bytes) {
        return decodeFloat(bytes, 0);
    }
    
    public static final float decodeFloat(final byte[] bytes, final int offset) {
        return Float.intBitsToFloat(decodeInt(bytes, offset));
    }
    
    public static final int decodeInt(final byte[] bytes, final int offset) {
        return (bytes[offset] & 0xFF) << 24 | (bytes[offset + 1] & 0xFF) << 16 | (bytes[offset + 2] & 0xFF) << 8 | (bytes[offset + 3] & 0xFF);
    }
}
