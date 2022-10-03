package sun.security.ssl;

import java.util.Map;
import java.security.PrivilegedAction;
import sun.security.util.SecurityConstants;
import java.security.Security;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
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
    static final boolean ALLOW_ECC;
    private static final ProviderList fipsProviderList;
    private static final boolean kerberosAvailable;
    static final String CIPHER_RSA_PKCS1 = "RSA/ECB/PKCS1Padding";
    static final String CIPHER_RC4 = "RC4";
    static final String CIPHER_DES = "DES/CBC/NoPadding";
    static final String CIPHER_3DES = "DESede/CBC/NoPadding";
    static final String CIPHER_AES = "AES/CBC/NoPadding";
    static final String CIPHER_AES_GCM = "AES/GCM/NoPadding";
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
    
    static boolean isKerberosAvailable() {
        return JsseJce.kerberosAvailable;
    }
    
    static Cipher getCipher(final String s) throws NoSuchAlgorithmException {
        try {
            if (SunJSSE.cryptoProvider == null) {
                return Cipher.getInstance(s);
            }
            return Cipher.getInstance(s, SunJSSE.cryptoProvider);
        }
        catch (final NoSuchPaddingException ex) {
            throw new NoSuchAlgorithmException(ex);
        }
    }
    
    static Signature getSignature(final String s) throws NoSuchAlgorithmException {
        if (SunJSSE.cryptoProvider == null) {
            return Signature.getInstance(s);
        }
        if (s == "MD5andSHA1withRSA" && SunJSSE.cryptoProvider.getService("Signature", s) == null) {
            try {
                return Signature.getInstance(s, "SunJSSE");
            }
            catch (final NoSuchProviderException ex) {
                throw new NoSuchAlgorithmException(ex);
            }
        }
        return Signature.getInstance(s, SunJSSE.cryptoProvider);
    }
    
    static KeyGenerator getKeyGenerator(final String s) throws NoSuchAlgorithmException {
        if (SunJSSE.cryptoProvider == null) {
            return KeyGenerator.getInstance(s);
        }
        return KeyGenerator.getInstance(s, SunJSSE.cryptoProvider);
    }
    
    static KeyPairGenerator getKeyPairGenerator(final String s) throws NoSuchAlgorithmException {
        if (SunJSSE.cryptoProvider == null) {
            return KeyPairGenerator.getInstance(s);
        }
        return KeyPairGenerator.getInstance(s, SunJSSE.cryptoProvider);
    }
    
    static KeyAgreement getKeyAgreement(final String s) throws NoSuchAlgorithmException {
        if (SunJSSE.cryptoProvider == null) {
            return KeyAgreement.getInstance(s);
        }
        return KeyAgreement.getInstance(s, SunJSSE.cryptoProvider);
    }
    
    static Mac getMac(final String s) throws NoSuchAlgorithmException {
        if (SunJSSE.cryptoProvider == null) {
            return Mac.getInstance(s);
        }
        return Mac.getInstance(s, SunJSSE.cryptoProvider);
    }
    
    static KeyFactory getKeyFactory(final String s) throws NoSuchAlgorithmException {
        if (SunJSSE.cryptoProvider == null) {
            return KeyFactory.getInstance(s);
        }
        return KeyFactory.getInstance(s, SunJSSE.cryptoProvider);
    }
    
    static AlgorithmParameters getAlgorithmParameters(final String s) throws NoSuchAlgorithmException {
        if (SunJSSE.cryptoProvider == null) {
            return AlgorithmParameters.getInstance(s);
        }
        return AlgorithmParameters.getInstance(s, SunJSSE.cryptoProvider);
    }
    
    static SecureRandom getSecureRandom() throws KeyManagementException {
        if (SunJSSE.cryptoProvider == null) {
            return new SecureRandom();
        }
        try {
            return SecureRandom.getInstance("PKCS11", SunJSSE.cryptoProvider);
        }
        catch (final NoSuchAlgorithmException ex) {
            for (final Provider.Service service : SunJSSE.cryptoProvider.getServices()) {
                if (service.getType().equals("SecureRandom")) {
                    try {
                        return SecureRandom.getInstance(service.getAlgorithm(), SunJSSE.cryptoProvider);
                    }
                    catch (final NoSuchAlgorithmException ex2) {}
                }
            }
            throw new KeyManagementException("FIPS mode: no SecureRandom  implementation found in provider " + SunJSSE.cryptoProvider.getName());
        }
    }
    
    static MessageDigest getMD5() {
        return getMessageDigest("MD5");
    }
    
    static MessageDigest getSHA() {
        return getMessageDigest("SHA");
    }
    
    static MessageDigest getMessageDigest(final String s) {
        try {
            if (SunJSSE.cryptoProvider == null) {
                return MessageDigest.getInstance(s);
            }
            return MessageDigest.getInstance(s, SunJSSE.cryptoProvider);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException("Algorithm " + s + " not available", ex);
        }
    }
    
    static int getRSAKeyLength(final PublicKey publicKey) {
        BigInteger bigInteger;
        if (publicKey instanceof RSAPublicKey) {
            bigInteger = ((RSAPublicKey)publicKey).getModulus();
        }
        else {
            bigInteger = getRSAPublicKeySpec(publicKey).getModulus();
        }
        return bigInteger.bitLength();
    }
    
    static RSAPublicKeySpec getRSAPublicKeySpec(final PublicKey publicKey) {
        if (publicKey instanceof RSAPublicKey) {
            final RSAPublicKey rsaPublicKey = (RSAPublicKey)publicKey;
            return new RSAPublicKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
        }
        try {
            return getKeyFactory("RSA").getKeySpec(publicKey, RSAPublicKeySpec.class);
        }
        catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    static ECParameterSpec getECParameterSpec(final String s) {
        return ECUtil.getECParameterSpec(SunJSSE.cryptoProvider, s);
    }
    
    static String getNamedCurveOid(final ECParameterSpec ecParameterSpec) {
        return ECUtil.getCurveName(SunJSSE.cryptoProvider, ecParameterSpec);
    }
    
    static ECPoint decodePoint(final byte[] array, final EllipticCurve ellipticCurve) throws IOException {
        return ECUtil.decodePoint(array, ellipticCurve);
    }
    
    static byte[] encodePoint(final ECPoint ecPoint, final EllipticCurve ellipticCurve) {
        return ECUtil.encodePoint(ecPoint, ellipticCurve);
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
        ALLOW_ECC = Utilities.getBooleanProperty("com.sun.net.ssl.enableECC", true);
        boolean kerberosAvailable2;
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    Class.forName("sun.security.krb5.PrincipalName", true, null);
                    return null;
                }
            });
            kerberosAvailable2 = true;
        }
        catch (final Exception ex) {
            kerberosAvailable2 = false;
        }
        kerberosAvailable = kerberosAvailable2;
        if (!SunJSSE.isFIPS()) {
            fipsProviderList = null;
        }
        else {
            final Provider provider = Security.getProvider("SUN");
            if (provider == null) {
                throw new RuntimeException("FIPS mode: SUN provider must be installed");
            }
            fipsProviderList = ProviderList.newList(SunJSSE.cryptoProvider, new SunCertificates(provider));
        }
    }
    
    private static final class SunCertificates extends Provider
    {
        private static final long serialVersionUID = -3284138292032213752L;
        
        SunCertificates(final Provider provider) {
            super("SunCertificates", SecurityConstants.PROVIDER_VER, "SunJSSE internal");
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    for (final Map.Entry entry : provider.entrySet()) {
                        final String s = (String)entry.getKey();
                        if (s.startsWith("CertPathValidator.") || s.startsWith("CertPathBuilder.") || s.startsWith("CertStore.") || s.startsWith("CertificateFactory.")) {
                            SunCertificates.this.put(s, entry.getValue());
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
            boolean isAvailable2 = true;
            try {
                JsseJce.getSignature("SHA1withECDSA");
                JsseJce.getSignature("NONEwithECDSA");
                JsseJce.getKeyAgreement("ECDH");
                JsseJce.getKeyFactory("EC");
                JsseJce.getKeyPairGenerator("EC");
                JsseJce.getAlgorithmParameters("EC");
            }
            catch (final Exception ex) {
                isAvailable2 = false;
            }
            isAvailable = isAvailable2;
        }
    }
}
