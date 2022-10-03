package org.openjsse.sun.security.ssl;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.AbstractMap;
import java.util.EnumMap;
import java.security.AlgorithmParameters;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SNIServerName;
import javax.security.auth.x500.X500Principal;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Queue;
import java.security.AlgorithmConstraints;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashMap;

abstract class HandshakeContext implements ConnectionContext
{
    static final boolean allowUnsafeRenegotiation;
    static final boolean allowLegacyHelloMessages;
    LinkedHashMap<Byte, SSLConsumer> handshakeConsumers;
    final HashMap<Byte, HandshakeProducer> handshakeProducers;
    final SSLContextImpl sslContext;
    final TransportContext conContext;
    final SSLConfiguration sslConfig;
    final List<ProtocolVersion> activeProtocols;
    final List<CipherSuite> activeCipherSuites;
    final AlgorithmConstraints algorithmConstraints;
    final ProtocolVersion maximumActiveProtocol;
    final HandshakeOutStream handshakeOutput;
    final HandshakeHash handshakeHash;
    SSLSessionImpl handshakeSession;
    boolean handshakeFinished;
    boolean kickstartMessageDelivered;
    boolean isResumption;
    SSLSessionImpl resumingSession;
    final Queue<Map.Entry<Byte, ByteBuffer>> delegatedActions;
    volatile boolean taskDelegated;
    volatile Exception delegatedThrown;
    ProtocolVersion negotiatedProtocol;
    CipherSuite negotiatedCipherSuite;
    final List<SSLPossession> handshakePossessions;
    final List<SSLCredentials> handshakeCredentials;
    SSLKeyDerivation handshakeKeyDerivation;
    SSLKeyExchange handshakeKeyExchange;
    SecretKey baseReadSecret;
    SecretKey baseWriteSecret;
    int clientHelloVersion;
    String applicationProtocol;
    RandomCookie clientHelloRandom;
    RandomCookie serverHelloRandom;
    byte[] certRequestContext;
    final Map<SSLExtension, SSLExtension.SSLExtensionSpec> handshakeExtensions;
    int maxFragmentLength;
    List<SignatureScheme> localSupportedSignAlgs;
    List<SignatureScheme> peerRequestedSignatureSchemes;
    List<SignatureScheme> peerRequestedCertSignSchemes;
    X500Principal[] peerSupportedAuthorities;
    List<X500Principal> localSupportedAuthorities;
    List<SupportedGroupsExtension.NamedGroup> clientRequestedNamedGroups;
    SupportedGroupsExtension.NamedGroup serverSelectedNamedGroup;
    List<SNIServerName> requestedServerNames;
    SNIServerName negotiatedServerName;
    boolean staplingActive;
    
    protected HandshakeContext(final SSLContextImpl sslContext, final TransportContext conContext) throws IOException {
        this.taskDelegated = false;
        this.delegatedThrown = null;
        this.peerSupportedAuthorities = null;
        this.localSupportedAuthorities = null;
        this.staplingActive = false;
        this.sslContext = sslContext;
        this.conContext = conContext;
        this.sslConfig = (SSLConfiguration)conContext.sslConfig.clone();
        this.algorithmConstraints = new SSLAlgorithmConstraints(this.sslConfig.userSpecifiedAlgorithmConstraints);
        this.activeProtocols = getActiveProtocols(this.sslConfig.enabledProtocols, this.sslConfig.enabledCipherSuites, this.algorithmConstraints);
        if (this.activeProtocols.isEmpty()) {
            throw new SSLHandshakeException("No appropriate protocol (protocol is disabled or cipher suites are inappropriate)");
        }
        ProtocolVersion maximumVersion = ProtocolVersion.NONE;
        for (final ProtocolVersion pv : this.activeProtocols) {
            if (maximumVersion == ProtocolVersion.NONE || pv.compare(maximumVersion) > 0) {
                maximumVersion = pv;
            }
        }
        this.maximumActiveProtocol = maximumVersion;
        this.activeCipherSuites = getActiveCipherSuites(this.activeProtocols, this.sslConfig.enabledCipherSuites, this.algorithmConstraints);
        if (this.activeCipherSuites.isEmpty()) {
            throw new SSLHandshakeException("No appropriate cipher suite");
        }
        this.handshakeConsumers = new LinkedHashMap<Byte, SSLConsumer>();
        this.handshakeProducers = new HashMap<Byte, HandshakeProducer>();
        this.handshakeHash = conContext.inputRecord.handshakeHash;
        this.handshakeOutput = new HandshakeOutStream(conContext.outputRecord);
        this.handshakeFinished = false;
        this.kickstartMessageDelivered = false;
        this.delegatedActions = new LinkedList<Map.Entry<Byte, ByteBuffer>>();
        this.handshakeExtensions = new HashMap<SSLExtension, SSLExtension.SSLExtensionSpec>();
        this.handshakePossessions = new LinkedList<SSLPossession>();
        this.handshakeCredentials = new LinkedList<SSLCredentials>();
        this.requestedServerNames = null;
        this.negotiatedServerName = null;
        this.negotiatedCipherSuite = conContext.cipherSuite;
        this.initialize();
    }
    
