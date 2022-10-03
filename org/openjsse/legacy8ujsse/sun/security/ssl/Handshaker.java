package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.security.PrivilegedActionException;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLKeyException;
import java.io.OutputStream;
import sun.security.internal.spec.TlsKeyMaterialSpec;
import sun.security.internal.spec.TlsKeyMaterialParameterSpec;
import java.security.GeneralSecurityException;
import javax.crypto.KeyGenerator;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import sun.security.internal.spec.TlsMasterSecretParameterSpec;
import java.security.DigestException;
import java.security.ProviderException;
import sun.misc.HexDumpEncoder;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLProtocolException;
import java.security.PrivilegedExceptionAction;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.security.AlgorithmParameters;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import javax.net.ssl.SSLHandshakeException;
import java.util.ArrayList;
import javax.net.ssl.SSLParameters;
import java.security.AccessControlContext;
import java.io.IOException;
import java.util.Collections;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLEngine;
import java.util.function.BiFunction;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import java.util.List;
import java.util.Collection;
import java.security.AlgorithmConstraints;

abstract class Handshaker
{
    ProtocolVersion protocolVersion;
    ProtocolVersion activeProtocolVersion;
    boolean secureRenegotiation;
    byte[] clientVerifyData;
    byte[] serverVerifyData;
    boolean isInitialHandshake;
    private ProtocolList enabledProtocols;
    private CipherSuiteList enabledCipherSuites;
    String identificationProtocol;
    AlgorithmConstraints algorithmConstraints;
    private Collection<SignatureAndHashAlgorithm> localSupportedSignAlgs;
    Collection<SignatureAndHashAlgorithm> peerSupportedSignAlgs;
    private ProtocolList activeProtocols;
    private CipherSuiteList activeCipherSuites;
    List<SNIServerName> serverNames;
    Collection<SNIMatcher> sniMatchers;
    String[] localApl;
    String applicationProtocol;
    BiFunction<SSLEngine, List<String>, String> appProtocolSelectorSSLEngine;
    BiFunction<SSLSocket, List<String>, String> appProtocolSelectorSSLSocket;
    private boolean isClient;
    private boolean needCertVerify;
    SSLSocketImpl conn;
    SSLEngineImpl engine;
    HandshakeHash handshakeHash;
    HandshakeInStream input;
    HandshakeOutStream output;
    SSLContextImpl sslContext;
    RandomCookie clnt_random;
    RandomCookie svr_random;
    SSLSessionImpl session;
    HandshakeStateManager handshakeState;
    boolean clientHelloDelivered;
    boolean serverHelloRequested;
    boolean handshakeActivated;
    boolean handshakeFinished;
    CipherSuite cipherSuite;
    CipherSuite.KeyExchange keyExchange;
    boolean resumingSession;
    boolean enableNewSession;
    boolean preferLocalCipherSuites;
    private SecretKey clntWriteKey;
    private SecretKey svrWriteKey;
    private IvParameterSpec clntWriteIV;
    private IvParameterSpec svrWriteIV;
    private SecretKey clntMacSecret;
    private SecretKey svrMacSecret;
    private volatile boolean taskDelegated;
    private volatile DelegatedTask<?> delegatedTask;
    private volatile Exception thrown;
    private Object thrownLock;
    static final Debug debug;
    static final boolean allowUnsafeRenegotiation;
    static final boolean allowLegacyHelloMessages;
    static final boolean rejectClientInitiatedRenego;
    static final boolean useExtendedMasterSecret;
    static final boolean allowLegacyResumption;
    static final boolean allowLegacyMasterSecret;
    static final int maxHandshakeMessageSize;
    boolean requestedToUseEMS;
    boolean invalidated;
    
    Handshaker(final SSLSocketImpl c, final SSLContextImpl context, final ProtocolList enabledProtocols, final boolean needCertVerify, final boolean isClient, final ProtocolVersion activeProtocolVersion, final boolean isInitialHandshake, final boolean secureRenegotiation, final byte[] clientVerifyData, final byte[] serverVerifyData) {
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.localApl = null;
        this.applicationProtocol = null;
        this.appProtocolSelectorSSLEngine = null;
        this.appProtocolSelectorSSLSocket = null;
        this.conn = null;
        this.engine = null;
        this.preferLocalCipherSuites = false;
        this.taskDelegated = false;
        this.delegatedTask = null;
        this.thrown = null;
        this.thrownLock = new Object();
        this.requestedToUseEMS = false;
        this.conn = c;
        this.init(context, enabledProtocols, needCertVerify, isClient, activeProtocolVersion, isInitialHandshake, secureRenegotiation, clientVerifyData, serverVerifyData);
    }
    
