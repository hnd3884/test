package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class CramerShoupPrivateKeyParameters extends CramerShoupKeyParameters
{
    private BigInteger x1;
    private BigInteger x2;
    private BigInteger y1;
    private BigInteger y2;
    private BigInteger z;
    private CramerShoupPublicKeyParameters pk;
    
    public CramerShoupPrivateKeyParameters(final CramerShoupParameters cramerShoupParameters, final BigInteger x1, final BigInteger x2, final BigInteger y1, final BigInteger y2, final BigInteger z) {
        super(true, cramerShoupParameters);
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z = z;
    }
    
    public BigInteger getX1() {
        return this.x1;
    }
    
    public BigInteger getX2() {
        return this.x2;
    }
    
    public BigInteger getY1() {
        return this.y1;
    }
    
    public BigInteger getY2() {
        return this.y2;
    }
    
    public BigInteger getZ() {
        return this.z;
    }
    
    public void setPk(final CramerShoupPublicKeyParameters pk) {
        this.pk = pk;
    }
    
    public CramerShoupPublicKeyParameters getPk() {
        return this.pk;
    }
    
    @Override
    public int hashCode() {
        return this.x1.hashCode() ^ this.x2.hashCode() ^ this.y1.hashCode() ^ this.y2.hashCode() ^ this.z.hashCode() ^ super.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof CramerShoupPrivateKeyParameters)) {
            return false;
        }
        final CramerShoupPrivateKeyParameters cramerShoupPrivateKeyParameters = (CramerShoupPrivateKeyParameters)o;
        return cramerShoupPrivateKeyParameters.getX1().equals(this.x1) && cramerShoupPrivateKeyParameters.getX2().equals(this.x2) && cramerShoupPrivateKeyParameters.getY1().equals(this.y1) && cramerShoupPrivateKeyParameters.getY2().equals(this.y2) && cramerShoupPrivateKeyParameters.getZ().equals(this.z) && super.equals(o);
    }
}
