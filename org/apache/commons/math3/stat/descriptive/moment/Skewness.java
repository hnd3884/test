package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class Skewness extends AbstractStorelessUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = 7101857578996691352L;
    protected ThirdMoment moment;
    protected boolean incMoment;
    
    public Skewness() {
        this.moment = null;
        this.incMoment = true;
        this.moment = new ThirdMoment();
    }
    
    public Skewness(final ThirdMoment m3) {
        this.moment = null;
        this.incMoment = false;
        this.moment = m3;
    }
    
    public Skewness(final Skewness original) throws NullArgumentException {
        this.moment = null;
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
        if (this.moment.n < 3L) {
            return Double.NaN;
        }
        final double variance = this.moment.m2 / (this.moment.n - 1L);
        if (variance < 1.0E-19) {
            return 0.0;
        }
        final double n0 = (double)this.moment.getN();
        return n0 * this.moment.m3 / ((n0 - 1.0) * (n0 - 2.0) * FastMath.sqrt(variance) * variance);
    }
    
    public long getN() {
        return this.moment.getN();
    }
    
    @Override
    public void clear() {
        if (this.incMoment) {
            this.moment.clear();
        }
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        double skew = Double.NaN;
        if (this.test(values, begin, length) && length > 2) {
            final Mean mean = new Mean();
            final double m = mean.evaluate(values, begin, length);
            double accum = 0.0;
            double accum2 = 0.0;
            for (int i = begin; i < begin + length; ++i) {
                final double d = values[i] - m;
                accum += d * d;
                accum2 += d;
            }
            final double variance = (accum - accum2 * accum2 / length) / (length - 1);
            double accum3 = 0.0;
            for (int j = begin; j < begin + length; ++j) {
                final double d2 = values[j] - m;
                accum3 += d2 * d2 * d2;
            }
            accum3 /= variance * FastMath.sqrt(variance);
            final double n0 = length;
            skew = n0 / ((n0 - 1.0) * (n0 - 2.0)) * accum3;
        }
        return skew;
    }
    
    @Override
    public Skewness copy() {
        final Skewness result = new Skewness();
        copy(this, result);
        return result;
    }
    
    public static void copy(final Skewness source, final Skewness dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.moment = new ThirdMoment(source.moment.copy());
        dest.incMoment = source.incMoment;
    }
}
