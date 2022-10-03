package org.apache.commons.math3.optim.univariate;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.optim.AbstractConvergenceChecker;

public class SimpleUnivariateValueChecker extends AbstractConvergenceChecker<UnivariatePointValuePair>
{
    private static final int ITERATION_CHECK_DISABLED = -1;
    private final int maxIterationCount;
    
    public SimpleUnivariateValueChecker(final double relativeThreshold, final double absoluteThreshold) {
        super(relativeThreshold, absoluteThreshold);
        this.maxIterationCount = -1;
    }
    
    public SimpleUnivariateValueChecker(final double relativeThreshold, final double absoluteThreshold, final int maxIter) {
        super(relativeThreshold, absoluteThreshold);
        if (maxIter <= 0) {
            throw new NotStrictlyPositiveException(maxIter);
        }
        this.maxIterationCount = maxIter;
    }
    
    @Override
    public boolean converged(final int iteration, final UnivariatePointValuePair previous, final UnivariatePointValuePair current) {
        if (this.maxIterationCount != -1 && iteration >= this.maxIterationCount) {
            return true;
        }
        final double p = previous.getValue();
        final double c = current.getValue();
        final double difference = FastMath.abs(p - c);
        final double size = FastMath.max(FastMath.abs(p), FastMath.abs(c));
        return difference <= size * this.getRelativeThreshold() || difference <= this.getAbsoluteThreshold();
    }
}
