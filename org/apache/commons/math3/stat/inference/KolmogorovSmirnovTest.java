package org.apache.commons.math3.stat.inference;

import java.util.HashSet;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.fraction.BigFractionField;
import org.apache.commons.math3.fraction.FractionConversionException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.distribution.EnumeratedRealDistribution;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.FastMath;
import java.util.Arrays;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.random.RandomGenerator;

public class KolmogorovSmirnovTest
{
    protected static final int MAXIMUM_PARTIAL_SUM_COUNT = 100000;
    protected static final double KS_SUM_CAUCHY_CRITERION = 1.0E-20;
    protected static final double PG_SUM_RELATIVE_ERROR = 1.0E-10;
    @Deprecated
    protected static final int SMALL_SAMPLE_PRODUCT = 200;
    protected static final int LARGE_SAMPLE_PRODUCT = 10000;
    @Deprecated
    protected static final int MONTE_CARLO_ITERATIONS = 1000000;
    private final RandomGenerator rng;
    
    public KolmogorovSmirnovTest() {
        this.rng = new Well19937c();
    }
    
    @Deprecated
    public KolmogorovSmirnovTest(final RandomGenerator rng) {
        this.rng = rng;
    }
    
    public double kolmogorovSmirnovTest(final RealDistribution distribution, final double[] data, final boolean exact) {
        return 1.0 - this.cdf(this.kolmogorovSmirnovStatistic(distribution, data), data.length, exact);
    }
    
    public double kolmogorovSmirnovStatistic(final RealDistribution distribution, final double[] data) {
        this.checkArray(data);
        final int n = data.length;
        final double nd = n;
        final double[] dataCopy = new double[n];
        System.arraycopy(data, 0, dataCopy, 0, n);
        Arrays.sort(dataCopy);
        double d = 0.0;
        for (int i = 1; i <= n; ++i) {
            final double yi = distribution.cumulativeProbability(dataCopy[i - 1]);
            final double currD = FastMath.max(yi - (i - 1) / nd, i / nd - yi);
            if (currD > d) {
                d = currD;
            }
        }
        return d;
    }
    
    public double kolmogorovSmirnovTest(final double[] x, final double[] y, final boolean strict) {
        final long lengthProduct = x.length * (long)y.length;
        double[] xa = null;
        double[] ya = null;
        if (lengthProduct < 10000L && hasTies(x, y)) {
            xa = MathArrays.copyOf(x);
            ya = MathArrays.copyOf(y);
            fixTies(xa, ya);
        }
        else {
            xa = x;
            ya = y;
        }
        if (lengthProduct < 10000L) {
            return this.exactP(this.kolmogorovSmirnovStatistic(xa, ya), x.length, y.length, strict);
        }
        return this.approximateP(this.kolmogorovSmirnovStatistic(x, y), x.length, y.length);
    }
    
    public double kolmogorovSmirnovTest(final double[] x, final double[] y) {
        return this.kolmogorovSmirnovTest(x, y, true);
    }
    
    public double kolmogorovSmirnovStatistic(final double[] x, final double[] y) {
        return this.integralKolmogorovSmirnovStatistic(x, y) / (double)(x.length * (long)y.length);
    }
    
    private long integralKolmogorovSmirnovStatistic(final double[] x, final double[] y) {
        this.checkArray(x);
        this.checkArray(y);
        final double[] sx = MathArrays.copyOf(x);
        final double[] sy = MathArrays.copyOf(y);
        Arrays.sort(sx);
        Arrays.sort(sy);
        final int n = sx.length;
        final int m = sy.length;
        int rankX = 0;
        int rankY = 0;
        long curD = 0L;
        long supD = 0L;
        do {
            double z;
            for (z = ((Double.compare(sx[rankX], sy[rankY]) <= 0) ? sx[rankX] : sy[rankY]); rankX < n && Double.compare(sx[rankX], z) == 0; ++rankX, curD += m) {}
            while (rankY < m && Double.compare(sy[rankY], z) == 0) {
                ++rankY;
                curD -= n;
            }
            if (curD > supD) {
                supD = curD;
            }
            else {
                if (-curD <= supD) {
                    continue;
                }
                supD = -curD;
            }
        } while (rankX < n && rankY < m);
        return supD;
    }
    
    public double kolmogorovSmirnovTest(final RealDistribution distribution, final double[] data) {
        return this.kolmogorovSmirnovTest(distribution, data, false);
    }
    
