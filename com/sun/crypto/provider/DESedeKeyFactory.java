package com.sun.crypto.provider;

import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactorySpi;

public final class DESedeKeyFactory extends SecretKeyFactorySpi
{
    @Override
    protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            if (keySpec instanceof DESedeKeySpec) {
                return new DESedeKey(((DESedeKeySpec)keySpec).getKey());
            }
            if (keySpec instanceof SecretKeySpec) {
                return new DESedeKey(((SecretKeySpec)keySpec).getEncoded());
            }
            throw new InvalidKeySpecException("Inappropriate key specification");
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidKeySpecException(ex.getMessage());
        }
    }
    
    @Override
    protected KeySpec engineGetKeySpec(final SecretKey secretKey, final Class<?> clazz) throws InvalidKeySpecException {
        try {
            if (!(secretKey instanceof SecretKey) || !secretKey.getAlgorithm().equalsIgnoreCase("DESede") || !secretKey.getFormat().equalsIgnoreCase("RAW")) {
                throw new InvalidKeySpecException("Inappropriate key format/algorithm");
            }
            if (DESedeKeySpec.class.isAssignableFrom(clazz)) {
                return new DESedeKeySpec(secretKey.getEncoded());
            }
            throw new InvalidKeySpecException("Inappropriate key specification");
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidKeySpecException("Secret key has wrong size");
        }
    }
    
    @Override
    protected SecretKey engineTranslateKey(final SecretKey secretKey) throws InvalidKeyException {
        try {
            if (secretKey == null || !secretKey.getAlgorithm().equalsIgnoreCase("DESede") || !secretKey.getFormat().equalsIgnoreCase("RAW")) {
                throw new InvalidKeyException("Inappropriate key format/algorithm");
            }
            if (secretKey instanceof DESedeKey) {
                return secretKey;
            }
            return this.engineGenerateSecret(this.engineGetKeySpec(secretKey, DESedeKeySpec.class));
        }
        catch (final InvalidKeySpecException ex) {
            throw new InvalidKeyException("Cannot translate key");
        }
    }
}
