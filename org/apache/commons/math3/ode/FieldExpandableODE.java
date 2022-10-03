package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.util.MathArrays;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.RealFieldElement;

public class FieldExpandableODE<T extends RealFieldElement<T>>
{
    private final FirstOrderFieldDifferentialEquations<T> primary;
    private List<FieldSecondaryEquations<T>> components;
    private FieldEquationsMapper<T> mapper;
    
    public FieldExpandableODE(final FirstOrderFieldDifferentialEquations<T> primary) {
        this.primary = primary;
        this.components = new ArrayList<FieldSecondaryEquations<T>>();
        this.mapper = new FieldEquationsMapper<T>(null, primary.getDimension());
    }
    
    public FieldEquationsMapper<T> getMapper() {
        return this.mapper;
    }
    
    public int addSecondaryEquations(final FieldSecondaryEquations<T> secondary) {
        this.components.add(secondary);
        this.mapper = new FieldEquationsMapper<T>(this.mapper, secondary.getDimension());
        return this.components.size();
    }
    
    public void init(final T t0, final T[] y0, final T finalTime) {
        int index = 0;
        final T[] primary0 = this.mapper.extractEquationData(index, y0);
        this.primary.init(t0, primary0, finalTime);
        while (++index < this.mapper.getNumberOfEquations()) {
            final T[] secondary0 = this.mapper.extractEquationData(index, y0);
            this.components.get(index - 1).init(t0, primary0, secondary0, finalTime);
        }
    }
    
    public T[] computeDerivatives(final T t, final T[] y) throws MaxCountExceededException, DimensionMismatchException {
        final T[] yDot = MathArrays.buildArray(t.getField(), this.mapper.getTotalDimension());
        int index = 0;
        final T[] primaryState = this.mapper.extractEquationData(index, y);
        final T[] primaryStateDot = this.primary.computeDerivatives(t, primaryState);
        this.mapper.insertEquationData(index, primaryStateDot, yDot);
        while (++index < this.mapper.getNumberOfEquations()) {
            final T[] componentState = this.mapper.extractEquationData(index, y);
            final T[] componentStateDot = this.components.get(index - 1).computeDerivatives(t, primaryState, primaryStateDot, componentState);
            this.mapper.insertEquationData(index, componentStateDot, yDot);
        }
        return yDot;
    }
}
