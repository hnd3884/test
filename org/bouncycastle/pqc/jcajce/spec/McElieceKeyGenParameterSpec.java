package org.bouncycastle.pqc.jcajce.spec;

import java.security.InvalidParameterException;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialRingGF2;
import java.security.spec.AlgorithmParameterSpec;

public class McElieceKeyGenParameterSpec implements AlgorithmParameterSpec
{
    public static final int DEFAULT_M = 11;
    public static final int DEFAULT_T = 50;
    private int m;
    private int t;
    private int n;
    private int fieldPoly;
    
    public McElieceKeyGenParameterSpec() {
        this(11, 50);
    }
    
    public McElieceKeyGenParameterSpec(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException("key size must be positive");
        }
        this.m = 0;
        this.n = 1;
        while (this.n < n) {
            this.n <<= 1;
            ++this.m;
        }
        this.t = this.n >>> 1;
        this.t /= this.m;
        this.fieldPoly = PolynomialRingGF2.getIrreduciblePolynomial(this.m);
    }
    
    public McElieceKeyGenParameterSpec(final int m, final int t) throws InvalidParameterException {
        if (m < 1) {
            throw new IllegalArgumentException("m must be positive");
        }
        if (m > 32) {
            throw new IllegalArgumentException("m is too large");
        }
        this.m = m;
        this.n = 1 << m;
        if (t < 0) {
            throw new IllegalArgumentException("t must be positive");
        }
        if (t > this.n) {
            throw new IllegalArgumentException("t must be less than n = 2^m");
        }
        this.t = t;
        this.fieldPoly = PolynomialRingGF2.getIrreduciblePolynomial(m);
    }
    
    public McElieceKeyGenParameterSpec(final int m, final int t, final int fieldPoly) {
        this.m = m;
        if (m < 1) {
            throw new IllegalArgumentException("m must be positive");
        }
        if (m > 32) {
            throw new IllegalArgumentException(" m is too large");
        }
        this.n = 1 << m;
        if ((this.t = t) < 0) {
            throw new IllegalArgumentException("t must be positive");
        }
        if (t > this.n) {
            throw new IllegalArgumentException("t must be less than n = 2^m");
        }
        if (PolynomialRingGF2.degree(fieldPoly) == m && PolynomialRingGF2.isIrreducible(fieldPoly)) {
            this.fieldPoly = fieldPoly;
            return;
        }
        throw new IllegalArgumentException("polynomial is not a field polynomial for GF(2^m)");
    }
    
    public int getM() {
        return this.m;
    }
    
    public int getN() {
        return this.n;
    }
    
    public int getT() {
        return this.t;
    }
    
    public int getFieldPoly() {
        return this.fieldPoly;
    }
}
