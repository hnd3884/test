package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.StatUtils;

public class TTest
{
    public double pairedT(final double[] sample1, final double[] sample2) throws NullArgumentException, NoDataException, DimensionMismatchException, NumberIsTooSmallException {
        this.checkSampleData(sample1);
        this.checkSampleData(sample2);
        final double meanDifference = StatUtils.meanDifference(sample1, sample2);
        return this.t(meanDifference, 0.0, StatUtils.varianceDifference(sample1, sample2, meanDifference), sample1.length);
    }
    
    public double pairedTTest(final double[] sample1, final double[] sample2) throws NullArgumentException, NoDataException, DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException {
        final double meanDifference = StatUtils.meanDifference(sample1, sample2);
        return this.tTest(meanDifference, 0.0, StatUtils.varianceDifference(sample1, sample2, meanDifference), sample1.length);
    }
    
    public boolean pairedTTest(final double[] sample1, final double[] sample2, final double alpha) throws NullArgumentException, NoDataException, DimensionMismatchException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        this.checkSignificanceLevel(alpha);
        return this.pairedTTest(sample1, sample2) < alpha;
    }
    
    public double t(final double mu, final double[] observed) throws NullArgumentException, NumberIsTooSmallException {
        this.checkSampleData(observed);
        return this.t(StatUtils.mean(observed), mu, StatUtils.variance(observed), observed.length);
    }
    
    public double t(final double mu, final StatisticalSummary sampleStats) throws NullArgumentException, NumberIsTooSmallException {
        this.checkSampleData(sampleStats);
        return this.t(sampleStats.getMean(), mu, sampleStats.getVariance(), (double)sampleStats.getN());
    }
    
    public double homoscedasticT(final double[] sample1, final double[] sample2) throws NullArgumentException, NumberIsTooSmallException {
        this.checkSampleData(sample1);
        this.checkSampleData(sample2);
        return this.homoscedasticT(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
    }
    
    public double t(final double[] sample1, final double[] sample2) throws NullArgumentException, NumberIsTooSmallException {
        this.checkSampleData(sample1);
        this.checkSampleData(sample2);
        return this.t(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
    }
    
    public double t(final StatisticalSummary sampleStats1, final StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException {
        this.checkSampleData(sampleStats1);
        this.checkSampleData(sampleStats2);
        return this.t(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), (double)sampleStats1.getN(), (double)sampleStats2.getN());
    }
    
    public double homoscedasticT(final StatisticalSummary sampleStats1, final StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException {
        this.checkSampleData(sampleStats1);
        this.checkSampleData(sampleStats2);
        return this.homoscedasticT(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), (double)sampleStats1.getN(), (double)sampleStats2.getN());
    }
    
    public double tTest(final double mu, final double[] sample) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        this.checkSampleData(sample);
        return this.tTest(StatUtils.mean(sample), mu, StatUtils.variance(sample), sample.length);
    }
    
    public boolean tTest(final double mu, final double[] sample, final double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        this.checkSignificanceLevel(alpha);
        return this.tTest(mu, sample) < alpha;
    }
    
    public double tTest(final double mu, final StatisticalSummary sampleStats) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        this.checkSampleData(sampleStats);
        return this.tTest(sampleStats.getMean(), mu, sampleStats.getVariance(), (double)sampleStats.getN());
    }
    
    public boolean tTest(final double mu, final StatisticalSummary sampleStats, final double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        this.checkSignificanceLevel(alpha);
        return this.tTest(mu, sampleStats) < alpha;
    }
    
    public double tTest(final double[] sample1, final double[] sample2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        this.checkSampleData(sample1);
        this.checkSampleData(sample2);
        return this.tTest(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
    }
    
    public double homoscedasticTTest(final double[] sample1, final double[] sample2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        this.checkSampleData(sample1);
        this.checkSampleData(sample2);
        return this.homoscedasticTTest(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1), StatUtils.variance(sample2), sample1.length, sample2.length);
    }
    
    public boolean tTest(final double[] sample1, final double[] sample2, final double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        this.checkSignificanceLevel(alpha);
        return this.tTest(sample1, sample2) < alpha;
    }
    
    public boolean homoscedasticTTest(final double[] sample1, final double[] sample2, final double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        this.checkSignificanceLevel(alpha);
        return this.homoscedasticTTest(sample1, sample2) < alpha;
    }
    
    public double tTest(final StatisticalSummary sampleStats1, final StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        this.checkSampleData(sampleStats1);
        this.checkSampleData(sampleStats2);
        return this.tTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), (double)sampleStats1.getN(), (double)sampleStats2.getN());
    }
    
    public double homoscedasticTTest(final StatisticalSummary sampleStats1, final StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        this.checkSampleData(sampleStats1);
        this.checkSampleData(sampleStats2);
        return this.homoscedasticTTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(), sampleStats2.getVariance(), (double)sampleStats1.getN(), (double)sampleStats2.getN());
    }
    
    public boolean tTest(final StatisticalSummary sampleStats1, final StatisticalSummary sampleStats2, final double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        this.checkSignificanceLevel(alpha);
        return this.tTest(sampleStats1, sampleStats2) < alpha;
    }
    
    protected double df(final double v1, final double v2, final double n1, final double n2) {
        return (v1 / n1 + v2 / n2) * (v1 / n1 + v2 / n2) / (v1 * v1 / (n1 * n1 * (n1 - 1.0)) + v2 * v2 / (n2 * n2 * (n2 - 1.0)));
    }
    
    protected double t(final double m, final double mu, final double v, final double n) {
        return (m - mu) / FastMath.sqrt(v / n);
    }
    
    protected double t(final double m1, final double m2, final double v1, final double v2, final double n1, final double n2) {
        return (m1 - m2) / FastMath.sqrt(v1 / n1 + v2 / n2);
    }
    
    protected double homoscedasticT(final double m1, final double m2, final double v1, final double v2, final double n1, final double n2) {
        final double pooledVariance = ((n1 - 1.0) * v1 + (n2 - 1.0) * v2) / (n1 + n2 - 2.0);
        return (m1 - m2) / FastMath.sqrt(pooledVariance * (1.0 / n1 + 1.0 / n2));
    }
    
    protected double tTest(final double m, final double mu, final double v, final double n) throws MaxCountExceededException, MathIllegalArgumentException {
        final double t = FastMath.abs(this.t(m, mu, v, n));
        final TDistribution distribution = new TDistribution(null, n - 1.0);
        return 2.0 * distribution.cumulativeProbability(-t);
    }
    
    protected double tTest(final double m1, final double m2, final double v1, final double v2, final double n1, final double n2) throws MaxCountExceededException, NotStrictlyPositiveException {
        final double t = FastMath.abs(this.t(m1, m2, v1, v2, n1, n2));
        final double degreesOfFreedom = this.df(v1, v2, n1, n2);
        final TDistribution distribution = new TDistribution(null, degreesOfFreedom);
        return 2.0 * distribution.cumulativeProbability(-t);
    }
    
    protected double homoscedasticTTest(final double m1, final double m2, final double v1, final double v2, final double n1, final double n2) throws MaxCountExceededException, NotStrictlyPositiveException {
        final double t = FastMath.abs(this.homoscedasticT(m1, m2, v1, v2, n1, n2));
        final double degreesOfFreedom = n1 + n2 - 2.0;
        final TDistribution distribution = new TDistribution(null, degreesOfFreedom);
        return 2.0 * distribution.cumulativeProbability(-t);
    }
    
    private void checkSignificanceLevel(final double alpha) throws OutOfRangeException {
        if (alpha <= 0.0 || alpha > 0.5) {
            throw new OutOfRangeException(LocalizedFormats.SIGNIFICANCE_LEVEL, alpha, 0.0, 0.5);
        }
    }
    
    private void checkSampleData(final double[] data) throws NullArgumentException, NumberIsTooSmallException {
        if (data == null) {
            throw new NullArgumentException();
        }
        if (data.length < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.INSUFFICIENT_DATA_FOR_T_STATISTIC, data.length, 2, true);
        }
    }
    
    private void checkSampleData(final StatisticalSummary stat) throws NullArgumentException, NumberIsTooSmallException {
        if (stat == null) {
            throw new NullArgumentException();
        }
        if (stat.getN() < 2L) {
            throw new NumberIsTooSmallException(LocalizedFormats.INSUFFICIENT_DATA_FOR_T_STATISTIC, stat.getN(), 2, true);
        }
    }
}
