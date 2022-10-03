package org.bouncycastle.pqc.jcajce.provider.mceliece;

import org.bouncycastle.pqc.crypto.mceliece.McEliecePrivateKeyParameters;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.PublicKey;

public class McElieceKeysToParams
{
    public static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCMcEliecePublicKey) {
            return ((BCMcEliecePublicKey)publicKey).getKeyParams();
        }
        throw new InvalidKeyException("can't identify McEliece public key: " + publicKey.getClass().getName());
    }
    
    public static AsymmetricKeyParameter generatePrivateKeyParameter(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCMcEliecePrivateKey) {
            final BCMcEliecePrivateKey bcMcEliecePrivateKey = (BCMcEliecePrivateKey)privateKey;
            return new McEliecePrivateKeyParameters(bcMcEliecePrivateKey.getN(), bcMcEliecePrivateKey.getK(), bcMcEliecePrivateKey.getField(), bcMcEliecePrivateKey.getGoppaPoly(), bcMcEliecePrivateKey.getP1(), bcMcEliecePrivateKey.getP2(), bcMcEliecePrivateKey.getSInv());
        }
        throw new InvalidKeyException("can't identify McEliece private key.");
    }
}
