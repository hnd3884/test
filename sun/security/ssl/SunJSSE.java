package sun.security.ssl;

import java.util.Iterator;
import sun.security.rsa.SunRsaSignEntries;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import sun.security.util.SecurityConstants;
import java.security.ProviderException;
import java.security.Provider;

public abstract class SunJSSE extends Provider
{
    private static final long serialVersionUID = 3231825739635378733L;
    private static String info;
    private static String fipsInfo;
    private static Boolean fips;
    static Provider cryptoProvider;
    
    protected static synchronized boolean isFIPS() {
        if (SunJSSE.fips == null) {
            SunJSSE.fips = false;
        }
        return SunJSSE.fips;
    }
    
    private static synchronized void ensureFIPS(final Provider cryptoProvider) {
        if (SunJSSE.fips == null) {
            SunJSSE.fips = true;
            SunJSSE.cryptoProvider = cryptoProvider;
        }
        else {
            if (!SunJSSE.fips) {
                throw new ProviderException("SunJSSE already initialized in non-FIPS mode");
            }
            if (SunJSSE.cryptoProvider != cryptoProvider) {
                throw new ProviderException("SunJSSE already initialized with FIPS crypto provider " + SunJSSE.cryptoProvider);
            }
        }
    }
    
    protected SunJSSE() {
        super("SunJSSE", SecurityConstants.PROVIDER_VER, SunJSSE.info);
        this.subclassCheck();
        if (Boolean.TRUE.equals(SunJSSE.fips)) {
            throw new ProviderException("SunJSSE is already initialized in FIPS mode");
        }
        this.registerAlgorithms(false);
    }
    
    protected SunJSSE(final Provider provider) {
        this(checkNull(provider), provider.getName());
    }
    
    protected SunJSSE(final String s) {
        this(null, checkNull(s));
    }
    
    private static <T> T checkNull(final T t) {
        if (t == null) {
            throw new ProviderException("cryptoProvider must not be null");
        }
        return t;
    }
    
    private SunJSSE(Provider provider, final String s) {
        super("SunJSSE", SecurityConstants.PROVIDER_VER, SunJSSE.fipsInfo + s + ")");
        this.subclassCheck();
        if (provider == null) {
            provider = Security.getProvider(s);
            if (provider == null) {
                throw new ProviderException("Crypto provider not installed: " + s);
            }
        }
        ensureFIPS(provider);
        this.registerAlgorithms(true);
    }
    
    private void registerAlgorithms(final boolean b) {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                SunJSSE.this.doRegister(b);
                return null;
            }
        });
    }
    
    private void ps(final String s, final String s2, final String s3, final List<String> list, final HashMap<String, String> hashMap) {
        this.putService(new Service(this, s, s2, s3, list, hashMap));
    }
    
    private void doRegister(final boolean b) {
        if (!b) {
            final Iterator iterator = new SunRsaSignEntries((Provider)this).iterator();
            while (iterator.hasNext()) {
                this.putService((Service)iterator.next());
            }
        }
        this.ps("Signature", "MD5andSHA1withRSA", "sun.security.ssl.RSASignature", null, null);
        this.ps("KeyManagerFactory", "SunX509", "sun.security.ssl.KeyManagerFactoryImpl$SunX509", null, null);
        this.ps("KeyManagerFactory", "NewSunX509", "sun.security.ssl.KeyManagerFactoryImpl$X509", SunEntries.createAliases(new String[] { "PKIX" }), null);
        this.ps("TrustManagerFactory", "SunX509", "sun.security.ssl.TrustManagerFactoryImpl$SimpleFactory", null, null);
        this.ps("TrustManagerFactory", "PKIX", "sun.security.ssl.TrustManagerFactoryImpl$PKIXFactory", SunEntries.createAliases(new String[] { "SunPKIX", "X509", "X.509" }), null);
        this.ps("SSLContext", "TLSv1", "sun.security.ssl.SSLContextImpl$TLS10Context", b ? null : SunEntries.createAliases(new String[] { "SSLv3" }), null);
        this.ps("SSLContext", "TLSv1.1", "sun.security.ssl.SSLContextImpl$TLS11Context", null, null);
        this.ps("SSLContext", "TLSv1.2", "sun.security.ssl.SSLContextImpl$TLS12Context", null, null);
        this.ps("SSLContext", "TLSv1.3", "sun.security.ssl.SSLContextImpl$TLS13Context", null, null);
        this.ps("SSLContext", "TLS", "sun.security.ssl.SSLContextImpl$TLSContext", b ? null : SunEntries.createAliases(new String[] { "SSL" }), null);
        this.ps("SSLContext", "Default", "sun.security.ssl.SSLContextImpl$DefaultSSLContext", null, null);
        this.ps("KeyStore", "PKCS12", "sun.security.pkcs12.PKCS12KeyStore", null, null);
    }
    
    private void subclassCheck() {
        if (this.getClass() != com.sun.net.ssl.internal.ssl.Provider.class) {
            throw new AssertionError((Object)("Illegal subclass: " + this.getClass()));
        }
    }
    
    @Override
    protected final void finalize() throws Throwable {
        super.finalize();
    }
    
    static {
        SunJSSE.info = "Sun JSSE provider(PKCS12, SunX509/PKIX key/trust factories, SSLv3/TLSv1/TLSv1.1/TLSv1.2/TLSv1.3)";
        SunJSSE.fipsInfo = "Sun JSSE provider (FIPS mode, crypto provider ";
    }
}
