package org.openjsse.sun.security.ssl;

import sun.security.util.ObjectIdentifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.ProviderException;
import java.security.Provider;

public abstract class OpenJSSE extends Provider
{
    public static final double PROVIDER_VER;
    private static final long serialVersionUID = 3231825739635378733L;
    private static String info;
    private static String fipsInfo;
    private static Boolean fips;
    static Provider cryptoProvider;
    
    protected static synchronized boolean isFIPS() {
        if (OpenJSSE.fips == null) {
            OpenJSSE.fips = false;
        }
        return OpenJSSE.fips;
    }
    
    private static synchronized void ensureFIPS(final Provider p) {
        if (OpenJSSE.fips == null) {
            OpenJSSE.fips = true;
            OpenJSSE.cryptoProvider = p;
        }
        else {
            if (!OpenJSSE.fips) {
                throw new ProviderException("OpenJSSE already initialized in non-FIPS mode");
            }
            if (OpenJSSE.cryptoProvider != p) {
                throw new ProviderException("OpenJSSE already initialized with FIPS crypto provider " + OpenJSSE.cryptoProvider);
            }
        }
    }
    
    protected OpenJSSE() {
        super("OpenJSSE", OpenJSSE.PROVIDER_VER, OpenJSSE.info);
        this.subclassCheck();
        if (Boolean.TRUE.equals(OpenJSSE.fips)) {
            throw new ProviderException("OpenJSSE is already initialized in FIPS mode");
        }
        this.registerAlgorithms(false);
    }
    
    protected OpenJSSE(final Provider cryptoProvider) {
        this(checkNull(cryptoProvider), cryptoProvider.getName());
    }
    
    protected OpenJSSE(final String cryptoProvider) {
        this(null, checkNull(cryptoProvider));
    }
    
    private static <T> T checkNull(final T t) {
        if (t == null) {
            throw new ProviderException("cryptoProvider must not be null");
        }
        return t;
    }
    
    private OpenJSSE(Provider cryptoProvider, final String providerName) {
        super("OpenJSSE", OpenJSSE.PROVIDER_VER, OpenJSSE.fipsInfo + providerName + ")");
        this.subclassCheck();
        if (cryptoProvider == null) {
            cryptoProvider = Security.getProvider(providerName);
            if (cryptoProvider == null) {
                throw new ProviderException("Crypto provider not installed: " + providerName);
            }
        }
        ensureFIPS(cryptoProvider);
        this.registerAlgorithms(true);
    }
    