    Handshaker(final SSLEngineImpl engine, final SSLContextImpl context, final ProtocolList enabledProtocols, final boolean needCertVerify, final boolean isClient, final ProtocolVersion activeProtocolVersion, final boolean isInitialHandshake, final boolean secureRenegotiation, final byte[] clientVerifyData, final byte[] serverVerifyData) {
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.localApl = null;
        this.applicationProtocol = null;
        this.appProtocolSelectorSSLEngine = null;
        this.appProtocolSelectorSSLSocket = null;
        this.conn = null;
        this.engine = null;
        this.preferLocalCipherSuites = false;
        this.taskDelegated = false;
        this.delegatedTask = null;
        this.thrown = null;
        this.thrownLock = new Object();
        this.requestedToUseEMS = false;
        this.engine = engine;
        this.init(context, enabledProtocols, needCertVerify, isClient, activeProtocolVersion, isInitialHandshake, secureRenegotiation, clientVerifyData, serverVerifyData);
    }
    
    private void init(final SSLContextImpl context, final ProtocolList enabledProtocols, final boolean needCertVerify, final boolean isClient, final ProtocolVersion activeProtocolVersion, final boolean isInitialHandshake, final boolean secureRenegotiation, final byte[] clientVerifyData, final byte[] serverVerifyData) {
        if (Handshaker.debug != null && Debug.isOn("handshake")) {
            System.out.println("Allow unsafe renegotiation: " + Handshaker.allowUnsafeRenegotiation + "\nAllow legacy hello messages: " + Handshaker.allowLegacyHelloMessages + "\nIs initial handshake: " + isInitialHandshake + "\nIs secure renegotiation: " + secureRenegotiation);
        }
        this.sslContext = context;
        this.isClient = isClient;
        this.needCertVerify = needCertVerify;
        this.activeProtocolVersion = activeProtocolVersion;
        this.isInitialHandshake = isInitialHandshake;
        this.secureRenegotiation = secureRenegotiation;
        this.clientVerifyData = clientVerifyData;
        this.serverVerifyData = serverVerifyData;
        this.enableNewSession = true;
        this.invalidated = false;
        this.handshakeState = new HandshakeStateManager();
        this.clientHelloDelivered = false;
        this.serverHelloRequested = false;
        this.handshakeActivated = false;
        this.handshakeFinished = false;
        this.setCipherSuite(CipherSuite.C_NULL);
        this.setEnabledProtocols(enabledProtocols);
        if (this.conn != null) {
            this.algorithmConstraints = new SSLAlgorithmConstraints(this.conn, true);
        }
        else {
            this.algorithmConstraints = new SSLAlgorithmConstraints(this.engine, true);
        }
    }
    
    void fatalSE(final byte b, final String diagnostic) throws IOException {
        this.fatalSE(b, diagnostic, null);
    }
    
    void fatalSE(final byte b, final Throwable cause) throws IOException {
        this.fatalSE(b, null, cause);
    }
    
    void fatalSE(final byte b, final String diagnostic, final Throwable cause) throws IOException {
        if (this.conn != null) {
            this.conn.fatal(b, diagnostic, cause);
        }
        else {
            this.engine.fatal(b, diagnostic, cause);
        }
    }
    
    void warningSE(final byte b) {
        if (this.conn != null) {
            this.conn.warning(b);
        }
        else {
            this.engine.warning(b);
        }
    }
    
    String getHostSE() {
        if (this.conn != null) {
            return this.conn.getHost();
        }
        return this.engine.getPeerHost();
    }
    
    String getHostAddressSE() {
        if (this.conn != null) {
            return this.conn.getInetAddress().getHostAddress();
        }
        return this.engine.getPeerHost();
    }
    
    int getPortSE() {
        if (this.conn != null) {
            return this.conn.getPort();
        }
        return this.engine.getPeerPort();
    }
    
    int getLocalPortSE() {
        if (this.conn != null) {
            return this.conn.getLocalPort();
        }
        return -1;
    }
    
    AccessControlContext getAccSE() {
        if (this.conn != null) {
            return this.conn.getAcc();
        }
        return this.engine.getAcc();
    }
    
    String getEndpointIdentificationAlgorithmSE() {
        SSLParameters paras;
        if (this.conn != null) {
            paras = this.conn.getSSLParameters();
        }
        else {
            paras = this.engine.getSSLParameters();
        }
        return paras.getEndpointIdentificationAlgorithm();
    }
    
    private void setVersionSE(final ProtocolVersion protocolVersion) {
        if (this.conn != null) {
            this.conn.setVersion(protocolVersion);
        }
        else {
            this.engine.setVersion(protocolVersion);
        }
    }
    
    void setVersion(final ProtocolVersion protocolVersion) {
        this.setVersionSE(this.protocolVersion = protocolVersion);
        this.output.r.setVersion(protocolVersion);
    }
    
