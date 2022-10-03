package com.unboundid.util.ssl;

import java.security.cert.X509Certificate;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import com.unboundid.util.StaticUtils;
import javax.net.ssl.SSLServerSocket;
import java.net.ServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Collection;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.security.SecureRandom;
import com.unboundid.util.Validator;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class SSLUtil
{
    public static final String PROPERTY_DEFAULT_SSL_PROTOCOL = "com.unboundid.util.SSLUtil.defaultSSLProtocol";
    public static final String PROPERTY_ENABLED_SSL_PROTOCOLS = "com.unboundid.util.SSLUtil.enabledSSLProtocols";
    public static final String PROPERTY_ENABLED_SSL_CIPHER_SUITES = "com.unboundid.util.SSLUtil.enabledSSLCipherSuites";
    public static final String SSL_PROTOCOL_TLS_1_3 = "TLSv1.3";
    public static final String SSL_PROTOCOL_TLS_1_2 = "TLSv1.2";
    public static final String SSL_PROTOCOL_TLS_1_1 = "TLSv1.1";
    public static final String SSL_PROTOCOL_TLS_1 = "TLSv1";
    public static final String SSL_PROTOCOL_SSL_3 = "SSLv3";
    public static final String SSL_PROTOCOL_SSL_2_HELLO = "SSLv2Hello";
    private static final AtomicReference<String> DEFAULT_SSL_PROTOCOL;
    private static final AtomicReference<Set<String>> ENABLED_SSL_CIPHER_SUITES;
    private static final AtomicReference<Set<String>> ENABLED_SSL_PROTOCOLS;
    private final KeyManager[] keyManagers;
    private final TrustManager[] trustManagers;
    
    public SSLUtil() {
        this.keyManagers = null;
        this.trustManagers = null;
    }
    
    public SSLUtil(final TrustManager trustManager) {
        this.keyManagers = null;
        if (trustManager == null) {
            this.trustManagers = null;
        }
        else {
            this.trustManagers = new TrustManager[] { trustManager };
        }
    }
    
    public SSLUtil(final TrustManager[] trustManagers) {
        this.keyManagers = null;
        if (trustManagers == null || trustManagers.length == 0) {
            this.trustManagers = null;
        }
        else {
            this.trustManagers = trustManagers;
        }
    }
    
    public SSLUtil(final KeyManager keyManager, final TrustManager trustManager) {
        if (keyManager == null) {
            this.keyManagers = null;
        }
        else {
            this.keyManagers = new KeyManager[] { keyManager };
        }
        if (trustManager == null) {
            this.trustManagers = null;
        }
        else {
            this.trustManagers = new TrustManager[] { trustManager };
        }
    }
    
    public SSLUtil(final KeyManager[] keyManagers, final TrustManager[] trustManagers) {
        if (keyManagers == null || keyManagers.length == 0) {
            this.keyManagers = null;
        }
        else {
            this.keyManagers = keyManagers;
        }
        if (trustManagers == null || trustManagers.length == 0) {
            this.trustManagers = null;
        }
        else {
            this.trustManagers = trustManagers;
        }
    }
    
    public KeyManager[] getKeyManagers() {
        return this.keyManagers;
    }
    
    public TrustManager[] getTrustManagers() {
        return this.trustManagers;
    }
    
    public SSLContext createSSLContext() throws GeneralSecurityException {
        return this.createSSLContext(SSLUtil.DEFAULT_SSL_PROTOCOL.get());
    }
    
    public SSLContext createSSLContext(final String protocol) throws GeneralSecurityException {
        Validator.ensureNotNull(protocol);
        final SSLContext sslContext = SSLContext.getInstance(protocol);
        sslContext.init(this.keyManagers, this.trustManagers, null);
        return sslContext;
    }
    
    public SSLContext createSSLContext(final String protocol, final String provider) throws GeneralSecurityException {
        Validator.ensureNotNull(protocol, provider);
        final SSLContext sslContext = SSLContext.getInstance(protocol, provider);
        sslContext.init(this.keyManagers, this.trustManagers, null);
        return sslContext;
    }
    
    public SSLSocketFactory createSSLSocketFactory() throws GeneralSecurityException {
        return new SetEnabledProtocolsAndCipherSuitesSSLSocketFactory(this.createSSLContext().getSocketFactory(), SSLUtil.ENABLED_SSL_PROTOCOLS.get(), SSLUtil.ENABLED_SSL_CIPHER_SUITES.get());
    }
    
    public SSLSocketFactory createSSLSocketFactory(final String protocol) throws GeneralSecurityException {
        return new SetEnabledProtocolsAndCipherSuitesSSLSocketFactory(this.createSSLContext(protocol).getSocketFactory(), protocol, SSLUtil.ENABLED_SSL_CIPHER_SUITES.get());
    }
    
    public SSLSocketFactory createSSLSocketFactory(final String protocol, final String provider) throws GeneralSecurityException {
        return this.createSSLContext(protocol, provider).getSocketFactory();
    }
    
    public SSLServerSocketFactory createSSLServerSocketFactory() throws GeneralSecurityException {
        return new SetEnabledProtocolsAndCipherSuitesSSLServerSocketFactory(this.createSSLContext().getServerSocketFactory(), SSLUtil.ENABLED_SSL_PROTOCOLS.get(), SSLUtil.ENABLED_SSL_CIPHER_SUITES.get());
    }
    
    public SSLServerSocketFactory createSSLServerSocketFactory(final String protocol) throws GeneralSecurityException {
        return new SetEnabledProtocolsAndCipherSuitesSSLServerSocketFactory(this.createSSLContext(protocol).getServerSocketFactory(), protocol, SSLUtil.ENABLED_SSL_CIPHER_SUITES.get());
    }
    
    public SSLServerSocketFactory createSSLServerSocketFactory(final String protocol, final String provider) throws GeneralSecurityException {
        return this.createSSLContext(protocol, provider).getServerSocketFactory();
    }
    
    public static String getDefaultSSLProtocol() {
        return SSLUtil.DEFAULT_SSL_PROTOCOL.get();
    }
    
    public static void setDefaultSSLProtocol(final String defaultSSLProtocol) {
        Validator.ensureNotNull(defaultSSLProtocol);
        SSLUtil.DEFAULT_SSL_PROTOCOL.set(defaultSSLProtocol);
    }
    
    public static Set<String> getEnabledSSLProtocols() {
        return SSLUtil.ENABLED_SSL_PROTOCOLS.get();
    }
    
    public static void setEnabledSSLProtocols(final Collection<String> enabledSSLProtocols) {
        if (enabledSSLProtocols == null) {
            SSLUtil.ENABLED_SSL_PROTOCOLS.set(Collections.emptySet());
        }
        else {
            SSLUtil.ENABLED_SSL_PROTOCOLS.set(Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(enabledSSLProtocols)));
        }
    }
    
    public static void applyEnabledSSLProtocols(final Socket socket) throws LDAPException {
        try {
            applyEnabledSSLProtocols(socket, SSLUtil.ENABLED_SSL_PROTOCOLS.get());
        }
        catch (final IOException ioe) {
            Debug.debugException(ioe);
            throw new LDAPException(ResultCode.CONNECT_ERROR, ioe.getMessage(), ioe);
        }
    }
    
    static void applyEnabledSSLProtocols(final Socket socket, final Set<String> protocols) throws IOException {
        if (socket == null || !(socket instanceof SSLSocket) || protocols.isEmpty()) {
            return;
        }
        final SSLSocket sslSocket = (SSLSocket)socket;
        final String[] protocolsToEnable = getSSLProtocolsToEnable(protocols, sslSocket.getSupportedProtocols());
        try {
            sslSocket.setEnabledProtocols(protocolsToEnable);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    static void applyEnabledSSLProtocols(final ServerSocket serverSocket, final Set<String> protocols) throws IOException {
        if (serverSocket == null || !(serverSocket instanceof SSLServerSocket) || protocols.isEmpty()) {
            return;
        }
        final SSLServerSocket sslServerSocket = (SSLServerSocket)serverSocket;
        final String[] protocolsToEnable = getSSLProtocolsToEnable(protocols, sslServerSocket.getSupportedProtocols());
        try {
            sslServerSocket.setEnabledProtocols(protocolsToEnable);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    private static String[] getSSLProtocolsToEnable(final Set<String> desiredProtocols, final String[] supportedProtocols) throws IOException {
        final Set<String> lowerProtocols = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(desiredProtocols.size()));
        for (final String s : desiredProtocols) {
            lowerProtocols.add(StaticUtils.toLowerCase(s));
        }
        final ArrayList<String> enabledList = new ArrayList<String>(supportedProtocols.length);
        for (final String supportedProtocol : supportedProtocols) {
            if (lowerProtocols.contains(StaticUtils.toLowerCase(supportedProtocol))) {
                enabledList.add(supportedProtocol);
            }
        }
        if (enabledList.isEmpty()) {
            final StringBuilder enabledBuffer = new StringBuilder();
            final Iterator<String> enabledIterator = desiredProtocols.iterator();
            while (enabledIterator.hasNext()) {
                enabledBuffer.append('\'');
                enabledBuffer.append(enabledIterator.next());
                enabledBuffer.append('\'');
                if (enabledIterator.hasNext()) {
                    enabledBuffer.append(", ");
                }
            }
            final StringBuilder supportedBuffer = new StringBuilder();
            for (int i = 0; i < supportedProtocols.length; ++i) {
                if (i > 0) {
                    supportedBuffer.append(", ");
                }
                supportedBuffer.append('\'');
                supportedBuffer.append(supportedProtocols[i]);
                supportedBuffer.append('\'');
            }
            throw new IOException(SSLMessages.ERR_NO_ENABLED_SSL_PROTOCOLS_AVAILABLE_FOR_SOCKET.get(enabledBuffer.toString(), supportedBuffer.toString(), "com.unboundid.util.SSLUtil.enabledSSLProtocols", SSLUtil.class.getName() + ".setEnabledSSLProtocols"));
        }
        return enabledList.toArray(StaticUtils.NO_STRINGS);
    }
    
    public static Set<String> getEnabledSSLCipherSuites() {
        return SSLUtil.ENABLED_SSL_CIPHER_SUITES.get();
    }
    
    public static void setEnabledSSLCipherSuites(final Collection<String> enabledSSLCipherSuites) {
        if (enabledSSLCipherSuites == null) {
            SSLUtil.ENABLED_SSL_CIPHER_SUITES.set(Collections.emptySet());
        }
        else {
            SSLUtil.ENABLED_SSL_CIPHER_SUITES.set(Collections.unmodifiableSet((Set<? extends String>)new LinkedHashSet<String>(enabledSSLCipherSuites)));
        }
    }
    
    public static void applyEnabledSSLCipherSuites(final Socket socket) throws LDAPException {
        try {
            applyEnabledSSLCipherSuites(socket, SSLUtil.ENABLED_SSL_CIPHER_SUITES.get());
        }
        catch (final IOException ioe) {
            Debug.debugException(ioe);
            throw new LDAPException(ResultCode.CONNECT_ERROR, ioe.getMessage(), ioe);
        }
    }
    
    static void applyEnabledSSLCipherSuites(final Socket socket, final Set<String> cipherSuites) throws IOException {
        if (socket == null || !(socket instanceof SSLSocket) || cipherSuites.isEmpty()) {
            return;
        }
        final SSLSocket sslSocket = (SSLSocket)socket;
        final String[] cipherSuitesToEnable = getSSLCipherSuitesToEnable(cipherSuites, sslSocket.getSupportedCipherSuites());
        try {
            sslSocket.setEnabledCipherSuites(cipherSuitesToEnable);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    static void applyEnabledSSLCipherSuites(final ServerSocket serverSocket, final Set<String> cipherSuites) throws IOException {
        if (serverSocket == null || !(serverSocket instanceof SSLServerSocket) || cipherSuites.isEmpty()) {
            return;
        }
        final SSLServerSocket sslServerSocket = (SSLServerSocket)serverSocket;
        final String[] cipherSuitesToEnable = getSSLCipherSuitesToEnable(cipherSuites, sslServerSocket.getSupportedCipherSuites());
        try {
            sslServerSocket.setEnabledCipherSuites(cipherSuitesToEnable);
        }
        catch (final Exception e) {
            Debug.debugException(e);
        }
    }
    
    private static String[] getSSLCipherSuitesToEnable(final Set<String> desiredCipherSuites, final String[] supportedCipherSuites) throws IOException {
        final Set<String> upperCipherSuites = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(desiredCipherSuites.size()));
        for (final String s : desiredCipherSuites) {
            upperCipherSuites.add(StaticUtils.toUpperCase(s));
        }
        final ArrayList<String> enabledList = new ArrayList<String>(supportedCipherSuites.length);
        for (final String supportedCipherSuite : supportedCipherSuites) {
            if (upperCipherSuites.contains(StaticUtils.toUpperCase(supportedCipherSuite))) {
                enabledList.add(supportedCipherSuite);
            }
        }
        if (enabledList.isEmpty()) {
            final StringBuilder enabledBuffer = new StringBuilder();
            final Iterator<String> enabledIterator = desiredCipherSuites.iterator();
            while (enabledIterator.hasNext()) {
                enabledBuffer.append('\'');
                enabledBuffer.append(enabledIterator.next());
                enabledBuffer.append('\'');
                if (enabledIterator.hasNext()) {
                    enabledBuffer.append(", ");
                }
            }
            final StringBuilder supportedBuffer = new StringBuilder();
            for (int i = 0; i < supportedCipherSuites.length; ++i) {
                if (i > 0) {
                    supportedBuffer.append(", ");
                }
                supportedBuffer.append('\'');
                supportedBuffer.append(supportedCipherSuites[i]);
                supportedBuffer.append('\'');
            }
            throw new IOException(SSLMessages.ERR_NO_ENABLED_SSL_CIPHER_SUITES_AVAILABLE_FOR_SOCKET.get(enabledBuffer.toString(), supportedBuffer.toString(), "com.unboundid.util.SSLUtil.enabledSSLCipherSuites", SSLUtil.class.getName() + ".setEnabledSSLCipherSuites"));
        }
        return enabledList.toArray(StaticUtils.NO_STRINGS);
    }
    
    static void configureSSLDefaults() {
        final String defaultPropValue = StaticUtils.getSystemProperty("com.unboundid.util.SSLUtil.defaultSSLProtocol");
        if (defaultPropValue != null && !defaultPropValue.isEmpty()) {
            SSLUtil.DEFAULT_SSL_PROTOCOL.set(defaultPropValue);
        }
        else {
            try {
                final SSLContext defaultContext = SSLContext.getDefault();
                final String[] supportedProtocols = defaultContext.getSupportedSSLParameters().getProtocols();
                final LinkedHashSet<String> protocolMap = new LinkedHashSet<String>(Arrays.asList(supportedProtocols));
                if (protocolMap.contains("TLSv1.3")) {
                    SSLUtil.DEFAULT_SSL_PROTOCOL.set("TLSv1.3");
                }
                else if (protocolMap.contains("TLSv1.2")) {
                    SSLUtil.DEFAULT_SSL_PROTOCOL.set("TLSv1.2");
                }
                else if (protocolMap.contains("TLSv1.1")) {
                    SSLUtil.DEFAULT_SSL_PROTOCOL.set("TLSv1.1");
                }
                else if (protocolMap.contains("TLSv1")) {
                    SSLUtil.DEFAULT_SSL_PROTOCOL.set("TLSv1");
                }
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        final LinkedHashSet<String> enabledProtocols = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(10));
        if (SSLUtil.DEFAULT_SSL_PROTOCOL.get().equals("TLSv1.3")) {
            enabledProtocols.add("TLSv1.3");
            enabledProtocols.add("TLSv1.2");
            enabledProtocols.add("TLSv1.1");
        }
        else if (SSLUtil.DEFAULT_SSL_PROTOCOL.get().equals("TLSv1.2")) {
            enabledProtocols.add("TLSv1.2");
            enabledProtocols.add("TLSv1.1");
        }
        else if (SSLUtil.DEFAULT_SSL_PROTOCOL.get().equals("TLSv1.1")) {
            enabledProtocols.add("TLSv1.1");
        }
        enabledProtocols.add("TLSv1");
        String enabledPropValue = StaticUtils.getSystemProperty("com.unboundid.util.SSLUtil.enabledSSLProtocols");
        if (enabledPropValue != null && !enabledPropValue.isEmpty()) {
            enabledProtocols.clear();
            final StringTokenizer tokenizer = new StringTokenizer(enabledPropValue, ", ", false);
            while (tokenizer.hasMoreTokens()) {
                final String token = tokenizer.nextToken();
                if (!token.isEmpty()) {
                    enabledProtocols.add(token);
                }
            }
        }
        SSLUtil.ENABLED_SSL_PROTOCOLS.set(Collections.unmodifiableSet((Set<? extends String>)enabledProtocols));
        SSLUtil.ENABLED_SSL_CIPHER_SUITES.set(TLSCipherSuiteSelector.getRecommendedCipherSuites());
        enabledPropValue = StaticUtils.getSystemProperty("com.unboundid.util.SSLUtil.enabledSSLCipherSuites");
        if (enabledPropValue != null && !enabledPropValue.isEmpty()) {
            final LinkedHashSet<String> enabledCipherSuites = new LinkedHashSet<String>(StaticUtils.computeMapCapacity(50));
            final StringTokenizer tokenizer2 = new StringTokenizer(enabledPropValue, ", ", false);
            while (tokenizer2.hasMoreTokens()) {
                final String token2 = tokenizer2.nextToken();
                if (!token2.isEmpty()) {
                    enabledCipherSuites.add(token2);
                }
            }
            if (!enabledCipherSuites.isEmpty()) {
                SSLUtil.ENABLED_SSL_CIPHER_SUITES.set(Collections.unmodifiableSet((Set<? extends String>)enabledCipherSuites));
            }
        }
    }
    
    public static String certificateToString(final X509Certificate certificate) {
        final StringBuilder buffer = new StringBuilder();
        certificateToString(certificate, buffer);
        return buffer.toString();
    }
    
    public static void certificateToString(final X509Certificate certificate, final StringBuilder buffer) {
        buffer.append("Certificate(subject='");
        buffer.append(certificate.getSubjectX500Principal().getName("RFC2253"));
        buffer.append("', serialNumber=");
        buffer.append(certificate.getSerialNumber());
        buffer.append(", notBefore=");
        StaticUtils.encodeGeneralizedTime(certificate.getNotBefore());
        buffer.append(", notAfter=");
        StaticUtils.encodeGeneralizedTime(certificate.getNotAfter());
        buffer.append(", signatureAlgorithm='");
        buffer.append(certificate.getSigAlgName());
        buffer.append("', signatureBytes='");
        StaticUtils.toHex(certificate.getSignature(), buffer);
        buffer.append("', issuerSubject='");
        buffer.append(certificate.getIssuerX500Principal().getName("RFC2253"));
        buffer.append("')");
    }
    
    static {
        DEFAULT_SSL_PROTOCOL = new AtomicReference<String>("TLSv1");
        ENABLED_SSL_CIPHER_SUITES = new AtomicReference<Set<String>>();
        ENABLED_SSL_PROTOCOLS = new AtomicReference<Set<String>>();
        configureSSLDefaults();
    }
}
