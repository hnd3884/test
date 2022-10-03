package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;

public class SimpsonIntegrator extends BaseAbstractUnivariateIntegrator
{
    public static final int SIMPSON_MAX_ITERATIONS_COUNT = 64;
    
    public SimpsonIntegrator(final double relativeAccuracy, final double absoluteAccuracy, final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(maximalIterationCount, 64, false);
        }
    }
    
    public SimpsonIntegrator(final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(maximalIterationCount, 64, false);
        }
    }
    
    public SimpsonIntegrator() {
        super(3, 64);
    }
    
    @Override
    protected double doIntegrate() throws TooManyEvaluationsException, MaxCountExceededException {
        final TrapezoidIntegrator qtrap = new TrapezoidIntegrator();
        if (this.getMinimalIterationCount() == 1) {
            return (4.0 * qtrap.stage(this, 1) - qtrap.stage(this, 0)) / 3.0;
        }
        double olds = 0.0;
        double oldt = qtrap.stage(this, 0);
        double s;
        while (true) {
            final double t = qtrap.stage(this, this.getIterations());
            this.incrementCount();
            s = (4.0 * t - oldt) / 3.0;
            if (this.getIterations() >= this.getMinimalIterationCount()) {
                final double delta = FastMath.abs(s - olds);
                final double rLimit = this.getRelativeAccuracy() * (FastMath.abs(olds) + FastMath.abs(s)) * 0.5;
                if (delta <= rLimit || delta <= this.getAbsoluteAccuracy()) {
                    break;
                }
            }
            olds = s;
            oldt = t;
        }
        return s;
    }
}
