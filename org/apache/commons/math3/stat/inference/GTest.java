package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class GTest
{
    public double g(final double[] expected, final long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException {
        if (expected.length < 2) {
            throw new DimensionMismatchException(expected.length, 2);
        }
        if (expected.length != observed.length) {
            throw new DimensionMismatchException(expected.length, observed.length);
        }
        MathArrays.checkPositive(expected);
        MathArrays.checkNonNegative(observed);
        double sumExpected = 0.0;
        double sumObserved = 0.0;
        for (int i = 0; i < observed.length; ++i) {
            sumExpected += expected[i];
            sumObserved += observed[i];
        }
        double ratio = 1.0;
        boolean rescale = false;
        if (FastMath.abs(sumExpected - sumObserved) > 1.0E-5) {
            ratio = sumObserved / sumExpected;
            rescale = true;
        }
        double sum = 0.0;
        for (int j = 0; j < observed.length; ++j) {
            final double dev = rescale ? FastMath.log(observed[j] / (ratio * expected[j])) : FastMath.log(observed[j] / expected[j]);
            sum += observed[j] * dev;
        }
        return 2.0 * sum;
    }
    
    public double gTest(final double[] expected, final long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, MaxCountExceededException {
        final ChiSquaredDistribution distribution = new ChiSquaredDistribution(null, expected.length - 1.0);
        return 1.0 - distribution.cumulativeProbability(this.g(expected, observed));
    }
    
    public double gTestIntrinsic(final double[] expected, final long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, MaxCountExceededException {
        final ChiSquaredDistribution distribution = new ChiSquaredDistribution(null, expected.length - 2.0);
        return 1.0 - distribution.cumulativeProbability(this.g(expected, observed));
    }
    
    public boolean gTest(final double[] expected, final long[] observed, final double alpha) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, OutOfRangeException, MaxCountExceededException {
        if (alpha <= 0.0 || alpha > 0.5) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, alpha, 0, 0.5);
        }
        return this.gTest(expected, observed) < alpha;
    }
    
    private double entropy(final long[][] k) {
        double h = 0.0;
        double sum_k = 0.0;
        for (int i = 0; i < k.length; ++i) {
            for (int j = 0; j < k[i].length; ++j) {
                sum_k += k[i][j];
            }
        }
        for (int i = 0; i < k.length; ++i) {
            for (int j = 0; j < k[i].length; ++j) {
                if (k[i][j] != 0L) {
                    final double p_ij = k[i][j] / sum_k;
                    h += p_ij * FastMath.log(p_ij);
                }
            }
        }
        return -h;
    }
    
    private double entropy(final long[] k) {
        double h = 0.0;
        double sum_k = 0.0;
        for (int i = 0; i < k.length; ++i) {
            sum_k += k[i];
        }
        for (int i = 0; i < k.length; ++i) {
            if (k[i] != 0L) {
                final double p_i = k[i] / sum_k;
                h += p_i * FastMath.log(p_i);
            }
        }
        return -h;
    }
    
    public double gDataSetsComparison(final long[] observed1, final long[] observed2) throws DimensionMismatchException, NotPositiveException, ZeroException {
        if (observed1.length < 2) {
            throw new DimensionMismatchException(observed1.length, 2);
        }
        if (observed1.length != observed2.length) {
            throw new DimensionMismatchException(observed1.length, observed2.length);
        }
        MathArrays.checkNonNegative(observed1);
        MathArrays.checkNonNegative(observed2);
        long countSum1 = 0L;
        long countSum2 = 0L;
        final long[] collSums = new long[observed1.length];
        final long[][] k = new long[2][observed1.length];
        for (int i = 0; i < observed1.length; ++i) {
            if (observed1[i] == 0L && observed2[i] == 0L) {
                throw new ZeroException(LocalizedFormats.OBSERVED_COUNTS_BOTTH_ZERO_FOR_ENTRY, new Object[] { i });
            }
            countSum1 += observed1[i];
            countSum2 += observed2[i];
            collSums[i] = observed1[i] + observed2[i];
            k[0][i] = observed1[i];
            k[1][i] = observed2[i];
        }
        if (countSum1 == 0L || countSum2 == 0L) {
            throw new ZeroException();
        }
        final long[] rowSums = { countSum1, countSum2 };
        final double sum = countSum1 + (double)countSum2;
        return 2.0 * sum * (this.entropy(rowSums) + this.entropy(collSums) - this.entropy(k));
    }
    
    public double rootLogLikelihoodRatio(final long k11, final long k12, final long k21, final long k22) {
        final double llr = this.gDataSetsComparison(new long[] { k11, k12 }, new long[] { k21, k22 });
        double sqrt = FastMath.sqrt(llr);
        if (k11 / (double)(k11 + k12) < k21 / (double)(k21 + k22)) {
            sqrt = -sqrt;
        }
        return sqrt;
    }
    
    public double gTestDataSetsComparison(final long[] observed1, final long[] observed2) throws DimensionMismatchException, NotPositiveException, ZeroException, MaxCountExceededException {
        final ChiSquaredDistribution distribution = new ChiSquaredDistribution(null, observed1.length - 1.0);
        return 1.0 - distribution.cumulativeProbability(this.gDataSetsComparison(observed1, observed2));
    }
    
    public boolean gTestDataSetsComparison(final long[] observed1, final long[] observed2, final double alpha) throws DimensionMismatchException, NotPositiveException, ZeroException, OutOfRangeException, MaxCountExceededException {
        if (alpha <= 0.0 || alpha > 0.5) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, alpha, 0, 0.5);
        }
        return this.gTestDataSetsComparison(observed1, observed2) < alpha;
    }
}
