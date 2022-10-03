package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.util.ArrayList;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.util.List;

public class JacobianMatrices
{
    private ExpandableStatefulODE efode;
    private int index;
    private MainStateJacobianProvider jode;
    private ParameterizedODE pode;
    private int stateDim;
    private ParameterConfiguration[] selectedParameters;
    private List<ParameterJacobianProvider> jacobianProviders;
    private int paramDim;
    private boolean dirtyParameter;
    private double[] matricesData;
    
    public JacobianMatrices(final FirstOrderDifferentialEquations fode, final double[] hY, final String... parameters) throws DimensionMismatchException {
        this(new MainStateJacobianWrapper(fode, hY), parameters);
    }
    
    public JacobianMatrices(final MainStateJacobianProvider jode, final String... parameters) {
        this.efode = null;
        this.index = -1;
        this.jode = jode;
        this.pode = null;
        this.stateDim = jode.getDimension();
        if (parameters == null) {
            this.selectedParameters = null;
            this.paramDim = 0;
        }
        else {
            this.selectedParameters = new ParameterConfiguration[parameters.length];
            for (int i = 0; i < parameters.length; ++i) {
                this.selectedParameters[i] = new ParameterConfiguration(parameters[i], Double.NaN);
            }
            this.paramDim = parameters.length;
        }
        this.dirtyParameter = false;
        this.jacobianProviders = new ArrayList<ParameterJacobianProvider>();
        this.matricesData = new double[(this.stateDim + this.paramDim) * this.stateDim];
        for (int i = 0; i < this.stateDim; ++i) {
            this.matricesData[i * (this.stateDim + 1)] = 1.0;
        }
    }
    
    public void registerVariationalEquations(final ExpandableStatefulODE expandable) throws DimensionMismatchException, MismatchedEquations {
        final FirstOrderDifferentialEquations ode = (this.jode instanceof MainStateJacobianWrapper) ? ((MainStateJacobianWrapper)this.jode).ode : this.jode;
        if (expandable.getPrimary() != ode) {
            throw new MismatchedEquations();
        }
        this.efode = expandable;
        this.index = this.efode.addSecondaryEquations(new JacobiansSecondaryEquations());
        this.efode.setSecondaryState(this.index, this.matricesData);
    }
    
    public void addParameterJacobianProvider(final ParameterJacobianProvider provider) {
        this.jacobianProviders.add(provider);
    }
    
    public void setParameterizedODE(final ParameterizedODE parameterizedOde) {
        this.pode = parameterizedOde;
        this.dirtyParameter = true;
    }
    
    public void setParameterStep(final String parameter, final double hP) throws UnknownParameterException {
        for (final ParameterConfiguration param : this.selectedParameters) {
            if (parameter.equals(param.getParameterName())) {
                param.setHP(hP);
                this.dirtyParameter = true;
                return;
            }
        }
        throw new UnknownParameterException(parameter);
    }
    
    public void setInitialMainStateJacobian(final double[][] dYdY0) throws DimensionMismatchException {
        this.checkDimension(this.stateDim, dYdY0);
        this.checkDimension(this.stateDim, dYdY0[0]);
        int i = 0;
        for (final double[] row : dYdY0) {
            System.arraycopy(row, 0, this.matricesData, i, this.stateDim);
            i += this.stateDim;
        }
        if (this.efode != null) {
            this.efode.setSecondaryState(this.index, this.matricesData);
        }
    }
    
    public void setInitialParameterJacobian(final String pName, final double[] dYdP) throws UnknownParameterException, DimensionMismatchException {
        this.checkDimension(this.stateDim, dYdP);
        int i = this.stateDim * this.stateDim;
        for (final ParameterConfiguration param : this.selectedParameters) {
            if (pName.equals(param.getParameterName())) {
                System.arraycopy(dYdP, 0, this.matricesData, i, this.stateDim);
                if (this.efode != null) {
                    this.efode.setSecondaryState(this.index, this.matricesData);
                }
                return;
            }
            i += this.stateDim;
        }
        throw new UnknownParameterException(pName);
    }
    
    public void getCurrentMainSetJacobian(final double[][] dYdY0) {
        final double[] p = this.efode.getSecondaryState(this.index);
        int j = 0;
        for (int i = 0; i < this.stateDim; ++i) {
            System.arraycopy(p, j, dYdY0[i], 0, this.stateDim);
            j += this.stateDim;
        }
    }
    
    public void getCurrentParameterJacobian(final String pName, final double[] dYdP) {
        final double[] p = this.efode.getSecondaryState(this.index);
        int i = this.stateDim * this.stateDim;
        for (final ParameterConfiguration param : this.selectedParameters) {
            if (param.getParameterName().equals(pName)) {
                System.arraycopy(p, i, dYdP, 0, this.stateDim);
                return;
            }
            i += this.stateDim;
        }
    }
    
