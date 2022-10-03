package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;

public class SymmetricGaussIntegrator extends GaussIntegrator
{
    public SymmetricGaussIntegrator(final double[] points, final double[] weights) throws NonMonotonicSequenceException, DimensionMismatchException {
        super(points, weights);
    }
    
    public SymmetricGaussIntegrator(final Pair<double[], double[]> pointsAndWeights) throws NonMonotonicSequenceException {
        this(pointsAndWeights.getFirst(), pointsAndWeights.getSecond());
    }
    
    @Override
    public double integrate(final UnivariateFunction f) {
        final int ruleLength = this.getNumberOfPoints();
        if (ruleLength == 1) {
            return this.getWeight(0) * f.value(0.0);
        }
        final int iMax = ruleLength / 2;
        double s = 0.0;
        double c = 0.0;
        for (int i = 0; i < iMax; ++i) {
            final double p = this.getPoint(i);
            final double w = this.getWeight(i);
            final double f2 = f.value(p);
            final double f3 = f.value(-p);
            final double y = w * (f2 + f3) - c;
            final double t = s + y;
            c = t - s - y;
            s = t;
        }
        if (ruleLength % 2 != 0) {
            final double w2 = this.getWeight(iMax);
            final double y2 = w2 * f.value(0.0) - c;
            final double t2 = s += y2;
        }
        return s;
    }
}
