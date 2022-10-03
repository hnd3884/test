package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Pair;

public class HermiteRuleFactory extends BaseRuleFactory<Double>
{
    private static final double SQRT_PI = 1.772453850905516;
    private static final double H0 = 0.7511255444649425;
    private static final double H1 = 1.0622519320271968;
    
    @Override
    protected Pair<Double[], Double[]> computeRule(final int numberOfPoints) throws DimensionMismatchException {
        if (numberOfPoints == 1) {
            return new Pair<Double[], Double[]>(new Double[] { 0.0 }, new Double[] { 1.772453850905516 });
        }
        final int lastNumPoints = numberOfPoints - 1;
        final Double[] previousPoints = this.getRuleInternal(lastNumPoints).getFirst();
        final Double[] points = new Double[numberOfPoints];
        final Double[] weights = new Double[numberOfPoints];
        final double sqrtTwoTimesLastNumPoints = FastMath.sqrt(2 * lastNumPoints);
        final double sqrtTwoTimesNumPoints = FastMath.sqrt(2 * numberOfPoints);
        final int iMax = numberOfPoints / 2;
        for (int i = 0; i < iMax; ++i) {
            double a = (i == 0) ? (-sqrtTwoTimesLastNumPoints) : previousPoints[i - 1];
            double b = (iMax == 1) ? -0.5 : previousPoints[i];
            double hma = 0.7511255444649425;
            double ha = 1.0622519320271968 * a;
            double hmb = 0.7511255444649425;
            double hb = 1.0622519320271968 * b;
            for (int j = 1; j < numberOfPoints; ++j) {
                final double jp1 = j + 1;
                final double s = FastMath.sqrt(2.0 / jp1);
                final double sm = FastMath.sqrt(j / jp1);
                final double hpa = s * a * ha - sm * hma;
                final double hpb = s * b * hb - sm * hmb;
                hma = ha;
                ha = hpa;
                hmb = hb;
                hb = hpb;
            }
            double c = 0.5 * (a + b);
            double hmc = 0.7511255444649425;
            double hc = 1.0622519320271968 * c;
            boolean done = false;
            while (!done) {
                done = (b - a <= Math.ulp(c));
                hmc = 0.7511255444649425;
                hc = 1.0622519320271968 * c;
                for (int k = 1; k < numberOfPoints; ++k) {
                    final double jp2 = k + 1;
                    final double s2 = FastMath.sqrt(2.0 / jp2);
                    final double sm2 = FastMath.sqrt(k / jp2);
                    final double hpc = s2 * c * hc - sm2 * hmc;
                    hmc = hc;
                    hc = hpc;
                }
                if (!done) {
                    if (ha * hc < 0.0) {
                        b = c;
                        hmb = hmc;
                        hb = hc;
                    }
                    else {
                        a = c;
                        hma = hmc;
                        ha = hc;
                    }
                    c = 0.5 * (a + b);
                }
            }
            final double d = sqrtTwoTimesNumPoints * hmc;
            final double w = 2.0 / (d * d);
            points[i] = c;
            weights[i] = w;
            final int idx = lastNumPoints - i;
            points[idx] = -c;
            weights[idx] = w;
        }
        if (numberOfPoints % 2 != 0) {
            double hm = 0.7511255444649425;
            for (int l = 1; l < numberOfPoints; l += 2) {
                final double jp3 = l + 1;
                hm *= -FastMath.sqrt(l / jp3);
            }
            final double d2 = sqrtTwoTimesNumPoints * hm;
            final double w2 = 2.0 / (d2 * d2);
            points[iMax] = 0.0;
            weights[iMax] = w2;
        }
        return new Pair<Double[], Double[]>(points, weights);
    }
}
