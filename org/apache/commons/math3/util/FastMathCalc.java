package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.DimensionMismatchException;
import java.io.PrintStream;

class FastMathCalc
{
    private static final long HEX_40000000 = 1073741824L;
    private static final double[] FACT;
    private static final double[][] LN_SPLIT_COEF;
    private static final String TABLE_START_DECL = "    {";
    private static final String TABLE_END_DECL = "    };";
    
    private FastMathCalc() {
    }
    
    private static void buildSinCosTables(final double[] SINE_TABLE_A, final double[] SINE_TABLE_B, final double[] COSINE_TABLE_A, final double[] COSINE_TABLE_B, final int SINE_TABLE_LEN, final double[] TANGENT_TABLE_A, final double[] TANGENT_TABLE_B) {
        final double[] result = new double[2];
        for (int i = 0; i < 7; ++i) {
            final double x = i / 8.0;
            slowSin(x, result);
            SINE_TABLE_A[i] = result[0];
            SINE_TABLE_B[i] = result[1];
            slowCos(x, result);
            COSINE_TABLE_A[i] = result[0];
            COSINE_TABLE_B[i] = result[1];
        }
        for (int i = 7; i < SINE_TABLE_LEN; ++i) {
            final double[] xs = new double[2];
            final double[] ys = new double[2];
            final double[] as = new double[2];
            final double[] bs = new double[2];
            final double[] temps = new double[2];
            if ((i & 0x1) == 0x0) {
                xs[0] = SINE_TABLE_A[i / 2];
                xs[1] = SINE_TABLE_B[i / 2];
                ys[0] = COSINE_TABLE_A[i / 2];
                ys[1] = COSINE_TABLE_B[i / 2];
                splitMult(xs, ys, result);
                SINE_TABLE_A[i] = result[0] * 2.0;
                SINE_TABLE_B[i] = result[1] * 2.0;
                splitMult(ys, ys, as);
                splitMult(xs, xs, temps);
                temps[0] = -temps[0];
                temps[1] = -temps[1];
                splitAdd(as, temps, result);
                COSINE_TABLE_A[i] = result[0];
                COSINE_TABLE_B[i] = result[1];
            }
            else {
                xs[0] = SINE_TABLE_A[i / 2];
                xs[1] = SINE_TABLE_B[i / 2];
                ys[0] = COSINE_TABLE_A[i / 2];
                ys[1] = COSINE_TABLE_B[i / 2];
                as[0] = SINE_TABLE_A[i / 2 + 1];
                as[1] = SINE_TABLE_B[i / 2 + 1];
                bs[0] = COSINE_TABLE_A[i / 2 + 1];
                bs[1] = COSINE_TABLE_B[i / 2 + 1];
                splitMult(xs, bs, temps);
                splitMult(ys, as, result);
                splitAdd(result, temps, result);
                SINE_TABLE_A[i] = result[0];
                SINE_TABLE_B[i] = result[1];
                splitMult(ys, bs, result);
                splitMult(xs, as, temps);
                temps[0] = -temps[0];
                temps[1] = -temps[1];
                splitAdd(result, temps, result);
                COSINE_TABLE_A[i] = result[0];
                COSINE_TABLE_B[i] = result[1];
            }
        }
        for (int i = 0; i < SINE_TABLE_LEN; ++i) {
            final double[] xs = new double[2];
            final double[] ys = new double[2];
            final double[] as = { COSINE_TABLE_A[i], COSINE_TABLE_B[i] };
            splitReciprocal(as, ys);
            xs[0] = SINE_TABLE_A[i];
            xs[1] = SINE_TABLE_B[i];
            splitMult(xs, ys, as);
            TANGENT_TABLE_A[i] = as[0];
            TANGENT_TABLE_B[i] = as[1];
        }
    }
    
    static double slowCos(final double x, final double[] result) {
        final double[] xs = new double[2];
        final double[] ys = new double[2];
        final double[] facts = new double[2];
        final double[] as = new double[2];
        split(x, xs);
        ys[0] = (ys[1] = 0.0);
        for (int i = FastMathCalc.FACT.length - 1; i >= 0; --i) {
            splitMult(xs, ys, as);
            ys[0] = as[0];
            ys[1] = as[1];
            if ((i & 0x1) == 0x0) {
                split(FastMathCalc.FACT[i], as);
                splitReciprocal(as, facts);
                if ((i & 0x2) != 0x0) {
                    facts[0] = -facts[0];
                    facts[1] = -facts[1];
                }
                splitAdd(ys, facts, as);
                ys[0] = as[0];
                ys[1] = as[1];
            }
        }
        if (result != null) {
            result[0] = ys[0];
            result[1] = ys[1];
        }
        return ys[0] + ys[1];
    }
    
