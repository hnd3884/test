package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import javax.crypto.interfaces.DHPrivateKey;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHParameters;
import javax.crypto.interfaces.DHPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.dh.BCDHPublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.PublicKey;

public class DHUtil
{
    public static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCDHPublicKey) {
            return ((BCDHPublicKey)publicKey).engineGetKeyParameters();
        }
        if (publicKey instanceof DHPublicKey) {
            final DHPublicKey dhPublicKey = (DHPublicKey)publicKey;
            return new DHPublicKeyParameters(dhPublicKey.getY(), new DHParameters(dhPublicKey.getParams().getP(), dhPublicKey.getParams().getG(), null, dhPublicKey.getParams().getL()));
        }
        throw new InvalidKeyException("can't identify DH public key.");
    }
    
    public static AsymmetricKeyParameter generatePrivateKeyParameter(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof DHPrivateKey) {
            final DHPrivateKey dhPrivateKey = (DHPrivateKey)privateKey;
            return new DHPrivateKeyParameters(dhPrivateKey.getX(), new DHParameters(dhPrivateKey.getParams().getP(), dhPrivateKey.getParams().getG(), null, dhPrivateKey.getParams().getL()));
        }
        throw new InvalidKeyException("can't identify DH private key.");
    }
}
