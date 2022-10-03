package org.apache.commons.math3.optimization.fitting;

import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.OutOfRangeException;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.optimization.DifferentiableMultivariateVectorOptimizer;
import org.apache.commons.math3.analysis.function.Gaussian;

@Deprecated
public class GaussianFitter extends CurveFitter<Gaussian.Parametric>
{
    public GaussianFitter(final DifferentiableMultivariateVectorOptimizer optimizer) {
        super(optimizer);
    }
    
    public double[] fit(final double[] initialGuess) {
        final Gaussian.Parametric f = new Gaussian.Parametric() {
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
        return this.fit(f, initialGuess);
    }
    
    public double[] fit() {
        final double[] guess = new ParameterGuesser(this.getObservations()).guess();
        return this.fit(guess);
    }
    
    public static class ParameterGuesser
    {
        private final double norm;
        private final double mean;
        private final double sigma;
        
        public ParameterGuesser(final WeightedObservedPoint[] observations) {
            if (observations == null) {
                throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY, new Object[0]);
            }
            if (observations.length < 3) {
                throw new NumberIsTooSmallException(observations.length, 3, true);
            }
            final WeightedObservedPoint[] sorted = this.sortObservations(observations);
            final double[] params = this.basicGuess(sorted);
            this.norm = params[0];
            this.mean = params[1];
            this.sigma = params[2];
        }
        
        public double[] guess() {
            return new double[] { this.norm, this.mean, this.sigma };
        }
        
        private WeightedObservedPoint[] sortObservations(final WeightedObservedPoint[] unsorted) {
            final WeightedObservedPoint[] observations = unsorted.clone();
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
            Arrays.sort(observations, cmp);
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
