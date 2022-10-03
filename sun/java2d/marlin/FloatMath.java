package sun.java2d.marlin;

public final class FloatMath implements MarlinConst
{
    static final boolean CHECK_OVERFLOW = true;
    static final boolean CHECK_NAN = true;
    public static final int FLOAT_SIGNIFICAND_WIDTH = 24;
    public static final int FLOAT_EXP_BIAS = 127;
    public static final int FLOAT_EXP_BIT_MASK = 2139095040;
    public static final int FLOAT_SIGNIF_BIT_MASK = 8388607;
    
    private FloatMath() {
    }
    
    static int max(final int n, final int n2) {
        return (n >= n2) ? n : n2;
    }
    
    static int min(final int n, final int n2) {
        return (n <= n2) ? n : n2;
    }
    
    public static float ceil_f(final float n) {
        final int floatToRawIntBits = Float.floatToRawIntBits(n);
        final int n2 = ((floatToRawIntBits & 0x7F800000) >> 23) - 127;
        if (n2 < 0) {
            return (n == 0.0f) ? n : ((n < 0.0f) ? -0.0f : 1.0f);
        }
        if (n2 >= 23) {
            return n;
        }
        assert n2 >= 0 && n2 <= 22;
        final int n3 = floatToRawIntBits & ~(8388607 >> n2);
        if (n3 == floatToRawIntBits) {
            return n;
        }
        return Float.intBitsToFloat(n3) + (~n3 >>> 31);
    }
    
    public static float floor_f(final float n) {
        final int floatToRawIntBits = Float.floatToRawIntBits(n);
        final int n2 = ((floatToRawIntBits & 0x7F800000) >> 23) - 127;
        if (n2 < 0) {
            return (n == 0.0f) ? n : ((n < 0.0f) ? -1.0f : 0.0f);
        }
        if (n2 >= 23) {
            return n;
        }
        assert n2 >= 0 && n2 <= 22;
        final int n3 = floatToRawIntBits & ~(8388607 >> n2);
        if (n3 == floatToRawIntBits) {
            return n;
        }
        return Float.intBitsToFloat(n3) + (n3 >> 31);
    }
    
    public static int ceil_int(final float n) {
        final int n2 = (int)n;
        if (n <= n2 || n2 == Integer.MAX_VALUE || Float.isNaN(n)) {
            return n2;
        }
        return n2 + 1;
    }
    
    public static int ceil_int(final double n) {
        final int n2 = (int)n;
        if (n <= n2 || n2 == Integer.MAX_VALUE || Double.isNaN(n)) {
            return n2;
        }
        return n2 + 1;
    }
    
    public static int floor_int(final float n) {
        final int n2 = (int)n;
        if (n >= n2 || n2 == Integer.MIN_VALUE || Float.isNaN(n)) {
            return n2;
        }
        return n2 - 1;
    }
    
    public static int floor_int(final double n) {
        final int n2 = (int)n;
        if (n >= n2 || n2 == Integer.MIN_VALUE || Double.isNaN(n)) {
            return n2;
        }
        return n2 - 1;
    }
}
