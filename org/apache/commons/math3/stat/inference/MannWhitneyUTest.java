package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.stat.ranking.TiesStrategy;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.stat.ranking.NaturalRanking;

public class MannWhitneyUTest
{
    private NaturalRanking naturalRanking;
    
    public MannWhitneyUTest() {
        this.naturalRanking = new NaturalRanking(NaNStrategy.FIXED, TiesStrategy.AVERAGE);
    }
    
    public MannWhitneyUTest(final NaNStrategy nanStrategy, final TiesStrategy tiesStrategy) {
        this.naturalRanking = new NaturalRanking(nanStrategy, tiesStrategy);
    }
    
    private void ensureDataConformance(final double[] x, final double[] y) throws NullArgumentException, NoDataException {
        if (x == null || y == null) {
            throw new NullArgumentException();
        }
        if (x.length == 0 || y.length == 0) {
            throw new NoDataException();
        }
    }
    
    private double[] concatenateSamples(final double[] x, final double[] y) {
        final double[] z = new double[x.length + y.length];
        System.arraycopy(x, 0, z, 0, x.length);
        System.arraycopy(y, 0, z, x.length, y.length);
        return z;
    }
    
    public double mannWhitneyU(final double[] x, final double[] y) throws NullArgumentException, NoDataException {
        this.ensureDataConformance(x, y);
        final double[] z = this.concatenateSamples(x, y);
        final double[] ranks = this.naturalRanking.rank(z);
        double sumRankX = 0.0;
        for (int i = 0; i < x.length; ++i) {
            sumRankX += ranks[i];
        }
        final double U1 = sumRankX - x.length * (long)(x.length + 1) / 2L;
        final double U2 = x.length * (long)y.length - U1;
        return FastMath.max(U1, U2);
    }
    
    private double calculateAsymptoticPValue(final double Umin, final int n1, final int n2) throws ConvergenceException, MaxCountExceededException {
        final long n1n2prod = n1 * (long)n2;
        final double EU = n1n2prod / 2.0;
        final double VarU = n1n2prod * (n1 + n2 + 1) / 12.0;
        final double z = (Umin - EU) / FastMath.sqrt(VarU);
        final NormalDistribution standardNormal = new NormalDistribution(null, 0.0, 1.0);
        return 2.0 * standardNormal.cumulativeProbability(z);
    }
    
    public double mannWhitneyUTest(final double[] x, final double[] y) throws NullArgumentException, NoDataException, ConvergenceException, MaxCountExceededException {
        this.ensureDataConformance(x, y);
        final double Umax = this.mannWhitneyU(x, y);
        final double Umin = x.length * (long)y.length - Umax;
        return this.calculateAsymptoticPValue(Umin, x.length, y.length);
    }
}
