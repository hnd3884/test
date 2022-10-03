package org.apache.commons.math3.stat.descriptive.moment;

import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.summary.SumOfLogs;
import org.apache.commons.math3.stat.descriptive.StorelessUnivariateStatistic;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

public class GeometricMean extends AbstractStorelessUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = -8178734905303459453L;
    private StorelessUnivariateStatistic sumOfLogs;
    
    public GeometricMean() {
        this.sumOfLogs = new SumOfLogs();
    }
    
    public GeometricMean(final GeometricMean original) throws NullArgumentException {
        copy(original, this);
    }
    
    public GeometricMean(final SumOfLogs sumOfLogs) {
        this.sumOfLogs = sumOfLogs;
    }
    
    @Override
    public GeometricMean copy() {
        final GeometricMean result = new GeometricMean();
        copy(this, result);
        return result;
    }
    
    @Override
    public void increment(final double d) {
        this.sumOfLogs.increment(d);
    }
    
    @Override
    public double getResult() {
        if (this.sumOfLogs.getN() > 0L) {
            return FastMath.exp(this.sumOfLogs.getResult() / this.sumOfLogs.getN());
        }
        return Double.NaN;
    }
    
    @Override
    public void clear() {
        this.sumOfLogs.clear();
    }
    
    @Override
    public double evaluate(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return FastMath.exp(this.sumOfLogs.evaluate(values, begin, length) / length);
    }
    
    public long getN() {
        return this.sumOfLogs.getN();
    }
    
    public void setSumLogImpl(final StorelessUnivariateStatistic sumLogImpl) throws MathIllegalStateException {
        this.checkEmpty();
        this.sumOfLogs = sumLogImpl;
    }
    
    public StorelessUnivariateStatistic getSumLogImpl() {
        return this.sumOfLogs;
    }
    
    public static void copy(final GeometricMean source, final GeometricMean dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.setData(source.getDataRef());
        dest.sumOfLogs = source.sumOfLogs.copy();
    }
    
    private void checkEmpty() throws MathIllegalStateException {
        if (this.getN() > 0L) {
            throw new MathIllegalStateException(LocalizedFormats.VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC, new Object[] { this.getN() });
        }
    }
}
