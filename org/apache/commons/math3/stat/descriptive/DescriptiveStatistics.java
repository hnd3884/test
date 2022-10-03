package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.MathArrays;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.util.ResizableDoubleArray;
import java.io.Serializable;

public class DescriptiveStatistics implements StatisticalSummary, Serializable
{
    public static final int INFINITE_WINDOW = -1;
    private static final long serialVersionUID = 4133067267405273064L;
    private static final String SET_QUANTILE_METHOD_NAME = "setQuantile";
    protected int windowSize;
    private ResizableDoubleArray eDA;
    private UnivariateStatistic meanImpl;
    private UnivariateStatistic geometricMeanImpl;
    private UnivariateStatistic kurtosisImpl;
    private UnivariateStatistic maxImpl;
    private UnivariateStatistic minImpl;
    private UnivariateStatistic percentileImpl;
    private UnivariateStatistic skewnessImpl;
    private UnivariateStatistic varianceImpl;
    private UnivariateStatistic sumsqImpl;
    private UnivariateStatistic sumImpl;
    
    public DescriptiveStatistics() {
        this.windowSize = -1;
        this.eDA = new ResizableDoubleArray();
        this.meanImpl = new Mean();
        this.geometricMeanImpl = new GeometricMean();
        this.kurtosisImpl = new Kurtosis();
        this.maxImpl = new Max();
        this.minImpl = new Min();
        this.percentileImpl = new Percentile();
        this.skewnessImpl = new Skewness();
        this.varianceImpl = new Variance();
        this.sumsqImpl = new SumOfSquares();
        this.sumImpl = new Sum();
    }
    
    public DescriptiveStatistics(final int window) throws MathIllegalArgumentException {
        this.windowSize = -1;
        this.eDA = new ResizableDoubleArray();
        this.meanImpl = new Mean();
        this.geometricMeanImpl = new GeometricMean();
        this.kurtosisImpl = new Kurtosis();
        this.maxImpl = new Max();
        this.minImpl = new Min();
        this.percentileImpl = new Percentile();
        this.skewnessImpl = new Skewness();
        this.varianceImpl = new Variance();
        this.sumsqImpl = new SumOfSquares();
        this.sumImpl = new Sum();
        this.setWindowSize(window);
    }
    
    public DescriptiveStatistics(final double[] initialDoubleArray) {
        this.windowSize = -1;
        this.eDA = new ResizableDoubleArray();
        this.meanImpl = new Mean();
        this.geometricMeanImpl = new GeometricMean();
        this.kurtosisImpl = new Kurtosis();
        this.maxImpl = new Max();
        this.minImpl = new Min();
        this.percentileImpl = new Percentile();
        this.skewnessImpl = new Skewness();
        this.varianceImpl = new Variance();
        this.sumsqImpl = new SumOfSquares();
        this.sumImpl = new Sum();
        if (initialDoubleArray != null) {
            this.eDA = new ResizableDoubleArray(initialDoubleArray);
        }
    }
    
    public DescriptiveStatistics(final DescriptiveStatistics original) throws NullArgumentException {
        this.windowSize = -1;
        this.eDA = new ResizableDoubleArray();
        this.meanImpl = new Mean();
        this.geometricMeanImpl = new GeometricMean();
        this.kurtosisImpl = new Kurtosis();
        this.maxImpl = new Max();
        this.minImpl = new Min();
        this.percentileImpl = new Percentile();
        this.skewnessImpl = new Skewness();
        this.varianceImpl = new Variance();
        this.sumsqImpl = new SumOfSquares();
        this.sumImpl = new Sum();
        copy(original, this);
    }
    
    public void addValue(final double v) {
        if (this.windowSize != -1) {
            if (this.getN() == this.windowSize) {
                this.eDA.addElementRolling(v);
            }
            else if (this.getN() < this.windowSize) {
                this.eDA.addElement(v);
            }
        }
        else {
            this.eDA.addElement(v);
        }
    }
    
    public void removeMostRecentValue() throws MathIllegalStateException {
        try {
            this.eDA.discardMostRecentElements(1);
        }
        catch (final MathIllegalArgumentException ex) {
            throw new MathIllegalStateException(LocalizedFormats.NO_DATA, new Object[0]);
        }
    }
    
