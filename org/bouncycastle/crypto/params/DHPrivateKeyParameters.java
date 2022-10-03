package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class DHPrivateKeyParameters extends DHKeyParameters
{
    private BigInteger x;
    
    public DHPrivateKeyParameters(final BigInteger x, final DHParameters dhParameters) {
        super(true, dhParameters);
        this.x = x;
    }
    
    public BigInteger getX() {
        return this.x;
    }
    
    @Override
    public int hashCode() {
        return this.x.hashCode() ^ super.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof DHPrivateKeyParameters && ((DHPrivateKeyParameters)o).getX().equals(this.x) && super.equals(o);
    }
}
