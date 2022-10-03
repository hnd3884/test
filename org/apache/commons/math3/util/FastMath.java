package org.apache.commons.math3.util;

import java.io.PrintStream;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class FastMath
{
    public static final double PI = 3.141592653589793;
    public static final double E = 2.718281828459045;
    static final int EXP_INT_TABLE_MAX_INDEX = 750;
    static final int EXP_INT_TABLE_LEN = 1500;
    static final int LN_MANT_LEN = 1024;
    static final int EXP_FRAC_TABLE_LEN = 1025;
    private static final double LOG_MAX_VALUE;
    private static final boolean RECOMPUTE_TABLES_AT_RUNTIME = false;
    private static final double LN_2_A = 0.6931470632553101;
    private static final double LN_2_B = 1.1730463525082348E-7;
    private static final double[][] LN_QUICK_COEF;
    private static final double[][] LN_HI_PREC_COEF;
    private static final int SINE_TABLE_LEN = 14;
    private static final double[] SINE_TABLE_A;
    private static final double[] SINE_TABLE_B;
    private static final double[] COSINE_TABLE_A;
    private static final double[] COSINE_TABLE_B;
    private static final double[] TANGENT_TABLE_A;
    private static final double[] TANGENT_TABLE_B;
    private static final long[] RECIP_2PI;
    private static final long[] PI_O_4_BITS;
    private static final double[] EIGHTHS;
    private static final double[] CBRTTWO;
    private static final long HEX_40000000 = 1073741824L;
    private static final long MASK_30BITS = -1073741824L;
    private static final int MASK_NON_SIGN_INT = Integer.MAX_VALUE;
    private static final long MASK_NON_SIGN_LONG = Long.MAX_VALUE;
    private static final long MASK_DOUBLE_EXPONENT = 9218868437227405312L;
    private static final long MASK_DOUBLE_MANTISSA = 4503599627370495L;
    private static final long IMPLICIT_HIGH_BIT = 4503599627370496L;
    private static final double TWO_POWER_52 = 4.503599627370496E15;
    private static final double F_1_3 = 0.3333333333333333;
    private static final double F_1_5 = 0.2;
    private static final double F_1_7 = 0.14285714285714285;
    private static final double F_1_9 = 0.1111111111111111;
    private static final double F_1_11 = 0.09090909090909091;
    private static final double F_1_13 = 0.07692307692307693;
    private static final double F_1_15 = 0.06666666666666667;
    private static final double F_1_17 = 0.058823529411764705;
    private static final double F_3_4 = 0.75;
    private static final double F_15_16 = 0.9375;
    private static final double F_13_14 = 0.9285714285714286;
    private static final double F_11_12 = 0.9166666666666666;
    private static final double F_9_10 = 0.9;
    private static final double F_7_8 = 0.875;
    private static final double F_5_6 = 0.8333333333333334;
    private static final double F_1_2 = 0.5;
    private static final double F_1_4 = 0.25;
    
    private FastMath() {
    }
    
    private static double doubleHighPart(final double d) {
        if (d > -Precision.SAFE_MIN && d < Precision.SAFE_MIN) {
            return d;
        }
        long xl = Double.doubleToRawLongBits(d);
        xl &= 0xFFFFFFFFC0000000L;
        return Double.longBitsToDouble(xl);
    }
    
    public static double sqrt(final double a) {
        return Math.sqrt(a);
    }
    
    public static double cosh(double x) {
        if (x != x) {
            return x;
        }
        if (x > 20.0) {
            if (x >= FastMath.LOG_MAX_VALUE) {
                final double t = exp(0.5 * x);
                return 0.5 * t * t;
            }
            return 0.5 * exp(x);
        }
        else {
            if (x >= -20.0) {
                final double[] hiPrec = new double[2];
                if (x < 0.0) {
                    x = -x;
                }
                exp(x, 0.0, hiPrec);
                double ya = hiPrec[0] + hiPrec[1];
                double yb = -(ya - hiPrec[0] - hiPrec[1]);
                double temp = ya * 1.073741824E9;
                final double yaa = ya + temp - temp;
                final double yab = ya - yaa;
                final double recip = 1.0 / ya;
                temp = recip * 1.073741824E9;
                final double recipa = recip + temp - temp;
                double recipb = recip - recipa;
                recipb += (1.0 - yaa * recipa - yaa * recipb - yab * recipa - yab * recipb) * recip;
                recipb += -yb * recip * recip;
                temp = ya + recipa;
                yb += -(temp - ya - recipa);
                ya = temp;
                temp = ya + recipb;
                yb += -(temp - ya - recipb);
                ya = temp;
                double result = ya + yb;
                result *= 0.5;
                return result;
            }
            if (x <= -FastMath.LOG_MAX_VALUE) {
                final double t = exp(-0.5 * x);
                return 0.5 * t * t;
            }
            return 0.5 * exp(-x);
        }
    }
    
    public static double sinh(double x) {
        boolean negate = false;
        if (x != x) {
            return x;
        }
        if (x > 20.0) {
            if (x >= FastMath.LOG_MAX_VALUE) {
                final double t = exp(0.5 * x);
                return 0.5 * t * t;
            }
            return 0.5 * exp(x);
        }
        else if (x < -20.0) {
            if (x <= -FastMath.LOG_MAX_VALUE) {
                final double t = exp(-0.5 * x);
                return -0.5 * t * t;
            }
            return -0.5 * exp(-x);
        }
        else {
            if (x == 0.0) {
                return x;
            }
            if (x < 0.0) {
                x = -x;
                negate = true;
            }
            double result;
            if (x > 0.25) {
                final double[] hiPrec = new double[2];
                exp(x, 0.0, hiPrec);
                double ya = hiPrec[0] + hiPrec[1];
                double yb = -(ya - hiPrec[0] - hiPrec[1]);
                double temp = ya * 1.073741824E9;
                final double yaa = ya + temp - temp;
                final double yab = ya - yaa;
                final double recip = 1.0 / ya;
                temp = recip * 1.073741824E9;
                double recipa = recip + temp - temp;
                double recipb = recip - recipa;
                recipb += (1.0 - yaa * recipa - yaa * recipb - yab * recipa - yab * recipb) * recip;
                recipb += -yb * recip * recip;
                recipa = -recipa;
                recipb = -recipb;
                temp = ya + recipa;
                yb += -(temp - ya - recipa);
                ya = temp;
                temp = ya + recipb;
                yb += -(temp - ya - recipb);
                ya = temp;
                result = ya + yb;
                result *= 0.5;
            }
            else {
                final double[] hiPrec = new double[2];
                expm1(x, hiPrec);
                double ya = hiPrec[0] + hiPrec[1];
                double yb = -(ya - hiPrec[0] - hiPrec[1]);
                final double denom = 1.0 + ya;
                final double denomr = 1.0 / denom;
                final double denomb = -(denom - 1.0 - ya) + yb;
                final double ratio = ya * denomr;
                double temp2 = ratio * 1.073741824E9;
                final double ra = ratio + temp2 - temp2;
                double rb = ratio - ra;
                temp2 = denom * 1.073741824E9;
                final double za = denom + temp2 - temp2;
                final double zb = denom - za;
                rb += (ya - za * ra - za * rb - zb * ra - zb * rb) * denomr;
                rb += yb * denomr;
                rb += -ya * denomb * denomr * denomr;
                temp2 = ya + ra;
                yb += -(temp2 - ya - ra);
                ya = temp2;
                temp2 = ya + rb;
                yb += -(temp2 - ya - rb);
                ya = temp2;
                result = ya + yb;
                result *= 0.5;
            }
            if (negate) {
                result = -result;
            }
            return result;
        }
    }
    
    public static double tanh(double x) {
        boolean negate = false;
        if (x != x) {
            return x;
        }
        if (x > 20.0) {
            return 1.0;
        }
        if (x < -20.0) {
            return -1.0;
        }
        if (x == 0.0) {
            return x;
        }
        if (x < 0.0) {
            x = -x;
            negate = true;
        }
        double result;
        if (x >= 0.5) {
            final double[] hiPrec = new double[2];
            exp(x * 2.0, 0.0, hiPrec);
            final double ya = hiPrec[0] + hiPrec[1];
            final double yb = -(ya - hiPrec[0] - hiPrec[1]);
            double na = -1.0 + ya;
            double nb = -(na + 1.0 - ya);
            double temp = na + yb;
            nb += -(temp - na - yb);
            na = temp;
            double da = 1.0 + ya;
            double db = -(da - 1.0 - ya);
            temp = da + yb;
            db += -(temp - da - yb);
            da = temp;
            temp = da * 1.073741824E9;
            final double daa = da + temp - temp;
            final double dab = da - daa;
            final double ratio = na / da;
            temp = ratio * 1.073741824E9;
            final double ratioa = ratio + temp - temp;
            double ratiob = ratio - ratioa;
            ratiob += (na - daa * ratioa - daa * ratiob - dab * ratioa - dab * ratiob) / da;
            ratiob += nb / da;
            ratiob += -db * na / da / da;
            result = ratioa + ratiob;
        }
        else {
            final double[] hiPrec = new double[2];
            expm1(x * 2.0, hiPrec);
            final double ya = hiPrec[0] + hiPrec[1];
            final double yb = -(ya - hiPrec[0] - hiPrec[1]);
            final double na = ya;
            final double nb = yb;
            double da2 = 2.0 + ya;
            double db2 = -(da2 - 2.0 - ya);
            double temp2 = da2 + yb;
            db2 += -(temp2 - da2 - yb);
            da2 = temp2;
            temp2 = da2 * 1.073741824E9;
            final double daa = da2 + temp2 - temp2;
            final double dab = da2 - daa;
            final double ratio = na / da2;
            temp2 = ratio * 1.073741824E9;
            final double ratioa = ratio + temp2 - temp2;
            double ratiob = ratio - ratioa;
            ratiob += (na - daa * ratioa - daa * ratiob - dab * ratioa - dab * ratiob) / da2;
            ratiob += nb / da2;
            ratiob += -db2 * na / da2 / da2;
            result = ratioa + ratiob;
        }
        if (negate) {
            result = -result;
        }
        return result;
    }
    
    public static double acosh(final double a) {
        return log(a + sqrt(a * a - 1.0));
    }
    
    public static double asinh(double a) {
        boolean negative = false;
        if (a < 0.0) {
            negative = true;
            a = -a;
        }
        double absAsinh;
        if (a > 0.167) {
            absAsinh = log(sqrt(a * a + 1.0) + a);
        }
        else {
            final double a2 = a * a;
            if (a > 0.097) {
                absAsinh = a * (1.0 - a2 * (0.3333333333333333 - a2 * (0.2 - a2 * (0.14285714285714285 - a2 * (0.1111111111111111 - a2 * (0.09090909090909091 - a2 * (0.07692307692307693 - a2 * (0.06666666666666667 - a2 * 0.058823529411764705 * 0.9375) * 0.9285714285714286) * 0.9166666666666666) * 0.9) * 0.875) * 0.8333333333333334) * 0.75) * 0.5);
            }
            else if (a > 0.036) {
                absAsinh = a * (1.0 - a2 * (0.3333333333333333 - a2 * (0.2 - a2 * (0.14285714285714285 - a2 * (0.1111111111111111 - a2 * (0.09090909090909091 - a2 * 0.07692307692307693 * 0.9166666666666666) * 0.9) * 0.875) * 0.8333333333333334) * 0.75) * 0.5);
            }
            else if (a > 0.0036) {
                absAsinh = a * (1.0 - a2 * (0.3333333333333333 - a2 * (0.2 - a2 * (0.14285714285714285 - a2 * 0.1111111111111111 * 0.875) * 0.8333333333333334) * 0.75) * 0.5);
            }
            else {
                absAsinh = a * (1.0 - a2 * (0.3333333333333333 - a2 * 0.2 * 0.75) * 0.5);
            }
        }
        return negative ? (-absAsinh) : absAsinh;
    }
    
    public static double atanh(double a) {
        boolean negative = false;
        if (a < 0.0) {
            negative = true;
            a = -a;
        }
        double absAtanh;
        if (a > 0.15) {
            absAtanh = 0.5 * log((1.0 + a) / (1.0 - a));
        }
        else {
            final double a2 = a * a;
            if (a > 0.087) {
                absAtanh = a * (1.0 + a2 * (0.3333333333333333 + a2 * (0.2 + a2 * (0.14285714285714285 + a2 * (0.1111111111111111 + a2 * (0.09090909090909091 + a2 * (0.07692307692307693 + a2 * (0.06666666666666667 + a2 * 0.058823529411764705))))))));
            }
            else if (a > 0.031) {
                absAtanh = a * (1.0 + a2 * (0.3333333333333333 + a2 * (0.2 + a2 * (0.14285714285714285 + a2 * (0.1111111111111111 + a2 * (0.09090909090909091 + a2 * 0.07692307692307693))))));
            }
            else if (a > 0.003) {
                absAtanh = a * (1.0 + a2 * (0.3333333333333333 + a2 * (0.2 + a2 * (0.14285714285714285 + a2 * 0.1111111111111111))));
            }
            else {
                absAtanh = a * (1.0 + a2 * (0.3333333333333333 + a2 * 0.2));
            }
        }
        return negative ? (-absAtanh) : absAtanh;
    }
    
    public static double signum(final double a) {
        return (a < 0.0) ? -1.0 : ((a > 0.0) ? 1.0 : a);
    }
    
    public static float signum(final float a) {
        return (a < 0.0f) ? -1.0f : ((a > 0.0f) ? 1.0f : a);
    }
    
    public static double nextUp(final double a) {
        return nextAfter(a, Double.POSITIVE_INFINITY);
    }
    
    public static float nextUp(final float a) {
        return nextAfter(a, Double.POSITIVE_INFINITY);
    }
    
    public static double nextDown(final double a) {
        return nextAfter(a, Double.NEGATIVE_INFINITY);
    }
    
    public static float nextDown(final float a) {
        return nextAfter(a, Double.NEGATIVE_INFINITY);
    }
    
    public static double random() {
        return Math.random();
    }
    
    public static double exp(final double x) {
        return exp(x, 0.0, null);
    }
    
    private static double exp(final double x, final double extra, final double[] hiPrec) {
        int intVal = (int)x;
        if (x < 0.0) {
            if (x < -746.0) {
                if (hiPrec != null) {
                    hiPrec[1] = (hiPrec[0] = 0.0);
                }
                return 0.0;
            }
            if (intVal < -709) {
                final double result = exp(x + 40.19140625, extra, hiPrec) / 2.85040095144011776E17;
                if (hiPrec != null) {
                    final int n = 0;
                    hiPrec[n] /= 2.85040095144011776E17;
                    final int n2 = 1;
                    hiPrec[n2] /= 2.85040095144011776E17;
                }
                return result;
            }
            if (intVal == -709) {
                final double result = exp(x + 1.494140625, extra, hiPrec) / 4.455505956692757;
                if (hiPrec != null) {
                    final int n3 = 0;
                    hiPrec[n3] /= 4.455505956692757;
                    final int n4 = 1;
                    hiPrec[n4] /= 4.455505956692757;
                }
                return result;
            }
            --intVal;
        }
        else if (intVal > 709) {
            if (hiPrec != null) {
                hiPrec[0] = Double.POSITIVE_INFINITY;
                hiPrec[1] = 0.0;
            }
            return Double.POSITIVE_INFINITY;
        }
        final double intPartA = ExpIntTable.EXP_INT_TABLE_A[750 + intVal];
        final double intPartB = ExpIntTable.EXP_INT_TABLE_B[750 + intVal];
        final int intFrac = (int)((x - intVal) * 1024.0);
        final double fracPartA = ExpFracTable.EXP_FRAC_TABLE_A[intFrac];
        final double fracPartB = ExpFracTable.EXP_FRAC_TABLE_B[intFrac];
        final double epsilon = x - (intVal + intFrac / 1024.0);
        double z = 0.04168701738764507;
        z = z * epsilon + 0.1666666505023083;
        z = z * epsilon + 0.5000000000042687;
        z = z * epsilon + 1.0;
        z = z * epsilon - 3.940510424527919E-20;
        final double tempA = intPartA * fracPartA;
        final double tempB = intPartA * fracPartB + intPartB * fracPartA + intPartB * fracPartB;
        final double tempC = tempB + tempA;
        if (tempC == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }
        double result2;
        if (extra != 0.0) {
            result2 = tempC * extra * z + tempC * extra + tempC * z + tempB + tempA;
        }
        else {
            result2 = tempC * z + tempB + tempA;
        }
        if (hiPrec != null) {
            hiPrec[0] = tempA;
            hiPrec[1] = tempC * extra * z + tempC * extra + tempC * z + tempB;
        }
        return result2;
    }
    
    public static double expm1(final double x) {
        return expm1(x, null);
    }
    
    private static double expm1(double x, final double[] hiPrecOut) {
        if (x != x || x == 0.0) {
            return x;
        }
        if (x > -1.0 && x < 1.0) {
            boolean negative = false;
            if (x < 0.0) {
                x = -x;
                negative = true;
            }
            final int intFrac = (int)(x * 1024.0);
            double tempA = ExpFracTable.EXP_FRAC_TABLE_A[intFrac] - 1.0;
            double tempB = ExpFracTable.EXP_FRAC_TABLE_B[intFrac];
            double temp = tempA + tempB;
            tempB = -(temp - tempA - tempB);
            tempA = temp;
            temp = tempA * 1.073741824E9;
            final double baseA = tempA + temp - temp;
            final double baseB = tempB + (tempA - baseA);
            final double epsilon = x - intFrac / 1024.0;
            double zb = 0.008336750013465571;
            zb = zb * epsilon + 0.041666663879186654;
            zb = zb * epsilon + 0.16666666666745392;
            zb = zb * epsilon + 0.49999999999999994;
            zb *= epsilon;
            zb *= epsilon;
            double za = epsilon;
            double temp2 = za + zb;
            zb = -(temp2 - za - zb);
            za = temp2;
            temp2 = za * 1.073741824E9;
            temp2 = za + temp2 - temp2;
            zb += za - temp2;
            za = temp2;
            double ya = za * baseA;
            temp2 = ya + za * baseB;
            double yb = -(temp2 - ya - za * baseB);
            ya = temp2;
            temp2 = ya + zb * baseA;
            yb += -(temp2 - ya - zb * baseA);
            ya = temp2;
            temp2 = ya + zb * baseB;
            yb += -(temp2 - ya - zb * baseB);
            ya = temp2;
            temp2 = ya + baseA;
            yb += -(temp2 - baseA - ya);
            ya = temp2;
            temp2 = ya + za;
            yb += -(temp2 - ya - za);
            ya = temp2;
            temp2 = ya + baseB;
            yb += -(temp2 - ya - baseB);
            ya = temp2;
            temp2 = ya + zb;
            yb += -(temp2 - ya - zb);
            ya = temp2;
            if (negative) {
                final double denom = 1.0 + ya;
                final double denomr = 1.0 / denom;
                final double denomb = -(denom - 1.0 - ya) + yb;
                final double ratio = ya * denomr;
                temp2 = ratio * 1.073741824E9;
                final double ra = ratio + temp2 - temp2;
                double rb = ratio - ra;
                temp2 = denom * 1.073741824E9;
                za = denom + temp2 - temp2;
                zb = denom - za;
                rb += (ya - za * ra - za * rb - zb * ra - zb * rb) * denomr;
                rb += yb * denomr;
                rb += -ya * denomb * denomr * denomr;
                ya = -ra;
                yb = -rb;
            }
            if (hiPrecOut != null) {
                hiPrecOut[0] = ya;
                hiPrecOut[1] = yb;
            }
            return ya + yb;
        }
        final double[] hiPrec = new double[2];
        exp(x, 0.0, hiPrec);
        if (x > 0.0) {
            return -1.0 + hiPrec[0] + hiPrec[1];
        }
        final double ra2 = -1.0 + hiPrec[0];
        double rb2 = -(ra2 + 1.0 - hiPrec[0]);
        rb2 += hiPrec[1];
        return ra2 + rb2;
    }
    
    public static double log(final double x) {
        return log(x, null);
    }
    
    private static double log(final double x, final double[] hiPrec) {
        if (x == 0.0) {
            return Double.NEGATIVE_INFINITY;
        }
        long bits = Double.doubleToRawLongBits(x);
        if (((bits & Long.MIN_VALUE) != 0x0L || x != x) && x != 0.0) {
            if (hiPrec != null) {
                hiPrec[0] = Double.NaN;
            }
            return Double.NaN;
        }
        if (x == Double.POSITIVE_INFINITY) {
            if (hiPrec != null) {
                hiPrec[0] = Double.POSITIVE_INFINITY;
            }
            return Double.POSITIVE_INFINITY;
        }
        int exp = (int)(bits >> 52) - 1023;
        if ((bits & 0x7FF0000000000000L) == 0x0L) {
            if (x == 0.0) {
                if (hiPrec != null) {
                    hiPrec[0] = Double.NEGATIVE_INFINITY;
                }
                return Double.NEGATIVE_INFINITY;
            }
            for (bits <<= 1; (bits & 0x10000000000000L) == 0x0L; bits <<= 1) {
                --exp;
            }
        }
        if ((exp == -1 || exp == 0) && x < 1.01 && x > 0.99 && hiPrec == null) {
            double xa = x - 1.0;
            double xb = xa - x + 1.0;
            double tmp = xa * 1.073741824E9;
            double aa = xa + tmp - tmp;
            double ab = xa - aa;
            xa = aa;
            xb = ab;
            final double[] lnCoef_last = FastMath.LN_QUICK_COEF[FastMath.LN_QUICK_COEF.length - 1];
            double ya = lnCoef_last[0];
            double yb = lnCoef_last[1];
            for (int i = FastMath.LN_QUICK_COEF.length - 2; i >= 0; --i) {
                aa = ya * xa;
                ab = ya * xb + yb * xa + yb * xb;
                tmp = aa * 1.073741824E9;
                ya = aa + tmp - tmp;
                yb = aa - ya + ab;
                final double[] lnCoef_i = FastMath.LN_QUICK_COEF[i];
                aa = ya + lnCoef_i[0];
                ab = yb + lnCoef_i[1];
                tmp = aa * 1.073741824E9;
                ya = aa + tmp - tmp;
                yb = aa - ya + ab;
            }
            aa = ya * xa;
            ab = ya * xb + yb * xa + yb * xb;
            tmp = aa * 1.073741824E9;
            ya = aa + tmp - tmp;
            yb = aa - ya + ab;
            return ya + yb;
        }
        final double[] lnm = lnMant.LN_MANT[(int)((bits & 0xFFC0000000000L) >> 42)];
        final double epsilon = (bits & 0x3FFFFFFFFFFL) / (4.503599627370496E15 + (bits & 0xFFC0000000000L));
        double lnza = 0.0;
        double lnzb = 0.0;
        if (hiPrec != null) {
            double tmp2 = epsilon * 1.073741824E9;
            double aa2 = epsilon + tmp2 - tmp2;
            double ab2 = epsilon - aa2;
            final double xa2 = aa2;
            double xb2 = ab2;
            final double numer = (double)(bits & 0x3FFFFFFFFFFL);
            final double denom = 4.503599627370496E15 + (bits & 0xFFC0000000000L);
            aa2 = numer - xa2 * denom - xb2 * denom;
            xb2 += aa2 / denom;
            final double[] lnCoef_last2 = FastMath.LN_HI_PREC_COEF[FastMath.LN_HI_PREC_COEF.length - 1];
            double ya2 = lnCoef_last2[0];
            double yb2 = lnCoef_last2[1];
            for (int j = FastMath.LN_HI_PREC_COEF.length - 2; j >= 0; --j) {
                aa2 = ya2 * xa2;
                ab2 = ya2 * xb2 + yb2 * xa2 + yb2 * xb2;
                tmp2 = aa2 * 1.073741824E9;
                ya2 = aa2 + tmp2 - tmp2;
                yb2 = aa2 - ya2 + ab2;
                final double[] lnCoef_i2 = FastMath.LN_HI_PREC_COEF[j];
                aa2 = ya2 + lnCoef_i2[0];
                ab2 = yb2 + lnCoef_i2[1];
                tmp2 = aa2 * 1.073741824E9;
                ya2 = aa2 + tmp2 - tmp2;
                yb2 = aa2 - ya2 + ab2;
            }
            aa2 = ya2 * xa2;
            ab2 = ya2 * xb2 + yb2 * xa2 + yb2 * xb2;
            lnza = aa2 + ab2;
            lnzb = -(lnza - aa2 - ab2);
        }
        else {
            lnza = -0.16624882440418567;
            lnza = lnza * epsilon + 0.19999954120254515;
            lnza = lnza * epsilon - 0.2499999997677497;
            lnza = lnza * epsilon + 0.3333333333332802;
            lnza = lnza * epsilon - 0.5;
            lnza = lnza * epsilon + 1.0;
            lnza *= epsilon;
        }
        double a = 0.6931470632553101 * exp;
        double b = 0.0;
        double c = a + lnm[0];
        double d = -(c - a - lnm[0]);
        a = c;
        b += d;
        c = a + lnza;
        d = -(c - a - lnza);
        a = c;
        b += d;
        c = a + 1.1730463525082348E-7 * exp;
        d = -(c - a - 1.1730463525082348E-7 * exp);
        a = c;
        b += d;
        c = a + lnm[1];
        d = -(c - a - lnm[1]);
        a = c;
        b += d;
        c = a + lnzb;
        d = -(c - a - lnzb);
        a = c;
        b += d;
        if (hiPrec != null) {
            hiPrec[0] = a;
            hiPrec[1] = b;
        }
        return a + b;
    }
    
    public static double log1p(final double x) {
        if (x == -1.0) {
            return Double.NEGATIVE_INFINITY;
        }
        if (x == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }
        if (x <= 1.0E-6 && x >= -1.0E-6) {
            final double y = (x * 0.3333333333333333 - 0.5) * x + 1.0;
            return y * x;
        }
        final double xpa = 1.0 + x;
        final double xpb = -(xpa - 1.0 - x);
        final double[] hiPrec = new double[2];
        final double lores = log(xpa, hiPrec);
        if (Double.isInfinite(lores)) {
            return lores;
        }
        final double fx1 = xpb / xpa;
        final double epsilon = 0.5 * fx1 + 1.0;
        return epsilon * fx1 + hiPrec[1] + hiPrec[0];
    }
    
    public static double log10(final double x) {
        final double[] hiPrec = new double[2];
        final double lores = log(x, hiPrec);
        if (Double.isInfinite(lores)) {
            return lores;
        }
        final double tmp = hiPrec[0] * 1.073741824E9;
        final double lna = hiPrec[0] + tmp - tmp;
        final double lnb = hiPrec[0] - lna + hiPrec[1];
        final double rln10a = 0.4342944622039795;
        final double rln10b = 1.9699272335463627E-8;
        return 1.9699272335463627E-8 * lnb + 1.9699272335463627E-8 * lna + 0.4342944622039795 * lnb + 0.4342944622039795 * lna;
    }
    
    public static double log(final double base, final double x) {
        return log(x) / log(base);
    }
    
    public static double pow(final double x, final double y) {
        if (y == 0.0) {
            return 1.0;
        }
        final long yBits = Double.doubleToRawLongBits(y);
        final int yRawExp = (int)((yBits & 0x7FF0000000000000L) >> 52);
        final long yRawMantissa = yBits & 0xFFFFFFFFFFFFFL;
        final long xBits = Double.doubleToRawLongBits(x);
        final int xRawExp = (int)((xBits & 0x7FF0000000000000L) >> 52);
        final long xRawMantissa = xBits & 0xFFFFFFFFFFFFFL;
        if (yRawExp > 1085) {
            if ((yRawExp == 2047 && yRawMantissa != 0L) || (xRawExp == 2047 && xRawMantissa != 0L)) {
                return Double.NaN;
            }
            if (xRawExp == 1023 && xRawMantissa == 0L) {
                if (yRawExp == 2047) {
                    return Double.NaN;
                }
                return 1.0;
            }
            else {
                if (y > 0.0 ^ xRawExp < 1023) {
                    return Double.POSITIVE_INFINITY;
                }
                return 0.0;
            }
        }
        else {
            if (yRawExp >= 1023) {
                final long yFullMantissa = 0x10000000000000L | yRawMantissa;
                if (yRawExp >= 1075) {
                    final long l = yFullMantissa << yRawExp - 1075;
                    return pow(x, (y < 0.0) ? (-l) : l);
                }
                final long integralMask = -1L << 1075 - yRawExp;
                if ((yFullMantissa & integralMask) == yFullMantissa) {
                    final long i = yFullMantissa >> 1075 - yRawExp;
                    return pow(x, (y < 0.0) ? (-i) : i);
                }
            }
            if (x == 0.0) {
                return (y < 0.0) ? Double.POSITIVE_INFINITY : 0.0;
            }
            if (xRawExp == 2047) {
                if (xRawMantissa == 0L) {
                    return (y < 0.0) ? 0.0 : Double.POSITIVE_INFINITY;
                }
                return Double.NaN;
            }
            else {
                if (x < 0.0) {
                    return Double.NaN;
                }
                final double tmp = y * 1.073741824E9;
                final double ya = y + tmp - tmp;
                final double yb = y - ya;
                final double[] lns = new double[2];
                final double lores = log(x, lns);
                if (Double.isInfinite(lores)) {
                    return lores;
                }
                double lna = lns[0];
                double lnb = lns[1];
                final double tmp2 = lna * 1.073741824E9;
                final double tmp3 = lna + tmp2 - tmp2;
                lnb += lna - tmp3;
                lna = tmp3;
                final double aa = lna * ya;
                final double ab = lna * yb + lnb * ya + lnb * yb;
                lna = aa + ab;
                lnb = -(lna - aa - ab);
                double z = 0.008333333333333333;
                z = z * lnb + 0.041666666666666664;
                z = z * lnb + 0.16666666666666666;
                z = z * lnb + 0.5;
                z = z * lnb + 1.0;
                z *= lnb;
                final double result = exp(lna, z, null);
                return result;
            }
        }
    }
    
    public static double pow(final double d, final int e) {
        return pow(d, (long)e);
    }
    
    public static double pow(final double d, final long e) {
        if (e == 0L) {
            return 1.0;
        }
        if (e > 0L) {
            return new Split(d).pow(e).full;
        }
        return new Split(d).reciprocal().pow(-e).full;
    }
    
    private static double polySine(final double x) {
        final double x2 = x * x;
        double p = 2.7553817452272217E-6;
        p = p * x2 - 1.9841269659586505E-4;
        p = p * x2 + 0.008333333333329196;
        p = p * x2 - 0.16666666666666666;
        p = p * x2 * x;
        return p;
    }
    
    private static double polyCosine(final double x) {
        final double x2 = x * x;
        double p = 2.479773539153719E-5;
        p = p * x2 - 0.0013888888689039883;
        p = p * x2 + 0.041666666666621166;
        p = p * x2 - 0.49999999999999994;
        p *= x2;
        return p;
    }
    
    private static double sinQ(final double xa, final double xb) {
        final int idx = (int)(xa * 8.0 + 0.5);
        final double epsilon = xa - FastMath.EIGHTHS[idx];
        final double sintA = FastMath.SINE_TABLE_A[idx];
        final double sintB = FastMath.SINE_TABLE_B[idx];
        final double costA = FastMath.COSINE_TABLE_A[idx];
        final double costB = FastMath.COSINE_TABLE_B[idx];
        double sinEpsA = epsilon;
        double sinEpsB = polySine(epsilon);
        final double cosEpsA = 1.0;
        final double cosEpsB = polyCosine(epsilon);
        final double temp = sinEpsA * 1.073741824E9;
        final double temp2 = sinEpsA + temp - temp;
        sinEpsB += sinEpsA - temp2;
        sinEpsA = temp2;
        double a = 0.0;
        double b = 0.0;
        double t = sintA;
        double c = a + t;
        double d = -(c - a - t);
        a = c;
        b += d;
        t = costA * sinEpsA;
        c = a + t;
        d = -(c - a - t);
        a = c;
        b += d;
        b = b + sintA * cosEpsB + costA * sinEpsB;
        b = b + sintB + costB * sinEpsA + sintB * cosEpsB + costB * sinEpsB;
        if (xb != 0.0) {
            t = ((costA + costB) * (1.0 + cosEpsB) - (sintA + sintB) * (sinEpsA + sinEpsB)) * xb;
            c = a + t;
            d = -(c - a - t);
            a = c;
            b += d;
        }
        final double result = a + b;
        return result;
    }
    
    private static double cosQ(final double xa, final double xb) {
        final double pi2a = 1.5707963267948966;
        final double pi2b = 6.123233995736766E-17;
        final double a = 1.5707963267948966 - xa;
        double b = -(a - 1.5707963267948966 + xa);
        b += 6.123233995736766E-17 - xb;
        return sinQ(a, b);
    }
    
    private static double tanQ(final double xa, final double xb, final boolean cotanFlag) {
        final int idx = (int)(xa * 8.0 + 0.5);
        final double epsilon = xa - FastMath.EIGHTHS[idx];
        final double sintA = FastMath.SINE_TABLE_A[idx];
        final double sintB = FastMath.SINE_TABLE_B[idx];
        final double costA = FastMath.COSINE_TABLE_A[idx];
        final double costB = FastMath.COSINE_TABLE_B[idx];
        double sinEpsA = epsilon;
        double sinEpsB = polySine(epsilon);
        final double cosEpsA = 1.0;
        final double cosEpsB = polyCosine(epsilon);
        double temp = sinEpsA * 1.073741824E9;
        final double temp2 = sinEpsA + temp - temp;
        sinEpsB += sinEpsA - temp2;
        sinEpsA = temp2;
        double a = 0.0;
        double b = 0.0;
        double t = sintA;
        double c = a + t;
        double d = -(c - a - t);
        a = c;
        b += d;
        t = costA * sinEpsA;
        c = a + t;
        d = -(c - a - t);
        a = c;
        b += d;
        b += sintA * cosEpsB + costA * sinEpsB;
        b += sintB + costB * sinEpsA + sintB * cosEpsB + costB * sinEpsB;
        double sina = a + b;
        double sinb = -(sina - a - b);
        b = (a = (c = (d = 0.0)));
        t = costA * 1.0;
        c = a + t;
        d = -(c - a - t);
        a = c;
        b += d;
        t = -sintA * sinEpsA;
        c = a + t;
        d = -(c - a - t);
        a = c;
        b += d;
        b += costB * 1.0 + costA * cosEpsB + costB * cosEpsB;
        b -= sintB * sinEpsA + sintA * sinEpsB + sintB * sinEpsB;
        double cosa = a + b;
        double cosb = -(cosa - a - b);
        if (cotanFlag) {
            double tmp = cosa;
            cosa = sina;
            sina = tmp;
            tmp = cosb;
            cosb = sinb;
            sinb = tmp;
        }
        final double est = sina / cosa;
        temp = est * 1.073741824E9;
        final double esta = est + temp - temp;
        final double estb = est - esta;
        temp = cosa * 1.073741824E9;
        final double cosaa = cosa + temp - temp;
        final double cosab = cosa - cosaa;
        double err = (sina - esta * cosaa - esta * cosab - estb * cosaa - estb * cosab) / cosa;
        err += sinb / cosa;
        err += -sina * cosb / cosa / cosa;
        if (xb != 0.0) {
            double xbadj = xb + est * est * xb;
            if (cotanFlag) {
                xbadj = -xbadj;
            }
            err += xbadj;
        }
        return est + err;
    }
    
    private static void reducePayneHanek(final double x, final double[] result) {
        long inbits = Double.doubleToRawLongBits(x);
        int exponent = (int)(inbits >> 52 & 0x7FFL) - 1023;
        inbits &= 0xFFFFFFFFFFFFFL;
        inbits |= 0x10000000000000L;
        ++exponent;
        inbits <<= 11;
        final int idx = exponent >> 6;
        final int shift = exponent - (idx << 6);
        long shpi0;
        long shpiA;
        long shpiB;
        if (shift != 0) {
            shpi0 = ((idx == 0) ? 0L : (FastMath.RECIP_2PI[idx - 1] << shift));
            shpi0 |= FastMath.RECIP_2PI[idx] >>> 64 - shift;
            shpiA = (FastMath.RECIP_2PI[idx] << shift | FastMath.RECIP_2PI[idx + 1] >>> 64 - shift);
            shpiB = (FastMath.RECIP_2PI[idx + 1] << shift | FastMath.RECIP_2PI[idx + 2] >>> 64 - shift);
        }
        else {
            shpi0 = ((idx == 0) ? 0L : FastMath.RECIP_2PI[idx - 1]);
            shpiA = FastMath.RECIP_2PI[idx];
            shpiB = FastMath.RECIP_2PI[idx + 1];
        }
        long a = inbits >>> 32;
        long b = inbits & 0xFFFFFFFFL;
        long c = shpiA >>> 32;
        long d = shpiA & 0xFFFFFFFFL;
        long ac = a * c;
        long bd = b * d;
        long bc = b * c;
        long ad = a * d;
        long prodB = bd + (ad << 32);
        long prodA = ac + (ad >>> 32);
        boolean bita = (bd & Long.MIN_VALUE) != 0x0L;
        boolean bitb = (ad & 0x80000000L) != 0x0L;
        boolean bitsum = (prodB & Long.MIN_VALUE) != 0x0L;
        if ((bita && bitb) || ((bita || bitb) && !bitsum)) {
            ++prodA;
        }
        bita = ((prodB & Long.MIN_VALUE) != 0x0L);
        bitb = ((bc & 0x80000000L) != 0x0L);
        prodB += bc << 32;
        prodA += bc >>> 32;
        bitsum = ((prodB & Long.MIN_VALUE) != 0x0L);
        if ((bita && bitb) || ((bita || bitb) && !bitsum)) {
            ++prodA;
        }
        c = shpiB >>> 32;
        d = (shpiB & 0xFFFFFFFFL);
        ac = a * c;
        bc = b * c;
        ad = a * d;
        ac += bc + ad >>> 32;
        bita = ((prodB & Long.MIN_VALUE) != 0x0L);
        bitb = ((ac & Long.MIN_VALUE) != 0x0L);
        prodB += ac;
        bitsum = ((prodB & Long.MIN_VALUE) != 0x0L);
        if ((bita && bitb) || ((bita || bitb) && !bitsum)) {
            ++prodA;
        }
        c = shpi0 >>> 32;
        d = (shpi0 & 0xFFFFFFFFL);
        bd = b * d;
        bc = b * c;
        ad = a * d;
        prodA += bd + (bc + ad << 32);
        final int intPart = (int)(prodA >>> 62);
        prodA <<= 2;
        prodA |= prodB >>> 62;
        prodB <<= 2;
        a = prodA >>> 32;
        b = (prodA & 0xFFFFFFFFL);
        c = FastMath.PI_O_4_BITS[0] >>> 32;
        d = (FastMath.PI_O_4_BITS[0] & 0xFFFFFFFFL);
        ac = a * c;
        bd = b * d;
        bc = b * c;
        ad = a * d;
        long prod2B = bd + (ad << 32);
        long prod2A = ac + (ad >>> 32);
        bita = ((bd & Long.MIN_VALUE) != 0x0L);
        bitb = ((ad & 0x80000000L) != 0x0L);
        bitsum = ((prod2B & Long.MIN_VALUE) != 0x0L);
        if ((bita && bitb) || ((bita || bitb) && !bitsum)) {
            ++prod2A;
        }
        bita = ((prod2B & Long.MIN_VALUE) != 0x0L);
        bitb = ((bc & 0x80000000L) != 0x0L);
        prod2B += bc << 32;
        prod2A += bc >>> 32;
        bitsum = ((prod2B & Long.MIN_VALUE) != 0x0L);
        if ((bita && bitb) || ((bita || bitb) && !bitsum)) {
            ++prod2A;
        }
        c = FastMath.PI_O_4_BITS[1] >>> 32;
        d = (FastMath.PI_O_4_BITS[1] & 0xFFFFFFFFL);
        ac = a * c;
        bc = b * c;
        ad = a * d;
        ac += bc + ad >>> 32;
        bita = ((prod2B & Long.MIN_VALUE) != 0x0L);
        bitb = ((ac & Long.MIN_VALUE) != 0x0L);
        prod2B += ac;
        bitsum = ((prod2B & Long.MIN_VALUE) != 0x0L);
        if ((bita && bitb) || ((bita || bitb) && !bitsum)) {
            ++prod2A;
        }
        a = prodB >>> 32;
        b = (prodB & 0xFFFFFFFFL);
        c = FastMath.PI_O_4_BITS[0] >>> 32;
        d = (FastMath.PI_O_4_BITS[0] & 0xFFFFFFFFL);
        ac = a * c;
        bc = b * c;
        ad = a * d;
        ac += bc + ad >>> 32;
        bita = ((prod2B & Long.MIN_VALUE) != 0x0L);
        bitb = ((ac & Long.MIN_VALUE) != 0x0L);
        prod2B += ac;
        bitsum = ((prod2B & Long.MIN_VALUE) != 0x0L);
        if ((bita && bitb) || ((bita || bitb) && !bitsum)) {
            ++prod2A;
        }
        final double tmpA = (prod2A >>> 12) / 4.503599627370496E15;
        final double tmpB = (((prod2A & 0xFFFL) << 40) + (prod2B >>> 24)) / 4.503599627370496E15 / 4.503599627370496E15;
        final double sumA = tmpA + tmpB;
        final double sumB = -(sumA - tmpA - tmpB);
        result[0] = intPart;
        result[1] = sumA * 2.0;
        result[2] = sumB * 2.0;
    }
    
    public static double sin(final double x) {
        boolean negative = false;
        int quadrant = 0;
        double xb = 0.0;
        double xa = x;
        if (x < 0.0) {
            negative = true;
            xa = -xa;
        }
        if (xa == 0.0) {
            final long bits = Double.doubleToRawLongBits(x);
            if (bits < 0L) {
                return -0.0;
            }
            return 0.0;
        }
        else {
            if (xa != xa || xa == Double.POSITIVE_INFINITY) {
                return Double.NaN;
            }
            if (xa > 3294198.0) {
                final double[] reduceResults = new double[3];
                reducePayneHanek(xa, reduceResults);
                quadrant = ((int)reduceResults[0] & 0x3);
                xa = reduceResults[1];
                xb = reduceResults[2];
            }
            else if (xa > 1.5707963267948966) {
                final CodyWaite cw = new CodyWaite(xa);
                quadrant = (cw.getK() & 0x3);
                xa = cw.getRemA();
                xb = cw.getRemB();
            }
            if (negative) {
                quadrant ^= 0x2;
            }
            switch (quadrant) {
                case 0: {
                    return sinQ(xa, xb);
                }
                case 1: {
                    return cosQ(xa, xb);
                }
                case 2: {
                    return -sinQ(xa, xb);
                }
                case 3: {
                    return -cosQ(xa, xb);
                }
                default: {
                    return Double.NaN;
                }
            }
        }
    }
    
    public static double cos(final double x) {
        int quadrant = 0;
        double xa = x;
        if (x < 0.0) {
            xa = -xa;
        }
        if (xa != xa || xa == Double.POSITIVE_INFINITY) {
            return Double.NaN;
        }
        double xb = 0.0;
        if (xa > 3294198.0) {
            final double[] reduceResults = new double[3];
            reducePayneHanek(xa, reduceResults);
            quadrant = ((int)reduceResults[0] & 0x3);
            xa = reduceResults[1];
            xb = reduceResults[2];
        }
        else if (xa > 1.5707963267948966) {
            final CodyWaite cw = new CodyWaite(xa);
            quadrant = (cw.getK() & 0x3);
            xa = cw.getRemA();
            xb = cw.getRemB();
        }
        switch (quadrant) {
            case 0: {
                return cosQ(xa, xb);
            }
            case 1: {
                return -sinQ(xa, xb);
            }
            case 2: {
                return -cosQ(xa, xb);
            }
            case 3: {
                return sinQ(xa, xb);
            }
            default: {
                return Double.NaN;
            }
        }
    }
    
    public static double tan(final double x) {
        boolean negative = false;
        int quadrant = 0;
        double xa = x;
        if (x < 0.0) {
            negative = true;
            xa = -xa;
        }
        if (xa == 0.0) {
            final long bits = Double.doubleToRawLongBits(x);
            if (bits < 0L) {
                return -0.0;
            }
            return 0.0;
        }
        else {
            if (xa != xa || xa == Double.POSITIVE_INFINITY) {
                return Double.NaN;
            }
            double xb = 0.0;
            if (xa > 3294198.0) {
                final double[] reduceResults = new double[3];
                reducePayneHanek(xa, reduceResults);
                quadrant = ((int)reduceResults[0] & 0x3);
                xa = reduceResults[1];
                xb = reduceResults[2];
            }
            else if (xa > 1.5707963267948966) {
                final CodyWaite cw = new CodyWaite(xa);
                quadrant = (cw.getK() & 0x3);
                xa = cw.getRemA();
                xb = cw.getRemB();
            }
            if (xa > 1.5) {
                final double pi2a = 1.5707963267948966;
                final double pi2b = 6.123233995736766E-17;
                final double a = 1.5707963267948966 - xa;
                double b = -(a - 1.5707963267948966 + xa);
                b += 6.123233995736766E-17 - xb;
                xa = a + b;
                xb = -(xa - a - b);
                quadrant ^= 0x1;
                negative ^= true;
            }
            double result;
            if ((quadrant & 0x1) == 0x0) {
                result = tanQ(xa, xb, false);
            }
            else {
                result = -tanQ(xa, xb, true);
            }
            if (negative) {
                result = -result;
            }
            return result;
        }
    }
    
    public static double atan(final double x) {
        return atan(x, 0.0, false);
    }
    
    private static double atan(double xa, double xb, final boolean leftPlane) {
        if (xa == 0.0) {
            return leftPlane ? copySign(3.141592653589793, xa) : xa;
        }
        boolean negate;
        if (xa < 0.0) {
            xa = -xa;
            xb = -xb;
            negate = true;
        }
        else {
            negate = false;
        }
        if (xa > 1.633123935319537E16) {
            return (negate ^ leftPlane) ? -1.5707963267948966 : 1.5707963267948966;
        }
        int idx;
        if (xa < 1.0) {
            idx = (int)((-1.7168146928204135 * xa * xa + 8.0) * xa + 0.5);
        }
        else {
            final double oneOverXa = 1.0 / xa;
            idx = (int)(-((-1.7168146928204135 * oneOverXa * oneOverXa + 8.0) * oneOverXa) + 13.07);
        }
        final double ttA = FastMath.TANGENT_TABLE_A[idx];
        final double ttB = FastMath.TANGENT_TABLE_B[idx];
        double epsA = xa - ttA;
        double epsB = -(epsA - xa + ttA);
        epsB += xb - ttB;
        double temp = epsA + epsB;
        epsB = -(temp - epsA - epsB);
        epsA = temp;
        temp = xa * 1.073741824E9;
        double ya = xa + temp - temp;
        double yb = xb + xa - ya;
        xa = ya;
        xb += yb;
        if (idx == 0) {
            final double denom = 1.0 / (1.0 + (xa + xb) * (ttA + ttB));
            ya = epsA * denom;
            yb = epsB * denom;
        }
        else {
            double temp2 = xa * ttA;
            double za = 1.0 + temp2;
            double zb = -(za - 1.0 - temp2);
            temp2 = xb * ttA + xa * ttB;
            temp = za + temp2;
            zb += -(temp - za - temp2);
            za = temp;
            zb += xb * ttB;
            ya = epsA / za;
            temp = ya * 1.073741824E9;
            final double yaa = ya + temp - temp;
            final double yab = ya - yaa;
            temp = za * 1.073741824E9;
            final double zaa = za + temp - temp;
            final double zab = za - zaa;
            yb = (epsA - yaa * zaa - yaa * zab - yab * zaa - yab * zab) / za;
            yb += -epsA * zb / za / za;
            yb += epsB / za;
        }
        epsA = ya;
        epsB = yb;
        final double epsA2 = epsA * epsA;
        yb = 0.07490822288864472;
        yb = yb * epsA2 - 0.09088450866185192;
        yb = yb * epsA2 + 0.11111095942313305;
        yb = yb * epsA2 - 0.1428571423679182;
        yb = yb * epsA2 + 0.19999999999923582;
        yb = yb * epsA2 - 0.33333333333333287;
        yb = yb * epsA2 * epsA;
        ya = epsA;
        temp = ya + yb;
        yb = -(temp - ya - yb);
        ya = temp;
        yb += epsB / (1.0 + epsA * epsA);
        final double eighths = FastMath.EIGHTHS[idx];
        double za2 = eighths + ya;
        double zb2 = -(za2 - eighths - ya);
        temp = za2 + yb;
        zb2 += -(temp - za2 - yb);
        za2 = temp;
        double result = za2 + zb2;
        if (leftPlane) {
            final double resultb = -(result - za2 - zb2);
            final double pia = 3.141592653589793;
            final double pib = 1.2246467991473532E-16;
            za2 = 3.141592653589793 - result;
            zb2 = -(za2 - 3.141592653589793 + result);
            zb2 += 1.2246467991473532E-16 - resultb;
            result = za2 + zb2;
        }
        if (negate ^ leftPlane) {
            result = -result;
        }
        return result;
    }
    
    public static double atan2(final double y, final double x) {
        if (x != x || y != y) {
            return Double.NaN;
        }
        if (y == 0.0) {
            final double result = x * y;
            final double invx = 1.0 / x;
            final double invy = 1.0 / y;
            if (invx == 0.0) {
                if (x > 0.0) {
                    return y;
                }
                return copySign(3.141592653589793, y);
            }
            else {
                if (x >= 0.0 && invx >= 0.0) {
                    return result;
                }
                if (y < 0.0 || invy < 0.0) {
                    return -3.141592653589793;
                }
                return 3.141592653589793;
            }
        }
        else if (y == Double.POSITIVE_INFINITY) {
            if (x == Double.POSITIVE_INFINITY) {
                return 0.7853981633974483;
            }
            if (x == Double.NEGATIVE_INFINITY) {
                return 2.356194490192345;
            }
            return 1.5707963267948966;
        }
        else if (y == Double.NEGATIVE_INFINITY) {
            if (x == Double.POSITIVE_INFINITY) {
                return -0.7853981633974483;
            }
            if (x == Double.NEGATIVE_INFINITY) {
                return -2.356194490192345;
            }
            return -1.5707963267948966;
        }
        else {
            if (x == Double.POSITIVE_INFINITY) {
                if (y > 0.0 || 1.0 / y > 0.0) {
                    return 0.0;
                }
                if (y < 0.0 || 1.0 / y < 0.0) {
                    return -0.0;
                }
            }
            if (x == Double.NEGATIVE_INFINITY) {
                if (y > 0.0 || 1.0 / y > 0.0) {
                    return 3.141592653589793;
                }
                if (y < 0.0 || 1.0 / y < 0.0) {
                    return -3.141592653589793;
                }
            }
            if (x == 0.0) {
                if (y > 0.0 || 1.0 / y > 0.0) {
                    return 1.5707963267948966;
                }
                if (y < 0.0 || 1.0 / y < 0.0) {
                    return -1.5707963267948966;
                }
            }
            final double r = y / x;
            if (Double.isInfinite(r)) {
                return atan(r, 0.0, x < 0.0);
            }
            double ra = doubleHighPart(r);
            double rb = r - ra;
            final double xa = doubleHighPart(x);
            final double xb = x - xa;
            rb += (y - ra * xa - ra * xb - rb * xa - rb * xb) / x;
            final double temp = ra + rb;
            rb = -(temp - ra - rb);
            ra = temp;
            if (ra == 0.0) {
                ra = copySign(0.0, y);
            }
            final double result2 = atan(ra, rb, x < 0.0);
            return result2;
        }
    }
    
    public static double asin(final double x) {
        if (x != x) {
            return Double.NaN;
        }
        if (x > 1.0 || x < -1.0) {
            return Double.NaN;
        }
        if (x == 1.0) {
            return 1.5707963267948966;
        }
        if (x == -1.0) {
            return -1.5707963267948966;
        }
        if (x == 0.0) {
            return x;
        }
        double temp = x * 1.073741824E9;
        final double xa = x + temp - temp;
        final double xb = x - xa;
        double ya = xa * xa;
        double yb = xa * xb * 2.0 + xb * xb;
        ya = -ya;
        yb = -yb;
        double za = 1.0 + ya;
        double zb = -(za - 1.0 - ya);
        temp = za + yb;
        zb += -(temp - za - yb);
        za = temp;
        final double y = sqrt(za);
        temp = y * 1.073741824E9;
        ya = y + temp - temp;
        yb = y - ya;
        yb += (za - ya * ya - 2.0 * ya * yb - yb * yb) / (2.0 * y);
        final double dx = zb / (2.0 * y);
        final double r = x / y;
        temp = r * 1.073741824E9;
        double ra = r + temp - temp;
        double rb = r - ra;
        rb += (x - ra * ya - ra * yb - rb * ya - rb * yb) / y;
        rb += -x * dx / y / y;
        temp = ra + rb;
        rb = -(temp - ra - rb);
        ra = temp;
        return atan(ra, rb, false);
    }
    
    public static double acos(final double x) {
        if (x != x) {
            return Double.NaN;
        }
        if (x > 1.0 || x < -1.0) {
            return Double.NaN;
        }
        if (x == -1.0) {
            return 3.141592653589793;
        }
        if (x == 1.0) {
            return 0.0;
        }
        if (x == 0.0) {
            return 1.5707963267948966;
        }
        double temp = x * 1.073741824E9;
        final double xa = x + temp - temp;
        final double xb = x - xa;
        double ya = xa * xa;
        double yb = xa * xb * 2.0 + xb * xb;
        ya = -ya;
        yb = -yb;
        double za = 1.0 + ya;
        double zb = -(za - 1.0 - ya);
        temp = za + yb;
        zb += -(temp - za - yb);
        za = temp;
        double y = sqrt(za);
        temp = y * 1.073741824E9;
        ya = y + temp - temp;
        yb = y - ya;
        yb += (za - ya * ya - 2.0 * ya * yb - yb * yb) / (2.0 * y);
        yb += zb / (2.0 * y);
        y = ya + yb;
        yb = -(y - ya - yb);
        final double r = y / x;
        if (Double.isInfinite(r)) {
            return 1.5707963267948966;
        }
        double ra = doubleHighPart(r);
        double rb = r - ra;
        rb += (y - ra * xa - ra * xb - rb * xa - rb * xb) / x;
        rb += yb / x;
        temp = ra + rb;
        rb = -(temp - ra - rb);
        ra = temp;
        return atan(ra, rb, x < 0.0);
    }
    
    public static double cbrt(double x) {
        long inbits = Double.doubleToRawLongBits(x);
        int exponent = (int)(inbits >> 52 & 0x7FFL) - 1023;
        boolean subnormal = false;
        if (exponent == -1023) {
            if (x == 0.0) {
                return x;
            }
            subnormal = true;
            x *= 1.8014398509481984E16;
            inbits = Double.doubleToRawLongBits(x);
            exponent = (int)(inbits >> 52 & 0x7FFL) - 1023;
        }
        if (exponent == 1024) {
            return x;
        }
        final int exp3 = exponent / 3;
        final double p2 = Double.longBitsToDouble((inbits & Long.MIN_VALUE) | (long)(exp3 + 1023 & 0x7FF) << 52);
        final double mant = Double.longBitsToDouble((inbits & 0xFFFFFFFFFFFFFL) | 0x3FF0000000000000L);
        double est = -0.010714690733195933;
        est = est * mant + 0.0875862700108075;
        est = est * mant - 0.3058015757857271;
        est = est * mant + 0.7249995199969751;
        est = est * mant + 0.5039018405998233;
        est *= FastMath.CBRTTWO[exponent % 3 + 2];
        final double xs = x / (p2 * p2 * p2);
        est += (xs - est * est * est) / (3.0 * est * est);
        est += (xs - est * est * est) / (3.0 * est * est);
        double temp = est * 1.073741824E9;
        final double ya = est + temp - temp;
        final double yb = est - ya;
        double za = ya * ya;
        double zb = ya * yb * 2.0 + yb * yb;
        temp = za * 1.073741824E9;
        final double temp2 = za + temp - temp;
        zb += za - temp2;
        za = temp2;
        zb = za * yb + ya * zb + zb * yb;
        za *= ya;
        final double na = xs - za;
        double nb = -(na - xs + za);
        nb -= zb;
        est += (na + nb) / (3.0 * est * est);
        est *= p2;
        if (subnormal) {
            est *= 3.814697265625E-6;
        }
        return est;
    }
    
    public static double toRadians(final double x) {
        if (Double.isInfinite(x) || x == 0.0) {
            return x;
        }
        final double facta = 0.01745329052209854;
        final double factb = 1.997844754509471E-9;
        final double xa = doubleHighPart(x);
        final double xb = x - xa;
        double result = xb * 1.997844754509471E-9 + xb * 0.01745329052209854 + xa * 1.997844754509471E-9 + xa * 0.01745329052209854;
        if (result == 0.0) {
            result *= x;
        }
        return result;
    }
    
    public static double toDegrees(final double x) {
        if (Double.isInfinite(x) || x == 0.0) {
            return x;
        }
        final double facta = 57.2957763671875;
        final double factb = 3.145894820876798E-6;
        final double xa = doubleHighPart(x);
        final double xb = x - xa;
        return xb * 3.145894820876798E-6 + xb * 57.2957763671875 + xa * 3.145894820876798E-6 + xa * 57.2957763671875;
    }
    
    public static int abs(final int x) {
        final int i = x >>> 31;
        return (x ^ ~i + 1) + i;
    }
    
    public static long abs(final long x) {
        final long l = x >>> 63;
        return (x ^ ~l + 1L) + l;
    }
    
    public static float abs(final float x) {
        return Float.intBitsToFloat(Integer.MAX_VALUE & Float.floatToRawIntBits(x));
    }
    
    public static double abs(final double x) {
        return Double.longBitsToDouble(Long.MAX_VALUE & Double.doubleToRawLongBits(x));
    }
    
    public static double ulp(final double x) {
        if (Double.isInfinite(x)) {
            return Double.POSITIVE_INFINITY;
        }
        return abs(x - Double.longBitsToDouble(Double.doubleToRawLongBits(x) ^ 0x1L));
    }
    
    public static float ulp(final float x) {
        if (Float.isInfinite(x)) {
            return Float.POSITIVE_INFINITY;
        }
        return abs(x - Float.intBitsToFloat(Float.floatToIntBits(x) ^ 0x1));
    }
    
    public static double scalb(final double d, final int n) {
        if (n > -1023 && n < 1024) {
            return d * Double.longBitsToDouble((long)(n + 1023) << 52);
        }
        if (Double.isNaN(d) || Double.isInfinite(d) || d == 0.0) {
            return d;
        }
        if (n < -2098) {
            return (d > 0.0) ? 0.0 : -0.0;
        }
        if (n > 2097) {
            return (d > 0.0) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
        final long bits = Double.doubleToRawLongBits(d);
        final long sign = bits & Long.MIN_VALUE;
        final int exponent = (int)(bits >>> 52) & 0x7FF;
        long mantissa = bits & 0xFFFFFFFFFFFFFL;
        int scaledExponent = exponent + n;
        if (n < 0) {
            if (scaledExponent > 0) {
                return Double.longBitsToDouble(sign | (long)scaledExponent << 52 | mantissa);
            }
            if (scaledExponent > -53) {
                mantissa |= 0x10000000000000L;
                final long mostSignificantLostBit = mantissa & 1L << -scaledExponent;
                mantissa >>>= 1 - scaledExponent;
                if (mostSignificantLostBit != 0L) {
                    ++mantissa;
                }
                return Double.longBitsToDouble(sign | mantissa);
            }
            return (sign == 0L) ? 0.0 : -0.0;
        }
        else if (exponent == 0) {
            while (mantissa >>> 52 != 1L) {
                mantissa <<= 1;
                --scaledExponent;
            }
            ++scaledExponent;
            mantissa &= 0xFFFFFFFFFFFFFL;
            if (scaledExponent < 2047) {
                return Double.longBitsToDouble(sign | (long)scaledExponent << 52 | mantissa);
            }
            return (sign == 0L) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
        else {
            if (scaledExponent < 2047) {
                return Double.longBitsToDouble(sign | (long)scaledExponent << 52 | mantissa);
            }
            return (sign == 0L) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
        }
    }
    
    public static float scalb(final float f, final int n) {
        if (n > -127 && n < 128) {
            return f * Float.intBitsToFloat(n + 127 << 23);
        }
        if (Float.isNaN(f) || Float.isInfinite(f) || f == 0.0f) {
            return f;
        }
        if (n < -277) {
            return (f > 0.0f) ? 0.0f : -0.0f;
        }
        if (n > 276) {
            return (f > 0.0f) ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        }
        final int bits = Float.floatToIntBits(f);
        final int sign = bits & Integer.MIN_VALUE;
        final int exponent = bits >>> 23 & 0xFF;
        int mantissa = bits & 0x7FFFFF;
        int scaledExponent = exponent + n;
        if (n < 0) {
            if (scaledExponent > 0) {
                return Float.intBitsToFloat(sign | scaledExponent << 23 | mantissa);
            }
            if (scaledExponent > -24) {
                mantissa |= 0x800000;
                final int mostSignificantLostBit = mantissa & 1 << -scaledExponent;
                mantissa >>>= 1 - scaledExponent;
                if (mostSignificantLostBit != 0) {
                    ++mantissa;
                }
                return Float.intBitsToFloat(sign | mantissa);
            }
            return (sign == 0) ? 0.0f : -0.0f;
        }
        else if (exponent == 0) {
            while (mantissa >>> 23 != 1) {
                mantissa <<= 1;
                --scaledExponent;
            }
            ++scaledExponent;
            mantissa &= 0x7FFFFF;
            if (scaledExponent < 255) {
                return Float.intBitsToFloat(sign | scaledExponent << 23 | mantissa);
            }
            return (sign == 0) ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        }
        else {
            if (scaledExponent < 255) {
                return Float.intBitsToFloat(sign | scaledExponent << 23 | mantissa);
            }
            return (sign == 0) ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
        }
    }
    
    public static double nextAfter(final double d, final double direction) {
        if (Double.isNaN(d) || Double.isNaN(direction)) {
            return Double.NaN;
        }
        if (d == direction) {
            return direction;
        }
        if (Double.isInfinite(d)) {
            return (d < 0.0) ? -1.7976931348623157E308 : Double.MAX_VALUE;
        }
        if (d == 0.0) {
            return (direction < 0.0) ? -4.9E-324 : Double.MIN_VALUE;
        }
        final long bits = Double.doubleToRawLongBits(d);
        final long sign = bits & Long.MIN_VALUE;
        if (direction < d ^ sign == 0L) {
            return Double.longBitsToDouble(sign | (bits & Long.MAX_VALUE) + 1L);
        }
        return Double.longBitsToDouble(sign | (bits & Long.MAX_VALUE) - 1L);
    }
    
    public static float nextAfter(final float f, final double direction) {
        if (Double.isNaN(f) || Double.isNaN(direction)) {
            return Float.NaN;
        }
        if (f == direction) {
            return (float)direction;
        }
        if (Float.isInfinite(f)) {
            return (f < 0.0f) ? -3.4028235E38f : Float.MAX_VALUE;
        }
        if (f == 0.0f) {
            return (direction < 0.0) ? -1.4E-45f : Float.MIN_VALUE;
        }
        final int bits = Float.floatToIntBits(f);
        final int sign = bits & Integer.MIN_VALUE;
        if (direction < f ^ sign == 0) {
            return Float.intBitsToFloat(sign | (bits & Integer.MAX_VALUE) + 1);
        }
        return Float.intBitsToFloat(sign | (bits & Integer.MAX_VALUE) - 1);
    }
    
    public static double floor(final double x) {
        if (x != x) {
            return x;
        }
        if (x >= 4.503599627370496E15 || x <= -4.503599627370496E15) {
            return x;
        }
        long y = (long)x;
        if (x < 0.0 && y != x) {
            --y;
        }
        if (y == 0L) {
            return x * y;
        }
        return (double)y;
    }
    
    public static double ceil(final double x) {
        if (x != x) {
            return x;
        }
        double y = floor(x);
        if (y == x) {
            return y;
        }
        ++y;
        if (y == 0.0) {
            return x * y;
        }
        return y;
    }
    
    public static double rint(final double x) {
        final double y = floor(x);
        final double d = x - y;
        if (d > 0.5) {
            if (y == -1.0) {
                return -0.0;
            }
            return y + 1.0;
        }
        else {
            if (d < 0.5) {
                return y;
            }
            final long z = (long)y;
            return ((z & 0x1L) == 0x0L) ? y : (y + 1.0);
        }
    }
    
    public static long round(final double x) {
        return (long)floor(x + 0.5);
    }
    
    public static int round(final float x) {
        return (int)floor(x + 0.5f);
    }
    
    public static int min(final int a, final int b) {
        return (a <= b) ? a : b;
    }
    
    public static long min(final long a, final long b) {
        return (a <= b) ? a : b;
    }
    
    public static float min(final float a, final float b) {
        if (a > b) {
            return b;
        }
        if (a < b) {
            return a;
        }
        if (a != b) {
            return Float.NaN;
        }
        final int bits = Float.floatToRawIntBits(a);
        if (bits == Integer.MIN_VALUE) {
            return a;
        }
        return b;
    }
    
    public static double min(final double a, final double b) {
        if (a > b) {
            return b;
        }
        if (a < b) {
            return a;
        }
        if (a != b) {
            return Double.NaN;
        }
        final long bits = Double.doubleToRawLongBits(a);
        if (bits == Long.MIN_VALUE) {
            return a;
        }
        return b;
    }
    
    public static int max(final int a, final int b) {
        return (a <= b) ? b : a;
    }
    
    public static long max(final long a, final long b) {
        return (a <= b) ? b : a;
    }
    
    public static float max(final float a, final float b) {
        if (a > b) {
            return a;
        }
        if (a < b) {
            return b;
        }
        if (a != b) {
            return Float.NaN;
        }
        final int bits = Float.floatToRawIntBits(a);
        if (bits == Integer.MIN_VALUE) {
            return b;
        }
        return a;
    }
    
    public static double max(final double a, final double b) {
        if (a > b) {
            return a;
        }
        if (a < b) {
            return b;
        }
        if (a != b) {
            return Double.NaN;
        }
        final long bits = Double.doubleToRawLongBits(a);
        if (bits == Long.MIN_VALUE) {
            return b;
        }
        return a;
    }
    
    public static double hypot(final double x, final double y) {
        if (Double.isInfinite(x) || Double.isInfinite(y)) {
            return Double.POSITIVE_INFINITY;
        }
        if (Double.isNaN(x) || Double.isNaN(y)) {
            return Double.NaN;
        }
        final int expX = getExponent(x);
        final int expY = getExponent(y);
        if (expX > expY + 27) {
            return abs(x);
        }
        if (expY > expX + 27) {
            return abs(y);
        }
        final int middleExp = (expX + expY) / 2;
        final double scaledX = scalb(x, -middleExp);
        final double scaledY = scalb(y, -middleExp);
        final double scaledH = sqrt(scaledX * scaledX + scaledY * scaledY);
        return scalb(scaledH, middleExp);
    }
    
    public static double IEEEremainder(final double dividend, final double divisor) {
        return StrictMath.IEEEremainder(dividend, divisor);
    }
    
    public static int toIntExact(final long n) throws MathArithmeticException {
        if (n < -2147483648L || n > 2147483647L) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW, new Object[0]);
        }
        return (int)n;
    }
    
    public static int incrementExact(final int n) throws MathArithmeticException {
        if (n == Integer.MAX_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, new Object[] { n, 1 });
        }
        return n + 1;
    }
    
    public static long incrementExact(final long n) throws MathArithmeticException {
        if (n == Long.MAX_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, new Object[] { n, 1 });
        }
        return n + 1L;
    }
    
    public static int decrementExact(final int n) throws MathArithmeticException {
        if (n == Integer.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, new Object[] { n, 1 });
        }
        return n - 1;
    }
    
    public static long decrementExact(final long n) throws MathArithmeticException {
        if (n == Long.MIN_VALUE) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, new Object[] { n, 1 });
        }
        return n - 1L;
    }
    
    public static int addExact(final int a, final int b) throws MathArithmeticException {
        final int sum = a + b;
        if ((a ^ b) >= 0 && (sum ^ b) < 0) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, new Object[] { a, b });
        }
        return sum;
    }
    
    public static long addExact(final long a, final long b) throws MathArithmeticException {
        final long sum = a + b;
        if ((a ^ b) >= 0L && (sum ^ b) < 0L) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION, new Object[] { a, b });
        }
        return sum;
    }
    
    public static int subtractExact(final int a, final int b) {
        final int sub = a - b;
        if ((a ^ b) < 0 && (sub ^ b) >= 0) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, new Object[] { a, b });
        }
        return sub;
    }
    
    public static long subtractExact(final long a, final long b) {
        final long sub = a - b;
        if ((a ^ b) < 0L && (sub ^ b) >= 0L) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION, new Object[] { a, b });
        }
        return sub;
    }
    
    public static int multiplyExact(final int a, final int b) {
        if ((b > 0 && (a > Integer.MAX_VALUE / b || a < Integer.MIN_VALUE / b)) || (b < -1 && (a > Integer.MIN_VALUE / b || a < Integer.MAX_VALUE / b)) || (b == -1 && a == Integer.MIN_VALUE)) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_MULTIPLICATION, new Object[] { a, b });
        }
        return a * b;
    }
    
    public static long multiplyExact(final long a, final long b) {
        if ((b > 0L && (a > Long.MAX_VALUE / b || a < Long.MIN_VALUE / b)) || (b < -1L && (a > Long.MIN_VALUE / b || a < Long.MAX_VALUE / b)) || (b == -1L && a == Long.MIN_VALUE)) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_MULTIPLICATION, new Object[] { a, b });
        }
        return a * b;
    }
    
    public static int floorDiv(final int a, final int b) throws MathArithmeticException {
        if (b == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        final int m = a % b;
        if ((a ^ b) >= 0 || m == 0) {
            return a / b;
        }
        return a / b - 1;
    }
    
    public static long floorDiv(final long a, final long b) throws MathArithmeticException {
        if (b == 0L) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        final long m = a % b;
        if ((a ^ b) >= 0L || m == 0L) {
            return a / b;
        }
        return a / b - 1L;
    }
    
    public static int floorMod(final int a, final int b) throws MathArithmeticException {
        if (b == 0) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        final int m = a % b;
        if ((a ^ b) >= 0 || m == 0) {
            return m;
        }
        return b + m;
    }
    
    public static long floorMod(final long a, final long b) {
        if (b == 0L) {
            throw new MathArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
        }
        final long m = a % b;
        if ((a ^ b) >= 0L || m == 0L) {
            return m;
        }
        return b + m;
    }
    
    public static double copySign(final double magnitude, final double sign) {
        final long m = Double.doubleToRawLongBits(magnitude);
        final long s = Double.doubleToRawLongBits(sign);
        if ((m ^ s) >= 0L) {
            return magnitude;
        }
        return -magnitude;
    }
    
    public static float copySign(final float magnitude, final float sign) {
        final int m = Float.floatToRawIntBits(magnitude);
        final int s = Float.floatToRawIntBits(sign);
        if ((m ^ s) >= 0) {
            return magnitude;
        }
        return -magnitude;
    }
    
    public static int getExponent(final double d) {
        return (int)(Double.doubleToRawLongBits(d) >>> 52 & 0x7FFL) - 1023;
    }
    
    public static int getExponent(final float f) {
        return (Float.floatToRawIntBits(f) >>> 23 & 0xFF) - 127;
    }
    
    public static void main(final String[] a) {
        final PrintStream out = System.out;
        FastMathCalc.printarray(out, "EXP_INT_TABLE_A", 1500, ExpIntTable.EXP_INT_TABLE_A);
        FastMathCalc.printarray(out, "EXP_INT_TABLE_B", 1500, ExpIntTable.EXP_INT_TABLE_B);
        FastMathCalc.printarray(out, "EXP_FRAC_TABLE_A", 1025, ExpFracTable.EXP_FRAC_TABLE_A);
        FastMathCalc.printarray(out, "EXP_FRAC_TABLE_B", 1025, ExpFracTable.EXP_FRAC_TABLE_B);
        FastMathCalc.printarray(out, "LN_MANT", 1024, lnMant.LN_MANT);
        FastMathCalc.printarray(out, "SINE_TABLE_A", 14, FastMath.SINE_TABLE_A);
        FastMathCalc.printarray(out, "SINE_TABLE_B", 14, FastMath.SINE_TABLE_B);
        FastMathCalc.printarray(out, "COSINE_TABLE_A", 14, FastMath.COSINE_TABLE_A);
        FastMathCalc.printarray(out, "COSINE_TABLE_B", 14, FastMath.COSINE_TABLE_B);
        FastMathCalc.printarray(out, "TANGENT_TABLE_A", 14, FastMath.TANGENT_TABLE_A);
        FastMathCalc.printarray(out, "TANGENT_TABLE_B", 14, FastMath.TANGENT_TABLE_B);
    }
    
    static {
        LOG_MAX_VALUE = StrictMath.log(Double.MAX_VALUE);
        LN_QUICK_COEF = new double[][] { { 1.0, 5.669184079525E-24 }, { -0.25, -0.25 }, { 0.3333333134651184, 1.986821492305628E-8 }, { -0.25, -6.663542893624021E-14 }, { 0.19999998807907104, 1.1921056801463227E-8 }, { -0.1666666567325592, -7.800414592973399E-9 }, { 0.1428571343421936, 5.650007086920087E-9 }, { -0.12502530217170715, -7.44321345601866E-11 }, { 0.11113807559013367, 9.219544613762692E-9 } };
        LN_HI_PREC_COEF = new double[][] { { 1.0, -6.032174644509064E-23 }, { -0.25, -0.25 }, { 0.3333333134651184, 1.9868161777724352E-8 }, { -0.2499999701976776, -2.957007209750105E-8 }, { 0.19999954104423523, 1.5830993332061267E-10 }, { -0.16624879837036133, -2.6033824355191673E-8 } };
        SINE_TABLE_A = new double[] { 0.0, 0.1246747374534607, 0.24740394949913025, 0.366272509098053, 0.4794255495071411, 0.5850973129272461, 0.6816387176513672, 0.7675435543060303, 0.8414709568023682, 0.902267575263977, 0.9489846229553223, 0.9808930158615112, 0.9974949359893799, 0.9985313415527344 };
        SINE_TABLE_B = new double[] { 0.0, -4.068233003401932E-9, 9.755392680573412E-9, 1.9987994582857286E-8, -1.0902938113007961E-8, -3.9986783938944604E-8, 4.23719669792332E-8, -5.207000323380292E-8, 2.800552834259E-8, 1.883511811213715E-8, -3.5997360512765566E-9, 4.116164446561962E-8, 5.0614674548127384E-8, -1.0129027912496858E-9 };
        COSINE_TABLE_A = new double[] { 1.0, 0.9921976327896118, 0.9689123630523682, 0.9305076599121094, 0.8775825500488281, 0.8109631538391113, 0.7316888570785522, 0.6409968137741089, 0.5403022766113281, 0.4311765432357788, 0.3153223395347595, 0.19454771280288696, 0.07073719799518585, -0.05417713522911072 };
        COSINE_TABLE_B = new double[] { 0.0, 3.4439717236742845E-8, 5.865827662008209E-8, -3.7999795083850525E-8, 1.184154459111628E-8, -3.43338934259355E-8, 1.1795268640216787E-8, 4.438921624363781E-8, 2.925681159240093E-8, -2.6437112632041807E-8, 2.2860509143963117E-8, -4.813899778443457E-9, 3.6725170580355583E-9, 2.0217439756338078E-10 };
        TANGENT_TABLE_A = new double[] { 0.0, 0.1256551444530487, 0.25534194707870483, 0.3936265707015991, 0.5463024377822876, 0.7214844226837158, 0.9315965175628662, 1.1974215507507324, 1.5574076175689697, 2.092571258544922, 3.0095696449279785, 5.041914939880371, 14.101419448852539, -18.430862426757812 };
        TANGENT_TABLE_B = new double[] { 0.0, -7.877917738262007E-9, -2.5857668567479893E-8, 5.2240336371356666E-9, 5.206150291559893E-8, 1.8307188599677033E-8, -5.7618793749770706E-8, 7.848361555046424E-8, 1.0708593250394448E-7, 1.7827257129423813E-8, 2.893485277253286E-8, 3.1660099222737955E-7, 4.983191803254889E-7, -3.356118100840571E-7 };
        RECIP_2PI = new long[] { 2935890503282001226L, 9154082963658192752L, 3952090531849364496L, 9193070505571053912L, 7910884519577875640L, 113236205062349959L, 4577762542105553359L, -5034868814120038111L, 4208363204685324176L, 5648769086999809661L, 2819561105158720014L, -4035746434778044925L, -302932621132653753L, -2644281811660520851L, -3183605296591799669L, 6722166367014452318L, -3512299194304650054L, -7278142539171889152L };
        PI_O_4_BITS = new long[] { -3958705157555305932L, -4267615245585081135L };
        EIGHTHS = new double[] { 0.0, 0.125, 0.25, 0.375, 0.5, 0.625, 0.75, 0.875, 1.0, 1.125, 1.25, 1.375, 1.5, 1.625 };
        CBRTTWO = new double[] { 0.6299605249474366, 0.7937005259840998, 1.0, 1.2599210498948732, 1.5874010519681994 };
    }
    
    private static class Split
    {
        public static final Split NAN;
        public static final Split POSITIVE_INFINITY;
        public static final Split NEGATIVE_INFINITY;
        private final double full;
        private final double high;
        private final double low;
        
        Split(final double x) {
            this.full = x;
            this.high = Double.longBitsToDouble(Double.doubleToRawLongBits(x) & 0xFFFFFFFFF8000000L);
            this.low = x - this.high;
        }
        
        Split(final double high, final double low) {
            this((high == 0.0) ? ((low == 0.0 && Double.doubleToRawLongBits(high) == Long.MIN_VALUE) ? -0.0 : low) : (high + low), high, low);
        }
        
        Split(final double full, final double high, final double low) {
            this.full = full;
            this.high = high;
            this.low = low;
        }
        
        public Split multiply(final Split b) {
            final Split mulBasic = new Split(this.full * b.full);
            final double mulError = this.low * b.low - (mulBasic.full - this.high * b.high - this.low * b.high - this.high * b.low);
            return new Split(mulBasic.high, mulBasic.low + mulError);
        }
        
        public Split reciprocal() {
            final double approximateInv = 1.0 / this.full;
            final Split splitInv = new Split(approximateInv);
            final Split product = this.multiply(splitInv);
            final double error = product.high - 1.0 + product.low;
            return Double.isNaN(error) ? splitInv : new Split(splitInv.high, splitInv.low - error / this.full);
        }
        
        private Split pow(final long e) {
            Split result = new Split(1.0);
            Split d2p = new Split(this.full, this.high, this.low);
            for (long p = e; p != 0L; p >>>= 1) {
                if ((p & 0x1L) != 0x0L) {
                    result = result.multiply(d2p);
                }
                d2p = d2p.multiply(d2p);
            }
            if (!Double.isNaN(result.full)) {
                return result;
            }
            if (Double.isNaN(this.full)) {
                return Split.NAN;
            }
            if (FastMath.abs(this.full) < 1.0) {
                return new Split(FastMath.copySign(0.0, this.full), 0.0);
            }
            if (this.full < 0.0 && (e & 0x1L) == 0x1L) {
                return Split.NEGATIVE_INFINITY;
            }
            return Split.POSITIVE_INFINITY;
        }
        
        static {
            NAN = new Split(Double.NaN, 0.0);
            POSITIVE_INFINITY = new Split(Double.POSITIVE_INFINITY, 0.0);
            NEGATIVE_INFINITY = new Split(Double.NEGATIVE_INFINITY, 0.0);
        }
    }
    
    private static class ExpIntTable
    {
        private static final double[] EXP_INT_TABLE_A;
        private static final double[] EXP_INT_TABLE_B;
        
        static {
            EXP_INT_TABLE_A = FastMathLiteralArrays.loadExpIntA();
            EXP_INT_TABLE_B = FastMathLiteralArrays.loadExpIntB();
        }
    }
    
    private static class ExpFracTable
    {
        private static final double[] EXP_FRAC_TABLE_A;
        private static final double[] EXP_FRAC_TABLE_B;
        
        static {
            EXP_FRAC_TABLE_A = FastMathLiteralArrays.loadExpFracA();
            EXP_FRAC_TABLE_B = FastMathLiteralArrays.loadExpFracB();
        }
    }
    
    private static class lnMant
    {
        private static final double[][] LN_MANT;
        
        static {
            LN_MANT = FastMathLiteralArrays.loadLnMant();
        }
    }
    
    private static class CodyWaite
    {
        private final int finalK;
        private final double finalRemA;
        private final double finalRemB;
        
        CodyWaite(final double xa) {
            int k = (int)(xa * 0.6366197723675814);
            double remA;
            double remB;
            while (true) {
                double a = -k * 1.570796251296997;
                remA = xa + a;
                remB = -(remA - xa - a);
                a = -k * 7.549789948768648E-8;
                double b = remA;
                remA = a + b;
                remB += -(remA - b - a);
                a = -k * 6.123233995736766E-17;
                b = remA;
                remA = a + b;
                remB += -(remA - b - a);
                if (remA > 0.0) {
                    break;
                }
                --k;
            }
            this.finalK = k;
            this.finalRemA = remA;
            this.finalRemB = remB;
        }
        
        int getK() {
            return this.finalK;
        }
        
        double getRemA() {
            return this.finalRemA;
        }
        
        double getRemB() {
            return this.finalRemB;
        }
    }
}
