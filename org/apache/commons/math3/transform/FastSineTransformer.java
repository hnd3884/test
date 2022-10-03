package org.apache.commons.math3.transform;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;

public class FastSineTransformer implements RealTransformer, Serializable
{
    static final long serialVersionUID = 20120211L;
    private final DstNormalization normalization;
    
    public FastSineTransformer(final DstNormalization normalization) {
        this.normalization = normalization;
    }
    
    public double[] transform(final double[] f, final TransformType type) {
        if (this.normalization == DstNormalization.ORTHOGONAL_DST_I) {
            final double s = FastMath.sqrt(2.0 / f.length);
            return TransformUtils.scaleArray(this.fst(f), s);
        }
        if (type == TransformType.FORWARD) {
            return this.fst(f);
        }
        final double s = 2.0 / f.length;
        return TransformUtils.scaleArray(this.fst(f), s);
    }
    
    public double[] transform(final UnivariateFunction f, final double min, final double max, final int n, final TransformType type) {
        final double[] data = FunctionUtils.sample(f, min, max, n);
        data[0] = 0.0;
        return this.transform(data, type);
    }
    
    protected double[] fst(final double[] f) throws MathIllegalArgumentException {
        final double[] transformed = new double[f.length];
        if (!ArithmeticUtils.isPowerOfTwo(f.length)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING, new Object[] { f.length });
        }
        if (f[0] != 0.0) {
            throw new MathIllegalArgumentException(LocalizedFormats.FIRST_ELEMENT_NOT_ZERO, new Object[] { f[0] });
        }
        final int n = f.length;
        if (n == 1) {
            transformed[0] = 0.0;
            return transformed;
        }
        final double[] x = new double[n];
        x[0] = 0.0;
        x[n >> 1] = 2.0 * f[n >> 1];
        for (int i = 1; i < n >> 1; ++i) {
            final double a = FastMath.sin(i * 3.141592653589793 / n) * (f[i] + f[n - i]);
            final double b = 0.5 * (f[i] - f[n - i]);
            x[i] = a + b;
            x[n - i] = a - b;
        }
        final FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        final Complex[] y = transformer.transform(x, TransformType.FORWARD);
        transformed[0] = 0.0;
        transformed[1] = 0.5 * y[0].getReal();
        for (int j = 1; j < n >> 1; ++j) {
            transformed[2 * j] = -y[j].getImaginary();
            transformed[2 * j + 1] = y[j].getReal() + transformed[2 * j - 1];
        }
        return transformed;
    }
}
