package org.apache.commons.math3.fitting;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer;
import org.apache.commons.math3.analysis.function.HarmonicOscillator;

@Deprecated
public class HarmonicFitter extends CurveFitter<HarmonicOscillator.Parametric>
{
    public HarmonicFitter(final MultivariateVectorOptimizer optimizer) {
        super(optimizer);
    }
    
    public double[] fit(final double[] initialGuess) {
        return this.fit(new HarmonicOscillator.Parametric(), initialGuess);
    }
    
    public double[] fit() {
        return this.fit(new ParameterGuesser(this.getObservations()).guess());
    }
    
    public static class ParameterGuesser
    {
        private final double a;
        private final double omega;
        private final double phi;
        
        public ParameterGuesser(final WeightedObservedPoint[] observations) {
            if (observations.length < 4) {
                throw new NumberIsTooSmallException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, observations.length, 4, true);
            }
            final WeightedObservedPoint[] sorted = this.sortObservations(observations);
            final double[] aOmega = this.guessAOmega(sorted);
            this.a = aOmega[0];
            this.omega = aOmega[1];
            this.phi = this.guessPhi(sorted);
        }
        
        public double[] guess() {
            return new double[] { this.a, this.omega, this.phi };
        }
        
        private WeightedObservedPoint[] sortObservations(final WeightedObservedPoint[] unsorted) {
            final WeightedObservedPoint[] observations = unsorted.clone();
            WeightedObservedPoint curr = observations[0];
            for (int j = 1; j < observations.length; ++j) {
                final WeightedObservedPoint prec = curr;
                curr = observations[j];
                if (curr.getX() < prec.getX()) {
                    int i = j - 1;
                    for (WeightedObservedPoint mI = observations[i]; i >= 0 && curr.getX() < mI.getX(); mI = observations[i]) {
                        observations[i + 1] = mI;
                        if (i-- != 0) {}
                    }
                    observations[i + 1] = curr;
                    curr = observations[j];
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
