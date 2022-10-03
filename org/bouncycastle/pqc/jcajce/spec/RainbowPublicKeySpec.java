package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.KeySpec;

public class RainbowPublicKeySpec implements KeySpec
{
    private short[][] coeffquadratic;
    private short[][] coeffsingular;
    private short[] coeffscalar;
    private int docLength;
    
    public RainbowPublicKeySpec(final int docLength, final short[][] coeffquadratic, final short[][] coeffsingular, final short[] coeffscalar) {
        this.docLength = docLength;
        this.coeffquadratic = coeffquadratic;
        this.coeffsingular = coeffsingular;
        this.coeffscalar = coeffscalar;
    }
    
    public int getDocLength() {
        return this.docLength;
    }
    
    public short[][] getCoeffQuadratic() {
        return this.coeffquadratic;
    }
    
    public short[][] getCoeffSingular() {
        return this.coeffsingular;
    }
    
    public short[] getCoeffScalar() {
        return this.coeffscalar;
    }
}
