package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class GOST3410Parameters implements CipherParameters
{
    private BigInteger p;
    private BigInteger q;
    private BigInteger a;
    private GOST3410ValidationParameters validation;
    
    public GOST3410Parameters(final BigInteger p3, final BigInteger q, final BigInteger a) {
        this.p = p3;
        this.q = q;
        this.a = a;
    }
    
    public GOST3410Parameters(final BigInteger p4, final BigInteger q, final BigInteger a, final GOST3410ValidationParameters validation) {
        this.a = a;
        this.p = p4;
        this.q = q;
        this.validation = validation;
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
    
    public GOST3410ValidationParameters getValidationParameters() {
        return this.validation;
    }
    
    @Override
    public int hashCode() {
        return this.p.hashCode() ^ this.q.hashCode() ^ this.a.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GOST3410Parameters)) {
            return false;
        }
        final GOST3410Parameters gost3410Parameters = (GOST3410Parameters)o;
        return gost3410Parameters.getP().equals(this.p) && gost3410Parameters.getQ().equals(this.q) && gost3410Parameters.getA().equals(this.a);
    }
}
