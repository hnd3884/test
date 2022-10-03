package org.apache.commons.math3.transform;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.complex.Complex;

public class TransformUtils
{
    private static final int[] POWERS_OF_TWO;
    
    private TransformUtils() {
    }
    
    public static double[] scaleArray(final double[] f, final double d) {
        for (int i = 0; i < f.length; ++i) {
            final int n = i;
            f[n] *= d;
        }
        return f;
    }
    
    public static Complex[] scaleArray(final Complex[] f, final double d) {
        for (int i = 0; i < f.length; ++i) {
            f[i] = new Complex(d * f[i].getReal(), d * f[i].getImaginary());
        }
        return f;
    }
    
    public static double[][] createRealImaginaryArray(final Complex[] dataC) {
        final double[][] dataRI = new double[2][dataC.length];
        final double[] dataR = dataRI[0];
        final double[] dataI = dataRI[1];
        for (int i = 0; i < dataC.length; ++i) {
            final Complex c = dataC[i];
            dataR[i] = c.getReal();
            dataI[i] = c.getImaginary();
        }
        return dataRI;
    }
    
    public static Complex[] createComplexArray(final double[][] dataRI) throws DimensionMismatchException {
        if (dataRI.length != 2) {
            throw new DimensionMismatchException(dataRI.length, 2);
        }
        final double[] dataR = dataRI[0];
        final double[] dataI = dataRI[1];
        if (dataR.length != dataI.length) {
            throw new DimensionMismatchException(dataI.length, dataR.length);
        }
        final int n = dataR.length;
        final Complex[] c = new Complex[n];
        for (int i = 0; i < n; ++i) {
            c[i] = new Complex(dataR[i], dataI[i]);
        }
        return c;
    }
    
    public static int exactLog2(final int n) throws MathIllegalArgumentException {
        final int index = Arrays.binarySearch(TransformUtils.POWERS_OF_TWO, n);
        if (index < 0) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING, new Object[] { n });
        }
        return index;
    }
    
    static {
        POWERS_OF_TWO = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824 };
    }
}
