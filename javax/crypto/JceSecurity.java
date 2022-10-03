package javax.crypto;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.WeakHashMap;
import java.security.Permission;
import java.security.PrivilegedExceptionAction;
import java.util.IdentityHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.nio.file.Path;
import java.io.File;
import java.nio.file.Paths;
import java.security.Security;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import sun.security.jca.GetInstance;
import java.net.URL;
import sun.security.util.Debug;
import java.security.Provider;
import java.util.Map;
import java.security.SecureRandom;

final class JceSecurity
{
    static final SecureRandom RANDOM;
    private static CryptoPermissions defaultPolicy;
    private static CryptoPermissions exemptPolicy;
    private static final Map<IdentityWrapper, Object> verificationResults;
    private static final Map<Provider, Object> verifyingProviders;
    private static final boolean isRestricted;
    private static final Debug debug;
    private static final Object PROVIDER_VERIFIED;
    private static final URL NULL_URL;
    private static final Map<Class<?>, URL> codeBaseCacheRef;
    
    private JceSecurity() {
    }
    
    static GetInstance.Instance getInstance(final String s, final Class<?> clazz, final String s2, final String s3) throws NoSuchAlgorithmException, NoSuchProviderException {
        final Provider.Service service = GetInstance.getService(s, s2, s3);
        final Exception verificationResult = getVerificationResult(service.getProvider());
        if (verificationResult != null) {
            throw (NoSuchProviderException)new NoSuchProviderException("JCE cannot authenticate the provider " + s3).initCause(verificationResult);
        }
        return GetInstance.getInstance(service, clazz);
    }
    
    static GetInstance.Instance getInstance(final String s, final Class<?> clazz, final String s2, final Provider provider) throws NoSuchAlgorithmException {
        final Provider.Service service = GetInstance.getService(s, s2, provider);
        final Exception verificationResult = getVerificationResult(provider);
        if (verificationResult != null) {
            throw new SecurityException("JCE cannot authenticate the provider " + provider.getName(), verificationResult);
        }
        return GetInstance.getInstance(service, clazz);
    }
    
    static GetInstance.Instance getInstance(final String s, final Class<?> clazz, final String s2) throws NoSuchAlgorithmException {
        final List<Provider.Service> services = GetInstance.getServices(s, s2);
        Throwable t = null;
        for (final Provider.Service service : services) {
            if (!canUseProvider(service.getProvider())) {
                continue;
            }
            try {
                return GetInstance.getInstance(service, clazz);
            }
            catch (final NoSuchAlgorithmException ex) {
                t = ex;
                continue;
            }
            break;
        }
        throw new NoSuchAlgorithmException("Algorithm " + s2 + " not available", t);
    }
    
    static CryptoPermissions verifyExemptJar(final URL url) throws Exception {
        final JarVerifier jarVerifier = new JarVerifier(url, true);
        jarVerifier.verify();
        return jarVerifier.getPermissions();
    }
    
    static void verifyProviderJar(final URL url) throws Exception {
        new JarVerifier(url, false).verify();
    }
    
    static Exception getVerificationResult(final Provider provider) {
        final IdentityWrapper identityWrapper = new IdentityWrapper(provider);
        Object o = JceSecurity.verificationResults.get(identityWrapper);
        if (o == null) {
            synchronized (JceSecurity.class) {
                o = JceSecurity.verificationResults.get(identityWrapper);
                if (o == null) {
                    IdentityWrapper identityWrapper2;
                    while ((identityWrapper2 = IdentityWrapper.queue.poll()) != null) {
                        JceSecurity.verificationResults.remove(identityWrapper2);
                    }
                    if (JceSecurity.verifyingProviders.get(provider) != null) {
                        return new NoSuchProviderException("Recursion during verification");
                    }
                    try {
                        JceSecurity.verifyingProviders.put(provider, Boolean.FALSE);
                        verifyProviderJar(getCodeBase(provider.getClass()));
                        o = JceSecurity.PROVIDER_VERIFIED;
                    }
                    catch (final Exception ex) {
                        o = ex;
                    }
                    finally {
                        JceSecurity.verifyingProviders.remove(provider);
                    }
                    JceSecurity.verificationResults.put(identityWrapper, o);
                    if (JceSecurity.debug != null) {
                        JceSecurity.debug.println("Provider " + provider.getName() + " verification result: " + o);
                    }
                }
            }
        }
        return (o == JceSecurity.PROVIDER_VERIFIED) ? null : ((Exception)o);
    }
    
    static boolean canUseProvider(final Provider provider) {
        return getVerificationResult(provider) == null;
    }
    
    static URL getCodeBase(final Class<?> clazz) {
        synchronized (JceSecurity.codeBaseCacheRef) {
            URL url = JceSecurity.codeBaseCacheRef.get(clazz);
            if (url == null) {
                url = AccessController.doPrivileged((PrivilegedAction<URL>)new PrivilegedAction<URL>() {
                    @Override
                    public URL run() {
                        final ProtectionDomain protectionDomain = clazz.getProtectionDomain();
                        if (protectionDomain != null) {
                            final CodeSource codeSource = protectionDomain.getCodeSource();
                            if (codeSource != null) {
                                return codeSource.getLocation();
                            }
                        }
                        return JceSecurity.NULL_URL;
                    }
                });
                JceSecurity.codeBaseCacheRef.put(clazz, url);
            }
            return (url == JceSecurity.NULL_URL) ? null : url;
        }
    }
    
