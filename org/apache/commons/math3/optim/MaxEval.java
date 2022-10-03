package org.apache.commons.math3.optim;

import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class MaxEval implements OptimizationData
{
    private final int maxEval;
    
    public MaxEval(final int max) {
        if (max <= 0) {
            throw new NotStrictlyPositiveException(max);
        }
        this.maxEval = max;
    }
    
    public int getMaxEval() {
        return this.maxEval;
    }
    
    public static MaxEval unlimited() {
        return new MaxEval(Integer.MAX_VALUE);
    }
}