    static double slowSin(final double x, final double[] result) {
        final double[] xs = new double[2];
        final double[] ys = new double[2];
        final double[] facts = new double[2];
        final double[] as = new double[2];
        split(x, xs);
        ys[0] = (ys[1] = 0.0);
        for (int i = FastMathCalc.FACT.length - 1; i >= 0; --i) {
            splitMult(xs, ys, as);
            ys[0] = as[0];
            ys[1] = as[1];
            if ((i & 0x1) != 0x0) {
                split(FastMathCalc.FACT[i], as);
                splitReciprocal(as, facts);
                if ((i & 0x2) != 0x0) {
                    facts[0] = -facts[0];
                    facts[1] = -facts[1];
                }
                splitAdd(ys, facts, as);
                ys[0] = as[0];
                ys[1] = as[1];
            }
        }
        if (result != null) {
            result[0] = ys[0];
            result[1] = ys[1];
        }
        return ys[0] + ys[1];
    }
    
    static double slowexp(final double x, final double[] result) {
        final double[] xs = new double[2];
        final double[] ys = new double[2];
        final double[] facts = new double[2];
        final double[] as = new double[2];
        split(x, xs);
        ys[0] = (ys[1] = 0.0);
        for (int i = FastMathCalc.FACT.length - 1; i >= 0; --i) {
            splitMult(xs, ys, as);
            ys[0] = as[0];
            ys[1] = as[1];
            split(FastMathCalc.FACT[i], as);
            splitReciprocal(as, facts);
            splitAdd(ys, facts, as);
            ys[0] = as[0];
            ys[1] = as[1];
        }
        if (result != null) {
            result[0] = ys[0];
            result[1] = ys[1];
        }
        return ys[0] + ys[1];
    }
    
    private static void split(final double d, final double[] split) {
        if (d < 8.0E298 && d > -8.0E298) {
            final double a = d * 1.073741824E9;
            split[0] = d + a - a;
            split[1] = d - split[0];
        }
        else {
            final double a = d * 9.313225746154785E-10;
            split[0] = (d + a - d) * 1.073741824E9;
            split[1] = d - split[0];
        }
    }
    
    private static void resplit(final double[] a) {
        final double c = a[0] + a[1];
        final double d = -(c - a[0] - a[1]);
        if (c < 8.0E298 && c > -8.0E298) {
            final double z = c * 1.073741824E9;
            a[0] = c + z - z;
            a[1] = c - a[0] + d;
        }
        else {
            final double z = c * 9.313225746154785E-10;
            a[0] = (c + z - c) * 1.073741824E9;
            a[1] = c - a[0] + d;
        }
    }
    
    private static void splitMult(final double[] a, final double[] b, final double[] ans) {
        ans[0] = a[0] * b[0];
        ans[1] = a[0] * b[1] + a[1] * b[0] + a[1] * b[1];
        resplit(ans);
    }
    
    private static void splitAdd(final double[] a, final double[] b, final double[] ans) {
        ans[0] = a[0] + b[0];
        ans[1] = a[1] + b[1];
        resplit(ans);
    }
    
    static void splitReciprocal(final double[] in, final double[] result) {
        final double b = 2.384185791015625E-7;
        final double a = 0.9999997615814209;
        if (in[0] == 0.0) {
            in[0] = in[1];
            in[1] = 0.0;
        }
        result[0] = 0.9999997615814209 / in[0];
        result[1] = (2.384185791015625E-7 * in[0] - 0.9999997615814209 * in[1]) / (in[0] * in[0] + in[0] * in[1]);
        if (result[1] != result[1]) {
            result[1] = 0.0;
        }
        resplit(result);
        for (int i = 0; i < 2; ++i) {
            double err = 1.0 - result[0] * in[0] - result[0] * in[1] - result[1] * in[0] - result[1] * in[1];
            err *= result[0] + result[1];
            final int n = 1;
            result[n] += err;
        }
    }
    
    private static void quadMult(final double[] a, final double[] b, final double[] result) {
        final double[] xs = new double[2];
        final double[] ys = new double[2];
        final double[] zs = new double[2];
        split(a[0], xs);
        split(b[0], ys);
        splitMult(xs, ys, zs);
        result[0] = zs[0];
        result[1] = zs[1];
        split(b[1], ys);
        splitMult(xs, ys, zs);
        double tmp = result[0] + zs[0];
        final int n = 1;
        result[n] -= tmp - result[0] - zs[0];
        result[0] = tmp;
        tmp = result[0] + zs[1];
        final int n2 = 1;
        result[n2] -= tmp - result[0] - zs[1];
        result[0] = tmp;
        split(a[1], xs);
        split(b[0], ys);
        splitMult(xs, ys, zs);
        tmp = result[0] + zs[0];
        final int n3 = 1;
        result[n3] -= tmp - result[0] - zs[0];
        result[0] = tmp;
        tmp = result[0] + zs[1];
        final int n4 = 1;
        result[n4] -= tmp - result[0] - zs[1];
        result[0] = tmp;
        split(a[1], xs);
        split(b[1], ys);
        splitMult(xs, ys, zs);
        tmp = result[0] + zs[0];
        final int n5 = 1;
        result[n5] -= tmp - result[0] - zs[0];
        result[0] = tmp;
        tmp = result[0] + zs[1];
        final int n6 = 1;
        result[n6] -= tmp - result[0] - zs[1];
        result[0] = tmp;
    }
    
