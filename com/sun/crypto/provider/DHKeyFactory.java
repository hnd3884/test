package com.sun.crypto.provider;

import javax.crypto.spec.DHParameterSpec;
import java.security.Key;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.spec.DHPrivateKeySpec;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.KeyFactorySpi;

public final class DHKeyFactory extends KeyFactorySpi
{
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            if (keySpec instanceof DHPublicKeySpec) {
                final DHPublicKeySpec dhPublicKeySpec = (DHPublicKeySpec)keySpec;
                return new DHPublicKey(dhPublicKeySpec.getY(), dhPublicKeySpec.getP(), dhPublicKeySpec.getG());
            }
            if (keySpec instanceof X509EncodedKeySpec) {
                return new DHPublicKey(((X509EncodedKeySpec)keySpec).getEncoded());
            }
            throw new InvalidKeySpecException("Inappropriate key specification");
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidKeySpecException("Inappropriate key specification", ex);
        }
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            if (keySpec instanceof DHPrivateKeySpec) {
                final DHPrivateKeySpec dhPrivateKeySpec = (DHPrivateKeySpec)keySpec;
                return new DHPrivateKey(dhPrivateKeySpec.getX(), dhPrivateKeySpec.getP(), dhPrivateKeySpec.getG());
            }
            if (keySpec instanceof PKCS8EncodedKeySpec) {
                return new DHPrivateKey(((PKCS8EncodedKeySpec)keySpec).getEncoded());
            }
            throw new InvalidKeySpecException("Inappropriate key specification");
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidKeySpecException("Inappropriate key specification", ex);
        }
    }
    
    @Override
    protected <T extends KeySpec> T engineGetKeySpec(final Key key, final Class<T> clazz) throws InvalidKeySpecException {
        if (key instanceof javax.crypto.interfaces.DHPublicKey) {
            if (DHPublicKeySpec.class.isAssignableFrom(clazz)) {
                final javax.crypto.interfaces.DHPublicKey dhPublicKey = (javax.crypto.interfaces.DHPublicKey)key;
                final DHParameterSpec params = dhPublicKey.getParams();
                return clazz.cast(new DHPublicKeySpec(dhPublicKey.getY(), params.getP(), params.getG()));
            }
            if (X509EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return clazz.cast(new X509EncodedKeySpec(key.getEncoded()));
            }
            throw new InvalidKeySpecException("Inappropriate key specification");
        }
        else {
            if (!(key instanceof javax.crypto.interfaces.DHPrivateKey)) {
                throw new InvalidKeySpecException("Inappropriate key type");
            }
            if (DHPrivateKeySpec.class.isAssignableFrom(clazz)) {
                final javax.crypto.interfaces.DHPrivateKey dhPrivateKey = (javax.crypto.interfaces.DHPrivateKey)key;
                final DHParameterSpec params2 = dhPrivateKey.getParams();
                return clazz.cast(new DHPrivateKeySpec(dhPrivateKey.getX(), params2.getP(), params2.getG()));
            }
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return clazz.cast(new PKCS8EncodedKeySpec(key.getEncoded()));
            }
            throw new InvalidKeySpecException("Inappropriate key specification");
        }
    }
    
    @Override
    protected Key engineTranslateKey(final Key key) throws InvalidKeyException {
        try {
            if (key instanceof javax.crypto.interfaces.DHPublicKey) {
                if (key instanceof DHPublicKey) {
                    return key;
                }
                return this.engineGeneratePublic(this.engineGetKeySpec(key, DHPublicKeySpec.class));
            }
            else {
                if (!(key instanceof javax.crypto.interfaces.DHPrivateKey)) {
                    throw new InvalidKeyException("Wrong algorithm type");
                }
                if (key instanceof DHPrivateKey) {
                    return key;
                }
                return this.engineGeneratePrivate(this.engineGetKeySpec(key, DHPrivateKeySpec.class));
            }
        }
        catch (final InvalidKeySpecException ex) {
            throw new InvalidKeyException("Cannot translate key", ex);
        }
    }
}
