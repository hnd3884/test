package org.apache.commons.math3.optim.nonlinear.scalar.gradient;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.LineSearch;
import org.apache.commons.math3.optim.nonlinear.scalar.GradientMultivariateOptimizer;

public class NonLinearConjugateGradientOptimizer extends GradientMultivariateOptimizer
{
    private final Formula updateFormula;
    private final Preconditioner preconditioner;
    private final LineSearch line;
    
    public NonLinearConjugateGradientOptimizer(final Formula updateFormula, final ConvergenceChecker<PointValuePair> checker) {
        this(updateFormula, checker, 1.0E-8, 1.0E-8, 1.0E-8, new IdentityPreconditioner());
    }
    
    @Deprecated
    public NonLinearConjugateGradientOptimizer(final Formula updateFormula, final ConvergenceChecker<PointValuePair> checker, final UnivariateSolver lineSearchSolver) {
        this(updateFormula, checker, lineSearchSolver, new IdentityPreconditioner());
    }
    
    public NonLinearConjugateGradientOptimizer(final Formula updateFormula, final ConvergenceChecker<PointValuePair> checker, final double relativeTolerance, final double absoluteTolerance, final double initialBracketingRange) {
        this(updateFormula, checker, relativeTolerance, absoluteTolerance, initialBracketingRange, new IdentityPreconditioner());
    }
    
    @Deprecated
    public NonLinearConjugateGradientOptimizer(final Formula updateFormula, final ConvergenceChecker<PointValuePair> checker, final UnivariateSolver lineSearchSolver, final Preconditioner preconditioner) {
        this(updateFormula, checker, lineSearchSolver.getRelativeAccuracy(), lineSearchSolver.getAbsoluteAccuracy(), lineSearchSolver.getAbsoluteAccuracy(), preconditioner);
    }
    
    public NonLinearConjugateGradientOptimizer(final Formula updateFormula, final ConvergenceChecker<PointValuePair> checker, final double relativeTolerance, final double absoluteTolerance, final double initialBracketingRange, final Preconditioner preconditioner) {
        super(checker);
        this.updateFormula = updateFormula;
        this.preconditioner = preconditioner;
        this.line = new LineSearch(this, relativeTolerance, absoluteTolerance, initialBracketingRange);
    }
    
    @Override
    public PointValuePair optimize(final OptimizationData... optData) throws TooManyEvaluationsException {
        return super.optimize(optData);
    }
    
    @Override
    protected PointValuePair doOptimize() {
        final ConvergenceChecker<PointValuePair> checker = this.getConvergenceChecker();
        final double[] point = this.getStartPoint();
        final GoalType goal = this.getGoalType();
        final int n = point.length;
        double[] r = this.computeObjectiveGradient(point);
        if (goal == GoalType.MINIMIZE) {
            for (int i = 0; i < n; ++i) {
                r[i] = -r[i];
            }
        }
        double[] steepestDescent = this.preconditioner.precondition(point, r);
        double[] searchDirection = steepestDescent.clone();
        double delta = 0.0;
        for (int j = 0; j < n; ++j) {
            delta += r[j] * searchDirection[j];
        }
        PointValuePair current = null;
        while (true) {
            this.incrementIterationCount();
            final double objective = this.computeObjectiveValue(point);
            final PointValuePair previous = current;
            current = new PointValuePair(point, objective);
            if (previous != null && checker.converged(this.getIterations(), previous, current)) {
                return current;
            }
            final double step = this.line.search(point, searchDirection).getPoint();
            for (int k = 0; k < point.length; ++k) {
                final double[] array = point;
                final int n2 = k;
                array[n2] += step * searchDirection[k];
            }
            r = this.computeObjectiveGradient(point);
            if (goal == GoalType.MINIMIZE) {
                for (int k = 0; k < n; ++k) {
                    r[k] = -r[k];
                }
            }
            final double deltaOld = delta;
            final double[] newSteepestDescent = this.preconditioner.precondition(point, r);
            delta = 0.0;
            for (int l = 0; l < n; ++l) {
                delta += r[l] * newSteepestDescent[l];
            }
            double beta = 0.0;
            switch (this.updateFormula) {
                case FLETCHER_REEVES: {
                    beta = delta / deltaOld;
                    break;
                }
                case POLAK_RIBIERE: {
                    double deltaMid = 0.0;
                    for (int m = 0; m < r.length; ++m) {
                        deltaMid += r[m] * steepestDescent[m];
                    }
                    beta = (delta - deltaMid) / deltaOld;
                    break;
                }
                default: {
                    throw new MathInternalError();
                }
            }
            steepestDescent = newSteepestDescent;
            if (this.getIterations() % n == 0 || beta < 0.0) {
                searchDirection = steepestDescent.clone();
            }
            else {
                for (int i2 = 0; i2 < n; ++i2) {
                    searchDirection[i2] = steepestDescent[i2] + beta * searchDirection[i2];
                }
            }
        }
    }
    
    @Override
    protected void parseOptimizationData(final OptimizationData... optData) {
        super.parseOptimizationData(optData);
        this.checkParameters();
    }
    
    private void checkParameters() {
        if (this.getLowerBound() != null || this.getUpperBound() != null) {
            throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT, new Object[0]);
        }
    }
    
    public enum Formula
    {
        FLETCHER_REEVES, 
        POLAK_RIBIERE;
    }
    
    @Deprecated
    public static class BracketingStep implements OptimizationData
    {
        private final double initialStep;
        
        public BracketingStep(final double step) {
            this.initialStep = step;
        }
        
        public double getBracketingStep() {
            return this.initialStep;
        }
    }
    
    public static class IdentityPreconditioner implements Preconditioner
    {
        public double[] precondition(final double[] variables, final double[] r) {
            return r.clone();
        }
    }
}