    protected HandshakeContext(final TransportContext conContext) {
        this.taskDelegated = false;
        this.delegatedThrown = null;
        this.peerSupportedAuthorities = null;
        this.localSupportedAuthorities = null;
        this.staplingActive = false;
        this.sslContext = conContext.sslContext;
        this.conContext = conContext;
        this.sslConfig = conContext.sslConfig;
        this.negotiatedProtocol = conContext.protocolVersion;
        this.negotiatedCipherSuite = conContext.cipherSuite;
        this.handshakeOutput = new HandshakeOutStream(conContext.outputRecord);
        this.delegatedActions = new LinkedList<Map.Entry<Byte, ByteBuffer>>();
        this.handshakeConsumers = new LinkedHashMap<Byte, SSLConsumer>();
        this.handshakeProducers = null;
        this.handshakeHash = null;
        this.activeProtocols = null;
        this.activeCipherSuites = null;
        this.algorithmConstraints = null;
        this.maximumActiveProtocol = null;
        this.handshakeExtensions = Collections.emptyMap();
        this.handshakePossessions = null;
        this.handshakeCredentials = null;
    }
    
    private void initialize() {
        ProtocolVersion inputHelloVersion;
        ProtocolVersion outputHelloVersion;
        if (this.conContext.isNegotiated) {
            inputHelloVersion = this.conContext.protocolVersion;
            outputHelloVersion = this.conContext.protocolVersion;
        }
        else if (this.activeProtocols.contains(ProtocolVersion.SSL20Hello)) {
            inputHelloVersion = ProtocolVersion.SSL20Hello;
            if (this.maximumActiveProtocol.useTLS13PlusSpec()) {
                outputHelloVersion = this.maximumActiveProtocol;
            }
            else {
                outputHelloVersion = ProtocolVersion.SSL20Hello;
            }
        }
        else {
            inputHelloVersion = this.maximumActiveProtocol;
            outputHelloVersion = this.maximumActiveProtocol;
        }
        this.conContext.inputRecord.setHelloVersion(inputHelloVersion);
        this.conContext.outputRecord.setHelloVersion(outputHelloVersion);
        if (!this.conContext.isNegotiated) {
            this.conContext.protocolVersion = this.maximumActiveProtocol;
        }
        this.conContext.outputRecord.setVersion(this.conContext.protocolVersion);
    }
    
