package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ECDHUPublicParameters implements CipherParameters
{
    private ECPublicKeyParameters staticPublicKey;
    private ECPublicKeyParameters ephemeralPublicKey;
    
    public ECDHUPublicParameters(final ECPublicKeyParameters staticPublicKey, final ECPublicKeyParameters ephemeralPublicKey) {
        if (staticPublicKey == null) {
            throw new NullPointerException("staticPublicKey cannot be null");
        }
        if (ephemeralPublicKey == null) {
            throw new NullPointerException("ephemeralPublicKey cannot be null");
        }
        if (!staticPublicKey.getParameters().equals(ephemeralPublicKey.getParameters())) {
            throw new IllegalArgumentException("static and ephemeral public keys have different domain parameters");
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
