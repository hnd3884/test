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
import java.security.NoSuchProviderException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;

public class NamedJcaJceHelper implements JcaJceHelper
{
    protected final String providerName;
    
    public NamedJcaJceHelper(final String providerName) {
        this.providerName = providerName;
    }
    
    public Cipher createCipher(final String s) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        return Cipher.getInstance(s, this.providerName);
    }
    
    public Mac createMac(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Mac.getInstance(s, this.providerName);
    }
    
    public KeyAgreement createKeyAgreement(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyAgreement.getInstance(s, this.providerName);
    }
    
    public AlgorithmParameterGenerator createAlgorithmParameterGenerator(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return AlgorithmParameterGenerator.getInstance(s, this.providerName);
    }
    
    public AlgorithmParameters createAlgorithmParameters(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return AlgorithmParameters.getInstance(s, this.providerName);
    }
    
    public KeyGenerator createKeyGenerator(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyGenerator.getInstance(s, this.providerName);
    }
    
    public KeyFactory createKeyFactory(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyFactory.getInstance(s, this.providerName);
    }
    
    public SecretKeyFactory createSecretKeyFactory(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return SecretKeyFactory.getInstance(s, this.providerName);
    }
    
    public KeyPairGenerator createKeyPairGenerator(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyPairGenerator.getInstance(s, this.providerName);
    }
    
    public MessageDigest createDigest(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return MessageDigest.getInstance(s, this.providerName);
    }
    
    public Signature createSignature(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Signature.getInstance(s, this.providerName);
    }
    
    public CertificateFactory createCertificateFactory(final String s) throws CertificateException, NoSuchProviderException {
        return CertificateFactory.getInstance(s, this.providerName);
    }
    
    public SecureRandom createSecureRandom(final String s) throws NoSuchAlgorithmException, NoSuchProviderException {
        return SecureRandom.getInstance(s, this.providerName);
    }
}
