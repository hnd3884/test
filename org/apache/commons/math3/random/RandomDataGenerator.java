package org.apache.commons.math3.random;

import java.security.NoSuchProviderException;
import java.util.Random;
import java.security.SecureRandom;
import java.util.Collection;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.ZipfDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.distribution.PascalDistribution;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.distribution.HypergeometricDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.math3.exception.MathInternalError;
import java.security.MessageDigest;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.io.Serializable;

public class RandomDataGenerator implements RandomData, Serializable
{
    private static final long serialVersionUID = -626730818244969716L;
    private RandomGenerator rand;
    private RandomGenerator secRand;
    
    public RandomDataGenerator() {
        this.rand = null;
        this.secRand = null;
    }
    
    public RandomDataGenerator(final RandomGenerator rand) {
        this.rand = null;
        this.secRand = null;
        this.rand = rand;
    }
    
    public String nextHexString(final int len) throws NotStrictlyPositiveException {
        if (len <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.LENGTH, len);
        }
        final RandomGenerator ran = this.getRandomGenerator();
        final StringBuilder outBuffer = new StringBuilder();
        final byte[] randomBytes = new byte[len / 2 + 1];
        ran.nextBytes(randomBytes);
        for (int i = 0; i < randomBytes.length; ++i) {
            final Integer c = (Integer)randomBytes[i];
            String hex = Integer.toHexString(c + 128);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            outBuffer.append(hex);
        }
        return outBuffer.toString().substring(0, len);
    }
    
    public int nextInt(final int lower, final int upper) throws NumberIsTooLargeException {
        return new UniformIntegerDistribution(this.getRandomGenerator(), lower, upper).sample();
    }
    
    public long nextLong(final long lower, final long upper) throws NumberIsTooLargeException {
        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, lower, upper, false);
        }
        final long max = upper - lower + 1L;
        if (max <= 0L) {
            final RandomGenerator rng = this.getRandomGenerator();
            long r;
            do {
                r = rng.nextLong();
            } while (r < lower || r > upper);
            return r;
        }
        if (max < 2147483647L) {
            return lower + this.getRandomGenerator().nextInt((int)max);
        }
        return lower + nextLong(this.getRandomGenerator(), max);
    }
    
    private static long nextLong(final RandomGenerator rng, final long n) throws IllegalArgumentException {
        if (n > 0L) {
            final byte[] byteArray = new byte[8];
            long bits;
            long val;
            do {
                rng.nextBytes(byteArray);
                bits = 0L;
                for (final byte b : byteArray) {
                    bits = (bits << 8 | ((long)b & 0xFFL));
                }
                bits &= Long.MAX_VALUE;
                val = bits % n;
            } while (bits - val + (n - 1L) < 0L);
            return val;
        }
        throw new NotStrictlyPositiveException(n);
    }
    
    public String nextSecureHexString(final int len) throws NotStrictlyPositiveException {
        if (len <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.LENGTH, len);
        }
        final RandomGenerator secRan = this.getSecRan();
        MessageDigest alg = null;
        try {
            alg = MessageDigest.getInstance("SHA-1");
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new MathInternalError(ex);
        }
        alg.reset();
        final int numIter = len / 40 + 1;
        final StringBuilder outBuffer = new StringBuilder();
        for (int iter = 1; iter < numIter + 1; ++iter) {
            final byte[] randomBytes = new byte[40];
            secRan.nextBytes(randomBytes);
            alg.update(randomBytes);
            final byte[] hash = alg.digest();
            for (int i = 0; i < hash.length; ++i) {
                final Integer c = (Integer)hash[i];
                String hex = Integer.toHexString(c + 128);
                if (hex.length() == 1) {
                    hex = "0" + hex;
                }
                outBuffer.append(hex);
            }
        }
        return outBuffer.toString().substring(0, len);
    }
    
    public int nextSecureInt(final int lower, final int upper) throws NumberIsTooLargeException {
        return new UniformIntegerDistribution(this.getSecRan(), lower, upper).sample();
    }
    
    public long nextSecureLong(final long lower, final long upper) throws NumberIsTooLargeException {
        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, lower, upper, false);
        }
        final RandomGenerator rng = this.getSecRan();
        final long max = upper - lower + 1L;
        if (max <= 0L) {
            long r;
            do {
                r = rng.nextLong();
            } while (r < lower || r > upper);
            return r;
        }
        if (max < 2147483647L) {
            return lower + rng.nextInt((int)max);
        }
        return lower + nextLong(rng, max);
    }
    
    public long nextPoisson(final double mean) throws NotStrictlyPositiveException {
        return new PoissonDistribution(this.getRandomGenerator(), mean, 1.0E-12, 10000000).sample();
    }
    
    public double nextGaussian(final double mu, final double sigma) throws NotStrictlyPositiveException {
        if (sigma <= 0.0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.STANDARD_DEVIATION, sigma);
        }
        return sigma * this.getRandomGenerator().nextGaussian() + mu;
    }
    
    public double nextExponential(final double mean) throws NotStrictlyPositiveException {
        return new ExponentialDistribution(this.getRandomGenerator(), mean, 1.0E-9).sample();
    }
    
    public double nextGamma(final double shape, final double scale) throws NotStrictlyPositiveException {
        return new GammaDistribution(this.getRandomGenerator(), shape, scale, 1.0E-9).sample();
    }
    
    public int nextHypergeometric(final int populationSize, final int numberOfSuccesses, final int sampleSize) throws NotPositiveException, NotStrictlyPositiveException, NumberIsTooLargeException {
        return new HypergeometricDistribution(this.getRandomGenerator(), populationSize, numberOfSuccesses, sampleSize).sample();
    }
    
    public int nextPascal(final int r, final double p) throws NotStrictlyPositiveException, OutOfRangeException {
        return new PascalDistribution(this.getRandomGenerator(), r, p).sample();
    }
    
    public double nextT(final double df) throws NotStrictlyPositiveException {
        return new TDistribution(this.getRandomGenerator(), df, 1.0E-9).sample();
    }
    
    public double nextWeibull(final double shape, final double scale) throws NotStrictlyPositiveException {
        return new WeibullDistribution(this.getRandomGenerator(), shape, scale, 1.0E-9).sample();
    }
    
    public int nextZipf(final int numberOfElements, final double exponent) throws NotStrictlyPositiveException {
        return new ZipfDistribution(this.getRandomGenerator(), numberOfElements, exponent).sample();
    }
    
    public double nextBeta(final double alpha, final double beta) {
        return new BetaDistribution(this.getRandomGenerator(), alpha, beta, 1.0E-9).sample();
    }
    
    public int nextBinomial(final int numberOfTrials, final double probabilityOfSuccess) {
        return new BinomialDistribution(this.getRandomGenerator(), numberOfTrials, probabilityOfSuccess).sample();
    }
    
    public double nextCauchy(final double median, final double scale) {
        return new CauchyDistribution(this.getRandomGenerator(), median, scale, 1.0E-9).sample();
    }
    
    public double nextChiSquare(final double df) {
        return new ChiSquaredDistribution(this.getRandomGenerator(), df, 1.0E-9).sample();
    }
    
    public double nextF(final double numeratorDf, final double denominatorDf) throws NotStrictlyPositiveException {
        return new FDistribution(this.getRandomGenerator(), numeratorDf, denominatorDf, 1.0E-9).sample();
    }
    
    public double nextUniform(final double lower, final double upper) throws NumberIsTooLargeException, NotFiniteNumberException, NotANumberException {
        return this.nextUniform(lower, upper, false);
    }
    
    public double nextUniform(final double lower, final double upper, final boolean lowerInclusive) throws NumberIsTooLargeException, NotFiniteNumberException, NotANumberException {
        if (lower >= upper) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_BOUND_NOT_BELOW_UPPER_BOUND, lower, upper, false);
        }
        if (Double.isInfinite(lower)) {
            throw new NotFiniteNumberException(LocalizedFormats.INFINITE_BOUND, lower, new Object[0]);
        }
        if (Double.isInfinite(upper)) {
            throw new NotFiniteNumberException(LocalizedFormats.INFINITE_BOUND, upper, new Object[0]);
        }
        if (Double.isNaN(lower) || Double.isNaN(upper)) {
            throw new NotANumberException();
        }
        RandomGenerator generator;
        double u;
        for (generator = this.getRandomGenerator(), u = generator.nextDouble(); !lowerInclusive && u <= 0.0; u = generator.nextDouble()) {}
        return u * upper + (1.0 - u) * lower;
    }
    
    public int[] nextPermutation(final int n, final int k) throws NumberIsTooLargeException, NotStrictlyPositiveException {
        if (k > n) {
            throw new NumberIsTooLargeException(LocalizedFormats.PERMUTATION_EXCEEDS_N, k, n, true);
        }
        if (k <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.PERMUTATION_SIZE, k);
        }
        final int[] index = MathArrays.natural(n);
        MathArrays.shuffle(index, this.getRandomGenerator());
        return MathArrays.copyOf(index, k);
    }
    
    public Object[] nextSample(final Collection<?> c, final int k) throws NumberIsTooLargeException, NotStrictlyPositiveException {
        final int len = c.size();
        if (k > len) {
            throw new NumberIsTooLargeException(LocalizedFormats.SAMPLE_SIZE_EXCEEDS_COLLECTION_SIZE, k, len, true);
        }
        if (k <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES, k);
        }
        final Object[] objects = c.toArray();
        final int[] index = this.nextPermutation(len, k);
        final Object[] result = new Object[k];
        for (int i = 0; i < k; ++i) {
            result[i] = objects[index[i]];
        }
        return result;
    }
    
    public void reSeed(final long seed) {
        this.getRandomGenerator().setSeed(seed);
    }
    
    public void reSeedSecure() {
        this.getSecRan().setSeed(System.currentTimeMillis());
    }
    
    public void reSeedSecure(final long seed) {
        this.getSecRan().setSeed(seed);
    }
    
    public void reSeed() {
        this.getRandomGenerator().setSeed(System.currentTimeMillis() + System.identityHashCode(this));
    }
    
    public void setSecureAlgorithm(final String algorithm, final String provider) throws NoSuchAlgorithmException, NoSuchProviderException {
        this.secRand = RandomGeneratorFactory.createRandomGenerator(SecureRandom.getInstance(algorithm, provider));
    }
    
    public RandomGenerator getRandomGenerator() {
        if (this.rand == null) {
            this.initRan();
        }
        return this.rand;
    }
    
    private void initRan() {
        this.rand = new Well19937c(System.currentTimeMillis() + System.identityHashCode(this));
    }
    
    private RandomGenerator getSecRan() {
        if (this.secRand == null) {
            (this.secRand = RandomGeneratorFactory.createRandomGenerator(new SecureRandom())).setSeed(System.currentTimeMillis() + System.identityHashCode(this));
        }
        return this.secRand;
    }
}
