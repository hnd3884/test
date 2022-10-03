package com.sun.crypto.provider;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.Provider;
import java.security.KeyFactory;
import java.security.PublicKey;

final class ConstructKeys
{
    private static final PublicKey constructPublicKey(final byte[] array, final String s) throws InvalidKeyException, NoSuchAlgorithmException {
        PublicKey publicKey;
        try {
            publicKey = KeyFactory.getInstance(s, SunJCE.getInstance()).generatePublic(new X509EncodedKeySpec(array));
        }
        catch (final NoSuchAlgorithmException ex) {
            try {
                publicKey = KeyFactory.getInstance(s).generatePublic(new X509EncodedKeySpec(array));
            }
            catch (final NoSuchAlgorithmException ex2) {
                throw new NoSuchAlgorithmException("No installed providers can create keys for the " + s + "algorithm");
            }
            catch (final InvalidKeySpecException ex3) {
                final InvalidKeyException ex4 = new InvalidKeyException("Cannot construct public key");
                ex4.initCause(ex3);
                throw ex4;
            }
        }
        catch (final InvalidKeySpecException ex5) {
            final InvalidKeyException ex6 = new InvalidKeyException("Cannot construct public key");
            ex6.initCause(ex5);
            throw ex6;
        }
        return publicKey;
    }
    
    private static final PrivateKey constructPrivateKey(final byte[] array, final String s) throws InvalidKeyException, NoSuchAlgorithmException {
        PrivateKey generatePrivate;
        try {
            return KeyFactory.getInstance(s, SunJCE.getInstance()).generatePrivate(new PKCS8EncodedKeySpec(array));
        }
        catch (final NoSuchAlgorithmException ex) {
            try {
                generatePrivate = KeyFactory.getInstance(s).generatePrivate(new PKCS8EncodedKeySpec(array));
            }
            catch (final NoSuchAlgorithmException ex2) {
                throw new NoSuchAlgorithmException("No installed providers can create keys for the " + s + "algorithm");
            }
            catch (final InvalidKeySpecException ex3) {
                final InvalidKeyException ex4 = new InvalidKeyException("Cannot construct private key");
                ex4.initCause(ex3);
                throw ex4;
            }
        }
        catch (final InvalidKeySpecException ex5) {
            final InvalidKeyException ex6 = new InvalidKeyException("Cannot construct private key");
            ex6.initCause(ex5);
            throw ex6;
        }
        return generatePrivate;
    }
    
    private static final SecretKey constructSecretKey(final byte[] array, final String s) {
        return new SecretKeySpec(array, s);
    }
    
    static final Key constructKey(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        Key key = null;
        switch (n) {
            case 3: {
                key = constructSecretKey(array, s);
                break;
            }
            case 2: {
                key = constructPrivateKey(array, s);
                break;
            }
            case 1: {
                key = constructPublicKey(array, s);
                break;
            }
        }
        return key;
    }
}
