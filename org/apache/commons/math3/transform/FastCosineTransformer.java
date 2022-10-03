package org.apache.commons.math3.transform;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;

public class FastCosineTransformer implements RealTransformer, Serializable
{
    static final long serialVersionUID = 20120212L;
    private final DctNormalization normalization;
    
    public FastCosineTransformer(final DctNormalization normalization) {
        this.normalization = normalization;
    }
    
    public double[] transform(final double[] f, final TransformType type) throws MathIllegalArgumentException {
        if (type != TransformType.FORWARD) {
            final double s2 = 2.0 / (f.length - 1);
            double s3;
            if (this.normalization == DctNormalization.ORTHOGONAL_DCT_I) {
                s3 = FastMath.sqrt(s2);
            }
            else {
                s3 = s2;
            }
            return TransformUtils.scaleArray(this.fct(f), s3);
        }
        if (this.normalization == DctNormalization.ORTHOGONAL_DCT_I) {
            final double s4 = FastMath.sqrt(2.0 / (f.length - 1));
            return TransformUtils.scaleArray(this.fct(f), s4);
        }
        return this.fct(f);
    }
    
    public double[] transform(final UnivariateFunction f, final double min, final double max, final int n, final TransformType type) throws MathIllegalArgumentException {
        final double[] data = FunctionUtils.sample(f, min, max, n);
        return this.transform(data, type);
    }
    
    protected double[] fct(final double[] f) throws MathIllegalArgumentException {
        final double[] transformed = new double[f.length];
        final int n = f.length - 1;
        if (!ArithmeticUtils.isPowerOfTwo(n)) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO_PLUS_ONE, new Object[] { f.length });
        }
        if (n == 1) {
            transformed[0] = 0.5 * (f[0] + f[1]);
            transformed[1] = 0.5 * (f[0] - f[1]);
            return transformed;
        }
        final double[] x = new double[n];
        x[0] = 0.5 * (f[0] + f[n]);
        x[n >> 1] = f[n >> 1];
        double t1 = 0.5 * (f[0] - f[n]);
        for (int i = 1; i < n >> 1; ++i) {
            final double a = 0.5 * (f[i] + f[n - i]);
            final double b = FastMath.sin(i * 3.141592653589793 / n) * (f[i] - f[n - i]);
            final double c = FastMath.cos(i * 3.141592653589793 / n) * (f[i] - f[n - i]);
            x[i] = a - b;
            x[n - i] = a + b;
            t1 += c;
        }
        final FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
        final Complex[] y = transformer.transform(x, TransformType.FORWARD);
        transformed[0] = y[0].getReal();
        transformed[1] = t1;
        for (int j = 1; j < n >> 1; ++j) {
            transformed[2 * j] = y[j].getReal();
            transformed[2 * j + 1] = transformed[2 * j - 1] - y[j].getImaginary();
        }
        transformed[n] = y[n >> 1].getReal();
        return transformed;
    }
}
