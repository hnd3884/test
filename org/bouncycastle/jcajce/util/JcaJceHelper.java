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

public interface JcaJceHelper
{
    Cipher createCipher(final String p0) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException;
    
    Mac createMac(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
    
    KeyAgreement createKeyAgreement(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
    
    AlgorithmParameterGenerator createAlgorithmParameterGenerator(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
    
    AlgorithmParameters createAlgorithmParameters(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
    
    KeyGenerator createKeyGenerator(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
    
    KeyFactory createKeyFactory(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
    
    SecretKeyFactory createSecretKeyFactory(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
    
    KeyPairGenerator createKeyPairGenerator(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
    
    MessageDigest createDigest(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
    
    Signature createSignature(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
    
    CertificateFactory createCertificateFactory(final String p0) throws NoSuchProviderException, CertificateException;
    
    SecureRandom createSecureRandom(final String p0) throws NoSuchAlgorithmException, NoSuchProviderException;
}
