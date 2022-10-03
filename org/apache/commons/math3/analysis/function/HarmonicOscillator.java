package org.apache.commons.math3.analysis.function;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.ParametricUnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

public class HarmonicOscillator implements UnivariateDifferentiableFunction, DifferentiableUnivariateFunction
{
    private final double amplitude;
    private final double omega;
    private final double phase;
    
    public HarmonicOscillator(final double amplitude, final double omega, final double phase) {
        this.amplitude = amplitude;
        this.omega = omega;
        this.phase = phase;
    }
    
    public double value(final double x) {
        return value(this.omega * x + this.phase, this.amplitude);
    }
    
    @Deprecated
    public UnivariateFunction derivative() {
        return FunctionUtils.toDifferentiableUnivariateFunction(this).derivative();
    }
    
    private static double value(final double xTimesOmegaPlusPhase, final double amplitude) {
        return amplitude * FastMath.cos(xTimesOmegaPlusPhase);
    }
    
    public DerivativeStructure value(final DerivativeStructure t) throws DimensionMismatchException {
        final double x = t.getValue();
        final double[] f = new double[t.getOrder() + 1];
        final double alpha = this.omega * x + this.phase;
        f[0] = this.amplitude * FastMath.cos(alpha);
        if (f.length > 1) {
            f[1] = -this.amplitude * this.omega * FastMath.sin(alpha);
            final double mo2 = -this.omega * this.omega;
            for (int i = 2; i < f.length; ++i) {
                f[i] = mo2 * f[i - 2];
            }
        }
        return t.compose(f);
    }
    
    public static class Parametric implements ParametricUnivariateFunction
    {
        public double value(final double x, final double... param) throws NullArgumentException, DimensionMismatchException {
            this.validateParameters(param);
            return value(x * param[1] + param[2], param[0]);
        }
        
        public double[] gradient(final double x, final double... param) throws NullArgumentException, DimensionMismatchException {
            this.validateParameters(param);
            final double amplitude = param[0];
            final double omega = param[1];
            final double phase = param[2];
            final double xTimesOmegaPlusPhase = omega * x + phase;
            final double a = value(xTimesOmegaPlusPhase, 1.0);
            final double p = -amplitude * FastMath.sin(xTimesOmegaPlusPhase);
            final double w = p * x;
            return new double[] { a, w, p };
        }
        
        private void validateParameters(final double[] param) throws NullArgumentException, DimensionMismatchException {
            if (param == null) {
                throw new NullArgumentException();
            }
            if (param.length != 3) {
                throw new DimensionMismatchException(param.length, 3);
            }
        }
    }
}
