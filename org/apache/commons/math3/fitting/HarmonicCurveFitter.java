package org.apache.commons.math3.fitting;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.ZeroException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.Iterator;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import java.util.Collection;
import org.apache.commons.math3.analysis.function.HarmonicOscillator;

public class HarmonicCurveFitter extends AbstractCurveFitter
{
    private static final HarmonicOscillator.Parametric FUNCTION;
    private final double[] initialGuess;
    private final int maxIter;
    
    private HarmonicCurveFitter(final double[] initialGuess, final int maxIter) {
        this.initialGuess = initialGuess;
        this.maxIter = maxIter;
    }
    
    public static HarmonicCurveFitter create() {
        return new HarmonicCurveFitter(null, Integer.MAX_VALUE);
    }
    
    public HarmonicCurveFitter withStartPoint(final double[] newStart) {
        return new HarmonicCurveFitter(newStart.clone(), this.maxIter);
    }
    
    public HarmonicCurveFitter withMaxIterations(final int newMaxIter) {
        return new HarmonicCurveFitter(this.initialGuess, newMaxIter);
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
        final TheoreticalValuesFunction model = new TheoreticalValuesFunction(HarmonicCurveFitter.FUNCTION, observations);
        final double[] startPoint = (this.initialGuess != null) ? this.initialGuess : new ParameterGuesser(observations).guess();
        return new LeastSquaresBuilder().maxEvaluations(Integer.MAX_VALUE).maxIterations(this.maxIter).start(startPoint).target(target).weight(new DiagonalMatrix(weights)).model(model.getModelFunction(), model.getModelFunctionJacobian()).build();
    }
    
    static {
        FUNCTION = new HarmonicOscillator.Parametric();
    }
    
    public static class ParameterGuesser
    {
        private final double a;
        private final double omega;
        private final double phi;
        
        public ParameterGuesser(final Collection<WeightedObservedPoint> observations) {
            if (observations.size() < 4) {
                throw new NumberIsTooSmallException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, observations.size(), 4, true);
            }
            final WeightedObservedPoint[] sorted = this.sortObservations(observations).toArray(new WeightedObservedPoint[0]);
            final double[] aOmega = this.guessAOmega(sorted);
            this.a = aOmega[0];
            this.omega = aOmega[1];
            this.phi = this.guessPhi(sorted);
        }
        
        public double[] guess() {
            return new double[] { this.a, this.omega, this.phi };
        }
        
        private List<WeightedObservedPoint> sortObservations(final Collection<WeightedObservedPoint> unsorted) {
            final List<WeightedObservedPoint> observations = new ArrayList<WeightedObservedPoint>(unsorted);
            WeightedObservedPoint curr = observations.get(0);
            for (int len = observations.size(), j = 1; j < len; ++j) {
                final WeightedObservedPoint prec = curr;
                curr = observations.get(j);
                if (curr.getX() < prec.getX()) {
                    int i = j - 1;
                    for (WeightedObservedPoint mI = observations.get(i); i >= 0 && curr.getX() < mI.getX(); mI = observations.get(i)) {
                        observations.set(i + 1, mI);
                        if (i-- != 0) {}
                    }
                    observations.set(i + 1, curr);
                    curr = observations.get(j);
                }
            }
            return observations;
        }
        
        private double[] guessAOmega(final WeightedObservedPoint[] observations) {
            final double[] aOmega = new double[2];
            double sx2 = 0.0;
            double sy2 = 0.0;
            double sxy = 0.0;
            double sxz = 0.0;
            double syz = 0.0;
            double currentX = observations[0].getX();
            double currentY = observations[0].getY();
            double f2Integral = 0.0;
            double fPrime2Integral = 0.0;
            final double startX = currentX;
            for (int i = 1; i < observations.length; ++i) {
                final double previousX = currentX;
                final double previousY = currentY;
                currentX = observations[i].getX();
                currentY = observations[i].getY();
                final double dx = currentX - previousX;
                final double dy = currentY - previousY;
                final double f2StepIntegral = dx * (previousY * previousY + previousY * currentY + currentY * currentY) / 3.0;
                final double fPrime2StepIntegral = dy * dy / dx;
                final double x = currentX - startX;
                f2Integral += f2StepIntegral;
                fPrime2Integral += fPrime2StepIntegral;
                sx2 += x * x;
                sy2 += f2Integral * f2Integral;
                sxy += x * f2Integral;
                sxz += x * fPrime2Integral;
                syz += f2Integral * fPrime2Integral;
            }
            final double c1 = sy2 * sxz - sxy * syz;
            final double c2 = sxy * sxz - sx2 * syz;
            final double c3 = sx2 * sy2 - sxy * sxy;
            if (c1 / c2 < 0.0 || c2 / c3 < 0.0) {
                final int last = observations.length - 1;
                final double xRange = observations[last].getX() - observations[0].getX();
                if (xRange == 0.0) {
                    throw new ZeroException();
                }
                aOmega[1] = 6.283185307179586 / xRange;
                double yMin = Double.POSITIVE_INFINITY;
                double yMax = Double.NEGATIVE_INFINITY;
                for (int j = 1; j < observations.length; ++j) {
                    final double y = observations[j].getY();
                    if (y < yMin) {
                        yMin = y;
                    }
                    if (y > yMax) {
                        yMax = y;
                    }
                }
                aOmega[0] = 0.5 * (yMax - yMin);
            }
            else {
                if (c2 == 0.0) {
                    throw new MathIllegalStateException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
                }
                aOmega[0] = FastMath.sqrt(c1 / c2);
                aOmega[1] = FastMath.sqrt(c2 / c3);
            }
            return aOmega;
        }
        
        private double guessPhi(final WeightedObservedPoint[] observations) {
            double fcMean = 0.0;
            double fsMean = 0.0;
            double currentX = observations[0].getX();
            double currentY = observations[0].getY();
            for (int i = 1; i < observations.length; ++i) {
                final double previousX = currentX;
                final double previousY = currentY;
                currentX = observations[i].getX();
                currentY = observations[i].getY();
                final double currentYPrime = (currentY - previousY) / (currentX - previousX);
                final double omegaX = this.omega * currentX;
                final double cosine = FastMath.cos(omegaX);
                final double sine = FastMath.sin(omegaX);
                fcMean += this.omega * currentY * cosine - currentYPrime * sine;
                fsMean += this.omega * currentY * sine + currentYPrime * cosine;
            }
            return FastMath.atan2(-fsMean, fcMean);
        }
    }
}
