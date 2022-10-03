package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic;

public class SemiVariance extends AbstractUnivariateStatistic implements Serializable
{
    public static final Direction UPSIDE_VARIANCE;
    public static final Direction DOWNSIDE_VARIANCE;
    private static final long serialVersionUID = -2653430366886024994L;
    private boolean biasCorrected;
    private Direction varianceDirection;
    
    public SemiVariance() {
        this.biasCorrected = true;
        this.varianceDirection = Direction.DOWNSIDE;
    }
    
    public SemiVariance(final boolean biasCorrected) {
        this.biasCorrected = true;
        this.varianceDirection = Direction.DOWNSIDE;
        this.biasCorrected = biasCorrected;
    }
    
    public SemiVariance(final Direction direction) {
        this.biasCorrected = true;
        this.varianceDirection = Direction.DOWNSIDE;
        this.varianceDirection = direction;
    }
    
    public SemiVariance(final boolean corrected, final Direction direction) {
        this.biasCorrected = true;
        this.varianceDirection = Direction.DOWNSIDE;
        this.biasCorrected = corrected;
        this.varianceDirection = direction;
    }
    
    public SemiVariance(final SemiVariance original) throws NullArgumentException {
        this.biasCorrected = true;
        this.varianceDirection = Direction.DOWNSIDE;
        copy(original, this);
    }
    
    @Override
    public SemiVariance copy() {
        final SemiVariance result = new SemiVariance();
        copy(this, result);
        return result;
    }
    
    public static void copy(final SemiVariance source, final SemiVariance dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.biasCorrected = source.biasCorrected;
        dest.varianceDirection = source.varianceDirection;
    }
    
    @Override
    public double evaluate(final double[] values, final int start, final int length) throws MathIllegalArgumentException {
        final double m = new Mean().evaluate(values, start, length);
        return this.evaluate(values, m, this.varianceDirection, this.biasCorrected, 0, values.length);
    }
    
    public double evaluate(final double[] values, final Direction direction) throws MathIllegalArgumentException {
        final double m = new Mean().evaluate(values);
        return this.evaluate(values, m, direction, this.biasCorrected, 0, values.length);
    }
    
    public double evaluate(final double[] values, final double cutoff) throws MathIllegalArgumentException {
        return this.evaluate(values, cutoff, this.varianceDirection, this.biasCorrected, 0, values.length);
    }
    
    public double evaluate(final double[] values, final double cutoff, final Direction direction) throws MathIllegalArgumentException {
        return this.evaluate(values, cutoff, direction, this.biasCorrected, 0, values.length);
    }
    
    public double evaluate(final double[] values, final double cutoff, final Direction direction, final boolean corrected, final int start, final int length) throws MathIllegalArgumentException {
        this.test(values, start, length);
        if (values.length == 0) {
            return Double.NaN;
        }
        if (values.length == 1) {
            return 0.0;
        }
        final boolean booleanDirection = direction.getDirection();
        double dev = 0.0;
        double sumsq = 0.0;
        for (int i = start; i < length; ++i) {
            if (values[i] > cutoff == booleanDirection) {
                dev = values[i] - cutoff;
                sumsq += dev * dev;
            }
        }
        if (corrected) {
            return sumsq / (length - 1.0);
        }
        return sumsq / length;
    }
    
    public boolean isBiasCorrected() {
        return this.biasCorrected;
    }
    
    public void setBiasCorrected(final boolean biasCorrected) {
        this.biasCorrected = biasCorrected;
    }
    
    public Direction getVarianceDirection() {
        return this.varianceDirection;
    }
    
    public void setVarianceDirection(final Direction varianceDirection) {
        this.varianceDirection = varianceDirection;
    }
    
    static {
        UPSIDE_VARIANCE = Direction.UPSIDE;
        DOWNSIDE_VARIANCE = Direction.DOWNSIDE;
    }
    
    public enum Direction
    {
        UPSIDE(true), 
        DOWNSIDE(false);
        
        private boolean direction;
        
        private Direction(final boolean b) {
            this.direction = b;
        }
        
        boolean getDirection() {
            return this.direction;
        }
    }
}
