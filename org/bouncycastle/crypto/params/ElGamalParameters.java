package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class ElGamalParameters implements CipherParameters
{
    private BigInteger g;
    private BigInteger p;
    private int l;
    
    public ElGamalParameters(final BigInteger bigInteger, final BigInteger bigInteger2) {
        this(bigInteger, bigInteger2, 0);
    }
    
    public ElGamalParameters(final BigInteger p3, final BigInteger g, final int l) {
        this.g = g;
        this.p = p3;
        this.l = l;
    }
    
    public BigInteger getP() {
        return this.p;
    }
    
    public BigInteger getG() {
        return this.g;
    }
    
    public int getL() {
        return this.l;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ElGamalParameters)) {
            return false;
        }
        final ElGamalParameters elGamalParameters = (ElGamalParameters)o;
        return elGamalParameters.getP().equals(this.p) && elGamalParameters.getG().equals(this.g) && elGamalParameters.getL() == this.l;
    }
    
    @Override
    public int hashCode() {
        return (this.getP().hashCode() ^ this.getG().hashCode()) + this.l;
    }
}
