package org.apache.commons.math3.optimization;

import org.apache.commons.math3.util.Precision;

@Deprecated
public abstract class AbstractConvergenceChecker<PAIR> implements ConvergenceChecker<PAIR>
{
    @Deprecated
    private static final double DEFAULT_RELATIVE_THRESHOLD;
    @Deprecated
    private static final double DEFAULT_ABSOLUTE_THRESHOLD;
    private final double relativeThreshold;
    private final double absoluteThreshold;
    
    @Deprecated
    public AbstractConvergenceChecker() {
        this.relativeThreshold = AbstractConvergenceChecker.DEFAULT_RELATIVE_THRESHOLD;
        this.absoluteThreshold = AbstractConvergenceChecker.DEFAULT_ABSOLUTE_THRESHOLD;
    }
    
    public AbstractConvergenceChecker(final double relativeThreshold, final double absoluteThreshold) {
        this.relativeThreshold = relativeThreshold;
        this.absoluteThreshold = absoluteThreshold;
    }
    
    public double getRelativeThreshold() {
        return this.relativeThreshold;
    }
    
    public double getAbsoluteThreshold() {
        return this.absoluteThreshold;
    }
    
    public abstract boolean converged(final int p0, final PAIR p1, final PAIR p2);
    
    static {
        DEFAULT_RELATIVE_THRESHOLD = 100.0 * Precision.EPSILON;
        DEFAULT_ABSOLUTE_THRESHOLD = 100.0 * Precision.SAFE_MIN;
    }
}
