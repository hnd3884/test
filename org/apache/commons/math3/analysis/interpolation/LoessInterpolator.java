package org.apache.commons.math3.analysis.interpolation;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.FastMath;
import java.util.Arrays;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.NotFiniteNumberException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.io.Serializable;

public class LoessInterpolator implements UnivariateInterpolator, Serializable
{
    public static final double DEFAULT_BANDWIDTH = 0.3;
    public static final int DEFAULT_ROBUSTNESS_ITERS = 2;
    public static final double DEFAULT_ACCURACY = 1.0E-12;
    private static final long serialVersionUID = 5204927143605193821L;
    private final double bandwidth;
    private final int robustnessIters;
    private final double accuracy;
    
    public LoessInterpolator() {
        this.bandwidth = 0.3;
        this.robustnessIters = 2;
        this.accuracy = 1.0E-12;
    }
    
    public LoessInterpolator(final double bandwidth, final int robustnessIters) {
        this(bandwidth, robustnessIters, 1.0E-12);
    }
    
    public LoessInterpolator(final double bandwidth, final int robustnessIters, final double accuracy) throws OutOfRangeException, NotPositiveException {
        if (bandwidth < 0.0 || bandwidth > 1.0) {
            throw new OutOfRangeException(LocalizedFormats.BANDWIDTH, bandwidth, 0, 1);
        }
        this.bandwidth = bandwidth;
        if (robustnessIters < 0) {
            throw new NotPositiveException(LocalizedFormats.ROBUSTNESS_ITERATIONS, robustnessIters);
        }
        this.robustnessIters = robustnessIters;
        this.accuracy = accuracy;
    }
    
    public final PolynomialSplineFunction interpolate(final double[] xval, final double[] yval) throws NonMonotonicSequenceException, DimensionMismatchException, NoDataException, NotFiniteNumberException, NumberIsTooSmallException {
        return new SplineInterpolator().interpolate(xval, this.smooth(xval, yval));
    }
    
    public final double[] smooth(final double[] xval, final double[] yval, final double[] weights) throws NonMonotonicSequenceException, DimensionMismatchException, NoDataException, NotFiniteNumberException, NumberIsTooSmallException {
        if (xval.length != yval.length) {
            throw new DimensionMismatchException(xval.length, yval.length);
        }
        final int n = xval.length;
        if (n == 0) {
            throw new NoDataException();
        }
        checkAllFiniteReal(xval);
        checkAllFiniteReal(yval);
        checkAllFiniteReal(weights);
        MathArrays.checkOrder(xval);
        if (n == 1) {
            return new double[] { yval[0] };
        }
        if (n == 2) {
            return new double[] { yval[0], yval[1] };
        }
        final int bandwidthInPoints = (int)(this.bandwidth * n);
        if (bandwidthInPoints < 2) {
            throw new NumberIsTooSmallException(LocalizedFormats.BANDWIDTH, bandwidthInPoints, 2, true);
        }
        final double[] res = new double[n];
        final double[] residuals = new double[n];
        final double[] sortedResiduals = new double[n];
        final double[] robustnessWeights = new double[n];
        Arrays.fill(robustnessWeights, 1.0);
        for (int iter = 0; iter <= this.robustnessIters; ++iter) {
            final int[] bandwidthInterval = { 0, bandwidthInPoints - 1 };
            for (int i = 0; i < n; ++i) {
                final double x = xval[i];
                if (i > 0) {
                    updateBandwidthInterval(xval, weights, i, bandwidthInterval);
                }
                final int ileft = bandwidthInterval[0];
                final int iright = bandwidthInterval[1];
                int edge;
                if (xval[i] - xval[ileft] > xval[iright] - xval[i]) {
                    edge = ileft;
                }
                else {
                    edge = iright;
                }
                double sumWeights = 0.0;
                double sumX = 0.0;
                double sumXSquared = 0.0;
                double sumY = 0.0;
                double sumXY = 0.0;
                final double denom = FastMath.abs(1.0 / (xval[edge] - x));
                for (int k = ileft; k <= iright; ++k) {
                    final double xk = xval[k];
                    final double yk = yval[k];
                    final double dist = (k < i) ? (x - xk) : (xk - x);
                    final double w = tricube(dist * denom) * robustnessWeights[k] * weights[k];
                    final double xkw = xk * w;
                    sumWeights += w;
                    sumX += xkw;
                    sumXSquared += xk * xkw;
                    sumY += yk * w;
                    sumXY += yk * xkw;
                }
                final double meanX = sumX / sumWeights;
                final double meanY = sumY / sumWeights;
                final double meanXY = sumXY / sumWeights;
                final double meanXSquared = sumXSquared / sumWeights;
                double beta;
                if (FastMath.sqrt(FastMath.abs(meanXSquared - meanX * meanX)) < this.accuracy) {
                    beta = 0.0;
                }
                else {
                    beta = (meanXY - meanX * meanY) / (meanXSquared - meanX * meanX);
                }
                final double alpha = meanY - beta * meanX;
                res[i] = beta * x + alpha;
                residuals[i] = FastMath.abs(yval[i] - res[i]);
            }
            if (iter == this.robustnessIters) {
                break;
            }
            System.arraycopy(residuals, 0, sortedResiduals, 0, n);
            Arrays.sort(sortedResiduals);
            final double medianResidual = sortedResiduals[n / 2];
            if (FastMath.abs(medianResidual) < this.accuracy) {
                break;
            }
            for (int j = 0; j < n; ++j) {
                final double arg = residuals[j] / (6.0 * medianResidual);
                if (arg >= 1.0) {
                    robustnessWeights[j] = 0.0;
                }
                else {
                    final double w2 = 1.0 - arg * arg;
                    robustnessWeights[j] = w2 * w2;
                }
            }
        }
        return res;
    }
    
    public final double[] smooth(final double[] xval, final double[] yval) throws NonMonotonicSequenceException, DimensionMismatchException, NoDataException, NotFiniteNumberException, NumberIsTooSmallException {
        if (xval.length != yval.length) {
            throw new DimensionMismatchException(xval.length, yval.length);
        }
        final double[] unitWeights = new double[xval.length];
        Arrays.fill(unitWeights, 1.0);
        return this.smooth(xval, yval, unitWeights);
    }
    
    private static void updateBandwidthInterval(final double[] xval, final double[] weights, final int i, final int[] bandwidthInterval) {
        final int left = bandwidthInterval[0];
        final int right = bandwidthInterval[1];
        final int nextRight = nextNonzero(weights, right);
        if (nextRight < xval.length && xval[nextRight] - xval[i] < xval[i] - xval[left]) {
            final int nextLeft = nextNonzero(weights, bandwidthInterval[0]);
            bandwidthInterval[0] = nextLeft;
            bandwidthInterval[1] = nextRight;
        }
    }
    
    private static int nextNonzero(final double[] weights, final int i) {
        int j;
        for (j = i + 1; j < weights.length && weights[j] == 0.0; ++j) {}
        return j;
    }
    
    private static double tricube(final double x) {
        final double absX = FastMath.abs(x);
        if (absX >= 1.0) {
            return 0.0;
        }
        final double tmp = 1.0 - absX * absX * absX;
        return tmp * tmp * tmp;
    }
    
    private static void checkAllFiniteReal(final double[] values) {
        for (int i = 0; i < values.length; ++i) {
            MathUtils.checkFinite(values[i]);
        }
    }
}
