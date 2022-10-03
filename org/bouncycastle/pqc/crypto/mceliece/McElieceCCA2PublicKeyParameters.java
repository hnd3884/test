package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.pqc.math.linearalgebra.GF2Matrix;

public class McElieceCCA2PublicKeyParameters extends McElieceCCA2KeyParameters
{
    private int n;
    private int t;
    private GF2Matrix matrixG;
    
    public McElieceCCA2PublicKeyParameters(final int n, final int t, final GF2Matrix gf2Matrix, final String s) {
        super(false, s);
        this.n = n;
        this.t = t;
        this.matrixG = new GF2Matrix(gf2Matrix);
    }
    
    public int getN() {
        return this.n;
    }
    
    public int getT() {
        return this.t;
    }
    
    public GF2Matrix getG() {
        return this.matrixG;
    }
    
    public int getK() {
        return this.matrixG.getNumRows();
    }
}
