package org.bouncycastle.jce.spec;

import java.math.BigInteger;

public class GOST3410PublicKeyParameterSetSpec
{
    private BigInteger p;
    private BigInteger q;
    private BigInteger a;
    
    public GOST3410PublicKeyParameterSetSpec(final BigInteger p3, final BigInteger q, final BigInteger a) {
        this.p = p3;
        this.q = q;
        this.a = a;
    }
    
    public BigInteger getP() {
        return this.p;
    }
    
    public BigInteger getQ() {
        return this.q;
    }
    
    public BigInteger getA() {
        return this.a;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof GOST3410PublicKeyParameterSetSpec) {
            final GOST3410PublicKeyParameterSetSpec gost3410PublicKeyParameterSetSpec = (GOST3410PublicKeyParameterSetSpec)o;
            return this.a.equals(gost3410PublicKeyParameterSetSpec.a) && this.p.equals(gost3410PublicKeyParameterSetSpec.p) && this.q.equals(gost3410PublicKeyParameterSetSpec.q);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.a.hashCode() ^ this.p.hashCode() ^ this.q.hashCode();
    }
}
