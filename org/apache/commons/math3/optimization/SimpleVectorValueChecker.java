package org.apache.commons.math3.optimization;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

@Deprecated
public class SimpleVectorValueChecker extends AbstractConvergenceChecker<PointVectorValuePair>
{
    private static final int ITERATION_CHECK_DISABLED = -1;
    private final int maxIterationCount;
    
    @Deprecated
    public SimpleVectorValueChecker() {
        this.maxIterationCount = -1;
    }
    
    public SimpleVectorValueChecker(final double relativeThreshold, final double absoluteThreshold) {
        super(relativeThreshold, absoluteThreshold);
        this.maxIterationCount = -1;
    }
    
    public SimpleVectorValueChecker(final double relativeThreshold, final double absoluteThreshold, final int maxIter) {
        super(relativeThreshold, absoluteThreshold);
        if (maxIter <= 0) {
            throw new NotStrictlyPositiveException(maxIter);
        }
        this.maxIterationCount = maxIter;
    }
    
    @Override
    public boolean converged(final int iteration, final PointVectorValuePair previous, final PointVectorValuePair current) {
        if (this.maxIterationCount != -1 && iteration >= this.maxIterationCount) {
            return true;
        }
        final double[] p = previous.getValueRef();
        final double[] c = current.getValueRef();
        for (int i = 0; i < p.length; ++i) {
            final double pi = p[i];
            final double ci = c[i];
            final double difference = FastMath.abs(pi - ci);
            final double size = FastMath.max(FastMath.abs(pi), FastMath.abs(ci));
            if (difference > size * this.getRelativeThreshold() && difference > this.getAbsoluteThreshold()) {
                return false;
            }
        }
        return true;
    }
}
