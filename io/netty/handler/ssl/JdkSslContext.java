package io.netty.handler.ssl;

import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.KeyManager;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.security.KeyStore;
import java.io.IOException;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.security.UnrecoverableKeyException;
import java.security.Security;
import javax.net.ssl.KeyManagerFactory;
import java.io.File;
import io.netty.buffer.ByteBufAllocator;
import javax.net.ssl.SSLSessionContext;
import java.util.Arrays;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.LinkedHashSet;
import io.netty.util.internal.EmptyArrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLContext;
import java.security.Provider;
import java.util.Set;
import java.util.List;
import io.netty.util.internal.logging.InternalLogger;

public class JdkSslContext extends SslContext
{
    private static final InternalLogger logger;
    static final String PROTOCOL = "TLS";
    private static final String[] DEFAULT_PROTOCOLS;
    private static final List<String> DEFAULT_CIPHERS;
    private static final List<String> DEFAULT_CIPHERS_NON_TLSV13;
    private static final Set<String> SUPPORTED_CIPHERS;
    private static final Set<String> SUPPORTED_CIPHERS_NON_TLSV13;
    private static final Provider DEFAULT_PROVIDER;
    private final String[] protocols;
    private final String[] cipherSuites;
    private final List<String> unmodifiableCipherSuites;
    private final JdkApplicationProtocolNegotiator apn;
    private final ClientAuth clientAuth;
    private final SSLContext sslContext;
    private final boolean isClient;
    
    private static String[] defaultProtocols(final SSLContext context, final SSLEngine engine) {
        final String[] supportedProtocols = context.getDefaultSSLParameters().getProtocols();
        final Set<String> supportedProtocolsSet = new HashSet<String>(supportedProtocols.length);
        Collections.addAll(supportedProtocolsSet, supportedProtocols);
        final List<String> protocols = new ArrayList<String>();
        SslUtils.addIfSupported(supportedProtocolsSet, protocols, "TLSv1.3", "TLSv1.2", "TLSv1.1", "TLSv1");
        if (!protocols.isEmpty()) {
            return protocols.toArray(EmptyArrays.EMPTY_STRINGS);
        }
        return engine.getEnabledProtocols();
    }
    
    private static Set<String> supportedCiphers(final SSLEngine engine) {
        final String[] supportedCiphers = engine.getSupportedCipherSuites();
        final Set<String> supportedCiphersSet = new LinkedHashSet<String>(supportedCiphers.length);
        for (int i = 0; i < supportedCiphers.length; ++i) {
            final String supportedCipher = supportedCiphers[i];
            supportedCiphersSet.add(supportedCipher);
            if (supportedCipher.startsWith("SSL_")) {
                final String tlsPrefixedCipherName = "TLS_" + supportedCipher.substring("SSL_".length());
                try {
                    engine.setEnabledCipherSuites(new String[] { tlsPrefixedCipherName });
                    supportedCiphersSet.add(tlsPrefixedCipherName);
                }
                catch (final IllegalArgumentException ex) {}
            }
        }
        return supportedCiphersSet;
    }
    
    private static List<String> defaultCiphers(final SSLEngine engine, final Set<String> supportedCiphers) {
        final List<String> ciphers = new ArrayList<String>();
        SslUtils.addIfSupported(supportedCiphers, ciphers, SslUtils.DEFAULT_CIPHER_SUITES);
        SslUtils.useFallbackCiphersIfDefaultIsEmpty(ciphers, engine.getEnabledCipherSuites());
        return ciphers;
    }
    
    private static boolean isTlsV13Supported(final String[] protocols) {
        for (final String protocol : protocols) {
            if ("TLSv1.3".equals(protocol)) {
                return true;
            }
        }
        return false;
    }
    
    @Deprecated
    public JdkSslContext(final SSLContext sslContext, final boolean isClient, final ClientAuth clientAuth) {
        this(sslContext, isClient, null, IdentityCipherSuiteFilter.INSTANCE, JdkDefaultApplicationProtocolNegotiator.INSTANCE, clientAuth, null, false);
    }
    
    @Deprecated
    public JdkSslContext(final SSLContext sslContext, final boolean isClient, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final ClientAuth clientAuth) {
        this(sslContext, isClient, ciphers, cipherFilter, apn, clientAuth, null, false);
    }
    
    public JdkSslContext(final SSLContext sslContext, final boolean isClient, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final ApplicationProtocolConfig apn, final ClientAuth clientAuth, final String[] protocols, final boolean startTls) {
        this(sslContext, isClient, ciphers, cipherFilter, toNegotiator(apn, !isClient), clientAuth, (String[])((protocols == null) ? null : ((String[])protocols.clone())), startTls);
    }
    
