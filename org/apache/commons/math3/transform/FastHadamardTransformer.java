package org.apache.commons.math3.transform;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import java.io.Serializable;

public class FastHadamardTransformer implements RealTransformer, Serializable
{
    static final long serialVersionUID = 20120211L;
    
    public double[] transform(final double[] f, final TransformType type) {
        if (type == TransformType.FORWARD) {
            return this.fht(f);
        }
        return TransformUtils.scaleArray(this.fht(f), 1.0 / f.length);
    }
    
    public double[] transform(final UnivariateFunction f, final double min, final double max, final int n, final TransformType type) {
        return this.transform(FunctionUtils.sample(f, min, max, n), type);
    }
    
    public int[] transform(final int[] f) {
        return this.fht(f);
    }
    
    protected double[] fht(final double[] x) throws MathIllegalArgumentException {
        final int n = x.length;
        final int halfN = n / 2;
        if (!ArithmeticUtils.isPowerOfTwo(n)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO, new Object[] { n });
        }
        double[] yPrevious = new double[n];
        double[] yCurrent = x.clone();
        for (int j = 1; j < n; j <<= 1) {
            final double[] yTmp = yCurrent;
            yCurrent = yPrevious;
            yPrevious = yTmp;
            for (int i = 0; i < halfN; ++i) {
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI] + yPrevious[twoI + 1];
            }
            for (int i = halfN; i < n; ++i) {
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI - n] - yPrevious[twoI - n + 1];
            }
        }
        return yCurrent;
    }
    
    protected int[] fht(final int[] x) throws MathIllegalArgumentException {
        final int n = x.length;
        final int halfN = n / 2;
        if (!ArithmeticUtils.isPowerOfTwo(n)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO, new Object[] { n });
        }
        int[] yPrevious = new int[n];
        int[] yCurrent = x.clone();
        for (int j = 1; j < n; j <<= 1) {
            final int[] yTmp = yCurrent;
            yCurrent = yPrevious;
            yPrevious = yTmp;
            for (int i = 0; i < halfN; ++i) {
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI] + yPrevious[twoI + 1];
            }
            for (int i = halfN; i < n; ++i) {
                final int twoI = 2 * i;
                yCurrent[i] = yPrevious[twoI - n] - yPrevious[twoI - n + 1];
            }
        }
        return yCurrent;
    }
}
