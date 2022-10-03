package sun.misc;

public class FpUtils
{
    private FpUtils() {
    }
    
    @Deprecated
    public static int getExponent(final double n) {
        return Math.getExponent(n);
    }
    
    @Deprecated
    public static int getExponent(final float n) {
        return Math.getExponent(n);
    }
    
    @Deprecated
    public static double rawCopySign(final double n, final double n2) {
        return Math.copySign(n, n2);
    }
    
    @Deprecated
    public static float rawCopySign(final float n, final float n2) {
        return Math.copySign(n, n2);
    }
    
    @Deprecated
    public static boolean isFinite(final double n) {
        return Double.isFinite(n);
    }
    
    @Deprecated
    public static boolean isFinite(final float n) {
        return Float.isFinite(n);
    }
    
    public static boolean isInfinite(final double n) {
        return Double.isInfinite(n);
    }
    
    public static boolean isInfinite(final float n) {
        return Float.isInfinite(n);
    }
    
    public static boolean isNaN(final double n) {
        return Double.isNaN(n);
    }
    
    public static boolean isNaN(final float n) {
        return Float.isNaN(n);
    }
    
    public static boolean isUnordered(final double n, final double n2) {
        return isNaN(n) || isNaN(n2);
    }
    
    public static boolean isUnordered(final float n, final float n2) {
        return isNaN(n) || isNaN(n2);
    }
    
    public static int ilogb(final double n) {
        int exponent = getExponent(n);
        switch (exponent) {
            case 1024: {
                if (isNaN(n)) {
                    return 1073741824;
                }
                return 268435456;
            }
            case -1023: {
                if (n == 0.0) {
                    return -268435456;
                }
                long n2 = Double.doubleToRawLongBits(n) & 0xFFFFFFFFFFFFFL;
                assert n2 != 0L;
                while (n2 < 4503599627370496L) {
                    n2 *= 2L;
                    --exponent;
                }
                ++exponent;
                assert exponent >= -1074 && exponent < -1022;
                return exponent;
            }
            default: {
                assert exponent >= -1022 && exponent <= 1023;
                return exponent;
            }
        }
    }
    
    public static int ilogb(final float n) {
        int exponent = getExponent(n);
        switch (exponent) {
            case 128: {
                if (isNaN(n)) {
                    return 1073741824;
                }
                return 268435456;
            }
            case -127: {
                if (n == 0.0f) {
                    return -268435456;
                }
                int i = Float.floatToRawIntBits(n) & 0x7FFFFF;
                assert i != 0;
                while (i < 8388608) {
                    i *= 2;
                    --exponent;
                }
                ++exponent;
                assert exponent >= -149 && exponent < -126;
                return exponent;
            }
            default: {
                assert exponent >= -126 && exponent <= 127;
                return exponent;
            }
        }
    }
    
    @Deprecated
    public static double scalb(final double n, final int n2) {
        return Math.scalb(n, n2);
    }
    
    @Deprecated
    public static float scalb(final float n, final int n2) {
        return Math.scalb(n, n2);
    }
    
    @Deprecated
    public static double nextAfter(final double n, final double n2) {
        return Math.nextAfter(n, n2);
    }
    
    @Deprecated
    public static float nextAfter(final float n, final double n2) {
        return Math.nextAfter(n, n2);
    }
    
    @Deprecated
    public static double nextUp(final double n) {
        return Math.nextUp(n);
    }
    
    @Deprecated
    public static float nextUp(final float n) {
        return Math.nextUp(n);
    }
    
    @Deprecated
    public static double nextDown(final double n) {
        return Math.nextDown(n);
    }
    
    @Deprecated
    public static double nextDown(final float n) {
        return Math.nextDown(n);
    }
    
    @Deprecated
    public static double copySign(final double n, final double n2) {
        return StrictMath.copySign(n, n2);
    }
    
    @Deprecated
    public static float copySign(final float n, final float n2) {
        return StrictMath.copySign(n, n2);
    }
    
    @Deprecated
    public static double ulp(final double n) {
        return Math.ulp(n);
    }
    
    @Deprecated
    public static float ulp(final float n) {
        return Math.ulp(n);
    }
    
    @Deprecated
    public static double signum(final double n) {
        return Math.signum(n);
    }
    
    @Deprecated
    public static float signum(final float n) {
        return Math.signum(n);
    }
}
