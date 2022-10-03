package org.apache.lucene.util;

public class SmallFloat
{
    private SmallFloat() {
    }
    
    public static byte floatToByte(final float f, final int numMantissaBits, final int zeroExp) {
        final int fzero = 63 - zeroExp << numMantissaBits;
        final int bits = Float.floatToRawIntBits(f);
        final int smallfloat = bits >> 24 - numMantissaBits;
        if (smallfloat <= fzero) {
            return (byte)((bits > 0) ? 1 : 0);
        }
        if (smallfloat >= fzero + 256) {
            return -1;
        }
        return (byte)(smallfloat - fzero);
    }
    
    public static float byteToFloat(final byte b, final int numMantissaBits, final int zeroExp) {
        if (b == 0) {
            return 0.0f;
        }
        int bits = (b & 0xFF) << 24 - numMantissaBits;
        bits += 63 - zeroExp << 24;
        return Float.intBitsToFloat(bits);
    }
    
    public static byte floatToByte315(final float f) {
        final int bits = Float.floatToRawIntBits(f);
        final int smallfloat = bits >> 21;
        if (smallfloat <= 384) {
            return (byte)((bits > 0) ? 1 : 0);
        }
        if (smallfloat >= 640) {
            return -1;
        }
        return (byte)(smallfloat - 384);
    }
    
    public static float byte315ToFloat(final byte b) {
        if (b == 0) {
            return 0.0f;
        }
        int bits = (b & 0xFF) << 21;
        bits += 805306368;
        return Float.intBitsToFloat(bits);
    }
    
    public static byte floatToByte52(final float f) {
        final int bits = Float.floatToRawIntBits(f);
        final int smallfloat = bits >> 19;
        if (smallfloat <= 1952) {
            return (byte)((bits > 0) ? 1 : 0);
        }
        if (smallfloat >= 2208) {
            return -1;
        }
        return (byte)(smallfloat - 1952);
    }
    
    public static float byte52ToFloat(final byte b) {
        if (b == 0) {
            return 0.0f;
        }
        int bits = (b & 0xFF) << 19;
        bits += 1023410176;
        return Float.intBitsToFloat(bits);
    }
}
