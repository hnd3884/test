package org.apache.commons.math3.optimization;

import java.io.Serializable;
import org.apache.commons.math3.util.Pair;

@Deprecated
public class PointVectorValuePair extends Pair<double[], double[]> implements Serializable
{
    private static final long serialVersionUID = 20120513L;
    
    public PointVectorValuePair(final double[] point, final double[] value) {
        this(point, value, true);
    }
    
    public PointVectorValuePair(final double[] point, final double[] value, final boolean copyArray) {
        super((double[])(copyArray ? ((point == null) ? null : ((double[])point.clone())) : point), (double[])(copyArray ? ((value == null) ? null : ((double[])value.clone())) : value));
    }
    
    public double[] getPoint() {
        final double[] p = ((Pair<double[], V>)this).getKey();
        return (double[])((p == null) ? null : ((double[])p.clone()));
    }
    
    public double[] getPointRef() {
        return ((Pair<double[], V>)this).getKey();
    }
    
    @Override
    public double[] getValue() {
        final double[] v = super.getValue();
        return (double[])((v == null) ? null : ((double[])v.clone()));
    }
    
    public double[] getValueRef() {
        return super.getValue();
    }
    
    private Object writeReplace() {
        return new DataTransferObject(((Pair<double[], V>)this).getKey(), this.getValue());
    }
    
    private static class DataTransferObject implements Serializable
    {
        private static final long serialVersionUID = 20120513L;
        private final double[] point;
        private final double[] value;
        
        DataTransferObject(final double[] point, final double[] value) {
            this.point = point.clone();
            this.value = value.clone();
        }
        
        private Object readResolve() {
            return new PointVectorValuePair(this.point, this.value, false);
        }
    }
}
