package org.bouncycastle.pqc.math.linearalgebra;

public abstract class Matrix
{
    protected int numRows;
    protected int numColumns;
    public static final char MATRIX_TYPE_ZERO = 'Z';
    public static final char MATRIX_TYPE_UNIT = 'I';
    public static final char MATRIX_TYPE_RANDOM_LT = 'L';
    public static final char MATRIX_TYPE_RANDOM_UT = 'U';
    public static final char MATRIX_TYPE_RANDOM_REGULAR = 'R';
    
    public int getNumRows() {
        return this.numRows;
    }
    
    public int getNumColumns() {
        return this.numColumns;
    }
    
    public abstract byte[] getEncoded();
    
    public abstract Matrix computeInverse();
    
    public abstract boolean isZero();
    
    public abstract Matrix rightMultiply(final Matrix p0);
    
    public abstract Matrix rightMultiply(final Permutation p0);
    
    public abstract Vector leftMultiply(final Vector p0);
    
    public abstract Vector rightMultiply(final Vector p0);
    
    @Override
    public abstract String toString();
}
