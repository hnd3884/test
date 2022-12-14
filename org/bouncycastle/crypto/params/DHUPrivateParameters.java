package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class DHUPrivateParameters implements CipherParameters
{
    private DHPrivateKeyParameters staticPrivateKey;
    private DHPrivateKeyParameters ephemeralPrivateKey;
    private DHPublicKeyParameters ephemeralPublicKey;
    
    public DHUPrivateParameters(final DHPrivateKeyParameters dhPrivateKeyParameters, final DHPrivateKeyParameters dhPrivateKeyParameters2) {
        this(dhPrivateKeyParameters, dhPrivateKeyParameters2, null);
    }
    
    public DHUPrivateParameters(final DHPrivateKeyParameters staticPrivateKey, final DHPrivateKeyParameters ephemeralPrivateKey, DHPublicKeyParameters ephemeralPublicKey) {
        if (staticPrivateKey == null) {
            throw new NullPointerException("staticPrivateKey cannot be null");
        }
        if (ephemeralPrivateKey == null) {
            throw new NullPointerException("ephemeralPrivateKey cannot be null");
        }
        final DHParameters parameters = staticPrivateKey.getParameters();
        if (!parameters.equals(ephemeralPrivateKey.getParameters())) {
            throw new IllegalArgumentException("static and ephemeral private keys have different domain parameters");
        }
        if (ephemeralPublicKey == null) {
            ephemeralPublicKey = new DHPublicKeyParameters(parameters.getG().modPow(ephemeralPrivateKey.getX(), parameters.getP()), parameters);
        }
        else if (!parameters.equals(ephemeralPublicKey.getParameters())) {
            throw new IllegalArgumentException("ephemeral public key has different domain parameters");
        }
        this.staticPrivateKey = staticPrivateKey;
        this.ephemeralPrivateKey = ephemeralPrivateKey;
        this.ephemeralPublicKey = ephemeralPublicKey;
    }
    
    public DHPrivateKeyParameters getStaticPrivateKey() {
        return this.staticPrivateKey;
    }
    
    public DHPrivateKeyParameters getEphemeralPrivateKey() {
        return this.ephemeralPrivateKey;
    }
    
    public DHPublicKeyParameters getEphemeralPublicKey() {
        return this.ephemeralPublicKey;
    }
}
