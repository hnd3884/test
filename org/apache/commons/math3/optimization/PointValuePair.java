package org.apache.commons.math3.optimization;

import java.io.Serializable;
import org.apache.commons.math3.util.Pair;

@Deprecated
public class PointValuePair extends Pair<double[], Double> implements Serializable
{
    private static final long serialVersionUID = 20120513L;
    
    public PointValuePair(final double[] point, final double value) {
        this(point, value, true);
    }
    
    public PointValuePair(final double[] point, final double value, final boolean copyArray) {
        super((double[])(copyArray ? ((point == null) ? null : ((double[])point.clone())) : point), value);
    }
    
    public double[] getPoint() {
        final double[] p = ((Pair<double[], V>)this).getKey();
        return (double[])((p == null) ? null : ((double[])p.clone()));
    }
    
    public double[] getPointRef() {
        return ((Pair<double[], V>)this).getKey();
    }
    
    private Object writeReplace() {
        return new DataTransferObject(((Pair<double[], V>)this).getKey(), ((Pair<K, Double>)this).getValue());
    }
    
    private static class DataTransferObject implements Serializable
    {
        private static final long serialVersionUID = 20120513L;
        private final double[] point;
        private final double value;
        
        DataTransferObject(final double[] point, final double value) {
            this.point = point.clone();
            this.value = value;
        }
        
        private Object readResolve() {
            return new PointValuePair(this.point, this.value, false);
        }
    }
}
