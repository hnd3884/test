package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathArithmeticException;
import java.math.BigDecimal;

public class Precision
{
    public static final double EPSILON;
    public static final double SAFE_MIN;
    private static final long EXPONENT_OFFSET = 1023L;
    private static final long SGN_MASK = Long.MIN_VALUE;
    private static final int SGN_MASK_FLOAT = Integer.MIN_VALUE;
    private static final double POSITIVE_ZERO = 0.0;
    private static final long POSITIVE_ZERO_DOUBLE_BITS;
    private static final long NEGATIVE_ZERO_DOUBLE_BITS;
    private static final int POSITIVE_ZERO_FLOAT_BITS;
    private static final int NEGATIVE_ZERO_FLOAT_BITS;
    
    private Precision() {
    }
    
    public static int compareTo(final double x, final double y, final double eps) {
        if (equals(x, y, eps)) {
            return 0;
        }
        if (x < y) {
            return -1;
        }
        return 1;
    }
    
    public static int compareTo(final double x, final double y, final int maxUlps) {
        if (equals(x, y, maxUlps)) {
            return 0;
        }
        if (x < y) {
            return -1;
        }
        return 1;
    }
    
    public static boolean equals(final float x, final float y) {
        return equals(x, y, 1);
    }
    
    public static boolean equalsIncludingNaN(final float x, final float y) {
        return (x != x || y != y) ? (!(x != x ^ y != y)) : equals(x, y, 1);
    }
    
    public static boolean equals(final float x, final float y, final float eps) {
        return equals(x, y, 1) || FastMath.abs(y - x) <= eps;
    }
    
    public static boolean equalsIncludingNaN(final float x, final float y, final float eps) {
        return equalsIncludingNaN(x, y) || FastMath.abs(y - x) <= eps;
    }
    
    public static boolean equals(final float x, final float y, final int maxUlps) {
        final int xInt = Float.floatToRawIntBits(x);
        final int yInt = Float.floatToRawIntBits(y);
        boolean isEqual;
        if (((xInt ^ yInt) & Integer.MIN_VALUE) == 0x0) {
            isEqual = (FastMath.abs(xInt - yInt) <= maxUlps);
        }
        else {
            int deltaPlus;
            int deltaMinus;
            if (xInt < yInt) {
                deltaPlus = yInt - Precision.POSITIVE_ZERO_FLOAT_BITS;
                deltaMinus = xInt - Precision.NEGATIVE_ZERO_FLOAT_BITS;
            }
            else {
                deltaPlus = xInt - Precision.POSITIVE_ZERO_FLOAT_BITS;
                deltaMinus = yInt - Precision.NEGATIVE_ZERO_FLOAT_BITS;
            }
            isEqual = (deltaPlus <= maxUlps && deltaMinus <= maxUlps - deltaPlus);
        }
        return isEqual && !Float.isNaN(x) && !Float.isNaN(y);
    }
    
    public static boolean equalsIncludingNaN(final float x, final float y, final int maxUlps) {
        return (x != x || y != y) ? (!(x != x ^ y != y)) : equals(x, y, maxUlps);
    }
    
    public static boolean equals(final double x, final double y) {
        return equals(x, y, 1);
    }
    
    public static boolean equalsIncludingNaN(final double x, final double y) {
        return (x != x || y != y) ? (!(x != x ^ y != y)) : equals(x, y, 1);
    }
    
    public static boolean equals(final double x, final double y, final double eps) {
        return equals(x, y, 1) || FastMath.abs(y - x) <= eps;
    }
    
    public static boolean equalsWithRelativeTolerance(final double x, final double y, final double eps) {
        if (equals(x, y, 1)) {
            return true;
        }
        final double absoluteMax = FastMath.max(FastMath.abs(x), FastMath.abs(y));
        final double relativeDifference = FastMath.abs((x - y) / absoluteMax);
        return relativeDifference <= eps;
    }
    
    public static boolean equalsIncludingNaN(final double x, final double y, final double eps) {
        return equalsIncludingNaN(x, y) || FastMath.abs(y - x) <= eps;
    }
    
    public static boolean equals(final double x, final double y, final int maxUlps) {
        final long xInt = Double.doubleToRawLongBits(x);
        final long yInt = Double.doubleToRawLongBits(y);
        boolean isEqual;
        if (((xInt ^ yInt) & Long.MIN_VALUE) == 0x0L) {
            isEqual = (FastMath.abs(xInt - yInt) <= maxUlps);
        }
        else {
            long deltaPlus;
            long deltaMinus;
            if (xInt < yInt) {
                deltaPlus = yInt - Precision.POSITIVE_ZERO_DOUBLE_BITS;
                deltaMinus = xInt - Precision.NEGATIVE_ZERO_DOUBLE_BITS;
            }
            else {
                deltaPlus = xInt - Precision.POSITIVE_ZERO_DOUBLE_BITS;
                deltaMinus = yInt - Precision.NEGATIVE_ZERO_DOUBLE_BITS;
            }
            isEqual = (deltaPlus <= maxUlps && deltaMinus <= maxUlps - deltaPlus);
        }
        return isEqual && !Double.isNaN(x) && !Double.isNaN(y);
    }
    
