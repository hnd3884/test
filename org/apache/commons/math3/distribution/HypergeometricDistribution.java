package org.apache.commons.math3.distribution;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;

public class HypergeometricDistribution extends AbstractIntegerDistribution
{
    private static final long serialVersionUID = -436928820673516179L;
    private final int numberOfSuccesses;
    private final int populationSize;
    private final int sampleSize;
    private double numericalVariance;
    private boolean numericalVarianceIsCalculated;
    
    public HypergeometricDistribution(final int populationSize, final int numberOfSuccesses, final int sampleSize) throws NotPositiveException, NotStrictlyPositiveException, NumberIsTooLargeException {
        this(new Well19937c(), populationSize, numberOfSuccesses, sampleSize);
    }
    
    public HypergeometricDistribution(final RandomGenerator rng, final int populationSize, final int numberOfSuccesses, final int sampleSize) throws NotPositiveException, NotStrictlyPositiveException, NumberIsTooLargeException {
        super(rng);
        this.numericalVariance = Double.NaN;
        this.numericalVarianceIsCalculated = false;
        if (populationSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.POPULATION_SIZE, populationSize);
        }
        if (numberOfSuccesses < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_SUCCESSES, numberOfSuccesses);
        }
        if (sampleSize < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize);
        }
        if (numberOfSuccesses > populationSize) {
            throw new NumberIsTooLargeException(LocalizedFormats.NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE, numberOfSuccesses, populationSize, true);
        }
        if (sampleSize > populationSize) {
            throw new NumberIsTooLargeException(LocalizedFormats.SAMPLE_SIZE_LARGER_THAN_POPULATION_SIZE, sampleSize, populationSize, true);
        }
        this.numberOfSuccesses = numberOfSuccesses;
        this.populationSize = populationSize;
        this.sampleSize = sampleSize;
    }
    
    public double cumulativeProbability(final int x) {
        final int[] domain = this.getDomain(this.populationSize, this.numberOfSuccesses, this.sampleSize);
        double ret;
        if (x < domain[0]) {
            ret = 0.0;
        }
        else if (x >= domain[1]) {
            ret = 1.0;
        }
        else {
            ret = this.innerCumulativeProbability(domain[0], x, 1);
        }
        return ret;
    }
    
    private int[] getDomain(final int n, final int m, final int k) {
        return new int[] { this.getLowerDomain(n, m, k), this.getUpperDomain(m, k) };
    }
    
    private int getLowerDomain(final int n, final int m, final int k) {
        return FastMath.max(0, m - (n - k));
    }
    
    public int getNumberOfSuccesses() {
        return this.numberOfSuccesses;
    }
    
    public int getPopulationSize() {
        return this.populationSize;
    }
    
    public int getSampleSize() {
        return this.sampleSize;
    }
    
    private int getUpperDomain(final int m, final int k) {
        return FastMath.min(k, m);
    }
    
    public double probability(final int x) {
        final double logProbability = this.logProbability(x);
        return (logProbability == Double.NEGATIVE_INFINITY) ? 0.0 : FastMath.exp(logProbability);
    }
    
    @Override
    public double logProbability(final int x) {
        final int[] domain = this.getDomain(this.populationSize, this.numberOfSuccesses, this.sampleSize);
        double ret;
        if (x < domain[0] || x > domain[1]) {
            ret = Double.NEGATIVE_INFINITY;
        }
        else {
            final double p = this.sampleSize / (double)this.populationSize;
            final double q = (this.populationSize - this.sampleSize) / (double)this.populationSize;
            final double p2 = SaddlePointExpansion.logBinomialProbability(x, this.numberOfSuccesses, p, q);
            final double p3 = SaddlePointExpansion.logBinomialProbability(this.sampleSize - x, this.populationSize - this.numberOfSuccesses, p, q);
            final double p4 = SaddlePointExpansion.logBinomialProbability(this.sampleSize, this.populationSize, p, q);
            ret = p2 + p3 - p4;
        }
        return ret;
    }
    
    public double upperCumulativeProbability(final int x) {
        final int[] domain = this.getDomain(this.populationSize, this.numberOfSuccesses, this.sampleSize);
        double ret;
        if (x <= domain[0]) {
            ret = 1.0;
        }
        else if (x > domain[1]) {
            ret = 0.0;
        }
        else {
            ret = this.innerCumulativeProbability(domain[1], x, -1);
        }
        return ret;
    }
    
    private double innerCumulativeProbability(int x0, final int x1, final int dx) {
        double ret;
        for (ret = this.probability(x0); x0 != x1; x0 += dx, ret += this.probability(x0)) {}
        return ret;
    }
    
    public double getNumericalMean() {
        return this.getSampleSize() * (this.getNumberOfSuccesses() / (double)this.getPopulationSize());
    }
    
    public double getNumericalVariance() {
        if (!this.numericalVarianceIsCalculated) {
            this.numericalVariance = this.calculateNumericalVariance();
            this.numericalVarianceIsCalculated = true;
        }
        return this.numericalVariance;
    }
    
    protected double calculateNumericalVariance() {
        final double N = this.getPopulationSize();
        final double m = this.getNumberOfSuccesses();
        final double n = this.getSampleSize();
        return n * m * (N - n) * (N - m) / (N * N * (N - 1.0));
    }
    
    public int getSupportLowerBound() {
        return FastMath.max(0, this.getSampleSize() + this.getNumberOfSuccesses() - this.getPopulationSize());
    }
    
    public int getSupportUpperBound() {
        return FastMath.min(this.getNumberOfSuccesses(), this.getSampleSize());
    }
    
    public boolean isSupportConnected() {
        return true;
    }
}
