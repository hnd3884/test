package org.bouncycastle.pqc.jcajce.provider.rainbow;

import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.PublicKey;

public class RainbowKeysToParams
{
    public static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCRainbowPublicKey) {
            final BCRainbowPublicKey bcRainbowPublicKey = (BCRainbowPublicKey)publicKey;
            return new RainbowPublicKeyParameters(bcRainbowPublicKey.getDocLength(), bcRainbowPublicKey.getCoeffQuadratic(), bcRainbowPublicKey.getCoeffSingular(), bcRainbowPublicKey.getCoeffScalar());
        }
        throw new InvalidKeyException("can't identify Rainbow public key: " + publicKey.getClass().getName());
    }
    
    public static AsymmetricKeyParameter generatePrivateKeyParameter(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCRainbowPrivateKey) {
            final BCRainbowPrivateKey bcRainbowPrivateKey = (BCRainbowPrivateKey)privateKey;
            return new RainbowPrivateKeyParameters(bcRainbowPrivateKey.getInvA1(), bcRainbowPrivateKey.getB1(), bcRainbowPrivateKey.getInvA2(), bcRainbowPrivateKey.getB2(), bcRainbowPrivateKey.getVi(), bcRainbowPrivateKey.getLayers());
        }
        throw new InvalidKeyException("can't identify Rainbow private key.");
    }
}