    private static void setupJurisdictionPolicies() throws Exception {
        final String property = System.getProperty("java.home");
        final String property2 = Security.getProperty("crypto.policy");
        final Path path = (property2 == null) ? null : Paths.get(property2, new String[0]);
        if (path != null && (path.getNameCount() != 1 || path.compareTo(path.getFileName()) != 0)) {
            throw new SecurityException("Invalid policy directory name format: " + property2);
        }
        Path path2;
        if (path == null) {
            path2 = Paths.get(property, "lib", "security");
        }
        else {
            path2 = Paths.get(property, "lib", "security", "policy", property2);
        }
        if (JceSecurity.debug != null) {
            JceSecurity.debug.println("crypto policy directory: " + path2);
        }
        File file = new File(path2.toFile(), "US_export_policy.jar");
        File file2 = new File(path2.toFile(), "local_policy.jar");
        if (property2 == null && (!file.exists() || !file2.exists())) {
            final Path value = Paths.get(property, "lib", "security", "policy", "unlimited");
            file = new File(value.toFile(), "US_export_policy.jar");
            file2 = new File(value.toFile(), "local_policy.jar");
        }
        if (ClassLoader.getSystemResource("javax/crypto/Cipher.class") == null || !file.exists() || !file2.exists()) {
            throw new SecurityException("Cannot locate policy or framework files!");
        }
        final CryptoPermissions cryptoPermissions = new CryptoPermissions();
        final CryptoPermissions cryptoPermissions2 = new CryptoPermissions();
        loadPolicies(file, cryptoPermissions, cryptoPermissions2);
        final CryptoPermissions cryptoPermissions3 = new CryptoPermissions();
        final CryptoPermissions cryptoPermissions4 = new CryptoPermissions();
        loadPolicies(file2, cryptoPermissions3, cryptoPermissions4);
        if (cryptoPermissions.isEmpty() || cryptoPermissions3.isEmpty()) {
            throw new SecurityException("Missing mandatory jurisdiction policy files");
        }
        JceSecurity.defaultPolicy = cryptoPermissions.getMinimum(cryptoPermissions3);
        if (cryptoPermissions2.isEmpty()) {
            JceSecurity.exemptPolicy = (cryptoPermissions4.isEmpty() ? null : cryptoPermissions4);
        }
        else {
            JceSecurity.exemptPolicy = cryptoPermissions2.getMinimum(cryptoPermissions4);
        }
    }
    
    private static void loadPolicies(final File file, final CryptoPermissions cryptoPermissions, final CryptoPermissions cryptoPermissions2) throws Exception {
        final JarFile jarFile = new JarFile(file);
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry jarEntry = entries.nextElement();
            InputStream inputStream = null;
            try {
                if (jarEntry.getName().startsWith("default_")) {
                    inputStream = jarFile.getInputStream(jarEntry);
                    cryptoPermissions.load(inputStream);
                }
                else {
                    if (!jarEntry.getName().startsWith("exempt_")) {
                        continue;
                    }
                    inputStream = jarFile.getInputStream(jarEntry);
                    cryptoPermissions2.load(inputStream);
                }
            }
            finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            JarVerifier.verifyPolicySigned(jarEntry.getCertificates());
        }
        jarFile.close();
    }
    
    static CryptoPermissions getDefaultPolicy() {
        return JceSecurity.defaultPolicy;
    }
    
    static CryptoPermissions getExemptPolicy() {
        return JceSecurity.exemptPolicy;
    }
    
    static boolean isRestricted() {
        return JceSecurity.isRestricted;
    }
    
    static {
        RANDOM = new SecureRandom();
        JceSecurity.defaultPolicy = null;
        JceSecurity.exemptPolicy = null;
        verificationResults = new ConcurrentHashMap<IdentityWrapper, Object>();
        verifyingProviders = new IdentityHashMap<Provider, Object>();
        debug = Debug.getInstance("jca", "Cipher");
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    setupJurisdictionPolicies();
                    return null;
                }
            });
            isRestricted = !JceSecurity.defaultPolicy.implies(CryptoAllPermission.INSTANCE);
        }
        catch (final Exception ex) {
            throw new SecurityException("Can not initialize cryptographic mechanism", ex);
        }
        PROVIDER_VERIFIED = Boolean.TRUE;
        try {
            NULL_URL = new URL("http://null.oracle.com/");
        }
        catch (final Exception ex2) {
            throw new RuntimeException(ex2);
        }
        codeBaseCacheRef = new WeakHashMap<Class<?>, URL>();
    }
    
    private static final class IdentityWrapper
    {
        static ConcurrentLinkedQueue<IdentityWrapper> queue;
        final WeakReference<Provider> obj;
        final int hashCode;
        
        IdentityWrapper(final Provider provider) {
            this.hashCode = System.identityHashCode(provider);
            this.obj = new WeakReference<Provider>(provider);
        }
        
        @Override
        public boolean equals(final Object o) {
            final Provider provider = this.obj.get();
            if (provider == null) {
                IdentityWrapper.queue.add(this);
                return false;
            }
            return this == o || (o instanceof IdentityWrapper && provider == ((IdentityWrapper)o).obj.get());
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        static {
            IdentityWrapper.queue = new ConcurrentLinkedQueue<IdentityWrapper>();
        }
    }
}
