package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class GaussIntegrator
{
    private final double[] points;
    private final double[] weights;
    
    public GaussIntegrator(final double[] points, final double[] weights) throws NonMonotonicSequenceException, DimensionMismatchException {
        if (points.length != weights.length) {
            throw new DimensionMismatchException(points.length, weights.length);
        }
        MathArrays.checkOrder(points, MathArrays.OrderDirection.INCREASING, true, true);
        this.points = points.clone();
        this.weights = weights.clone();
    }
    
    public GaussIntegrator(final Pair<double[], double[]> pointsAndWeights) throws NonMonotonicSequenceException {
        this(pointsAndWeights.getFirst(), pointsAndWeights.getSecond());
    }
    
    public double integrate(final UnivariateFunction f) {
        double s = 0.0;
        double c = 0.0;
        for (int i = 0; i < this.points.length; ++i) {
            final double x = this.points[i];
            final double w = this.weights[i];
            final double y = w * f.value(x) - c;
            final double t = s + y;
            c = t - s - y;
            s = t;
        }
        return s;
    }
    
    public int getNumberOfPoints() {
        return this.points.length;
    }
    
    public double getPoint(final int index) {
        return this.points[index];
    }
    
    public double getWeight(final int index) {
        return this.weights[index];
    }
}
