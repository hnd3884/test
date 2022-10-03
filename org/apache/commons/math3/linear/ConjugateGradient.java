package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.util.IterationEvent;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.IterationManager;

public class ConjugateGradient extends PreconditionedIterativeLinearSolver
{
    public static final String OPERATOR = "operator";
    public static final String VECTOR = "vector";
    private boolean check;
    private final double delta;
    
    public ConjugateGradient(final int maxIterations, final double delta, final boolean check) {
        super(maxIterations);
        this.delta = delta;
        this.check = check;
    }
    
    public ConjugateGradient(final IterationManager manager, final double delta, final boolean check) throws NullArgumentException {
        super(manager);
        this.delta = delta;
        this.check = check;
    }
    
    public final boolean getCheck() {
        return this.check;
    }
    
    @Override
    public RealVector solveInPlace(final RealLinearOperator a, final RealLinearOperator m, final RealVector b, final RealVector x0) throws NullArgumentException, NonPositiveDefiniteOperatorException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException {
        PreconditionedIterativeLinearSolver.checkParameters(a, m, b, x0);
        final IterationManager manager = this.getIterationManager();
        manager.resetIterationCount();
        final double rmax = this.delta * b.getNorm();
        final RealVector bro = RealVector.unmodifiableRealVector(b);
        manager.incrementIterationCount();
        final RealVector x = x0;
        final RealVector xro = RealVector.unmodifiableRealVector(x);
        final RealVector p = x.copy();
        RealVector q = a.operate(p);
        final RealVector r = b.combine(1.0, -1.0, q);
        final RealVector rro = RealVector.unmodifiableRealVector(r);
        double rnorm = r.getNorm();
        RealVector z;
        if (m == null) {
            z = r;
        }
        else {
            z = null;
        }
        IterativeLinearSolverEvent evt = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), xro, bro, rro, rnorm);
        manager.fireInitializationEvent(evt);
        if (rnorm <= rmax) {
            manager.fireTerminationEvent(evt);
            return x;
        }
        double rhoPrev = 0.0;
        while (true) {
            manager.incrementIterationCount();
            evt = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), xro, bro, rro, rnorm);
            manager.fireIterationStartedEvent(evt);
            if (m != null) {
                z = m.operate(r);
            }
            final double rhoNext = r.dotProduct(z);
            if (this.check && rhoNext <= 0.0) {
                final NonPositiveDefiniteOperatorException e = new NonPositiveDefiniteOperatorException();
                final ExceptionContext context = e.getContext();
                context.setValue("operator", m);
                context.setValue("vector", r);
                throw e;
            }
            if (manager.getIterations() == 2) {
                p.setSubVector(0, z);
            }
            else {
                p.combineToSelf(rhoNext / rhoPrev, 1.0, z);
            }
            q = a.operate(p);
            final double pq = p.dotProduct(q);
            if (this.check && pq <= 0.0) {
                final NonPositiveDefiniteOperatorException e2 = new NonPositiveDefiniteOperatorException();
                final ExceptionContext context2 = e2.getContext();
                context2.setValue("operator", a);
                context2.setValue("vector", p);
                throw e2;
            }
            final double alpha = rhoNext / pq;
            x.combineToSelf(1.0, alpha, p);
            r.combineToSelf(1.0, -alpha, q);
            rhoPrev = rhoNext;
            rnorm = r.getNorm();
            evt = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), xro, bro, rro, rnorm);
            manager.fireIterationPerformedEvent(evt);
            if (rnorm <= rmax) {
                manager.fireTerminationEvent(evt);
                return x;
            }
        }
    }
}
