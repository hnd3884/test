package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import javax.crypto.interfaces.DHPrivateKey;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import javax.crypto.interfaces.DHPublicKey;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.PublicKey;

public class ElGamalUtil
{
    public static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof ElGamalPublicKey) {
            final ElGamalPublicKey elGamalPublicKey = (ElGamalPublicKey)publicKey;
            return new ElGamalPublicKeyParameters(elGamalPublicKey.getY(), new ElGamalParameters(elGamalPublicKey.getParameters().getP(), elGamalPublicKey.getParameters().getG()));
        }
        if (publicKey instanceof DHPublicKey) {
            final DHPublicKey dhPublicKey = (DHPublicKey)publicKey;
            return new ElGamalPublicKeyParameters(dhPublicKey.getY(), new ElGamalParameters(dhPublicKey.getParams().getP(), dhPublicKey.getParams().getG()));
        }
        throw new InvalidKeyException("can't identify public key for El Gamal.");
    }
    
    public static AsymmetricKeyParameter generatePrivateKeyParameter(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof ElGamalPrivateKey) {
            final ElGamalPrivateKey elGamalPrivateKey = (ElGamalPrivateKey)privateKey;
            return new ElGamalPrivateKeyParameters(elGamalPrivateKey.getX(), new ElGamalParameters(elGamalPrivateKey.getParameters().getP(), elGamalPrivateKey.getParameters().getG()));
        }
        if (privateKey instanceof DHPrivateKey) {
            final DHPrivateKey dhPrivateKey = (DHPrivateKey)privateKey;
            return new ElGamalPrivateKeyParameters(dhPrivateKey.getX(), new ElGamalParameters(dhPrivateKey.getParams().getP(), dhPrivateKey.getParams().getG()));
        }
        throw new InvalidKeyException("can't identify private key for El Gamal.");
    }
}