    void setEnabledProtocols(final ProtocolList enabledProtocols) {
        this.activeCipherSuites = null;
        this.activeProtocols = null;
        this.enabledProtocols = enabledProtocols;
    }
    
    void setEnabledCipherSuites(final CipherSuiteList enabledCipherSuites) {
        this.activeCipherSuites = null;
        this.activeProtocols = null;
        this.enabledCipherSuites = enabledCipherSuites;
    }
    
    void setAlgorithmConstraints(final AlgorithmConstraints algorithmConstraints) {
        this.activeCipherSuites = null;
        this.activeProtocols = null;
        this.algorithmConstraints = new SSLAlgorithmConstraints(algorithmConstraints);
        this.localSupportedSignAlgs = null;
    }
    
    Collection<SignatureAndHashAlgorithm> getLocalSupportedSignAlgs() {
        if (this.localSupportedSignAlgs == null) {
            this.localSupportedSignAlgs = SignatureAndHashAlgorithm.getSupportedAlgorithms(this.algorithmConstraints);
        }
        return this.localSupportedSignAlgs;
    }
    
    void setPeerSupportedSignAlgs(final Collection<SignatureAndHashAlgorithm> algorithms) {
        this.peerSupportedSignAlgs = new ArrayList<SignatureAndHashAlgorithm>(algorithms);
    }
    
    Collection<SignatureAndHashAlgorithm> getPeerSupportedSignAlgs() {
        return this.peerSupportedSignAlgs;
    }
    
    void setIdentificationProtocol(final String protocol) {
        this.identificationProtocol = protocol;
    }
    
    void setSNIServerNames(final List<SNIServerName> serverNames) {
        this.serverNames = serverNames;
    }
    
    void setSNIMatchers(final Collection<SNIMatcher> sniMatchers) {
        this.sniMatchers = sniMatchers;
    }
    
    void setApplicationProtocols(final String[] apl) {
        this.localApl = apl;
    }
    
    String getHandshakeApplicationProtocol() {
        return this.applicationProtocol;
    }
    
    void setApplicationProtocolSelectorSSLEngine(final BiFunction<SSLEngine, List<String>, String> selector) {
        this.appProtocolSelectorSSLEngine = selector;
    }
    
    void setApplicationProtocolSelectorSSLSocket(final BiFunction<SSLSocket, List<String>, String> selector) {
        this.appProtocolSelectorSSLSocket = selector;
    }
    
    void setUseCipherSuitesOrder(final boolean on) {
        this.preferLocalCipherSuites = on;
    }
    
    void activate(ProtocolVersion helloVersion) throws IOException {
        if (this.activeProtocols == null) {
            this.activeProtocols = this.getActiveProtocols();
        }
        if (this.activeProtocols.collection().isEmpty() || this.activeProtocols.max.v == ProtocolVersion.NONE.v) {
            throw new SSLHandshakeException("No appropriate protocol (protocol is disabled or cipher suites are inappropriate)");
        }
        if (this.activeCipherSuites == null) {
            this.activeCipherSuites = this.getActiveCipherSuites();
        }
        if (this.activeCipherSuites.collection().isEmpty()) {
            throw new SSLHandshakeException("No appropriate cipher suite");
        }
        if (!this.isInitialHandshake) {
            this.protocolVersion = this.activeProtocolVersion;
        }
        else {
            this.protocolVersion = this.activeProtocols.max;
        }
        if (helloVersion == null || helloVersion.v == ProtocolVersion.NONE.v) {
            helloVersion = this.activeProtocols.helloVersion;
        }
        this.handshakeHash = new HandshakeHash(this.needCertVerify);
        this.input = new HandshakeInStream(this.handshakeHash);
        if (this.conn != null) {
            this.output = new HandshakeOutStream(this.protocolVersion, helloVersion, this.handshakeHash, this.conn);
            this.conn.getAppInputStream().r.setHandshakeHash(this.handshakeHash);
            this.conn.getAppInputStream().r.setHelloVersion(helloVersion);
            this.conn.getAppOutputStream().r.setHelloVersion(helloVersion);
        }
        else {
            this.output = new HandshakeOutStream(this.protocolVersion, helloVersion, this.handshakeHash, this.engine);
            this.engine.inputRecord.setHandshakeHash(this.handshakeHash);
            this.engine.inputRecord.setHelloVersion(helloVersion);
            this.engine.outputRecord.setHelloVersion(helloVersion);
        }
        this.handshakeActivated = true;
    }
    
    void setCipherSuite(final CipherSuite s) {
        this.cipherSuite = s;
        this.keyExchange = s.keyExchange;
    }
    
