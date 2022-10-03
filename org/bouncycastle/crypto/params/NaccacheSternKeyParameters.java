package org.bouncycastle.crypto.params;

import java.math.BigInteger;

public class NaccacheSternKeyParameters extends AsymmetricKeyParameter
{
    private BigInteger g;
    private BigInteger n;
    int lowerSigmaBound;
    
    public NaccacheSternKeyParameters(final boolean b, final BigInteger g, final BigInteger n, final int lowerSigmaBound) {
        super(b);
        this.g = g;
        this.n = n;
        this.lowerSigmaBound = lowerSigmaBound;
    }
    
    public BigInteger getG() {
        return this.g;
    }
    
    public int getLowerSigmaBound() {
        return this.lowerSigmaBound;
    }
    
    public BigInteger getModulus() {
        return this.n;
    }
}
