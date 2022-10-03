package org.apache.commons.math3.optimization;

@Deprecated
public class InitialGuess implements OptimizationData
{
    private final double[] init;
    
    public InitialGuess(final double[] startPoint) {
        this.init = startPoint.clone();
    }
    
    public double[] getInitialGuess() {
        return this.init.clone();
    }
}
