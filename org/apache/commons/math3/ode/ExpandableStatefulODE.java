package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class ExpandableStatefulODE
{
    private final FirstOrderDifferentialEquations primary;
    private final EquationsMapper primaryMapper;
    private double time;
    private final double[] primaryState;
    private final double[] primaryStateDot;
    private List<SecondaryComponent> components;
    
    public ExpandableStatefulODE(final FirstOrderDifferentialEquations primary) {
        final int n = primary.getDimension();
        this.primary = primary;
        this.primaryMapper = new EquationsMapper(0, n);
        this.time = Double.NaN;
        this.primaryState = new double[n];
        this.primaryStateDot = new double[n];
        this.components = new ArrayList<SecondaryComponent>();
    }
    
    public FirstOrderDifferentialEquations getPrimary() {
        return this.primary;
    }
    
    public int getTotalDimension() {
        if (this.components.isEmpty()) {
            return this.primaryMapper.getDimension();
        }
        final EquationsMapper lastMapper = this.components.get(this.components.size() - 1).mapper;
        return lastMapper.getFirstIndex() + lastMapper.getDimension();
    }
    
    public void computeDerivatives(final double t, final double[] y, final double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
        this.primaryMapper.extractEquationData(y, this.primaryState);
        this.primary.computeDerivatives(t, this.primaryState, this.primaryStateDot);
        for (final SecondaryComponent component : this.components) {
            component.mapper.extractEquationData(y, component.state);
            component.equation.computeDerivatives(t, this.primaryState, this.primaryStateDot, component.state, component.stateDot);
            component.mapper.insertEquationData(component.stateDot, yDot);
        }
        this.primaryMapper.insertEquationData(this.primaryStateDot, yDot);
    }
    
    public int addSecondaryEquations(final SecondaryEquations secondary) {
        int firstIndex;
        if (this.components.isEmpty()) {
            this.components = new ArrayList<SecondaryComponent>();
            firstIndex = this.primary.getDimension();
        }
        else {
            final SecondaryComponent last = this.components.get(this.components.size() - 1);
            firstIndex = last.mapper.getFirstIndex() + last.mapper.getDimension();
        }
        this.components.add(new SecondaryComponent(secondary, firstIndex));
        return this.components.size() - 1;
    }
    
    public EquationsMapper getPrimaryMapper() {
        return this.primaryMapper;
    }
    
    public EquationsMapper[] getSecondaryMappers() {
        final EquationsMapper[] mappers = new EquationsMapper[this.components.size()];
        for (int i = 0; i < mappers.length; ++i) {
            mappers[i] = this.components.get(i).mapper;
        }
        return mappers;
    }
    
    public void setTime(final double time) {
        this.time = time;
    }
    
    public double getTime() {
        return this.time;
    }
    
    public void setPrimaryState(final double[] primaryState) throws DimensionMismatchException {
        if (primaryState.length != this.primaryState.length) {
            throw new DimensionMismatchException(primaryState.length, this.primaryState.length);
        }
        System.arraycopy(primaryState, 0, this.primaryState, 0, primaryState.length);
    }
    
    public double[] getPrimaryState() {
        return this.primaryState.clone();
    }
    
    public double[] getPrimaryStateDot() {
        return this.primaryStateDot.clone();
    }
    
    public void setSecondaryState(final int index, final double[] secondaryState) throws DimensionMismatchException {
        final double[] localArray = this.components.get(index).state;
        if (secondaryState.length != localArray.length) {
            throw new DimensionMismatchException(secondaryState.length, localArray.length);
        }
        System.arraycopy(secondaryState, 0, localArray, 0, secondaryState.length);
    }
    
    public double[] getSecondaryState(final int index) {
        return this.components.get(index).state.clone();
    }
    
    public double[] getSecondaryStateDot(final int index) {
        return this.components.get(index).stateDot.clone();
    }
    
    public void setCompleteState(final double[] completeState) throws DimensionMismatchException {
        if (completeState.length != this.getTotalDimension()) {
            throw new DimensionMismatchException(completeState.length, this.getTotalDimension());
        }
        this.primaryMapper.extractEquationData(completeState, this.primaryState);
        for (final SecondaryComponent component : this.components) {
            component.mapper.extractEquationData(completeState, component.state);
        }
    }
    
    public double[] getCompleteState() throws DimensionMismatchException {
        final double[] completeState = new double[this.getTotalDimension()];
        this.primaryMapper.insertEquationData(this.primaryState, completeState);
        for (final SecondaryComponent component : this.components) {
            component.mapper.insertEquationData(component.state, completeState);
        }
        return completeState;
    }
    
    private static class SecondaryComponent
    {
        private final SecondaryEquations equation;
        private final EquationsMapper mapper;
        private final double[] state;
        private final double[] stateDot;
        
        SecondaryComponent(final SecondaryEquations equation, final int firstIndex) {
            final int n = equation.getDimension();
            this.equation = equation;
            this.mapper = new EquationsMapper(firstIndex, n);
            this.state = new double[n];
            this.stateDot = new double[n];
        }
    }
}
