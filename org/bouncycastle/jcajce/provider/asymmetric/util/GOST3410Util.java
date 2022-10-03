package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import java.security.PrivateKey;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.PublicKey;

public class GOST3410Util
{
    public static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof GOST3410PublicKey) {
            final GOST3410PublicKey gost3410PublicKey = (GOST3410PublicKey)publicKey;
            final GOST3410PublicKeyParameterSetSpec publicKeyParameters = gost3410PublicKey.getParameters().getPublicKeyParameters();
            return new GOST3410PublicKeyParameters(gost3410PublicKey.getY(), new GOST3410Parameters(publicKeyParameters.getP(), publicKeyParameters.getQ(), publicKeyParameters.getA()));
        }
        throw new InvalidKeyException("can't identify GOST3410 public key: " + publicKey.getClass().getName());
    }
    
    public static AsymmetricKeyParameter generatePrivateKeyParameter(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof GOST3410PrivateKey) {
            final GOST3410PrivateKey gost3410PrivateKey = (GOST3410PrivateKey)privateKey;
            final GOST3410PublicKeyParameterSetSpec publicKeyParameters = gost3410PrivateKey.getParameters().getPublicKeyParameters();
            return new GOST3410PrivateKeyParameters(gost3410PrivateKey.getX(), new GOST3410Parameters(publicKeyParameters.getP(), publicKeyParameters.getQ(), publicKeyParameters.getA()));
        }
        throw new InvalidKeyException("can't identify GOST3410 private key.");
    }
}
