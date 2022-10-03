package org.bouncycastle.crypto.agreement.srp;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.SRP6GroupParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import java.math.BigInteger;

public class SRP6Client
{
    protected BigInteger N;
    protected BigInteger g;
    protected BigInteger a;
    protected BigInteger A;
    protected BigInteger B;
    protected BigInteger x;
    protected BigInteger u;
    protected BigInteger S;
    protected BigInteger M1;
    protected BigInteger M2;
    protected BigInteger Key;
    protected Digest digest;
    protected SecureRandom random;
    
    public void init(final BigInteger n, final BigInteger g, final Digest digest, final SecureRandom random) {
        this.N = n;
        this.g = g;
        this.digest = digest;
        this.random = random;
    }
    
    public void init(final SRP6GroupParameters srp6GroupParameters, final Digest digest, final SecureRandom secureRandom) {
        this.init(srp6GroupParameters.getN(), srp6GroupParameters.getG(), digest, secureRandom);
    }
    
    public BigInteger generateClientCredentials(final byte[] array, final byte[] array2, final byte[] array3) {
        this.x = SRP6Util.calculateX(this.digest, this.N, array, array2, array3);
        this.a = this.selectPrivateValue();
        return this.A = this.g.modPow(this.a, this.N);
    }
    
    public BigInteger calculateSecret(final BigInteger bigInteger) throws CryptoException {
        this.B = SRP6Util.validatePublicValue(this.N, bigInteger);
        this.u = SRP6Util.calculateU(this.digest, this.N, this.A, this.B);
        return this.S = this.calculateS();
    }
    
    protected BigInteger selectPrivateValue() {
        return SRP6Util.generatePrivateValue(this.digest, this.N, this.g, this.random);
    }
    
    private BigInteger calculateS() {
        return this.B.subtract(this.g.modPow(this.x, this.N).multiply(SRP6Util.calculateK(this.digest, this.N, this.g)).mod(this.N)).mod(this.N).modPow(this.u.multiply(this.x).add(this.a), this.N);
    }
    
    public BigInteger calculateClientEvidenceMessage() throws CryptoException {
        if (this.A == null || this.B == null || this.S == null) {
            throw new CryptoException("Impossible to compute M1: some data are missing from the previous operations (A,B,S)");
        }
        return this.M1 = SRP6Util.calculateM1(this.digest, this.N, this.A, this.B, this.S);
    }
    
    public boolean verifyServerEvidenceMessage(final BigInteger m2) throws CryptoException {
        if (this.A == null || this.M1 == null || this.S == null) {
            throw new CryptoException("Impossible to compute and verify M2: some data are missing from the previous operations (A,M1,S)");
        }
        if (SRP6Util.calculateM2(this.digest, this.N, this.A, this.M1, this.S).equals(m2)) {
            this.M2 = m2;
            return true;
        }
        return false;
    }
    
    public BigInteger calculateSessionKey() throws CryptoException {
        if (this.S == null || this.M1 == null || this.M2 == null) {
            throw new CryptoException("Impossible to compute Key: some data are missing from the previous operations (S,M1,M2)");
        }
        return this.Key = SRP6Util.calculateKey(this.digest, this.N, this.S);
    }
}