    boolean isNegotiable(final CipherSuite s) {
        if (this.activeCipherSuites == null) {
            this.activeCipherSuites = this.getActiveCipherSuites();
        }
        return isNegotiable(this.activeCipherSuites, s);
    }
    
    static final boolean isNegotiable(final CipherSuiteList proposed, final CipherSuite s) {
        return proposed.contains(s) && s.isNegotiable();
    }
    
    boolean isNegotiable(final ProtocolVersion protocolVersion) {
        if (this.activeProtocols == null) {
            this.activeProtocols = this.getActiveProtocols();
        }
        return this.activeProtocols.contains(protocolVersion);
    }
    
    ProtocolVersion selectProtocolVersion(final ProtocolVersion protocolVersion) {
        if (this.activeProtocols == null) {
            this.activeProtocols = this.getActiveProtocols();
        }
        return this.activeProtocols.selectProtocolVersion(protocolVersion);
    }
    
    CipherSuiteList getActiveCipherSuites() {
        if (this.activeCipherSuites == null) {
            if (this.activeProtocols == null) {
                this.activeProtocols = this.getActiveProtocols();
            }
            final ArrayList<CipherSuite> suites = new ArrayList<CipherSuite>();
            if (!this.activeProtocols.collection().isEmpty() && this.activeProtocols.min.v != ProtocolVersion.NONE.v) {
                boolean checkedCurves = false;
                boolean hasCurves = false;
                for (final CipherSuite suite : this.enabledCipherSuites.collection()) {
                    if (suite.obsoleted > this.activeProtocols.min.v && suite.supported <= this.activeProtocols.max.v) {
                        if (!this.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), suite.name, null)) {
                            continue;
                        }
                        boolean available = true;
                        if (suite.keyExchange.isEC) {
                            if (!checkedCurves) {
                                hasCurves = EllipticCurvesExtension.hasActiveCurves(this.algorithmConstraints);
                                checkedCurves = true;
                                if (!hasCurves && Handshaker.debug != null && Debug.isOn("verbose")) {
                                    System.out.println("No available elliptic curves");
                                }
                            }
                            available = hasCurves;
                            if (!available && Handshaker.debug != null && Debug.isOn("verbose")) {
                                System.out.println("No active elliptic curves, ignore " + suite);
                            }
                        }
                        if (!available) {
                            continue;
                        }
                        suites.add(suite);
                    }
                    else {
                        if (Handshaker.debug == null || !Debug.isOn("verbose")) {
                            continue;
                        }
                        if (suite.obsoleted <= this.activeProtocols.min.v) {
                            System.out.println("Ignoring obsoleted cipher suite: " + suite);
                        }
                        else {
                            System.out.println("Ignoring unsupported cipher suite: " + suite);
                        }
                    }
                }
            }
            this.activeCipherSuites = new CipherSuiteList(suites);
        }
        return this.activeCipherSuites;
    }
    
    ProtocolList getActiveProtocols() {
        if (this.activeProtocols == null) {
            boolean enabledSSL20Hello = false;
            boolean checkedCurves = false;
            boolean hasCurves = false;
            final ArrayList<ProtocolVersion> protocols = new ArrayList<ProtocolVersion>(4);
            for (final ProtocolVersion protocol : this.enabledProtocols.collection()) {
                if (protocol.v == ProtocolVersion.SSL20Hello.v) {
                    enabledSSL20Hello = true;
                }
                else if (!this.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), protocol.name, null)) {
                    if (Handshaker.debug == null || !Debug.isOn("verbose")) {
                        continue;
                    }
                    System.out.println("Ignoring disabled protocol: " + protocol);
                }
                else {
                    boolean found = false;
                    for (final CipherSuite suite : this.enabledCipherSuites.collection()) {
                        if (suite.isAvailable() && suite.obsoleted > protocol.v && suite.supported <= protocol.v) {
                            if (this.algorithmConstraints.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), suite.name, null)) {
                                boolean available = true;
                                if (suite.keyExchange.isEC) {
                                    if (!checkedCurves) {
                                        hasCurves = EllipticCurvesExtension.hasActiveCurves(this.algorithmConstraints);
                                        checkedCurves = true;
                                        if (!hasCurves && Handshaker.debug != null && Debug.isOn("verbose")) {
                                            System.out.println("No activated elliptic curves");
                                        }
                                    }
                                    available = hasCurves;
                                    if (!available && Handshaker.debug != null && Debug.isOn("verbose")) {
                                        System.out.println("No active elliptic curves, ignore " + suite + " for " + protocol);
                                    }
                                }
                                if (available) {
                                    protocols.add(protocol);
                                    found = true;
                                    break;
                                }
                                continue;
                            }
                            else {
                                if (Handshaker.debug == null || !Debug.isOn("verbose")) {
                                    continue;
                                }
                                System.out.println("Ignoring disabled cipher suite: " + suite + " for " + protocol);
                            }
                        }
                        else {
                            if (Handshaker.debug == null || !Debug.isOn("verbose")) {
                                continue;
                            }
                            System.out.println("Ignoring unsupported cipher suite: " + suite + " for " + protocol);
                        }
                    }
                    if (found || Handshaker.debug == null || !Debug.isOn("handshake")) {
                        continue;
                    }
                    System.out.println("No available cipher suite for " + protocol);
                }
            }
            if (!protocols.isEmpty() && enabledSSL20Hello) {
                protocols.add(ProtocolVersion.SSL20Hello);
            }
            this.activeProtocols = new ProtocolList(protocols);
        }
        return this.activeProtocols;
    }
    
    void setEnableSessionCreation(final boolean newSessions) {
        this.enableNewSession = newSessions;
    }
    
    CipherBox newReadCipher() throws NoSuchAlgorithmException {
        final CipherSuite.BulkCipher cipher = this.cipherSuite.cipher;
        CipherBox box;
        if (this.isClient) {
            box = cipher.newCipher(this.protocolVersion, this.svrWriteKey, this.svrWriteIV, this.sslContext.getSecureRandom(), false);
            this.svrWriteKey = null;
            this.svrWriteIV = null;
        }
        else {
            box = cipher.newCipher(this.protocolVersion, this.clntWriteKey, this.clntWriteIV, this.sslContext.getSecureRandom(), false);
            this.clntWriteKey = null;
            this.clntWriteIV = null;
        }
        return box;
    }
    
    CipherBox newWriteCipher() throws NoSuchAlgorithmException {
        final CipherSuite.BulkCipher cipher = this.cipherSuite.cipher;
        CipherBox box;
        if (this.isClient) {
            box = cipher.newCipher(this.protocolVersion, this.clntWriteKey, this.clntWriteIV, this.sslContext.getSecureRandom(), true);
            this.clntWriteKey = null;
            this.clntWriteIV = null;
        }
        else {
            box = cipher.newCipher(this.protocolVersion, this.svrWriteKey, this.svrWriteIV, this.sslContext.getSecureRandom(), true);
            this.svrWriteKey = null;
            this.svrWriteIV = null;
        }
        return box;
    }
    
    Authenticator newReadAuthenticator() throws NoSuchAlgorithmException, InvalidKeyException {
        Authenticator authenticator = null;
        if (this.cipherSuite.cipher.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
            authenticator = new Authenticator(this.protocolVersion);
        }
        else {
            final CipherSuite.MacAlg macAlg = this.cipherSuite.macAlg;
            if (this.isClient) {
                authenticator = macAlg.newMac(this.protocolVersion, this.svrMacSecret);
                this.svrMacSecret = null;
            }
            else {
                authenticator = macAlg.newMac(this.protocolVersion, this.clntMacSecret);
                this.clntMacSecret = null;
            }
        }
        return authenticator;
    }
    
    Authenticator newWriteAuthenticator() throws NoSuchAlgorithmException, InvalidKeyException {
        Authenticator authenticator = null;
        if (this.cipherSuite.cipher.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
            authenticator = new Authenticator(this.protocolVersion);
        }
        else {
            final CipherSuite.MacAlg macAlg = this.cipherSuite.macAlg;
            if (this.isClient) {
                authenticator = macAlg.newMac(this.protocolVersion, this.clntMacSecret);
                this.clntMacSecret = null;
            }
            else {
                authenticator = macAlg.newMac(this.protocolVersion, this.svrMacSecret);
                this.svrMacSecret = null;
            }
        }
        return authenticator;
    }
    
    boolean isDone() {
        return this.started() && this.handshakeState.isEmpty() && this.handshakeFinished;
    }
    
    SSLSessionImpl getSession() {
        return this.session;
    }
    
    void setHandshakeSessionSE(final SSLSessionImpl handshakeSession) {
        if (this.conn != null) {
            this.conn.setHandshakeSession(handshakeSession);
        }
        else {
            this.engine.setHandshakeSession(handshakeSession);
        }
    }
    
    boolean isSecureRenegotiation() {
        return this.secureRenegotiation;
    }
    
    byte[] getClientVerifyData() {
        return this.clientVerifyData;
    }
    
    byte[] getServerVerifyData() {
        return this.serverVerifyData;
    }
    
    void process_record(final InputRecord r, final boolean expectingFinished) throws IOException {
        this.checkThrown();
        this.input.incomingRecord(r);
        if (this.conn != null || expectingFinished) {
            this.processLoop();
        }
        else {
            this.delegateTask((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    Handshaker.this.processLoop();
                    return null;
                }
            });
        }
    }
    
    void processLoop() throws IOException {
        while (this.input.available() >= 4) {
            this.input.mark(4);
            final byte messageType = (byte)this.input.getInt8();
            final int messageLen = this.input.getInt24();
            if (messageLen > Handshaker.maxHandshakeMessageSize) {
                throw new SSLProtocolException("The size of the handshake message (" + messageLen + ") exceeds the maximum allowed size (" + Handshaker.maxHandshakeMessageSize + ")");
            }
            if (this.input.available() < messageLen) {
                this.input.reset();
                return;
            }
            if (messageType == 1) {
                this.clientHelloDelivered = true;
            }
            else if (messageType == 0) {
                this.serverHelloRequested = true;
            }
            if (messageType == 0) {
                this.input.reset();
                this.processMessage(messageType, messageLen);
                this.input.ignore(4 + messageLen);
            }
            else {
                this.input.mark(messageLen);
                this.processMessage(messageType, messageLen);
                this.input.digestNow();
            }
        }
    }
    
    boolean activated() {
        return this.handshakeActivated;
    }
    
    boolean started() {
        return this.serverHelloRequested || this.clientHelloDelivered;
    }
    
    void kickstart() throws IOException {
        if ((this.isClient && this.clientHelloDelivered) || (!this.isClient && this.serverHelloRequested)) {
            return;
        }
        final HandshakeMessage m = this.getKickstartMessage();
        this.handshakeState.update(m, this.resumingSession);
        if (Handshaker.debug != null && Debug.isOn("handshake")) {
            m.print(System.out);
        }
        m.write(this.output);
        this.output.flush();
        final int handshakeType = m.messageType();
        if (handshakeType == 0) {
            this.serverHelloRequested = true;
        }
        else {
            this.clientHelloDelivered = true;
        }
    }
    
    abstract HandshakeMessage getKickstartMessage() throws SSLException;
    
    abstract void processMessage(final byte p0, final int p1) throws IOException;
    
    abstract void handshakeAlert(final byte p0) throws SSLProtocolException;
    
    void sendChangeCipherSpec(final HandshakeMessage.Finished mesg, final boolean lastMessage) throws IOException {
        this.output.flush();
        OutputRecord r;
        if (this.conn != null) {
            r = new OutputRecord((byte)20);
        }
        else {
            r = new EngineOutputRecord((byte)20, this.engine);
        }
        r.setVersion(this.protocolVersion);
        r.write(1);
        if (this.conn != null) {
            this.conn.writeLock.lock();
            try {
                this.handshakeState.changeCipherSpec(false, this.isClient);
                this.conn.writeRecord(r);
                this.conn.changeWriteCiphers();
                if (Handshaker.debug != null && Debug.isOn("handshake")) {
                    mesg.print(System.out);
                }
                this.handshakeState.update(mesg, this.resumingSession);
                mesg.write(this.output);
                this.output.flush();
            }
            finally {
                this.conn.writeLock.unlock();
            }
        }
        else {
            synchronized (this.engine.writeLock) {
                this.handshakeState.changeCipherSpec(false, this.isClient);
                this.engine.writeRecord((EngineOutputRecord)r);
                this.engine.changeWriteCiphers();
                if (Handshaker.debug != null && Debug.isOn("handshake")) {
                    mesg.print(System.out);
                }
                this.handshakeState.update(mesg, this.resumingSession);
                mesg.write(this.output);
                if (lastMessage) {
                    this.output.setFinishedMsg();
                }
                this.output.flush();
            }
        }
        if (lastMessage) {
            this.handshakeFinished = true;
        }
    }
    
    void receiveChangeCipherSpec() throws IOException {
        this.handshakeState.changeCipherSpec(true, this.isClient);
    }
    
    void calculateKeys(final SecretKey preMasterSecret, final ProtocolVersion version) {
        final SecretKey master = this.calculateMasterSecret(preMasterSecret, version);
        this.session.setMasterSecret(master);
        this.calculateConnectionKeys(master);
    }
    
    private SecretKey calculateMasterSecret(final SecretKey preMasterSecret, final ProtocolVersion requestedVersion) {
        if (Handshaker.debug != null && Debug.isOn("keygen")) {
            final HexDumpEncoder dump = new HexDumpEncoder();
            System.out.println("SESSION KEYGEN:");
            System.out.println("PreMaster Secret:");
            printHex(dump, preMasterSecret.getEncoded());
        }
        String masterAlg;
        CipherSuite.PRF prf;
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
            masterAlg = "SunTls12MasterSecret";
            prf = this.cipherSuite.prfAlg;
        }
        else {
            masterAlg = "SunTlsMasterSecret";
            prf = CipherSuite.PRF.P_NONE;
        }
        final String prfHashAlg = prf.getPRFHashAlg();
        final int prfHashLength = prf.getPRFHashLength();
        final int prfBlockSize = prf.getPRFBlockSize();
        TlsMasterSecretParameterSpec spec;
        if (this.session.getUseExtendedMasterSecret()) {
            masterAlg = "SunTlsExtendedMasterSecret";
            byte[] sessionHash = null;
            if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
                sessionHash = this.handshakeHash.getFinishedHash();
            }
            else {
                sessionHash = new byte[36];
                try {
                    this.handshakeHash.getMD5Clone().digest(sessionHash, 0, 16);
                    this.handshakeHash.getSHAClone().digest(sessionHash, 16, 20);
                }
                catch (final DigestException de) {
                    throw new ProviderException(de);
                }
            }
            spec = new TlsMasterSecretParameterSpec(preMasterSecret, (int)this.protocolVersion.major, (int)this.protocolVersion.minor, sessionHash, prfHashAlg, prfHashLength, prfBlockSize);
        }
        else {
            spec = new TlsMasterSecretParameterSpec(preMasterSecret, this.protocolVersion.major, this.protocolVersion.minor, this.clnt_random.random_bytes, this.svr_random.random_bytes, prfHashAlg, prfHashLength, prfBlockSize);
        }
        try {
            final KeyGenerator kg = JsseJce.getKeyGenerator(masterAlg);
            kg.init(spec);
            return kg.generateKey();
        }
        catch (final InvalidAlgorithmParameterException | NoSuchAlgorithmException iae) {
            if (Handshaker.debug != null && Debug.isOn("handshake")) {
                System.out.println("RSA master secret generation error:");
                iae.printStackTrace(System.out);
            }
            throw new ProviderException(iae);
        }
    }
    
    void calculateConnectionKeys(final SecretKey masterKey) {
        final int hashSize = this.cipherSuite.macAlg.size;
        final boolean is_exportable = this.cipherSuite.exportable;
        final CipherSuite.BulkCipher cipher = this.cipherSuite.cipher;
        final int expandedKeySize = is_exportable ? cipher.expandedKeySize : 0;
        String keyMaterialAlg;
        CipherSuite.PRF prf;
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
            keyMaterialAlg = "SunTls12KeyMaterial";
            prf = this.cipherSuite.prfAlg;
        }
        else {
            keyMaterialAlg = "SunTlsKeyMaterial";
            prf = CipherSuite.PRF.P_NONE;
        }
        final String prfHashAlg = prf.getPRFHashAlg();
        final int prfHashLength = prf.getPRFHashLength();
        final int prfBlockSize = prf.getPRFBlockSize();
        int ivSize = cipher.ivSize;
        if (cipher.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
            ivSize = cipher.fixedIvSize;
        }
        else if (this.protocolVersion.v >= ProtocolVersion.TLS11.v && cipher.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
            ivSize = 0;
        }
        final TlsKeyMaterialParameterSpec spec = new TlsKeyMaterialParameterSpec(masterKey, this.protocolVersion.major, this.protocolVersion.minor, this.clnt_random.random_bytes, this.svr_random.random_bytes, cipher.algorithm, cipher.keySize, expandedKeySize, ivSize, hashSize, prfHashAlg, prfHashLength, prfBlockSize);
        try {
            final KeyGenerator kg = JsseJce.getKeyGenerator(keyMaterialAlg);
            kg.init(spec);
            final TlsKeyMaterialSpec keySpec = (TlsKeyMaterialSpec)kg.generateKey();
            this.clntWriteKey = keySpec.getClientCipherKey();
            this.svrWriteKey = keySpec.getServerCipherKey();
            this.clntWriteIV = keySpec.getClientIv();
            this.svrWriteIV = keySpec.getServerIv();
            this.clntMacSecret = keySpec.getClientMacKey();
            this.svrMacSecret = keySpec.getServerMacKey();
        }
        catch (final GeneralSecurityException e) {
            throw new ProviderException(e);
        }
        if (Handshaker.debug != null && Debug.isOn("keygen")) {
            synchronized (System.out) {
                final HexDumpEncoder dump = new HexDumpEncoder();
                System.out.println("CONNECTION KEYGEN:");
                System.out.println("Client Nonce:");
                printHex(dump, this.clnt_random.random_bytes);
                System.out.println("Server Nonce:");
                printHex(dump, this.svr_random.random_bytes);
                System.out.println("Master Secret:");
                printHex(dump, masterKey.getEncoded());
                if (this.clntMacSecret != null) {
                    System.out.println("Client MAC write Secret:");
                    printHex(dump, this.clntMacSecret.getEncoded());
                    System.out.println("Server MAC write Secret:");
                    printHex(dump, this.svrMacSecret.getEncoded());
                }
                else {
                    System.out.println("... no MAC keys used for this cipher");
                }
                if (this.clntWriteKey != null) {
                    System.out.println("Client write key:");
                    printHex(dump, this.clntWriteKey.getEncoded());
                    System.out.println("Server write key:");
                    printHex(dump, this.svrWriteKey.getEncoded());
                }
                else {
                    System.out.println("... no encryption keys used");
                }
                if (this.clntWriteIV != null) {
                    System.out.println("Client write IV:");
                    printHex(dump, this.clntWriteIV.getIV());
                    System.out.println("Server write IV:");
                    printHex(dump, this.svrWriteIV.getIV());
                }
                else if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
                    System.out.println("... no IV derived for this protocol");
                }
                else {
                    System.out.println("... no IV used for this cipher");
                }
                System.out.flush();
            }
        }
    }
    
    private static void printHex(final HexDumpEncoder dump, final byte[] bytes) {
        if (bytes == null) {
            System.out.println("(key bytes not available)");
        }
        else {
            try {
                dump.encodeBuffer(bytes, System.out);
            }
            catch (final IOException ex) {}
        }
    }
    
    static void throwSSLException(final String msg, final Throwable cause) throws SSLException {
        final SSLException e = new SSLException(msg);
        e.initCause(cause);
        throw e;
    }
    
    private <T> void delegateTask(final PrivilegedExceptionAction<T> pea) {
        this.delegatedTask = new DelegatedTask<Object>(pea);
        this.taskDelegated = false;
        this.thrown = null;
    }
    
    DelegatedTask<?> getTask() {
        if (!this.taskDelegated) {
            this.taskDelegated = true;
            return this.delegatedTask;
        }
        return null;
    }
    
    boolean taskOutstanding() {
        return this.delegatedTask != null;
    }
    
    void checkThrown() throws SSLException {
        synchronized (this.thrownLock) {
            if (this.thrown != null) {
                String msg = this.thrown.getMessage();
                if (msg == null) {
                    msg = "Delegated task threw Exception/Error";
                }
                final Exception e = this.thrown;
                this.thrown = null;
                if (e instanceof RuntimeException) {
                    throw new RuntimeException(msg, e);
                }
                if (e instanceof SSLHandshakeException) {
                    throw (SSLHandshakeException)new SSLHandshakeException(msg).initCause(e);
                }
                if (e instanceof SSLKeyException) {
                    throw (SSLKeyException)new SSLKeyException(msg).initCause(e);
                }
                if (e instanceof SSLPeerUnverifiedException) {
                    throw (SSLPeerUnverifiedException)new SSLPeerUnverifiedException(msg).initCause(e);
                }
                if (e instanceof SSLProtocolException) {
                    throw (SSLProtocolException)new SSLProtocolException(msg).initCause(e);
                }
                throw new SSLException(msg, e);
            }
        }
    }
    
    static {
        debug = Debug.getInstance("ssl");
        allowUnsafeRenegotiation = Debug.getBooleanProperty("sun.security.ssl.allowUnsafeRenegotiation", false);
        allowLegacyHelloMessages = Debug.getBooleanProperty("sun.security.ssl.allowLegacyHelloMessages", true);
        rejectClientInitiatedRenego = Debug.getBooleanProperty("jdk.tls.rejectClientInitiatedRenegotiation", false);
        allowLegacyResumption = Debug.getBooleanProperty("jdk.tls.allowLegacyResumption", true);
        allowLegacyMasterSecret = Debug.getBooleanProperty("jdk.tls.allowLegacyMasterSecret", true);
        maxHandshakeMessageSize = AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("jdk.tls.maxHandshakeMessageSize", 32768));
        boolean supportExtendedMasterSecret = true;
        try {
            JsseJce.getKeyGenerator("SunTlsExtendedMasterSecret");
        }
        catch (final NoSuchAlgorithmException nae) {
            supportExtendedMasterSecret = false;
        }
        if (supportExtendedMasterSecret) {
            useExtendedMasterSecret = Debug.getBooleanProperty("jdk.tls.useExtendedMasterSecret", true);
        }
        else {
            useExtendedMasterSecret = false;
        }
    }
    
    class DelegatedTask<E> implements Runnable
    {
        private PrivilegedExceptionAction<E> pea;
        
        DelegatedTask(final PrivilegedExceptionAction<E> pea) {
            this.pea = pea;
        }
        
        @Override
        public void run() {
            synchronized (Handshaker.this.engine) {
                try {
                    AccessController.doPrivileged(this.pea, Handshaker.this.engine.getAcc());
                }
                catch (final PrivilegedActionException pae) {
                    Handshaker.this.thrown = pae.getException();
                }
                catch (final RuntimeException rte) {
                    Handshaker.this.thrown = rte;
                }
                Handshaker.this.delegatedTask = null;
                Handshaker.this.taskDelegated = false;
            }
        }
    }
}
