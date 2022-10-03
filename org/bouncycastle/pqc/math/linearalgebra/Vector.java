package org.bouncycastle.pqc.math.linearalgebra;

public abstract class Vector
{
    protected int length;
    
    public final int getLength() {
        return this.length;
    }
    
    public abstract byte[] getEncoded();
    
    public abstract boolean isZero();
    
    public abstract Vector add(final Vector p0);
    
    public abstract Vector multiply(final Permutation p0);
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public abstract int hashCode();
    
    @Override
    public abstract String toString();
}
