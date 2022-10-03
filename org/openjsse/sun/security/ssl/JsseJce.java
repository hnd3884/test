package org.openjsse.sun.security.ssl;

import java.security.AccessController;
import java.util.Map;
import java.security.PrivilegedAction;
import java.security.Security;
import sun.security.jca.Providers;
import java.io.IOException;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import sun.security.util.ECUtil;
import java.security.spec.ECParameterSpec;
import java.security.Key;
import java.security.spec.RSAPublicKeySpec;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.security.PublicKey;
import java.security.MessageDigest;
import java.util.Iterator;
import java.security.KeyManagementException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import javax.crypto.Mac;
import javax.crypto.KeyAgreement;
import java.security.KeyPairGenerator;
import javax.crypto.KeyGenerator;
import java.security.NoSuchProviderException;
import java.security.Signature;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import sun.security.jca.ProviderList;

final class JsseJce
{
    public static double PROVIDER_VER;
    static final boolean ALLOW_ECC;
    private static final ProviderList fipsProviderList;
    static final String CIPHER_RSA_PKCS1 = "RSA/ECB/PKCS1Padding";
    static final String CIPHER_RC4 = "RC4";
    static final String CIPHER_DES = "DES/CBC/NoPadding";
    static final String CIPHER_3DES = "DESede/CBC/NoPadding";
    static final String CIPHER_AES = "AES/CBC/NoPadding";
    static final String CIPHER_AES_GCM = "AES/GCM/NoPadding";
    static final String CIPHER_CHACHA20_POLY1305 = "ChaCha20-Poly1305";
    static final String SIGNATURE_DSA = "DSA";
    static final String SIGNATURE_ECDSA = "SHA1withECDSA";
    static final String SIGNATURE_RAWDSA = "RawDSA";
    static final String SIGNATURE_RAWECDSA = "NONEwithECDSA";
    static final String SIGNATURE_RAWRSA = "NONEwithRSA";
    static final String SIGNATURE_SSLRSA = "MD5andSHA1withRSA";
    
    private JsseJce() {
    }
    
    static boolean isEcAvailable() {
        return EcAvailability.isAvailable;
    }
    
    static Cipher getCipher(final String transformation) throws NoSuchAlgorithmException {
        try {
            if (OpenJSSE.cryptoProvider == null) {
                return Cipher.getInstance(transformation);
            }
            return Cipher.getInstance(transformation, OpenJSSE.cryptoProvider);
        }
        catch (final NoSuchPaddingException e) {
            throw new NoSuchAlgorithmException(e);
        }
    }
    
    static Signature getSignature(final String algorithm) throws NoSuchAlgorithmException {
        if (OpenJSSE.cryptoProvider == null) {
            return Signature.getInstance(algorithm);
        }
        if (algorithm == "MD5andSHA1withRSA" && OpenJSSE.cryptoProvider.getService("Signature", algorithm) == null) {
            try {
                return Signature.getInstance(algorithm, "OpenJSSE");
            }
            catch (final NoSuchProviderException e) {
                throw new NoSuchAlgorithmException(e);
            }
        }
        return Signature.getInstance(algorithm, OpenJSSE.cryptoProvider);
    }
    
    static KeyGenerator getKeyGenerator(final String algorithm) throws NoSuchAlgorithmException {
        if (OpenJSSE.cryptoProvider == null) {
            return KeyGenerator.getInstance(algorithm);
        }
        return KeyGenerator.getInstance(algorithm, OpenJSSE.cryptoProvider);
    }
    
    static KeyPairGenerator getKeyPairGenerator(final String algorithm) throws NoSuchAlgorithmException {
        if (OpenJSSE.cryptoProvider == null) {
            return KeyPairGenerator.getInstance(algorithm);
        }
        return KeyPairGenerator.getInstance(algorithm, OpenJSSE.cryptoProvider);
    }
    
    static KeyAgreement getKeyAgreement(final String algorithm) throws NoSuchAlgorithmException {
        if (OpenJSSE.cryptoProvider == null) {
            return KeyAgreement.getInstance(algorithm);
        }
        return KeyAgreement.getInstance(algorithm, OpenJSSE.cryptoProvider);
    }
    
    static Mac getMac(final String algorithm) throws NoSuchAlgorithmException {
        if (OpenJSSE.cryptoProvider == null) {
            return Mac.getInstance(algorithm);
        }
        return Mac.getInstance(algorithm, OpenJSSE.cryptoProvider);
    }
    
    static KeyFactory getKeyFactory(final String algorithm) throws NoSuchAlgorithmException {
        if (OpenJSSE.cryptoProvider == null) {
            return KeyFactory.getInstance(algorithm);
        }
        return KeyFactory.getInstance(algorithm, OpenJSSE.cryptoProvider);
    }
    
    static AlgorithmParameters getAlgorithmParameters(final String algorithm) throws NoSuchAlgorithmException {
        if (OpenJSSE.cryptoProvider == null) {
            return AlgorithmParameters.getInstance(algorithm);
        }
        return AlgorithmParameters.getInstance(algorithm, OpenJSSE.cryptoProvider);
    }
    
