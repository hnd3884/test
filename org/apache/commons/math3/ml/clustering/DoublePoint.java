package org.apache.commons.math3.ml.clustering;

import java.util.Arrays;
import java.io.Serializable;

public class DoublePoint implements Clusterable, Serializable
{
    private static final long serialVersionUID = 3946024775784901369L;
    private final double[] point;
    
    public DoublePoint(final double[] point) {
        this.point = point;
    }
    
    public DoublePoint(final int[] point) {
        this.point = new double[point.length];
        for (int i = 0; i < point.length; ++i) {
            this.point[i] = point[i];
        }
    }
    
    public double[] getPoint() {
        return this.point;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof DoublePoint && Arrays.equals(this.point, ((DoublePoint)other).point);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.point);
    }
    
    @Override
    public String toString() {
        return Arrays.toString(this.point);
    }
}
