package cryptix.jce.provider.elgamal;

import java.math.BigInteger;
import cryptix.jce.ElGamalParams;

final class ElGamalParamsCryptix implements ElGamalParams
{
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger g;
    
    public BigInteger getP() {
        return this.p;
    }
    
    public BigInteger getQ() {
        return this.q;
    }
    
    public BigInteger getG() {
        return this.g;
    }
    
    ElGamalParamsCryptix(final BigInteger p, final BigInteger q, final BigInteger g) {
        this.p = p;
        this.q = q;
        this.g = g;
    }
}
