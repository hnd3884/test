package org.apache.commons.math3.fitting;

import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.OutOfRangeException;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import java.util.Iterator;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import java.util.Collection;
import org.apache.commons.math3.analysis.function.Gaussian;

public class GaussianCurveFitter extends AbstractCurveFitter
{
    private static final Gaussian.Parametric FUNCTION;
    private final double[] initialGuess;
    private final int maxIter;
    
    private GaussianCurveFitter(final double[] initialGuess, final int maxIter) {
        this.initialGuess = initialGuess;
        this.maxIter = maxIter;
    }
    
    public static GaussianCurveFitter create() {
        return new GaussianCurveFitter(null, Integer.MAX_VALUE);
    }
    
    public GaussianCurveFitter withStartPoint(final double[] newStart) {
        return new GaussianCurveFitter(newStart.clone(), this.maxIter);
    }
    
    public GaussianCurveFitter withMaxIterations(final int newMaxIter) {
        return new GaussianCurveFitter(this.initialGuess, newMaxIter);
    }
    
    @Override
    protected LeastSquaresProblem getProblem(final Collection<WeightedObservedPoint> observations) {
        final int len = observations.size();
        final double[] target = new double[len];
        final double[] weights = new double[len];
        int i = 0;
        for (final WeightedObservedPoint obs : observations) {
            target[i] = obs.getY();
            weights[i] = obs.getWeight();
            ++i;
        }
        final TheoreticalValuesFunction model = new TheoreticalValuesFunction(GaussianCurveFitter.FUNCTION, observations);
        final double[] startPoint = (this.initialGuess != null) ? this.initialGuess : new ParameterGuesser(observations).guess();
        return new LeastSquaresBuilder().maxEvaluations(Integer.MAX_VALUE).maxIterations(this.maxIter).start(startPoint).target(target).weight(new DiagonalMatrix(weights)).model(model.getModelFunction(), model.getModelFunctionJacobian()).build();
    }
    
    static {
        FUNCTION = new Gaussian.Parametric() {
            @Override
            public double value(final double x, final double... p) {
                double v = Double.POSITIVE_INFINITY;
                try {
                    v = super.value(x, p);
                }
                catch (final NotStrictlyPositiveException ex) {}
                return v;
            }
            
            @Override
            public double[] gradient(final double x, final double... p) {
                double[] v = { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
                try {
                    v = super.gradient(x, p);
                }
                catch (final NotStrictlyPositiveException ex) {}
                return v;
            }
        };
    }
    
    public static class ParameterGuesser
    {
        private final double norm;
        private final double mean;
        private final double sigma;
        
        public ParameterGuesser(final Collection<WeightedObservedPoint> observations) {
            if (observations == null) {
                throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
            }
            if (observations.size() < 3) {
                throw new NumberIsTooSmallException(observations.size(), 3, true);
            }
            final List<WeightedObservedPoint> sorted = this.sortObservations(observations);
            final double[] params = this.basicGuess(sorted.toArray(new WeightedObservedPoint[0]));
            this.norm = params[0];
            this.mean = params[1];
            this.sigma = params[2];
        }
        
        public double[] guess() {
            return new double[] { this.norm, this.mean, this.sigma };
        }
        
        private List<WeightedObservedPoint> sortObservations(final Collection<WeightedObservedPoint> unsorted) {
            final List<WeightedObservedPoint> observations = new ArrayList<WeightedObservedPoint>(unsorted);
            final Comparator<WeightedObservedPoint> cmp = new Comparator<WeightedObservedPoint>() {
                public int compare(final WeightedObservedPoint p1, final WeightedObservedPoint p2) {
                    if (p1 == null && p2 == null) {
                        return 0;
                    }
                    if (p1 == null) {
                        return -1;
                    }
                    if (p2 == null) {
                        return 1;
                    }
                    final int cmpX = Double.compare(p1.getX(), p2.getX());
                    if (cmpX < 0) {
                        return -1;
                    }
                    if (cmpX > 0) {
                        return 1;
                    }
                    final int cmpY = Double.compare(p1.getY(), p2.getY());
                    if (cmpY < 0) {
                        return -1;
                    }
                    if (cmpY > 0) {
                        return 1;
                    }
                    final int cmpW = Double.compare(p1.getWeight(), p2.getWeight());
                    if (cmpW < 0) {
                        return -1;
                    }
                    if (cmpW > 0) {
                        return 1;
                    }
                    return 0;
                }
            };
            Collections.sort(observations, cmp);
            return observations;
        }
        
        private double[] basicGuess(final WeightedObservedPoint[] points) {
            final int maxYIdx = this.findMaxY(points);
            final double n = points[maxYIdx].getY();
            final double m = points[maxYIdx].getX();
            double fwhmApprox;
            try {
                final double halfY = n + (m - n) / 2.0;
                final double fwhmX1 = this.interpolateXAtY(points, maxYIdx, -1, halfY);
                final double fwhmX2 = this.interpolateXAtY(points, maxYIdx, 1, halfY);
                fwhmApprox = fwhmX2 - fwhmX1;
            }
            catch (final OutOfRangeException e) {
                fwhmApprox = points[points.length - 1].getX() - points[0].getX();
            }
            final double s = fwhmApprox / (2.0 * FastMath.sqrt(2.0 * FastMath.log(2.0)));
            return new double[] { n, m, s };
        }
        
        private int findMaxY(final WeightedObservedPoint[] points) {
            int maxYIdx = 0;
            for (int i = 1; i < points.length; ++i) {
                if (points[i].getY() > points[maxYIdx].getY()) {
                    maxYIdx = i;
                }
            }
            return maxYIdx;
        }
        
        private double interpolateXAtY(final WeightedObservedPoint[] points, final int startIdx, final int idxStep, final double y) throws OutOfRangeException {
            if (idxStep == 0) {
                throw new ZeroException();
            }
            final WeightedObservedPoint[] twoPoints = this.getInterpolationPointsForY(points, startIdx, idxStep, y);
            final WeightedObservedPoint p1 = twoPoints[0];
            final WeightedObservedPoint p2 = twoPoints[1];
            if (p1.getY() == y) {
                return p1.getX();
            }
            if (p2.getY() == y) {
                return p2.getX();
            }
            return p1.getX() + (y - p1.getY()) * (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
        }
        
        private WeightedObservedPoint[] getInterpolationPointsForY(final WeightedObservedPoint[] points, final int startIdx, final int idxStep, final double y) throws OutOfRangeException {
            if (idxStep == 0) {
                throw new ZeroException();
            }
            int i = startIdx;
            while (true) {
                if (idxStep < 0) {
                    if (i + idxStep < 0) {
                        break;
                    }
                }
                else if (i + idxStep >= points.length) {
                    break;
                }
                final WeightedObservedPoint p1 = points[i];
                final WeightedObservedPoint p2 = points[i + idxStep];
                if (this.isBetween(y, p1.getY(), p2.getY())) {
                    if (idxStep < 0) {
                        return new WeightedObservedPoint[] { p2, p1 };
                    }
                    return new WeightedObservedPoint[] { p1, p2 };
                }
                else {
                    i += idxStep;
                }
            }
            throw new OutOfRangeException(y, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
        
        private boolean isBetween(final double value, final double boundary1, final double boundary2) {
            return (value >= boundary1 && value <= boundary2) || (value >= boundary2 && value <= boundary1);
        }
    }
}
