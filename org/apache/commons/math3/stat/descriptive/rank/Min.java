package org.apache.commons.math3.stat.descriptive.rank;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class Min extends AbstractStorelessUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = -2941995784909003131L;
    private long n;
    private double value;
    
    public Min() {
        this.n = 0L;
        this.value = Double.NaN;
    }
    
    public Min(final Min original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public void increment(final double d) {
        if (d < this.value || Double.isNaN(this.value)) {
            this.value = d;
        }
        ++this.n;
    }
    
    @Override
    public void clear() {
        this.value = Double.NaN;
        this.n = 0L;
    }
    
    @Override
    public double getResult() {
        return this.value;
    }
    
    public long getN() {
        return this.n;
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        double min = Double.NaN;
        if (this.test(values, begin, length)) {
            min = values[begin];
            for (int i = begin; i < begin + length; ++i) {
                if (!Double.isNaN(values[i])) {
                    min = ((min < values[i]) ? min : values[i]);
                }
            }
        }
        return min;
    }
    
    @Override
    public Min copy() {
        final Min result = new Min();
        copy(this, result);
        return result;
    }
    
    public static void copy(final Min source, final Min dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.n = source.n;
        dest.value = source.value;
    }
}
