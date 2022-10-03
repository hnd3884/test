package org.apache.commons.math3.optimization;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.util.Pair;

@Deprecated
public class SimplePointChecker<PAIR extends Pair<double[], ?>> extends AbstractConvergenceChecker<PAIR>
{
    private static final int ITERATION_CHECK_DISABLED = -1;
    private final int maxIterationCount;
    
    @Deprecated
    public SimplePointChecker() {
        this.maxIterationCount = -1;
    }
    
    public SimplePointChecker(final double relativeThreshold, final double absoluteThreshold) {
        super(relativeThreshold, absoluteThreshold);
        this.maxIterationCount = -1;
    }
    
    public SimplePointChecker(final double relativeThreshold, final double absoluteThreshold, final int maxIter) {
        super(relativeThreshold, absoluteThreshold);
        if (maxIter <= 0) {
            throw new NotStrictlyPositiveException(maxIter);
        }
        this.maxIterationCount = maxIter;
    }
    
    @Override
    public boolean converged(final int iteration, final PAIR previous, final PAIR current) {
        if (this.maxIterationCount != -1 && iteration >= this.maxIterationCount) {
            return true;
        }
        final double[] p = ((Pair<double[], V>)previous).getKey();
        final double[] c = ((Pair<double[], V>)current).getKey();
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
