package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class CramerShoupPublicKeyParameters extends CramerShoupKeyParameters
{
    private BigInteger c;
    private BigInteger d;
    private BigInteger h;
    
    public CramerShoupPublicKeyParameters(final CramerShoupParameters cramerShoupParameters, final BigInteger c, final BigInteger d, final BigInteger h) {
        super(false, cramerShoupParameters);
        this.c = c;
        this.d = d;
        this.h = h;
    }
    
    public BigInteger getC() {
        return this.c;
    }
    
    public BigInteger getD() {
        return this.d;
    }
    
    public BigInteger getH() {
        return this.h;
    }
    
    @Override
    public int hashCode() {
        return this.c.hashCode() ^ this.d.hashCode() ^ this.h.hashCode() ^ super.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof CramerShoupPublicKeyParameters)) {
            return false;
        }
        final CramerShoupPublicKeyParameters cramerShoupPublicKeyParameters = (CramerShoupPublicKeyParameters)o;
        return cramerShoupPublicKeyParameters.getC().equals(this.c) && cramerShoupPublicKeyParameters.getD().equals(this.d) && cramerShoupPublicKeyParameters.getH().equals(this.h) && super.equals(o);
    }
}
