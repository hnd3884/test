package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public interface FieldVector<T extends FieldElement<T>>
{
    Field<T> getField();
    
    FieldVector<T> copy();
    
    FieldVector<T> add(final FieldVector<T> p0) throws DimensionMismatchException;
    
    FieldVector<T> subtract(final FieldVector<T> p0) throws DimensionMismatchException;
    
    FieldVector<T> mapAdd(final T p0) throws NullArgumentException;
    
    FieldVector<T> mapAddToSelf(final T p0) throws NullArgumentException;
    
    FieldVector<T> mapSubtract(final T p0) throws NullArgumentException;
    
    FieldVector<T> mapSubtractToSelf(final T p0) throws NullArgumentException;
    
    FieldVector<T> mapMultiply(final T p0) throws NullArgumentException;
    
    FieldVector<T> mapMultiplyToSelf(final T p0) throws NullArgumentException;
    
    FieldVector<T> mapDivide(final T p0) throws NullArgumentException, MathArithmeticException;
    
    FieldVector<T> mapDivideToSelf(final T p0) throws NullArgumentException, MathArithmeticException;
    
    FieldVector<T> mapInv() throws MathArithmeticException;
    
    FieldVector<T> mapInvToSelf() throws MathArithmeticException;
    
    FieldVector<T> ebeMultiply(final FieldVector<T> p0) throws DimensionMismatchException;
    
    FieldVector<T> ebeDivide(final FieldVector<T> p0) throws DimensionMismatchException, MathArithmeticException;
    
    @Deprecated
    T[] getData();
    
    T dotProduct(final FieldVector<T> p0) throws DimensionMismatchException;
    
    FieldVector<T> projection(final FieldVector<T> p0) throws DimensionMismatchException, MathArithmeticException;
    
    FieldMatrix<T> outerProduct(final FieldVector<T> p0);
    
    T getEntry(final int p0) throws OutOfRangeException;
    
    void setEntry(final int p0, final T p1) throws OutOfRangeException;
    
    int getDimension();
    
    FieldVector<T> append(final FieldVector<T> p0);
    
    FieldVector<T> append(final T p0);
    
    FieldVector<T> getSubVector(final int p0, final int p1) throws OutOfRangeException, NotPositiveException;
    
    void setSubVector(final int p0, final FieldVector<T> p1) throws OutOfRangeException;
    
    void set(final T p0);
    
    T[] toArray();
}
