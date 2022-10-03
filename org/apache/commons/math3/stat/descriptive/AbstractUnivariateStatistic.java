package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public abstract class AbstractUnivariateStatistic implements UnivariateStatistic
{
    private double[] storedData;
    
    public void setData(final double[] values) {
        this.storedData = (double[])((values == null) ? null : ((double[])values.clone()));
    }
    
    public double[] getData() {
        return (double[])((this.storedData == null) ? null : ((double[])this.storedData.clone()));
    }
    
    protected double[] getDataRef() {
        return this.storedData;
    }
    
    public void setData(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        if (begin < 0) {
            throw new NotPositiveException(LocalizedFormats.START_POSITION, begin);
        }
        if (length < 0) {
            throw new NotPositiveException(LocalizedFormats.LENGTH, length);
        }
        if (begin + length > values.length) {
            throw new NumberIsTooLargeException(LocalizedFormats.SUBARRAY_ENDS_AFTER_ARRAY_END, begin + length, values.length, true);
        }
        System.arraycopy(values, begin, this.storedData = new double[length], 0, length);
    }
    
    public double evaluate() throws MathIllegalArgumentException {
        return this.evaluate(this.storedData);
    }
    
    public double evaluate(final double[] values) throws MathIllegalArgumentException {
        this.test(values, 0, 0);
        return this.evaluate(values, 0, values.length);
    }
    
    public abstract double evaluate(final double[] p0, final int p1, final int p2) throws MathIllegalArgumentException;
    
    public abstract UnivariateStatistic copy();
    
    protected boolean test(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return MathArrays.verifyValues(values, begin, length, false);
    }
    
    protected boolean test(final double[] values, final int begin, final int length, final boolean allowEmpty) throws MathIllegalArgumentException {
        return MathArrays.verifyValues(values, begin, length, allowEmpty);
    }
    
    protected boolean test(final double[] values, final double[] weights, final int begin, final int length) throws MathIllegalArgumentException {
        return MathArrays.verifyValues(values, weights, begin, length, false);
    }
    
    protected boolean test(final double[] values, final double[] weights, final int begin, final int length, final boolean allowEmpty) throws MathIllegalArgumentException {
        return MathArrays.verifyValues(values, weights, begin, length, allowEmpty);
    }
}
