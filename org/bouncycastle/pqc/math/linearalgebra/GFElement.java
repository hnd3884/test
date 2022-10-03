package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;

public interface GFElement
{
    Object clone();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    boolean isZero();
    
    boolean isOne();
    
    GFElement add(final GFElement p0) throws RuntimeException;
    
    void addToThis(final GFElement p0) throws RuntimeException;
    
    GFElement subtract(final GFElement p0) throws RuntimeException;
    
    void subtractFromThis(final GFElement p0);
    
    GFElement multiply(final GFElement p0) throws RuntimeException;
    
    void multiplyThisBy(final GFElement p0) throws RuntimeException;
    
    GFElement invert() throws ArithmeticException;
    
    BigInteger toFlexiBigInt();
    
    byte[] toByteArray();
    
    String toString();
    
    String toString(final int p0);
}
