package org.apache.commons.math3.stat.descriptive.summary;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class SumOfLogs extends AbstractStorelessUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = -370076995648386763L;
    private int n;
    private double value;
    
    public SumOfLogs() {
        this.value = 0.0;
        this.n = 0;
    }
    
    public SumOfLogs(final SumOfLogs original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public void increment(final double d) {
        this.value += FastMath.log(d);
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
        this.n = 0;
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        double sumLog = Double.NaN;
        if (this.test(values, begin, length, true)) {
            sumLog = 0.0;
            for (int i = begin; i < begin + length; ++i) {
                sumLog += FastMath.log(values[i]);
            }
        }
        return sumLog;
    }
    
    @Override
    public SumOfLogs copy() {
        final SumOfLogs result = new SumOfLogs();
        copy(this, result);
        return result;
    }
    
    public static void copy(final SumOfLogs source, final SumOfLogs dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.n = source.n;
        dest.value = source.value;
    }
}
