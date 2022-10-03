package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ParameterJacobianWrapper implements ParameterJacobianProvider
{
    private final FirstOrderDifferentialEquations fode;
    private final ParameterizedODE pode;
    private final Map<String, Double> hParam;
    
    ParameterJacobianWrapper(final FirstOrderDifferentialEquations fode, final ParameterizedODE pode, final ParameterConfiguration[] paramsAndSteps) {
        this.fode = fode;
        this.pode = pode;
        this.hParam = new HashMap<String, Double>();
        for (final ParameterConfiguration param : paramsAndSteps) {
            final String name = param.getParameterName();
            if (pode.isSupported(name)) {
                this.hParam.put(name, param.getHP());
            }
        }
    }
    
    public Collection<String> getParametersNames() {
        return this.pode.getParametersNames();
    }
    
    public boolean isSupported(final String name) {
        return this.pode.isSupported(name);
    }
    
    public void computeParameterJacobian(final double t, final double[] y, final double[] yDot, final String paramName, final double[] dFdP) throws DimensionMismatchException, MaxCountExceededException {
        final int n = this.fode.getDimension();
        if (this.pode.isSupported(paramName)) {
            final double[] tmpDot = new double[n];
            final double p = this.pode.getParameter(paramName);
            final double hP = this.hParam.get(paramName);
            this.pode.setParameter(paramName, p + hP);
            this.fode.computeDerivatives(t, y, tmpDot);
            for (int i = 0; i < n; ++i) {
                dFdP[i] = (tmpDot[i] - yDot[i]) / hP;
            }
            this.pode.setParameter(paramName, p);
        }
        else {
            Arrays.fill(dFdP, 0, n, 0.0);
        }
    }
}
