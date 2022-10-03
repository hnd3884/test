package org.bouncycastle.pqc.crypto.rainbow;

public class RainbowPublicKeyParameters extends RainbowKeyParameters
{
    private short[][] coeffquadratic;
    private short[][] coeffsingular;
    private short[] coeffscalar;
    
    public RainbowPublicKeyParameters(final int n, final short[][] coeffquadratic, final short[][] coeffsingular, final short[] coeffscalar) {
        super(false, n);
        this.coeffquadratic = coeffquadratic;
        this.coeffsingular = coeffsingular;
        this.coeffscalar = coeffscalar;
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
