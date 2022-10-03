package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.pqc.math.linearalgebra.PolynomialRingGF2m;
import org.bouncycastle.pqc.math.linearalgebra.GoppaCode;
import org.bouncycastle.pqc.math.linearalgebra.Permutation;
import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;
import org.bouncycastle.pqc.math.linearalgebra.PolynomialGF2mSmallM;
import org.bouncycastle.pqc.math.linearalgebra.GF2mField;

public class McEliecePrivateKeyParameters extends McElieceKeyParameters
{
    private String oid;
    private int n;
    private int k;
    private GF2mField field;
    private PolynomialGF2mSmallM goppaPoly;
    private GF2Matrix sInv;
    private Permutation p1;
    private Permutation p2;
    private GF2Matrix h;
    private PolynomialGF2mSmallM[] qInv;
    
    public McEliecePrivateKeyParameters(final int n, final int k, final GF2mField field, final PolynomialGF2mSmallM goppaPoly, final Permutation p7, final Permutation p8, final GF2Matrix sInv) {
        super(true, null);
        this.k = k;
        this.n = n;
        this.field = field;
        this.goppaPoly = goppaPoly;
        this.sInv = sInv;
        this.p1 = p7;
        this.p2 = p8;
        this.h = GoppaCode.createCanonicalCheckMatrix(field, goppaPoly);
        this.qInv = new PolynomialRingGF2m(field, goppaPoly).getSquareRootMatrix();
    }
    
    public McEliecePrivateKeyParameters(final int n, final int k, final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4, final byte[] array5, final byte[] array6, final byte[][] array7) {
        super(true, null);
        this.n = n;
        this.k = k;
        this.field = new GF2mField(array);
        this.goppaPoly = new PolynomialGF2mSmallM(this.field, array2);
        this.sInv = new GF2Matrix(array3);
        this.p1 = new Permutation(array4);
        this.p2 = new Permutation(array5);
        this.h = new GF2Matrix(array6);
        this.qInv = new PolynomialGF2mSmallM[array7.length];
        for (int i = 0; i < array7.length; ++i) {
            this.qInv[i] = new PolynomialGF2mSmallM(this.field, array7[i]);
        }
    }
    
    public int getN() {
        return this.n;
    }
    
    public int getK() {
        return this.k;
    }
    
    public GF2mField getField() {
        return this.field;
    }
    
    public PolynomialGF2mSmallM getGoppaPoly() {
        return this.goppaPoly;
    }
    
    public GF2Matrix getSInv() {
        return this.sInv;
    }
    
    public Permutation getP1() {
        return this.p1;
    }
    
    public Permutation getP2() {
        return this.p2;
    }
    
    public GF2Matrix getH() {
        return this.h;
    }
    
    public PolynomialGF2mSmallM[] getQInv() {
        return this.qInv;
    }
}