    public static boolean equalsIncludingNaN(final double x, final double y, final int maxUlps) {
        return (x != x || y != y) ? (!(x != x ^ y != y)) : equals(x, y, maxUlps);
    }
    
    public static double round(final double x, final int scale) {
        return round(x, scale, 4);
    }
    
    public static double round(final double x, final int scale, final int roundingMethod) {
        try {
            final double rounded = new BigDecimal(Double.toString(x)).setScale(scale, roundingMethod).doubleValue();
            return (rounded == 0.0) ? (0.0 * x) : rounded;
        }
        catch (final NumberFormatException ex) {
            if (Double.isInfinite(x)) {
                return x;
            }
            return Double.NaN;
        }
    }
    
    public static float round(final float x, final int scale) {
        return round(x, scale, 4);
    }
    
    public static float round(final float x, final int scale, final int roundingMethod) throws MathArithmeticException, MathIllegalArgumentException {
        final float sign = FastMath.copySign(1.0f, x);
        final float factor = (float)FastMath.pow(10.0, scale) * sign;
        return (float)roundUnscaled(x * factor, sign, roundingMethod) / factor;
    }
    
    private static double roundUnscaled(double unscaled, final double sign, final int roundingMethod) throws MathArithmeticException, MathIllegalArgumentException {
        switch (roundingMethod) {
            case 2: {
                if (sign == -1.0) {
                    unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
                    break;
                }
                unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
                break;
            }
            case 1: {
                unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
                break;
            }
            case 3: {
                if (sign == -1.0) {
                    unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
                    break;
                }
                unscaled = FastMath.floor(FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY));
                break;
            }
            case 5: {
                unscaled = FastMath.nextAfter(unscaled, Double.NEGATIVE_INFINITY);
                final double fraction = unscaled - FastMath.floor(unscaled);
                if (fraction > 0.5) {
                    unscaled = FastMath.ceil(unscaled);
                    break;
                }
                unscaled = FastMath.floor(unscaled);
                break;
            }
            case 6: {
                final double fraction = unscaled - FastMath.floor(unscaled);
                if (fraction > 0.5) {
                    unscaled = FastMath.ceil(unscaled);
                    break;
                }
                if (fraction < 0.5) {
                    unscaled = FastMath.floor(unscaled);
                    break;
                }
                if (FastMath.floor(unscaled) / 2.0 == FastMath.floor(FastMath.floor(unscaled) / 2.0)) {
                    unscaled = FastMath.floor(unscaled);
                    break;
                }
                unscaled = FastMath.ceil(unscaled);
                break;
            }
            case 4: {
                unscaled = FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY);
                final double fraction = unscaled - FastMath.floor(unscaled);
                if (fraction >= 0.5) {
                    unscaled = FastMath.ceil(unscaled);
                    break;
                }
                unscaled = FastMath.floor(unscaled);
                break;
            }
            case 7: {
                if (unscaled != FastMath.floor(unscaled)) {
                    throw new MathArithmeticException();
                }
                break;
            }
            case 0: {
                if (unscaled != FastMath.floor(unscaled)) {
                    unscaled = FastMath.ceil(FastMath.nextAfter(unscaled, Double.POSITIVE_INFINITY));
                    break;
                }
                break;
            }
            default: {
                throw new MathIllegalArgumentException(LocalizedFormats.INVALID_ROUNDING_METHOD, new Object[] { roundingMethod, "ROUND_CEILING", 2, "ROUND_DOWN", 1, "ROUND_FLOOR", 3, "ROUND_HALF_DOWN", 5, "ROUND_HALF_EVEN", 6, "ROUND_HALF_UP", 4, "ROUND_UNNECESSARY", 7, "ROUND_UP", 0 });
            }
        }
        return unscaled;
    }
    
    public static double representableDelta(final double x, final double originalDelta) {
        return x + originalDelta - x;
    }
    
    static {
        POSITIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(0.0);
        NEGATIVE_ZERO_DOUBLE_BITS = Double.doubleToRawLongBits(-0.0);
        POSITIVE_ZERO_FLOAT_BITS = Float.floatToRawIntBits(0.0f);
        NEGATIVE_ZERO_FLOAT_BITS = Float.floatToRawIntBits(-0.0f);
        EPSILON = Double.longBitsToDouble(4368491638549381120L);
        SAFE_MIN = Double.longBitsToDouble(4503599627370496L);
    }
}
