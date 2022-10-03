package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class ElGamalPrivateKeyParameters extends ElGamalKeyParameters
{
    private BigInteger x;
    
    public ElGamalPrivateKeyParameters(final BigInteger x, final ElGamalParameters elGamalParameters) {
        super(true, elGamalParameters);
        this.x = x;
    }
    
    public BigInteger getX() {
        return this.x;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof ElGamalPrivateKeyParameters && ((ElGamalPrivateKeyParameters)o).getX().equals(this.x) && super.equals(o);
    }
    
    @Override
    public int hashCode() {
        return this.getX().hashCode();
    }
}
