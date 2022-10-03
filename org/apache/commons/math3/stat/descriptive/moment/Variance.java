package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.WeightedEvaluation;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class Variance extends AbstractStorelessUnivariateStatistic implements Serializable, WeightedEvaluation
{
    private static final long serialVersionUID = -9111962718267217978L;
    protected SecondMoment moment;
    protected boolean incMoment;
    private boolean isBiasCorrected;
    
    public Variance() {
        this.moment = null;
        this.incMoment = true;
        this.isBiasCorrected = true;
        this.moment = new SecondMoment();
    }
    
    public Variance(final SecondMoment m2) {
        this.moment = null;
        this.incMoment = true;
        this.isBiasCorrected = true;
        this.incMoment = false;
        this.moment = m2;
    }
    
    public Variance(final boolean isBiasCorrected) {
        this.moment = null;
        this.incMoment = true;
        this.isBiasCorrected = true;
        this.moment = new SecondMoment();
        this.isBiasCorrected = isBiasCorrected;
    }
    
    public Variance(final boolean isBiasCorrected, final SecondMoment m2) {
        this.moment = null;
        this.incMoment = true;
        this.isBiasCorrected = true;
        this.incMoment = false;
        this.moment = m2;
        this.isBiasCorrected = isBiasCorrected;
    }
    
    public Variance(final Variance original) throws NullArgumentException {
        this.moment = null;
        this.incMoment = true;
        this.isBiasCorrected = true;
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
        if (this.moment.n == 0L) {
            return Double.NaN;
        }
        if (this.moment.n == 1L) {
            return 0.0;
        }
        if (this.isBiasCorrected) {
            return this.moment.m2 / (this.moment.n - 1.0);
        }
        return this.moment.m2 / this.moment.n;
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
    public double evaluate(final double[] values) throws MathIllegalArgumentException {
        if (values == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        return this.evaluate(values, 0, values.length);
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        double var = Double.NaN;
        if (this.test(values, begin, length)) {
            this.clear();
            if (length == 1) {
                var = 0.0;
            }
            else if (length > 1) {
                final Mean mean = new Mean();
                final double m = mean.evaluate(values, begin, length);
                var = this.evaluate(values, m, begin, length);
            }
        }
        return var;
    }
    
    public double evaluate(final double[] values, final double[] weights, final int begin, final int length) throws MathIllegalArgumentException {
        double var = Double.NaN;
        if (this.test(values, weights, begin, length)) {
            this.clear();
            if (length == 1) {
                var = 0.0;
            }
            else if (length > 1) {
                final Mean mean = new Mean();
                final double m = mean.evaluate(values, weights, begin, length);
                var = this.evaluate(values, weights, m, begin, length);
            }
        }
        return var;
    }
    
    public double evaluate(final double[] values, final double[] weights) throws MathIllegalArgumentException {
        return this.evaluate(values, weights, 0, values.length);
    }
    
    public double evaluate(final double[] values, final double mean, final int begin, final int length) throws MathIllegalArgumentException {
        double var = Double.NaN;
        if (this.test(values, begin, length)) {
            if (length == 1) {
                var = 0.0;
            }
            else if (length > 1) {
                double accum = 0.0;
                double dev = 0.0;
                double accum2 = 0.0;
                for (int i = begin; i < begin + length; ++i) {
                    dev = values[i] - mean;
                    accum += dev * dev;
                    accum2 += dev;
                }
                final double len = length;
                if (this.isBiasCorrected) {
                    var = (accum - accum2 * accum2 / len) / (len - 1.0);
                }
                else {
                    var = (accum - accum2 * accum2 / len) / len;
                }
            }
        }
        return var;
    }
    
    public double evaluate(final double[] values, final double mean) throws MathIllegalArgumentException {
        return this.evaluate(values, mean, 0, values.length);
    }
    
    public double evaluate(final double[] values, final double[] weights, final double mean, final int begin, final int length) throws MathIllegalArgumentException {
        double var = Double.NaN;
        if (this.test(values, weights, begin, length)) {
            if (length == 1) {
                var = 0.0;
            }
            else if (length > 1) {
                double accum = 0.0;
                double dev = 0.0;
                double accum2 = 0.0;
                for (int i = begin; i < begin + length; ++i) {
                    dev = values[i] - mean;
                    accum += weights[i] * (dev * dev);
                    accum2 += weights[i] * dev;
                }
                double sumWts = 0.0;
                for (int j = begin; j < begin + length; ++j) {
                    sumWts += weights[j];
                }
                if (this.isBiasCorrected) {
                    var = (accum - accum2 * accum2 / sumWts) / (sumWts - 1.0);
                }
                else {
                    var = (accum - accum2 * accum2 / sumWts) / sumWts;
                }
            }
        }
        return var;
    }
    
    public double evaluate(final double[] values, final double[] weights, final double mean) throws MathIllegalArgumentException {
        return this.evaluate(values, weights, mean, 0, values.length);
    }
    
    public boolean isBiasCorrected() {
        return this.isBiasCorrected;
    }
    
    public void setBiasCorrected(final boolean biasCorrected) {
        this.isBiasCorrected = biasCorrected;
    }
    
    @Override
    public Variance copy() {
        final Variance result = new Variance();
        copy(this, result);
        return result;
    }
    
    public static void copy(final Variance source, final Variance dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.moment = source.moment.copy();
        dest.isBiasCorrected = source.isBiasCorrected;
        dest.incMoment = source.incMoment;
    }
}
