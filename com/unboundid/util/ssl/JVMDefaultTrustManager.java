package com.unboundid.util.ssl;

import com.unboundid.util.ssl.cert.AuthorityKeyIdentifierExtension;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.util.Date;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Enumeration;
import com.unboundid.util.ObjectPair;
import com.unboundid.util.ssl.cert.SubjectKeyIdentifierExtension;
import com.unboundid.util.ssl.cert.X509CertificateExtension;
import java.util.LinkedHashMap;
import com.unboundid.util.Debug;
import java.util.Collections;
import com.unboundid.util.StaticUtils;
import com.unboundid.asn1.ASN1OctetString;
import java.util.Map;
import java.security.KeyStore;
import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;
import javax.net.ssl.X509TrustManager;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class JVMDefaultTrustManager implements X509TrustManager, Serializable
{
    private static final AtomicReference<JVMDefaultTrustManager> INSTANCE;
    private static final String PROPERTY_JAVA_HOME = "java.home";
    static final String[] FILE_EXTENSIONS;
    private static final X509Certificate[] NO_CERTIFICATES;
    private static final long serialVersionUID = -8587938729712485943L;
    private final CertificateException certificateException;
    private final File caCertsFile;
    private final KeyStore keystore;
    private final Map<ASN1OctetString, X509Certificate> trustedCertsBySignature;
    private final Map<ASN1OctetString, com.unboundid.util.ssl.cert.X509Certificate> trustedCertsByKeyID;
    
    JVMDefaultTrustManager(final String javaHomePropertyName) {
        final String javaHomePath = StaticUtils.getSystemProperty(javaHomePropertyName);
        if (javaHomePath == null) {
            this.certificateException = new CertificateException(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_NO_JAVA_HOME.get(javaHomePropertyName));
            this.caCertsFile = null;
            this.keystore = null;
            this.trustedCertsBySignature = Collections.emptyMap();
            this.trustedCertsByKeyID = Collections.emptyMap();
            return;
        }
        final File javaHomeDirectory = new File(javaHomePath);
        if (!javaHomeDirectory.exists() || !javaHomeDirectory.isDirectory()) {
            this.certificateException = new CertificateException(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_INVALID_JAVA_HOME.get(javaHomePropertyName, javaHomePath));
            this.caCertsFile = null;
            this.keystore = null;
            this.trustedCertsBySignature = Collections.emptyMap();
            this.trustedCertsByKeyID = Collections.emptyMap();
            return;
        }
        ObjectPair<KeyStore, File> keystorePair;
        try {
            keystorePair = getJVMDefaultKeyStore(javaHomeDirectory);
        }
        catch (final CertificateException ce) {
            Debug.debugException(ce);
            this.certificateException = ce;
            this.caCertsFile = null;
            this.keystore = null;
            this.trustedCertsBySignature = Collections.emptyMap();
            this.trustedCertsByKeyID = Collections.emptyMap();
            return;
        }
        this.keystore = keystorePair.getFirst();
        this.caCertsFile = keystorePair.getSecond();
        final LinkedHashMap<ASN1OctetString, X509Certificate> certsBySignature = new LinkedHashMap<ASN1OctetString, X509Certificate>(StaticUtils.computeMapCapacity(50));
        final LinkedHashMap<ASN1OctetString, com.unboundid.util.ssl.cert.X509Certificate> certsByKeyID = new LinkedHashMap<ASN1OctetString, com.unboundid.util.ssl.cert.X509Certificate>(StaticUtils.computeMapCapacity(50));
        try {
            final Enumeration<String> aliasEnumeration = this.keystore.aliases();
            while (aliasEnumeration.hasMoreElements()) {
                final String alias = aliasEnumeration.nextElement();
                try {
                    final X509Certificate certificate = (X509Certificate)this.keystore.getCertificate(alias);
                    if (certificate == null) {
                        continue;
                    }
                    certsBySignature.put(new ASN1OctetString(certificate.getSignature()), certificate);
                    try {
                        final com.unboundid.util.ssl.cert.X509Certificate c = new com.unboundid.util.ssl.cert.X509Certificate(certificate.getEncoded());
                        for (final X509CertificateExtension e : c.getExtensions()) {
                            if (e instanceof SubjectKeyIdentifierExtension) {
                                final SubjectKeyIdentifierExtension skie = (SubjectKeyIdentifierExtension)e;
                                certsByKeyID.put(new ASN1OctetString(skie.getKeyIdentifier().getValue()), c);
                            }
                        }
                    }
                    catch (final Exception e2) {
                        Debug.debugException(e2);
                    }
                }
                catch (final Exception e3) {
                    Debug.debugException(e3);
                }
            }
        }
        catch (final Exception e4) {
            Debug.debugException(e4);
            this.certificateException = new CertificateException(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_ERROR_ITERATING_THROUGH_CACERTS.get(this.caCertsFile.getAbsolutePath(), StaticUtils.getExceptionMessage(e4)), e4);
            this.trustedCertsBySignature = Collections.emptyMap();
            this.trustedCertsByKeyID = Collections.emptyMap();
            return;
        }
        this.trustedCertsBySignature = Collections.unmodifiableMap((Map<? extends ASN1OctetString, ? extends X509Certificate>)certsBySignature);
        this.trustedCertsByKeyID = Collections.unmodifiableMap((Map<? extends ASN1OctetString, ? extends com.unboundid.util.ssl.cert.X509Certificate>)certsByKeyID);
        this.certificateException = null;
    }
    
    public static JVMDefaultTrustManager getInstance() {
        final JVMDefaultTrustManager existingInstance = JVMDefaultTrustManager.INSTANCE.get();
        if (existingInstance != null) {
            return existingInstance;
        }
        final JVMDefaultTrustManager newInstance = new JVMDefaultTrustManager("java.home");
        if (JVMDefaultTrustManager.INSTANCE.compareAndSet(null, newInstance)) {
            return newInstance;
        }
        return JVMDefaultTrustManager.INSTANCE.get();
    }
    
    KeyStore getKeyStore() throws CertificateException {
        if (this.certificateException != null) {
            throw this.certificateException;
        }
        return this.keystore;
    }
    
    public File getCACertsFile() throws CertificateException {
        if (this.certificateException != null) {
            throw this.certificateException;
        }
        return this.caCertsFile;
    }
    
    public Collection<X509Certificate> getTrustedIssuerCertificates() throws CertificateException {
        if (this.certificateException != null) {
            throw this.certificateException;
        }
        return this.trustedCertsBySignature.values();
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.checkTrusted(chain);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        this.checkTrusted(chain);
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        if (this.certificateException != null) {
            return JVMDefaultTrustManager.NO_CERTIFICATES;
        }
        final X509Certificate[] acceptedIssuers = new X509Certificate[this.trustedCertsBySignature.size()];
        return this.trustedCertsBySignature.values().toArray(acceptedIssuers);
    }
    
    private static ObjectPair<KeyStore, File> getJVMDefaultKeyStore(final File javaHomeDirectory) throws CertificateException {
        final File libSecurityCACerts = StaticUtils.constructPath(javaHomeDirectory, "lib", "security", "cacerts");
        final File jreLibSecurityCACerts = StaticUtils.constructPath(javaHomeDirectory, "jre", "lib", "security", "cacerts");
        final ArrayList<File> tryFirstFiles = new ArrayList<File>(2 * JVMDefaultTrustManager.FILE_EXTENSIONS.length + 2);
        tryFirstFiles.add(libSecurityCACerts);
        tryFirstFiles.add(jreLibSecurityCACerts);
        for (final String extension : JVMDefaultTrustManager.FILE_EXTENSIONS) {
            tryFirstFiles.add(new File(libSecurityCACerts.getAbsolutePath() + extension));
            tryFirstFiles.add(new File(jreLibSecurityCACerts.getAbsolutePath() + extension));
        }
        for (final File f : tryFirstFiles) {
            final KeyStore keyStore = loadKeyStore(f);
            if (keyStore != null) {
                return new ObjectPair<KeyStore, File>(keyStore, f);
            }
        }
        final LinkedHashMap<File, CertificateException> exceptions = new LinkedHashMap<File, CertificateException>(StaticUtils.computeMapCapacity(1));
        final ObjectPair<KeyStore, File> keystorePair = searchForKeyStore(javaHomeDirectory, exceptions);
        if (keystorePair != null) {
            return keystorePair;
        }
        if (exceptions.isEmpty()) {
            throw new CertificateException(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_CACERTS_NOT_FOUND_NO_EXCEPTION.get());
        }
        final StringBuilder buffer = new StringBuilder();
        buffer.append(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_CACERTS_NOT_FOUND_WITH_EXCEPTION.get());
        for (final Map.Entry<File, CertificateException> e : exceptions.entrySet()) {
            if (buffer.charAt(buffer.length() - 1) != '.') {
                buffer.append('.');
            }
            buffer.append("  ");
            buffer.append(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_LOAD_ERROR.get(e.getKey().getAbsolutePath(), StaticUtils.getExceptionMessage(e.getValue())));
        }
        throw new CertificateException(buffer.toString());
    }
    
    private static ObjectPair<KeyStore, File> searchForKeyStore(final File directory, final Map<File, CertificateException> exceptions) {
        for (final File f : directory.listFiles()) {
            Label_0211: {
                if (f.isDirectory()) {
                    final ObjectPair<KeyStore, File> p = searchForKeyStore(f, exceptions);
                    if (p != null) {
                        return p;
                    }
                }
                else {
                    final String lowerName = StaticUtils.toLowerCase(f.getName());
                    if (lowerName.equals("cacerts")) {
                        try {
                            final KeyStore keystore = loadKeyStore(f);
                            return new ObjectPair<KeyStore, File>(keystore, f);
                        }
                        catch (final CertificateException ce) {
                            Debug.debugException(ce);
                            exceptions.put(f, ce);
                            break Label_0211;
                        }
                    }
                    for (final String extension : JVMDefaultTrustManager.FILE_EXTENSIONS) {
                        if (lowerName.equals("cacerts" + extension)) {
                            try {
                                final KeyStore keystore2 = loadKeyStore(f);
                                return new ObjectPair<KeyStore, File>(keystore2, f);
                            }
                            catch (final CertificateException ce2) {
                                Debug.debugException(ce2);
                                exceptions.put(f, ce2);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private static KeyStore loadKeyStore(final File f) throws CertificateException {
        if (!f.exists() || !f.isFile()) {
            return null;
        }
        CertificateException firstGetInstanceException = null;
        CertificateException firstLoadException = null;
        for (final String keyStoreType : new String[] { "JKS", "PKCS12" }) {
            Label_0275: {
                KeyStore keyStore;
                try {
                    keyStore = KeyStore.getInstance(keyStoreType);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    if (firstGetInstanceException == null) {
                        firstGetInstanceException = new CertificateException(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_CANNOT_INSTANTIATE_KEYSTORE.get(keyStoreType, StaticUtils.getExceptionMessage(e)), e);
                    }
                    break Label_0275;
                }
                try (final FileInputStream inputStream = new FileInputStream(f)) {
                    keyStore.load(inputStream, null);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    if (firstLoadException == null) {
                        firstLoadException = new CertificateException(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_CANNOT_ERROR_LOADING_KEYSTORE.get(f.getAbsolutePath(), StaticUtils.getExceptionMessage(e)), e);
                    }
                    break Label_0275;
                }
                return keyStore;
            }
        }
        if (firstLoadException != null) {
            throw firstLoadException;
        }
        throw firstGetInstanceException;
    }
    
    void checkTrusted(final X509Certificate[] chain) throws CertificateException {
        if (this.certificateException != null) {
            throw this.certificateException;
        }
        if (chain == null || chain.length == 0) {
            throw new CertificateException(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_NO_CERTS_IN_CHAIN.get());
        }
        boolean foundIssuer = false;
        final Date currentTime = new Date();
        for (final X509Certificate cert : chain) {
            final Date notBefore = cert.getNotBefore();
            if (currentTime.before(notBefore)) {
                throw new CertificateNotYetValidException(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_CERT_NOT_YET_VALID.get(chainToString(chain), String.valueOf(cert.getSubjectDN()), String.valueOf(notBefore)));
            }
            final Date notAfter = cert.getNotAfter();
            if (currentTime.after(notAfter)) {
                throw new CertificateExpiredException(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANAGER_CERT_EXPIRED.get(chainToString(chain), String.valueOf(cert.getSubjectDN()), String.valueOf(notAfter)));
            }
            final ASN1OctetString signature = new ASN1OctetString(cert.getSignature());
            foundIssuer |= (this.trustedCertsBySignature.get(signature) != null);
        }
        if (!foundIssuer) {
            foundIssuer = this.checkIncompleteChain(chain);
        }
        if (!foundIssuer) {
            throw new CertificateException(SSLMessages.ERR_JVM_DEFAULT_TRUST_MANGER_NO_TRUSTED_ISSUER_FOUND.get(chainToString(chain)));
        }
    }
    
    private boolean checkIncompleteChain(final X509Certificate[] chain) {
        try {
            final com.unboundid.util.ssl.cert.X509Certificate c = new com.unboundid.util.ssl.cert.X509Certificate(chain[chain.length - 1].getEncoded());
            if (c.isSelfSigned()) {
                return false;
            }
            for (final X509CertificateExtension e : c.getExtensions()) {
                if (e instanceof AuthorityKeyIdentifierExtension) {
                    final AuthorityKeyIdentifierExtension akie = (AuthorityKeyIdentifierExtension)e;
                    final ASN1OctetString authorityKeyID = new ASN1OctetString(akie.getKeyIdentifier().getValue());
                    final com.unboundid.util.ssl.cert.X509Certificate issuer = this.trustedCertsByKeyID.get(authorityKeyID);
                    if (issuer != null && issuer.isWithinValidityWindow()) {
                        c.verifySignature(issuer);
                        return true;
                    }
                    continue;
                }
            }
        }
        catch (final Exception e2) {
            Debug.debugException(e2);
        }
        return false;
    }
    
    static String chainToString(final X509Certificate[] chain) {
        final StringBuilder buffer = new StringBuilder();
        switch (chain.length) {
            case 0: {
                break;
            }
            case 1: {
                buffer.append('\'');
                buffer.append(chain[0].getSubjectDN());
                buffer.append('\'');
                break;
            }
            case 2: {
                buffer.append('\'');
                buffer.append(chain[0].getSubjectDN());
                buffer.append("' and '");
                buffer.append(chain[1].getSubjectDN());
                buffer.append('\'');
                break;
            }
            default: {
                for (int i = 0; i < chain.length; ++i) {
                    if (i > 0) {
                        buffer.append(", ");
                    }
                    if (i == chain.length - 1) {
                        buffer.append("and ");
                    }
                    buffer.append('\'');
                    buffer.append(chain[i].getSubjectDN());
                    buffer.append('\'');
                }
                break;
            }
        }
        return buffer.toString();
    }
    
    static {
        INSTANCE = new AtomicReference<JVMDefaultTrustManager>();
        FILE_EXTENSIONS = new String[] { ".jks", ".p12", ".pkcs12", ".pfx" };
        NO_CERTIFICATES = new X509Certificate[0];
    }
}
