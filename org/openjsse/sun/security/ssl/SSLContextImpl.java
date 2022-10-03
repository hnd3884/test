package org.openjsse.sun.security.ssl;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.AccessController;
import java.util.Map;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;
import sun.security.action.GetPropertyAction;
import java.util.Iterator;
import java.util.ArrayList;
import java.security.AlgorithmParameters;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.util.TreeSet;
import java.util.List;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import org.openjsse.javax.net.ssl.SSLEngine;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.security.KeyManagementException;
import java.security.KeyStore;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import java.util.Collection;
import java.security.SecureRandom;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.SSLContextSpi;

public abstract class SSLContextImpl extends SSLContextSpi
{
    private final EphemeralKeyManager ephemeralKeyManager;
    private final SSLSessionContextImpl clientCache;
    private final SSLSessionContextImpl serverCache;
    private boolean isInitialized;
    private X509ExtendedKeyManager keyManager;
    private X509TrustManager trustManager;
    private SecureRandom secureRandom;
    private volatile HelloCookieManager.Builder helloCookieManagerBuilder;
    private final boolean clientEnableStapling;
    private final boolean serverEnableStapling;
    private static final Collection<CipherSuite> clientCustomizedCipherSuites;
    private static final Collection<CipherSuite> serverCustomizedCipherSuites;
    private volatile StatusResponseManager statusResponseManager;
    
    SSLContextImpl() {
        this.clientEnableStapling = Utilities.getBooleanProperty("jdk.tls.client.enableStatusRequestExtension", true);
        this.serverEnableStapling = Utilities.getBooleanProperty("jdk.tls.server.enableStatusRequestExtension", false);
        this.ephemeralKeyManager = new EphemeralKeyManager();
        this.clientCache = new SSLSessionContextImpl();
        this.serverCache = new SSLSessionContextImpl();
    }
    
