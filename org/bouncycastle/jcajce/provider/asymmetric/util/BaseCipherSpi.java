package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.InvalidCipherTextException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import javax.crypto.spec.RC5ParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.crypto.Wrapper;
import java.security.AlgorithmParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import javax.crypto.CipherSpi;

public abstract class BaseCipherSpi extends CipherSpi
{
    private Class[] availableSpecs;
    private final JcaJceHelper helper;
    protected AlgorithmParameters engineParams;
    protected Wrapper wrapEngine;
    private int ivSize;
    private byte[] iv;
    
    protected BaseCipherSpi() {
        this.availableSpecs = new Class[] { IvParameterSpec.class, PBEParameterSpec.class, RC2ParameterSpec.class, RC5ParameterSpec.class };
        this.helper = new BCJcaJceHelper();
        this.engineParams = null;
        this.wrapEngine = null;
    }
    
    @Override
    protected int engineGetBlockSize() {
        return 0;
    }
    
    @Override
    protected byte[] engineGetIV() {
        return null;
    }
    
    @Override
    protected int engineGetKeySize(final Key key) {
        return key.getEncoded().length;
    }
    
    @Override
    protected int engineGetOutputSize(final int n) {
        return -1;
    }
    
    @Override
    protected AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    protected final AlgorithmParameters createParametersInstance(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return this.helper.createAlgorithmParameters(s);
    }
    
    @Override
    protected void engineSetMode(final String s) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException("can't support mode " + s);
    }
    
    @Override
    protected void engineSetPadding(final String s) throws NoSuchPaddingException {
        throw new NoSuchPaddingException("Padding " + s + " unknown.");
    }
    
    @Override
    protected byte[] engineWrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        final byte[] encoded = key.getEncoded();
        if (encoded == null) {
            throw new InvalidKeyException("Cannot wrap key, null encoding.");
        }
        try {
            if (this.wrapEngine == null) {
                return this.engineDoFinal(encoded, 0, encoded.length);
            }
            return this.wrapEngine.wrap(encoded, 0, encoded.length);
        }
        catch (final BadPaddingException ex) {
            throw new IllegalBlockSizeException(ex.getMessage());
        }
    }
    
    @Override
    protected Key engineUnwrap(final byte[] array, final String s, final int n) throws InvalidKeyException {
        byte[] array2;
        try {
            if (this.wrapEngine == null) {
                array2 = this.engineDoFinal(array, 0, array.length);
            }
            else {
                array2 = this.wrapEngine.unwrap(array, 0, array.length);
            }
        }
        catch (final InvalidCipherTextException ex) {
            throw new InvalidKeyException(ex.getMessage());
        }
        catch (final BadPaddingException ex2) {
            throw new InvalidKeyException("unable to unwrap") {
                @Override
                public synchronized Throwable getCause() {
                    return ex2;
                }
            };
        }
        catch (final IllegalBlockSizeException ex3) {
            throw new InvalidKeyException(ex3.getMessage());
        }
        if (n == 3) {
            return new SecretKeySpec(array2, s);
        }
        if (s.equals("") && n == 2) {
            try {
                final PrivateKeyInfo instance = PrivateKeyInfo.getInstance(array2);
                final PrivateKey privateKey = BouncyCastleProvider.getPrivateKey(instance);
                if (privateKey != null) {
                    return privateKey;
                }
                throw new InvalidKeyException("algorithm " + instance.getPrivateKeyAlgorithm().getAlgorithm() + " not supported");
            }
            catch (final Exception ex4) {
                throw new InvalidKeyException("Invalid key encoding.");
            }
        }
        try {
            final KeyFactory keyFactory = this.helper.createKeyFactory(s);
            if (n == 1) {
                return keyFactory.generatePublic(new X509EncodedKeySpec(array2));
            }
            if (n == 2) {
                return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(array2));
            }
        }
        catch (final NoSuchAlgorithmException ex5) {
            throw new InvalidKeyException("Unknown key type " + ex5.getMessage());
        }
        catch (final InvalidKeySpecException ex6) {
            throw new InvalidKeyException("Unknown key type " + ex6.getMessage());
        }
        catch (final NoSuchProviderException ex7) {
            throw new InvalidKeyException("Unknown key type " + ex7.getMessage());
        }
        throw new InvalidKeyException("Unknown key type " + n);
    }
}
