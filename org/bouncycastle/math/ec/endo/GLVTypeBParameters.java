package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;

public class GLVTypeBParameters
{
    protected final BigInteger beta;
    protected final BigInteger lambda;
    protected final BigInteger v1A;
    protected final BigInteger v1B;
    protected final BigInteger v2A;
    protected final BigInteger v2B;
    protected final BigInteger g1;
    protected final BigInteger g2;
    protected final int bits;
    
    private static void checkVector(final BigInteger[] array, final String s) {
        if (array == null || array.length != 2 || array[0] == null || array[1] == null) {
            throw new IllegalArgumentException("'" + s + "' must consist of exactly 2 (non-null) values");
        }
    }
    
    public GLVTypeBParameters(final BigInteger beta, final BigInteger lambda, final BigInteger[] array, final BigInteger[] array2, final BigInteger g1, final BigInteger g2, final int bits) {
        checkVector(array, "v1");
        checkVector(array2, "v2");
        this.beta = beta;
        this.lambda = lambda;
        this.v1A = array[0];
        this.v1B = array[1];
        this.v2A = array2[0];
        this.v2B = array2[1];
        this.g1 = g1;
        this.g2 = g2;
        this.bits = bits;
    }
    
    public BigInteger getBeta() {
        return this.beta;
    }
    
    public BigInteger getLambda() {
        return this.lambda;
    }
    
    @Deprecated
    public BigInteger[] getV1() {
        return new BigInteger[] { this.v1A, this.v1B };
    }
    
    public BigInteger getV1A() {
        return this.v1A;
    }
    
    public BigInteger getV1B() {
        return this.v1B;
    }
    
    @Deprecated
    public BigInteger[] getV2() {
        return new BigInteger[] { this.v2A, this.v2B };
    }
    
    public BigInteger getV2A() {
        return this.v2A;
    }
    
    public BigInteger getV2B() {
        return this.v2B;
    }
    
    public BigInteger getG1() {
        return this.g1;
    }
    
    public BigInteger getG2() {
        return this.g2;
    }
    
    public int getBits() {
        return this.bits;
    }
}
