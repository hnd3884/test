package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.WeightedEvaluation;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class Mean extends AbstractStorelessUnivariateStatistic implements Serializable, WeightedEvaluation
{
    private static final long serialVersionUID = -1296043746617791564L;
    protected FirstMoment moment;
    protected boolean incMoment;
    
    public Mean() {
        this.incMoment = true;
        this.moment = new FirstMoment();
    }
    
    public Mean(final FirstMoment m1) {
        this.moment = m1;
        this.incMoment = false;
    }
    
    public Mean(final Mean original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public void increment(final double d) {
        if (this.incMoment) {
            this.moment.increment(d);
        }
    }
    
    @Override
    public void clear() {
        if (this.incMoment) {
            this.moment.clear();
        }
    }
    
    @Override
    public double getResult() {
        return this.moment.m1;
    }
    
    public long getN() {
        return this.moment.getN();
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        if (this.test(values, begin, length)) {
            final Sum sum = new Sum();
            final double sampleSize = length;
            final double xbar = sum.evaluate(values, begin, length) / sampleSize;
            double correction = 0.0;
            for (int i = begin; i < begin + length; ++i) {
                correction += values[i] - xbar;
            }
            return xbar + correction / sampleSize;
        }
        return Double.NaN;
    }
    
    public double evaluate(final double[] values, final double[] weights, final int begin, final int length) throws MathIllegalArgumentException {
        if (this.test(values, weights, begin, length)) {
            final Sum sum = new Sum();
            final double sumw = sum.evaluate(weights, begin, length);
            final double xbarw = sum.evaluate(values, weights, begin, length) / sumw;
            double correction = 0.0;
            for (int i = begin; i < begin + length; ++i) {
                correction += weights[i] * (values[i] - xbarw);
            }
            return xbarw + correction / sumw;
        }
        return Double.NaN;
    }
    
    public double evaluate(final double[] values, final double[] weights) throws MathIllegalArgumentException {
        return this.evaluate(values, weights, 0, values.length);
    }
    
    @Override
    public Mean copy() {
        final Mean result = new Mean();
        copy(this, result);
        return result;
    }
    
    public static void copy(final Mean source, final Mean dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.incMoment = source.incMoment;
        dest.moment = source.moment.copy();
    }
}
