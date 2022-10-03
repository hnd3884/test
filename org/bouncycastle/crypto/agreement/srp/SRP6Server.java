package org.bouncycastle.crypto.agreement.srp;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.SRP6GroupParameters;
import org.bouncycastle.crypto.Digest;
import java.security.SecureRandom;
import java.math.BigInteger;

public class SRP6Server
{
    protected BigInteger N;
    protected BigInteger g;
    protected BigInteger v;
    protected SecureRandom random;
    protected Digest digest;
    protected BigInteger A;
    protected BigInteger b;
    protected BigInteger B;
    protected BigInteger u;
    protected BigInteger S;
    protected BigInteger M1;
    protected BigInteger M2;
    protected BigInteger Key;
    
    public void init(final BigInteger n, final BigInteger g, final BigInteger v, final Digest digest, final SecureRandom random) {
        this.N = n;
        this.g = g;
        this.v = v;
        this.random = random;
        this.digest = digest;
    }
    
    public void init(final SRP6GroupParameters srp6GroupParameters, final BigInteger bigInteger, final Digest digest, final SecureRandom secureRandom) {
        this.init(srp6GroupParameters.getN(), srp6GroupParameters.getG(), bigInteger, digest, secureRandom);
    }
    
    public BigInteger generateServerCredentials() {
        final BigInteger calculateK = SRP6Util.calculateK(this.digest, this.N, this.g);
        this.b = this.selectPrivateValue();
        return this.B = calculateK.multiply(this.v).mod(this.N).add(this.g.modPow(this.b, this.N)).mod(this.N);
    }
    
    public BigInteger calculateSecret(final BigInteger bigInteger) throws CryptoException {
        this.A = SRP6Util.validatePublicValue(this.N, bigInteger);
        this.u = SRP6Util.calculateU(this.digest, this.N, this.A, this.B);
        return this.S = this.calculateS();
    }
    
    protected BigInteger selectPrivateValue() {
        return SRP6Util.generatePrivateValue(this.digest, this.N, this.g, this.random);
    }
    
    private BigInteger calculateS() {
        return this.v.modPow(this.u, this.N).multiply(this.A).mod(this.N).modPow(this.b, this.N);
    }
    
    public boolean verifyClientEvidenceMessage(final BigInteger m1) throws CryptoException {
        if (this.A == null || this.B == null || this.S == null) {
            throw new CryptoException("Impossible to compute and verify M1: some data are missing from the previous operations (A,B,S)");
        }
        if (SRP6Util.calculateM1(this.digest, this.N, this.A, this.B, this.S).equals(m1)) {
            this.M1 = m1;
            return true;
        }
        return false;
    }
    
    public BigInteger calculateServerEvidenceMessage() throws CryptoException {
        if (this.A == null || this.M1 == null || this.S == null) {
            throw new CryptoException("Impossible to compute M2: some data are missing from the previous operations (A,M1,S)");
        }
        return this.M2 = SRP6Util.calculateM2(this.digest, this.N, this.A, this.M1, this.S);
    }
    
    public BigInteger calculateSessionKey() throws CryptoException {
        if (this.S == null || this.M1 == null || this.M2 == null) {
            throw new CryptoException("Impossible to compute Key: some data are missing from the previous operations (S,M1,M2)");
        }
        return this.Key = SRP6Util.calculateKey(this.digest, this.N, this.S);
    }
}
