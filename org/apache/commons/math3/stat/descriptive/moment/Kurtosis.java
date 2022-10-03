package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class Kurtosis extends AbstractStorelessUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = 2784465764798260919L;
    protected FourthMoment moment;
    protected boolean incMoment;
    
    public Kurtosis() {
        this.incMoment = true;
        this.moment = new FourthMoment();
    }
    
    public Kurtosis(final FourthMoment m4) {
        this.incMoment = false;
        this.moment = m4;
    }
    
    public Kurtosis(final Kurtosis original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public void increment(final double d) {
        if (this.incMoment) {
            this.moment.increment(d);
        }
    }
    
    @Override
    public double getResult() {
        double kurtosis = Double.NaN;
        if (this.moment.getN() > 3L) {
            final double variance = this.moment.m2 / (this.moment.n - 1L);
            if (this.moment.n <= 3L || variance < 1.0E-19) {
                kurtosis = 0.0;
            }
            else {
                final double n = (double)this.moment.n;
                kurtosis = (n * (n + 1.0) * this.moment.getResult() - 3.0 * this.moment.m2 * this.moment.m2 * (n - 1.0)) / ((n - 1.0) * (n - 2.0) * (n - 3.0) * variance * variance);
            }
        }
        return kurtosis;
    }
    
    @Override
    public void clear() {
        if (this.incMoment) {
            this.moment.clear();
        }
    }
    
    public long getN() {
        return this.moment.getN();
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        double kurt = Double.NaN;
        if (this.test(values, begin, length) && length > 3) {
            final Variance variance = new Variance();
            variance.incrementAll(values, begin, length);
            final double mean = variance.moment.m1;
            final double stdDev = FastMath.sqrt(variance.getResult());
            double accum3 = 0.0;
            for (int i = begin; i < begin + length; ++i) {
                accum3 += FastMath.pow(values[i] - mean, 4.0);
            }
            accum3 /= FastMath.pow(stdDev, 4.0);
            final double n0 = length;
            final double coefficientOne = n0 * (n0 + 1.0) / ((n0 - 1.0) * (n0 - 2.0) * (n0 - 3.0));
            final double termTwo = 3.0 * FastMath.pow(n0 - 1.0, 2.0) / ((n0 - 2.0) * (n0 - 3.0));
            kurt = coefficientOne * accum3 - termTwo;
        }
        return kurt;
    }
    
    @Override
    public Kurtosis copy() {
        final Kurtosis result = new Kurtosis();
        copy(this, result);
        return result;
    }
    
    public static void copy(final Kurtosis source, final Kurtosis dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.moment = source.moment.copy();
        dest.incMoment = source.incMoment;
    }
}
