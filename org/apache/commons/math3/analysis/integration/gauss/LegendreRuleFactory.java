package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.Pair;

public class LegendreRuleFactory extends BaseRuleFactory<Double>
{
    @Override
    protected Pair<Double[], Double[]> computeRule(final int numberOfPoints) throws DimensionMismatchException {
        if (numberOfPoints == 1) {
            return new Pair<Double[], Double[]>(new Double[] { 0.0 }, new Double[] { 2.0 });
        }
        final Double[] previousPoints = this.getRuleInternal(numberOfPoints - 1).getFirst();
        final Double[] points = new Double[numberOfPoints];
        final Double[] weights = new Double[numberOfPoints];
        final int iMax = numberOfPoints / 2;
        for (int i = 0; i < iMax; ++i) {
            double a = (i == 0) ? -1.0 : previousPoints[i - 1];
            double b = (iMax == 1) ? 1.0 : previousPoints[i];
            double pma = 1.0;
            double pa = a;
            double pmb = 1.0;
            double pb = b;
            for (int j = 1; j < numberOfPoints; ++j) {
                final int two_j_p_1 = 2 * j + 1;
                final int j_p_1 = j + 1;
                final double ppa = (two_j_p_1 * a * pa - j * pma) / j_p_1;
                final double ppb = (two_j_p_1 * b * pb - j * pmb) / j_p_1;
                pma = pa;
                pa = ppa;
                pmb = pb;
                pb = ppb;
            }
            double c = 0.5 * (a + b);
            double pmc = 1.0;
            double pc = c;
            boolean done = false;
            while (!done) {
                done = (b - a <= Math.ulp(c));
                pmc = 1.0;
                pc = c;
                for (int k = 1; k < numberOfPoints; ++k) {
                    final double ppc = ((2 * k + 1) * c * pc - k * pmc) / (k + 1);
                    pmc = pc;
                    pc = ppc;
                }
                if (!done) {
                    if (pa * pc <= 0.0) {
                        b = c;
                        pmb = pmc;
                        pb = pc;
                    }
                    else {
                        a = c;
                        pma = pmc;
                        pa = pc;
                    }
                    c = 0.5 * (a + b);
                }
            }
            final double d = numberOfPoints * (pmc - c * pc);
            final double w = 2.0 * (1.0 - c * c) / (d * d);
            points[i] = c;
            weights[i] = w;
            final int idx = numberOfPoints - i - 1;
            points[idx] = -c;
            weights[idx] = w;
        }
        if (numberOfPoints % 2 != 0) {
            double pmc2 = 1.0;
            for (int l = 1; l < numberOfPoints; l += 2) {
                pmc2 = -l * pmc2 / (l + 1);
            }
            final double d2 = numberOfPoints * pmc2;
            final double w2 = 2.0 / (d2 * d2);
            points[iMax] = 0.0;
            weights[iMax] = w2;
        }
        return new Pair<Double[], Double[]>(points, weights);
    }
}