    private static List<ProtocolVersion> getActiveProtocols(final List<ProtocolVersion> enabledProtocols, final List<CipherSuite> enabledCipherSuites, final AlgorithmConstraints algorithmConstraints) {
        boolean enabledSSL20Hello = false;
        final ArrayList<ProtocolVersion> protocols = new ArrayList<ProtocolVersion>(4);
        for (final ProtocolVersion protocol : enabledProtocols) {
            if (!enabledSSL20Hello && protocol == ProtocolVersion.SSL20Hello) {
                enabledSSL20Hello = true;
            }
            else {
                if (!algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), protocol.name, null)) {
                    continue;
                }
                boolean found = false;
                final Map<SupportedGroupsExtension.NamedGroupType, Boolean> cachedStatus = new EnumMap<SupportedGroupsExtension.NamedGroupType, Boolean>(SupportedGroupsExtension.NamedGroupType.class);
                for (final CipherSuite suite : enabledCipherSuites) {
                    if (suite.isAvailable() && suite.supports(protocol)) {
                        if (isActivatable(suite, algorithmConstraints, cachedStatus)) {
                            protocols.add(protocol);
                            found = true;
                            break;
                        }
                        continue;
                    }
                    else {
                        if (!SSLLogger.isOn || !SSLLogger.isOn("verbose")) {
                            continue;
                        }
                        SSLLogger.fine("Ignore unsupported cipher suite: " + suite + " for " + protocol, new Object[0]);
                    }
                }
                if (found || !SSLLogger.isOn || !SSLLogger.isOn("handshake")) {
                    continue;
                }
                SSLLogger.fine("No available cipher suite for " + protocol, new Object[0]);
            }
        }
        if (!protocols.isEmpty()) {
            if (enabledSSL20Hello) {
                protocols.add(ProtocolVersion.SSL20Hello);
            }
            Collections.sort(protocols);
        }
        return Collections.unmodifiableList((List<? extends ProtocolVersion>)protocols);
    }
    
    private static List<CipherSuite> getActiveCipherSuites(final List<ProtocolVersion> enabledProtocols, final List<CipherSuite> enabledCipherSuites, final AlgorithmConstraints algorithmConstraints) {
        final List<CipherSuite> suites = new LinkedList<CipherSuite>();
        if (enabledProtocols != null && !enabledProtocols.isEmpty()) {
            final Map<SupportedGroupsExtension.NamedGroupType, Boolean> cachedStatus = new EnumMap<SupportedGroupsExtension.NamedGroupType, Boolean>(SupportedGroupsExtension.NamedGroupType.class);
            for (final CipherSuite suite : enabledCipherSuites) {
                if (!suite.isAvailable()) {
                    continue;
                }
                boolean isSupported = false;
                for (final ProtocolVersion protocol : enabledProtocols) {
                    if (!suite.supports(protocol)) {
                        continue;
                    }
                    if (isActivatable(suite, algorithmConstraints, cachedStatus)) {
                        suites.add(suite);
                        isSupported = true;
                        break;
                    }
                }
                if (isSupported || !SSLLogger.isOn || !SSLLogger.isOn("verbose")) {
                    continue;
                }
                SSLLogger.finest("Ignore unsupported cipher suite: " + suite, new Object[0]);
            }
        }
        return Collections.unmodifiableList((List<? extends CipherSuite>)suites);
    }
    
    static byte getHandshakeType(final TransportContext conContext, final Plaintext plaintext) throws IOException {
        if (plaintext.contentType != ContentType.HANDSHAKE.id) {
            throw conContext.fatal(Alert.INTERNAL_ERROR, "Unexpected operation for record: " + plaintext.contentType);
        }
        if (plaintext.fragment == null || plaintext.fragment.remaining() < 4) {
            throw conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid handshake message: insufficient data");
        }
        final byte handshakeType = (byte)Record.getInt8(plaintext.fragment);
        final int handshakeLen = Record.getInt24(plaintext.fragment);
        if (handshakeLen != plaintext.fragment.remaining()) {
            throw conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid handshake message: insufficient handshake body");
        }
        return handshakeType;
    }
    
    void dispatch(final byte handshakeType, final Plaintext plaintext) throws IOException {
        if (this.conContext.transport.useDelegatedTask()) {
            final boolean hasDelegated = !this.delegatedActions.isEmpty();
            if (hasDelegated || (handshakeType != SSLHandshake.FINISHED.id && handshakeType != SSLHandshake.KEY_UPDATE.id && handshakeType != SSLHandshake.NEW_SESSION_TICKET.id)) {
                if (!hasDelegated) {
                    this.taskDelegated = false;
                    this.delegatedThrown = null;
                }
                ByteBuffer fragment = ByteBuffer.wrap(new byte[plaintext.fragment.remaining()]);
                fragment.put(plaintext.fragment);
                fragment = (ByteBuffer)fragment.rewind();
                this.delegatedActions.add(new AbstractMap.SimpleImmutableEntry<Byte, ByteBuffer>(handshakeType, fragment));
            }
            else {
                this.dispatch(handshakeType, plaintext.fragment);
            }
        }
        else {
            this.dispatch(handshakeType, plaintext.fragment);
        }
    }
    
    void dispatch(final byte handshakeType, final ByteBuffer fragment) throws IOException {
        SSLConsumer consumer;
        if (handshakeType == SSLHandshake.HELLO_REQUEST.id) {
            consumer = SSLHandshake.HELLO_REQUEST;
        }
        else {
            consumer = this.handshakeConsumers.get(handshakeType);
        }
        if (consumer == null) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected handshake message: " + SSLHandshake.nameOf(handshakeType));
        }
        try {
            consumer.consume(this, fragment);
        }
        catch (final UnsupportedOperationException unsoe) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported handshake message: " + SSLHandshake.nameOf(handshakeType), unsoe);
        }
        catch (final BufferUnderflowException | BufferOverflowException be) {
            throw this.conContext.fatal(Alert.DECODE_ERROR, "Illegal handshake message: " + SSLHandshake.nameOf(handshakeType), be);
        }
        this.handshakeHash.consume();
    }
    
    abstract void kickstart() throws IOException;
    
    boolean isNegotiable(final CipherSuite cs) {
        return isNegotiable(this.activeCipherSuites, cs);
    }
    
    static final boolean isNegotiable(final List<CipherSuite> proposed, final CipherSuite cs) {
        return proposed.contains(cs) && cs.isNegotiable();
    }
    
    static final boolean isNegotiable(final List<CipherSuite> proposed, final ProtocolVersion protocolVersion, final CipherSuite cs) {
        return proposed.contains(cs) && cs.isNegotiable() && cs.supports(protocolVersion);
    }
    
    boolean isNegotiable(final ProtocolVersion protocolVersion) {
        return this.activeProtocols.contains(protocolVersion);
    }
    
    void setVersion(final ProtocolVersion protocolVersion) {
        this.conContext.protocolVersion = protocolVersion;
    }
    
    private static boolean isActivatable(final CipherSuite suite, final AlgorithmConstraints algorithmConstraints, final Map<SupportedGroupsExtension.NamedGroupType, Boolean> cachedStatus) {
        if (!algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), suite.name, null)) {
            if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                SSLLogger.fine("Ignore disabled cipher suite: " + suite, new Object[0]);
            }
            return false;
        }
        if (suite.keyExchange == null) {
            return true;
        }
        final SupportedGroupsExtension.NamedGroupType groupType = suite.keyExchange.groupType;
        if (groupType != SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_NONE) {
            final Boolean checkedStatus = cachedStatus.get(groupType);
            boolean available;
            if (checkedStatus == null) {
                available = SupportedGroupsExtension.SupportedGroups.isActivatable(algorithmConstraints, groupType);
                cachedStatus.put(groupType, available);
                if (!available && SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("No activated named group", new Object[0]);
                }
            }
            else {
                available = checkedStatus;
            }
            if (!available && SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                SSLLogger.fine("No active named group, ignore " + suite, new Object[0]);
            }
            return available;
        }
        return true;
    }
    
    List<SNIServerName> getRequestedServerNames() {
        if (this.requestedServerNames == null) {
            return Collections.emptyList();
        }
        return this.requestedServerNames;
    }
    
    static {
        allowUnsafeRenegotiation = Utilities.getBooleanProperty("sun.security.ssl.allowUnsafeRenegotiation", false);
        allowLegacyHelloMessages = Utilities.getBooleanProperty("sun.security.ssl.allowLegacyHelloMessages", true);
    }
}
