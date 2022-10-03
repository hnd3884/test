package com.sun.crypto.provider;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.Provider;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.CipherSpi;

public abstract class CipherWithWrappingSpi extends CipherSpi
{
    @Override
    protected final byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        byte[] engineDoFinal = null;
        try {
            final byte[] encoded = key.getEncoded();
            if (encoded == null || encoded.length == 0) {
                throw new InvalidKeyException("Cannot get an encoding of the key to be wrapped");
            }
            engineDoFinal = this.engineDoFinal(encoded, 0, encoded.length);
        }
        catch (final BadPaddingException ex) {}
        return engineDoFinal;
    }
    
    @Override
    protected final Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        Key key = null;
        byte[] engineDoFinal;
        try {
            engineDoFinal = this.engineDoFinal(array, 0, array.length);
        }
        catch (final BadPaddingException ex) {
            throw new InvalidKeyException();
        }
        catch (final IllegalBlockSizeException ex2) {
            throw new InvalidKeyException();
        }
        switch (n) {
            case 3: {
                key = this.constructSecretKey(engineDoFinal, s);
                break;
            }
            case 2: {
                key = this.constructPrivateKey(engineDoFinal, s);
                break;
            }
            case 1: {
                key = this.constructPublicKey(engineDoFinal, s);
                break;
            }
        }
        return key;
    }
    
    private final PublicKey constructPublicKey(final byte[] array, final String s) throws InvalidKeyException, NoSuchAlgorithmException {
        PublicKey publicKey = null;
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
            catch (final InvalidKeySpecException ex3) {}
        }
        catch (final InvalidKeySpecException ex4) {}
        return publicKey;
    }
    
    private final PrivateKey constructPrivateKey(final byte[] array, final String s) throws InvalidKeyException, NoSuchAlgorithmException {
        PrivateKey generatePrivate = null;
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
            catch (final InvalidKeySpecException ex3) {}
        }
        catch (final InvalidKeySpecException ex4) {}
        return generatePrivate;
    }
    
    private final SecretKey constructSecretKey(final byte[] array, final String s) {
        return new SecretKeySpec(array, s);
    }
}
