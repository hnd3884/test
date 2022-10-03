package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;

public class DHParameters implements CipherParameters
{
    private static final int DEFAULT_MINIMUM_LENGTH = 160;
    private BigInteger g;
    private BigInteger p;
    private BigInteger q;
    private BigInteger j;
    private int m;
    private int l;
    private DHValidationParameters validation;
    
    private static int getDefaultMParam(final int n) {
        if (n == 0) {
            return 160;
        }
        return (n < 160) ? n : 160;
    }
    
    public DHParameters(final BigInteger bigInteger, final BigInteger bigInteger2) {
        this(bigInteger, bigInteger2, null, 0);
    }
    
    public DHParameters(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        this(bigInteger, bigInteger2, bigInteger3, 0);
    }
    
    public DHParameters(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final int n) {
        this(bigInteger, bigInteger2, bigInteger3, getDefaultMParam(n), n, null, null);
    }
    
    public DHParameters(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final int n, final int n2) {
        this(bigInteger, bigInteger2, bigInteger3, n, n2, null, null);
    }
    
    public DHParameters(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final DHValidationParameters dhValidationParameters) {
        this(bigInteger, bigInteger2, bigInteger3, 160, 0, bigInteger4, dhValidationParameters);
    }
    
    public DHParameters(final BigInteger p7, final BigInteger g, final BigInteger q, final int m, final int l, final BigInteger j, final DHValidationParameters validation) {
        if (l != 0) {
            if (l > p7.bitLength()) {
                throw new IllegalArgumentException("when l value specified, it must satisfy 2^(l-1) <= p");
            }
            if (l < m) {
                throw new IllegalArgumentException("when l value specified, it may not be less than m value");
            }
        }
        if (m > p7.bitLength()) {
            throw new IllegalArgumentException("unsafe p value so small specific l required");
        }
        this.g = g;
        this.p = p7;
        this.q = q;
        this.m = m;
        this.l = l;
        this.j = j;
        this.validation = validation;
    }
    
    public BigInteger getP() {
        return this.p;
    }
    
    public BigInteger getG() {
        return this.g;
    }
    
    public BigInteger getQ() {
        return this.q;
    }
    
    public BigInteger getJ() {
        return this.j;
    }
    
    public int getM() {
        return this.m;
    }
    
    public int getL() {
        return this.l;
    }
    
    public DHValidationParameters getValidationParameters() {
        return this.validation;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DHParameters)) {
            return false;
        }
        final DHParameters dhParameters = (DHParameters)o;
        if (this.getQ() != null) {
            if (!this.getQ().equals(dhParameters.getQ())) {
                return false;
            }
        }
        else if (dhParameters.getQ() != null) {
            return false;
        }
        return dhParameters.getP().equals(this.p) && dhParameters.getG().equals(this.g);
    }
    
    @Override
    public int hashCode() {
        return this.getP().hashCode() ^ this.getG().hashCode() ^ ((this.getQ() != null) ? this.getQ().hashCode() : 0);
    }
}
