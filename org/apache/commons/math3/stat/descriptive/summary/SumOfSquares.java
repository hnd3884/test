package org.apache.commons.math3.stat.descriptive.summary;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class SumOfSquares extends AbstractStorelessUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = 1460986908574398008L;
    private long n;
    private double value;
    
    public SumOfSquares() {
        this.n = 0L;
        this.value = 0.0;
    }
    
    public SumOfSquares(final SumOfSquares original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public void increment(final double d) {
        this.value += d * d;
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
        double sumSq = Double.NaN;
        if (this.test(values, begin, length, true)) {
            sumSq = 0.0;
            for (int i = begin; i < begin + length; ++i) {
                sumSq += values[i] * values[i];
            }
        }
        return sumSq;
    }
    
    @Override
    public SumOfSquares copy() {
        final SumOfSquares result = new SumOfSquares();
        copy(this, result);
        return result;
    }
    
    public static void copy(final SumOfSquares source, final SumOfSquares dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.n = source.n;
        dest.value = source.value;
    }
}
