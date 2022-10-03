package com.google.api.client.util;

import java.security.Key;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.io.Reader;
import java.io.InputStreamReader;
import java.security.cert.Certificate;
import java.util.Iterator;
import java.security.cert.CertificateFactory;
import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.X509TrustManager;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.NoSuchAlgorithmException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStoreException;
import java.security.KeyStore;

public final class SecurityUtils
{
    public static KeyStore getDefaultKeyStore() throws KeyStoreException {
        return KeyStore.getInstance(KeyStore.getDefaultType());
    }
    
    public static KeyStore getJavaKeyStore() throws KeyStoreException {
        return KeyStore.getInstance("JKS");
    }
    
    public static KeyStore getPkcs12KeyStore() throws KeyStoreException {
        return KeyStore.getInstance("PKCS12");
    }
    
    public static void loadKeyStore(final KeyStore keyStore, final InputStream keyStream, final String storePass) throws IOException, GeneralSecurityException {
        try {
            keyStore.load(keyStream, storePass.toCharArray());
        }
        finally {
            keyStream.close();
        }
    }
    
    public static PrivateKey getPrivateKey(final KeyStore keyStore, final String alias, final String keyPass) throws GeneralSecurityException {
        return (PrivateKey)keyStore.getKey(alias, keyPass.toCharArray());
    }
    
    public static PrivateKey loadPrivateKeyFromKeyStore(final KeyStore keyStore, final InputStream keyStream, final String storePass, final String alias, final String keyPass) throws IOException, GeneralSecurityException {
        loadKeyStore(keyStore, keyStream, storePass);
        return getPrivateKey(keyStore, alias, keyPass);
    }
    
    public static KeyFactory getRsaKeyFactory() throws NoSuchAlgorithmException {
        return KeyFactory.getInstance("RSA");
    }
    
    public static Signature getSha1WithRsaSignatureAlgorithm() throws NoSuchAlgorithmException {
        return Signature.getInstance("SHA1withRSA");
    }
    
    public static Signature getSha256WithRsaSignatureAlgorithm() throws NoSuchAlgorithmException {
        return Signature.getInstance("SHA256withRSA");
    }
    
    public static Signature getEs256SignatureAlgorithm() throws NoSuchAlgorithmException {
        return Signature.getInstance("SHA256withECDSA");
    }
    
    public static byte[] sign(final Signature signatureAlgorithm, final PrivateKey privateKey, final byte[] contentBytes) throws InvalidKeyException, SignatureException {
        signatureAlgorithm.initSign(privateKey);
        signatureAlgorithm.update(contentBytes);
        return signatureAlgorithm.sign();
    }
    
    public static boolean verify(final Signature signatureAlgorithm, final PublicKey publicKey, final byte[] signatureBytes, final byte[] contentBytes) throws InvalidKeyException, SignatureException {
        signatureAlgorithm.initVerify(publicKey);
        signatureAlgorithm.update(contentBytes);
        try {
            return signatureAlgorithm.verify(signatureBytes);
        }
        catch (final SignatureException e) {
            return false;
        }
    }
    
    public static X509Certificate verify(final Signature signatureAlgorithm, final X509TrustManager trustManager, final List<String> certChainBase64, final byte[] signatureBytes, final byte[] contentBytes) throws InvalidKeyException, SignatureException {
        CertificateFactory certificateFactory;
        try {
            certificateFactory = getX509CertificateFactory();
        }
        catch (final CertificateException e) {
            return null;
        }
        final X509Certificate[] certificates = new X509Certificate[certChainBase64.size()];
        int currentCert = 0;
        for (final String certBase64 : certChainBase64) {
            final byte[] certDer = Base64.decodeBase64(certBase64);
            final ByteArrayInputStream bis = new ByteArrayInputStream(certDer);
            try {
                final Certificate cert = certificateFactory.generateCertificate(bis);
                if (!(cert instanceof X509Certificate)) {
                    return null;
                }
                certificates[currentCert++] = (X509Certificate)cert;
            }
            catch (final CertificateException e2) {
                return null;
            }
        }
        try {
            trustManager.checkServerTrusted(certificates, "RSA");
        }
        catch (final CertificateException e3) {
            return null;
        }
        final PublicKey pubKey = certificates[0].getPublicKey();
        if (verify(signatureAlgorithm, pubKey, signatureBytes, contentBytes)) {
            return certificates[0];
        }
        return null;
    }
    
    public static CertificateFactory getX509CertificateFactory() throws CertificateException {
        return CertificateFactory.getInstance("X.509");
    }
    
    public static void loadKeyStoreFromCertificates(final KeyStore keyStore, final CertificateFactory certificateFactory, final InputStream certificateStream) throws GeneralSecurityException {
        int i = 0;
        for (final Certificate cert : certificateFactory.generateCertificates(certificateStream)) {
            keyStore.setCertificateEntry(String.valueOf(i), cert);
            ++i;
        }
    }
    
    @Beta
    public static KeyStore createMtlsKeyStore(final InputStream certAndKey) throws GeneralSecurityException, IOException {
        final KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(null);
        PemReader.Section certSection = null;
        PemReader.Section keySection = null;
        final PemReader reader = new PemReader(new InputStreamReader(certAndKey));
        while (certSection == null || keySection == null) {
            final PemReader.Section section = reader.readNextSection();
            if (section == null) {
                break;
            }
            if (certSection == null && "CERTIFICATE".equals(section.getTitle())) {
                certSection = section;
            }
            else {
                if (!"PRIVATE KEY".equals(section.getTitle())) {
                    continue;
                }
                keySection = section;
            }
        }
        if (certSection == null) {
            throw new IllegalArgumentException("certificate is missing from certAndKey string");
        }
        if (keySection == null) {
            throw new IllegalArgumentException("private key is missing from certAndKey string");
        }
        final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        final X509Certificate cert = (X509Certificate)certFactory.generateCertificate(new ByteArrayInputStream(certSection.getBase64DecodedBytes()));
        final PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(keySection.getBase64DecodedBytes());
        final PrivateKey key = KeyFactory.getInstance(cert.getPublicKey().getAlgorithm()).generatePrivate(keySpecPKCS8);
        keystore.setKeyEntry("alias", key, new char[0], new X509Certificate[] { cert });
        return keystore;
    }
    
    private SecurityUtils() {
    }
}
