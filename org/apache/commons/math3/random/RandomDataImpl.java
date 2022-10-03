package org.apache.commons.math3.random;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.distribution.RealDistribution;
import java.util.Collection;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import java.io.Serializable;

@Deprecated
public class RandomDataImpl implements RandomData, Serializable
{
    private static final long serialVersionUID = -626730818244969716L;
    private final RandomDataGenerator delegate;
    
    public RandomDataImpl() {
        this.delegate = new RandomDataGenerator();
    }
    
    public RandomDataImpl(final RandomGenerator rand) {
        this.delegate = new RandomDataGenerator(rand);
    }
    
    @Deprecated
    RandomDataGenerator getDelegate() {
        return this.delegate;
    }
    
    public String nextHexString(final int len) throws NotStrictlyPositiveException {
        return this.delegate.nextHexString(len);
    }
    
    public int nextInt(final int lower, final int upper) throws NumberIsTooLargeException {
        return this.delegate.nextInt(lower, upper);
    }
    
    public long nextLong(final long lower, final long upper) throws NumberIsTooLargeException {
        return this.delegate.nextLong(lower, upper);
    }
    
    public String nextSecureHexString(final int len) throws NotStrictlyPositiveException {
        return this.delegate.nextSecureHexString(len);
    }
    
    public int nextSecureInt(final int lower, final int upper) throws NumberIsTooLargeException {
        return this.delegate.nextSecureInt(lower, upper);
    }
    
    public long nextSecureLong(final long lower, final long upper) throws NumberIsTooLargeException {
        return this.delegate.nextSecureLong(lower, upper);
    }
    
    public long nextPoisson(final double mean) throws NotStrictlyPositiveException {
        return this.delegate.nextPoisson(mean);
    }
    
    public double nextGaussian(final double mu, final double sigma) throws NotStrictlyPositiveException {
        return this.delegate.nextGaussian(mu, sigma);
    }
    
    public double nextExponential(final double mean) throws NotStrictlyPositiveException {
        return this.delegate.nextExponential(mean);
    }
    
    public double nextUniform(final double lower, final double upper) throws NumberIsTooLargeException, NotFiniteNumberException, NotANumberException {
        return this.delegate.nextUniform(lower, upper);
    }
    
    public double nextUniform(final double lower, final double upper, final boolean lowerInclusive) throws NumberIsTooLargeException, NotFiniteNumberException, NotANumberException {
        return this.delegate.nextUniform(lower, upper, lowerInclusive);
    }
    
    public double nextBeta(final double alpha, final double beta) {
        return this.delegate.nextBeta(alpha, beta);
    }
    
    public int nextBinomial(final int numberOfTrials, final double probabilityOfSuccess) {
        return this.delegate.nextBinomial(numberOfTrials, probabilityOfSuccess);
    }
    
    public double nextCauchy(final double median, final double scale) {
        return this.delegate.nextCauchy(median, scale);
    }
    
    public double nextChiSquare(final double df) {
        return this.delegate.nextChiSquare(df);
    }
    
    public double nextF(final double numeratorDf, final double denominatorDf) throws NotStrictlyPositiveException {
        return this.delegate.nextF(numeratorDf, denominatorDf);
    }
    
    public double nextGamma(final double shape, final double scale) throws NotStrictlyPositiveException {
        return this.delegate.nextGamma(shape, scale);
    }
    
    public int nextHypergeometric(final int populationSize, final int numberOfSuccesses, final int sampleSize) throws NotPositiveException, NotStrictlyPositiveException, NumberIsTooLargeException {
        return this.delegate.nextHypergeometric(populationSize, numberOfSuccesses, sampleSize);
    }
    
    public int nextPascal(final int r, final double p) throws NotStrictlyPositiveException, OutOfRangeException {
        return this.delegate.nextPascal(r, p);
    }
    
    public double nextT(final double df) throws NotStrictlyPositiveException {
        return this.delegate.nextT(df);
    }
    
    public double nextWeibull(final double shape, final double scale) throws NotStrictlyPositiveException {
        return this.delegate.nextWeibull(shape, scale);
    }
    
    public int nextZipf(final int numberOfElements, final double exponent) throws NotStrictlyPositiveException {
        return this.delegate.nextZipf(numberOfElements, exponent);
    }
    
    public void reSeed(final long seed) {
        this.delegate.reSeed(seed);
    }
    
    public void reSeedSecure() {
        this.delegate.reSeedSecure();
    }
    
    public void reSeedSecure(final long seed) {
        this.delegate.reSeedSecure(seed);
    }
    
    public void reSeed() {
        this.delegate.reSeed();
    }
    
    public void setSecureAlgorithm(final String algorithm, final String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        this.delegate.setSecureAlgorithm(algorithm, provider);
    }
    
    public int[] nextPermutation(final int n, final int k) throws NotStrictlyPositiveException, NumberIsTooLargeException {
        return this.delegate.nextPermutation(n, k);
    }
    
    public Object[] nextSample(final Collection<?> c, final int k) throws NotStrictlyPositiveException, NumberIsTooLargeException {
        return this.delegate.nextSample(c, k);
    }
    
    @Deprecated
    public double nextInversionDeviate(final RealDistribution distribution) throws MathIllegalArgumentException {
        return distribution.inverseCumulativeProbability(this.nextUniform(0.0, 1.0));
    }
    
    @Deprecated
    public int nextInversionDeviate(final IntegerDistribution distribution) throws MathIllegalArgumentException {
        return distribution.inverseCumulativeProbability(this.nextUniform(0.0, 1.0));
    }
}
