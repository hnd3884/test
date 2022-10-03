package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;

public class DefaultIterativeLinearSolverEvent extends IterativeLinearSolverEvent
{
    private static final long serialVersionUID = 20120129L;
    private final RealVector b;
    private final RealVector r;
    private final double rnorm;
    private final RealVector x;
    
    public DefaultIterativeLinearSolverEvent(final Object source, final int iterations, final RealVector x, final RealVector b, final RealVector r, final double rnorm) {
        super(source, iterations);
        this.x = x;
        this.b = b;
        this.r = r;
        this.rnorm = rnorm;
    }
    
    public DefaultIterativeLinearSolverEvent(final Object source, final int iterations, final RealVector x, final RealVector b, final double rnorm) {
        super(source, iterations);
        this.x = x;
        this.b = b;
        this.r = null;
        this.rnorm = rnorm;
    }
    
    @Override
    public double getNormOfResidual() {
        return this.rnorm;
    }
    
    @Override
    public RealVector getResidual() {
        if (this.r != null) {
            return this.r;
        }
        throw new MathUnsupportedOperationException();
    }
    
    @Override
    public RealVector getRightHandSideVector() {
        return this.b;
    }
    
    @Override
    public RealVector getSolution() {
        return this.x;
    }
    
    @Override
    public boolean providesResidual() {
        return this.r != null;
    }
}
