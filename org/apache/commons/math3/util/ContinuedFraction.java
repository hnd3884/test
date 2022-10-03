package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.ConvergenceException;

public abstract class ContinuedFraction
{
    private static final double DEFAULT_EPSILON = 1.0E-8;
    
    protected ContinuedFraction() {
    }
    
    protected abstract double getA(final int p0, final double p1);
    
    protected abstract double getB(final int p0, final double p1);
    
    public double evaluate(final double x) throws ConvergenceException {
        return this.evaluate(x, 1.0E-8, Integer.MAX_VALUE);
    }
    
    public double evaluate(final double x, final double epsilon) throws ConvergenceException {
        return this.evaluate(x, epsilon, Integer.MAX_VALUE);
    }
    
    public double evaluate(final double x, final int maxIterations) throws ConvergenceException, MaxCountExceededException {
        return this.evaluate(x, 1.0E-8, maxIterations);
    }
    
    public double evaluate(final double x, final double epsilon, final int maxIterations) throws ConvergenceException, MaxCountExceededException {
        final double small = 1.0E-50;
        double hPrev = this.getA(0, x);
        if (Precision.equals(hPrev, 0.0, 1.0E-50)) {
            hPrev = 1.0E-50;
        }
        int n = 1;
        double dPrev = 0.0;
        double cPrev = hPrev;
        double hN = hPrev;
        while (n < maxIterations) {
            final double a = this.getA(n, x);
            final double b = this.getB(n, x);
            double dN = a + b * dPrev;
            if (Precision.equals(dN, 0.0, 1.0E-50)) {
                dN = 1.0E-50;
            }
            double cN = a + b / cPrev;
            if (Precision.equals(cN, 0.0, 1.0E-50)) {
                cN = 1.0E-50;
            }
            dN = 1.0 / dN;
            final double deltaN = cN * dN;
            hN = hPrev * deltaN;
            if (Double.isInfinite(hN)) {
                throw new ConvergenceException(LocalizedFormats.CONTINUED_FRACTION_INFINITY_DIVERGENCE, new Object[] { x });
            }
            if (Double.isNaN(hN)) {
                throw new ConvergenceException(LocalizedFormats.CONTINUED_FRACTION_NAN_DIVERGENCE, new Object[] { x });
            }
            if (FastMath.abs(deltaN - 1.0) < epsilon) {
                break;
            }
            dPrev = dN;
            cPrev = cN;
            hPrev = hN;
            ++n;
        }
        if (n >= maxIterations) {
            throw new MaxCountExceededException(LocalizedFormats.NON_CONVERGENT_CONTINUED_FRACTION, maxIterations, new Object[] { x });
        }
        return hN;
    }
}
