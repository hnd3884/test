package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.ranking.TiesStrategy;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;

public class WilcoxonSignedRankTest
{
    private NaturalRanking naturalRanking;
    
    public WilcoxonSignedRankTest() {
        this.naturalRanking = new NaturalRanking(NaNStrategy.FIXED, TiesStrategy.AVERAGE);
    }
    
    public WilcoxonSignedRankTest(final NaNStrategy nanStrategy, final TiesStrategy tiesStrategy) {
        this.naturalRanking = new NaturalRanking(nanStrategy, tiesStrategy);
    }
    
    private void ensureDataConformance(final double[] x, final double[] y) throws NullArgumentException, NoDataException, DimensionMismatchException {
        if (x == null || y == null) {
            throw new NullArgumentException();
        }
        if (x.length == 0 || y.length == 0) {
            throw new NoDataException();
        }
        if (y.length != x.length) {
            throw new DimensionMismatchException(y.length, x.length);
        }
    }
    
    private double[] calculateDifferences(final double[] x, final double[] y) {
        final double[] z = new double[x.length];
        for (int i = 0; i < x.length; ++i) {
            z[i] = y[i] - x[i];
        }
        return z;
    }
    
    private double[] calculateAbsoluteDifferences(final double[] z) throws NullArgumentException, NoDataException {
        if (z == null) {
            throw new NullArgumentException();
        }
        if (z.length == 0) {
            throw new NoDataException();
        }
        final double[] zAbs = new double[z.length];
        for (int i = 0; i < z.length; ++i) {
            zAbs[i] = FastMath.abs(z[i]);
        }
        return zAbs;
    }
    
    public double wilcoxonSignedRank(final double[] x, final double[] y) throws NullArgumentException, NoDataException, DimensionMismatchException {
        this.ensureDataConformance(x, y);
        final double[] z = this.calculateDifferences(x, y);
        final double[] zAbs = this.calculateAbsoluteDifferences(z);
        final double[] ranks = this.naturalRanking.rank(zAbs);
        double Wplus = 0.0;
        for (int i = 0; i < z.length; ++i) {
            if (z[i] > 0.0) {
                Wplus += ranks[i];
            }
        }
        final int N = x.length;
        final double Wminus = N * (N + 1) / 2.0 - Wplus;
        return FastMath.max(Wplus, Wminus);
    }
    
    private double calculateExactPValue(final double Wmax, final int N) {
        final int m = 1 << N;
        int largerRankSums = 0;
        for (int i = 0; i < m; ++i) {
            int rankSum = 0;
            for (int j = 0; j < N; ++j) {
                if ((i >> j & 0x1) == 0x1) {
                    rankSum += j + 1;
                }
            }
            if (rankSum >= Wmax) {
                ++largerRankSums;
            }
        }
        return 2.0 * largerRankSums / m;
    }
    
    private double calculateAsymptoticPValue(final double Wmin, final int N) {
        final double ES = N * (N + 1) / 4.0;
        final double VarS = ES * ((2 * N + 1) / 6.0);
        final double z = (Wmin - ES - 0.5) / FastMath.sqrt(VarS);
        final NormalDistribution standardNormal = new NormalDistribution(null, 0.0, 1.0);
        return 2.0 * standardNormal.cumulativeProbability(z);
    }
    
    public double wilcoxonSignedRankTest(final double[] x, final double[] y, final boolean exactPValue) throws NullArgumentException, NoDataException, DimensionMismatchException, NumberIsTooLargeException, ConvergenceException, MaxCountExceededException {
        this.ensureDataConformance(x, y);
        final int N = x.length;
        final double Wmax = this.wilcoxonSignedRank(x, y);
        if (exactPValue && N > 30) {
            throw new NumberIsTooLargeException(N, 30, true);
        }
        if (exactPValue) {
            return this.calculateExactPValue(Wmax, N);
        }
        final double Wmin = N * (N + 1) / 2.0 - Wmax;
        return this.calculateAsymptoticPValue(Wmin, N);
    }
}
