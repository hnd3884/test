package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.optim.AbstractOptimizationProblem;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.analysis.MultivariateMatrixFunction;
import org.apache.commons.math3.analysis.MultivariateVectorFunction;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class LeastSquaresFactory
{
    private LeastSquaresFactory() {
    }
    
    public static LeastSquaresProblem create(final MultivariateJacobianFunction model, final RealVector observed, final RealVector start, final RealMatrix weight, final ConvergenceChecker<LeastSquaresProblem.Evaluation> checker, final int maxEvaluations, final int maxIterations, final boolean lazyEvaluation, final ParameterValidator paramValidator) {
        final LeastSquaresProblem p = new LocalLeastSquaresProblem(model, observed, start, checker, maxEvaluations, maxIterations, lazyEvaluation, paramValidator);
        if (weight != null) {
            return weightMatrix(p, weight);
        }
        return p;
    }
    
    public static LeastSquaresProblem create(final MultivariateJacobianFunction model, final RealVector observed, final RealVector start, final ConvergenceChecker<LeastSquaresProblem.Evaluation> checker, final int maxEvaluations, final int maxIterations) {
        return create(model, observed, start, null, checker, maxEvaluations, maxIterations, false, null);
    }
    
    public static LeastSquaresProblem create(final MultivariateJacobianFunction model, final RealVector observed, final RealVector start, final RealMatrix weight, final ConvergenceChecker<LeastSquaresProblem.Evaluation> checker, final int maxEvaluations, final int maxIterations) {
        return weightMatrix(create(model, observed, start, checker, maxEvaluations, maxIterations), weight);
    }
    
    public static LeastSquaresProblem create(final MultivariateVectorFunction model, final MultivariateMatrixFunction jacobian, final double[] observed, final double[] start, final RealMatrix weight, final ConvergenceChecker<LeastSquaresProblem.Evaluation> checker, final int maxEvaluations, final int maxIterations) {
        return create(model(model, jacobian), new ArrayRealVector(observed, false), new ArrayRealVector(start, false), weight, checker, maxEvaluations, maxIterations);
    }
    
    public static LeastSquaresProblem weightMatrix(final LeastSquaresProblem problem, final RealMatrix weights) {
        final RealMatrix weightSquareRoot = squareRoot(weights);
        return new LeastSquaresAdapter(problem) {
            @Override
            public LeastSquaresProblem.Evaluation evaluate(final RealVector point) {
                return new DenseWeightedEvaluation(super.evaluate(point), weightSquareRoot);
            }
        };
    }
    
    public static LeastSquaresProblem weightDiagonal(final LeastSquaresProblem problem, final RealVector weights) {
        return weightMatrix(problem, new DiagonalMatrix(weights.toArray()));
    }
    
    public static LeastSquaresProblem countEvaluations(final LeastSquaresProblem problem, final Incrementor counter) {
        return new LeastSquaresAdapter(problem) {
            @Override
            public LeastSquaresProblem.Evaluation evaluate(final RealVector point) {
                counter.incrementCount();
                return super.evaluate(point);
            }
        };
    }
    
    public static ConvergenceChecker<LeastSquaresProblem.Evaluation> evaluationChecker(final ConvergenceChecker<PointVectorValuePair> checker) {
        return new ConvergenceChecker<LeastSquaresProblem.Evaluation>() {
            public boolean converged(final int iteration, final LeastSquaresProblem.Evaluation previous, final LeastSquaresProblem.Evaluation current) {
                return checker.converged(iteration, new PointVectorValuePair(previous.getPoint().toArray(), previous.getResiduals().toArray(), false), new PointVectorValuePair(current.getPoint().toArray(), current.getResiduals().toArray(), false));
            }
        };
    }
    
    private static RealMatrix squareRoot(final RealMatrix m) {
        if (m instanceof DiagonalMatrix) {
            final int dim = m.getRowDimension();
            final RealMatrix sqrtM = new DiagonalMatrix(dim);
            for (int i = 0; i < dim; ++i) {
                sqrtM.setEntry(i, i, FastMath.sqrt(m.getEntry(i, i)));
            }
            return sqrtM;
        }
        final EigenDecomposition dec = new EigenDecomposition(m);
        return dec.getSquareRoot();
    }
    
    public static MultivariateJacobianFunction model(final MultivariateVectorFunction value, final MultivariateMatrixFunction jacobian) {
        return new LocalValueAndJacobianFunction(value, jacobian);
    }
    
    private static class LocalValueAndJacobianFunction implements ValueAndJacobianFunction
    {
        private final MultivariateVectorFunction value;
        private final MultivariateMatrixFunction jacobian;
        
        LocalValueAndJacobianFunction(final MultivariateVectorFunction value, final MultivariateMatrixFunction jacobian) {
            this.value = value;
            this.jacobian = jacobian;
        }
        
        public Pair<RealVector, RealMatrix> value(final RealVector point) {
            final double[] p = point.toArray();
            return new Pair<RealVector, RealMatrix>(this.computeValue(p), this.computeJacobian(p));
        }
        
        public RealVector computeValue(final double[] params) {
            return new ArrayRealVector(this.value.value(params), false);
        }
        
        public RealMatrix computeJacobian(final double[] params) {
            return new Array2DRowRealMatrix(this.jacobian.value(params), false);
        }
    }
    
    private static class LocalLeastSquaresProblem extends AbstractOptimizationProblem<Evaluation> implements LeastSquaresProblem
    {
        private final RealVector target;
        private final MultivariateJacobianFunction model;
        private final RealVector start;
        private final boolean lazyEvaluation;
        private final ParameterValidator paramValidator;
        
        LocalLeastSquaresProblem(final MultivariateJacobianFunction model, final RealVector target, final RealVector start, final ConvergenceChecker<Evaluation> checker, final int maxEvaluations, final int maxIterations, final boolean lazyEvaluation, final ParameterValidator paramValidator) {
            super(maxEvaluations, maxIterations, checker);
            this.target = target;
            this.model = model;
            this.start = start;
            this.lazyEvaluation = lazyEvaluation;
            this.paramValidator = paramValidator;
            if (lazyEvaluation && !(model instanceof ValueAndJacobianFunction)) {
                throw new MathIllegalStateException(LocalizedFormats.INVALID_IMPLEMENTATION, new Object[] { model.getClass().getName() });
            }
        }
        
        public int getObservationSize() {
            return this.target.getDimension();
        }
        
        public int getParameterSize() {
            return this.start.getDimension();
        }
        
        public RealVector getStart() {
            return (this.start == null) ? null : this.start.copy();
        }
        
        public Evaluation evaluate(final RealVector point) {
            final RealVector p = (this.paramValidator == null) ? point.copy() : this.paramValidator.validate(point.copy());
            if (this.lazyEvaluation) {
                return new LazyUnweightedEvaluation((ValueAndJacobianFunction)this.model, this.target, p);
            }
            final Pair<RealVector, RealMatrix> value = this.model.value(p);
            return new UnweightedEvaluation((RealVector)value.getFirst(), (RealMatrix)value.getSecond(), this.target, p);
        }
        
        private static class UnweightedEvaluation extends AbstractEvaluation
        {
            private final RealVector point;
            private final RealMatrix jacobian;
            private final RealVector residuals;
            
            private UnweightedEvaluation(final RealVector values, final RealMatrix jacobian, final RealVector target, final RealVector point) {
                super(target.getDimension());
                this.jacobian = jacobian;
                this.point = point;
                this.residuals = target.subtract(values);
            }
            
            public RealMatrix getJacobian() {
                return this.jacobian;
            }
            
            public RealVector getPoint() {
                return this.point;
            }
            
            public RealVector getResiduals() {
                return this.residuals;
            }
        }
        
        private static class LazyUnweightedEvaluation extends AbstractEvaluation
        {
            private final RealVector point;
            private final ValueAndJacobianFunction model;
            private final RealVector target;
            
            private LazyUnweightedEvaluation(final ValueAndJacobianFunction model, final RealVector target, final RealVector point) {
                super(target.getDimension());
                this.model = model;
                this.point = point;
                this.target = target;
            }
            
            public RealMatrix getJacobian() {
                return this.model.computeJacobian(this.point.toArray());
            }
            
            public RealVector getPoint() {
                return this.point;
            }
            
            public RealVector getResiduals() {
                return this.target.subtract(this.model.computeValue(this.point.toArray()));
            }
        }
    }
}