    public boolean kolmogorovSmirnovTest(final RealDistribution distribution, final double[] data, final double alpha) {
        if (alpha <= 0.0 || alpha > 0.5) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, alpha, 0, 0.5);
        }
        return this.kolmogorovSmirnovTest(distribution, data) < alpha;
    }
    
    public double bootstrap(final double[] x, final double[] y, final int iterations, final boolean strict) {
        final int xLength = x.length;
        final int yLength = y.length;
        final double[] combined = new double[xLength + yLength];
        System.arraycopy(x, 0, combined, 0, xLength);
        System.arraycopy(y, 0, combined, xLength, yLength);
        final EnumeratedRealDistribution dist = new EnumeratedRealDistribution(this.rng, combined);
        final long d = this.integralKolmogorovSmirnovStatistic(x, y);
        int greaterCount = 0;
        int equalCount = 0;
        for (int i = 0; i < iterations; ++i) {
            final double[] curX = dist.sample(xLength);
            final double[] curY = dist.sample(yLength);
            final long curD = this.integralKolmogorovSmirnovStatistic(curX, curY);
            if (curD > d) {
                ++greaterCount;
            }
            else if (curD == d) {
                ++equalCount;
            }
        }
        return strict ? (greaterCount / (double)iterations) : ((greaterCount + equalCount) / (double)iterations);
    }
    
    public double bootstrap(final double[] x, final double[] y, final int iterations) {
        return this.bootstrap(x, y, iterations, true);
    }
    
    public double cdf(final double d, final int n) throws MathArithmeticException {
        return this.cdf(d, n, false);
    }
    
    public double cdfExact(final double d, final int n) throws MathArithmeticException {
        return this.cdf(d, n, true);
    }
    
    public double cdf(final double d, final int n, final boolean exact) throws MathArithmeticException {
        final double ninv = 1.0 / n;
        final double ninvhalf = 0.5 * ninv;
        if (d <= ninvhalf) {
            return 0.0;
        }
        if (ninvhalf < d && d <= ninv) {
            double res = 1.0;
            final double f = 2.0 * d - ninv;
            for (int i = 1; i <= n; ++i) {
                res *= i * f;
            }
            return res;
        }
        if (1.0 - ninv <= d && d < 1.0) {
            return 1.0 - 2.0 * Math.pow(1.0 - d, n);
        }
        if (1.0 <= d) {
            return 1.0;
        }
        if (exact) {
            return this.exactK(d, n);
        }
        if (n <= 140) {
            return this.roundedK(d, n);
        }
        return this.pelzGood(d, n);
    }
    
    private double exactK(final double d, final int n) throws MathArithmeticException {
        final int k = (int)Math.ceil(n * d);
        final FieldMatrix<BigFraction> H = this.createExactH(d, n);
        final FieldMatrix<BigFraction> Hpower = H.power(n);
        BigFraction pFrac = Hpower.getEntry(k - 1, k - 1);
        for (int i = 1; i <= n; ++i) {
            pFrac = pFrac.multiply(i).divide(n);
        }
        return pFrac.bigDecimalValue(20, 4).doubleValue();
    }
    
    private double roundedK(final double d, final int n) {
        final int k = (int)Math.ceil(n * d);
        final RealMatrix H = this.createRoundedH(d, n);
        final RealMatrix Hpower = H.power(n);
        double pFrac = Hpower.getEntry(k - 1, k - 1);
        for (int i = 1; i <= n; ++i) {
            pFrac *= i / (double)n;
        }
        return pFrac;
    }
    
    public double pelzGood(final double d, final int n) {
        final double sqrtN = FastMath.sqrt(n);
        final double z = d * sqrtN;
        final double z2 = d * d * n;
        final double z3 = z2 * z2;
        final double z4 = z3 * z2;
        final double z5 = z3 * z3;
        double ret = 0.0;
        double sum = 0.0;
        double increment = 0.0;
        double kTerm = 0.0;
        double z2Term = 9.869604401089358 / (8.0 * z2);
        int k;
        for (k = 1; k < 100000; ++k) {
            kTerm = 2 * k - 1;
            increment = FastMath.exp(-z2Term * kTerm * kTerm);
            sum += increment;
            if (increment <= 1.0E-10 * sum) {
                break;
            }
        }
        if (k == 100000) {
            throw new TooManyIterationsException(100000);
        }
        ret = sum * FastMath.sqrt(6.283185307179586) / z;
        final double twoZ2 = 2.0 * z2;
        sum = 0.0;
        kTerm = 0.0;
        double kTerm2 = 0.0;
        for (k = 0; k < 100000; ++k) {
            kTerm = k + 0.5;
            kTerm2 = kTerm * kTerm;
            increment = (9.869604401089358 * kTerm2 - z2) * FastMath.exp(-9.869604401089358 * kTerm2 / twoZ2);
            sum += increment;
            if (FastMath.abs(increment) < 1.0E-10 * FastMath.abs(sum)) {
                break;
            }
        }
        if (k == 100000) {
            throw new TooManyIterationsException(100000);
        }
        final double sqrtHalfPi = FastMath.sqrt(1.5707963267948966);
        ret += sum * sqrtHalfPi / (3.0 * z3 * sqrtN);
        final double z4Term = 2.0 * z3;
        final double z6Term = 6.0 * z4;
        z2Term = 5.0 * z2;
        final double pi4 = 97.40909103400243;
        sum = 0.0;
        kTerm = 0.0;
        kTerm2 = 0.0;
        for (k = 0; k < 100000; ++k) {
            kTerm = k + 0.5;
            kTerm2 = kTerm * kTerm;
            increment = (z6Term + z4Term + 9.869604401089358 * (z4Term - z2Term) * kTerm2 + 97.40909103400243 * (1.0 - twoZ2) * kTerm2 * kTerm2) * FastMath.exp(-9.869604401089358 * kTerm2 / twoZ2);
            sum += increment;
            if (FastMath.abs(increment) < 1.0E-10 * FastMath.abs(sum)) {
                break;
            }
        }
        if (k == 100000) {
            throw new TooManyIterationsException(100000);
        }
        double sum2 = 0.0;
        kTerm2 = 0.0;
        for (k = 1; k < 100000; ++k) {
            kTerm2 = k * k;
            increment = 9.869604401089358 * kTerm2 * FastMath.exp(-9.869604401089358 * kTerm2 / twoZ2);
            sum2 += increment;
            if (FastMath.abs(increment) < 1.0E-10 * FastMath.abs(sum2)) {
                break;
            }
        }
        if (k == 100000) {
            throw new TooManyIterationsException(100000);
        }
        ret += sqrtHalfPi / n * (sum / (36.0 * z2 * z2 * z2 * z) - sum2 / (18.0 * z2 * z));
        final double pi5 = 961.3891935753043;
        sum = 0.0;
        double kTerm3 = 0.0;
        double kTerm4 = 0.0;
        for (k = 0; k < 100000; ++k) {
            kTerm = k + 0.5;
            kTerm2 = kTerm * kTerm;
            kTerm3 = kTerm2 * kTerm2;
            kTerm4 = kTerm3 * kTerm2;
            increment = (961.3891935753043 * kTerm4 * (5.0 - 30.0 * z2) + 97.40909103400243 * kTerm3 * (-60.0 * z2 + 212.0 * z3) + 9.869604401089358 * kTerm2 * (135.0 * z3 - 96.0 * z4) - 30.0 * z4 - 90.0 * z5) * FastMath.exp(-9.869604401089358 * kTerm2 / twoZ2);
            sum += increment;
            if (FastMath.abs(increment) < 1.0E-10 * FastMath.abs(sum)) {
                break;
            }
        }
        if (k == 100000) {
            throw new TooManyIterationsException(100000);
        }
        sum2 = 0.0;
        for (k = 1; k < 100000; ++k) {
            kTerm2 = k * k;
            kTerm3 = kTerm2 * kTerm2;
            increment = (-97.40909103400243 * kTerm3 + 29.608813203268074 * kTerm2 * z2) * FastMath.exp(-9.869604401089358 * kTerm2 / twoZ2);
            sum2 += increment;
            if (FastMath.abs(increment) < 1.0E-10 * FastMath.abs(sum2)) {
                break;
            }
        }
        if (k == 100000) {
            throw new TooManyIterationsException(100000);
        }
        return ret + sqrtHalfPi / (sqrtN * n) * (sum / (3240.0 * z4 * z3) + sum2 / (108.0 * z4));
    }
    
    private FieldMatrix<BigFraction> createExactH(final double d, final int n) throws NumberIsTooLargeException, FractionConversionException {
        final int k = (int)Math.ceil(n * d);
        final int m = 2 * k - 1;
        final double hDouble = k - n * d;
        if (hDouble >= 1.0) {
            throw new NumberIsTooLargeException(hDouble, 1.0, false);
        }
        BigFraction h = null;
        try {
            h = new BigFraction(hDouble, 1.0E-20, 10000);
        }
        catch (final FractionConversionException e1) {
            try {
                h = new BigFraction(hDouble, 1.0E-10, 10000);
            }
            catch (final FractionConversionException e2) {
                h = new BigFraction(hDouble, 1.0E-5, 10000);
            }
        }
        final BigFraction[][] Hdata = new BigFraction[m][m];
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < m; ++j) {
                if (i - j + 1 < 0) {
                    Hdata[i][j] = BigFraction.ZERO;
                }
                else {
                    Hdata[i][j] = BigFraction.ONE;
                }
            }
        }
        final BigFraction[] hPowers = new BigFraction[m];
        hPowers[0] = h;
        for (int l = 1; l < m; ++l) {
            hPowers[l] = h.multiply(hPowers[l - 1]);
        }
        for (int l = 0; l < m; ++l) {
            Hdata[l][0] = Hdata[l][0].subtract(hPowers[l]);
            Hdata[m - 1][l] = Hdata[m - 1][l].subtract(hPowers[m - l - 1]);
        }
        if (h.compareTo(BigFraction.ONE_HALF) == 1) {
            Hdata[m - 1][0] = Hdata[m - 1][0].add(h.multiply(2).subtract(1).pow(m));
        }
        for (int l = 0; l < m; ++l) {
            for (int j2 = 0; j2 < l + 1; ++j2) {
                if (l - j2 + 1 > 0) {
                    for (int g = 2; g <= l - j2 + 1; ++g) {
                        Hdata[l][j2] = Hdata[l][j2].divide(g);
                    }
                }
            }
        }
        return new Array2DRowFieldMatrix<BigFraction>(BigFractionField.getInstance(), Hdata);
    }
    
    private RealMatrix createRoundedH(final double d, final int n) throws NumberIsTooLargeException {
        final int k = (int)Math.ceil(n * d);
        final int m = 2 * k - 1;
        final double h = k - n * d;
        if (h >= 1.0) {
            throw new NumberIsTooLargeException(h, 1.0, false);
        }
        final double[][] Hdata = new double[m][m];
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < m; ++j) {
                if (i - j + 1 < 0) {
                    Hdata[i][j] = 0.0;
                }
                else {
                    Hdata[i][j] = 1.0;
                }
            }
        }
        final double[] hPowers = new double[m];
        hPowers[0] = h;
        for (int l = 1; l < m; ++l) {
            hPowers[l] = h * hPowers[l - 1];
        }
        for (int l = 0; l < m; ++l) {
            Hdata[l][0] -= hPowers[l];
            final double[] array = Hdata[m - 1];
            final int n2 = l;
            array[n2] -= hPowers[m - l - 1];
        }
        if (Double.compare(h, 0.5) > 0) {
            final double[] array2 = Hdata[m - 1];
            final int n3 = 0;
            array2[n3] += FastMath.pow(2.0 * h - 1.0, m);
        }
        for (int l = 0; l < m; ++l) {
            for (int j2 = 0; j2 < l + 1; ++j2) {
                if (l - j2 + 1 > 0) {
                    for (int g = 2; g <= l - j2 + 1; ++g) {
                        final double[] array3 = Hdata[l];
                        final int n4 = j2;
                        array3[n4] /= g;
                    }
                }
            }
        }
        return MatrixUtils.createRealMatrix(Hdata);
    }
    
    private void checkArray(final double[] array) {
        if (array == null) {
            throw new NullArgumentException(LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
        }
        if (array.length < 2) {
            throw new InsufficientDataException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, new Object[] { array.length, 2 });
        }
    }
    
    public double ksSum(final double t, final double tolerance, final int maxIterations) {
        if (t == 0.0) {
            return 0.0;
        }
        final double x = -2.0 * t * t;
        int sign = -1;
        long i = 1L;
        double partialSum = 0.5;
        for (double delta = 1.0; delta > tolerance && i < maxIterations; delta = FastMath.exp(x * i * i), partialSum += sign * delta, sign *= -1, ++i) {}
        if (i == maxIterations) {
            throw new TooManyIterationsException(maxIterations);
        }
        return partialSum * 2.0;
    }
    
    private static long calculateIntegralD(final double d, final int n, final int m, final boolean strict) {
        final double tol = 1.0E-12;
        final long nm = n * (long)m;
        final long upperBound = (long)FastMath.ceil((d - 1.0E-12) * nm);
        final long lowerBound = (long)FastMath.floor((d + 1.0E-12) * nm);
        if (strict && lowerBound == upperBound) {
            return upperBound + 1L;
        }
        return upperBound;
    }
    
    public double exactP(final double d, final int n, final int m, final boolean strict) {
        return 1.0 - n(m, n, m, n, calculateIntegralD(d, m, n, strict), strict) / CombinatoricsUtils.binomialCoefficientDouble(n + m, m);
    }
    
    public double approximateP(final double d, final int n, final int m) {
        final double dm = m;
        final double dn = n;
        return 1.0 - this.ksSum(d * FastMath.sqrt(dm * dn / (dm + dn)), 1.0E-20, 100000);
    }
    
    static void fillBooleanArrayRandomlyWithFixedNumberTrueValues(final boolean[] b, final int numberOfTrueValues, final RandomGenerator rng) {
        Arrays.fill(b, true);
        for (int k = numberOfTrueValues; k < b.length; ++k) {
            final int r = rng.nextInt(k + 1);
            b[b[r] ? r : k] = false;
        }
    }
    
    public double monteCarloP(final double d, final int n, final int m, final boolean strict, final int iterations) {
        return this.integralMonteCarloP(calculateIntegralD(d, n, m, strict), n, m, iterations);
    }
    
    private double integralMonteCarloP(final long d, final int n, final int m, final int iterations) {
        final int nn = FastMath.max(n, m);
        final int mm = FastMath.min(n, m);
        final int sum = nn + mm;
        int tail = 0;
        final boolean[] b = new boolean[sum];
        for (int i = 0; i < iterations; ++i) {
            fillBooleanArrayRandomlyWithFixedNumberTrueValues(b, nn, this.rng);
            long curD = 0L;
            for (int j = 0; j < b.length; ++j) {
                if (b[j]) {
                    curD += mm;
                    if (curD >= d) {
                        ++tail;
                        break;
                    }
                }
                else {
                    curD -= nn;
                    if (curD <= -d) {
                        ++tail;
                        break;
                    }
                }
            }
        }
        return tail / (double)iterations;
    }
    
    private static void fixTies(final double[] x, final double[] y) {
        final double[] values = MathArrays.unique(MathArrays.concatenate(new double[][] { x, y }));
        if (values.length == x.length + y.length) {
            return;
        }
        double minDelta = 1.0;
        double prev = values[0];
        double delta = 1.0;
        for (int i = 1; i < values.length; ++i) {
            delta = prev - values[i];
            if (delta < minDelta) {
                minDelta = delta;
            }
            prev = values[i];
        }
        minDelta /= 2.0;
        final RealDistribution dist = new UniformRealDistribution(new JDKRandomGenerator(100), -minDelta, minDelta);
        int ct = 0;
        boolean ties = true;
        do {
            jitter(x, dist);
            jitter(y, dist);
            ties = hasTies(x, y);
            ++ct;
        } while (ties && ct < 1000);
        if (ties) {
            throw new MathInternalError();
        }
    }
    
    private static boolean hasTies(final double[] x, final double[] y) {
        final HashSet<Double> values = new HashSet<Double>();
        for (int i = 0; i < x.length; ++i) {
            if (!values.add(x[i])) {
                return true;
            }
        }
        for (int i = 0; i < y.length; ++i) {
            if (!values.add(y[i])) {
                return true;
            }
        }
        return false;
    }
    
    private static void jitter(final double[] data, final RealDistribution dist) {
        for (int i = 0; i < data.length; ++i) {
            final int n = i;
            data[n] += dist.sample();
        }
    }
    
    private static int c(final int i, final int j, final int m, final int n, final long cmn, final boolean strict) {
        if (strict) {
            return (FastMath.abs(i * (long)n - j * (long)m) <= cmn) ? 1 : 0;
        }
        return (FastMath.abs(i * (long)n - j * (long)m) < cmn) ? 1 : 0;
    }
    
    private static double n(final int i, final int j, final int m, final int n, final long cnm, final boolean strict) {
        final double[] lag = new double[n];
        double last = 0.0;
        for (int k = 0; k < n; ++k) {
            lag[k] = c(0, k + 1, m, n, cnm, strict);
        }
        for (int k = 1; k <= i; ++k) {
            last = c(k, 0, m, n, cnm, strict);
            for (int l = 1; l <= j; ++l) {
                lag[l - 1] = c(k, l, m, n, cnm, strict) * (last + lag[l - 1]);
                last = lag[l - 1];
            }
        }
        return last;
    }
}
