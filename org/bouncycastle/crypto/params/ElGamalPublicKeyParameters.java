package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class ElGamalPublicKeyParameters extends ElGamalKeyParameters
{
    private BigInteger y;
    
    public ElGamalPublicKeyParameters(final BigInteger y, final ElGamalParameters elGamalParameters) {
        super(false, elGamalParameters);
        this.y = y;
    }
    
    public BigInteger getY() {
        return this.y;
    }
    
    @Override
    public int hashCode() {
        return this.y.hashCode() ^ super.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ElGamalPublicKeyParameters && ((ElGamalPublicKeyParameters)o).getY().equals(this.y) && super.equals(o);
    }
}
