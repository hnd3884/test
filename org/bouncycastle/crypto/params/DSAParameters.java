package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class DSAParameters implements CipherParameters
{
    private BigInteger g;
    private BigInteger q;
    private BigInteger p;
    private DSAValidationParameters validation;
    
    public DSAParameters(final BigInteger p3, final BigInteger q, final BigInteger g) {
        this.g = g;
        this.p = p3;
        this.q = q;
    }
    
    public DSAParameters(final BigInteger p4, final BigInteger q, final BigInteger g, final DSAValidationParameters validation) {
        this.g = g;
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
    
    public BigInteger getG() {
        return this.g;
    }
    
    public DSAValidationParameters getValidationParameters() {
        return this.validation;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DSAParameters)) {
            return false;
        }
        final DSAParameters dsaParameters = (DSAParameters)o;
        return dsaParameters.getP().equals(this.p) && dsaParameters.getQ().equals(this.q) && dsaParameters.getG().equals(this.g);
    }
    
    @Override
    public int hashCode() {
        return this.getP().hashCode() ^ this.getQ().hashCode() ^ this.getG().hashCode();
    }
}
