package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public abstract class AbstractStorelessUnivariateStatistic extends AbstractUnivariateStatistic implements StorelessUnivariateStatistic
{
    @Override
    public double evaluate(final double[] values) throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        return this.evaluate(values, 0, values.length);
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        if (this.test(values, begin, length)) {
            this.clear();
            this.incrementAll(values, begin, length);
        }
        return this.getResult();
    }
    
    @Override
    public abstract StorelessUnivariateStatistic copy();
    
    public abstract void clear();
    
    public abstract double getResult();
    
    public abstract void increment(final double p0);
    
    public void incrementAll(final double[] values) throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        this.incrementAll(values, 0, values.length);
    }
    
    public void incrementAll(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        if (this.test(values, begin, length)) {
            for (int k = begin + length, i = begin; i < k; ++i) {
                this.increment(values[i]);
            }
        }
    }
    
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof AbstractStorelessUnivariateStatistic)) {
            return false;
        }
        final AbstractStorelessUnivariateStatistic stat = (AbstractStorelessUnivariateStatistic)object;
        return Precision.equalsIncludingNaN(stat.getResult(), this.getResult()) && Precision.equalsIncludingNaN((float)stat.getN(), (float)this.getN());
    }
    
    @Override
    public int hashCode() {
        return 31 * (31 + MathUtils.hash(this.getResult())) + MathUtils.hash((double)this.getN());
    }
}
