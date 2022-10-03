package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ECDHUPrivateParameters implements CipherParameters
{
    private ECPrivateKeyParameters staticPrivateKey;
    private ECPrivateKeyParameters ephemeralPrivateKey;
    private ECPublicKeyParameters ephemeralPublicKey;
    
    public ECDHUPrivateParameters(final ECPrivateKeyParameters ecPrivateKeyParameters, final ECPrivateKeyParameters ecPrivateKeyParameters2) {
        this(ecPrivateKeyParameters, ecPrivateKeyParameters2, null);
    }
    
    public ECDHUPrivateParameters(final ECPrivateKeyParameters staticPrivateKey, final ECPrivateKeyParameters ephemeralPrivateKey, ECPublicKeyParameters ephemeralPublicKey) {
        if (staticPrivateKey == null) {
            throw new NullPointerException("staticPrivateKey cannot be null");
        }
        if (ephemeralPrivateKey == null) {
            throw new NullPointerException("ephemeralPrivateKey cannot be null");
        }
        final ECDomainParameters parameters = staticPrivateKey.getParameters();
        if (!parameters.equals(ephemeralPrivateKey.getParameters())) {
            throw new IllegalArgumentException("static and ephemeral private keys have different domain parameters");
        }
        if (ephemeralPublicKey == null) {
            ephemeralPublicKey = new ECPublicKeyParameters(parameters.getG().multiply(ephemeralPrivateKey.getD()), parameters);
        }
        else if (!parameters.equals(ephemeralPublicKey.getParameters())) {
            throw new IllegalArgumentException("ephemeral public key has different domain parameters");
        }
        this.staticPrivateKey = staticPrivateKey;
        this.ephemeralPrivateKey = ephemeralPrivateKey;
        this.ephemeralPublicKey = ephemeralPublicKey;
    }
    
    public ECPrivateKeyParameters getStaticPrivateKey() {
        return this.staticPrivateKey;
    }
    
    public ECPrivateKeyParameters getEphemeralPrivateKey() {
        return this.ephemeralPrivateKey;
    }
    
    public ECPublicKeyParameters getEphemeralPublicKey() {
        return this.ephemeralPublicKey;
    }
}
