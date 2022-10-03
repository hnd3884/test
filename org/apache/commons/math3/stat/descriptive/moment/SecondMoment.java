package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;

public class SecondMoment extends FirstMoment implements Serializable
{
    private static final long serialVersionUID = 3942403127395076445L;
    protected double m2;
    
    public SecondMoment() {
        this.m2 = Double.NaN;
    }
    
    public SecondMoment(final SecondMoment original) throws NullArgumentException {
        super(original);
        this.m2 = original.m2;
    }
    
    @Override
    public void increment(final double d) {
        if (this.n < 1L) {
            final double n = 0.0;
            this.m2 = n;
            this.m1 = n;
        }
        super.increment(d);
        this.m2 += (this.n - 1.0) * this.dev * this.nDev;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.m2 = Double.NaN;
    }
    
    @Override
    public double getResult() {
        return this.m2;
    }
    
    @Override
    public SecondMoment copy() {
        final SecondMoment result = new SecondMoment();
        copy(this, result);
        return result;
    }
    
    public static void copy(final SecondMoment source, final SecondMoment dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        FirstMoment.copy(source, dest);
        dest.m2 = source.m2;
    }
}
