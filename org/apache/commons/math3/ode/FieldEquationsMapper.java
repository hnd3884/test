package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.util.MathArrays;
import java.io.Serializable;
import org.apache.commons.math3.RealFieldElement;

public class FieldEquationsMapper<T extends RealFieldElement<T>> implements Serializable
{
    private static final long serialVersionUID = 20151114L;
    private final int[] start;
    
    FieldEquationsMapper(final FieldEquationsMapper<T> mapper, final int dimension) {
        final int index = (mapper == null) ? 0 : mapper.getNumberOfEquations();
        this.start = new int[index + 2];
        if (mapper == null) {
            this.start[0] = 0;
        }
        else {
            System.arraycopy(mapper.start, 0, this.start, 0, index + 1);
        }
        this.start[index + 1] = this.start[index] + dimension;
    }
    
    public int getNumberOfEquations() {
        return this.start.length - 1;
    }
    
    public int getTotalDimension() {
        return this.start[this.start.length - 1];
    }
    
    public T[] mapState(final FieldODEState<T> state) {
        final T[] y = MathArrays.buildArray(state.getTime().getField(), this.getTotalDimension());
        int index = 0;
        this.insertEquationData(index, state.getState(), y);
        while (++index < this.getNumberOfEquations()) {
            this.insertEquationData(index, state.getSecondaryState(index), y);
        }
        return y;
    }
    
    public T[] mapDerivative(final FieldODEStateAndDerivative<T> state) {
        final T[] yDot = MathArrays.buildArray((Field<T>)state.getTime().getField(), this.getTotalDimension());
        int index = 0;
        this.insertEquationData(index, state.getDerivative(), yDot);
        while (++index < this.getNumberOfEquations()) {
            this.insertEquationData(index, state.getSecondaryDerivative(index), yDot);
        }
        return yDot;
    }
    
    public FieldODEStateAndDerivative<T> mapStateAndDerivative(final T t, final T[] y, final T[] yDot) throws DimensionMismatchException {
        if (y.length != this.getTotalDimension()) {
            throw new DimensionMismatchException(y.length, this.getTotalDimension());
        }
        if (yDot.length != this.getTotalDimension()) {
            throw new DimensionMismatchException(yDot.length, this.getTotalDimension());
        }
        final int n = this.getNumberOfEquations();
        int index = 0;
        final T[] state = this.extractEquationData(index, y);
        final T[] derivative = this.extractEquationData(index, yDot);
        if (n < 2) {
            return new FieldODEStateAndDerivative<T>(t, state, derivative);
        }
        final T[][] secondaryState = MathArrays.buildArray(t.getField(), n - 1, -1);
        final T[][] secondaryDerivative = MathArrays.buildArray(t.getField(), n - 1, -1);
        while (++index < this.getNumberOfEquations()) {
            secondaryState[index - 1] = this.extractEquationData(index, y);
            secondaryDerivative[index - 1] = this.extractEquationData(index, yDot);
        }
        return new FieldODEStateAndDerivative<T>(t, state, derivative, secondaryState, secondaryDerivative);
    }
    
    public T[] extractEquationData(final int index, final T[] complete) throws MathIllegalArgumentException, DimensionMismatchException {
        this.checkIndex(index);
        final int begin = this.start[index];
        final int end = this.start[index + 1];
        if (complete.length < end) {
            throw new DimensionMismatchException(complete.length, end);
        }
        final int dimension = end - begin;
        final T[] equationData = MathArrays.buildArray(complete[0].getField(), dimension);
        System.arraycopy(complete, begin, equationData, 0, dimension);
        return equationData;
    }
    
    public void insertEquationData(final int index, final T[] equationData, final T[] complete) throws DimensionMismatchException {
        this.checkIndex(index);
        final int begin = this.start[index];
        final int end = this.start[index + 1];
        final int dimension = end - begin;
        if (complete.length < end) {
            throw new DimensionMismatchException(complete.length, end);
        }
        if (equationData.length != dimension) {
            throw new DimensionMismatchException(equationData.length, dimension);
        }
        System.arraycopy(equationData, 0, complete, begin, dimension);
    }
    
    private void checkIndex(final int index) throws MathIllegalArgumentException {
        if (index < 0 || index > this.start.length - 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.ARGUMENT_OUTSIDE_DOMAIN, new Object[] { index, 0, this.start.length - 2 });
        }
    }
}