    @Override
    protected void engineInit(final KeyManager[] km, TrustManager[] tm, final SecureRandom sr) throws KeyManagementException {
        this.isInitialized = false;
        this.keyManager = this.chooseKeyManager(km);
        if (tm == null) {
            try {
                final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init((KeyStore)null);
                tm = tmf.getTrustManagers();
            }
            catch (final Exception ex) {}
        }
        this.trustManager = this.chooseTrustManager(tm);
        if (sr == null) {
            this.secureRandom = JsseJce.getSecureRandom();
        }
        else {
            if (OpenJSSE.isFIPS() && sr.getProvider() != OpenJSSE.cryptoProvider) {
                throw new KeyManagementException("FIPS mode: SecureRandom must be from provider " + OpenJSSE.cryptoProvider.getName());
            }
            this.secureRandom = sr;
        }
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
            SSLLogger.finest("trigger seeding of SecureRandom", new Object[0]);
        }
        this.secureRandom.nextInt();
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
            SSLLogger.finest("done seeding of SecureRandom", new Object[0]);
        }
        this.isInitialized = true;
    }
    
    private X509TrustManager chooseTrustManager(final TrustManager[] tm) throws KeyManagementException {
        int i = 0;
        while (tm != null && i < tm.length) {
            if (tm[i] instanceof X509TrustManager) {
                if (OpenJSSE.isFIPS() && !(tm[i] instanceof X509TrustManagerImpl)) {
                    throw new KeyManagementException("FIPS mode: only OpenJSSE TrustManagers may be used");
                }
                if (tm[i] instanceof X509ExtendedTrustManager) {
                    return (X509TrustManager)tm[i];
                }
                return new AbstractTrustManagerWrapper((X509TrustManager)tm[i]);
            }
            else {
                ++i;
            }
        }
        return DummyX509TrustManager.INSTANCE;
    }
    
    private X509ExtendedKeyManager chooseKeyManager(final KeyManager[] kms) throws KeyManagementException {
        int i = 0;
        while (kms != null && i < kms.length) {
            final KeyManager km = kms[i];
            if (!(km instanceof X509KeyManager)) {
                ++i;
            }
            else if (OpenJSSE.isFIPS()) {
                if (km instanceof X509KeyManagerImpl || km instanceof SunX509KeyManagerImpl) {
                    return (X509ExtendedKeyManager)km;
                }
                throw new KeyManagementException("FIPS mode: only OpenJSSE KeyManagers may be used");
            }
            else {
                if (km instanceof X509ExtendedKeyManager) {
                    return (X509ExtendedKeyManager)km;
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
                    SSLLogger.warning("X509KeyManager passed to SSLContext.init():  need an X509ExtendedKeyManager for SSLEngine use", new Object[0]);
                }
                return new AbstractKeyManagerWrapper((X509KeyManager)km);
            }
        }
        return DummyX509KeyManager.INSTANCE;
    }
    
    abstract SSLEngine createSSLEngineImpl();
    
    abstract SSLEngine createSSLEngineImpl(final String p0, final int p1);
    
    @Override
    protected SSLEngine engineCreateSSLEngine() {
        if (!this.isInitialized) {
            throw new IllegalStateException("SSLContext is not initialized");
        }
        return this.createSSLEngineImpl();
    }
    
    @Override
    protected SSLEngine engineCreateSSLEngine(final String host, final int port) {
        if (!this.isInitialized) {
            throw new IllegalStateException("SSLContext is not initialized");
        }
        return this.createSSLEngineImpl(host, port);
    }
    
    @Override
    protected SSLSocketFactory engineGetSocketFactory() {
        if (!this.isInitialized) {
            throw new IllegalStateException("SSLContext is not initialized");
        }
        return new SSLSocketFactoryImpl(this);
    }
    
    @Override
    protected SSLServerSocketFactory engineGetServerSocketFactory() {
        if (!this.isInitialized) {
            throw new IllegalStateException("SSLContext is not initialized");
        }
        return new SSLServerSocketFactoryImpl(this);
    }
    
    @Override
    protected SSLSessionContext engineGetClientSessionContext() {
        return this.clientCache;
    }
    
    @Override
    protected SSLSessionContext engineGetServerSessionContext() {
        return this.serverCache;
    }
    
    SecureRandom getSecureRandom() {
        return this.secureRandom;
    }
    
    X509ExtendedKeyManager getX509KeyManager() {
        return this.keyManager;
    }
    
    X509TrustManager getX509TrustManager() {
        return this.trustManager;
    }
    
    EphemeralKeyManager getEphemeralKeyManager() {
        return this.ephemeralKeyManager;
    }
    
    HelloCookieManager getHelloCookieManager(final ProtocolVersion protocolVersion) {
        if (this.helloCookieManagerBuilder == null) {
            synchronized (this) {
                if (this.helloCookieManagerBuilder == null) {
                    this.helloCookieManagerBuilder = new HelloCookieManager.Builder(this.secureRandom);
                }
            }
        }
        return this.helloCookieManagerBuilder.valueOf(protocolVersion);
    }
    
    StatusResponseManager getStatusResponseManager() {
        if (this.serverEnableStapling && this.statusResponseManager == null) {
            synchronized (this) {
                if (this.statusResponseManager == null) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
                        SSLLogger.finest("Initializing StatusResponseManager", new Object[0]);
                    }
                    this.statusResponseManager = new StatusResponseManager();
                }
            }
        }
        return this.statusResponseManager;
    }
    
    abstract List<ProtocolVersion> getSupportedProtocolVersions();
    
    abstract List<ProtocolVersion> getServerDefaultProtocolVersions();
    
    abstract List<ProtocolVersion> getClientDefaultProtocolVersions();
    
    abstract List<CipherSuite> getSupportedCipherSuites();
    
    abstract List<CipherSuite> getServerDefaultCipherSuites();
    
    abstract List<CipherSuite> getClientDefaultCipherSuites();
    
    abstract boolean isDTLS();
    
    List<ProtocolVersion> getDefaultProtocolVersions(final boolean roleIsServer) {
        return roleIsServer ? this.getServerDefaultProtocolVersions() : this.getClientDefaultProtocolVersions();
    }
    
    List<CipherSuite> getDefaultCipherSuites(final boolean roleIsServer) {
        return roleIsServer ? this.getServerDefaultCipherSuites() : this.getClientDefaultCipherSuites();
    }
    
    boolean isDefaultProtocolVesions(final List<ProtocolVersion> protocols) {
        return protocols == this.getServerDefaultProtocolVersions() || protocols == this.getClientDefaultProtocolVersions();
    }
    
    boolean isDefaultCipherSuiteList(final List<CipherSuite> cipherSuites) {
        return cipherSuites == this.getServerDefaultCipherSuites() || cipherSuites == this.getClientDefaultCipherSuites();
    }
    
    boolean isStaplingEnabled(final boolean isClient) {
        return isClient ? this.clientEnableStapling : this.serverEnableStapling;
    }
    
    private static List<CipherSuite> getApplicableSupportedCipherSuites(final List<ProtocolVersion> protocols) {
        return getApplicableCipherSuites(CipherSuite.allowedCipherSuites(), protocols);
    }
    
    private static List<CipherSuite> getApplicableEnabledCipherSuites(final List<ProtocolVersion> protocols, final boolean isClient) {
        if (isClient) {
            if (!SSLContextImpl.clientCustomizedCipherSuites.isEmpty()) {
                return getApplicableCipherSuites(SSLContextImpl.clientCustomizedCipherSuites, protocols);
            }
        }
        else if (!SSLContextImpl.serverCustomizedCipherSuites.isEmpty()) {
            return getApplicableCipherSuites(SSLContextImpl.serverCustomizedCipherSuites, protocols);
        }
        return getApplicableCipherSuites(CipherSuite.defaultCipherSuites(), protocols);
    }
    
    private static List<CipherSuite> getApplicableCipherSuites(final Collection<CipherSuite> allowedCipherSuites, final List<ProtocolVersion> protocols) {
        final TreeSet<CipherSuite> suites = new TreeSet<CipherSuite>();
        if (protocols != null && !protocols.isEmpty()) {
            for (final CipherSuite suite : allowedCipherSuites) {
                if (!suite.isAvailable()) {
                    continue;
                }
                boolean isSupported = false;
                for (final ProtocolVersion protocol : protocols) {
                    if (suite.supports(protocol)) {
                        if (!suite.bulkCipher.isAvailable()) {
                            continue;
                        }
                        if (SSLAlgorithmConstraints.DEFAULT.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), suite.name, null)) {
                            suites.add(suite);
                            isSupported = true;
                            break;
                        }
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx,verbose")) {
                            SSLLogger.fine("Ignore disabled cipher suite: " + suite.name, new Object[0]);
                            break;
                        }
                        break;
                    }
                }
                if (isSupported || !SSLLogger.isOn || !SSLLogger.isOn("ssl,sslctx,verbose")) {
                    continue;
                }
                SSLLogger.finest("Ignore unsupported cipher suite: " + suite, new Object[0]);
            }
        }
        return new ArrayList<CipherSuite>(suites);
    }
    
    private static Collection<CipherSuite> getCustomizedCipherSuites(final String propertyName) {
        String property = GetPropertyAction.privilegedGetProperty(propertyName);
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
            SSLLogger.fine("System property " + propertyName + " is set to '" + property + "'", new Object[0]);
        }
        if (property != null && property.length() != 0 && property.length() > 1 && property.charAt(0) == '\"' && property.charAt(property.length() - 1) == '\"') {
            property = property.substring(1, property.length() - 1);
        }
        if (property != null && property.length() != 0) {
            final String[] cipherSuiteNames = property.split(",");
            final Collection<CipherSuite> cipherSuites = new ArrayList<CipherSuite>(cipherSuiteNames.length);
            for (int i = 0; i < cipherSuiteNames.length; ++i) {
                cipherSuiteNames[i] = cipherSuiteNames[i].trim();
                if (!cipherSuiteNames[i].isEmpty()) {
                    CipherSuite suite;
                    try {
                        suite = CipherSuite.nameOf(cipherSuiteNames[i]);
                    }
                    catch (final IllegalArgumentException iae) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
                            SSLLogger.fine("Unknown or unsupported cipher suite name: " + cipherSuiteNames[i], new Object[0]);
                        }
                        continue;
                    }
                    if (suite != null && suite.isAvailable()) {
                        cipherSuites.add(suite);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
                        SSLLogger.fine("The current installed providers do not support cipher suite: " + cipherSuiteNames[i], new Object[0]);
                    }
                }
            }
            return cipherSuites;
        }
        return (Collection<CipherSuite>)Collections.emptyList();
    }
    
    private static List<ProtocolVersion> getAvailableProtocols(final ProtocolVersion[] protocolCandidates) {
        List<ProtocolVersion> availableProtocols = Collections.emptyList();
        if (protocolCandidates != null && protocolCandidates.length != 0) {
            availableProtocols = new ArrayList<ProtocolVersion>(protocolCandidates.length);
            for (final ProtocolVersion p : protocolCandidates) {
                if (p.isAvailable) {
                    availableProtocols.add(p);
                }
            }
        }
        return availableProtocols;
    }
    
    static {
        clientCustomizedCipherSuites = getCustomizedCipherSuites("jdk.tls.client.cipherSuites");
        serverCustomizedCipherSuites = getCustomizedCipherSuites("jdk.tls.server.cipherSuites");
    }
    
    private abstract static class AbstractTLSContext extends SSLContextImpl
    {
        private static final List<ProtocolVersion> supportedProtocols;
        private static final List<ProtocolVersion> serverDefaultProtocols;
        private static final List<CipherSuite> supportedCipherSuites;
        private static final List<CipherSuite> serverDefaultCipherSuites;
        
        @Override
        List<ProtocolVersion> getSupportedProtocolVersions() {
            return AbstractTLSContext.supportedProtocols;
        }
        
        @Override
        List<CipherSuite> getSupportedCipherSuites() {
            return AbstractTLSContext.supportedCipherSuites;
        }
        
        @Override
        List<ProtocolVersion> getServerDefaultProtocolVersions() {
            return AbstractTLSContext.serverDefaultProtocols;
        }
        
        @Override
        List<CipherSuite> getServerDefaultCipherSuites() {
            return AbstractTLSContext.serverDefaultCipherSuites;
        }
        
        @Override
        SSLEngine createSSLEngineImpl() {
            return new SSLEngineImpl(this);
        }
        
        @Override
        SSLEngine createSSLEngineImpl(final String host, final int port) {
            return new SSLEngineImpl(this, host, port);
        }
        
        @Override
        boolean isDTLS() {
            return false;
        }
        
        static ProtocolVersion[] getSupportedProtocols() {
            if (OpenJSSE.isFIPS()) {
                return new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10 };
            }
            return new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30, ProtocolVersion.SSL20Hello };
        }
        
        static {
            if (OpenJSSE.isFIPS()) {
                supportedProtocols = Arrays.asList(ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10);
                serverDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10 });
            }
            else {
                supportedProtocols = Arrays.asList(ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30, ProtocolVersion.SSL20Hello);
                serverDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30, ProtocolVersion.SSL20Hello });
            }
            supportedCipherSuites = getApplicableSupportedCipherSuites(AbstractTLSContext.supportedProtocols);
            serverDefaultCipherSuites = getApplicableEnabledCipherSuites(AbstractTLSContext.serverDefaultProtocols, false);
        }
    }
    
    public static final class TLS10Context extends AbstractTLSContext
    {
        private static final List<ProtocolVersion> clientDefaultProtocols;
        private static final List<CipherSuite> clientDefaultCipherSuites;
        
        @Override
        List<ProtocolVersion> getClientDefaultProtocolVersions() {
            return TLS10Context.clientDefaultProtocols;
        }
        
        @Override
        List<CipherSuite> getClientDefaultCipherSuites() {
            return TLS10Context.clientDefaultCipherSuites;
        }
        
        static {
            if (OpenJSSE.isFIPS()) {
                clientDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS10 });
            }
            else {
                clientDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.SSL30 });
            }
            clientDefaultCipherSuites = getApplicableEnabledCipherSuites(TLS10Context.clientDefaultProtocols, true);
        }
    }
    
    public static final class TLS11Context extends AbstractTLSContext
    {
        private static final List<ProtocolVersion> clientDefaultProtocols;
        private static final List<CipherSuite> clientDefaultCipherSuites;
        
        @Override
        List<ProtocolVersion> getClientDefaultProtocolVersions() {
            return TLS11Context.clientDefaultProtocols;
        }
        
        @Override
        List<CipherSuite> getClientDefaultCipherSuites() {
            return TLS11Context.clientDefaultCipherSuites;
        }
        
        static {
            if (OpenJSSE.isFIPS()) {
                clientDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS11, ProtocolVersion.TLS10 });
            }
            else {
                clientDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 });
            }
            clientDefaultCipherSuites = getApplicableEnabledCipherSuites(TLS11Context.clientDefaultProtocols, true);
        }
    }
    
    public static final class TLS12Context extends AbstractTLSContext
    {
        private static final List<ProtocolVersion> clientDefaultProtocols;
        private static final List<CipherSuite> clientDefaultCipherSuites;
        
        @Override
        List<ProtocolVersion> getClientDefaultProtocolVersions() {
            return TLS12Context.clientDefaultProtocols;
        }
        
        @Override
        List<CipherSuite> getClientDefaultCipherSuites() {
            return TLS12Context.clientDefaultCipherSuites;
        }
        
        static {
            if (OpenJSSE.isFIPS()) {
                clientDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10 });
            }
            else {
                clientDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 });
            }
            clientDefaultCipherSuites = getApplicableEnabledCipherSuites(TLS12Context.clientDefaultProtocols, true);
        }
    }
    
    public static final class TLS13Context extends AbstractTLSContext
    {
        private static final List<ProtocolVersion> clientDefaultProtocols;
        private static final List<CipherSuite> clientDefaultCipherSuites;
        
        @Override
        List<ProtocolVersion> getClientDefaultProtocolVersions() {
            return TLS13Context.clientDefaultProtocols;
        }
        
        @Override
        List<CipherSuite> getClientDefaultCipherSuites() {
            return TLS13Context.clientDefaultCipherSuites;
        }
        
        static {
            if (OpenJSSE.isFIPS()) {
                clientDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10 });
            }
            else {
                clientDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 });
            }
            clientDefaultCipherSuites = getApplicableEnabledCipherSuites(TLS13Context.clientDefaultProtocols, true);
        }
    }
    
    private static class CustomizedSSLProtocols
    {
        private static final String JDK_TLS_CLIENT_PROTOCOLS = "jdk.tls.client.protocols";
        private static final String JDK_TLS_SERVER_PROTOCOLS = "jdk.tls.server.protocols";
        static IllegalArgumentException reservedException;
        static final ArrayList<ProtocolVersion> customizedClientProtocols;
        static final ArrayList<ProtocolVersion> customizedServerProtocols;
        
        private static void populate(final String propname, final ArrayList<ProtocolVersion> arrayList) {
            String property = GetPropertyAction.privilegedGetProperty(propname);
            if (property == null) {
                return;
            }
            if (property.length() != 0 && property.length() > 1 && property.charAt(0) == '\"' && property.charAt(property.length() - 1) == '\"') {
                property = property.substring(1, property.length() - 1);
            }
            if (property.length() != 0) {
                final String[] protocols = property.split(",");
                for (int i = 0; i < protocols.length; ++i) {
                    protocols[i] = protocols[i].trim();
                    final ProtocolVersion pv = ProtocolVersion.nameOf(protocols[i]);
                    if (pv == null) {
                        CustomizedSSLProtocols.reservedException = new IllegalArgumentException(propname + ": " + protocols[i] + " is not a supported SSL protocol name");
                    }
                    if (OpenJSSE.isFIPS() && (pv == ProtocolVersion.SSL30 || pv == ProtocolVersion.SSL20Hello)) {
                        CustomizedSSLProtocols.reservedException = new IllegalArgumentException(propname + ": " + pv + " is not FIPS compliant");
                        break;
                    }
                    if (!arrayList.contains(pv)) {
                        arrayList.add(pv);
                    }
                }
            }
        }
        
        static {
            CustomizedSSLProtocols.reservedException = null;
            customizedClientProtocols = new ArrayList<ProtocolVersion>();
            customizedServerProtocols = new ArrayList<ProtocolVersion>();
            populate("jdk.tls.client.protocols", CustomizedSSLProtocols.customizedClientProtocols);
            populate("jdk.tls.server.protocols", CustomizedSSLProtocols.customizedServerProtocols);
        }
    }
    
    private static class CustomizedTLSContext extends AbstractTLSContext
    {
        private static final List<ProtocolVersion> clientDefaultProtocols;
        private static final List<ProtocolVersion> serverDefaultProtocols;
        private static final List<CipherSuite> clientDefaultCipherSuites;
        private static final List<CipherSuite> serverDefaultCipherSuites;
        private static final IllegalArgumentException reservedException;
        
        private static List<ProtocolVersion> customizedProtocols(final boolean client, final List<ProtocolVersion> customized) {
            final List<ProtocolVersion> refactored = new ArrayList<ProtocolVersion>();
            for (final ProtocolVersion pv : customized) {
                if (!pv.isDTLS) {
                    refactored.add(pv);
                }
            }
            ProtocolVersion[] candidates;
            if (refactored.isEmpty()) {
                if (client) {
                    candidates = getProtocols();
                }
                else {
                    candidates = AbstractTLSContext.getSupportedProtocols();
                }
            }
            else {
                candidates = refactored.toArray(new ProtocolVersion[refactored.size()]);
            }
            return getAvailableProtocols(candidates);
        }
        
        static ProtocolVersion[] getProtocols() {
            if (OpenJSSE.isFIPS()) {
                return new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10 };
            }
            return new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        }
        
        protected CustomizedTLSContext() {
            if (CustomizedTLSContext.reservedException != null) {
                throw CustomizedTLSContext.reservedException;
            }
        }
        
        @Override
        List<ProtocolVersion> getClientDefaultProtocolVersions() {
            return CustomizedTLSContext.clientDefaultProtocols;
        }
        
        @Override
        List<ProtocolVersion> getServerDefaultProtocolVersions() {
            return CustomizedTLSContext.serverDefaultProtocols;
        }
        
        @Override
        List<CipherSuite> getClientDefaultCipherSuites() {
            return CustomizedTLSContext.clientDefaultCipherSuites;
        }
        
        @Override
        List<CipherSuite> getServerDefaultCipherSuites() {
            return CustomizedTLSContext.serverDefaultCipherSuites;
        }
        
        static {
            reservedException = CustomizedSSLProtocols.reservedException;
            if (CustomizedTLSContext.reservedException == null) {
                clientDefaultProtocols = customizedProtocols(true, CustomizedSSLProtocols.customizedClientProtocols);
                serverDefaultProtocols = customizedProtocols(false, CustomizedSSLProtocols.customizedServerProtocols);
                clientDefaultCipherSuites = getApplicableEnabledCipherSuites(CustomizedTLSContext.clientDefaultProtocols, true);
                serverDefaultCipherSuites = getApplicableEnabledCipherSuites(CustomizedTLSContext.serverDefaultProtocols, false);
            }
            else {
                clientDefaultProtocols = null;
                serverDefaultProtocols = null;
                clientDefaultCipherSuites = null;
                serverDefaultCipherSuites = null;
            }
        }
    }
    
    public static final class TLSContext extends CustomizedTLSContext
    {
    }
    
    private static final class DefaultManagersHolder
    {
        private static final String NONE = "NONE";
        private static final String P11KEYSTORE = "PKCS11";
        private static final TrustManager[] trustManagers;
        private static final KeyManager[] keyManagers;
        private static final Exception reservedException;
        
        private static TrustManager[] getTrustManagers() throws Exception {
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            if ("OpenJSSE".equals(tmf.getProvider().getName())) {
                tmf.init((KeyStore)null);
            }
            else {
                final KeyStore ks = TrustStoreManager.getTrustedKeyStore();
                tmf.init(ks);
            }
            return tmf.getTrustManagers();
        }
        
        private static KeyManager[] getKeyManagers() throws Exception {
            final Map<String, String> props = new HashMap<String, String>();
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    props.put("keyStore", System.getProperty("javax.net.ssl.keyStore", ""));
                    props.put("keyStoreType", System.getProperty("javax.net.ssl.keyStoreType", KeyStore.getDefaultType()));
                    props.put("keyStoreProvider", System.getProperty("javax.net.ssl.keyStoreProvider", ""));
                    props.put("keyStorePasswd", System.getProperty("javax.net.ssl.keyStorePassword", ""));
                    return null;
                }
            });
            final String defaultKeyStore = props.get("keyStore");
            final String defaultKeyStoreType = props.get("keyStoreType");
            final String defaultKeyStoreProvider = props.get("keyStoreProvider");
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,defaultctx")) {
                SSLLogger.fine("keyStore is : " + defaultKeyStore, new Object[0]);
                SSLLogger.fine("keyStore type is : " + defaultKeyStoreType, new Object[0]);
                SSLLogger.fine("keyStore provider is : " + defaultKeyStoreProvider, new Object[0]);
            }
            if ("PKCS11".equals(defaultKeyStoreType) && !"NONE".equals(defaultKeyStore)) {
                throw new IllegalArgumentException("if keyStoreType is PKCS11, then keyStore must be NONE");
            }
            FileInputStream fs = null;
            KeyStore ks = null;
            char[] passwd = null;
            try {
                if (defaultKeyStore.length() != 0 && !"NONE".equals(defaultKeyStore)) {
                    fs = AccessController.doPrivileged((PrivilegedExceptionAction<FileInputStream>)new PrivilegedExceptionAction<FileInputStream>() {
                        @Override
                        public FileInputStream run() throws Exception {
                            return new FileInputStream(defaultKeyStore);
                        }
                    });
                }
                final String defaultKeyStorePassword = props.get("keyStorePasswd");
                if (defaultKeyStorePassword.length() != 0) {
                    passwd = defaultKeyStorePassword.toCharArray();
                }
                if (defaultKeyStoreType.length() != 0) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,defaultctx")) {
                        SSLLogger.finest("init keystore", new Object[0]);
                    }
                    if (defaultKeyStoreProvider.length() == 0) {
                        ks = KeyStore.getInstance(defaultKeyStoreType);
                    }
                    else {
                        ks = KeyStore.getInstance(defaultKeyStoreType, defaultKeyStoreProvider);
                    }
                    ks.load(fs, passwd);
                }
            }
            finally {
                if (fs != null) {
                    fs.close();
                    fs = null;
                }
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,defaultctx")) {
                SSLLogger.fine("init keymanager of type " + KeyManagerFactory.getDefaultAlgorithm(), new Object[0]);
            }
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            if ("PKCS11".equals(defaultKeyStoreType)) {
                kmf.init(ks, null);
            }
            else {
                kmf.init(ks, passwd);
            }
            return kmf.getKeyManagers();
        }
        
        static {
            Exception reserved = null;
            TrustManager[] tmMediator;
            try {
                tmMediator = getTrustManagers();
            }
            catch (final Exception e) {
                reserved = e;
                tmMediator = new TrustManager[0];
            }
            trustManagers = tmMediator;
            if (reserved == null) {
                KeyManager[] kmMediator;
                try {
                    kmMediator = getKeyManagers();
                }
                catch (final Exception e2) {
                    reserved = e2;
                    kmMediator = new KeyManager[0];
                }
                keyManagers = kmMediator;
            }
            else {
                keyManagers = new KeyManager[0];
            }
            reservedException = reserved;
        }
    }
    
    private static final class DefaultSSLContextHolder
    {
        private static final SSLContextImpl sslContext;
        static Exception reservedException;
        
        static {
            DefaultSSLContextHolder.reservedException = null;
            SSLContextImpl mediator = null;
            if (DefaultManagersHolder.reservedException != null) {
                DefaultSSLContextHolder.reservedException = DefaultManagersHolder.reservedException;
            }
            else {
                try {
                    mediator = new DefaultSSLContext();
                }
                catch (final Exception e) {
                    DefaultSSLContextHolder.reservedException = e;
                }
            }
            sslContext = mediator;
        }
    }
    
    public static final class DefaultSSLContext extends CustomizedTLSContext
    {
        public DefaultSSLContext() throws Exception {
            if (DefaultManagersHolder.reservedException != null) {
                throw DefaultManagersHolder.reservedException;
            }
            try {
                super.engineInit(DefaultManagersHolder.keyManagers, DefaultManagersHolder.trustManagers, null);
            }
            catch (final Exception e) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,defaultctx")) {
                    SSLLogger.fine("default context init failed: ", e);
                }
                throw e;
            }
        }
        
        @Override
        protected void engineInit(final KeyManager[] km, final TrustManager[] tm, final SecureRandom sr) throws KeyManagementException {
            throw new KeyManagementException("Default SSLContext is initialized automatically");
        }
        
        static SSLContextImpl getDefaultImpl() throws Exception {
            if (DefaultSSLContextHolder.reservedException != null) {
                throw DefaultSSLContextHolder.reservedException;
            }
            return DefaultSSLContextHolder.sslContext;
        }
    }
    
    private abstract static class AbstractDTLSContext extends SSLContextImpl
    {
        private static final List<ProtocolVersion> supportedProtocols;
        private static final List<ProtocolVersion> serverDefaultProtocols;
        private static final List<CipherSuite> supportedCipherSuites;
        private static final List<CipherSuite> serverDefaultCipherSuites;
        
        @Override
        List<ProtocolVersion> getSupportedProtocolVersions() {
            return AbstractDTLSContext.supportedProtocols;
        }
        
        @Override
        List<CipherSuite> getSupportedCipherSuites() {
            return AbstractDTLSContext.supportedCipherSuites;
        }
        
        @Override
        List<ProtocolVersion> getServerDefaultProtocolVersions() {
            return AbstractDTLSContext.serverDefaultProtocols;
        }
        
        @Override
        List<CipherSuite> getServerDefaultCipherSuites() {
            return AbstractDTLSContext.serverDefaultCipherSuites;
        }
        
        @Override
        SSLEngine createSSLEngineImpl() {
            return new SSLEngineImpl(this);
        }
        
        @Override
        SSLEngine createSSLEngineImpl(final String host, final int port) {
            return new SSLEngineImpl(this, host, port);
        }
        
        @Override
        boolean isDTLS() {
            return true;
        }
        
        static {
            supportedProtocols = Arrays.asList(ProtocolVersion.DTLS12, ProtocolVersion.DTLS10);
            serverDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.DTLS12, ProtocolVersion.DTLS10 });
            supportedCipherSuites = getApplicableSupportedCipherSuites(AbstractDTLSContext.supportedProtocols);
            serverDefaultCipherSuites = getApplicableEnabledCipherSuites(AbstractDTLSContext.serverDefaultProtocols, false);
        }
    }
    
    public static final class DTLS10Context extends AbstractDTLSContext
    {
        private static final List<ProtocolVersion> clientDefaultProtocols;
        private static final List<CipherSuite> clientDefaultCipherSuites;
        
        @Override
        List<ProtocolVersion> getClientDefaultProtocolVersions() {
            return DTLS10Context.clientDefaultProtocols;
        }
        
        @Override
        List<CipherSuite> getClientDefaultCipherSuites() {
            return DTLS10Context.clientDefaultCipherSuites;
        }
        
        static {
            clientDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.DTLS10 });
            clientDefaultCipherSuites = getApplicableEnabledCipherSuites(DTLS10Context.clientDefaultProtocols, true);
        }
    }
    
    public static final class DTLS12Context extends AbstractDTLSContext
    {
        private static final List<ProtocolVersion> clientDefaultProtocols;
        private static final List<CipherSuite> clientDefaultCipherSuites;
        
        @Override
        List<ProtocolVersion> getClientDefaultProtocolVersions() {
            return DTLS12Context.clientDefaultProtocols;
        }
        
        @Override
        List<CipherSuite> getClientDefaultCipherSuites() {
            return DTLS12Context.clientDefaultCipherSuites;
        }
        
        static {
            clientDefaultProtocols = getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.DTLS12, ProtocolVersion.DTLS10 });
            clientDefaultCipherSuites = getApplicableEnabledCipherSuites(DTLS12Context.clientDefaultProtocols, true);
        }
    }
    
    private static class CustomizedDTLSContext extends AbstractDTLSContext
    {
        private static final List<ProtocolVersion> clientDefaultProtocols;
        private static final List<ProtocolVersion> serverDefaultProtocols;
        private static final List<CipherSuite> clientDefaultCipherSuites;
        private static final List<CipherSuite> serverDefaultCipherSuites;
        private static IllegalArgumentException reservedException;
        
        private static List<ProtocolVersion> customizedProtocols(final boolean client, final List<ProtocolVersion> customized) {
            final List<ProtocolVersion> refactored = new ArrayList<ProtocolVersion>();
            for (final ProtocolVersion pv : customized) {
                if (pv.isDTLS) {
                    refactored.add(pv);
                }
            }
            ProtocolVersion[] candidates;
            if (refactored.isEmpty()) {
                candidates = new ProtocolVersion[] { ProtocolVersion.DTLS12, ProtocolVersion.DTLS10 };
                if (!client) {
                    return Arrays.asList(candidates);
                }
            }
            else {
                candidates = new ProtocolVersion[customized.size()];
                candidates = customized.toArray(candidates);
            }
            return getAvailableProtocols(candidates);
        }
        
        protected CustomizedDTLSContext() {
            if (CustomizedDTLSContext.reservedException != null) {
                throw CustomizedDTLSContext.reservedException;
            }
        }
        
        @Override
        List<ProtocolVersion> getClientDefaultProtocolVersions() {
            return CustomizedDTLSContext.clientDefaultProtocols;
        }
        
        @Override
        List<ProtocolVersion> getServerDefaultProtocolVersions() {
            return CustomizedDTLSContext.serverDefaultProtocols;
        }
        
        @Override
        List<CipherSuite> getClientDefaultCipherSuites() {
            return CustomizedDTLSContext.clientDefaultCipherSuites;
        }
        
        @Override
        List<CipherSuite> getServerDefaultCipherSuites() {
            return CustomizedDTLSContext.serverDefaultCipherSuites;
        }
        
        static {
            CustomizedDTLSContext.reservedException = null;
            CustomizedDTLSContext.reservedException = CustomizedSSLProtocols.reservedException;
            if (CustomizedDTLSContext.reservedException == null) {
                clientDefaultProtocols = customizedProtocols(true, CustomizedSSLProtocols.customizedClientProtocols);
                serverDefaultProtocols = customizedProtocols(false, CustomizedSSLProtocols.customizedServerProtocols);
                clientDefaultCipherSuites = getApplicableEnabledCipherSuites(CustomizedDTLSContext.clientDefaultProtocols, true);
                serverDefaultCipherSuites = getApplicableEnabledCipherSuites(CustomizedDTLSContext.serverDefaultProtocols, false);
            }
            else {
                clientDefaultProtocols = null;
                serverDefaultProtocols = null;
                clientDefaultCipherSuites = null;
                serverDefaultCipherSuites = null;
            }
        }
    }
    
    public static final class DTLSContext extends CustomizedDTLSContext
    {
    }
}
