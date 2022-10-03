package sun.security.ssl;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.util.AbstractMap;
import java.nio.Buffer;
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
    List<SupportedGroupsExtension.NamedGroup> clientRequestedNamedGroups;
    SupportedGroupsExtension.NamedGroup serverSelectedNamedGroup;
    List<SNIServerName> requestedServerNames;
    SNIServerName negotiatedServerName;
    boolean staplingActive;
    
    protected HandshakeContext(final SSLContextImpl sslContext, final TransportContext conContext) throws IOException {
        this.taskDelegated = false;
        this.delegatedThrown = null;
        this.peerSupportedAuthorities = null;
        this.staplingActive = false;
        this.sslContext = sslContext;
        this.conContext = conContext;
        this.sslConfig = (SSLConfiguration)conContext.sslConfig.clone();
        this.algorithmConstraints = new SSLAlgorithmConstraints(this.sslConfig.userSpecifiedAlgorithmConstraints);
        this.activeProtocols = getActiveProtocols(this.sslConfig.enabledProtocols, this.sslConfig.enabledCipherSuites, this.algorithmConstraints);
        if (this.activeProtocols.isEmpty()) {
            throw new SSLHandshakeException("No appropriate protocol (protocol is disabled or cipher suites are inappropriate)");
        }
        ProtocolVersion none = ProtocolVersion.NONE;
        for (final ProtocolVersion protocolVersion : this.activeProtocols) {
            if (none == ProtocolVersion.NONE || protocolVersion.compare(none) > 0) {
                none = protocolVersion;
            }
        }
        this.maximumActiveProtocol = none;
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
        ProtocolVersion helloVersion;
        ProtocolVersion helloVersion2;
        if (this.conContext.isNegotiated) {
            helloVersion = this.conContext.protocolVersion;
            helloVersion2 = this.conContext.protocolVersion;
        }
        else if (this.activeProtocols.contains(ProtocolVersion.SSL20Hello)) {
            helloVersion = ProtocolVersion.SSL20Hello;
            if (this.maximumActiveProtocol.useTLS13PlusSpec()) {
                helloVersion2 = this.maximumActiveProtocol;
            }
            else {
                helloVersion2 = ProtocolVersion.SSL20Hello;
            }
        }
        else {
            helloVersion = this.maximumActiveProtocol;
            helloVersion2 = this.maximumActiveProtocol;
        }
        this.conContext.inputRecord.setHelloVersion(helloVersion);
        this.conContext.outputRecord.setHelloVersion(helloVersion2);
        if (!this.conContext.isNegotiated) {
            this.conContext.protocolVersion = this.maximumActiveProtocol;
        }
        this.conContext.outputRecord.setVersion(this.conContext.protocolVersion);
    }
    
    private static List<ProtocolVersion> getActiveProtocols(final List<ProtocolVersion> list, final List<CipherSuite> list2, final AlgorithmConstraints algorithmConstraints) {
        int n = 0;
        final ArrayList list3 = new ArrayList(4);
        for (final ProtocolVersion protocolVersion : list) {
            if (n == 0 && protocolVersion == ProtocolVersion.SSL20Hello) {
                n = 1;
            }
            else {
                if (!algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), protocolVersion.name, null)) {
                    continue;
                }
                boolean b = false;
                final EnumMap<SupportedGroupsExtension.NamedGroupType, Boolean> enumMap = new EnumMap<SupportedGroupsExtension.NamedGroupType, Boolean>(SupportedGroupsExtension.NamedGroupType.class);
                for (final CipherSuite cipherSuite : list2) {
                    if (cipherSuite.isAvailable() && cipherSuite.supports(protocolVersion)) {
                        if (isActivatable(cipherSuite, algorithmConstraints, enumMap)) {
                            list3.add(protocolVersion);
                            b = true;
                            break;
                        }
                        continue;
                    }
                    else {
                        if (!SSLLogger.isOn || !SSLLogger.isOn("verbose")) {
                            continue;
                        }
                        SSLLogger.fine("Ignore unsupported cipher suite: " + cipherSuite + " for " + protocolVersion, new Object[0]);
                    }
                }
                if (b || !SSLLogger.isOn || !SSLLogger.isOn("handshake")) {
                    continue;
                }
                SSLLogger.fine("No available cipher suite for " + protocolVersion, new Object[0]);
            }
        }
        if (!list3.isEmpty()) {
            if (n != 0) {
                list3.add(ProtocolVersion.SSL20Hello);
            }
            Collections.sort((List<Comparable>)list3);
        }
        return (List<ProtocolVersion>)Collections.unmodifiableList((List<?>)list3);
    }
    
    private static List<CipherSuite> getActiveCipherSuites(final List<ProtocolVersion> list, final List<CipherSuite> list2, final AlgorithmConstraints algorithmConstraints) {
        final LinkedList list3 = new LinkedList();
        if (list != null && !list.isEmpty()) {
            final EnumMap enumMap = new EnumMap((Class<K>)SupportedGroupsExtension.NamedGroupType.class);
            for (final CipherSuite cipherSuite : list2) {
                if (!cipherSuite.isAvailable()) {
                    continue;
                }
                boolean b = false;
                final Iterator iterator2 = list.iterator();
                while (iterator2.hasNext()) {
                    if (!cipherSuite.supports((ProtocolVersion)iterator2.next())) {
                        continue;
                    }
                    if (isActivatable(cipherSuite, algorithmConstraints, enumMap)) {
                        list3.add(cipherSuite);
                        b = true;
                        break;
                    }
                }
                if (b || !SSLLogger.isOn || !SSLLogger.isOn("verbose")) {
                    continue;
                }
                SSLLogger.finest("Ignore unsupported cipher suite: " + cipherSuite, new Object[0]);
            }
        }
        return (List<CipherSuite>)Collections.unmodifiableList((List<?>)list3);
    }
    
    static byte getHandshakeType(final TransportContext transportContext, final Plaintext plaintext) throws IOException {
        if (plaintext.contentType != ContentType.HANDSHAKE.id) {
            throw transportContext.fatal(Alert.INTERNAL_ERROR, "Unexpected operation for record: " + plaintext.contentType);
        }
        if (plaintext.fragment == null || plaintext.fragment.remaining() < 4) {
            throw transportContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid handshake message: insufficient data");
        }
        final byte b = (byte)Record.getInt8(plaintext.fragment);
        if (Record.getInt24(plaintext.fragment) != plaintext.fragment.remaining()) {
            throw transportContext.fatal(Alert.UNEXPECTED_MESSAGE, "Invalid handshake message: insufficient handshake body");
        }
        return b;
    }
    
    void dispatch(final byte b, final Plaintext plaintext) throws IOException {
        if (this.conContext.transport.useDelegatedTask()) {
            final boolean b2 = !this.delegatedActions.isEmpty();
            if (b2 || (b != SSLHandshake.FINISHED.id && b != SSLHandshake.KEY_UPDATE.id && b != SSLHandshake.NEW_SESSION_TICKET.id)) {
                if (!b2) {
                    this.taskDelegated = false;
                    this.delegatedThrown = null;
                }
                final ByteBuffer wrap = ByteBuffer.wrap(new byte[plaintext.fragment.remaining()]);
                wrap.put(plaintext.fragment);
                this.delegatedActions.add(new AbstractMap.SimpleImmutableEntry<Byte, ByteBuffer>(b, wrap.rewind()));
            }
            else {
                this.dispatch(b, plaintext.fragment);
            }
        }
        else {
            this.dispatch(b, plaintext.fragment);
        }
    }
    
    void dispatch(final byte b, final ByteBuffer byteBuffer) throws IOException {
        SSLConsumer hello_REQUEST;
        if (b == SSLHandshake.HELLO_REQUEST.id) {
            hello_REQUEST = SSLHandshake.HELLO_REQUEST;
        }
        else {
            hello_REQUEST = this.handshakeConsumers.get(b);
        }
        if (hello_REQUEST == null) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unexpected handshake message: " + SSLHandshake.nameOf(b));
        }
        try {
            hello_REQUEST.consume(this, byteBuffer);
        }
        catch (final UnsupportedOperationException ex) {
            throw this.conContext.fatal(Alert.UNEXPECTED_MESSAGE, "Unsupported handshake message: " + SSLHandshake.nameOf(b), ex);
        }
        catch (final BufferUnderflowException | BufferOverflowException ex2) {
            throw this.conContext.fatal(Alert.DECODE_ERROR, "Illegal handshake message: " + SSLHandshake.nameOf(b), (Throwable)ex2);
        }
        this.handshakeHash.consume();
    }
    
    abstract void kickstart() throws IOException;
    
    boolean isNegotiable(final CipherSuite cipherSuite) {
        return isNegotiable(this.activeCipherSuites, cipherSuite);
    }
    
    static final boolean isNegotiable(final List<CipherSuite> list, final CipherSuite cipherSuite) {
        return list.contains(cipherSuite) && cipherSuite.isNegotiable();
    }
    
    static final boolean isNegotiable(final List<CipherSuite> list, final ProtocolVersion protocolVersion, final CipherSuite cipherSuite) {
        return list.contains(cipherSuite) && cipherSuite.isNegotiable() && cipherSuite.supports(protocolVersion);
    }
    
    boolean isNegotiable(final ProtocolVersion protocolVersion) {
        return this.activeProtocols.contains(protocolVersion);
    }
    
    void setVersion(final ProtocolVersion protocolVersion) {
        this.conContext.protocolVersion = protocolVersion;
    }
    
    private static boolean isActivatable(final CipherSuite cipherSuite, final AlgorithmConstraints algorithmConstraints, final Map<SupportedGroupsExtension.NamedGroupType, Boolean> map) {
        if (!algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), cipherSuite.name, null)) {
            if (SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                SSLLogger.fine("Ignore disabled cipher suite: " + cipherSuite, new Object[0]);
            }
            return false;
        }
        if (cipherSuite.keyExchange == null) {
            return true;
        }
        final SupportedGroupsExtension.NamedGroupType groupType = cipherSuite.keyExchange.groupType;
        if (groupType != SupportedGroupsExtension.NamedGroupType.NAMED_GROUP_NONE) {
            final Boolean b = map.get(groupType);
            boolean b2;
            if (b == null) {
                b2 = SupportedGroupsExtension.SupportedGroups.isActivatable(algorithmConstraints, groupType);
                map.put(groupType, b2);
                if (!b2 && SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                    SSLLogger.fine("No activated named group", new Object[0]);
                }
            }
            else {
                b2 = b;
            }
            if (!b2 && SSLLogger.isOn && SSLLogger.isOn("verbose")) {
                SSLLogger.fine("No active named group, ignore " + cipherSuite, new Object[0]);
            }
            return b2;
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
