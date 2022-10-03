package org.bouncycastle.jcajce.util;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.Signature;
import java.security.MessageDigest;
import java.security.KeyPairGenerator;
import javax.crypto.SecretKeyFactory;
import java.security.KeyFactory;
import javax.crypto.KeyGenerator;
import java.security.AlgorithmParameters;
import java.security.AlgorithmParameterGenerator;
import javax.crypto.KeyAgreement;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import java.security.Provider;

public class ProviderJcaJceHelper implements JcaJceHelper
{
    protected final Provider provider;
    
    public ProviderJcaJceHelper(final Provider provider) {
        this.provider = provider;
    }
    
    public Cipher createCipher(final String s) throws NoSuchAlgorithmException, NoSuchPaddingException {
        return Cipher.getInstance(s, this.provider);
    }
    
    public Mac createMac(final String s) throws NoSuchAlgorithmException {
        return Mac.getInstance(s, this.provider);
    }
    
    public KeyAgreement createKeyAgreement(final String s) throws NoSuchAlgorithmException {
        return KeyAgreement.getInstance(s, this.provider);
    }
    
    public AlgorithmParameterGenerator createAlgorithmParameterGenerator(final String s) throws NoSuchAlgorithmException {
        return AlgorithmParameterGenerator.getInstance(s, this.provider);
    }
    
    public AlgorithmParameters createAlgorithmParameters(final String s) throws NoSuchAlgorithmException {
        return AlgorithmParameters.getInstance(s, this.provider);
    }
    
    public KeyGenerator createKeyGenerator(final String s) throws NoSuchAlgorithmException {
        return KeyGenerator.getInstance(s, this.provider);
    }
    
    public KeyFactory createKeyFactory(final String s) throws NoSuchAlgorithmException {
        return KeyFactory.getInstance(s, this.provider);
    }
    
    public SecretKeyFactory createSecretKeyFactory(final String s) throws NoSuchAlgorithmException {
        return SecretKeyFactory.getInstance(s, this.provider);
    }
    
    public KeyPairGenerator createKeyPairGenerator(final String s) throws NoSuchAlgorithmException {
        return KeyPairGenerator.getInstance(s, this.provider);
    }
    
    public MessageDigest createDigest(final String s) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(s, this.provider);
    }
    
    public Signature createSignature(final String s) throws NoSuchAlgorithmException {
        return Signature.getInstance(s, this.provider);
    }
    
    public CertificateFactory createCertificateFactory(final String s) throws CertificateException {
        return CertificateFactory.getInstance(s, this.provider);
    }
    
    public SecureRandom createSecureRandom(final String s) throws NoSuchAlgorithmException {
        return SecureRandom.getInstance(s, this.provider);
    }
}