    private void registerAlgorithms(final boolean isfips) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                OpenJSSE.this.doRegister(isfips);
                return null;
            }
        });
    }
    
    private void doRegister(final boolean isfips) {
        if (!isfips) {
            this.put("KeyFactory.RSA", "sun.security.rsa.RSAKeyFactory$Legacy");
            this.put("Alg.Alias.KeyFactory.1.2.840.113549.1.1", "RSA");
            this.put("Alg.Alias.KeyFactory.OID.1.2.840.113549.1.1", "RSA");
            this.put("KeyPairGenerator.RSA", "sun.security.rsa.RSAKeyPairGenerator$Legacy");
            this.put("Alg.Alias.KeyPairGenerator.1.2.840.113549.1.1", "RSA");
            this.put("Alg.Alias.KeyPairGenerator.OID.1.2.840.113549.1.1", "RSA");
            this.put("Signature.MD2withRSA", "sun.security.rsa.RSASignature$MD2withRSA");
            this.put("Alg.Alias.Signature.1.2.840.113549.1.1.2", "MD2withRSA");
            this.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.2", "MD2withRSA");
            this.put("Signature.MD5withRSA", "sun.security.rsa.RSASignature$MD5withRSA");
            this.put("Alg.Alias.Signature.1.2.840.113549.1.1.4", "MD5withRSA");
            this.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.4", "MD5withRSA");
            this.put("Signature.SHA1withRSA", "sun.security.rsa.RSASignature$SHA1withRSA");
            this.put("Alg.Alias.Signature.1.2.840.113549.1.1.5", "SHA1withRSA");
            this.put("Alg.Alias.Signature.OID.1.2.840.113549.1.1.5", "SHA1withRSA");
            this.put("Alg.Alias.Signature.1.3.14.3.2.29", "SHA1withRSA");
            this.put("Alg.Alias.Signature.OID.1.3.14.3.2.29", "SHA1withRSA");
        }
        this.put("Signature.MD5andSHA1withRSA", "sun.security.ssl.RSASignature");
        this.put("Cipher.ChaCha20", "org.openjsse.com.sun.crypto.provider.ChaCha20Cipher$ChaCha20Only");
        this.put("Cipher.ChaCha20 SupportedKeyFormats", "RAW");
        this.put("Cipher.ChaCha20-Poly1305", "org.openjsse.com.sun.crypto.provider.ChaCha20Cipher$ChaCha20Poly1305");
        this.put("Cipher.ChaCha20-Poly1305 SupportedKeyFormats", "RAW");
        this.put("Alg.Alias.Cipher.1.2.840.113549.1.9.16.3.18", "ChaCha20-Poly1305");
        this.put("Alg.Alias.Cipher.OID.1.2.840.113549.1.9.16.3.18", "ChaCha20-Poly1305");
        this.put("KeyGenerator.ChaCha20", "org.openjsse.com.sun.crypto.provider.KeyGeneratorCore$ChaCha20KeyGenerator");
        this.put("AlgorithmParameters.ChaCha20-Poly1305", "org.openjsse.com.sun.crypto.provider.ChaCha20Poly1305Parameters");
        this.put("KeyManagerFactory.SunX509", "org.openjsse.sun.security.ssl.KeyManagerFactoryImpl$SunX509");
        this.put("KeyManagerFactory.NewSunX509", "org.openjsse.sun.security.ssl.KeyManagerFactoryImpl$X509");
        this.put("Alg.Alias.KeyManagerFactory.PKIX", "NewSunX509");
        this.put("TrustManagerFactory.SunX509", "org.openjsse.sun.security.ssl.TrustManagerFactoryImpl$SimpleFactory");
        this.put("TrustManagerFactory.PKIX", "org.openjsse.sun.security.ssl.TrustManagerFactoryImpl$PKIXFactory");
        this.put("Alg.Alias.TrustManagerFactory.SunPKIX", "PKIX");
        this.put("Alg.Alias.TrustManagerFactory.X509", "PKIX");
        this.put("Alg.Alias.TrustManagerFactory.X.509", "PKIX");
        this.put("SSLContext.TLSv1", "org.openjsse.sun.security.ssl.SSLContextImpl$TLS10Context");
        this.put("SSLContext.TLSv1.1", "org.openjsse.sun.security.ssl.SSLContextImpl$TLS11Context");
        this.put("SSLContext.TLSv1.2", "org.openjsse.sun.security.ssl.SSLContextImpl$TLS12Context");
        this.put("SSLContext.TLSv1.3", "org.openjsse.sun.security.ssl.SSLContextImpl$TLS13Context");
        this.put("SSLContext.TLS", "org.openjsse.sun.security.ssl.SSLContextImpl$TLSContext");
        if (!isfips) {
            this.put("Alg.Alias.SSLContext.SSL", "TLS");
            this.put("Alg.Alias.SSLContext.SSLv3", "TLSv1");
        }
        this.put("SSLContext.Default", "org.openjsse.sun.security.ssl.SSLContextImpl$DefaultSSLContext");
        this.put("KeyStore.PKCS12", "sun.security.pkcs12.PKCS12KeyStore");
        this.put("KeyGenerator.SunTlsPrf", "org.openjsse.com.sun.crypto.provider.TlsPrfGenerator$V10");
        this.put("KeyGenerator.SunTls12Prf", "org.openjsse.com.sun.crypto.provider.TlsPrfGenerator$V12");
        this.put("KeyGenerator.SunTlsMasterSecret", "org.openjsse.com.sun.crypto.provider.TlsMasterSecretGenerator");
        this.put("Alg.Alias.KeyGenerator.SunTls12MasterSecret", "SunTlsMasterSecret");
        this.put("Alg.Alias.KeyGenerator.SunTlsExtendedMasterSecret", "SunTlsMasterSecret");
        this.put("KeyGenerator.SunTlsKeyMaterial", "org.openjsse.com.sun.crypto.provider.TlsKeyMaterialGenerator");
        this.put("Alg.Alias.KeyGenerator.SunTls12KeyMaterial", "SunTlsKeyMaterial");
        this.put("KeyGenerator.SunTlsRsaPremasterSecret", "org.openjsse.com.sun.crypto.provider.TlsRsaPremasterSecretGenerator");
        this.put("Alg.Alias.KeyGenerator.SunTls12RsaPremasterSecret", "SunTlsRsaPremasterSecret");
        if (OpenJSSE.PROVIDER_VER == 1.8) {
            this.put("MessageDigest.SHA3-224", "org.openjsse.sun.security.provider.SHA3$SHA224");
            this.put("MessageDigest.SHA3-256", "org.openjsse.sun.security.provider.SHA3$SHA256");
            this.put("MessageDigest.SHA3-384", "org.openjsse.sun.security.provider.SHA3$SHA384");
            this.put("MessageDigest.SHA3-512", "org.openjsse.sun.security.provider.SHA3$SHA512");
        }
        this.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.7", "SHA3-224");
        this.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.7", "SHA3-224");
        this.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.8", "SHA3-256");
        this.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.8", "SHA3-256");
        this.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.9", "SHA3-384");
        this.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.9", "SHA3-384");
        this.put("Alg.Alias.MessageDigest.2.16.840.1.101.3.4.2.10", "SHA3-512");
        this.put("Alg.Alias.MessageDigest.OID.2.16.840.1.101.3.4.2.10", "SHA3-512");
    }
    
    private void subclassCheck() {
        if (this.getClass() != org.openjsse.net.ssl.OpenJSSE.class) {
            throw new AssertionError((Object)("Illegal subclass: " + this.getClass()));
        }
    }
    
    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
    }
    
    private static ObjectIdentifier oid(final int... values) {
        return AccessController.doPrivileged((PrivilegedAction<ObjectIdentifier>)new PrivilegedAction<ObjectIdentifier>() {
            @Override
            public ObjectIdentifier run() {
                return ObjectIdentifier.newInternal(values);
            }
        });
    }
    
    static {
        OpenJSSE.fipsInfo = "JDK JSSE provider (FIPS mode, crypto provider ";
        PROVIDER_VER = Double.parseDouble(System.getProperty("java.specification.version"));
        OpenJSSE.info = "JDK JSSE provider(PKCS12, SunX509/PKIX key/trust factories, SSLv3/TLSv1/TLSv1.1/TLSv1.2/TLSv1.3)";
    }
}
