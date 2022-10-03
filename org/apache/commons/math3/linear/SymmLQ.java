package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.IterationEvent;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.IterationManager;

public class SymmLQ extends PreconditionedIterativeLinearSolver
{
    private static final String OPERATOR = "operator";
    private static final String THRESHOLD = "threshold";
    private static final String VECTOR = "vector";
    private static final String VECTOR1 = "vector1";
    private static final String VECTOR2 = "vector2";
    private final boolean check;
    private final double delta;
    
    public SymmLQ(final int maxIterations, final double delta, final boolean check) {
        super(maxIterations);
        this.delta = delta;
        this.check = check;
    }
    
    public SymmLQ(final IterationManager manager, final double delta, final boolean check) {
        super(manager);
        this.delta = delta;
        this.check = check;
    }
    
    public final boolean getCheck() {
        return this.check;
    }
    
    @Override
    public RealVector solve(final RealLinearOperator a, final RealLinearOperator m, final RealVector b) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException {
        MathUtils.checkNotNull(a);
        final RealVector x = new ArrayRealVector(a.getColumnDimension());
        return this.solveInPlace(a, m, b, x, false, 0.0);
    }
    
    public RealVector solve(final RealLinearOperator a, final RealLinearOperator m, final RealVector b, final boolean goodb, final double shift) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, MaxCountExceededException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException {
        MathUtils.checkNotNull(a);
        final RealVector x = new ArrayRealVector(a.getColumnDimension());
        return this.solveInPlace(a, m, b, x, goodb, shift);
    }
    
    @Override
    public RealVector solve(final RealLinearOperator a, final RealLinearOperator m, final RealVector b, final RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        MathUtils.checkNotNull(x);
        return this.solveInPlace(a, m, b, x.copy(), false, 0.0);
    }
    
