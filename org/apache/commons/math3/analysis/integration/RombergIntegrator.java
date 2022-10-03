package org.apache.commons.math3.analysis.integration;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;

public class RombergIntegrator extends BaseAbstractUnivariateIntegrator
{
    public static final int ROMBERG_MAX_ITERATIONS_COUNT = 32;
    
    public RombergIntegrator(final double relativeAccuracy, final double absoluteAccuracy, final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(relativeAccuracy, absoluteAccuracy, minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 32) {
            throw new NumberIsTooLargeException(maximalIterationCount, 32, false);
        }
    }
    
    public RombergIntegrator(final int minimalIterationCount, final int maximalIterationCount) throws NotStrictlyPositiveException, NumberIsTooSmallException, NumberIsTooLargeException {
        super(minimalIterationCount, maximalIterationCount);
        if (maximalIterationCount > 32) {
            throw new NumberIsTooLargeException(maximalIterationCount, 32, false);
        }
    }
    
    public RombergIntegrator() {
        super(3, 32);
    }
    
    @Override
    protected double doIntegrate() throws TooManyEvaluationsException, MaxCountExceededException {
        final int m = this.getMaximalIterationCount() + 1;
        double[] previousRow = new double[m];
        double[] currentRow = new double[m];
        final TrapezoidIntegrator qtrap = new TrapezoidIntegrator();
        currentRow[0] = qtrap.stage(this, 0);
        this.incrementCount();
        double olds = currentRow[0];
        double s;
        while (true) {
            final int i = this.getIterations();
            final double[] tmpRow = previousRow;
            previousRow = currentRow;
            currentRow = tmpRow;
            currentRow[0] = qtrap.stage(this, i);
            this.incrementCount();
            for (int j = 1; j <= i; ++j) {
                final double r = (double)((1L << 2 * j) - 1L);
                final double tIJm1 = currentRow[j - 1];
                currentRow[j] = tIJm1 + (tIJm1 - previousRow[j - 1]) / r;
            }
            s = currentRow[i];
            if (i >= this.getMinimalIterationCount()) {
                final double delta = FastMath.abs(s - olds);
                final double rLimit = this.getRelativeAccuracy() * (FastMath.abs(olds) + FastMath.abs(s)) * 0.5;
                if (delta <= rLimit || delta <= this.getAbsoluteAccuracy()) {
                    break;
                }
            }
            olds = s;
        }
        return s;
    }
}
