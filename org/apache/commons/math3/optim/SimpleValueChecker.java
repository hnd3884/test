package org.apache.commons.math3.optim;

import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class SimpleValueChecker extends AbstractConvergenceChecker<PointValuePair>
{
    private static final int ITERATION_CHECK_DISABLED = -1;
    private final int maxIterationCount;
    
    public SimpleValueChecker(final double relativeThreshold, final double absoluteThreshold) {
        super(relativeThreshold, absoluteThreshold);
        this.maxIterationCount = -1;
    }
    
    public SimpleValueChecker(final double relativeThreshold, final double absoluteThreshold, final int maxIter) {
        super(relativeThreshold, absoluteThreshold);
        if (maxIter <= 0) {
            throw new NotStrictlyPositiveException(maxIter);
        }
        this.maxIterationCount = maxIter;
    }
    
    @Override
    public boolean converged(final int iteration, final PointValuePair previous, final PointValuePair current) {
        if (this.maxIterationCount != -1 && iteration >= this.maxIterationCount) {
            return true;
        }
        final double p = ((Pair<K, Double>)previous).getValue();
        final double c = ((Pair<K, Double>)current).getValue();
        final double difference = FastMath.abs(p - c);
        final double size = FastMath.max(FastMath.abs(p), FastMath.abs(c));
        return difference <= size * this.getRelativeThreshold() || difference <= this.getAbsoluteThreshold();
    }
}