    @Override
    public RealVector solve(final RealLinearOperator a, final RealVector b) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        MathUtils.checkNotNull(a);
        final RealVector x = new ArrayRealVector(a.getColumnDimension());
        x.set(0.0);
        return this.solveInPlace(a, null, b, x, false, 0.0);
    }
    
    public RealVector solve(final RealLinearOperator a, final RealVector b, final boolean goodb, final double shift) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        MathUtils.checkNotNull(a);
        final RealVector x = new ArrayRealVector(a.getColumnDimension());
        return this.solveInPlace(a, null, b, x, goodb, shift);
    }
    
    @Override
    public RealVector solve(final RealLinearOperator a, final RealVector b, final RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        MathUtils.checkNotNull(x);
        return this.solveInPlace(a, null, b, x.copy(), false, 0.0);
    }
    
    @Override
    public RealVector solveInPlace(final RealLinearOperator a, final RealLinearOperator m, final RealVector b, final RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        return this.solveInPlace(a, m, b, x, false, 0.0);
    }
    
    public RealVector solveInPlace(final RealLinearOperator a, final RealLinearOperator m, final RealVector b, final RealVector x, final boolean goodb, final double shift) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, NonPositiveDefiniteOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        PreconditionedIterativeLinearSolver.checkParameters(a, m, b, x);
        final IterationManager manager = this.getIterationManager();
        manager.resetIterationCount();
        manager.incrementIterationCount();
        final State state = new State(a, m, b, goodb, shift, this.delta, this.check);
        state.init();
        state.refineSolution(x);
        IterativeLinearSolverEvent event = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual());
        if (state.bEqualsNullVector()) {
            manager.fireTerminationEvent(event);
            return x;
        }
        final boolean earlyStop = state.betaEqualsZero() || state.hasConverged();
        manager.fireInitializationEvent(event);
        if (!earlyStop) {
            do {
                manager.incrementIterationCount();
                event = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual());
                manager.fireIterationStartedEvent(event);
                state.update();
                state.refineSolution(x);
                event = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual());
                manager.fireIterationPerformedEvent(event);
            } while (!state.hasConverged());
        }
        event = new DefaultIterativeLinearSolverEvent(this, manager.getIterations(), x, b, state.getNormOfResidual());
        manager.fireTerminationEvent(event);
        return x;
    }
    
    @Override
    public RealVector solveInPlace(final RealLinearOperator a, final RealVector b, final RealVector x) throws NullArgumentException, NonSquareOperatorException, DimensionMismatchException, NonSelfAdjointOperatorException, IllConditionedOperatorException, MaxCountExceededException {
        return this.solveInPlace(a, null, b, x, false, 0.0);
    }
    
    private static class State
    {
        static final double CBRT_MACH_PREC;
        static final double MACH_PREC;
        private final RealLinearOperator a;
        private final RealVector b;
        private final boolean check;
        private final double delta;
        private double beta;
        private double beta1;
        private double bstep;
        private double cgnorm;
        private double dbar;
        private double gammaZeta;
        private double gbar;
        private double gmax;
        private double gmin;
        private final boolean goodb;
        private boolean hasConverged;
        private double lqnorm;
        private final RealLinearOperator m;
        private double minusEpsZeta;
        private final RealVector mb;
        private double oldb;
        private RealVector r1;
        private RealVector r2;
        private double rnorm;
        private final double shift;
        private double snprod;
        private double tnorm;
        private RealVector wbar;
        private final RealVector xL;
        private RealVector y;
        private double ynorm2;
        private boolean bIsNull;
        
        State(final RealLinearOperator a, final RealLinearOperator m, final RealVector b, final boolean goodb, final double shift, final double delta, final boolean check) {
            this.a = a;
            this.m = m;
            this.b = b;
            this.xL = new ArrayRealVector(b.getDimension());
            this.goodb = goodb;
            this.shift = shift;
            this.mb = ((m == null) ? b : m.operate(b));
            this.hasConverged = false;
            this.check = check;
            this.delta = delta;
        }
        
        private static void checkSymmetry(final RealLinearOperator l, final RealVector x, final RealVector y, final RealVector z) throws NonSelfAdjointOperatorException {
            final double s = y.dotProduct(y);
            final double t = x.dotProduct(z);
            final double epsa = (s + State.MACH_PREC) * State.CBRT_MACH_PREC;
            if (FastMath.abs(s - t) > epsa) {
                final NonSelfAdjointOperatorException e = new NonSelfAdjointOperatorException();
                final ExceptionContext context = e.getContext();
                context.setValue("operator", l);
                context.setValue("vector1", x);
                context.setValue("vector2", y);
                context.setValue("threshold", epsa);
                throw e;
            }
        }
        
        private static void throwNPDLOException(final RealLinearOperator l, final RealVector v) throws NonPositiveDefiniteOperatorException {
            final NonPositiveDefiniteOperatorException e = new NonPositiveDefiniteOperatorException();
            final ExceptionContext context = e.getContext();
            context.setValue("operator", l);
            context.setValue("vector", v);
            throw e;
        }
        
        private static void daxpy(final double a, final RealVector x, final RealVector y) {
            for (int n = x.getDimension(), i = 0; i < n; ++i) {
                y.setEntry(i, a * x.getEntry(i) + y.getEntry(i));
            }
        }
        
        private static void daxpbypz(final double a, final RealVector x, final double b, final RealVector y, final RealVector z) {
            for (int n = z.getDimension(), i = 0; i < n; ++i) {
                final double zi = a * x.getEntry(i) + b * y.getEntry(i) + z.getEntry(i);
                z.setEntry(i, zi);
            }
        }
        
        void refineSolution(final RealVector x) {
            final int n = this.xL.getDimension();
            if (this.lqnorm < this.cgnorm) {
                if (!this.goodb) {
                    x.setSubVector(0, this.xL);
                }
                else {
                    final double step = this.bstep / this.beta1;
                    for (int i = 0; i < n; ++i) {
                        final double bi = this.mb.getEntry(i);
                        final double xi = this.xL.getEntry(i);
                        x.setEntry(i, xi + step * bi);
                    }
                }
            }
            else {
                final double anorm = FastMath.sqrt(this.tnorm);
                final double diag = (this.gbar == 0.0) ? (anorm * State.MACH_PREC) : this.gbar;
                final double zbar = this.gammaZeta / diag;
                final double step2 = (this.bstep + this.snprod * zbar) / this.beta1;
                if (!this.goodb) {
                    for (int j = 0; j < n; ++j) {
                        final double xi2 = this.xL.getEntry(j);
                        final double wi = this.wbar.getEntry(j);
                        x.setEntry(j, xi2 + zbar * wi);
                    }
                }
                else {
                    for (int j = 0; j < n; ++j) {
                        final double xi2 = this.xL.getEntry(j);
                        final double wi = this.wbar.getEntry(j);
                        final double bi2 = this.mb.getEntry(j);
                        x.setEntry(j, xi2 + zbar * wi + step2 * bi2);
                    }
                }
            }
        }
        
        void init() {
            this.xL.set(0.0);
            this.r1 = this.b.copy();
            this.y = ((this.m == null) ? this.b.copy() : this.m.operate(this.r1));
            if (this.m != null && this.check) {
                checkSymmetry(this.m, this.r1, this.y, this.m.operate(this.y));
            }
            this.beta1 = this.r1.dotProduct(this.y);
            if (this.beta1 < 0.0) {
                throwNPDLOException(this.m, this.y);
            }
            if (this.beta1 == 0.0) {
                this.bIsNull = true;
                return;
            }
            this.bIsNull = false;
            this.beta1 = FastMath.sqrt(this.beta1);
            final RealVector v = this.y.mapMultiply(1.0 / this.beta1);
            this.y = this.a.operate(v);
            if (this.check) {
                checkSymmetry(this.a, v, this.y, this.a.operate(this.y));
            }
            daxpy(-this.shift, v, this.y);
            final double alpha = v.dotProduct(this.y);
            daxpy(-alpha / this.beta1, this.r1, this.y);
            final double vty = v.dotProduct(this.y);
            final double vtv = v.dotProduct(v);
            daxpy(-vty / vtv, v, this.y);
            this.r2 = this.y.copy();
            if (this.m != null) {
                this.y = this.m.operate(this.r2);
            }
            this.oldb = this.beta1;
            this.beta = this.r2.dotProduct(this.y);
            if (this.beta < 0.0) {
                throwNPDLOException(this.m, this.y);
            }
            this.beta = FastMath.sqrt(this.beta);
            this.cgnorm = this.beta1;
            this.gbar = alpha;
            this.dbar = this.beta;
            this.gammaZeta = this.beta1;
            this.minusEpsZeta = 0.0;
            this.bstep = 0.0;
            this.snprod = 1.0;
            this.tnorm = alpha * alpha + this.beta * this.beta;
            this.ynorm2 = 0.0;
            this.gmax = FastMath.abs(alpha) + State.MACH_PREC;
            this.gmin = this.gmax;
            if (this.goodb) {
                (this.wbar = new ArrayRealVector(this.a.getRowDimension())).set(0.0);
            }
            else {
                this.wbar = v;
            }
            this.updateNorms();
        }
        
        void update() {
            final RealVector v = this.y.mapMultiply(1.0 / this.beta);
            this.y = this.a.operate(v);
            daxpbypz(-this.shift, v, -this.beta / this.oldb, this.r1, this.y);
            final double alpha = v.dotProduct(this.y);
            daxpy(-alpha / this.beta, this.r2, this.y);
            this.r1 = this.r2;
            this.r2 = this.y;
            if (this.m != null) {
                this.y = this.m.operate(this.r2);
            }
            this.oldb = this.beta;
            this.beta = this.r2.dotProduct(this.y);
            if (this.beta < 0.0) {
                throwNPDLOException(this.m, this.y);
            }
            this.beta = FastMath.sqrt(this.beta);
            this.tnorm += alpha * alpha + this.oldb * this.oldb + this.beta * this.beta;
            final double gamma = FastMath.sqrt(this.gbar * this.gbar + this.oldb * this.oldb);
            final double c = this.gbar / gamma;
            final double s = this.oldb / gamma;
            final double deltak = c * this.dbar + s * alpha;
            this.gbar = s * this.dbar - c * alpha;
            final double eps = s * this.beta;
            this.dbar = -c * this.beta;
            final double zeta = this.gammaZeta / gamma;
            final double zetaC = zeta * c;
            final double zetaS = zeta * s;
            for (int n = this.xL.getDimension(), i = 0; i < n; ++i) {
                final double xi = this.xL.getEntry(i);
                final double vi = v.getEntry(i);
                final double wi = this.wbar.getEntry(i);
                this.xL.setEntry(i, xi + wi * zetaC + vi * zetaS);
                this.wbar.setEntry(i, wi * s - vi * c);
            }
            this.bstep += this.snprod * c * zeta;
            this.snprod *= s;
            this.gmax = FastMath.max(this.gmax, gamma);
            this.gmin = FastMath.min(this.gmin, gamma);
            this.ynorm2 += zeta * zeta;
            this.gammaZeta = this.minusEpsZeta - deltak * zeta;
            this.minusEpsZeta = -eps * zeta;
            this.updateNorms();
        }
        
        private void updateNorms() {
            final double anorm = FastMath.sqrt(this.tnorm);
            final double ynorm = FastMath.sqrt(this.ynorm2);
            final double epsa = anorm * State.MACH_PREC;
            final double epsx = anorm * ynorm * State.MACH_PREC;
            final double epsr = anorm * ynorm * this.delta;
            final double diag = (this.gbar == 0.0) ? epsa : this.gbar;
            this.lqnorm = FastMath.sqrt(this.gammaZeta * this.gammaZeta + this.minusEpsZeta * this.minusEpsZeta);
            final double qrnorm = this.snprod * this.beta1;
            this.cgnorm = qrnorm * this.beta / FastMath.abs(diag);
            double acond;
            if (this.lqnorm <= this.cgnorm) {
                acond = this.gmax / this.gmin;
            }
            else {
                acond = this.gmax / FastMath.min(this.gmin, FastMath.abs(diag));
            }
            if (acond * State.MACH_PREC >= 0.1) {
                throw new IllConditionedOperatorException(acond);
            }
            if (this.beta1 <= epsx) {
                throw new SingularOperatorException();
            }
            this.rnorm = FastMath.min(this.cgnorm, this.lqnorm);
            this.hasConverged = (this.cgnorm <= epsx || this.cgnorm <= epsr);
        }
        
        boolean hasConverged() {
            return this.hasConverged;
        }
        
        boolean bEqualsNullVector() {
            return this.bIsNull;
        }
        
        boolean betaEqualsZero() {
            return this.beta < State.MACH_PREC;
        }
        
        double getNormOfResidual() {
            return this.rnorm;
        }
        
        static {
            MACH_PREC = FastMath.ulp(1.0);
            CBRT_MACH_PREC = FastMath.cbrt(State.MACH_PREC);
        }
    }
}
