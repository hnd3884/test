package org.apache.tomcat.util.net;

import org.apache.juli.logging.LogFactory;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.util.Locale;
import java.util.Collection;
import org.apache.tomcat.util.compat.JreCompat;
import org.apache.tomcat.util.net.openssl.ciphers.OpenSSLCipherConfigurationParser;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.KeyManagerFactory;
import java.util.HashSet;
import org.apache.tomcat.util.net.openssl.OpenSSLConf;
import java.security.KeyStore;
import java.util.List;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import java.util.LinkedHashSet;
import javax.management.ObjectName;
import java.util.Set;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import java.io.Serializable;

public class SSLHostConfig implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final Log log;
    private static final StringManager sm;
    private static final String DEFAULT_CIPHERS = "HIGH:!aNULL:!eNULL:!EXPORT:!DES:!RC4:!MD5:!kRSA";
    protected static final String DEFAULT_SSL_HOST_NAME = "_default_";
    protected static final Set<String> SSL_PROTO_ALL_SET;
    private Type configType;
    private String hostName;
    private transient Long openSslConfContext;
    private transient Long openSslContext;
    private String[] enabledCiphers;
    private String[] enabledProtocols;
    private ObjectName oname;
    private Set<String> explicitlyRequestedProtocols;
    private SSLHostConfigCertificate defaultCertificate;
    private Set<SSLHostConfigCertificate> certificates;
    private String certificateRevocationListFile;
    private CertificateVerification certificateVerification;
    private int certificateVerificationDepth;
    private boolean certificateVerificationDepthConfigured;
    private String ciphers;
    private LinkedHashSet<Cipher> cipherList;
    private List<String> jsseCipherNames;
    private String honorCipherOrder;
    private Set<String> protocols;
    private int sessionCacheSize;
    private int sessionTimeout;
    private String keyManagerAlgorithm;
    private boolean revocationEnabled;
    private String sslProtocol;
    private String trustManagerClassName;
    private String truststoreAlgorithm;
    private String truststoreFile;
    private String truststorePassword;
    private String truststoreProvider;
    private String truststoreType;
    private transient KeyStore truststore;
    private String certificateRevocationListPath;
    private String caCertificateFile;
    private String caCertificatePath;
    private boolean disableCompression;
    private boolean disableSessionTickets;
    private boolean insecureRenegotiation;
    private OpenSSLConf openSslConf;
    
    public SSLHostConfig() {
        this.configType = null;
        this.hostName = "_default_";
        this.openSslConfContext = 0L;
        this.openSslContext = 0L;
        this.explicitlyRequestedProtocols = new HashSet<String>();
        this.defaultCertificate = null;
        this.certificates = new LinkedHashSet<SSLHostConfigCertificate>(4);
        this.certificateVerification = CertificateVerification.NONE;
        this.certificateVerificationDepth = 10;
        this.certificateVerificationDepthConfigured = false;
        this.cipherList = null;
        this.jsseCipherNames = null;
        this.honorCipherOrder = null;
        this.protocols = new HashSet<String>();
        this.sessionCacheSize = -1;
        this.sessionTimeout = 86400;
        this.keyManagerAlgorithm = KeyManagerFactory.getDefaultAlgorithm();
        this.revocationEnabled = false;
        this.sslProtocol = "TLS";
        this.truststoreAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        this.truststoreFile = System.getProperty("javax.net.ssl.trustStore");
        this.truststorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        this.truststoreProvider = System.getProperty("javax.net.ssl.trustStoreProvider");
        this.truststoreType = System.getProperty("javax.net.ssl.trustStoreType");
        this.truststore = null;
        this.disableCompression = true;
        this.disableSessionTickets = false;
        this.insecureRenegotiation = false;
        this.openSslConf = null;
        this.setProtocols("all");
    }
    
    public Long getOpenSslConfContext() {
        return this.openSslConfContext;
    }
    
    public void setOpenSslConfContext(final Long openSslConfContext) {
        this.openSslConfContext = openSslConfContext;
    }
    
    public Long getOpenSslContext() {
        return this.openSslContext;
    }
    
    public void setOpenSslContext(final Long openSslContext) {
        this.openSslContext = openSslContext;
    }
    
    public String getConfigType() {
        return this.configType.name();
    }
    
    boolean setProperty(final String name, final Type configType) {
        if (this.configType == null) {
            this.configType = configType;
        }
        else if (configType != this.configType) {
            SSLHostConfig.log.warn((Object)SSLHostConfig.sm.getString("sslHostConfig.mismatch", new Object[] { name, this.getHostName(), configType, this.configType }));
            return false;
        }
        return true;
    }
    
    public String[] getEnabledProtocols() {
        return this.enabledProtocols;
    }
    
    public void setEnabledProtocols(final String[] enabledProtocols) {
        this.enabledProtocols = enabledProtocols;
    }
    
    public String[] getEnabledCiphers() {
        return this.enabledCiphers;
    }
    
    public void setEnabledCiphers(final String[] enabledCiphers) {
        this.enabledCiphers = enabledCiphers;
    }
    
    public ObjectName getObjectName() {
        return this.oname;
    }
    
    public void setObjectName(final ObjectName oname) {
        this.oname = oname;
    }
    
    private void registerDefaultCertificate() {
        if (this.defaultCertificate == null) {
            final SSLHostConfigCertificate defaultCertificate = new SSLHostConfigCertificate(this, SSLHostConfigCertificate.Type.UNDEFINED);
            this.addCertificate(defaultCertificate);
            this.defaultCertificate = defaultCertificate;
        }
    }
    
    public void addCertificate(final SSLHostConfigCertificate certificate) {
        if (this.certificates.size() == 0) {
            this.certificates.add(certificate);
            return;
        }
        if ((this.certificates.size() == 1 && this.certificates.iterator().next().getType() == SSLHostConfigCertificate.Type.UNDEFINED) || certificate.getType() == SSLHostConfigCertificate.Type.UNDEFINED) {
            throw new IllegalArgumentException(SSLHostConfig.sm.getString("sslHostConfig.certificate.notype"));
        }
        this.certificates.add(certificate);
    }
    
    public OpenSSLConf getOpenSslConf() {
        return this.openSslConf;
    }
    
    public void setOpenSslConf(final OpenSSLConf conf) {
        if (conf == null) {
            throw new IllegalArgumentException(SSLHostConfig.sm.getString("sslHostConfig.opensslconf.null"));
        }
        if (this.openSslConf != null) {
            throw new IllegalArgumentException(SSLHostConfig.sm.getString("sslHostConfig.opensslconf.alreadySet"));
        }
        this.setProperty("<OpenSSLConf>", Type.OPENSSL);
        this.openSslConf = conf;
    }
    
    public Set<SSLHostConfigCertificate> getCertificates() {
        return this.getCertificates(false);
    }
    
    public Set<SSLHostConfigCertificate> getCertificates(final boolean createDefaultIfEmpty) {
        if (this.certificates.size() == 0 && createDefaultIfEmpty) {
            this.registerDefaultCertificate();
        }
        return this.certificates;
    }
    
    public String getCertificateKeyPassword() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeyPassword();
    }
    
    public void setCertificateKeyPassword(final String certificateKeyPassword) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeyPassword(certificateKeyPassword);
    }
    
    public void setCertificateRevocationListFile(final String certificateRevocationListFile) {
        this.certificateRevocationListFile = certificateRevocationListFile;
    }
    
    public String getCertificateRevocationListFile() {
        return this.certificateRevocationListFile;
    }
    
    public void setCertificateVerification(final String certificateVerification) {
        try {
            this.certificateVerification = CertificateVerification.fromString(certificateVerification);
        }
        catch (final IllegalArgumentException iae) {
            this.certificateVerification = CertificateVerification.REQUIRED;
            throw iae;
        }
    }
    
    public CertificateVerification getCertificateVerification() {
        return this.certificateVerification;
    }
    
    public void setCertificateVerificationAsString(final String certificateVerification) {
        this.setCertificateVerification(certificateVerification);
    }
    
    public String getCertificateVerificationAsString() {
        return this.certificateVerification.toString();
    }
    
    public void setCertificateVerificationDepth(final int certificateVerificationDepth) {
        this.certificateVerificationDepth = certificateVerificationDepth;
        this.certificateVerificationDepthConfigured = true;
    }
    
    public int getCertificateVerificationDepth() {
        return this.certificateVerificationDepth;
    }
    
    public boolean isCertificateVerificationDepthConfigured() {
        return this.certificateVerificationDepthConfigured;
    }
    
    public void setCiphers(final String ciphersList) {
        if (ciphersList != null && !ciphersList.contains(":")) {
            final StringBuilder sb = new StringBuilder();
            final String[] arr$;
            final String[] ciphers = arr$ = ciphersList.split(",");
            for (final String cipher : arr$) {
                final String trimmed = cipher.trim();
                if (trimmed.length() > 0) {
                    String openSSLName = OpenSSLCipherConfigurationParser.jsseToOpenSSL(trimmed);
                    if (openSSLName == null) {
                        openSSLName = trimmed;
                    }
                    if (sb.length() > 0) {
                        sb.append(':');
                    }
                    sb.append(openSSLName);
                }
            }
            this.ciphers = sb.toString();
        }
        else {
            this.ciphers = ciphersList;
        }
        this.cipherList = null;
        this.jsseCipherNames = null;
    }
    
    public String getCiphers() {
        if (this.ciphers == null) {
            if (!JreCompat.isJre8Available() && Type.JSSE.equals(this.configType)) {
                this.ciphers = "HIGH:!aNULL:!eNULL:!EXPORT:!DES:!RC4:!MD5:!kRSA:!DHE";
            }
            else {
                this.ciphers = "HIGH:!aNULL:!eNULL:!EXPORT:!DES:!RC4:!MD5:!kRSA";
            }
        }
        return this.ciphers;
    }
    
    public LinkedHashSet<Cipher> getCipherList() {
        if (this.cipherList == null) {
            this.cipherList = OpenSSLCipherConfigurationParser.parse(this.getCiphers());
        }
        return this.cipherList;
    }
    
    public List<String> getJsseCipherNames() {
        if (this.jsseCipherNames == null) {
            this.jsseCipherNames = OpenSSLCipherConfigurationParser.convertForJSSE(this.getCipherList());
        }
        return this.jsseCipherNames;
    }
    
    public void setHonorCipherOrder(final String honorCipherOrder) {
        this.honorCipherOrder = honorCipherOrder;
    }
    
    public String getHonorCipherOrder() {
        return this.honorCipherOrder;
    }
    
    public void setHostName(final String hostName) {
        this.hostName = hostName.toLowerCase(Locale.ENGLISH);
    }
    
    public String getHostName() {
        return this.hostName;
    }
    
    public void setProtocols(final String input) {
        this.protocols.clear();
        this.explicitlyRequestedProtocols.clear();
        for (final String value : input.split("(?=[-+,])")) {
            String trimmed = value.trim();
            if (trimmed.length() > 1) {
                if (trimmed.charAt(0) == '+') {
                    trimmed = trimmed.substring(1).trim();
                    if (trimmed.equalsIgnoreCase("all")) {
                        this.protocols.addAll(SSLHostConfig.SSL_PROTO_ALL_SET);
                    }
                    else {
                        this.protocols.add(trimmed);
                        this.explicitlyRequestedProtocols.add(trimmed);
                    }
                }
                else if (trimmed.charAt(0) == '-') {
                    trimmed = trimmed.substring(1).trim();
                    if (trimmed.equalsIgnoreCase("all")) {
                        this.protocols.removeAll(SSLHostConfig.SSL_PROTO_ALL_SET);
                    }
                    else {
                        this.protocols.remove(trimmed);
                        this.explicitlyRequestedProtocols.remove(trimmed);
                    }
                }
                else {
                    if (trimmed.charAt(0) == ',') {
                        trimmed = trimmed.substring(1).trim();
                    }
                    if (!this.protocols.isEmpty()) {
                        SSLHostConfig.log.warn((Object)SSLHostConfig.sm.getString("sslHostConfig.prefix_missing", new Object[] { trimmed, this.getHostName() }));
                    }
                    if (trimmed.equalsIgnoreCase("all")) {
                        this.protocols.addAll(SSLHostConfig.SSL_PROTO_ALL_SET);
                    }
                    else {
                        this.protocols.add(trimmed);
                        this.explicitlyRequestedProtocols.add(trimmed);
                    }
                }
            }
        }
    }
    
    public Set<String> getProtocols() {
        return this.protocols;
    }
    
    boolean isExplicitlyRequestedProtocol(final String protocol) {
        return this.explicitlyRequestedProtocols.contains(protocol);
    }
    
    public void setSessionCacheSize(final int sessionCacheSize) {
        this.sessionCacheSize = sessionCacheSize;
    }
    
    public int getSessionCacheSize() {
        return this.sessionCacheSize;
    }
    
    public void setSessionTimeout(final int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
    
    public int getSessionTimeout() {
        return this.sessionTimeout;
    }
    
    public String getCertificateKeyAlias() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeyAlias();
    }
    
    public void setCertificateKeyAlias(final String certificateKeyAlias) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeyAlias(certificateKeyAlias);
    }
    
    public String getCertificateKeystoreFile() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeystoreFile();
    }
    
    public void setCertificateKeystoreFile(final String certificateKeystoreFile) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeystoreFile(certificateKeystoreFile);
    }
    
    public String getCertificateKeystorePassword() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeystorePassword();
    }
    
    public void setCertificateKeystorePassword(final String certificateKeystorePassword) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeystorePassword(certificateKeystorePassword);
    }
    
    public String getCertificateKeystoreProvider() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeystoreProvider();
    }
    
    public void setCertificateKeystoreProvider(final String certificateKeystoreProvider) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeystoreProvider(certificateKeystoreProvider);
    }
    
    public String getCertificateKeystoreType() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeystoreType();
    }
    
    public void setCertificateKeystoreType(final String certificateKeystoreType) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeystoreType(certificateKeystoreType);
    }
    
    public void setKeyManagerAlgorithm(final String keyManagerAlgorithm) {
        this.setProperty("keyManagerAlgorithm", Type.JSSE);
        this.keyManagerAlgorithm = keyManagerAlgorithm;
    }
    
    public String getKeyManagerAlgorithm() {
        return this.keyManagerAlgorithm;
    }
    
    public void setRevocationEnabled(final boolean revocationEnabled) {
        this.setProperty("revocationEnabled", Type.JSSE);
        this.revocationEnabled = revocationEnabled;
    }
    
    public boolean getRevocationEnabled() {
        return this.revocationEnabled;
    }
    
    public void setSslProtocol(final String sslProtocol) {
        this.setProperty("sslProtocol", Type.JSSE);
        this.sslProtocol = sslProtocol;
    }
    
    public String getSslProtocol() {
        return this.sslProtocol;
    }
    
    public void setTrustManagerClassName(final String trustManagerClassName) {
        this.setProperty("trustManagerClassName", Type.JSSE);
        this.trustManagerClassName = trustManagerClassName;
    }
    
    public String getTrustManagerClassName() {
        return this.trustManagerClassName;
    }
    
    public void setTruststoreAlgorithm(final String truststoreAlgorithm) {
        this.setProperty("truststoreAlgorithm", Type.JSSE);
        this.truststoreAlgorithm = truststoreAlgorithm;
    }
    
    public String getTruststoreAlgorithm() {
        return this.truststoreAlgorithm;
    }
    
    public void setTruststoreFile(final String truststoreFile) {
        this.setProperty("truststoreFile", Type.JSSE);
        this.truststoreFile = truststoreFile;
    }
    
    public String getTruststoreFile() {
        return this.truststoreFile;
    }
    
    public void setTruststorePassword(final String truststorePassword) {
        this.setProperty("truststorePassword", Type.JSSE);
        this.truststorePassword = truststorePassword;
    }
    
    public String getTruststorePassword() {
        return this.truststorePassword;
    }
    
    public void setTruststoreProvider(final String truststoreProvider) {
        this.setProperty("truststoreProvider", Type.JSSE);
        this.truststoreProvider = truststoreProvider;
    }
    
    public String getTruststoreProvider() {
        if (this.truststoreProvider != null) {
            return this.truststoreProvider;
        }
        final Set<SSLHostConfigCertificate> certificates = this.getCertificates();
        if (certificates.size() == 1) {
            return certificates.iterator().next().getCertificateKeystoreProvider();
        }
        return SSLHostConfigCertificate.DEFAULT_KEYSTORE_PROVIDER;
    }
    
    public void setTruststoreType(final String truststoreType) {
        this.setProperty("truststoreType", Type.JSSE);
        this.truststoreType = truststoreType;
    }
    
    public String getTruststoreType() {
        if (this.truststoreType == null) {
            final Set<SSLHostConfigCertificate> certificates = this.getCertificates();
            if (certificates.size() == 1) {
                final String keystoreType = certificates.iterator().next().getCertificateKeystoreType();
                if (!"PKCS12".equalsIgnoreCase(keystoreType)) {
                    return keystoreType;
                }
            }
            return SSLHostConfigCertificate.DEFAULT_KEYSTORE_TYPE;
        }
        return this.truststoreType;
    }
    
    public void setTrustStore(final KeyStore truststore) {
        this.truststore = truststore;
    }
    
    public KeyStore getTruststore() throws IOException {
        KeyStore result = this.truststore;
        if (result == null && this.truststoreFile != null) {
            try {
                result = SSLUtilBase.getStore(this.getTruststoreType(), this.getTruststoreProvider(), this.getTruststoreFile(), this.getTruststorePassword());
            }
            catch (final IOException ioe) {
                final Throwable cause = ioe.getCause();
                if (!(cause instanceof UnrecoverableKeyException)) {
                    throw ioe;
                }
                SSLHostConfig.log.warn((Object)SSLHostConfig.sm.getString("sslHostConfig.invalid_truststore_password"), cause);
                result = SSLUtilBase.getStore(this.getTruststoreType(), this.getTruststoreProvider(), this.getTruststoreFile(), null);
            }
        }
        return result;
    }
    
    public String getCertificateChainFile() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateChainFile();
    }
    
    public void setCertificateChainFile(final String certificateChainFile) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateChainFile(certificateChainFile);
    }
    
    public String getCertificateFile() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateFile();
    }
    
    public void setCertificateFile(final String certificateFile) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateFile(certificateFile);
    }
    
    public String getCertificateKeyFile() {
        if (this.defaultCertificate == null) {
            return null;
        }
        return this.defaultCertificate.getCertificateKeyFile();
    }
    
    public void setCertificateKeyFile(final String certificateKeyFile) {
        this.registerDefaultCertificate();
        this.defaultCertificate.setCertificateKeyFile(certificateKeyFile);
    }
    
    public void setCertificateRevocationListPath(final String certificateRevocationListPath) {
        this.setProperty("certificateRevocationListPath", Type.OPENSSL);
        this.certificateRevocationListPath = certificateRevocationListPath;
    }
    
    public String getCertificateRevocationListPath() {
        return this.certificateRevocationListPath;
    }
    
    public void setCaCertificateFile(final String caCertificateFile) {
        if (this.setProperty("caCertificateFile", Type.OPENSSL) && this.truststoreFile != null) {
            this.truststoreFile = null;
        }
        this.caCertificateFile = caCertificateFile;
    }
    
    public String getCaCertificateFile() {
        return this.caCertificateFile;
    }
    
    public void setCaCertificatePath(final String caCertificatePath) {
        if (this.setProperty("caCertificatePath", Type.OPENSSL) && this.truststoreFile != null) {
            this.truststoreFile = null;
        }
        this.caCertificatePath = caCertificatePath;
    }
    
    public String getCaCertificatePath() {
        return this.caCertificatePath;
    }
    
    public void setDisableCompression(final boolean disableCompression) {
        this.setProperty("disableCompression", Type.OPENSSL);
        this.disableCompression = disableCompression;
    }
    
    public boolean getDisableCompression() {
        return this.disableCompression;
    }
    
    public void setDisableSessionTickets(final boolean disableSessionTickets) {
        this.setProperty("disableSessionTickets", Type.OPENSSL);
        this.disableSessionTickets = disableSessionTickets;
    }
    
    public boolean getDisableSessionTickets() {
        return this.disableSessionTickets;
    }
    
    public void setInsecureRenegotiation(final boolean insecureRenegotiation) {
        this.setProperty("insecureRenegotiation", Type.OPENSSL);
        this.insecureRenegotiation = insecureRenegotiation;
    }
    
    public boolean getInsecureRenegotiation() {
        return this.insecureRenegotiation;
    }
    
    public static String adjustRelativePath(final String path) throws FileNotFoundException {
        if (path == null || path.length() == 0) {
            return path;
        }
        String newPath = path;
        File f = new File(newPath);
        if (!f.isAbsolute()) {
            newPath = System.getProperty("catalina.base") + File.separator + newPath;
            f = new File(newPath);
        }
        if (!f.exists()) {
            throw new FileNotFoundException(SSLHostConfig.sm.getString("sslHostConfig.fileNotFound", new Object[] { newPath }));
        }
        return newPath;
    }
    
    static {
        log = LogFactory.getLog((Class)SSLHostConfig.class);
        sm = StringManager.getManager((Class)SSLHostConfig.class);
        (SSL_PROTO_ALL_SET = new HashSet<String>()).add("SSLv2Hello");
        SSLHostConfig.SSL_PROTO_ALL_SET.add("TLSv1");
        SSLHostConfig.SSL_PROTO_ALL_SET.add("TLSv1.1");
        SSLHostConfig.SSL_PROTO_ALL_SET.add("TLSv1.2");
        SSLHostConfig.SSL_PROTO_ALL_SET.add("TLSv1.3");
    }
    
    public enum Type
    {
        JSSE, 
        OPENSSL;
    }
    
    public enum CertificateVerification
    {
        NONE, 
        OPTIONAL_NO_CA, 
        OPTIONAL, 
        REQUIRED;
        
        public static CertificateVerification fromString(final String value) {
            if ("true".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "require".equalsIgnoreCase(value) || "required".equalsIgnoreCase(value)) {
                return CertificateVerification.REQUIRED;
            }
            if ("optional".equalsIgnoreCase(value) || "want".equalsIgnoreCase(value)) {
                return CertificateVerification.OPTIONAL;
            }
            if ("optionalNoCA".equalsIgnoreCase(value) || "optional_no_ca".equalsIgnoreCase(value)) {
                return CertificateVerification.OPTIONAL_NO_CA;
            }
            if ("false".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) || "none".equalsIgnoreCase(value)) {
                return CertificateVerification.NONE;
            }
            throw new IllegalArgumentException(SSLHostConfig.sm.getString("sslHostConfig.certificateVerificationInvalid", new Object[] { value }));
        }
    }
}
