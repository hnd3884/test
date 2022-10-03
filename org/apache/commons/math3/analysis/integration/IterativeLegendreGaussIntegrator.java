package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.analysis.integration.gauss.GaussIntegrator;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.analysis.integration.gauss.GaussIntegratorFactory;

public class IterativeLegendreGaussIntegrator extends BaseAbstractUnivariateIntegrator
{
    private static final GaussIntegratorFactory FACTORY;
    private final int numberOfPoints;
    
    public IterativeLegendreGaussIntegrator(final int n, final double relativeAccuracy, final double absoluteAccuracy, final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (n <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_POINTS, n);
        }
        this.numberOfPoints = n;
    }
    
    public IterativeLegendreGaussIntegrator(final int n, final double relativeAccuracy, final double absoluteAccuracy) throws NotStrictlyPositiveException {
        this(n, relativeAccuracy, absoluteAccuracy, 3, Integer.MAX_VALUE);
    }
    
    public IterativeLegendreGaussIntegrator(final int n, final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException {
        this(n, 1.0E-6, 1.0E-15, minimalIterationCount, maximalIterationCount);
    }
    
    @Override
    protected double doIntegrate() throws MathIllegalArgumentException, TooManyEvaluationsException, MaxCountExceededException {
        double oldt = this.stage(1);
        int n = 2;
        double t;
        while (true) {
            t = this.stage(n);
            final double delta = FastMath.abs(t - oldt);
            final double limit = FastMath.max(this.getAbsoluteAccuracy(), this.getRelativeAccuracy() * (FastMath.abs(oldt) + FastMath.abs(t)) * 0.5);
            if (this.getIterations() + 1 >= this.getMinimalIterationCount() && delta <= limit) {
                break;
            }
            final double ratio = FastMath.min(4.0, FastMath.pow(delta / limit, 0.5 / this.numberOfPoints));
            n = FastMath.max((int)(ratio * n), n + 1);
            oldt = t;
            this.incrementCount();
        }
        return t;
    }
    
    private double stage(final int n) throws TooManyEvaluationsException {
        final UnivariateFunction f = new UnivariateFunction() {
            public double value(final double x) throws MathIllegalArgumentException, TooManyEvaluationsException {
                return IterativeLegendreGaussIntegrator.this.computeObjectiveValue(x);
            }
        };
        final double min = this.getMin();
        final double max = this.getMax();
        final double step = (max - min) / n;
        double sum = 0.0;
        for (int i = 0; i < n; ++i) {
            final double a = min + i * step;
            final double b = a + step;
            final GaussIntegrator g = IterativeLegendreGaussIntegrator.FACTORY.legendreHighPrecision(this.numberOfPoints, a, b);
            sum += g.integrate(f);
        }
        return sum;
    }
    
    static {
        FACTORY = new GaussIntegratorFactory();
    }
}
