package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.exception.ConvergenceException;
import java.util.Collection;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NullArgumentException;

public class TestUtils
{
    private static final TTest T_TEST;
    private static final ChiSquareTest CHI_SQUARE_TEST;
    private static final OneWayAnova ONE_WAY_ANANOVA;
    private static final GTest G_TEST;
    private static final KolmogorovSmirnovTest KS_TEST;
    
    private TestUtils() {
    }
    
    public static double homoscedasticT(final double[] sample1, final double[] sample2) throws NullArgumentException, NumberIsTooSmallException {
        return TestUtils.T_TEST.homoscedasticT(sample1, sample2);
    }
    
    public static double homoscedasticT(final StatisticalSummary sampleStats1, final StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException {
        return TestUtils.T_TEST.homoscedasticT(sampleStats1, sampleStats2);
    }
    
    public static boolean homoscedasticTTest(final double[] sample1, final double[] sample2, final double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.T_TEST.homoscedasticTTest(sample1, sample2, alpha);
    }
    
    public static double homoscedasticTTest(final double[] sample1, final double[] sample2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        return TestUtils.T_TEST.homoscedasticTTest(sample1, sample2);
    }
    
    public static double homoscedasticTTest(final StatisticalSummary sampleStats1, final StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        return TestUtils.T_TEST.homoscedasticTTest(sampleStats1, sampleStats2);
    }
    
    public static double pairedT(final double[] sample1, final double[] sample2) throws NullArgumentException, NoDataException, DimensionMismatchException, NumberIsTooSmallException {
        return TestUtils.T_TEST.pairedT(sample1, sample2);
    }
    
    public static boolean pairedTTest(final double[] sample1, final double[] sample2, final double alpha) throws NullArgumentException, NoDataException, DimensionMismatchException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.T_TEST.pairedTTest(sample1, sample2, alpha);
    }
    
    public static double pairedTTest(final double[] sample1, final double[] sample2) throws NullArgumentException, NoDataException, DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException {
        return TestUtils.T_TEST.pairedTTest(sample1, sample2);
    }
    
    public static double t(final double mu, final double[] observed) throws NullArgumentException, NumberIsTooSmallException {
        return TestUtils.T_TEST.t(mu, observed);
    }
    
    public static double t(final double mu, final StatisticalSummary sampleStats) throws NullArgumentException, NumberIsTooSmallException {
        return TestUtils.T_TEST.t(mu, sampleStats);
    }
    
    public static double t(final double[] sample1, final double[] sample2) throws NullArgumentException, NumberIsTooSmallException {
        return TestUtils.T_TEST.t(sample1, sample2);
    }
    
    public static double t(final StatisticalSummary sampleStats1, final StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException {
        return TestUtils.T_TEST.t(sampleStats1, sampleStats2);
    }
    
    public static boolean tTest(final double mu, final double[] sample, final double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.T_TEST.tTest(mu, sample, alpha);
    }
    
    public static double tTest(final double mu, final double[] sample) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        return TestUtils.T_TEST.tTest(mu, sample);
    }
    
    public static boolean tTest(final double mu, final StatisticalSummary sampleStats, final double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.T_TEST.tTest(mu, sampleStats, alpha);
    }
    
