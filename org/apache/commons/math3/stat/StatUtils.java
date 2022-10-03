package org.apache.commons.math3.stat;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.summary.SumOfLogs;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;

public final class StatUtils
{
    private static final UnivariateStatistic SUM;
    private static final UnivariateStatistic SUM_OF_SQUARES;
    private static final UnivariateStatistic PRODUCT;
    private static final UnivariateStatistic SUM_OF_LOGS;
    private static final UnivariateStatistic MIN;
    private static final UnivariateStatistic MAX;
    private static final UnivariateStatistic MEAN;
    private static final Variance VARIANCE;
    private static final Percentile PERCENTILE;
    private static final GeometricMean GEOMETRIC_MEAN;
    
    private StatUtils() {
    }
    
    public static double sum(final double[] values) throws MathIllegalArgumentException {
        return StatUtils.SUM.evaluate(values);
    }
    
    public static double sum(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return StatUtils.SUM.evaluate(values, begin, length);
    }
    
    public static double sumSq(final double[] values) throws MathIllegalArgumentException {
        return StatUtils.SUM_OF_SQUARES.evaluate(values);
    }
    
    public static double sumSq(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return StatUtils.SUM_OF_SQUARES.evaluate(values, begin, length);
    }
    
    public static double product(final double[] values) throws MathIllegalArgumentException {
        return StatUtils.PRODUCT.evaluate(values);
    }
    
    public static double product(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return StatUtils.PRODUCT.evaluate(values, begin, length);
    }
    
    public static double sumLog(final double[] values) throws MathIllegalArgumentException {
        return StatUtils.SUM_OF_LOGS.evaluate(values);
    }
    
    public static double sumLog(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return StatUtils.SUM_OF_LOGS.evaluate(values, begin, length);
    }
    
    public static double mean(final double[] values) throws MathIllegalArgumentException {
        return StatUtils.MEAN.evaluate(values);
    }
    
    public static double mean(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return StatUtils.MEAN.evaluate(values, begin, length);
    }
    
    public static double geometricMean(final double[] values) throws MathIllegalArgumentException {
        return StatUtils.GEOMETRIC_MEAN.evaluate(values);
    }
    
    public static double geometricMean(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return StatUtils.GEOMETRIC_MEAN.evaluate(values, begin, length);
    }
    
    public static double variance(final double[] values) throws MathIllegalArgumentException {
        return StatUtils.VARIANCE.evaluate(values);
    }
    
    public static double variance(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return StatUtils.VARIANCE.evaluate(values, begin, length);
    }
    
    public static double variance(final double[] values, final double mean, final int begin, final int length) throws MathIllegalArgumentException {
        return StatUtils.VARIANCE.evaluate(values, mean, begin, length);
    }
    
    public static double variance(final double[] values, final double mean) throws MathIllegalArgumentException {
        return StatUtils.VARIANCE.evaluate(values, mean);
    }
    
    public static double populationVariance(final double[] values) throws MathIllegalArgumentException {
        return new Variance(false).evaluate(values);
    }
    
    public static double populationVariance(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return new Variance(false).evaluate(values, begin, length);
    }
    
    public static double populationVariance(final double[] values, final double mean, final int begin, final int length) throws MathIllegalArgumentException {
        return new Variance(false).evaluate(values, mean, begin, length);
    }
    
    public static double populationVariance(final double[] values, final double mean) throws MathIllegalArgumentException {
        return new Variance(false).evaluate(values, mean);
    }
    
    public static double max(final double[] values) throws MathIllegalArgumentException {
        return StatUtils.MAX.evaluate(values);
    }
    
    public static double max(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return StatUtils.MAX.evaluate(values, begin, length);
    }
    
    public static double min(final double[] values) throws MathIllegalArgumentException {
        return StatUtils.MIN.evaluate(values);
    }
    
    public static double min(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        return StatUtils.MIN.evaluate(values, begin, length);
    }
    
    public static double percentile(final double[] values, final double p) throws MathIllegalArgumentException {
        return StatUtils.PERCENTILE.evaluate(values, p);
    }
    
    public static double percentile(final double[] values, final int begin, final int length, final double p) throws MathIllegalArgumentException {
        return StatUtils.PERCENTILE.evaluate(values, begin, length, p);
    }
    
    public static double sumDifference(final double[] sample1, final double[] sample2) throws DimensionMismatchException, NoDataException {
        final int n = sample1.length;
        if (n != sample2.length) {
            throw new DimensionMismatchException(n, sample2.length);
        }
        if (n <= 0) {
            throw new NoDataException(LocalizedFormats.INSUFFICIENT_DIMENSION);
        }
        double result = 0.0;
        for (int i = 0; i < n; ++i) {
            result += sample1[i] - sample2[i];
        }
        return result;
    }
    
    public static double meanDifference(final double[] sample1, final double[] sample2) throws DimensionMismatchException, NoDataException {
        return sumDifference(sample1, sample2) / sample1.length;
    }
    
    public static double varianceDifference(final double[] sample1, final double[] sample2, final double meanDifference) throws DimensionMismatchException, NumberIsTooSmallException {
        double sum1 = 0.0;
        double sum2 = 0.0;
        double diff = 0.0;
        final int n = sample1.length;
        if (n != sample2.length) {
            throw new DimensionMismatchException(n, sample2.length);
        }
        if (n < 2) {
            throw new NumberIsTooSmallException(n, 2, true);
        }
        for (int i = 0; i < n; ++i) {
            diff = sample1[i] - sample2[i];
            sum1 += (diff - meanDifference) * (diff - meanDifference);
            sum2 += diff - meanDifference;
        }
        return (sum1 - sum2 * sum2 / n) / (n - 1);
    }
    
    public static double[] normalize(final double[] sample) {
        final DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < sample.length; ++i) {
            stats.addValue(sample[i]);
        }
        final double mean = stats.getMean();
        final double standardDeviation = stats.getStandardDeviation();
        final double[] standardizedSample = new double[sample.length];
        for (int j = 0; j < sample.length; ++j) {
            standardizedSample[j] = (sample[j] - mean) / standardDeviation;
        }
        return standardizedSample;
    }
    
    public static double[] mode(final double[] sample) throws MathIllegalArgumentException {
        if (sample == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        return getMode(sample, 0, sample.length);
    }
    
    public static double[] mode(final double[] sample, final int begin, final int length) {
        if (sample == null) {
            throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
        }
        if (begin < 0) {
            throw new NotPositiveException(LocalizedFormats.START_POSITION, begin);
        }
        if (length < 0) {
            throw new NotPositiveException(LocalizedFormats.LENGTH, length);
        }
        return getMode(sample, begin, length);
    }
    
    private static double[] getMode(final double[] values, final int begin, final int length) {
        final Frequency freq = new Frequency();
        for (int i = begin; i < begin + length; ++i) {
            final double value = values[i];
            if (!Double.isNaN(value)) {
                freq.addValue(value);
            }
        }
        final List<Comparable<?>> list = freq.getMode();
        final double[] modes = new double[list.size()];
        int j = 0;
        for (final Comparable<?> c : list) {
            modes[j++] = (double)c;
        }
        return modes;
    }
    
    static {
        SUM = new Sum();
        SUM_OF_SQUARES = new SumOfSquares();
        PRODUCT = new Product();
        SUM_OF_LOGS = new SumOfLogs();
        MIN = new Min();
        MAX = new Max();
        MEAN = new Mean();
        VARIANCE = new Variance();
        PERCENTILE = new Percentile();
        GEOMETRIC_MEAN = new GeometricMean();
    }
}
