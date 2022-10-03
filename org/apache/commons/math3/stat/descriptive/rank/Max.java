package org.apache.commons.math3.stat.descriptive.rank;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class Max extends AbstractStorelessUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = -5593383832225844641L;
    private long n;
    private double value;
    
    public Max() {
        this.n = 0L;
        this.value = Double.NaN;
    }
    
    public Max(final Max original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public void increment(final double d) {
        if (d > this.value || Double.isNaN(this.value)) {
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
        double max = Double.NaN;
        if (this.test(values, begin, length)) {
            max = values[begin];
            for (int i = begin; i < begin + length; ++i) {
                if (!Double.isNaN(values[i])) {
                    max = ((max > values[i]) ? max : values[i]);
                }
            }
        }
        return max;
    }
    
    @Override
    public Max copy() {
        final Max result = new Max();
        copy(this, result);
        return result;
    }
    
    public static void copy(final Max source, final Max dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.n = source.n;
        dest.value = source.value;
    }
}
