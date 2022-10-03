package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.pqc.math.linearalgebra.PolynomialRingGF2m;
import org.bouncycastle.pqc.math.linearalgebra.GoppaCode;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;

public class McElieceCCA2PrivateKeyParameters extends McElieceCCA2KeyParameters
{
    private int n;
    private int k;
    private GF2mField field;
    private PolynomialGF2mSmallM goppaPoly;
    private Permutation p;
    private GF2Matrix h;
    private PolynomialGF2mSmallM[] qInv;
    
    public McElieceCCA2PrivateKeyParameters(final int n, final int n2, final GF2mField gf2mField, final PolynomialGF2mSmallM polynomialGF2mSmallM, final Permutation permutation, final String s) {
        this(n, n2, gf2mField, polynomialGF2mSmallM, GoppaCode.createCanonicalCheckMatrix(gf2mField, polynomialGF2mSmallM), permutation, s);
    }
    
    public McElieceCCA2PrivateKeyParameters(final int n, final int k, final GF2mField field, final PolynomialGF2mSmallM goppaPoly, final GF2Matrix h, final Permutation p7, final String s) {
        super(true, s);
        this.n = n;
        this.k = k;
        this.field = field;
        this.goppaPoly = goppaPoly;
        this.h = h;
        this.p = p7;
        this.qInv = new PolynomialRingGF2m(field, goppaPoly).getSquareRootMatrix();
    }
    
    public int getN() {
        return this.n;
    }
    
    public int getK() {
        return this.k;
    }
    
    public int getT() {
        return this.goppaPoly.getDegree();
    }
    
    public GF2mField getField() {
        return this.field;
    }
    
    public PolynomialGF2mSmallM getGoppaPoly() {
        return this.goppaPoly;
    }
    
    public Permutation getP() {
        return this.p;
    }
    
    public GF2Matrix getH() {
        return this.h;
    }
    
    public PolynomialGF2mSmallM[] getQInv() {
        return this.qInv;
    }
}