    public static double tTest(final double mu, final StatisticalSummary sampleStats) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        return TestUtils.T_TEST.tTest(mu, sampleStats);
    }
    
    public static boolean tTest(final double[] sample1, final double[] sample2, final double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.T_TEST.tTest(sample1, sample2, alpha);
    }
    
    public static double tTest(final double[] sample1, final double[] sample2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        return TestUtils.T_TEST.tTest(sample1, sample2);
    }
    
    public static boolean tTest(final StatisticalSummary sampleStats1, final StatisticalSummary sampleStats2, final double alpha) throws NullArgumentException, NumberIsTooSmallException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.T_TEST.tTest(sampleStats1, sampleStats2, alpha);
    }
    
    public static double tTest(final StatisticalSummary sampleStats1, final StatisticalSummary sampleStats2) throws NullArgumentException, NumberIsTooSmallException, MaxCountExceededException {
        return TestUtils.T_TEST.tTest(sampleStats1, sampleStats2);
    }
    
    public static double chiSquare(final double[] expected, final long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException {
        return TestUtils.CHI_SQUARE_TEST.chiSquare(expected, observed);
    }
    
    public static double chiSquare(final long[][] counts) throws NullArgumentException, NotPositiveException, DimensionMismatchException {
        return TestUtils.CHI_SQUARE_TEST.chiSquare(counts);
    }
    
    public static boolean chiSquareTest(final double[] expected, final long[] observed, final double alpha) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.CHI_SQUARE_TEST.chiSquareTest(expected, observed, alpha);
    }
    
    public static double chiSquareTest(final double[] expected, final long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, MaxCountExceededException {
        return TestUtils.CHI_SQUARE_TEST.chiSquareTest(expected, observed);
    }
    
    public static boolean chiSquareTest(final long[][] counts, final double alpha) throws NullArgumentException, DimensionMismatchException, NotPositiveException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.CHI_SQUARE_TEST.chiSquareTest(counts, alpha);
    }
    
    public static double chiSquareTest(final long[][] counts) throws NullArgumentException, DimensionMismatchException, NotPositiveException, MaxCountExceededException {
        return TestUtils.CHI_SQUARE_TEST.chiSquareTest(counts);
    }
    
    public static double chiSquareDataSetsComparison(final long[] observed1, final long[] observed2) throws DimensionMismatchException, NotPositiveException, ZeroException {
        return TestUtils.CHI_SQUARE_TEST.chiSquareDataSetsComparison(observed1, observed2);
    }
    
    public static double chiSquareTestDataSetsComparison(final long[] observed1, final long[] observed2) throws DimensionMismatchException, NotPositiveException, ZeroException, MaxCountExceededException {
        return TestUtils.CHI_SQUARE_TEST.chiSquareTestDataSetsComparison(observed1, observed2);
    }
    
    public static boolean chiSquareTestDataSetsComparison(final long[] observed1, final long[] observed2, final double alpha) throws DimensionMismatchException, NotPositiveException, ZeroException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.CHI_SQUARE_TEST.chiSquareTestDataSetsComparison(observed1, observed2, alpha);
    }
    
    public static double oneWayAnovaFValue(final Collection<double[]> categoryData) throws NullArgumentException, DimensionMismatchException {
        return TestUtils.ONE_WAY_ANANOVA.anovaFValue(categoryData);
    }
    
    public static double oneWayAnovaPValue(final Collection<double[]> categoryData) throws NullArgumentException, DimensionMismatchException, ConvergenceException, MaxCountExceededException {
        return TestUtils.ONE_WAY_ANANOVA.anovaPValue(categoryData);
    }
    
    public static boolean oneWayAnovaTest(final Collection<double[]> categoryData, final double alpha) throws NullArgumentException, DimensionMismatchException, OutOfRangeException, ConvergenceException, MaxCountExceededException {
        return TestUtils.ONE_WAY_ANANOVA.anovaTest(categoryData, alpha);
    }
    
    public static double g(final double[] expected, final long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException {
        return TestUtils.G_TEST.g(expected, observed);
    }
    
    public static double gTest(final double[] expected, final long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, MaxCountExceededException {
        return TestUtils.G_TEST.gTest(expected, observed);
    }
    
    public static double gTestIntrinsic(final double[] expected, final long[] observed) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, MaxCountExceededException {
        return TestUtils.G_TEST.gTestIntrinsic(expected, observed);
    }
    
    public static boolean gTest(final double[] expected, final long[] observed, final double alpha) throws NotPositiveException, NotStrictlyPositiveException, DimensionMismatchException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.G_TEST.gTest(expected, observed, alpha);
    }
    
    public static double gDataSetsComparison(final long[] observed1, final long[] observed2) throws DimensionMismatchException, NotPositiveException, ZeroException {
        return TestUtils.G_TEST.gDataSetsComparison(observed1, observed2);
    }
    
    public static double rootLogLikelihoodRatio(final long k11, final long k12, final long k21, final long k22) throws DimensionMismatchException, NotPositiveException, ZeroException {
        return TestUtils.G_TEST.rootLogLikelihoodRatio(k11, k12, k21, k22);
    }
    
    public static double gTestDataSetsComparison(final long[] observed1, final long[] observed2) throws DimensionMismatchException, NotPositiveException, ZeroException, MaxCountExceededException {
        return TestUtils.G_TEST.gTestDataSetsComparison(observed1, observed2);
    }
    
    public static boolean gTestDataSetsComparison(final long[] observed1, final long[] observed2, final double alpha) throws DimensionMismatchException, NotPositiveException, ZeroException, OutOfRangeException, MaxCountExceededException {
        return TestUtils.G_TEST.gTestDataSetsComparison(observed1, observed2, alpha);
    }
    
    public static double kolmogorovSmirnovStatistic(final RealDistribution dist, final double[] data) throws InsufficientDataException, NullArgumentException {
        return TestUtils.KS_TEST.kolmogorovSmirnovStatistic(dist, data);
    }
    
    public static double kolmogorovSmirnovTest(final RealDistribution dist, final double[] data) throws InsufficientDataException, NullArgumentException {
        return TestUtils.KS_TEST.kolmogorovSmirnovTest(dist, data);
    }
    
    public static double kolmogorovSmirnovTest(final RealDistribution dist, final double[] data, final boolean strict) throws InsufficientDataException, NullArgumentException {
        return TestUtils.KS_TEST.kolmogorovSmirnovTest(dist, data, strict);
    }
    
    public static boolean kolmogorovSmirnovTest(final RealDistribution dist, final double[] data, final double alpha) throws InsufficientDataException, NullArgumentException {
        return TestUtils.KS_TEST.kolmogorovSmirnovTest(dist, data, alpha);
    }
    
    public static double kolmogorovSmirnovStatistic(final double[] x, final double[] y) throws InsufficientDataException, NullArgumentException {
        return TestUtils.KS_TEST.kolmogorovSmirnovStatistic(x, y);
    }
    
    public static double kolmogorovSmirnovTest(final double[] x, final double[] y) throws InsufficientDataException, NullArgumentException {
        return TestUtils.KS_TEST.kolmogorovSmirnovTest(x, y);
    }
    
    public static double kolmogorovSmirnovTest(final double[] x, final double[] y, final boolean strict) throws InsufficientDataException, NullArgumentException {
        return TestUtils.KS_TEST.kolmogorovSmirnovTest(x, y, strict);
    }
    
    public static double exactP(final double d, final int m, final int n, final boolean strict) {
        return TestUtils.KS_TEST.exactP(d, n, m, strict);
    }
    
    public static double approximateP(final double d, final int n, final int m) {
        return TestUtils.KS_TEST.approximateP(d, n, m);
    }
    
    public static double monteCarloP(final double d, final int n, final int m, final boolean strict, final int iterations) {
        return TestUtils.KS_TEST.monteCarloP(d, n, m, strict, iterations);
    }
    
    static {
        T_TEST = new TTest();
        CHI_SQUARE_TEST = new ChiSquareTest();
        ONE_WAY_ANANOVA = new OneWayAnova();
        G_TEST = new GTest();
        KS_TEST = new KolmogorovSmirnovTest();
    }
}
