package org.apache.commons.math3.optimization;

@Deprecated
public class SimpleBounds implements OptimizationData
{
    private final double[] lower;
    private final double[] upper;
    
    public SimpleBounds(final double[] lB, final double[] uB) {
        this.lower = lB.clone();
        this.upper = uB.clone();
    }
    
    public double[] getLower() {
        return this.lower.clone();
    }
    
    public double[] getUpper() {
        return this.upper.clone();
    }
}
