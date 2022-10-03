package sun.security.ssl;

import java.io.InputStream;
import javax.net.ssl.KeyManagerFactory;
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
import java.util.LinkedHashSet;
import java.util.List;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLEngine;
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
        this.clientEnableStapling = Utilities.getBooleanProperty("jdk.tls.client.enableStatusRequestExtension", false);
        this.serverEnableStapling = Utilities.getBooleanProperty("jdk.tls.server.enableStatusRequestExtension", false);
        this.ephemeralKeyManager = new EphemeralKeyManager();
        this.clientCache = new SSLSessionContextImpl();
        this.serverCache = new SSLSessionContextImpl();
    }
    
    @Override
    protected void engineInit(final KeyManager[] array, TrustManager[] trustManagers, final SecureRandom secureRandom) throws KeyManagementException {
        this.isInitialized = false;
        this.keyManager = this.chooseKeyManager(array);
        if (trustManagers == null) {
            try {
                final TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                instance.init((KeyStore)null);
                trustManagers = instance.getTrustManagers();
            }
            catch (final Exception ex) {}
        }
        this.trustManager = this.chooseTrustManager(trustManagers);
        if (secureRandom == null) {
            this.secureRandom = JsseJce.getSecureRandom();
        }
        else {
            if (SunJSSE.isFIPS() && secureRandom.getProvider() != SunJSSE.cryptoProvider) {
                throw new KeyManagementException("FIPS mode: SecureRandom must be from provider " + SunJSSE.cryptoProvider.getName());
            }
            this.secureRandom = secureRandom;
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
    
    private X509TrustManager chooseTrustManager(final TrustManager[] array) throws KeyManagementException {
        int n = 0;
        while (array != null && n < array.length) {
            if (array[n] instanceof X509TrustManager) {
                if (SunJSSE.isFIPS() && !(array[n] instanceof X509TrustManagerImpl)) {
                    throw new KeyManagementException("FIPS mode: only SunJSSE TrustManagers may be used");
                }
                if (array[n] instanceof X509ExtendedTrustManager) {
                    return (X509TrustManager)array[n];
                }
                return new AbstractTrustManagerWrapper((X509TrustManager)array[n]);
            }
            else {
                ++n;
            }
        }
        return DummyX509TrustManager.INSTANCE;
    }
    
    private X509ExtendedKeyManager chooseKeyManager(final KeyManager[] array) throws KeyManagementException {
        int n = 0;
        while (array != null && n < array.length) {
            final KeyManager keyManager = array[n];
            if (!(keyManager instanceof X509KeyManager)) {
                ++n;
            }
            else if (SunJSSE.isFIPS()) {
                if (keyManager instanceof X509KeyManagerImpl || keyManager instanceof SunX509KeyManagerImpl) {
                    return (X509ExtendedKeyManager)keyManager;
                }
                throw new KeyManagementException("FIPS mode: only SunJSSE KeyManagers may be used");
            }
            else {
                if (keyManager instanceof X509ExtendedKeyManager) {
                    return (X509ExtendedKeyManager)keyManager;
                }
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
                    SSLLogger.warning("X509KeyManager passed to SSLContext.init():  need an X509ExtendedKeyManager for SSLEngine use", new Object[0]);
                }
                return new AbstractKeyManagerWrapper((X509KeyManager)keyManager);
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
    protected SSLEngine engineCreateSSLEngine(final String s, final int n) {
        if (!this.isInitialized) {
            throw new IllegalStateException("SSLContext is not initialized");
        }
        return this.createSSLEngineImpl(s, n);
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
    
    List<ProtocolVersion> getDefaultProtocolVersions(final boolean b) {
        return b ? this.getServerDefaultProtocolVersions() : this.getClientDefaultProtocolVersions();
    }
    
    List<CipherSuite> getDefaultCipherSuites(final boolean b) {
        return b ? this.getServerDefaultCipherSuites() : this.getClientDefaultCipherSuites();
    }
    
    boolean isDefaultProtocolVesions(final List<ProtocolVersion> list) {
        return list == this.getServerDefaultProtocolVersions() || list == this.getClientDefaultProtocolVersions();
    }
    
    boolean isDefaultCipherSuiteList(final List<CipherSuite> list) {
        return list == this.getServerDefaultCipherSuites() || list == this.getClientDefaultCipherSuites();
    }
    
    boolean isStaplingEnabled(final boolean b) {
        return b ? this.clientEnableStapling : this.serverEnableStapling;
    }
    
    private static List<CipherSuite> getApplicableSupportedCipherSuites(final List<ProtocolVersion> list) {
        return getApplicableCipherSuites(CipherSuite.allowedCipherSuites(), list);
    }
    
    private static List<CipherSuite> getApplicableEnabledCipherSuites(final List<ProtocolVersion> list, final boolean b) {
        if (b) {
            if (!SSLContextImpl.clientCustomizedCipherSuites.isEmpty()) {
                return getApplicableCipherSuites(SSLContextImpl.clientCustomizedCipherSuites, list);
            }
        }
        else if (!SSLContextImpl.serverCustomizedCipherSuites.isEmpty()) {
            return getApplicableCipherSuites(SSLContextImpl.serverCustomizedCipherSuites, list);
        }
        return getApplicableCipherSuites(CipherSuite.defaultCipherSuites(), list);
    }
    
    private static List<CipherSuite> getApplicableCipherSuites(final Collection<CipherSuite> collection, final List<ProtocolVersion> list) {
        final LinkedHashSet set = new LinkedHashSet();
        if (list != null && !list.isEmpty()) {
            for (final CipherSuite cipherSuite : collection) {
                if (!cipherSuite.isAvailable()) {
                    continue;
                }
                boolean b = false;
                final Iterator iterator2 = list.iterator();
                while (iterator2.hasNext()) {
                    if (cipherSuite.supports((ProtocolVersion)iterator2.next())) {
                        if (!cipherSuite.bulkCipher.isAvailable()) {
                            continue;
                        }
                        if (SSLAlgorithmConstraints.DEFAULT.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), cipherSuite.name, null)) {
                            set.add(cipherSuite);
                            b = true;
                            break;
                        }
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx,verbose")) {
                            SSLLogger.fine("Ignore disabled cipher suite: " + cipherSuite.name, new Object[0]);
                            break;
                        }
                        break;
                    }
                }
                if (b || !SSLLogger.isOn || !SSLLogger.isOn("ssl,sslctx,verbose")) {
                    continue;
                }
                SSLLogger.finest("Ignore unsupported cipher suite: " + cipherSuite, new Object[0]);
            }
        }
        return new ArrayList<CipherSuite>(set);
    }
    
    private static Collection<CipherSuite> getCustomizedCipherSuites(final String s) {
        String s2 = GetPropertyAction.privilegedGetProperty(s);
        if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
            SSLLogger.fine("System property " + s + " is set to '" + s2 + "'", new Object[0]);
        }
        if (s2 != null && !s2.isEmpty() && s2.length() > 1 && s2.charAt(0) == '\"' && s2.charAt(s2.length() - 1) == '\"') {
            s2 = s2.substring(1, s2.length() - 1);
        }
        if (s2 != null && !s2.isEmpty()) {
            final String[] split = s2.split(",");
            final ArrayList list = new ArrayList(split.length);
            for (int i = 0; i < split.length; ++i) {
                split[i] = split[i].trim();
                if (!split[i].isEmpty()) {
                    CipherSuite name;
                    try {
                        name = CipherSuite.nameOf(split[i]);
                    }
                    catch (final IllegalArgumentException ex) {
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
                            SSLLogger.fine("Unknown or unsupported cipher suite name: " + split[i], new Object[0]);
                        }
                        continue;
                    }
                    if (name != null && name.isAvailable()) {
                        list.add((Object)name);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl,sslctx")) {
                        SSLLogger.fine("The current installed providers do not support cipher suite: " + split[i], new Object[0]);
                    }
                }
            }
            return (Collection<CipherSuite>)list;
        }
        return (Collection<CipherSuite>)Collections.emptyList();
    }
    
    private static List<ProtocolVersion> getAvailableProtocols(final ProtocolVersion[] array) {
        List<Object> emptyList = Collections.emptyList();
        if (array != null && array.length != 0) {
            emptyList = new ArrayList<Object>(array.length);
            for (final ProtocolVersion protocolVersion : array) {
                if (protocolVersion.isAvailable) {
                    emptyList.add(protocolVersion);
                }
            }
        }
        return (List<ProtocolVersion>)emptyList;
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
        SSLEngine createSSLEngineImpl(final String s, final int n) {
            return new SSLEngineImpl(this, s, n);
        }
        
        static ProtocolVersion[] getSupportedProtocols() {
            if (SunJSSE.isFIPS()) {
                return new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10 };
            }
            return new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30, ProtocolVersion.SSL20Hello };
        }
        
        static {
            if (SunJSSE.isFIPS()) {
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
            if (SunJSSE.isFIPS()) {
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
            if (SunJSSE.isFIPS()) {
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
            if (SunJSSE.isFIPS()) {
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
            if (SunJSSE.isFIPS()) {
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
        
        private static void populate(final String s, final ArrayList<ProtocolVersion> list) {
            String s2 = GetPropertyAction.privilegedGetProperty(s);
            if (s2 == null) {
                return;
            }
            if (!s2.isEmpty() && s2.length() > 1 && s2.charAt(0) == '\"' && s2.charAt(s2.length() - 1) == '\"') {
                s2 = s2.substring(1, s2.length() - 1);
            }
            if (!s2.isEmpty()) {
                final String[] split = s2.split(",");
                for (int i = 0; i < split.length; ++i) {
                    split[i] = split[i].trim();
                    final ProtocolVersion name = ProtocolVersion.nameOf(split[i]);
                    if (name == null) {
                        CustomizedSSLProtocols.reservedException = new IllegalArgumentException(s + ": " + split[i] + " is not a supported SSL protocol name");
                    }
                    if (SunJSSE.isFIPS() && (name == ProtocolVersion.SSL30 || name == ProtocolVersion.SSL20Hello)) {
                        CustomizedSSLProtocols.reservedException = new IllegalArgumentException(s + ": " + name + " is not FIPS compliant");
                        break;
                    }
                    if (!list.contains(name)) {
                        list.add(name);
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
        
        private static List<ProtocolVersion> customizedProtocols(final boolean b, final List<ProtocolVersion> list) {
            final ArrayList list2 = new ArrayList();
            final Iterator<ProtocolVersion> iterator = list.iterator();
            while (iterator.hasNext()) {
                list2.add(iterator.next());
            }
            ProtocolVersion[] array;
            if (list2.isEmpty()) {
                if (b) {
                    array = getProtocols();
                }
                else {
                    array = AbstractTLSContext.getSupportedProtocols();
                }
            }
            else {
                array = (ProtocolVersion[])list2.toArray(new ProtocolVersion[list2.size()]);
            }
            return getAvailableProtocols(array);
        }
        
        static ProtocolVersion[] getProtocols() {
            if (SunJSSE.isFIPS()) {
                return new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10 };
            }
            return new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
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
            final TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            if ("SunJSSE".equals(instance.getProvider().getName())) {
                instance.init((KeyStore)null);
            }
            else {
                instance.init(TrustStoreManager.getTrustedKeyStore());
            }
            return instance.getTrustManagers();
        }
        
        private static KeyManager[] getKeyManagers() throws Exception {
            final HashMap hashMap = new HashMap();
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    hashMap.put("keyStore", System.getProperty("javax.net.ssl.keyStore", ""));
                    hashMap.put("keyStoreType", System.getProperty("javax.net.ssl.keyStoreType", KeyStore.getDefaultType()));
                    hashMap.put("keyStoreProvider", System.getProperty("javax.net.ssl.keyStoreProvider", ""));
                    hashMap.put("keyStorePasswd", System.getProperty("javax.net.ssl.keyStorePassword", ""));
                    return null;
                }
            });
            final String s = (String)hashMap.get("keyStore");
            final String s2 = (String)hashMap.get("keyStoreType");
            final String s3 = (String)hashMap.get("keyStoreProvider");
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,defaultctx")) {
                SSLLogger.fine("keyStore is : " + s, new Object[0]);
                SSLLogger.fine("keyStore type is : " + s2, new Object[0]);
                SSLLogger.fine("keyStore provider is : " + s3, new Object[0]);
            }
            if ("PKCS11".equals(s2) && !"NONE".equals(s)) {
                throw new IllegalArgumentException("if keyStoreType is PKCS11, then keyStore must be NONE");
            }
            InputStream inputStream = null;
            KeyStore keyStore = null;
            char[] charArray = null;
            try {
                if (!s.isEmpty() && !"NONE".equals(s)) {
                    inputStream = AccessController.doPrivileged((PrivilegedExceptionAction<FileInputStream>)new PrivilegedExceptionAction<FileInputStream>() {
                        @Override
                        public FileInputStream run() throws Exception {
                            return new FileInputStream(s);
                        }
                    });
                }
                final String s4 = (String)hashMap.get("keyStorePasswd");
                if (!s4.isEmpty()) {
                    charArray = s4.toCharArray();
                }
                if (s2.length() != 0) {
                    if (SSLLogger.isOn && SSLLogger.isOn("ssl,defaultctx")) {
                        SSLLogger.finest("init keystore", new Object[0]);
                    }
                    if (s3.isEmpty()) {
                        keyStore = KeyStore.getInstance(s2);
                    }
                    else {
                        keyStore = KeyStore.getInstance(s2, s3);
                    }
                    keyStore.load(inputStream, charArray);
                }
            }
            finally {
                if (inputStream != null) {
                    ((FileInputStream)inputStream).close();
                }
            }
            if (SSLLogger.isOn && SSLLogger.isOn("ssl,defaultctx")) {
                SSLLogger.fine("init keymanager of type " + KeyManagerFactory.getDefaultAlgorithm(), new Object[0]);
            }
            final KeyManagerFactory instance = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            if ("PKCS11".equals(s2)) {
                instance.init(keyStore, null);
            }
            else {
                instance.init(keyStore, charArray);
            }
            return instance.getKeyManagers();
        }
        
        static {
            Exception reservedException2 = null;
            TrustManager[] trustManagers2;
            try {
                trustManagers2 = getTrustManagers();
            }
            catch (final Exception ex) {
                reservedException2 = ex;
                trustManagers2 = new TrustManager[0];
            }
            trustManagers = trustManagers2;
            if (reservedException2 == null) {
                KeyManager[] keyManagers2;
                try {
                    keyManagers2 = getKeyManagers();
                }
                catch (final Exception ex2) {
                    reservedException2 = ex2;
                    keyManagers2 = new KeyManager[0];
                }
                keyManagers = keyManagers2;
            }
            else {
                keyManagers = new KeyManager[0];
            }
            reservedException = reservedException2;
        }
    }
    
    private static final class DefaultSSLContextHolder
    {
        private static final SSLContextImpl sslContext;
        static Exception reservedException;
        
        static {
            DefaultSSLContextHolder.reservedException = null;
            SSLContextImpl sslContext2 = null;
            if (DefaultManagersHolder.reservedException != null) {
                DefaultSSLContextHolder.reservedException = DefaultManagersHolder.reservedException;
            }
            else {
                try {
                    sslContext2 = new DefaultSSLContext();
                }
                catch (final Exception reservedException) {
                    DefaultSSLContextHolder.reservedException = reservedException;
                }
            }
            sslContext = sslContext2;
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
            catch (final Exception ex) {
                if (SSLLogger.isOn && SSLLogger.isOn("ssl,defaultctx")) {
                    SSLLogger.fine("default context init failed: ", ex);
                }
                throw ex;
            }
        }
        
        @Override
        protected void engineInit(final KeyManager[] array, final TrustManager[] array2, final SecureRandom secureRandom) throws KeyManagementException {
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