    private void checkDimension(final int expected, final Object array) throws DimensionMismatchException {
        final int arrayDimension = (array == null) ? 0 : Array.getLength(array);
        if (arrayDimension != expected) {
            throw new DimensionMismatchException(arrayDimension, expected);
        }
    }
    
    private class JacobiansSecondaryEquations implements SecondaryEquations
    {
        public int getDimension() {
            return JacobianMatrices.this.stateDim * (JacobianMatrices.this.stateDim + JacobianMatrices.this.paramDim);
        }
        
        public void computeDerivatives(final double t, final double[] y, final double[] yDot, final double[] z, final double[] zDot) throws MaxCountExceededException, DimensionMismatchException {
            if (JacobianMatrices.this.dirtyParameter && JacobianMatrices.this.paramDim != 0) {
                JacobianMatrices.this.jacobianProviders.add(new ParameterJacobianWrapper(JacobianMatrices.this.jode, JacobianMatrices.this.pode, JacobianMatrices.this.selectedParameters));
                JacobianMatrices.this.dirtyParameter = false;
            }
            final double[][] dFdY = new double[JacobianMatrices.this.stateDim][JacobianMatrices.this.stateDim];
            JacobianMatrices.this.jode.computeMainStateJacobian(t, y, yDot, dFdY);
            for (int i = 0; i < JacobianMatrices.this.stateDim; ++i) {
                final double[] dFdYi = dFdY[i];
                for (int j = 0; j < JacobianMatrices.this.stateDim; ++j) {
                    double s = 0.0;
                    int zIndex;
                    final int startIndex = zIndex = j;
                    for (int l = 0; l < JacobianMatrices.this.stateDim; ++l) {
                        s += dFdYi[l] * z[zIndex];
                        zIndex += JacobianMatrices.this.stateDim;
                    }
                    zDot[startIndex + i * JacobianMatrices.this.stateDim] = s;
                }
            }
            if (JacobianMatrices.this.paramDim != 0) {
                final double[] dFdP = new double[JacobianMatrices.this.stateDim];
                int startIndex2 = JacobianMatrices.this.stateDim * JacobianMatrices.this.stateDim;
                for (final ParameterConfiguration param : JacobianMatrices.this.selectedParameters) {
                    boolean found = false;
                    for (int k = 0; !found && k < JacobianMatrices.this.jacobianProviders.size(); ++k) {
                        final ParameterJacobianProvider provider = JacobianMatrices.this.jacobianProviders.get(k);
                        if (provider.isSupported(param.getParameterName())) {
                            provider.computeParameterJacobian(t, y, yDot, param.getParameterName(), dFdP);
                            for (int m = 0; m < JacobianMatrices.this.stateDim; ++m) {
                                final double[] dFdYi2 = dFdY[m];
                                int zIndex2 = startIndex2;
                                double s2 = dFdP[m];
                                for (int l2 = 0; l2 < JacobianMatrices.this.stateDim; ++l2) {
                                    s2 += dFdYi2[l2] * z[zIndex2];
                                    ++zIndex2;
                                }
                                zDot[startIndex2 + m] = s2;
                            }
                            found = true;
                        }
                    }
                    if (!found) {
                        Arrays.fill(zDot, startIndex2, startIndex2 + JacobianMatrices.this.stateDim, 0.0);
                    }
                    startIndex2 += JacobianMatrices.this.stateDim;
                }
            }
        }
    }
    
    private static class MainStateJacobianWrapper implements MainStateJacobianProvider
    {
        private final FirstOrderDifferentialEquations ode;
        private final double[] hY;
        
        MainStateJacobianWrapper(final FirstOrderDifferentialEquations ode, final double[] hY) throws DimensionMismatchException {
            this.ode = ode;
            this.hY = hY.clone();
            if (hY.length != ode.getDimension()) {
                throw new DimensionMismatchException(ode.getDimension(), hY.length);
            }
        }
        
        public int getDimension() {
            return this.ode.getDimension();
        }
        
        public void computeDerivatives(final double t, final double[] y, final double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
            this.ode.computeDerivatives(t, y, yDot);
        }
        
        public void computeMainStateJacobian(final double t, final double[] y, final double[] yDot, final double[][] dFdY) throws MaxCountExceededException, DimensionMismatchException {
            final int n = this.ode.getDimension();
            final double[] tmpDot = new double[n];
            for (int j = 0; j < n; ++j) {
                final double savedYj = y[j];
                final int n2 = j;
                y[n2] += this.hY[j];
                this.ode.computeDerivatives(t, y, tmpDot);
                for (int i = 0; i < n; ++i) {
                    dFdY[i][j] = (tmpDot[i] - yDot[i]) / this.hY[j];
                }
                y[j] = savedYj;
            }
        }
    }
    
    public static class MismatchedEquations extends MathIllegalArgumentException
    {
        private static final long serialVersionUID = 20120902L;
        
        public MismatchedEquations() {
            super(LocalizedFormats.UNMATCHED_ODE_IN_EXPANDED_SET, new Object[0]);
        }
    }
}