    static SecureRandom getSecureRandom() throws KeyManagementException {
        if (OpenJSSE.cryptoProvider == null) {
            return new SecureRandom();
        }
        try {
            return SecureRandom.getInstance("PKCS11", OpenJSSE.cryptoProvider);
        }
        catch (final NoSuchAlgorithmException ex) {
            for (final Provider.Service s : OpenJSSE.cryptoProvider.getServices()) {
                if (s.getType().equals("SecureRandom")) {
                    try {
                        return SecureRandom.getInstance(s.getAlgorithm(), OpenJSSE.cryptoProvider);
                    }
                    catch (final NoSuchAlgorithmException ex2) {}
                }
            }
            throw new KeyManagementException("FIPS mode: no SecureRandom  implementation found in provider " + OpenJSSE.cryptoProvider.getName());
        }
    }
    
    static MessageDigest getMD5() {
        return getMessageDigest("MD5");
    }
    
    static MessageDigest getSHA() {
        return getMessageDigest("SHA");
    }
    
    static MessageDigest getMessageDigest(final String algorithm) {
        try {
            if (OpenJSSE.cryptoProvider == null) {
                return MessageDigest.getInstance(algorithm);
            }
            return MessageDigest.getInstance(algorithm, OpenJSSE.cryptoProvider);
        }
        catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm " + algorithm + " not available", e);
        }
    }
    
    static int getRSAKeyLength(final PublicKey key) {
        BigInteger modulus;
        if (key instanceof RSAPublicKey) {
            modulus = ((RSAPublicKey)key).getModulus();
        }
        else {
            final RSAPublicKeySpec spec = getRSAPublicKeySpec(key);
            modulus = spec.getModulus();
        }
        return modulus.bitLength();
    }
    
    static RSAPublicKeySpec getRSAPublicKeySpec(final PublicKey key) {
        if (key instanceof RSAPublicKey) {
            final RSAPublicKey rsaKey = (RSAPublicKey)key;
            return new RSAPublicKeySpec(rsaKey.getModulus(), rsaKey.getPublicExponent());
        }
        try {
            final KeyFactory factory = getKeyFactory("RSA");
            return factory.getKeySpec(key, RSAPublicKeySpec.class);
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static ECParameterSpec getECParameterSpec(final String namedCurveOid) {
        return ECUtil.getECParameterSpec(OpenJSSE.cryptoProvider, namedCurveOid);
    }
    
    static String getNamedCurveOid(final ECParameterSpec params) {
        return ECUtil.getCurveName(OpenJSSE.cryptoProvider, params);
    }
    
    static ECPoint decodePoint(final byte[] encoded, final EllipticCurve curve) throws IOException {
        return ECUtil.decodePoint(encoded, curve);
    }
    
    static byte[] encodePoint(final ECPoint point, final EllipticCurve curve) {
        return ECUtil.encodePoint(point, curve);
    }
    
    static Object beginFipsProvider() {
        if (JsseJce.fipsProviderList == null) {
            return null;
        }
        return Providers.beginThreadProviderList(JsseJce.fipsProviderList);
    }
    
    static void endFipsProvider(final Object o) {
        if (JsseJce.fipsProviderList != null) {
            Providers.endThreadProviderList((ProviderList)o);
        }
    }
    
    static {
        JsseJce.PROVIDER_VER = 1.8;
        ALLOW_ECC = Utilities.getBooleanProperty("com.sun.net.ssl.enableECC", true);
        if (!OpenJSSE.isFIPS()) {
            fipsProviderList = null;
        }
        else {
            final Provider sun = Security.getProvider("SUN");
            if (sun == null) {
                throw new RuntimeException("FIPS mode: SUN provider must be installed");
            }
            final Provider sunCerts = new SunCertificates(sun);
            fipsProviderList = ProviderList.newList(OpenJSSE.cryptoProvider, sunCerts);
        }
    }
    
    private static final class SunCertificates extends Provider
    {
        private static final long serialVersionUID = -3284138292032213752L;
        
        SunCertificates(final Provider p) {
            super("SunCertificates", JsseJce.PROVIDER_VER, "OpenJSSE internal");
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    for (final Map.Entry<Object, Object> entry : p.entrySet()) {
                        final String key = entry.getKey();
                        if (key.startsWith("CertPathValidator.") || key.startsWith("CertPathBuilder.") || key.startsWith("CertStore.") || key.startsWith("CertificateFactory.")) {
                            SunCertificates.this.put(key, entry.getValue());
                        }
                    }
                    return null;
                }
            });
        }
    }
    
    private static class EcAvailability
    {
        private static final boolean isAvailable;
        
        static {
            boolean mediator = true;
            try {
                JsseJce.getSignature("SHA1withECDSA");
                JsseJce.getSignature("NONEwithECDSA");
                JsseJce.getKeyAgreement("ECDH");
                JsseJce.getKeyFactory("EC");
                JsseJce.getKeyPairGenerator("EC");
                JsseJce.getAlgorithmParameters("EC");
            }
            catch (final Exception e) {
                mediator = false;
            }
            isAvailable = mediator;
        }
    }
}
