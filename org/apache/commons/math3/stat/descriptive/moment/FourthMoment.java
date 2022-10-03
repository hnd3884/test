package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;

class FourthMoment extends ThirdMoment implements Serializable
{
    private static final long serialVersionUID = 4763990447117157611L;
    private double m4;
    
    FourthMoment() {
        this.m4 = Double.NaN;
    }
    
    FourthMoment(final FourthMoment original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public void increment(final double d) {
        if (this.n < 1L) {
            this.m4 = 0.0;
            this.m3 = 0.0;
            this.m2 = 0.0;
            this.m1 = 0.0;
        }
        final double prevM3 = this.m3;
        final double prevM4 = this.m2;
        super.increment(d);
        final double n0 = (double)this.n;
        this.m4 = this.m4 - 4.0 * this.nDev * prevM3 + 6.0 * this.nDevSq * prevM4 + (n0 * n0 - 3.0 * (n0 - 1.0)) * (this.nDevSq * this.nDevSq * (n0 - 1.0) * n0);
    }
    
    @Override
    public double getResult() {
        return this.m4;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.m4 = Double.NaN;
    }
    
    @Override
    public FourthMoment copy() {
        final FourthMoment result = new FourthMoment();
        copy(this, result);
        return result;
    }
    
    public static void copy(final FourthMoment source, final FourthMoment dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        ThirdMoment.copy(source, dest);
        dest.m4 = source.m4;
    }
}
