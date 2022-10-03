package org.apache.commons.math3.optim;

public abstract class AbstractConvergenceChecker<PAIR> implements ConvergenceChecker<PAIR>
{
    private final double relativeThreshold;
    private final double absoluteThreshold;
    
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
}
