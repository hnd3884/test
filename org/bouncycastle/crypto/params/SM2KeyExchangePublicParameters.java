package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class SM2KeyExchangePublicParameters implements CipherParameters
{
    private final ECPublicKeyParameters staticPublicKey;
    private final ECPublicKeyParameters ephemeralPublicKey;
    
    public SM2KeyExchangePublicParameters(final ECPublicKeyParameters staticPublicKey, final ECPublicKeyParameters ephemeralPublicKey) {
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
    
    public ECPublicKeyParameters getStaticPublicKey() {
        return this.staticPublicKey;
    }
    
    public ECPublicKeyParameters getEphemeralPublicKey() {
        return this.ephemeralPublicKey;
    }
}
