package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class StandardDeviation extends AbstractStorelessUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = 5728716329662425188L;
    private Variance variance;
    
    public StandardDeviation() {
        this.variance = null;
        this.variance = new Variance();
    }
    
    public StandardDeviation(final SecondMoment m2) {
        this.variance = null;
        this.variance = new Variance(m2);
    }
    
    public StandardDeviation(final StandardDeviation original) throws NullArgumentException {
        this.variance = null;
        copy(original, this);
    }
    
    public StandardDeviation(final boolean isBiasCorrected) {
        this.variance = null;
        this.variance = new Variance(isBiasCorrected);
    }
    
    public StandardDeviation(final boolean isBiasCorrected, final SecondMoment m2) {
        this.variance = null;
        this.variance = new Variance(isBiasCorrected, m2);
    }
    
    @Override
    public void increment(final double d) {
        this.variance.increment(d);
    }
    
    public long getN() {
        return this.variance.getN();
    }
    
    @Override
    public double getResult() {
        return FastMath.sqrt(this.variance.getResult());
    }
    
    @Override
    public void clear() {
        this.variance.clear();
    }
    
    @Override
    public double evaluate(final double[] values) throws MathIllegalArgumentException {
        return FastMath.sqrt(this.variance.evaluate(values));
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return FastMath.sqrt(this.variance.evaluate(values, begin, length));
    }
    
    public double evaluate(final double[] values, final double mean, final int begin, final int length) throws MathIllegalArgumentException {
        return FastMath.sqrt(this.variance.evaluate(values, mean, begin, length));
    }
    
    public double evaluate(final double[] values, final double mean) throws MathIllegalArgumentException {
        return FastMath.sqrt(this.variance.evaluate(values, mean));
    }
    
    public boolean isBiasCorrected() {
        return this.variance.isBiasCorrected();
    }
    
    public void setBiasCorrected(final boolean isBiasCorrected) {
        this.variance.setBiasCorrected(isBiasCorrected);
    }
    
    @Override
    public StandardDeviation copy() {
        final StandardDeviation result = new StandardDeviation();
        copy(this, result);
        return result;
    }
    
    public static void copy(final StandardDeviation source, final StandardDeviation dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.variance = source.variance.copy();
    }
}
