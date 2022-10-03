package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;

public class MidPointIntegrator extends BaseAbstractUnivariateIntegrator
{
    public static final int MIDPOINT_MAX_ITERATIONS_COUNT = 64;
    
    public MidPointIntegrator(final double relativeAccuracy, final double absoluteAccuracy, final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(maximalIterationCount, 64, false);
        }
    }
    
    public MidPointIntegrator(final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 64) {
            throw new NumberIsTooLargeException(maximalIterationCount, 64, false);
        }
    }
    
    public MidPointIntegrator() {
        super(3, 64);
    }
    
    private double stage(final int n, final double previousStageResult, final double min, final double diffMaxMin) throws TooManyEvaluationsException {
        final long np = 1L << n - 1;
        double sum = 0.0;
        final double spacing = diffMaxMin / np;
        double x = min + 0.5 * spacing;
        for (long i = 0L; i < np; ++i) {
            sum += this.computeObjectiveValue(x);
            x += spacing;
        }
        return 0.5 * (previousStageResult + sum * spacing);
    }
    
    @Override
    protected double doIntegrate() throws MathIllegalArgumentException, TooManyEvaluationsException, MaxCountExceededException {
        final double min = this.getMin();
        final double diff = this.getMax() - min;
        final double midPoint = min + 0.5 * diff;
        double oldt = diff * this.computeObjectiveValue(midPoint);
        double t;
        while (true) {
            this.incrementCount();
            final int i = this.getIterations();
            t = this.stage(i, oldt, min, diff);
            if (i >= this.getMinimalIterationCount()) {
                final double delta = FastMath.abs(t - oldt);
                final double rLimit = this.getRelativeAccuracy() * (FastMath.abs(oldt) + FastMath.abs(t)) * 0.5;
                if (delta <= rLimit || delta <= this.getAbsoluteAccuracy()) {
                    break;
                }
            }
            oldt = t;
        }
        return t;
    }
}