    JdkSslContext(final SSLContext sslContext, final boolean isClient, final Iterable<String> ciphers, final CipherSuiteFilter cipherFilter, final JdkApplicationProtocolNegotiator apn, final ClientAuth clientAuth, final String[] protocols, final boolean startTls) {
        super(startTls);
        this.apn = ObjectUtil.checkNotNull(apn, "apn");
        this.clientAuth = ObjectUtil.checkNotNull(clientAuth, "clientAuth");
        this.sslContext = ObjectUtil.checkNotNull(sslContext, "sslContext");
        Set<String> supportedCiphers;
        List<String> defaultCiphers;
        if (JdkSslContext.DEFAULT_PROVIDER.equals(sslContext.getProvider())) {
            this.protocols = ((protocols == null) ? JdkSslContext.DEFAULT_PROTOCOLS : protocols);
            if (isTlsV13Supported(this.protocols)) {
                supportedCiphers = JdkSslContext.SUPPORTED_CIPHERS;
                defaultCiphers = JdkSslContext.DEFAULT_CIPHERS;
            }
            else {
                supportedCiphers = JdkSslContext.SUPPORTED_CIPHERS_NON_TLSV13;
                defaultCiphers = JdkSslContext.DEFAULT_CIPHERS_NON_TLSV13;
            }
        }
        else {
            final SSLEngine engine = sslContext.createSSLEngine();
            try {
                if (protocols == null) {
                    this.protocols = defaultProtocols(sslContext, engine);
                }
                else {
                    this.protocols = protocols;
                }
                supportedCiphers = supportedCiphers(engine);
                defaultCiphers = defaultCiphers(engine, supportedCiphers);
                if (!isTlsV13Supported(this.protocols)) {
                    for (final String cipher : SslUtils.DEFAULT_TLSV13_CIPHER_SUITES) {
                        supportedCiphers.remove(cipher);
                        defaultCiphers.remove(cipher);
                    }
                }
            }
            finally {
                ReferenceCountUtil.release(engine);
            }
        }
        this.cipherSuites = ObjectUtil.checkNotNull(cipherFilter, "cipherFilter").filterCipherSuites(ciphers, defaultCiphers, supportedCiphers);
        this.unmodifiableCipherSuites = Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])this.cipherSuites));
        this.isClient = isClient;
    }
    
    public final SSLContext context() {
        return this.sslContext;
    }
    
    @Override
    public final boolean isClient() {
        return this.isClient;
    }
    
    @Override
    public final SSLSessionContext sessionContext() {
        if (this.isServer()) {
            return this.context().getServerSessionContext();
        }
        return this.context().getClientSessionContext();
    }
    
    @Override
    public final List<String> cipherSuites() {
        return this.unmodifiableCipherSuites;
    }
    
    @Override
    public final SSLEngine newEngine(final ByteBufAllocator alloc) {
        return this.configureAndWrapEngine(this.context().createSSLEngine(), alloc);
    }
    
    @Override
    public final SSLEngine newEngine(final ByteBufAllocator alloc, final String peerHost, final int peerPort) {
        return this.configureAndWrapEngine(this.context().createSSLEngine(peerHost, peerPort), alloc);
    }
    
    private SSLEngine configureAndWrapEngine(final SSLEngine engine, final ByteBufAllocator alloc) {
        engine.setEnabledCipherSuites(this.cipherSuites);
        engine.setEnabledProtocols(this.protocols);
        engine.setUseClientMode(this.isClient());
        if (this.isServer()) {
            switch (this.clientAuth) {
                case OPTIONAL: {
                    engine.setWantClientAuth(true);
                    break;
                }
                case REQUIRE: {
                    engine.setNeedClientAuth(true);
                    break;
                }
                case NONE: {
                    break;
                }
                default: {
                    throw new Error("Unknown auth " + this.clientAuth);
                }
            }
        }
        final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory factory = this.apn.wrapperFactory();
        if (factory instanceof JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory) {
            return ((JdkApplicationProtocolNegotiator.AllocatorAwareSslEngineWrapperFactory)factory).wrapSslEngine(engine, alloc, this.apn, this.isServer());
        }
        return factory.wrapSslEngine(engine, this.apn, this.isServer());
    }
    
    @Override
    public final JdkApplicationProtocolNegotiator applicationProtocolNegotiator() {
        return this.apn;
    }
    
    static JdkApplicationProtocolNegotiator toNegotiator(final ApplicationProtocolConfig config, final boolean isServer) {
        if (config == null) {
            return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
        }
        switch (config.protocol()) {
            case NONE: {
                return JdkDefaultApplicationProtocolNegotiator.INSTANCE;
            }
            case ALPN: {
                if (isServer) {
                    switch (config.selectorFailureBehavior()) {
                        case FATAL_ALERT: {
                            return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                        }
                        case NO_ADVERTISE: {
                            return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                        }
                        default: {
                            throw new UnsupportedOperationException("JDK provider does not support " + config.selectorFailureBehavior() + " failure behavior");
                        }
                    }
                }
                else {
                    switch (config.selectedListenerFailureBehavior()) {
                        case ACCEPT: {
                            return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                        }
                        case FATAL_ALERT: {
                            return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                        }
                        default: {
                            throw new UnsupportedOperationException("JDK provider does not support " + config.selectedListenerFailureBehavior() + " failure behavior");
                        }
                    }
                }
                break;
            }
            case NPN: {
                if (isServer) {
                    switch (config.selectedListenerFailureBehavior()) {
                        case ACCEPT: {
                            return new JdkNpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                        }
                        case FATAL_ALERT: {
                            return new JdkNpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                        }
                        default: {
                            throw new UnsupportedOperationException("JDK provider does not support " + config.selectedListenerFailureBehavior() + " failure behavior");
                        }
                    }
                }
                else {
                    switch (config.selectorFailureBehavior()) {
                        case FATAL_ALERT: {
                            return new JdkNpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                        }
                        case NO_ADVERTISE: {
                            return new JdkNpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                        }
                        default: {
                            throw new UnsupportedOperationException("JDK provider does not support " + config.selectorFailureBehavior() + " failure behavior");
                        }
                    }
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("JDK provider does not support " + config.protocol() + " protocol");
            }
        }
    }
    
    static KeyManagerFactory buildKeyManagerFactory(final File certChainFile, final File keyFile, final String keyPassword, final KeyManagerFactory kmf, final String keyStore) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        return buildKeyManagerFactory(certChainFile, algorithm, keyFile, keyPassword, kmf, keyStore);
    }
    
    @Deprecated
    protected static KeyManagerFactory buildKeyManagerFactory(final File certChainFile, final File keyFile, final String keyPassword, final KeyManagerFactory kmf) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, CertificateException, KeyException, IOException {
        return buildKeyManagerFactory(certChainFile, keyFile, keyPassword, kmf, KeyStore.getDefaultType());
    }
    
    static KeyManagerFactory buildKeyManagerFactory(final File certChainFile, final String keyAlgorithm, final File keyFile, final String keyPassword, final KeyManagerFactory kmf, final String keyStore) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
        return SslContext.buildKeyManagerFactory(SslContext.toX509Certificates(certChainFile), keyAlgorithm, SslContext.toPrivateKey(keyFile, keyPassword), keyPassword, kmf, keyStore);
    }
    
    @Deprecated
    protected static KeyManagerFactory buildKeyManagerFactory(final File certChainFile, final String keyAlgorithm, final File keyFile, final String keyPassword, final KeyManagerFactory kmf) throws KeyStoreException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, IOException, CertificateException, KeyException, UnrecoverableKeyException {
        return SslContext.buildKeyManagerFactory(SslContext.toX509Certificates(certChainFile), keyAlgorithm, SslContext.toPrivateKey(keyFile, keyPassword), keyPassword, kmf, KeyStore.getDefaultType());
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(JdkSslContext.class);
        SSLContext context;
        try {
            context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
        }
        catch (final Exception e) {
            throw new Error("failed to initialize the default SSL context", e);
        }
        DEFAULT_PROVIDER = context.getProvider();
        final SSLEngine engine = context.createSSLEngine();
        DEFAULT_PROTOCOLS = defaultProtocols(context, engine);
        SUPPORTED_CIPHERS = Collections.unmodifiableSet((Set<? extends String>)supportedCiphers(engine));
        DEFAULT_CIPHERS = Collections.unmodifiableList((List<? extends String>)defaultCiphers(engine, JdkSslContext.SUPPORTED_CIPHERS));
        final List<String> ciphersNonTLSv13 = new ArrayList<String>(JdkSslContext.DEFAULT_CIPHERS);
        ciphersNonTLSv13.removeAll(Arrays.asList(SslUtils.DEFAULT_TLSV13_CIPHER_SUITES));
        DEFAULT_CIPHERS_NON_TLSV13 = Collections.unmodifiableList((List<? extends String>)ciphersNonTLSv13);
        final Set<String> suppertedCiphersNonTLSv13 = new LinkedHashSet<String>(JdkSslContext.SUPPORTED_CIPHERS);
        suppertedCiphersNonTLSv13.removeAll(Arrays.asList(SslUtils.DEFAULT_TLSV13_CIPHER_SUITES));
        SUPPORTED_CIPHERS_NON_TLSV13 = Collections.unmodifiableSet((Set<? extends String>)suppertedCiphersNonTLSv13);
        if (JdkSslContext.logger.isDebugEnabled()) {
            JdkSslContext.logger.debug("Default protocols (JDK): {} ", Arrays.asList(JdkSslContext.DEFAULT_PROTOCOLS));
            JdkSslContext.logger.debug("Default cipher suites (JDK): {}", JdkSslContext.DEFAULT_CIPHERS);
        }
    }
}
