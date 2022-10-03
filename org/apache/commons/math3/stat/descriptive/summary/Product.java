package org.apache.commons.math3.stat.descriptive.summary;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.WeightedEvaluation;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class Product extends AbstractStorelessUnivariateStatistic implements Serializable, WeightedEvaluation
{
    private static final long serialVersionUID = 2824226005990582538L;
    private long n;
    private double value;
    
    public Product() {
        this.n = 0L;
        this.value = 1.0;
    }
    
    public Product(final Product original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public void increment(final double d) {
        this.value *= d;
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
        this.value = 1.0;
        this.n = 0L;
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        double product = Double.NaN;
        if (this.test(values, begin, length, true)) {
            product = 1.0;
            for (int i = begin; i < begin + length; ++i) {
                product *= values[i];
            }
        }
        return product;
    }
    
    public double evaluate(final double[] values, final double[] weights, final int begin, final int length) throws MathIllegalArgumentException {
        double product = Double.NaN;
        if (this.test(values, weights, begin, length, true)) {
            product = 1.0;
            for (int i = begin; i < begin + length; ++i) {
                product *= FastMath.pow(values[i], weights[i]);
            }
        }
        return product;
    }
    
    public double evaluate(final double[] values, final double[] weights) throws MathIllegalArgumentException {
        return this.evaluate(values, weights, 0, values.length);
    }
    
    @Override
    public Product copy() {
        final Product result = new Product();
        copy(this, result);
        return result;
    }
    
    public static void copy(final Product source, final Product dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.n = source.n;
        dest.value = source.value;
    }
}
