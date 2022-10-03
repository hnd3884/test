package org.apache.commons.math3.optimization;

@Deprecated
public class Target implements OptimizationData
{
    private final double[] target;
    
    public Target(final double[] observations) {
        this.target = observations.clone();
    }
    
    public double[] getTarget() {
        return this.target.clone();
    }
}
