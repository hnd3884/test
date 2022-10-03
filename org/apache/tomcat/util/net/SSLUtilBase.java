package org.apache.tomcat.util.net;

import org.apache.juli.logging.LogFactory;
import java.security.cert.CertificateFactory;
import java.security.cert.CertStoreParameters;
import java.security.cert.CRL;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.CertSelector;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509CertSelector;
import java.security.cert.CertificateException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.net.ssl.ManagerFactoryParameters;
import java.security.cert.CertPathParameters;
import java.security.cert.CRLException;
import javax.net.ssl.CertPathTrustManagerParameters;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import java.util.Enumeration;
import org.apache.tomcat.util.net.jsse.JSSEKeyManager;
import javax.net.ssl.X509KeyManager;
import java.util.Locale;
import java.security.Key;
import java.security.cert.Certificate;
import org.apache.tomcat.util.net.jsse.PEMFile;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSessionContext;
import java.security.SecureRandom;
import java.net.URI;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.apache.tomcat.util.security.KeyStoreUtil;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.file.ConfigFileLoader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public abstract class SSLUtilBase implements SSLUtil
{
    private static final Log log;
    private static final StringManager sm;
    protected final SSLHostConfig sslHostConfig;
    protected final SSLHostConfigCertificate certificate;
    private final String[] enabledProtocols;
    private final String[] enabledCiphers;
    
    protected SSLUtilBase(final SSLHostConfigCertificate certificate) {
        this(certificate, true);
    }
    
    protected SSLUtilBase(final SSLHostConfigCertificate certificate, final boolean warnTls13) {
        this.certificate = certificate;
        this.sslHostConfig = certificate.getSSLHostConfig();
        final Set<String> configuredProtocols = this.sslHostConfig.getProtocols();
        final Set<String> implementedProtocols = this.getImplementedProtocols();
        if (!implementedProtocols.contains("TLSv1.3") && !this.sslHostConfig.isExplicitlyRequestedProtocol("TLSv1.3")) {
            configuredProtocols.remove("TLSv1.3");
        }
        if (!implementedProtocols.contains("SSLv2Hello") && !this.sslHostConfig.isExplicitlyRequestedProtocol("SSLv2Hello")) {
            configuredProtocols.remove("SSLv2Hello");
        }
        final List<String> enabledProtocols = getEnabled("protocols", this.getLog(), warnTls13, configuredProtocols, implementedProtocols);
        if (enabledProtocols.contains("SSLv3")) {
            SSLUtilBase.log.warn((Object)SSLUtilBase.sm.getString("sslUtilBase.ssl3"));
        }
        this.enabledProtocols = enabledProtocols.toArray(new String[0]);
        if (enabledProtocols.contains("TLSv1.3") && this.sslHostConfig.getCertificateVerification() == SSLHostConfig.CertificateVerification.OPTIONAL && !this.isTls13RenegAuthAvailable() && warnTls13) {
            SSLUtilBase.log.warn((Object)SSLUtilBase.sm.getString("sslUtilBase.tls13.auth"));
        }
        final List<String> configuredCiphers = this.sslHostConfig.getJsseCipherNames();
        final Set<String> implementedCiphers = this.getImplementedCiphers();
        final List<String> enabledCiphers = getEnabled("ciphers", this.getLog(), false, configuredCiphers, implementedCiphers);
        this.enabledCiphers = enabledCiphers.toArray(new String[0]);
    }
    
    static <T> List<T> getEnabled(final String name, final Log log, final boolean warnOnSkip, final Collection<T> configured, final Collection<T> implemented) {
        final List<T> enabled = new ArrayList<T>();
        if (implemented.size() == 0) {
            enabled.addAll((Collection<? extends T>)configured);
        }
        else {
            enabled.addAll((Collection<? extends T>)configured);
            enabled.retainAll(implemented);
            if (enabled.isEmpty()) {
                throw new IllegalArgumentException(SSLUtilBase.sm.getString("sslUtilBase.noneSupported", new Object[] { name, configured }));
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)SSLUtilBase.sm.getString("sslUtilBase.active", new Object[] { name, enabled }));
            }
            if ((log.isDebugEnabled() || warnOnSkip) && enabled.size() != configured.size()) {
                final List<T> skipped = new ArrayList<T>((Collection<? extends T>)configured);
                skipped.removeAll(enabled);
                final String msg = SSLUtilBase.sm.getString("sslUtilBase.skipped", new Object[] { name, skipped });
                if (warnOnSkip) {
                    log.warn((Object)msg);
                }
                else {
                    log.debug((Object)msg);
                }
            }
        }
        return enabled;
    }
    
    static KeyStore getStore(final String type, final String provider, final String path, final String pass) throws IOException {
        KeyStore ks = null;
        InputStream istream = null;
        try {
            if (provider == null) {
                ks = KeyStore.getInstance(type);
            }
            else {
                ks = KeyStore.getInstance(type, provider);
            }
            if ("DKS".equalsIgnoreCase(type)) {
                final URI uri = ConfigFileLoader.getURI(path);
                ks.load(JreCompat.getInstance().getDomainLoadStoreParameter(uri));
            }
            else {
                if (!"PKCS11".equalsIgnoreCase(type) && !path.isEmpty() && !"NONE".equalsIgnoreCase(path)) {
                    istream = ConfigFileLoader.getInputStream(path);
                }
                char[] storePass = null;
                if (pass != null && (!"".equals(pass) || "JKS".equalsIgnoreCase(type) || "PKCS12".equalsIgnoreCase(type))) {
                    storePass = pass.toCharArray();
                }
                KeyStoreUtil.load(ks, istream, storePass);
            }
        }
        catch (final FileNotFoundException fnfe) {
            throw fnfe;
        }
        catch (final IOException ioe) {
            throw ioe;
        }
        catch (final Exception ex) {
            final String msg = SSLUtilBase.sm.getString("sslUtilBase.keystore_load_failed", new Object[] { type, path, ex.getMessage() });
            SSLUtilBase.log.error((Object)msg, (Throwable)ex);
            throw new IOException(msg);
        }
        finally {
            if (istream != null) {
                try {
                    istream.close();
                }
                catch (final IOException ex2) {}
            }
        }
        return ks;
    }
    
    @Override
    public final SSLContext createSSLContext(final List<String> negotiableProtocols) throws Exception {
        final SSLContext sslContext = this.createSSLContextInternal(negotiableProtocols);
        sslContext.init(this.getKeyManagers(), this.getTrustManagers(), null);
        final SSLSessionContext sessionContext = sslContext.getServerSessionContext();
        if (sessionContext != null) {
            this.configureSessionContext(sessionContext);
        }
        return sslContext;
    }
    
    @Override
    public void configureSessionContext(final SSLSessionContext sslSessionContext) {
        if (this.sslHostConfig.getSessionCacheSize() >= 0) {
            sslSessionContext.setSessionCacheSize(this.sslHostConfig.getSessionCacheSize());
        }
        if (this.sslHostConfig.getSessionTimeout() >= 0) {
            sslSessionContext.setSessionTimeout(this.sslHostConfig.getSessionTimeout());
        }
    }
    
    @Override
    public KeyManager[] getKeyManagers() throws Exception {
        String keyAlias = this.certificate.getCertificateKeyAlias();
        final String algorithm = this.sslHostConfig.getKeyManagerAlgorithm();
        String keyPass = this.certificate.getCertificateKeyPassword();
        if (keyPass == null) {
            keyPass = this.certificate.getCertificateKeystorePassword();
        }
        KeyStore ksUsed;
        final KeyStore ks = ksUsed = this.certificate.getCertificateKeystore();
        final char[] keyPassArray = keyPass.toCharArray();
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
        if (kmf.getProvider().getInfo().indexOf("FIPS") != -1) {
            if (keyAlias != null) {
                SSLUtilBase.log.warn((Object)SSLUtilBase.sm.getString("sslUtilBase.aliasIgnored", new Object[] { keyAlias }));
            }
            kmf.init(ksUsed, keyPassArray);
            return kmf.getKeyManagers();
        }
        if (ks == null) {
            if (this.certificate.getCertificateFile() == null) {
                throw new IOException(SSLUtilBase.sm.getString("sslUtilBase.noCertFile"));
            }
            final PEMFile privateKeyFile = new PEMFile((this.certificate.getCertificateKeyFile() != null) ? this.certificate.getCertificateKeyFile() : this.certificate.getCertificateFile(), keyPass);
            final PEMFile certificateFile = new PEMFile(this.certificate.getCertificateFile());
            final Collection<Certificate> chain = new ArrayList<Certificate>();
            chain.addAll(certificateFile.getCertificates());
            if (this.certificate.getCertificateChainFile() != null) {
                final PEMFile certificateChainFile = new PEMFile(this.certificate.getCertificateChainFile());
                chain.addAll(certificateChainFile.getCertificates());
            }
            if (keyAlias == null) {
                keyAlias = "tomcat";
            }
            ksUsed = KeyStore.getInstance("JKS");
            ksUsed.load(null, null);
            ksUsed.setKeyEntry(keyAlias, privateKeyFile.getPrivateKey(), keyPass.toCharArray(), chain.toArray(new Certificate[0]));
        }
        else {
            if (keyAlias != null && !ks.isKeyEntry(keyAlias)) {
                throw new IOException(SSLUtilBase.sm.getString("sslUtilBase.alias_no_key_entry", new Object[] { keyAlias }));
            }
            if (keyAlias == null) {
                final Enumeration<String> aliases = ks.aliases();
                if (!aliases.hasMoreElements()) {
                    throw new IOException(SSLUtilBase.sm.getString("sslUtilBase.noKeys"));
                }
                while (aliases.hasMoreElements() && keyAlias == null) {
                    keyAlias = aliases.nextElement();
                    if (!ks.isKeyEntry(keyAlias)) {
                        keyAlias = null;
                    }
                }
                if (keyAlias == null) {
                    throw new IOException(SSLUtilBase.sm.getString("sslUtilBase.alias_no_key_entry", new Object[] { null }));
                }
            }
            final Key k = ks.getKey(keyAlias, keyPassArray);
            if (k != null && !"DKS".equalsIgnoreCase(this.certificate.getCertificateKeystoreType()) && "PKCS#8".equalsIgnoreCase(k.getFormat())) {
                final String provider = this.certificate.getCertificateKeystoreProvider();
                if (provider == null) {
                    ksUsed = KeyStore.getInstance(this.certificate.getCertificateKeystoreType());
                }
                else {
                    ksUsed = KeyStore.getInstance(this.certificate.getCertificateKeystoreType(), provider);
                }
                ksUsed.load(null, null);
                ksUsed.setKeyEntry(keyAlias, k, keyPassArray, ks.getCertificateChain(keyAlias));
            }
        }
        kmf.init(ksUsed, keyPassArray);
        final KeyManager[] kms = kmf.getKeyManagers();
        if (kms != null && ksUsed == ks) {
            String alias = keyAlias;
            if ("JKS".equals(this.certificate.getCertificateKeystoreType())) {
                alias = alias.toLowerCase(Locale.ENGLISH);
            }
            for (int i = 0; i < kms.length; ++i) {
                kms[i] = new JSSEKeyManager((X509KeyManager)kms[i], alias);
            }
        }
        return kms;
    }
    
    @Override
    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }
    
    @Override
    public String[] getEnabledCiphers() {
        return this.enabledCiphers;
    }
    
    @Override
    public TrustManager[] getTrustManagers() throws Exception {
        final String className = this.sslHostConfig.getTrustManagerClassName();
        if (className == null || className.length() <= 0) {
            TrustManager[] tms = null;
            final KeyStore trustStore = this.sslHostConfig.getTruststore();
            if (trustStore != null) {
                this.checkTrustStoreEntries(trustStore);
                final String algorithm = this.sslHostConfig.getTruststoreAlgorithm();
                final String crlf = this.sslHostConfig.getCertificateRevocationListFile();
                final boolean revocationEnabled = this.sslHostConfig.getRevocationEnabled();
                if ("PKIX".equalsIgnoreCase(algorithm)) {
                    final TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                    final CertPathParameters params = this.getParameters(crlf, trustStore, revocationEnabled);
                    final ManagerFactoryParameters mfp = new CertPathTrustManagerParameters(params);
                    tmf.init(mfp);
                    tms = tmf.getTrustManagers();
                }
                else {
                    final TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                    tmf.init(trustStore);
                    tms = tmf.getTrustManagers();
                    if (crlf != null && crlf.length() > 0) {
                        throw new CRLException(SSLUtilBase.sm.getString("sslUtilBase.noCrlSupport", new Object[] { algorithm }));
                    }
                    if (this.sslHostConfig.isCertificateVerificationDepthConfigured()) {
                        SSLUtilBase.log.warn((Object)SSLUtilBase.sm.getString("sslUtilBase.noVerificationDepth", new Object[] { algorithm }));
                    }
                }
            }
            return tms;
        }
        final ClassLoader classLoader = this.getClass().getClassLoader();
        final Class<?> clazz = classLoader.loadClass(className);
        if (!TrustManager.class.isAssignableFrom(clazz)) {
            throw new InstantiationException(SSLUtilBase.sm.getString("sslUtilBase.invalidTrustManagerClassName", new Object[] { className }));
        }
        final Object trustManagerObject = clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
        final TrustManager trustManager = (TrustManager)trustManagerObject;
        return new TrustManager[] { trustManager };
    }
    
    private void checkTrustStoreEntries(final KeyStore trustStore) throws Exception {
        final Enumeration<String> aliases = trustStore.aliases();
        if (aliases != null) {
            final Date now = new Date();
            while (aliases.hasMoreElements()) {
                final String alias = aliases.nextElement();
                if (trustStore.isCertificateEntry(alias)) {
                    final Certificate cert = trustStore.getCertificate(alias);
                    if (cert instanceof X509Certificate) {
                        try {
                            ((X509Certificate)cert).checkValidity(now);
                        }
                        catch (final CertificateExpiredException | CertificateNotYetValidException e) {
                            final String msg = SSLUtilBase.sm.getString("sslUtilBase.trustedCertNotValid", new Object[] { alias, ((X509Certificate)cert).getSubjectDN(), e.getMessage() });
                            if (SSLUtilBase.log.isDebugEnabled()) {
                                SSLUtilBase.log.debug((Object)msg, (Throwable)e);
                            }
                            else {
                                SSLUtilBase.log.warn((Object)msg);
                            }
                        }
                    }
                    else {
                        if (!SSLUtilBase.log.isDebugEnabled()) {
                            continue;
                        }
                        SSLUtilBase.log.debug((Object)SSLUtilBase.sm.getString("sslUtilBase.trustedCertNotChecked", new Object[] { alias }));
                    }
                }
            }
        }
    }
    
    protected CertPathParameters getParameters(final String crlf, final KeyStore trustStore, final boolean revocationEnabled) throws Exception {
        final PKIXBuilderParameters xparams = new PKIXBuilderParameters(trustStore, new X509CertSelector());
        if (crlf != null && crlf.length() > 0) {
            final Collection<? extends CRL> crls = this.getCRLs(crlf);
            final CertStoreParameters csp = new CollectionCertStoreParameters(crls);
            final CertStore store = CertStore.getInstance("Collection", csp);
            xparams.addCertStore(store);
            xparams.setRevocationEnabled(true);
        }
        else {
            xparams.setRevocationEnabled(revocationEnabled);
        }
        xparams.setMaxPathLength(this.sslHostConfig.getCertificateVerificationDepth());
        return xparams;
    }
    
    protected Collection<? extends CRL> getCRLs(final String crlf) throws IOException, CRLException, CertificateException {
        Collection<? extends CRL> crls = null;
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            try (final InputStream is = ConfigFileLoader.getInputStream(crlf)) {
                crls = cf.generateCRLs(is);
            }
        }
        catch (final IOException iex) {
            throw iex;
        }
        catch (final CRLException crle) {
            throw crle;
        }
        catch (final CertificateException ce) {
            throw ce;
        }
        return crls;
    }
    
    protected abstract Set<String> getImplementedProtocols();
    
    protected abstract Set<String> getImplementedCiphers();
    
    protected abstract Log getLog();
    
    protected abstract boolean isTls13RenegAuthAvailable();
    
    protected abstract SSLContext createSSLContextInternal(final List<String> p0) throws Exception;
    
    static {
        log = LogFactory.getLog((Class)SSLUtilBase.class);
        sm = StringManager.getManager((Class)SSLUtilBase.class);
    }
}
