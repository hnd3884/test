package org.bouncycastle.pqc.jcajce.provider.mceliece;

import java.security.PrivateKey;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.PublicKey;

public class McElieceCCA2KeysToParams
{
    public static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof BCMcElieceCCA2PublicKey) {
            return ((BCMcElieceCCA2PublicKey)publicKey).getKeyParams();
        }
        throw new InvalidKeyException("can't identify McElieceCCA2 public key: " + publicKey.getClass().getName());
    }
    
    public static AsymmetricKeyParameter generatePrivateKeyParameter(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof BCMcElieceCCA2PrivateKey) {
            return ((BCMcElieceCCA2PrivateKey)privateKey).getKeyParams();
        }
        throw new InvalidKeyException("can't identify McElieceCCA2 private key.");
    }
}
