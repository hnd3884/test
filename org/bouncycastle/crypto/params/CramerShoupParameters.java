package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.Digest;
import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class CramerShoupParameters implements CipherParameters
{
    private BigInteger p;
    private BigInteger g1;
    private BigInteger g2;
    private Digest H;
    
    public CramerShoupParameters(final BigInteger p4, final BigInteger g1, final BigInteger g2, final Digest h) {
        this.p = p4;
        this.g1 = g1;
        this.g2 = g2;
        this.H = h;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DSAParameters)) {
            return false;
        }
        final CramerShoupParameters cramerShoupParameters = (CramerShoupParameters)o;
        return cramerShoupParameters.getP().equals(this.p) && cramerShoupParameters.getG1().equals(this.g1) && cramerShoupParameters.getG2().equals(this.g2);
    }
    
    @Override
    public int hashCode() {
        return this.getP().hashCode() ^ this.getG1().hashCode() ^ this.getG2().hashCode();
    }
    
    public BigInteger getG1() {
        return this.g1;
    }
    
    public BigInteger getG2() {
        return this.g2;
    }
    
    public BigInteger getP() {
        return this.p;
    }
    
    public Digest getH() {
        this.H.reset();
        return this.H;
    }
}
