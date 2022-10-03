package org.apache.commons.math3.ode;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

class ParameterizedWrapper implements ParameterizedODE
{
    private final FirstOrderDifferentialEquations fode;
    
    ParameterizedWrapper(final FirstOrderDifferentialEquations ode) {
        this.fode = ode;
    }
    
    public int getDimension() {
        return this.fode.getDimension();
    }
    
    public void computeDerivatives(final double t, final double[] y, final double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
        this.fode.computeDerivatives(t, y, yDot);
    }
    
    public Collection<String> getParametersNames() {
        return new ArrayList<String>();
    }
    
    public boolean isSupported(final String name) {
        return false;
    }
    
    public double getParameter(final String name) throws UnknownParameterException {
        if (!this.isSupported(name)) {
            throw new UnknownParameterException(name);
        }
        return Double.NaN;
    }
    
    public void setParameter(final String name, final double value) {
    }
}
