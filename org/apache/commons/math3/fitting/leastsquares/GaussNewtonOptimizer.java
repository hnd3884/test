package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.linear.NonPositiveDefiniteMatrixException;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.exception.NullArgumentException;

public class GaussNewtonOptimizer implements LeastSquaresOptimizer
{
    private static final double SINGULARITY_THRESHOLD = 1.0E-11;
    private final Decomposition decomposition;
    
    public GaussNewtonOptimizer() {
        this(Decomposition.QR);
    }
    
    public GaussNewtonOptimizer(final Decomposition decomposition) {
        this.decomposition = decomposition;
    }
    
    public Decomposition getDecomposition() {
        return this.decomposition;
    }
    
    public GaussNewtonOptimizer withDecomposition(final Decomposition newDecomposition) {
        return new GaussNewtonOptimizer(newDecomposition);
    }
    
    public Optimum optimize(final LeastSquaresProblem lsp) {
        final Incrementor evaluationCounter = lsp.getEvaluationCounter();
        final Incrementor iterationCounter = lsp.getIterationCounter();
        final ConvergenceChecker<LeastSquaresProblem.Evaluation> checker = lsp.getConvergenceChecker();
        if (checker == null) {
            throw new NullArgumentException();
        }
        RealVector currentPoint = lsp.getStart();
        LeastSquaresProblem.Evaluation current = null;
        while (true) {
            iterationCounter.incrementCount();
            final LeastSquaresProblem.Evaluation previous = current;
            evaluationCounter.incrementCount();
            current = lsp.evaluate(currentPoint);
            final RealVector currentResiduals = current.getResiduals();
            final RealMatrix weightedJacobian = current.getJacobian();
            currentPoint = current.getPoint();
            if (previous != null && checker.converged(iterationCounter.getCount(), previous, current)) {
                break;
            }
            final RealVector dX = this.decomposition.solve(weightedJacobian, currentResiduals);
            currentPoint = currentPoint.add(dX);
        }
        return new OptimumImpl(current, evaluationCounter.getCount(), iterationCounter.getCount());
    }
    
    @Override
    public String toString() {
        return "GaussNewtonOptimizer{decomposition=" + this.decomposition + '}';
    }
    
    private static Pair<RealMatrix, RealVector> computeNormalMatrix(final RealMatrix jacobian, final RealVector residuals) {
        final int nR = jacobian.getRowDimension();
        final int nC = jacobian.getColumnDimension();
        final RealMatrix normal = MatrixUtils.createRealMatrix(nC, nC);
        final RealVector jTr = new ArrayRealVector(nC);
        for (int i = 0; i < nR; ++i) {
            for (int j = 0; j < nC; ++j) {
                jTr.setEntry(j, jTr.getEntry(j) + residuals.getEntry(i) * jacobian.getEntry(i, j));
            }
            for (int k = 0; k < nC; ++k) {
                for (int l = k; l < nC; ++l) {
                    normal.setEntry(k, l, normal.getEntry(k, l) + jacobian.getEntry(i, k) * jacobian.getEntry(i, l));
                }
            }
        }
        for (int i = 0; i < nC; ++i) {
            for (int j = 0; j < i; ++j) {
                normal.setEntry(i, j, normal.getEntry(j, i));
            }
        }
        return new Pair<RealMatrix, RealVector>(normal, jTr);
    }
    
    public enum Decomposition
    {
        LU {
            @Override
            protected RealVector solve(final RealMatrix jacobian, final RealVector residuals) {
                try {
                    final Pair<RealMatrix, RealVector> normalEquation = computeNormalMatrix(jacobian, residuals);
                    final RealMatrix normal = normalEquation.getFirst();
                    final RealVector jTr = normalEquation.getSecond();
                    return new LUDecomposition(normal, 1.0E-11).getSolver().solve(jTr);
                }
                catch (final SingularMatrixException e) {
                    throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, new Object[] { e });
                }
            }
        }, 
        QR {
            @Override
            protected RealVector solve(final RealMatrix jacobian, final RealVector residuals) {
                try {
                    return new QRDecomposition(jacobian, 1.0E-11).getSolver().solve(residuals);
                }
                catch (final SingularMatrixException e) {
                    throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, new Object[] { e });
                }
            }
        }, 
        CHOLESKY {
            @Override
            protected RealVector solve(final RealMatrix jacobian, final RealVector residuals) {
                try {
                    final Pair<RealMatrix, RealVector> normalEquation = computeNormalMatrix(jacobian, residuals);
                    final RealMatrix normal = normalEquation.getFirst();
                    final RealVector jTr = normalEquation.getSecond();
                    return new CholeskyDecomposition(normal, 1.0E-11, 1.0E-11).getSolver().solve(jTr);
                }
                catch (final NonPositiveDefiniteMatrixException e) {
                    throw new ConvergenceException(LocalizedFormats.UNABLE_TO_SOLVE_SINGULAR_PROBLEM, new Object[] { e });
                }
            }
        }, 
        SVD {
            @Override
            protected RealVector solve(final RealMatrix jacobian, final RealVector residuals) {
                return new SingularValueDecomposition(jacobian).getSolver().solve(residuals);
            }
        };
        
        protected abstract RealVector solve(final RealMatrix p0, final RealVector p1);
    }
}
