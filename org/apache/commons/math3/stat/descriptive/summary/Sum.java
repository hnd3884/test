package org.apache.commons.math3.stat.descriptive.summary;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class Sum extends AbstractStorelessUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = -8231831954703408316L;
    private long n;
    private double value;
    
    public Sum() {
        this.n = 0L;
        this.value = 0.0;
    }
    
    public Sum(final Sum original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public void increment(final double d) {
        this.value += d;
        ++this.n;
    }
    
    @Override
    public double getResult() {
        return this.value;
    }
    
    public long getN() {
        return this.n;
    }
    
    @Override
    public void clear() {
        this.value = 0.0;
        this.n = 0L;
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        double sum = Double.NaN;
        if (this.test(values, begin, length, true)) {
            sum = 0.0;
            for (int i = begin; i < begin + length; ++i) {
                sum += values[i];
            }
        }
        return sum;
    }
    
    public double evaluate(final double[] values, final double[] weights, final int begin, final int length) throws MathIllegalArgumentException {
        double sum = Double.NaN;
        if (this.test(values, weights, begin, length, true)) {
            sum = 0.0;
            for (int i = begin; i < begin + length; ++i) {
                sum += values[i] * weights[i];
            }
        }
        return sum;
    }
    
    public double evaluate(final double[] values, final double[] weights) throws MathIllegalArgumentException {
        return this.evaluate(values, weights, 0, values.length);
    }
    
    @Override
    public Sum copy() {
        final Sum result = new Sum();
        copy(this, result);
        return result;
    }
    
    public static void copy(final Sum source, final Sum dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.n = source.n;
        dest.value = source.value;
    }
}
