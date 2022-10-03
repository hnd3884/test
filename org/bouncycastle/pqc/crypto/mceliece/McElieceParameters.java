package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.pqc.math.linearalgebra.PolynomialRingGF2;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.CipherParameters;

public class McElieceParameters implements CipherParameters
{
    public static final int DEFAULT_M = 11;
    public static final int DEFAULT_T = 50;
    private int m;
    private int t;
    private int n;
    private int fieldPoly;
    private Digest digest;
    
    public McElieceParameters() {
        this(11, 50);
    }
    
    public McElieceParameters(final Digest digest) {
        this(11, 50, digest);
    }
    
    public McElieceParameters(final int n) {
        this(n, null);
    }
    
    public McElieceParameters(final int n, final Digest digest) {
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
        this.digest = digest;
    }
    
    public McElieceParameters(final int n, final int n2) {
        this(n, n2, null);
    }
    
    public McElieceParameters(final int m, final int t, final Digest digest) {
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
        this.digest = digest;
    }
    
    public McElieceParameters(final int n, final int n2, final int n3) {
        this(n, n2, n3, null);
    }
    
    public McElieceParameters(final int m, final int t, final int fieldPoly, final Digest digest) {
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
            this.digest = digest;
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
