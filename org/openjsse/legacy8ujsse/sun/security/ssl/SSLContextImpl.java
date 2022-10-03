package org.openjsse.legacy8ujsse.sun.security.ssl;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Map;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Iterator;
import java.security.AlgorithmParameters;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.util.TreeSet;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
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
    private static final Debug debug;
    private final EphemeralKeyManager ephemeralKeyManager;
    private final SSLSessionContextImpl clientCache;
    private final SSLSessionContextImpl serverCache;
    private boolean isInitialized;
    private X509ExtendedKeyManager keyManager;
    private X509TrustManager trustManager;
    private SecureRandom secureRandom;
    private static final Collection<CipherSuite> clientCustomizedCipherSuites;
    private static final Collection<CipherSuite> serverCustomizedCipherSuites;
    
    SSLContextImpl() {
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
            if (Legacy8uJSSE.isFIPS() && sr.getProvider() != Legacy8uJSSE.cryptoProvider) {
                throw new KeyManagementException("FIPS mode: SecureRandom must be from provider " + Legacy8uJSSE.cryptoProvider.getName());
            }
            this.secureRandom = sr;
        }
        if (SSLContextImpl.debug != null && Debug.isOn("sslctx")) {
            System.out.println("trigger seeding of SecureRandom");
        }
        this.secureRandom.nextInt();
        if (SSLContextImpl.debug != null && Debug.isOn("sslctx")) {
            System.out.println("done seeding SecureRandom");
        }
        this.isInitialized = true;
    }
    
    private X509TrustManager chooseTrustManager(final TrustManager[] tm) throws KeyManagementException {
        int i = 0;
        while (tm != null && i < tm.length) {
            if (tm[i] instanceof X509TrustManager) {
                if (Legacy8uJSSE.isFIPS() && !(tm[i] instanceof X509TrustManagerImpl)) {
                    throw new KeyManagementException("FIPS mode: only Legacy8uJSSE TrustManagers may be used");
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
            else if (Legacy8uJSSE.isFIPS()) {
                if (km instanceof X509KeyManagerImpl || km instanceof SunX509KeyManagerImpl) {
                    return (X509ExtendedKeyManager)km;
                }
                throw new KeyManagementException("FIPS mode: only Legacy8uJSSE KeyManagers may be used");
            }
            else {
                if (km instanceof X509ExtendedKeyManager) {
                    return (X509ExtendedKeyManager)km;
                }
                if (SSLContextImpl.debug != null && Debug.isOn("sslctx")) {
                    System.out.println("X509KeyManager passed to SSLContext.init():  need an X509ExtendedKeyManager for SSLEngine use");
                }
                return new AbstractKeyManagerWrapper((X509KeyManager)km);
            }
        }
        return DummyX509KeyManager.INSTANCE;
    }
    
    @Override
    protected SSLSocketFactory engineGetSocketFactory() {
        if (!this.isInitialized) {
            throw new IllegalStateException("SSLContextImpl is not initialized");
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
    
    abstract SSLEngine createSSLEngineImpl();
    
    abstract SSLEngine createSSLEngineImpl(final String p0, final int p1);
    
    @Override
    protected SSLEngine engineCreateSSLEngine() {
        if (!this.isInitialized) {
            throw new IllegalStateException("SSLContextImpl is not initialized");
        }
        return this.createSSLEngineImpl();
    }
    
    @Override
    protected SSLEngine engineCreateSSLEngine(final String host, final int port) {
        if (!this.isInitialized) {
            throw new IllegalStateException("SSLContextImpl is not initialized");
        }
        return this.createSSLEngineImpl(host, port);
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
    
    abstract ProtocolList getSuportedProtocolList();
    
    abstract ProtocolList getServerDefaultProtocolList();
    
    abstract ProtocolList getClientDefaultProtocolList();
    
    abstract CipherSuiteList getSupportedCipherSuiteList();
    
    abstract CipherSuiteList getServerDefaultCipherSuiteList();
    
    abstract CipherSuiteList getClientDefaultCipherSuiteList();
    
    ProtocolList getDefaultProtocolList(final boolean roleIsServer) {
        return roleIsServer ? this.getServerDefaultProtocolList() : this.getClientDefaultProtocolList();
    }
    
    CipherSuiteList getDefaultCipherSuiteList(final boolean roleIsServer) {
        return roleIsServer ? this.getServerDefaultCipherSuiteList() : this.getClientDefaultCipherSuiteList();
    }
    
    boolean isDefaultProtocolList(final ProtocolList protocols) {
        return protocols == this.getServerDefaultProtocolList() || protocols == this.getClientDefaultProtocolList();
    }
    
    boolean isDefaultCipherSuiteList(final CipherSuiteList cipherSuites) {
        return cipherSuites == this.getServerDefaultCipherSuiteList() || cipherSuites == this.getClientDefaultCipherSuiteList();
    }
    
    private static CipherSuiteList getApplicableSupportedCipherSuiteList(final ProtocolList protocols) {
        return getApplicableCipherSuiteList(CipherSuite.allowedCipherSuites(), protocols, 1);
    }
    
    private static CipherSuiteList getApplicableEnabledCipherSuiteList(final ProtocolList protocols, final boolean isClient) {
        if (isClient) {
            if (!SSLContextImpl.clientCustomizedCipherSuites.isEmpty()) {
                return getApplicableCipherSuiteList(SSLContextImpl.clientCustomizedCipherSuites, protocols, 1);
            }
        }
        else if (!SSLContextImpl.serverCustomizedCipherSuites.isEmpty()) {
            return getApplicableCipherSuiteList(SSLContextImpl.serverCustomizedCipherSuites, protocols, 1);
        }
        return getApplicableCipherSuiteList(CipherSuite.allowedCipherSuites(), protocols, 300);
    }
    
    private static CipherSuiteList getApplicableCipherSuiteList(final Collection<CipherSuite> allowedCipherSuites, final ProtocolList protocols, final int minPriority) {
        final TreeSet<CipherSuite> suites = new TreeSet<CipherSuite>();
        if (!protocols.collection().isEmpty() && protocols.min.v != ProtocolVersion.NONE.v) {
            for (final CipherSuite suite : allowedCipherSuites) {
                if (suite.allowed) {
                    if (suite.priority < minPriority) {
                        continue;
                    }
                    if (suite.isAvailable() && suite.obsoleted > protocols.min.v && suite.supported <= protocols.max.v) {
                        if (SSLAlgorithmConstraints.DEFAULT.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), suite.name, null)) {
                            suites.add(suite);
                        }
                        else {
                            if (SSLContextImpl.debug == null || !Debug.isOn("sslctx") || !Debug.isOn("verbose")) {
                                continue;
                            }
                            System.out.println("Ignoring disabled cipher suite: " + suite.name);
                        }
                    }
                    else {
                        if (SSLContextImpl.debug == null || !Debug.isOn("sslctx") || !Debug.isOn("verbose")) {
                            continue;
                        }
                        if (suite.obsoleted <= protocols.min.v) {
                            System.out.println("Ignoring obsoleted cipher suite: " + suite);
                        }
                        else if (suite.supported > protocols.max.v) {
                            System.out.println("Ignoring unsupported cipher suite: " + suite);
                        }
                        else {
                            System.out.println("Ignoring unavailable cipher suite: " + suite);
                        }
                    }
                }
            }
        }
        return new CipherSuiteList(suites);
    }
    
    private static Collection<CipherSuite> getCustomizedCipherSuites(final String propertyName) {
        String property = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(propertyName));
        if (SSLContextImpl.debug != null && Debug.isOn("sslctx")) {
            System.out.println("System property " + propertyName + " is set to '" + property + "'");
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
                        suite = CipherSuite.valueOf(cipherSuiteNames[i]);
                    }
                    catch (final IllegalArgumentException iae) {
                        if (SSLContextImpl.debug != null && Debug.isOn("sslctx")) {
                            System.out.println("Unknown or unsupported cipher suite name: " + cipherSuiteNames[i]);
                        }
                        continue;
                    }
                    if (suite.isAvailable()) {
                        cipherSuites.add(suite);
                    }
                    else if (SSLContextImpl.debug != null && Debug.isOn("sslctx")) {
                        System.out.println("The current installed providers do not support cipher suite: " + cipherSuiteNames[i]);
                    }
                }
            }
            return cipherSuites;
        }
        return (Collection<CipherSuite>)Collections.emptyList();
    }
    
    private static String[] getAvailableProtocols(final ProtocolVersion[] protocolCandidates) {
        List<String> availableProtocols = Collections.emptyList();
        if (protocolCandidates != null && protocolCandidates.length != 0) {
            availableProtocols = new ArrayList<String>(protocolCandidates.length);
            for (final ProtocolVersion p : protocolCandidates) {
                if (ProtocolVersion.availableProtocols.contains(p)) {
                    availableProtocols.add(p.name);
                }
            }
        }
        return availableProtocols.toArray(new String[0]);
    }
    
    static {
        debug = Debug.getInstance("ssl");
        clientCustomizedCipherSuites = getCustomizedCipherSuites("jdk.tls.client.cipherSuites");
        serverCustomizedCipherSuites = getCustomizedCipherSuites("jdk.tls.server.cipherSuites");
    }
    
    private abstract static class AbstractTLSContext extends SSLContextImpl
    {
        private static final ProtocolList supportedProtocolList;
        private static final ProtocolList serverDefaultProtocolList;
        private static final CipherSuiteList supportedCipherSuiteList;
        private static final CipherSuiteList serverDefaultCipherSuiteList;
        
        @Override
        ProtocolList getSuportedProtocolList() {
            return AbstractTLSContext.supportedProtocolList;
        }
        
        @Override
        CipherSuiteList getSupportedCipherSuiteList() {
            return AbstractTLSContext.supportedCipherSuiteList;
        }
        
        @Override
        ProtocolList getServerDefaultProtocolList() {
            return AbstractTLSContext.serverDefaultProtocolList;
        }
        
        @Override
        CipherSuiteList getServerDefaultCipherSuiteList() {
            return AbstractTLSContext.serverDefaultCipherSuiteList;
        }
        
        @Override
        SSLEngine createSSLEngineImpl() {
            return new SSLEngineImpl(this);
        }
        
        @Override
        SSLEngine createSSLEngineImpl(final String host, final int port) {
            return new SSLEngineImpl(this, host, port);
        }
        
        static {
            if (Legacy8uJSSE.isFIPS()) {
                supportedProtocolList = new ProtocolList(new String[] { ProtocolVersion.TLS10.name, ProtocolVersion.TLS11.name, ProtocolVersion.TLS12.name });
                serverDefaultProtocolList = new ProtocolList(getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 }));
            }
            else {
                supportedProtocolList = new ProtocolList(new String[] { ProtocolVersion.SSL20Hello.name, ProtocolVersion.SSL30.name, ProtocolVersion.TLS10.name, ProtocolVersion.TLS11.name, ProtocolVersion.TLS12.name });
                serverDefaultProtocolList = new ProtocolList(getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.SSL20Hello, ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 }));
            }
            supportedCipherSuiteList = getApplicableSupportedCipherSuiteList(AbstractTLSContext.supportedProtocolList);
            serverDefaultCipherSuiteList = getApplicableEnabledCipherSuiteList(AbstractTLSContext.serverDefaultProtocolList, false);
        }
    }
    
    public static final class TLS10Context extends AbstractTLSContext
    {
        private static final ProtocolList clientDefaultProtocolList;
        private static final CipherSuiteList clientDefaultCipherSuiteList;
        
        @Override
        ProtocolList getClientDefaultProtocolList() {
            return TLS10Context.clientDefaultProtocolList;
        }
        
        @Override
        CipherSuiteList getClientDefaultCipherSuiteList() {
            return TLS10Context.clientDefaultCipherSuiteList;
        }
        
        static {
            if (Legacy8uJSSE.isFIPS()) {
                clientDefaultProtocolList = new ProtocolList(getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS10 }));
            }
            else {
                clientDefaultProtocolList = new ProtocolList(getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.SSL30, ProtocolVersion.TLS10 }));
            }
            clientDefaultCipherSuiteList = getApplicableEnabledCipherSuiteList(TLS10Context.clientDefaultProtocolList, true);
        }
    }
    
    public static final class TLS11Context extends AbstractTLSContext
    {
        private static final ProtocolList clientDefaultProtocolList;
        private static final CipherSuiteList clientDefaultCipherSuiteList;
        
        @Override
        ProtocolList getClientDefaultProtocolList() {
            return TLS11Context.clientDefaultProtocolList;
        }
        
        @Override
        CipherSuiteList getClientDefaultCipherSuiteList() {
            return TLS11Context.clientDefaultCipherSuiteList;
        }
        
        static {
            if (Legacy8uJSSE.isFIPS()) {
                clientDefaultProtocolList = new ProtocolList(getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.TLS11 }));
            }
            else {
                clientDefaultProtocolList = new ProtocolList(getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11 }));
            }
            clientDefaultCipherSuiteList = getApplicableEnabledCipherSuiteList(TLS11Context.clientDefaultProtocolList, true);
        }
    }
    
    public static final class TLS12Context extends AbstractTLSContext
    {
        private static final ProtocolList clientDefaultProtocolList;
        private static final CipherSuiteList clientDefaultCipherSuiteList;
        
        @Override
        ProtocolList getClientDefaultProtocolList() {
            return TLS12Context.clientDefaultProtocolList;
        }
        
        @Override
        CipherSuiteList getClientDefaultCipherSuiteList() {
            return TLS12Context.clientDefaultCipherSuiteList;
        }
        
        static {
            if (Legacy8uJSSE.isFIPS()) {
                clientDefaultProtocolList = new ProtocolList(getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 }));
            }
            else {
                clientDefaultProtocolList = new ProtocolList(getAvailableProtocols(new ProtocolVersion[] { ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 }));
            }
            clientDefaultCipherSuiteList = getApplicableEnabledCipherSuiteList(TLS12Context.clientDefaultProtocolList, true);
        }
    }
    
    private static class CustomizedSSLProtocols
    {
        private static final String PROPERTY_NAME = "jdk.tls.client.protocols";
        static IllegalArgumentException reservedException;
        static ArrayList<ProtocolVersion> customizedProtocols;
        
        static {
            CustomizedSSLProtocols.reservedException = null;
            CustomizedSSLProtocols.customizedProtocols = new ArrayList<ProtocolVersion>();
            String property = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.tls.client.protocols"));
            if (property != null && property.length() != 0 && property.length() > 1 && property.charAt(0) == '\"' && property.charAt(property.length() - 1) == '\"') {
                property = property.substring(1, property.length() - 1);
            }
            if (property != null && property.length() != 0) {
                final String[] protocols = property.split(",");
                for (int i = 0; i < protocols.length; ++i) {
                    protocols[i] = protocols[i].trim();
                    try {
                        final ProtocolVersion pro = ProtocolVersion.valueOf(protocols[i]);
                        if (Legacy8uJSSE.isFIPS() && (pro.v == ProtocolVersion.SSL30.v || pro.v == ProtocolVersion.SSL20Hello.v)) {
                            CustomizedSSLProtocols.reservedException = new IllegalArgumentException("jdk.tls.client.protocols: " + pro + " is not FIPS compliant");
                            break;
                        }
                        if (!CustomizedSSLProtocols.customizedProtocols.contains(pro)) {
                            CustomizedSSLProtocols.customizedProtocols.add(pro);
                        }
                    }
                    catch (final IllegalArgumentException iae) {
                        CustomizedSSLProtocols.reservedException = new IllegalArgumentException("jdk.tls.client.protocols: " + protocols[i] + " is not a standard SSL protocol name", iae);
                    }
                }
            }
        }
    }
    
    private static class CustomizedTLSContext extends AbstractTLSContext
    {
        private static final ProtocolList clientDefaultProtocolList;
        private static final CipherSuiteList clientDefaultCipherSuiteList;
        private static IllegalArgumentException reservedException;
        
        protected CustomizedTLSContext() {
            if (CustomizedTLSContext.reservedException != null) {
                throw CustomizedTLSContext.reservedException;
            }
        }
        
        @Override
        ProtocolList getClientDefaultProtocolList() {
            return CustomizedTLSContext.clientDefaultProtocolList;
        }
        
        @Override
        CipherSuiteList getClientDefaultCipherSuiteList() {
            return CustomizedTLSContext.clientDefaultCipherSuiteList;
        }
        
        static {
            CustomizedTLSContext.reservedException = null;
            CustomizedTLSContext.reservedException = CustomizedSSLProtocols.reservedException;
            if (CustomizedTLSContext.reservedException == null) {
                final ArrayList<ProtocolVersion> customizedTLSProtocols = new ArrayList<ProtocolVersion>();
                for (final ProtocolVersion protocol : CustomizedSSLProtocols.customizedProtocols) {
                    customizedTLSProtocols.add(protocol);
                }
                ProtocolVersion[] candidates;
                if (customizedTLSProtocols.isEmpty()) {
                    if (Legacy8uJSSE.isFIPS()) {
                        candidates = new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 };
                    }
                    else {
                        candidates = new ProtocolVersion[] { ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 };
                    }
                }
                else {
                    candidates = new ProtocolVersion[customizedTLSProtocols.size()];
                    candidates = customizedTLSProtocols.toArray(candidates);
                }
                clientDefaultProtocolList = new ProtocolList(getAvailableProtocols(candidates));
                clientDefaultCipherSuiteList = getApplicableEnabledCipherSuiteList(CustomizedTLSContext.clientDefaultProtocolList, true);
            }
            else {
                clientDefaultProtocolList = null;
                clientDefaultCipherSuiteList = null;
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
        static Exception reservedException;
        
        private static TrustManager[] getTrustManagers() throws Exception {
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            if ("Legacy8uJSSE".equals(tmf.getProvider().getName())) {
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
            if (SSLContextImpl.debug != null && Debug.isOn("defaultctx")) {
                System.out.println("keyStore is : " + defaultKeyStore);
                System.out.println("keyStore type is : " + defaultKeyStoreType);
                System.out.println("keyStore provider is : " + defaultKeyStoreProvider);
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
                    if (SSLContextImpl.debug != null && Debug.isOn("defaultctx")) {
                        System.out.println("init keystore");
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
            if (SSLContextImpl.debug != null && Debug.isOn("defaultctx")) {
                System.out.println("init keymanager of type " + KeyManagerFactory.getDefaultAlgorithm());
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
            DefaultManagersHolder.reservedException = null;
            TrustManager[] tmMediator;
            try {
                tmMediator = getTrustManagers();
            }
            catch (final Exception e) {
                DefaultManagersHolder.reservedException = e;
                tmMediator = new TrustManager[0];
            }
            trustManagers = tmMediator;
            if (DefaultManagersHolder.reservedException == null) {
                KeyManager[] kmMediator;
                try {
                    kmMediator = getKeyManagers();
                }
                catch (final Exception e2) {
                    DefaultManagersHolder.reservedException = e2;
                    kmMediator = new KeyManager[0];
                }
                keyManagers = kmMediator;
            }
            else {
                keyManagers = new KeyManager[0];
            }
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
                if (SSLContextImpl.debug != null && Debug.isOn("defaultctx")) {
                    System.out.println("default context init failed: " + e);
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
}
