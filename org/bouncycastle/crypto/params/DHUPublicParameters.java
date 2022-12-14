package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class DHUPublicParameters implements CipherParameters
{
    private DHPublicKeyParameters staticPublicKey;
    private DHPublicKeyParameters ephemeralPublicKey;
    
    public DHUPublicParameters(final DHPublicKeyParameters staticPublicKey, final DHPublicKeyParameters ephemeralPublicKey) {
        if (staticPublicKey == null) {
            throw new NullPointerException("staticPublicKey cannot be null");
        }
        if (ephemeralPublicKey == null) {
            throw new NullPointerException("ephemeralPublicKey cannot be null");
        }
        if (!staticPublicKey.getParameters().equals(ephemeralPublicKey.getParameters())) {
            throw new IllegalArgumentException("Static and ephemeral public keys have different domain parameters");
        }
        this.staticPublicKey = staticPublicKey;
        this.ephemeralPublicKey = ephemeralPublicKey;
    }
    
    public DHPublicKeyParameters getStaticPublicKey() {
        return this.staticPublicKey;
    }
    
    public DHPublicKeyParameters getEphemeralPublicKey() {
        return this.ephemeralPublicKey;
    }
}
