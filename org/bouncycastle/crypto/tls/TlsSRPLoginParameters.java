package org.bouncycastle.crypto.tls;

import java.math.BigInteger;
import org.bouncycastle.crypto.params.SRP6GroupParameters;

public class TlsSRPLoginParameters
{
    protected SRP6GroupParameters group;
    protected BigInteger verifier;
    protected byte[] salt;
    
    public TlsSRPLoginParameters(final SRP6GroupParameters group, final BigInteger verifier, final byte[] salt) {
        this.group = group;
        this.verifier = verifier;
        this.salt = salt;
    }
    
    public SRP6GroupParameters getGroup() {
        return this.group;
    }
    
    public byte[] getSalt() {
        return this.salt;
    }
    
    public BigInteger getVerifier() {
        return this.verifier;
    }
}
