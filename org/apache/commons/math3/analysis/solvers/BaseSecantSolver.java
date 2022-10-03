package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.analysis.UnivariateFunction;

public abstract class BaseSecantSolver extends AbstractUnivariateSolver implements BracketedUnivariateSolver<UnivariateFunction>
{
    protected static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    private AllowedSolution allowed;
    private final Method method;
    
    protected BaseSecantSolver(final double absoluteAccuracy, final Method method) {
        super(absoluteAccuracy);
        this.allowed = AllowedSolution.ANY_SIDE;
        this.method = method;
    }
    
    protected BaseSecantSolver(final double relativeAccuracy, final double absoluteAccuracy, final Method method) {
        super(relativeAccuracy, absoluteAccuracy);
        this.allowed = AllowedSolution.ANY_SIDE;
        this.method = method;
    }
    
    protected BaseSecantSolver(final double relativeAccuracy, final double absoluteAccuracy, final double functionValueAccuracy, final Method method) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
        this.allowed = AllowedSolution.ANY_SIDE;
        this.method = method;
    }
    
    public double solve(final int maxEval, final UnivariateFunction f, final double min, final double max, final AllowedSolution allowedSolution) {
        return this.solve(maxEval, f, min, max, min + 0.5 * (max - min), allowedSolution);
    }
    
    public double solve(final int maxEval, final UnivariateFunction f, final double min, final double max, final double startValue, final AllowedSolution allowedSolution) {
        this.allowed = allowedSolution;
        return super.solve(maxEval, f, min, max, startValue);
    }
    
    @Override
    public double solve(final int maxEval, final UnivariateFunction f, final double min, final double max, final double startValue) {
        return this.solve(maxEval, f, min, max, startValue, AllowedSolution.ANY_SIDE);
    }
    
    @Override
    protected final double doSolve() throws ConvergenceException {
        double x0 = this.getMin();
        double x2 = this.getMax();
        double f0 = this.computeObjectiveValue(x0);
        double f2 = this.computeObjectiveValue(x2);
        if (f0 == 0.0) {
            return x0;
        }
        if (f2 == 0.0) {
            return x2;
        }
        this.verifyBracketing(x0, x2);
        final double ftol = this.getFunctionValueAccuracy();
        final double atol = this.getAbsoluteAccuracy();
        final double rtol = this.getRelativeAccuracy();
        boolean inverted = false;
        while (true) {
            final double x3 = x2 - f2 * (x2 - x0) / (f2 - f0);
            final double fx = this.computeObjectiveValue(x3);
            if (fx == 0.0) {
                return x3;
            }
            if (f2 * fx < 0.0) {
                x0 = x2;
                f0 = f2;
                inverted = !inverted;
            }
            else {
                switch (this.method) {
                    case ILLINOIS: {
                        f0 *= 0.5;
                        break;
                    }
                    case PEGASUS: {
                        f0 *= f2 / (f2 + fx);
                        break;
                    }
                    case REGULA_FALSI: {
                        if (x3 == x2) {
                            throw new ConvergenceException();
                        }
                        break;
                    }
                    default: {
                        throw new MathInternalError();
                    }
                }
            }
            x2 = x3;
            f2 = fx;
            if (FastMath.abs(f2) <= ftol) {
                switch (this.allowed) {
                    case ANY_SIDE: {
                        return x2;
                    }
                    case LEFT_SIDE: {
                        if (inverted) {
                            return x2;
                        }
                        break;
                    }
                    case RIGHT_SIDE: {
                        if (!inverted) {
                            return x2;
                        }
                        break;
                    }
                    case BELOW_SIDE: {
                        if (f2 <= 0.0) {
                            return x2;
                        }
                        break;
                    }
                    case ABOVE_SIDE: {
                        if (f2 >= 0.0) {
                            return x2;
                        }
                        break;
                    }
                    default: {
                        throw new MathInternalError();
                    }
                }
            }
            if (FastMath.abs(x2 - x0) >= FastMath.max(rtol * FastMath.abs(x2), atol)) {
                continue;
            }
            switch (this.allowed) {
                case ANY_SIDE: {
                    return x2;
                }
                case LEFT_SIDE: {
                    return inverted ? x2 : x0;
                }
                case RIGHT_SIDE: {
                    return inverted ? x0 : x2;
                }
                case BELOW_SIDE: {
                    return (f2 <= 0.0) ? x2 : x0;
                }
                case ABOVE_SIDE: {
                    return (f2 >= 0.0) ? x2 : x0;
                }
                default: {
                    throw new MathInternalError();
                }
            }
        }
    }
    
    protected enum Method
    {
        REGULA_FALSI, 
        ILLINOIS, 
        PEGASUS;
    }
}
