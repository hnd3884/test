package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.ProviderException;
import java.security.Provider;

public abstract class Legacy8uJSSE extends Provider
{
    private static final long serialVersionUID = 3231825739635378733L;
    private static String info;
    private static String fipsInfo;
    private static Boolean fips;
    static Provider cryptoProvider;
    
    protected static synchronized boolean isFIPS() {
        if (Legacy8uJSSE.fips == null) {
            Legacy8uJSSE.fips = false;
        }
        return Legacy8uJSSE.fips;
    }
    
    private static synchronized void ensureFIPS(final Provider p) {
        if (Legacy8uJSSE.fips == null) {
            Legacy8uJSSE.fips = true;
            Legacy8uJSSE.cryptoProvider = p;
        }
        else {
            if (!Legacy8uJSSE.fips) {
                throw new ProviderException("Legacy8uJSSE already initialized in non-FIPS mode");
            }
            if (Legacy8uJSSE.cryptoProvider != p) {
                throw new ProviderException("Legacy8uJSSE already initialized with FIPS crypto provider " + Legacy8uJSSE.cryptoProvider);
            }
        }
    }
    
    protected Legacy8uJSSE() {
        super("Legacy8uJSSE", 1.8, Legacy8uJSSE.info);
        this.subclassCheck();
        if (Boolean.TRUE.equals(Legacy8uJSSE.fips)) {
            throw new ProviderException("Legacy8uJSSE is already initialized in FIPS mode");
        }
        this.registerAlgorithms(false);
    }
    
    protected Legacy8uJSSE(final Provider cryptoProvider) {
        this(checkNull(cryptoProvider), cryptoProvider.getName());
    }
    
    protected Legacy8uJSSE(final String cryptoProvider) {
        this(null, checkNull(cryptoProvider));
    }
    
    private static <T> T checkNull(final T t) {
        if (t == null) {
            throw new ProviderException("cryptoProvider must not be null");
        }
        return t;
    }
    
    private Legacy8uJSSE(Provider cryptoProvider, final String providerName) {
        super("Legacy8uJSSE", 1.8, Legacy8uJSSE.fipsInfo + providerName + ")");
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
                Legacy8uJSSE.this.doRegister(isfips);
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
        this.put("Signature.MD5andSHA1withRSA", "org.openjsse.legacy8ujsse.sun.security.ssl.RSASignature");
        this.put("KeyManagerFactory.SunX509", "org.openjsse.legacy8ujsse.sun.security.ssl.KeyManagerFactoryImpl$SunX509");
        this.put("KeyManagerFactory.NewSunX509", "org.openjsse.legacy8ujsse.sun.security.ssl.KeyManagerFactoryImpl$X509");
        this.put("Alg.Alias.KeyManagerFactory.PKIX", "NewSunX509");
        this.put("TrustManagerFactory.SunX509", "org.openjsse.legacy8ujsse.sun.security.ssl.TrustManagerFactoryImpl$SimpleFactory");
        this.put("TrustManagerFactory.PKIX", "org.openjsse.legacy8ujsse.sun.security.ssl.TrustManagerFactoryImpl$PKIXFactory");
        this.put("Alg.Alias.TrustManagerFactory.SunPKIX", "PKIX");
        this.put("Alg.Alias.TrustManagerFactory.X509", "PKIX");
        this.put("Alg.Alias.TrustManagerFactory.X.509", "PKIX");
        this.put("SSLContext.TLSv1", "org.openjsse.legacy8ujsse.sun.security.ssl.SSLContextImpl$TLS10Context");
        this.put("SSLContext.TLSv1.1", "org.openjsse.legacy8ujsse.sun.security.ssl.SSLContextImpl$TLS11Context");
        this.put("SSLContext.TLSv1.2", "org.openjsse.legacy8ujsse.sun.security.ssl.SSLContextImpl$TLS12Context");
        this.put("SSLContext.TLS", "org.openjsse.legacy8ujsse.sun.security.ssl.SSLContextImpl$TLSContext");
        if (!isfips) {
            this.put("Alg.Alias.SSLContext.SSL", "TLS");
            this.put("Alg.Alias.SSLContext.SSLv3", "TLSv1");
        }
        this.put("SSLContext.Default", "org.openjsse.legacy8ujsse.sun.security.ssl.SSLContextImpl$DefaultSSLContext");
        this.put("KeyStore.PKCS12", "sun.security.pkcs12.PKCS12KeyStore");
    }
    
    private void subclassCheck() {
        if (this.getClass() != org.openjsse.legacy8ujsse.net.ssl.Legacy8uJSSE.class) {
            throw new AssertionError((Object)("Illegal subclass: " + this.getClass()));
        }
    }
    
    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
    }
    
    static {
        Legacy8uJSSE.info = "Sun JSSE provider(PKCS12, SunX509/PKIX key/trust factories, SSLv3/TLSv1/TLSv1.1/TLSv1.2)";
        Legacy8uJSSE.fipsInfo = "Sun JSSE provider (FIPS mode, crypto provider ";
    }
}
