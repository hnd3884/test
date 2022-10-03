package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.RealFieldElement;

public interface FieldSecondaryEquations<T extends RealFieldElement<T>>
{
    int getDimension();
    
    void init(final T p0, final T[] p1, final T[] p2, final T p3);
    
    T[] computeDerivatives(final T p0, final T[] p1, final T[] p2, final T[] p3) throws MaxCountExceededException, DimensionMismatchException;
}
