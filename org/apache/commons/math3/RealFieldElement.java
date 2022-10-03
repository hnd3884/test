package org.apache.commons.math3;

import org.apache.commons.math3.exception.DimensionMismatchException;

public interface RealFieldElement<T> extends FieldElement<T>
{
    double getReal();
    
    T add(final double p0);
    
    T subtract(final double p0);
    
    T multiply(final double p0);
    
    T divide(final double p0);
    
    T remainder(final double p0);
    
    T remainder(final T p0) throws DimensionMismatchException;
    
    T abs();
    
    T ceil();
    
    T floor();
    
    T rint();
    
    long round();
    
    T signum();
    
    T copySign(final T p0);
    
    T copySign(final double p0);
    
    T scalb(final int p0);
    
    T hypot(final T p0) throws DimensionMismatchException;
    
    T reciprocal();
    
    T sqrt();
    
    T cbrt();
    
    T rootN(final int p0);
    
    T pow(final double p0);
    
    T pow(final int p0);
    
    T pow(final T p0) throws DimensionMismatchException;
    
    T exp();
    
    T expm1();
    
    T log();
    
    T log1p();
    
    T cos();
    
    T sin();
    
    T tan();
    
    T acos();
    
    T asin();
    
    T atan();
    
    T atan2(final T p0) throws DimensionMismatchException;
    
    T cosh();
    
    T sinh();
    
    T tanh();
    
    T acosh();
    
    T asinh();
    
    T atanh();
    
    T linearCombination(final T[] p0, final T[] p1) throws DimensionMismatchException;
    
    T linearCombination(final double[] p0, final T[] p1) throws DimensionMismatchException;
    
    T linearCombination(final T p0, final T p1, final T p2, final T p3);
    
    T linearCombination(final double p0, final T p1, final double p2, final T p3);
    
    T linearCombination(final T p0, final T p1, final T p2, final T p3, final T p4, final T p5);
    
    T linearCombination(final double p0, final T p1, final double p2, final T p3, final double p4, final T p5);
    
    T linearCombination(final T p0, final T p1, final T p2, final T p3, final T p4, final T p5, final T p6, final T p7);
    
    T linearCombination(final double p0, final T p1, final double p2, final T p3, final double p4, final T p5, final double p6, final T p7);
}
