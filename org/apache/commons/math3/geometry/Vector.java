package org.apache.commons.math3.geometry;

import java.text.NumberFormat;
import org.apache.commons.math3.exception.MathArithmeticException;

public interface Vector<S extends Space> extends Point<S>
{
    Vector<S> getZero();
    
    double getNorm1();
    
    double getNorm();
    
    double getNormSq();
    
    double getNormInf();
    
    Vector<S> add(final Vector<S> p0);
    
    Vector<S> add(final double p0, final Vector<S> p1);
    
    Vector<S> subtract(final Vector<S> p0);
    
    Vector<S> subtract(final double p0, final Vector<S> p1);
    
    Vector<S> negate();
    
    Vector<S> normalize() throws MathArithmeticException;
    
    Vector<S> scalarMultiply(final double p0);
    
    boolean isInfinite();
    
    double distance1(final Vector<S> p0);
    
    double distance(final Vector<S> p0);
    
    double distanceInf(final Vector<S> p0);
    
    double distanceSq(final Vector<S> p0);
    
    double dotProduct(final Vector<S> p0);
    
    String toString(final NumberFormat p0);
}
