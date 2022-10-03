package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.IterationManager;

public abstract class PreconditionedIterativeLinearSolver extends IterativeLinearSolver
{
    public PreconditionedIterativeLinearSolver(final int maxIterations) {
        super(maxIterations);
    }
    
    public PreconditionedIterativeLinearSolver(final IterationManager manager) throws NullArgumentException {
        super(manager);
    }
    
    public RealVector solve(final RealLinearOperator a, final RealLinearOperator m, final RealVector b, final RealVector x0) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException {
        MathUtils.checkNotNull(x0);
        return this.solveInPlace(a, m, b, x0.copy());
    }
    
    @Override
    public RealVector solve(final RealLinearOperator a, final RealVector b) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException {
        MathUtils.checkNotNull(a);
        final RealVector x = new ArrayRealVector(a.getColumnDimension());
        x.set(0.0);
        return this.solveInPlace(a, null, b, x);
    }
    
    @Override
    public RealVector solve(final RealLinearOperator a, final RealVector b, final RealVector x0) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException {
        MathUtils.checkNotNull(x0);
        return this.solveInPlace(a, null, b, x0.copy());
    }
    
    protected static void checkParameters(final RealLinearOperator a, final RealLinearOperator m, final RealVector b, final RealVector x0) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException {
        IterativeLinearSolver.checkParameters(a, b, x0);
        if (m != null) {
            if (m.getColumnDimension() != m.getRowDimension()) {
                throw new NonSquareOperatorException(m.getColumnDimension(), m.getRowDimension());
            }
            if (m.getRowDimension() != a.getRowDimension()) {
                throw new DimensionMismatchException(m.getRowDimension(), a.getRowDimension());
            }
        }
    }
    
    public RealVector solve(final RealLinearOperator a, final RealLinearOperator m, final RealVector b) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException {
        MathUtils.checkNotNull(a);
        final RealVector x = new ArrayRealVector(a.getColumnDimension());
        return this.solveInPlace(a, m, b, x);
    }
    
    public abstract RealVector solveInPlace(final RealLinearOperator p0, final RealLinearOperator p1, final RealVector p2, final RealVector p3) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException;
    
    @Override
    public RealVector solveInPlace(final RealLinearOperator a, final RealVector b, final RealVector x0) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException {
        return this.solveInPlace(a, null, b, x0);
    }
}
