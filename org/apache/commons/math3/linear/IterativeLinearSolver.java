package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.IterationManager;

public abstract class IterativeLinearSolver
{
    private final IterationManager manager;
    
    public IterativeLinearSolver(final int maxIterations) {
        this.manager = new IterationManager(maxIterations);
    }
    
    public IterativeLinearSolver(final IterationManager manager) throws NullArgumentException {
        MathUtils.checkNotNull(manager);
        this.manager = manager;
    }
    
    protected static void checkParameters(final RealLinearOperator a, final RealVector b, final RealVector x0) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException {
        MathUtils.checkNotNull(a);
        MathUtils.checkNotNull(b);
        MathUtils.checkNotNull(x0);
        if (a.getRowDimension() != a.getColumnDimension()) {
            throw new NonSquareOperatorException(a.getRowDimension(), a.getColumnDimension());
        }
        if (b.getDimension() != a.getRowDimension()) {
            throw new DimensionMismatchException(b.getDimension(), a.getRowDimension());
        }
        if (x0.getDimension() != a.getColumnDimension()) {
            throw new DimensionMismatchException(x0.getDimension(), a.getColumnDimension());
        }
    }
    
    public IterationManager getIterationManager() {
        return this.manager;
    }
    
    public RealVector solve(final RealLinearOperator a, final RealVector b) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException {
        MathUtils.checkNotNull(a);
        final RealVector x = new ArrayRealVector(a.getColumnDimension());
        x.set(0.0);
        return this.solveInPlace(a, b, x);
    }
    
    public RealVector solve(final RealLinearOperator a, final RealVector b, final RealVector x0) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException {
        MathUtils.checkNotNull(x0);
        return this.solveInPlace(a, b, x0.copy());
    }
    
    public abstract RealVector solveInPlace(final RealLinearOperator p0, final RealVector p1, final RealVector p2) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException;
}
