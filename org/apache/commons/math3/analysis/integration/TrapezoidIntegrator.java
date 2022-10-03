package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;

public class TrapezoidIntegrator extends BaseAbstractUnivariateIntegrator
{
    public static final int TRAPEZOID_MAX_ITERATIONS_COUNT = 64;
    private double s;
    
    public TrapezoidIntegrator(final double relativeAccuracy, final double absoluteAccuracy, final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(maximalIterationCount, 64, false);
        }
    }
    
    public TrapezoidIntegrator(final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(maximalIterationCount, 64, false);
        }
    }
    
    public TrapezoidIntegrator() {
        super(3, 64);
    }
    
    double stage(final BaseAbstractUnivariateIntegrator baseIntegrator, final int n) throws TooManyEvaluationsException {
        if (n == 0) {
            final double max = baseIntegrator.getMax();
            final double min = baseIntegrator.getMin();
            return this.s = 0.5 * (max - min) * (baseIntegrator.computeObjectiveValue(min) + baseIntegrator.computeObjectiveValue(max));
        }
        final long np = 1L << n - 1;
        double sum = 0.0;
        final double max2 = baseIntegrator.getMax();
        final double min2 = baseIntegrator.getMin();
        final double spacing = (max2 - min2) / np;
        double x = min2 + 0.5 * spacing;
        for (long i = 0L; i < np; ++i) {
            sum += baseIntegrator.computeObjectiveValue(x);
            x += spacing;
        }
        return this.s = 0.5 * (this.s + sum * spacing);
    }
    
    @Override
    protected double doIntegrate() throws MathIllegalArgumentException, TooManyEvaluationsException, MaxCountExceededException {
        double oldt = this.stage(this, 0);
        this.incrementCount();
        double t;
        while (true) {
            final int i = this.getIterations();
            t = this.stage(this, i);
            if (i >= this.getMinimalIterationCount()) {
                final double delta = FastMath.abs(t - oldt);
                final double rLimit = this.getRelativeAccuracy() * (FastMath.abs(oldt) + FastMath.abs(t)) * 0.5;
                if (delta <= rLimit || delta <= this.getAbsoluteAccuracy()) {
                    break;
                }
            }
            oldt = t;
            this.incrementCount();
        }
        return t;
    }
}