    static double expint(int p, final double[] result) {
        final double[] xs = new double[2];
        final double[] as = new double[2];
        final double[] ys = new double[2];
        xs[0] = 2.718281828459045;
        xs[1] = 1.4456468917292502E-16;
        split(1.0, ys);
        while (p > 0) {
            if ((p & 0x1) != 0x0) {
                quadMult(ys, xs, as);
                ys[0] = as[0];
                ys[1] = as[1];
            }
            quadMult(xs, xs, as);
            xs[0] = as[0];
            xs[1] = as[1];
            p >>= 1;
        }
        if (result != null) {
            result[0] = ys[0];
            result[1] = ys[1];
            resplit(result);
        }
        return ys[0] + ys[1];
    }
    
    static double[] slowLog(final double xi) {
        final double[] x = new double[2];
        final double[] x2 = new double[2];
        final double[] y = new double[2];
        final double[] a = new double[2];
        split(xi, x);
        final double[] array = x;
        final int n = 0;
        ++array[n];
        resplit(x);
        splitReciprocal(x, a);
        final double[] array2 = x;
        final int n2 = 0;
        array2[n2] -= 2.0;
        resplit(x);
        splitMult(x, a, y);
        x[0] = y[0];
        x[1] = y[1];
        splitMult(x, x, x2);
        y[0] = FastMathCalc.LN_SPLIT_COEF[FastMathCalc.LN_SPLIT_COEF.length - 1][0];
        y[1] = FastMathCalc.LN_SPLIT_COEF[FastMathCalc.LN_SPLIT_COEF.length - 1][1];
        for (int i = FastMathCalc.LN_SPLIT_COEF.length - 2; i >= 0; --i) {
            splitMult(y, x2, a);
            y[0] = a[0];
            y[1] = a[1];
            splitAdd(y, FastMathCalc.LN_SPLIT_COEF[i], a);
            y[0] = a[0];
            y[1] = a[1];
        }
        splitMult(y, x, a);
        y[0] = a[0];
        y[1] = a[1];
        return y;
    }
    
    static void printarray(final PrintStream out, final String name, final int expectedLen, final double[][] array2d) {
        out.println(name);
        checkLen(expectedLen, array2d.length);
        out.println("    { ");
        int i = 0;
        for (final double[] array : array2d) {
            out.print("        {");
            for (final double d : array) {
                out.printf("%-25.25s", format(d));
            }
            out.println("}, // " + i++);
        }
        out.println("    };");
    }
    
    static void printarray(final PrintStream out, final String name, final int expectedLen, final double[] array) {
        out.println(name + "=");
        checkLen(expectedLen, array.length);
        out.println("    {");
        for (final double d : array) {
            out.printf("        %s%n", format(d));
        }
        out.println("    };");
    }
    
    static String format(final double d) {
        if (d != d) {
            return "Double.NaN,";
        }
        return ((d >= 0.0) ? "+" : "") + Double.toString(d) + "d,";
    }
    
    private static void checkLen(final int expectedLen, final int actual) throws DimensionMismatchException {
        if (expectedLen != actual) {
            throw new DimensionMismatchException(actual, expectedLen);
        }
    }
    
    static {
        FACT = new double[] { 1.0, 1.0, 2.0, 6.0, 24.0, 120.0, 720.0, 5040.0, 40320.0, 362880.0, 3628800.0, 3.99168E7, 4.790016E8, 6.2270208E9, 8.71782912E10, 1.307674368E12, 2.0922789888E13, 3.55687428096E14, 6.402373705728E15, 1.21645100408832E17 };
        LN_SPLIT_COEF = new double[][] { { 2.0, 0.0 }, { 0.6666666269302368, 3.9736429850260626E-8 }, { 0.3999999761581421, 2.3841857910019882E-8 }, { 0.2857142686843872, 1.7029898543501842E-8 }, { 0.2222222089767456, 1.3245471311735498E-8 }, { 0.1818181574344635, 2.4384203044354907E-8 }, { 0.1538461446762085, 9.140260083262505E-9 }, { 0.13333332538604736, 9.220590270857665E-9 }, { 0.11764700710773468, 1.2393345855018391E-8 }, { 0.10526403784751892, 8.251545029714408E-9 }, { 0.0952233225107193, 1.2675934823758863E-8 }, { 0.08713622391223907, 1.1430250008909141E-8 }, { 0.07842259109020233, 2.404307984052299E-9 }, { 0.08371849358081818, 1.176342548272881E-8 }, { 0.030589580535888672, 1.2958646899018938E-9 }, { 0.14982303977012634, 1.225743062930824E-8 } };
    }
}
