package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.NullArgumentException;
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

public class ChiSquareTest
{
    public double chiSquare(final double[] expected, final long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException {
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
        double sumSq = 0.0;
        for (int j = 0; j < observed.length; ++j) {
            if (rescale) {
                final double dev = observed[j] - ratio * expected[j];
                sumSq += dev * dev / (ratio * expected[j]);
            }
            else {
                final double dev = observed[j] - expected[j];
                sumSq += dev * dev / expected[j];
            }
        }
        return sumSq;
    }
    
    public double chiSquareTest(final double[] expected, final long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, MaxCountExceededException {
        final ChiSquaredDistribution distribution = new ChiSquaredDistribution(null, expected.length - 1.0);
        return 1.0 - distribution.cumulativeProbability(this.chiSquare(expected, observed));
    }
    
    public boolean chiSquareTest(final double[] expected, final long[] observed, final double alpha) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, OutOfRangeException, MaxCountExceededException {
        if (alpha <= 0.0 || alpha > 0.5) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, alpha, 0, 0.5);
        }
        return this.chiSquareTest(expected, observed) < alpha;
    }
    
    public double chiSquare(final long[][] counts) throws NullArgumentException, NotPositiveException, DimensionMismatchException {
        this.checkArray(counts);
        final int nRows = counts.length;
        final int nCols = counts[0].length;
        final double[] rowSum = new double[nRows];
        final double[] colSum = new double[nCols];
        double total = 0.0;
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                final double[] array = rowSum;
                final int n = row;
                array[n] += counts[row][col];
                final double[] array2 = colSum;
                final int n2 = col;
                array2[n2] += counts[row][col];
                total += counts[row][col];
            }
        }
        double sumSq = 0.0;
        double expected = 0.0;
        for (int row2 = 0; row2 < nRows; ++row2) {
            for (int col2 = 0; col2 < nCols; ++col2) {
                expected = rowSum[row2] * colSum[col2] / total;
                sumSq += (counts[row2][col2] - expected) * (counts[row2][col2] - expected) / expected;
            }
        }
        return sumSq;
    }
    
    public double chiSquareTest(final long[][] counts) throws NullArgumentException, DimensionMismatchException, NotPositiveException, MaxCountExceededException {
        this.checkArray(counts);
        final double df = (counts.length - 1.0) * (counts[0].length - 1.0);
        final ChiSquaredDistribution distribution = new ChiSquaredDistribution(df);
        return 1.0 - distribution.cumulativeProbability(this.chiSquare(counts));
    }
    
    public boolean chiSquareTest(final long[][] counts, final double alpha) throws NullArgumentException, DimensionMismatchException, NotPositiveException, OutOfRangeException, MaxCountExceededException {
        if (alpha <= 0.0 || alpha > 0.5) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, alpha, 0, 0.5);
        }
        return this.chiSquareTest(counts) < alpha;
    }
    
    public double chiSquareDataSetsComparison(final long[] observed1, final long[] observed2) throws DimensionMismatchException, NotPositiveException, ZeroException {
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
        boolean unequalCounts = false;
        double weight = 0.0;
        for (int i = 0; i < observed1.length; ++i) {
            countSum1 += observed1[i];
            countSum2 += observed2[i];
        }
        if (countSum1 == 0L || countSum2 == 0L) {
            throw new ZeroException();
        }
        unequalCounts = (countSum1 != countSum2);
        if (unequalCounts) {
            weight = FastMath.sqrt(countSum1 / (double)countSum2);
        }
        double sumSq = 0.0;
        double dev = 0.0;
        double obs1 = 0.0;
        double obs2 = 0.0;
        for (int j = 0; j < observed1.length; ++j) {
            if (observed1[j] == 0L && observed2[j] == 0L) {
                throw new ZeroException(LocalizedFormats.OBSERVED_COUNTS_BOTTH_ZERO_FOR_ENTRY, new Object[] { j });
            }
            obs1 = (double)observed1[j];
            obs2 = (double)observed2[j];
            if (unequalCounts) {
                dev = obs1 / weight - obs2 * weight;
            }
            else {
                dev = obs1 - obs2;
            }
            sumSq += dev * dev / (obs1 + obs2);
        }
        return sumSq;
    }
    
    public double chiSquareTestDataSetsComparison(final long[] observed1, final long[] observed2) throws DimensionMismatchException, NotPositiveException, ZeroException, MaxCountExceededException {
        final ChiSquaredDistribution distribution = new ChiSquaredDistribution(null, observed1.length - 1.0);
        return 1.0 - distribution.cumulativeProbability(this.chiSquareDataSetsComparison(observed1, observed2));
    }
    
    public boolean chiSquareTestDataSetsComparison(final long[] observed1, final long[] observed2, final double alpha) throws DimensionMismatchException, NotPositiveException, ZeroException, OutOfRangeException, MaxCountExceededException {
        if (alpha <= 0.0 || alpha > 0.5) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, alpha, 0, 0.5);
        }
        return this.chiSquareTestDataSetsComparison(observed1, observed2) < alpha;
    }
    
    private void checkArray(final long[][] in) throws NullArgumentException, DimensionMismatchException, NotPositiveException {
        if (in.length < 2) {
            throw new DimensionMismatchException(in.length, 2);
        }
        if (in[0].length < 2) {
            throw new DimensionMismatchException(in[0].length, 2);
        }
        MathArrays.checkRectangular(in);
        MathArrays.checkNonNegative(in);
    }
}