    public double replaceMostRecentValue(final double v) throws MathIllegalStateException {
        return this.eDA.substituteMostRecentElement(v);
    }
    
    public double getMean() {
        return this.apply(this.meanImpl);
    }
    
    public double getGeometricMean() {
        return this.apply(this.geometricMeanImpl);
    }
    
    public double getVariance() {
        return this.apply(this.varianceImpl);
    }
    
    public double getPopulationVariance() {
        return this.apply(new Variance(false));
    }
    
    public double getStandardDeviation() {
        double stdDev = Double.NaN;
        if (this.getN() > 0L) {
            if (this.getN() > 1L) {
                stdDev = FastMath.sqrt(this.getVariance());
            }
            else {
                stdDev = 0.0;
            }
        }
        return stdDev;
    }
    
    public double getQuadraticMean() {
        final long n = this.getN();
        return (n > 0L) ? FastMath.sqrt(this.getSumsq() / n) : Double.NaN;
    }
    
    public double getSkewness() {
        return this.apply(this.skewnessImpl);
    }
    
    public double getKurtosis() {
        return this.apply(this.kurtosisImpl);
    }
    
    public double getMax() {
        return this.apply(this.maxImpl);
    }
    
    public double getMin() {
        return this.apply(this.minImpl);
    }
    
    public long getN() {
        return this.eDA.getNumElements();
    }
    
    public double getSum() {
        return this.apply(this.sumImpl);
    }
    
    public double getSumsq() {
        return this.apply(this.sumsqImpl);
    }
    
    public void clear() {
        this.eDA.clear();
    }
    
    public int getWindowSize() {
        return this.windowSize;
    }
    
