package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import org.apache.commons.math3.optim.PointValuePair;
import java.util.Comparator;
import org.apache.commons.math3.analysis.MultivariateFunction;

public class MultiDirectionalSimplex extends AbstractSimplex
{
    private static final double DEFAULT_KHI = 2.0;
    private static final double DEFAULT_GAMMA = 0.5;
    private final double khi;
    private final double gamma;
    
    public MultiDirectionalSimplex(final int n) {
        this(n, 1.0);
    }
    
    public MultiDirectionalSimplex(final int n, final double sideLength) {
        this(n, sideLength, 2.0, 0.5);
    }
    
    public MultiDirectionalSimplex(final int n, final double khi, final double gamma) {
        this(n, 1.0, khi, gamma);
    }
    
    public MultiDirectionalSimplex(final int n, final double sideLength, final double khi, final double gamma) {
        super(n, sideLength);
        this.khi = khi;
        this.gamma = gamma;
    }
    
    public MultiDirectionalSimplex(final double[] steps) {
        this(steps, 2.0, 0.5);
    }
    
    public MultiDirectionalSimplex(final double[] steps, final double khi, final double gamma) {
        super(steps);
        this.khi = khi;
        this.gamma = gamma;
    }
    
    public MultiDirectionalSimplex(final double[][] referenceSimplex) {
        this(referenceSimplex, 2.0, 0.5);
    }
    
    public MultiDirectionalSimplex(final double[][] referenceSimplex, final double khi, final double gamma) {
        super(referenceSimplex);
        this.khi = khi;
        this.gamma = gamma;
    }
    
    @Override
    public void iterate(final MultivariateFunction evaluationFunction, final Comparator<PointValuePair> comparator) {
        final PointValuePair[] original = this.getPoints();
        final PointValuePair best = original[0];
        final PointValuePair reflected = this.evaluateNewSimplex(evaluationFunction, original, 1.0, comparator);
        if (comparator.compare(reflected, best) < 0) {
            final PointValuePair[] reflectedSimplex = this.getPoints();
            final PointValuePair expanded = this.evaluateNewSimplex(evaluationFunction, original, this.khi, comparator);
            if (comparator.compare(reflected, expanded) <= 0) {
                this.setPoints(reflectedSimplex);
            }
            return;
        }
        this.evaluateNewSimplex(evaluationFunction, original, this.gamma, comparator);
    }
    
    private PointValuePair evaluateNewSimplex(final MultivariateFunction evaluationFunction, final PointValuePair[] original, final double coeff, final Comparator<PointValuePair> comparator) {
        final double[] xSmallest = original[0].getPointRef();
        this.setPoint(0, original[0]);
        final int dim = this.getDimension();
        for (int i = 1; i < this.getSize(); ++i) {
            final double[] xOriginal = original[i].getPointRef();
            final double[] xTransformed = new double[dim];
            for (int j = 0; j < dim; ++j) {
                xTransformed[j] = xSmallest[j] + coeff * (xSmallest[j] - xOriginal[j]);
            }
            this.setPoint(i, new PointValuePair(xTransformed, Double.NaN, false));
        }
        this.evaluate(evaluationFunction, comparator);
        return this.getPoint(0);
    }
}
