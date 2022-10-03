package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.util.FastMath;

public class LaguerreSolver extends AbstractPolynomialSolver
{
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    private final ComplexSolver complexSolver;
    
    public LaguerreSolver() {
        this(1.0E-6);
    }
    
    public LaguerreSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
        this.complexSolver = new ComplexSolver();
    }
    
    public LaguerreSolver(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy);
        this.complexSolver = new ComplexSolver();
    }
    
    public LaguerreSolver(final double relativeAccuracy, final double absoluteAccuracy, final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
        this.complexSolver = new ComplexSolver();
    }
    
    public double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        final double min = this.getMin();
        final double max = this.getMax();
        final double initial = this.getStartValue();
        final double functionValueAccuracy = this.getFunctionValueAccuracy();
        this.verifySequence(min, initial, max);
        final double yInitial = this.computeObjectiveValue(initial);
        if (FastMath.abs(yInitial) <= functionValueAccuracy) {
            return initial;
        }
        final double yMin = this.computeObjectiveValue(min);
        if (FastMath.abs(yMin) <= functionValueAccuracy) {
            return min;
        }
        if (yInitial * yMin < 0.0) {
            return this.laguerre(min, initial, yMin, yInitial);
        }
        final double yMax = this.computeObjectiveValue(max);
        if (FastMath.abs(yMax) <= functionValueAccuracy) {
            return max;
        }
        if (yInitial * yMax < 0.0) {
            return this.laguerre(initial, max, yInitial, yMax);
        }
        throw new NoBracketingException(min, max, yMin, yMax);
    }
    
    @Deprecated
    public double laguerre(final double lo, final double hi, final double fLo, final double fHi) {
        final Complex[] c = ComplexUtils.convertToComplex(this.getCoefficients());
        final Complex initial = new Complex(0.5 * (lo + hi), 0.0);
        final Complex z = this.complexSolver.solve(c, initial);
        if (this.complexSolver.isRoot(lo, hi, z)) {
            return z.getReal();
        }
        double r = Double.NaN;
        final Complex[] root = this.complexSolver.solveAll(c, initial);
        for (int i = 0; i < root.length; ++i) {
            if (this.complexSolver.isRoot(lo, hi, root[i])) {
                r = root[i].getReal();
                break;
            }
        }
        return r;
    }
    
    public Complex[] solveAllComplex(final double[] coefficients, final double initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
        return this.solveAllComplex(coefficients, initial, Integer.MAX_VALUE);
    }
    
    public Complex[] solveAllComplex(final double[] coefficients, final double initial, final int maxEval) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
        this.setup(maxEval, new PolynomialFunction(coefficients), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, initial);
        return this.complexSolver.solveAll(ComplexUtils.convertToComplex(coefficients), new Complex(initial, 0.0));
    }
    
    public Complex solveComplex(final double[] coefficients, final double initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
        return this.solveComplex(coefficients, initial, Integer.MAX_VALUE);
    }
    
    public Complex solveComplex(final double[] coefficients, final double initial, final int maxEval) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
        this.setup(maxEval, new PolynomialFunction(coefficients), Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, initial);
        return this.complexSolver.solve(ComplexUtils.convertToComplex(coefficients), new Complex(initial, 0.0));
    }
    
    private class ComplexSolver
    {
        public boolean isRoot(final double min, final double max, final Complex z) {
            if (LaguerreSolver.this.isSequence(min, z.getReal(), max)) {
                final double tolerance = FastMath.max(LaguerreSolver.this.getRelativeAccuracy() * z.abs(), LaguerreSolver.this.getAbsoluteAccuracy());
                return FastMath.abs(z.getImaginary()) <= tolerance || z.abs() <= LaguerreSolver.this.getFunctionValueAccuracy();
            }
            return false;
        }
        
        public Complex[] solveAll(final Complex[] coefficients, final Complex initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
            if (coefficients == null) {
                throw new NullArgumentException();
            }
            final int n = coefficients.length - 1;
            if (n == 0) {
                throw new NoDataException(LocalizedFormats.POLYNOMIAL);
            }
            final Complex[] c = new Complex[n + 1];
            for (int i = 0; i <= n; ++i) {
                c[i] = coefficients[i];
            }
            final Complex[] root = new Complex[n];
            for (int j = 0; j < n; ++j) {
                final Complex[] subarray = new Complex[n - j + 1];
                System.arraycopy(c, 0, subarray, 0, subarray.length);
                root[j] = this.solve(subarray, initial);
                Complex newc = c[n - j];
                Complex oldc = null;
                for (int k = n - j - 1; k >= 0; --k) {
                    oldc = c[k];
                    c[k] = newc;
                    newc = oldc.add(newc.multiply(root[j]));
                }
            }
            return root;
        }
        
        public Complex solve(final Complex[] coefficients, final Complex initial) throws NullArgumentException, NoDataException, TooManyEvaluationsException {
            if (coefficients == null) {
                throw new NullArgumentException();
            }
            final int n = coefficients.length - 1;
            if (n == 0) {
                throw new NoDataException(LocalizedFormats.POLYNOMIAL);
            }
            final double absoluteAccuracy = LaguerreSolver.this.getAbsoluteAccuracy();
            final double relativeAccuracy = LaguerreSolver.this.getRelativeAccuracy();
            final double functionValueAccuracy = LaguerreSolver.this.getFunctionValueAccuracy();
            final Complex nC = new Complex(n, 0.0);
            final Complex n1C = new Complex(n - 1, 0.0);
            Complex z = initial;
            Complex oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
            while (true) {
                Complex pv = coefficients[n];
                Complex dv = Complex.ZERO;
                Complex d2v = Complex.ZERO;
                for (int j = n - 1; j >= 0; --j) {
                    d2v = dv.add(z.multiply(d2v));
                    dv = pv.add(z.multiply(dv));
                    pv = coefficients[j].add(z.multiply(pv));
                }
                d2v = d2v.multiply(new Complex(2.0, 0.0));
                final double tolerance = FastMath.max(relativeAccuracy * z.abs(), absoluteAccuracy);
                if (z.subtract(oldz).abs() <= tolerance) {
                    return z;
                }
                if (pv.abs() <= functionValueAccuracy) {
                    return z;
                }
                final Complex G = dv.divide(pv);
                final Complex G2 = G.multiply(G);
                final Complex H = G2.subtract(d2v.divide(pv));
                final Complex delta = n1C.multiply(nC.multiply(H).subtract(G2));
                final Complex deltaSqrt = delta.sqrt();
                final Complex dplus = G.add(deltaSqrt);
                final Complex dminus = G.subtract(deltaSqrt);
                final Complex denominator = (dplus.abs() > dminus.abs()) ? dplus : dminus;
                if (denominator.equals(new Complex(0.0, 0.0))) {
                    z = z.add(new Complex(absoluteAccuracy, absoluteAccuracy));
                    oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
                }
                else {
                    oldz = z;
                    z = z.subtract(nC.divide(denominator));
                }
                LaguerreSolver.this.incrementEvaluationCount();
            }
        }
    }
}