    public void setWindowSize(final int windowSize) throws MathIllegalArgumentException {
        if (windowSize < 1 && windowSize != -1) {
            throw new MathIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_WINDOW_SIZE, new Object[] { windowSize });
        }
        if ((this.windowSize = windowSize) != -1 && windowSize < this.eDA.getNumElements()) {
            this.eDA.discardFrontElements(this.eDA.getNumElements() - windowSize);
        }
    }
    
    public double[] getValues() {
        return this.eDA.getElements();
    }
    
    public double[] getSortedValues() {
        final double[] sort = this.getValues();
        Arrays.sort(sort);
        return sort;
    }
    
    public double getElement(final int index) {
        return this.eDA.getElement(index);
    }
    
    public double getPercentile(final double p) throws MathIllegalStateException, MathIllegalArgumentException {
        if (this.percentileImpl instanceof Percentile) {
            ((Percentile)this.percentileImpl).setQuantile(p);
        }
        else {
            try {
                this.percentileImpl.getClass().getMethod("setQuantile", Double.TYPE).invoke(this.percentileImpl, p);
            }
            catch (final NoSuchMethodException e1) {
                throw new MathIllegalStateException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD, new Object[] { this.percentileImpl.getClass().getName(), "setQuantile" });
            }
            catch (final IllegalAccessException e2) {
                throw new MathIllegalStateException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD, new Object[] { "setQuantile", this.percentileImpl.getClass().getName() });
            }
            catch (final InvocationTargetException e3) {
                throw new IllegalStateException(e3.getCause());
            }
        }
        return this.apply(this.percentileImpl);
    }
    
    @Override
    public String toString() {
        final StringBuilder outBuffer = new StringBuilder();
        final String endl = "\n";
        outBuffer.append("DescriptiveStatistics:").append(endl);
        outBuffer.append("n: ").append(this.getN()).append(endl);
        outBuffer.append("min: ").append(this.getMin()).append(endl);
        outBuffer.append("max: ").append(this.getMax()).append(endl);
        outBuffer.append("mean: ").append(this.getMean()).append(endl);
        outBuffer.append("std dev: ").append(this.getStandardDeviation()).append(endl);
        try {
            outBuffer.append("median: ").append(this.getPercentile(50.0)).append(endl);
        }
        catch (final MathIllegalStateException ex) {
            outBuffer.append("median: unavailable").append(endl);
        }
        outBuffer.append("skewness: ").append(this.getSkewness()).append(endl);
        outBuffer.append("kurtosis: ").append(this.getKurtosis()).append(endl);
        return outBuffer.toString();
    }
    
    public double apply(final UnivariateStatistic stat) {
        return this.eDA.compute(stat);
    }
    
    public synchronized UnivariateStatistic getMeanImpl() {
        return this.meanImpl;
    }
    
    public synchronized void setMeanImpl(final UnivariateStatistic meanImpl) {
        this.meanImpl = meanImpl;
    }
    
    public synchronized UnivariateStatistic getGeometricMeanImpl() {
        return this.geometricMeanImpl;
    }
    
    public synchronized void setGeometricMeanImpl(final UnivariateStatistic geometricMeanImpl) {
        this.geometricMeanImpl = geometricMeanImpl;
    }
    
    public synchronized UnivariateStatistic getKurtosisImpl() {
        return this.kurtosisImpl;
    }
    
    public synchronized void setKurtosisImpl(final UnivariateStatistic kurtosisImpl) {
        this.kurtosisImpl = kurtosisImpl;
    }
    
    public synchronized UnivariateStatistic getMaxImpl() {
        return this.maxImpl;
    }
    
    public synchronized void setMaxImpl(final UnivariateStatistic maxImpl) {
        this.maxImpl = maxImpl;
    }
    
    public synchronized UnivariateStatistic getMinImpl() {
        return this.minImpl;
    }
    
    public synchronized void setMinImpl(final UnivariateStatistic minImpl) {
        this.minImpl = minImpl;
    }
    
    public synchronized UnivariateStatistic getPercentileImpl() {
        return this.percentileImpl;
    }
    
    public synchronized void setPercentileImpl(final UnivariateStatistic percentileImpl) throws MathIllegalArgumentException {
        try {
            percentileImpl.getClass().getMethod("setQuantile", Double.TYPE).invoke(percentileImpl, 50.0);
        }
        catch (final NoSuchMethodException e1) {
            throw new MathIllegalArgumentException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_UNSUPPORTED_METHOD, new Object[] { percentileImpl.getClass().getName(), "setQuantile" });
        }
        catch (final IllegalAccessException e2) {
            throw new MathIllegalArgumentException(LocalizedFormats.PERCENTILE_IMPLEMENTATION_CANNOT_ACCESS_METHOD, new Object[] { "setQuantile", percentileImpl.getClass().getName() });
        }
        catch (final InvocationTargetException e3) {
            throw new IllegalArgumentException(e3.getCause());
        }
        this.percentileImpl = percentileImpl;
    }
    
    public synchronized UnivariateStatistic getSkewnessImpl() {
        return this.skewnessImpl;
    }
    
    public synchronized void setSkewnessImpl(final UnivariateStatistic skewnessImpl) {
        this.skewnessImpl = skewnessImpl;
    }
    
    public synchronized UnivariateStatistic getVarianceImpl() {
        return this.varianceImpl;
    }
    
    public synchronized void setVarianceImpl(final UnivariateStatistic varianceImpl) {
        this.varianceImpl = varianceImpl;
    }
    
    public synchronized UnivariateStatistic getSumsqImpl() {
        return this.sumsqImpl;
    }
    
    public synchronized void setSumsqImpl(final UnivariateStatistic sumsqImpl) {
        this.sumsqImpl = sumsqImpl;
    }
    
    public synchronized UnivariateStatistic getSumImpl() {
        return this.sumImpl;
    }
    
    public synchronized void setSumImpl(final UnivariateStatistic sumImpl) {
        this.sumImpl = sumImpl;
    }
    
    public DescriptiveStatistics copy() {
        final DescriptiveStatistics result = new DescriptiveStatistics();
        copy(this, result);
        return result;
    }
    
    public static void copy(final DescriptiveStatistics source, final DescriptiveStatistics dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        dest.eDA = source.eDA.copy();
        dest.windowSize = source.windowSize;
        dest.maxImpl = source.maxImpl.copy();
        dest.meanImpl = source.meanImpl.copy();
        dest.minImpl = source.minImpl.copy();
        dest.sumImpl = source.sumImpl.copy();
        dest.varianceImpl = source.varianceImpl.copy();
        dest.sumsqImpl = source.sumsqImpl.copy();
        dest.geometricMeanImpl = source.geometricMeanImpl.copy();
        dest.kurtosisImpl = source.kurtosisImpl;
        dest.skewnessImpl = source.skewnessImpl;
        dest.percentileImpl = source.percentileImpl;
    }
}
