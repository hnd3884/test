package org.apache.commons.math3;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NullArgumentException;

public interface FieldElement<T>
{
    T add(final T p0) throws NullArgumentException;
    
    T subtract(final T p0) throws NullArgumentException;
    
    T negate();
    
    T multiply(final int p0);
    
    T multiply(final T p0) throws NullArgumentException;
    
    T divide(final T p0) throws NullArgumentException, MathArithmeticException;
    
    T reciprocal() throws MathArithmeticException;
    
    Field<T> getField();
}
