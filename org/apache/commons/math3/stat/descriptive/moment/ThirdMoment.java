package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;

class ThirdMoment extends SecondMoment implements Serializable
{
    private static final long serialVersionUID = -7818711964045118679L;
    protected double m3;
    protected double nDevSq;
    
    ThirdMoment() {
        this.m3 = Double.NaN;
        this.nDevSq = Double.NaN;
    }
    
    ThirdMoment(final ThirdMoment original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public void increment(final double d) {
        if (this.n < 1L) {
            final double m3 = 0.0;
            this.m1 = m3;
            this.m2 = m3;
            this.m3 = m3;
        }
        final double prevM2 = this.m2;
        super.increment(d);
        this.nDevSq = this.nDev * this.nDev;
        final double n0 = (double)this.n;
        this.m3 = this.m3 - 3.0 * this.nDev * prevM2 + (n0 - 1.0) * (n0 - 2.0) * this.nDevSq * this.dev;
    }
    
    @Override
    public double getResult() {
        return this.m3;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.m3 = Double.NaN;
        this.nDevSq = Double.NaN;
    }
    
    @Override
    public ThirdMoment copy() {
        final ThirdMoment result = new ThirdMoment();
        copy(this, result);
        return result;
    }
    
    public static void copy(final ThirdMoment source, final ThirdMoment dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        SecondMoment.copy(source, dest);
        dest.m3 = source.m3;
        dest.nDevSq = source.nDevSq;
    }
}
