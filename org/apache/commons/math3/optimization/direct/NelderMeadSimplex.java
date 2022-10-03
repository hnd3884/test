package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.optimization.PointValuePair;
import java.util.Comparator;
import org.apache.commons.math3.analysis.MultivariateFunction;

@Deprecated
public class NelderMeadSimplex extends AbstractSimplex
{
    private static final double DEFAULT_RHO = 1.0;
    private static final double DEFAULT_KHI = 2.0;
    private static final double DEFAULT_GAMMA = 0.5;
    private static final double DEFAULT_SIGMA = 0.5;
    private final double rho;
    private final double khi;
    private final double gamma;
    private final double sigma;
    
    public NelderMeadSimplex(final int n) {
        this(n, 1.0);
    }
    
    public NelderMeadSimplex(final int n, final double sideLength) {
        this(n, sideLength, 1.0, 2.0, 0.5, 0.5);
    }
    
    public NelderMeadSimplex(final int n, final double sideLength, final double rho, final double khi, final double gamma, final double sigma) {
        super(n, sideLength);
        this.rho = rho;
        this.khi = khi;
        this.gamma = gamma;
        this.sigma = sigma;
    }
    
    public NelderMeadSimplex(final int n, final double rho, final double khi, final double gamma, final double sigma) {
        this(n, 1.0, rho, khi, gamma, sigma);
    }
    
    public NelderMeadSimplex(final double[] steps) {
        this(steps, 1.0, 2.0, 0.5, 0.5);
    }
    
    public NelderMeadSimplex(final double[] steps, final double rho, final double khi, final double gamma, final double sigma) {
        super(steps);
        this.rho = rho;
        this.khi = khi;
        this.gamma = gamma;
        this.sigma = sigma;
    }
    
    public NelderMeadSimplex(final double[][] referenceSimplex) {
        this(referenceSimplex, 1.0, 2.0, 0.5, 0.5);
    }
    
    public NelderMeadSimplex(final double[][] referenceSimplex, final double rho, final double khi, final double gamma, final double sigma) {
        super(referenceSimplex);
        this.rho = rho;
        this.khi = khi;
        this.gamma = gamma;
        this.sigma = sigma;
    }
    
    @Override
    public void iterate(final MultivariateFunction evaluationFunction, final Comparator<PointValuePair> comparator) {
        final int n = this.getDimension();
        final PointValuePair best = this.getPoint(0);
        final PointValuePair secondBest = this.getPoint(n - 1);
        final PointValuePair worst = this.getPoint(n);
        final double[] xWorst = worst.getPointRef();
        final double[] centroid = new double[n];
        for (int i = 0; i < n; ++i) {
            final double[] x = this.getPoint(i).getPointRef();
            for (int j = 0; j < n; ++j) {
                final double[] array = centroid;
                final int n2 = j;
                array[n2] += x[j];
            }
        }
        final double scaling = 1.0 / n;
        for (int j = 0; j < n; ++j) {
            final double[] array2 = centroid;
            final int n3 = j;
            array2[n3] *= scaling;
        }
        final double[] xR = new double[n];
        for (int k = 0; k < n; ++k) {
            xR[k] = centroid[k] + this.rho * (centroid[k] - xWorst[k]);
        }
        final PointValuePair reflected = new PointValuePair(xR, evaluationFunction.value(xR), false);
        if (comparator.compare(best, reflected) <= 0 && comparator.compare(reflected, secondBest) < 0) {
            this.replaceWorstPoint(reflected, comparator);
        }
        else if (comparator.compare(reflected, best) < 0) {
            final double[] xE = new double[n];
            for (int l = 0; l < n; ++l) {
                xE[l] = centroid[l] + this.khi * (xR[l] - centroid[l]);
            }
            final PointValuePair expanded = new PointValuePair(xE, evaluationFunction.value(xE), false);
            if (comparator.compare(expanded, reflected) < 0) {
                this.replaceWorstPoint(expanded, comparator);
            }
            else {
                this.replaceWorstPoint(reflected, comparator);
            }
        }
        else {
            if (comparator.compare(reflected, worst) < 0) {
                final double[] xC = new double[n];
                for (int l = 0; l < n; ++l) {
                    xC[l] = centroid[l] + this.gamma * (xR[l] - centroid[l]);
                }
                final PointValuePair outContracted = new PointValuePair(xC, evaluationFunction.value(xC), false);
                if (comparator.compare(outContracted, reflected) <= 0) {
                    this.replaceWorstPoint(outContracted, comparator);
                    return;
                }
            }
            else {
                final double[] xC = new double[n];
                for (int l = 0; l < n; ++l) {
                    xC[l] = centroid[l] - this.gamma * (centroid[l] - xWorst[l]);
                }
                final PointValuePair inContracted = new PointValuePair(xC, evaluationFunction.value(xC), false);
                if (comparator.compare(inContracted, worst) < 0) {
                    this.replaceWorstPoint(inContracted, comparator);
                    return;
                }
            }
            final double[] xSmallest = this.getPoint(0).getPointRef();
            for (int m = 1; m <= n; ++m) {
                final double[] x2 = this.getPoint(m).getPoint();
                for (int j2 = 0; j2 < n; ++j2) {
                    x2[j2] = xSmallest[j2] + this.sigma * (x2[j2] - xSmallest[j2]);
                }
                this.setPoint(m, new PointValuePair(x2, Double.NaN, false));
            }
            this.evaluate(evaluationFunction, comparator);
        }
    }
}
