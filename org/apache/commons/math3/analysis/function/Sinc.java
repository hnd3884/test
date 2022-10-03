package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class Sinc implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction
{
    private static final double SHORTCUT = 0.006;
    private final boolean normalized;
    
    public Sinc() {
        this(false);
    }
    
    public Sinc(final boolean normalized) {
        this.normalized = normalized;
    }
    
    public double value(final double x) {
        final double scaledX = this.normalized ? (3.141592653589793 * x) : x;
        if (FastMath.abs(scaledX) <= 0.006) {
            final double scaledX2 = scaledX * scaledX;
            return ((scaledX2 - 20.0) * scaledX2 + 120.0) / 120.0;
        }
        return FastMath.sin(scaledX) / scaledX;
    }
    
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }
    
    public DerivativeStructure value(final DerivativeStructure t) throws DimensionMismatchException {
        final double scaledX = (this.normalized ? 3.141592653589793 : 1.0) * t.getValue();
        final double scaledX2 = scaledX * scaledX;
        final double[] f = new double[t.getOrder() + 1];
        if (FastMath.abs(scaledX) <= 0.006) {
            for (int i = 0; i < f.length; ++i) {
                final int k = i / 2;
                if ((i & 0x1) == 0x0) {
                    f[i] = (((k & 0x1) == 0x0) ? 1 : -1) * (1.0 / (i + 1) - scaledX2 * (1.0 / (2 * i + 6) - scaledX2 / (24 * i + 120)));
                }
                else {
                    f[i] = (((k & 0x1) == 0x0) ? (-scaledX) : scaledX) * (1.0 / (i + 2) - scaledX2 * (1.0 / (6 * i + 24) - scaledX2 / (120 * i + 720)));
                }
            }
        }
        else {
            final double inv = 1.0 / scaledX;
            final double cos = FastMath.cos(scaledX);
            final double sin = FastMath.sin(scaledX);
            f[0] = inv * sin;
            final double[] sc = new double[f.length];
            sc[0] = 1.0;
            double coeff = inv;
            for (int n = 1; n < f.length; ++n) {
                double s = 0.0;
                double c = 0.0;
                int kStart;
                if ((n & 0x1) == 0x0) {
                    sc[n] = 0.0;
                    kStart = n;
                }
                else {
                    sc[n] = sc[n - 1];
                    c = sc[n];
                    kStart = n - 1;
                }
                for (int j = kStart; j > 1; j -= 2) {
                    sc[j] = (j - n) * sc[j] - sc[j - 1];
                    s = s * scaledX2 + sc[j];
                    sc[j - 1] = (j - 1 - n) * sc[j - 1] + sc[j - 2];
                    c = c * scaledX2 + sc[j - 1];
                }
                final double[] array = sc;
                final int n2 = 0;
                array[n2] *= -n;
                s = s * scaledX2 + sc[0];
                coeff *= inv;
                f[n] = coeff * (s * sin + c * scaledX * cos);
            }
        }
        if (this.normalized) {
            double scale = 3.141592653589793;
            for (int l = 1; l < f.length; ++l) {
                final double[] array2 = f;
                final int n3 = l;
                array2[n3] *= scale;
                scale *= 3.141592653589793;
            }
        }
        return t.compose(f);
    }
}
