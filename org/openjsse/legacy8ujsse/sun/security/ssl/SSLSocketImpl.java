package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Iterator;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLParameters;
import sun.misc.JavaNetAccess;
import sun.misc.SharedSecrets;
import java.security.GeneralSecurityException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.crypto.BadPaddingException;
import java.io.EOFException;
import javax.net.ssl.SSLHandshakeException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLProtocolException;
import java.security.AccessController;
import java.net.SocketException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import javax.net.ssl.HandshakeCompletedListener;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import javax.net.ssl.SSLSocket;
import java.util.function.BiFunction;
import javax.net.ssl.SNIMatcher;
import java.util.Collection;
import javax.net.ssl.SNIServerName;
import java.util.List;
import java.security.AlgorithmConstraints;
import java.security.AccessControlContext;
import javax.net.ssl.SSLException;

public final class SSLSocketImpl extends BaseSSLSocketImpl
{
    private static final int cs_START = 0;
    private static final int cs_HANDSHAKE = 1;
    private static final int cs_DATA = 2;
    private static final int cs_RENEGOTIATE = 3;
    private static final int cs_ERROR = 4;
    private static final int cs_SENT_CLOSE = 5;
    private static final int cs_CLOSED = 6;
    private static final int cs_APP_CLOSED = 7;
    private volatile int connectionState;
    private boolean expectingFinished;
    private SSLException closeReason;
    private byte doClientAuth;
    private boolean roleIsServer;
    private boolean enableSessionCreation;
    private String host;
    private boolean autoClose;
    private AccessControlContext acc;
    private CipherSuiteList enabledCipherSuites;
    private String identificationProtocol;
    private AlgorithmConstraints algorithmConstraints;
    List<SNIServerName> serverNames;
    Collection<SNIMatcher> sniMatchers;
    private boolean noSniExtension;
    private boolean noSniMatcher;
    String[] applicationProtocols;
    String applicationProtocol;
    BiFunction<SSLSocket, List<String>, String> applicationProtocolSelector;
    private final Object handshakeLock;
    final ReentrantLock writeLock;
    private final Object readLock;
    private InputRecord inrec;
    private Authenticator readAuthenticator;
    private Authenticator writeAuthenticator;
    private CipherBox readCipher;
    private CipherBox writeCipher;
    private boolean secureRenegotiation;
    private byte[] clientVerifyData;
    private byte[] serverVerifyData;
    private SSLContextImpl sslContext;
    private Handshaker handshaker;
    private SSLSessionImpl sess;
    private volatile SSLSessionImpl handshakeSession;
    private HashMap<HandshakeCompletedListener, AccessControlContext> handshakeListeners;
    private InputStream sockInput;
    private OutputStream sockOutput;
    private AppInputStream input;
    private AppOutputStream output;
    private ProtocolList enabledProtocols;
    private ProtocolVersion protocolVersion;
    private static final Debug debug;
    private boolean isFirstAppOutputRecord;
    private ByteArrayOutputStream heldRecordBuffer;
    private boolean preferLocalCipherSuites;
    static final boolean trustNameService;
    
    SSLSocketImpl(final SSLContextImpl context, final String host, final int port) throws IOException, UnknownHostException {
        this.enableSessionCreation = true;
        this.autoClose = true;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.noSniExtension = false;
        this.noSniMatcher = false;
        this.applicationProtocols = new String[0];
        this.applicationProtocol = null;
        this.handshakeLock = new Object();
        this.writeLock = new ReentrantLock();
        this.readLock = new Object();
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.isFirstAppOutputRecord = true;
        this.heldRecordBuffer = null;
        this.preferLocalCipherSuites = false;
        this.host = host;
        this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
        this.init(context, false);
        final SocketAddress socketAddress = (host != null) ? new InetSocketAddress(host, port) : new InetSocketAddress(InetAddress.getByName(null), port);
        this.connect(socketAddress, 0);
    }
    
    SSLSocketImpl(final SSLContextImpl context, final InetAddress host, final int port) throws IOException {
        this.enableSessionCreation = true;
        this.autoClose = true;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.noSniExtension = false;
        this.noSniMatcher = false;
        this.applicationProtocols = new String[0];
        this.applicationProtocol = null;
        this.handshakeLock = new Object();
        this.writeLock = new ReentrantLock();
        this.readLock = new Object();
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.isFirstAppOutputRecord = true;
        this.heldRecordBuffer = null;
        this.init(context, this.preferLocalCipherSuites = false);
        final SocketAddress socketAddress = new InetSocketAddress(host, port);
        this.connect(socketAddress, 0);
    }
    
    SSLSocketImpl(final SSLContextImpl context, final String host, final int port, final InetAddress localAddr, final int localPort) throws IOException, UnknownHostException {
        this.enableSessionCreation = true;
        this.autoClose = true;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.noSniExtension = false;
        this.noSniMatcher = false;
        this.applicationProtocols = new String[0];
        this.applicationProtocol = null;
        this.handshakeLock = new Object();
        this.writeLock = new ReentrantLock();
        this.readLock = new Object();
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.isFirstAppOutputRecord = true;
        this.heldRecordBuffer = null;
        this.preferLocalCipherSuites = false;
        this.host = host;
        this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
        this.init(context, false);
        this.bind(new InetSocketAddress(localAddr, localPort));
        final SocketAddress socketAddress = (host != null) ? new InetSocketAddress(host, port) : new InetSocketAddress(InetAddress.getByName(null), port);
        this.connect(socketAddress, 0);
    }
    
    SSLSocketImpl(final SSLContextImpl context, final InetAddress host, final int port, final InetAddress localAddr, final int localPort) throws IOException {
        this.enableSessionCreation = true;
        this.autoClose = true;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.noSniExtension = false;
        this.noSniMatcher = false;
        this.applicationProtocols = new String[0];
        this.applicationProtocol = null;
        this.handshakeLock = new Object();
        this.writeLock = new ReentrantLock();
        this.readLock = new Object();
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.isFirstAppOutputRecord = true;
        this.heldRecordBuffer = null;
        this.init(context, this.preferLocalCipherSuites = false);
        this.bind(new InetSocketAddress(localAddr, localPort));
        final SocketAddress socketAddress = new InetSocketAddress(host, port);
        this.connect(socketAddress, 0);
    }
    
    SSLSocketImpl(final SSLContextImpl context, final boolean serverMode, final CipherSuiteList suites, final byte clientAuth, final boolean sessionCreation, final ProtocolList protocols, final String identificationProtocol, final AlgorithmConstraints algorithmConstraints, final Collection<SNIMatcher> sniMatchers, final boolean preferLocalCipherSuites, final String[] applicationProtocols) throws IOException {
        this.enableSessionCreation = true;
        this.autoClose = true;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.noSniExtension = false;
        this.noSniMatcher = false;
        this.applicationProtocols = new String[0];
        this.applicationProtocol = null;
        this.handshakeLock = new Object();
        this.writeLock = new ReentrantLock();
        this.readLock = new Object();
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.isFirstAppOutputRecord = true;
        this.heldRecordBuffer = null;
        this.preferLocalCipherSuites = false;
        this.doClientAuth = clientAuth;
        this.enableSessionCreation = sessionCreation;
        this.identificationProtocol = identificationProtocol;
        this.algorithmConstraints = algorithmConstraints;
        this.sniMatchers = sniMatchers;
        this.preferLocalCipherSuites = preferLocalCipherSuites;
        this.applicationProtocols = applicationProtocols;
        this.init(context, serverMode);
        this.enabledCipherSuites = suites;
        this.enabledProtocols = protocols;
    }
    
    SSLSocketImpl(final SSLContextImpl context) {
        this.enableSessionCreation = true;
        this.autoClose = true;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.noSniExtension = false;
        this.noSniMatcher = false;
        this.applicationProtocols = new String[0];
        this.applicationProtocol = null;
        this.handshakeLock = new Object();
        this.writeLock = new ReentrantLock();
        this.readLock = new Object();
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.isFirstAppOutputRecord = true;
        this.heldRecordBuffer = null;
        this.init(context, this.preferLocalCipherSuites = false);
    }
    
    SSLSocketImpl(final SSLContextImpl context, final Socket sock, final String host, final int port, final boolean autoClose) throws IOException {
        super(sock);
        this.enableSessionCreation = true;
        this.autoClose = true;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.noSniExtension = false;
        this.noSniMatcher = false;
        this.applicationProtocols = new String[0];
        this.applicationProtocol = null;
        this.handshakeLock = new Object();
        this.writeLock = new ReentrantLock();
        this.readLock = new Object();
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.isFirstAppOutputRecord = true;
        this.heldRecordBuffer = null;
        this.preferLocalCipherSuites = false;
        if (!sock.isConnected()) {
            throw new SocketException("Underlying socket is not connected");
        }
        this.host = host;
        this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
        this.init(context, false);
        this.autoClose = autoClose;
        this.doneConnect();
    }
    
    SSLSocketImpl(final SSLContextImpl context, final Socket sock, final InputStream consumed, final boolean autoClose) throws IOException {
        super(sock, consumed);
        this.enableSessionCreation = true;
        this.autoClose = true;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.noSniExtension = false;
        this.noSniMatcher = false;
        this.applicationProtocols = new String[0];
        this.applicationProtocol = null;
        this.handshakeLock = new Object();
        this.writeLock = new ReentrantLock();
        this.readLock = new Object();
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.isFirstAppOutputRecord = true;
        this.heldRecordBuffer = null;
        this.preferLocalCipherSuites = false;
        if (!sock.isConnected()) {
            throw new SocketException("Underlying socket is not connected");
        }
        this.init(context, true);
        this.autoClose = autoClose;
        this.doneConnect();
    }
    
    private void init(final SSLContextImpl context, final boolean isServer) {
        this.sslContext = context;
        this.sess = new SSLSessionImpl();
        this.handshakeSession = null;
        this.roleIsServer = isServer;
        this.connectionState = 0;
        this.readCipher = CipherBox.NULL;
        this.readAuthenticator = MAC.NULL;
        this.writeCipher = CipherBox.NULL;
        this.writeAuthenticator = MAC.NULL;
        this.secureRenegotiation = false;
        this.clientVerifyData = new byte[0];
        this.serverVerifyData = new byte[0];
        this.enabledCipherSuites = this.sslContext.getDefaultCipherSuiteList(this.roleIsServer);
        this.enabledProtocols = this.sslContext.getDefaultProtocolList(this.roleIsServer);
        this.inrec = null;
        this.acc = AccessController.getContext();
        this.input = new AppInputStream(this);
        this.output = new AppOutputStream(this);
    }
    
    @Override
    public void connect(final SocketAddress endpoint, final int timeout) throws IOException {
        if (this.isLayered()) {
            throw new SocketException("Already connected");
        }
        if (!(endpoint instanceof InetSocketAddress)) {
            throw new SocketException("Cannot handle non-Inet socket addresses.");
        }
        super.connect(endpoint, timeout);
        if (this.host == null || this.host.length() == 0) {
            this.useImplicitHost(false);
        }
        this.doneConnect();
    }
    
    void doneConnect() throws IOException {
        this.sockInput = super.getInputStream();
        this.sockOutput = super.getOutputStream();
        this.initHandshaker();
    }
    
    private synchronized int getConnectionState() {
        return this.connectionState;
    }
    
    private synchronized void setConnectionState(final int state) {
        this.connectionState = state;
    }
    
    AccessControlContext getAcc() {
        return this.acc;
    }
    
    void writeRecord(final OutputRecord r) throws IOException {
        this.writeRecord(r, false);
    }
    
    void writeRecord(final OutputRecord r, final boolean holdRecord) throws IOException {
    Label_0108:
        while (r.contentType() == 23) {
            switch (this.getConnectionState()) {
                case 1: {
                    this.performInitialHandshake();
                    continue;
                }
                case 2:
                case 3: {
                    break Label_0108;
                }
                case 4: {
                    this.fatal((byte)0, "error while writing to socket");
                    continue;
                }
                case 5:
                case 6:
                case 7: {
                    if (this.closeReason != null) {
                        throw this.closeReason;
                    }
                    throw new SocketException("Socket closed");
                }
                default: {
                    throw new SSLProtocolException("State error, send app data");
                }
            }
        }
        if (!r.isEmpty()) {
            if (r.isAlert((byte)0) && this.getSoLinger() >= 0) {
                boolean interrupted = Thread.interrupted();
                try {
                    if (this.writeLock.tryLock(this.getSoLinger(), TimeUnit.SECONDS)) {
                        try {
                            this.writeRecordInternal(r, holdRecord);
                        }
                        finally {
                            this.writeLock.unlock();
                        }
                    }
                    else {
                        final SSLException ssle = new SSLException("SO_LINGER timeout, close_notify message cannot be sent.");
                        if (this.isLayered() && !this.autoClose) {
                            this.fatal((byte)(-1), ssle);
                        }
                        else if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
                            System.out.println(Thread.currentThread().getName() + ", received Exception: " + ssle);
                        }
                        this.sess.invalidate();
                    }
                }
                catch (final InterruptedException ie) {
                    interrupted = true;
                }
                if (interrupted) {
                    Thread.currentThread().interrupt();
                }
            }
            else {
                this.writeLock.lock();
                try {
                    this.writeRecordInternal(r, holdRecord);
                }
                finally {
                    this.writeLock.unlock();
                }
            }
        }
    }
    
    private void writeRecordInternal(final OutputRecord r, boolean holdRecord) throws IOException {
        r.encrypt(this.writeAuthenticator, this.writeCipher);
        if (holdRecord) {
            if (this.getTcpNoDelay()) {
                holdRecord = false;
            }
            else if (this.heldRecordBuffer == null) {
                this.heldRecordBuffer = new ByteArrayOutputStream(40);
            }
        }
        r.write(this.sockOutput, holdRecord, this.heldRecordBuffer);
        if (this.connectionState < 4) {
            this.checkSequenceNumber(this.writeAuthenticator, r.contentType());
        }
        if (this.isFirstAppOutputRecord && r.contentType() == 23) {
            this.isFirstAppOutputRecord = false;
        }
    }
    
    boolean needToSplitPayload() {
        this.writeLock.lock();
        try {
            return this.protocolVersion.v <= ProtocolVersion.TLS10.v && this.writeCipher.isCBCMode() && !this.isFirstAppOutputRecord && Record.enableCBCProtection;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    void readDataRecord(final InputRecord r) throws IOException {
        if (this.getConnectionState() == 1) {
            this.performInitialHandshake();
        }
        this.readRecord(r, true);
    }
    
    private void readRecord(final InputRecord r, final boolean needAppData) throws IOException {
        synchronized (this.readLock) {
            int state;
            while ((state = this.getConnectionState()) != 6 && state != 4 && state != 7) {
                try {
                    r.setAppDataValid(false);
                    r.read(this.sockInput, this.sockOutput);
                }
                catch (final SSLProtocolException e) {
                    try {
                        this.fatal((byte)10, e);
                    }
                    catch (final IOException ex) {}
                    throw e;
                }
                catch (final EOFException eof) {
                    final boolean handshaking = this.getConnectionState() <= 1;
                    final boolean rethrow = SSLSocketImpl.requireCloseNotify || handshaking;
                    if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
                        System.out.println(Thread.currentThread().getName() + ", received EOFException: " + (rethrow ? "error" : "ignored"));
                    }
                    if (rethrow) {
                        SSLException e2;
                        if (handshaking) {
                            e2 = new SSLHandshakeException("Remote host closed connection during handshake");
                        }
                        else {
                            e2 = new SSLProtocolException("Remote host closed connection incorrectly");
                        }
                        e2.initCause(eof);
                        throw e2;
                    }
                    this.closeInternal(false);
                    continue;
                }
                try {
                    r.decrypt(this.readAuthenticator, this.readCipher);
                }
                catch (final BadPaddingException e3) {
                    final byte alertType = (byte)((r.contentType() == 22) ? 40 : 20);
                    this.fatal(alertType, e3.getMessage(), e3);
                }
                synchronized (this) {
                    switch (r.contentType()) {
                        case 22: {
                            this.initHandshaker();
                            if (!this.handshaker.activated()) {
                                if (this.connectionState == 3) {
                                    this.handshaker.activate(this.protocolVersion);
                                }
                                else {
                                    this.handshaker.activate(null);
                                }
                            }
                            this.handshaker.process_record(r, this.expectingFinished);
                            this.expectingFinished = false;
                            if (this.handshaker.invalidated) {
                                this.handshaker = null;
                                this.inrec.setHandshakeHash(null);
                                if (this.connectionState == 3) {
                                    this.connectionState = 2;
                                }
                            }
                            else if (this.handshaker.isDone()) {
                                this.secureRenegotiation = this.handshaker.isSecureRenegotiation();
                                this.clientVerifyData = this.handshaker.getClientVerifyData();
                                this.serverVerifyData = this.handshaker.getServerVerifyData();
                                this.applicationProtocol = this.handshaker.getHandshakeApplicationProtocol();
                                this.sess = this.handshaker.getSession();
                                this.handshakeSession = null;
                                this.handshaker = null;
                                this.connectionState = 2;
                                if (this.handshakeListeners != null) {
                                    final HandshakeCompletedEvent event = new HandshakeCompletedEvent(this, this.sess);
                                    final Thread t = new NotifyHandshakeThread(this.handshakeListeners.entrySet(), event);
                                    t.start();
                                }
                            }
                            if (needAppData || this.connectionState != 2) {
                                continue;
                            }
                            break;
                        }
                        case 23: {
                            if (this.connectionState != 2 && this.connectionState != 3 && this.connectionState != 5) {
                                throw new SSLProtocolException("Data received in non-data state: " + this.connectionState);
                            }
                            if (this.expectingFinished) {
                                throw new SSLProtocolException("Expecting finished message, received data");
                            }
                            if (!needAppData) {
                                throw new SSLException("Discarding app data");
                            }
                            r.setAppDataValid(true);
                            break;
                        }
                        case 21: {
                            this.recvAlert(r);
                            continue;
                        }
                        case 20: {
                            if (this.connectionState != 1 && this.connectionState != 3) {
                                this.fatal((byte)10, "illegal change cipher spec msg, conn state = " + this.connectionState);
                            }
                            else if (r.available() != 1 || r.read() != 1) {
                                this.fatal((byte)10, "Malformed change cipher spec msg");
                            }
                            this.handshaker.receiveChangeCipherSpec();
                            this.changeReadCiphers();
                            this.expectingFinished = true;
                            continue;
                        }
                        default: {
                            if (SSLSocketImpl.debug == null || !Debug.isOn("ssl")) {
                                continue;
                            }
                            System.out.println(Thread.currentThread().getName() + ", Received record type: " + r.contentType());
                            continue;
                        }
                    }
                    if (this.connectionState < 4) {
                        this.checkSequenceNumber(this.readAuthenticator, r.contentType());
                    }
                    return;
                }
                break;
            }
            r.close();
        }
    }
    
    private void checkSequenceNumber(final Authenticator authenticator, final byte type) throws IOException {
        if (this.connectionState >= 4 || authenticator == MAC.NULL) {
            return;
        }
        if (authenticator.seqNumOverflow()) {
            if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
                System.out.println(Thread.currentThread().getName() + ", sequence number extremely close to overflow (2^64-1 packets). Closing connection.");
            }
            this.fatal((byte)40, "sequence number overflow");
        }
        if (type != 22 && authenticator.seqNumIsHuge()) {
            if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
                System.out.println(Thread.currentThread().getName() + ", request renegotiation to avoid sequence number overflow");
            }
            this.startHandshake();
        }
    }
    
    AppInputStream getAppInputStream() {
        return this.input;
    }
    
    AppOutputStream getAppOutputStream() {
        return this.output;
    }
    
    private void initHandshaker() {
        switch (this.connectionState) {
            case 0:
            case 2: {
                if (this.connectionState == 0) {
                    this.connectionState = 1;
                }
                else {
                    this.connectionState = 3;
                }
                if (this.roleIsServer) {
                    (this.handshaker = new ServerHandshaker(this, this.sslContext, this.enabledProtocols, this.doClientAuth, this.protocolVersion, this.connectionState == 1, this.secureRenegotiation, this.clientVerifyData, this.serverVerifyData)).setSNIMatchers(this.sniMatchers);
                    this.handshaker.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
                }
                else {
                    (this.handshaker = new ClientHandshaker(this, this.sslContext, this.enabledProtocols, this.protocolVersion, this.connectionState == 1, this.secureRenegotiation, this.clientVerifyData, this.serverVerifyData)).setSNIServerNames(this.serverNames);
                }
                this.handshaker.setEnabledCipherSuites(this.enabledCipherSuites);
                this.handshaker.setEnableSessionCreation(this.enableSessionCreation);
                this.handshaker.setApplicationProtocols(this.applicationProtocols);
                this.handshaker.setApplicationProtocolSelectorSSLSocket(this.applicationProtocolSelector);
                return;
            }
            case 1:
            case 3: {
                return;
            }
            default: {
                throw new IllegalStateException("Internal error");
            }
        }
    }
    
    private void performInitialHandshake() throws IOException {
        synchronized (this.handshakeLock) {
            if (this.getConnectionState() == 1) {
                this.kickstartHandshake();
                if (this.inrec == null) {
                    (this.inrec = new InputRecord()).setHandshakeHash(this.input.r.getHandshakeHash());
                    this.inrec.setHelloVersion(this.input.r.getHelloVersion());
                    this.inrec.enableFormatChecks();
                }
                this.readRecord(this.inrec, false);
                this.inrec = null;
            }
        }
    }
    
    @Override
    public void startHandshake() throws IOException {
        this.startHandshake(true);
    }
    
    private void startHandshake(final boolean resumable) throws IOException {
        this.checkWrite();
        try {
            if (this.getConnectionState() == 1) {
                this.performInitialHandshake();
            }
            else {
                this.kickstartHandshake();
            }
        }
        catch (final Exception e) {
            this.handleException(e, resumable);
        }
    }
    
    private synchronized void kickstartHandshake() throws IOException {
        switch (this.connectionState) {
            case 1: {
                break;
            }
            case 2: {
                if (!this.secureRenegotiation && !Handshaker.allowUnsafeRenegotiation) {
                    throw new SSLHandshakeException("Insecure renegotiation is not allowed");
                }
                if (!this.secureRenegotiation && SSLSocketImpl.debug != null && Debug.isOn("handshake")) {
                    System.out.println("Warning: Using insecure renegotiation");
                }
                this.initHandshaker();
                break;
            }
            case 3: {
                return;
            }
            case 0: {
                throw new SocketException("handshaking attempted on unconnected socket");
            }
            default: {
                throw new SocketException("connection is closed");
            }
        }
        if (!this.handshaker.activated()) {
            if (this.connectionState == 3) {
                this.handshaker.activate(this.protocolVersion);
            }
            else {
                this.handshaker.activate(null);
            }
            if (this.handshaker instanceof ClientHandshaker) {
                this.handshaker.kickstart();
            }
            else if (this.connectionState != 1) {
                this.handshaker.kickstart();
                this.handshaker.handshakeHash.reset();
            }
        }
    }
    
    @Override
    public boolean isClosed() {
        return this.connectionState == 7;
    }
    
    boolean checkEOF() throws IOException {
        switch (this.getConnectionState()) {
            case 0: {
                throw new SocketException("Socket is not connected");
            }
            case 1:
            case 2:
            case 3:
            case 5: {
                return false;
            }
            case 7: {
                throw new SocketException("Socket is closed");
            }
            default: {
                if (this.closeReason == null) {
                    return true;
                }
                final IOException e = new SSLException("Connection has been shutdown: " + this.closeReason);
                e.initCause(this.closeReason);
                throw e;
            }
        }
    }
    
    void checkWrite() throws IOException {
        if (this.checkEOF() || this.getConnectionState() == 5) {
            throw new SocketException("Connection closed by remote host");
        }
    }
    
    protected void closeSocket() throws IOException {
        if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", called closeSocket()");
        }
        super.close();
    }
    
    private void closeSocket(final boolean selfInitiated) throws IOException {
        if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", called closeSocket(" + selfInitiated + ")");
        }
        if (!this.isLayered() || this.autoClose) {
            super.close();
        }
        else if (selfInitiated) {
            this.waitForClose(false);
        }
    }
    
    @Override
    public void close() throws IOException {
        if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", called close()");
        }
        this.closeInternal(true);
        this.setConnectionState(7);
    }
    
    private void closeInternal(final boolean selfInitiated) throws IOException {
        if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", called closeInternal(" + selfInitiated + ")");
        }
        int state = this.getConnectionState();
        boolean closeSocketCalled = false;
        Throwable cachedThrowable = null;
        try {
            switch (state) {
                case 0: {
                    this.closeSocket(selfInitiated);
                    break;
                }
                case 4: {
                    this.closeSocket();
                    break;
                }
                case 6:
                case 7: {
                    break;
                }
                default: {
                    synchronized (this) {
                        if ((state = this.getConnectionState()) == 6 || state == 4 || state == 7) {
                            return;
                        }
                        if (state != 5) {
                            try {
                                this.warning((byte)0);
                                this.connectionState = 5;
                            }
                            catch (final Throwable th) {
                                this.connectionState = 4;
                                cachedThrowable = th;
                                closeSocketCalled = true;
                                this.closeSocket(selfInitiated);
                            }
                        }
                    }
                    if (state == 5) {
                        if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
                            System.out.println(Thread.currentThread().getName() + ", close invoked again; state = " + this.getConnectionState());
                        }
                        if (!selfInitiated) {
                            return;
                        }
                        synchronized (this) {
                            while (this.connectionState < 6) {
                                try {
                                    this.wait();
                                }
                                catch (final InterruptedException ex) {}
                            }
                        }
                        if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
                            System.out.println(Thread.currentThread().getName() + ", after primary close; state = " + this.getConnectionState());
                        }
                        return;
                    }
                    else {
                        if (!closeSocketCalled) {
                            closeSocketCalled = true;
                            this.closeSocket(selfInitiated);
                            break;
                        }
                        break;
                    }
                    break;
                }
            }
        }
        finally {
            synchronized (this) {
                this.connectionState = ((this.connectionState == 7) ? 7 : 6);
                this.notifyAll();
            }
            if (closeSocketCalled) {
                this.disposeCiphers();
            }
            if (cachedThrowable != null) {
                if (cachedThrowable instanceof Error) {
                    throw (Error)cachedThrowable;
                }
                if (cachedThrowable instanceof RuntimeException) {
                    throw (RuntimeException)cachedThrowable;
                }
            }
        }
    }
    
    void waitForClose(final boolean rethrow) throws IOException {
        if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", waiting for close_notify or alert: state " + this.getConnectionState());
        }
        try {
            int state;
            while ((state = this.getConnectionState()) != 6 && state != 4 && state != 7) {
                if (this.inrec == null) {
                    this.inrec = new InputRecord();
                }
                try {
                    this.readRecord(this.inrec, true);
                }
                catch (final SocketTimeoutException e) {
                    if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
                        System.out.println(Thread.currentThread().getName() + ", received Exception: " + e);
                    }
                    this.fatal((byte)(-1), "Did not receive close_notify from peer", e);
                }
            }
            this.inrec = null;
        }
        catch (final IOException e2) {
            if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
                System.out.println(Thread.currentThread().getName() + ", Exception while waiting for close " + e2);
            }
            if (rethrow) {
                throw e2;
            }
        }
    }
    
    private void disposeCiphers() {
        synchronized (this.readLock) {
            this.readCipher.dispose();
        }
        this.writeLock.lock();
        try {
            this.writeCipher.dispose();
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    void handleException(final Exception e) throws IOException {
        this.handleException(e, true);
    }
    
    private synchronized void handleException(final Exception e, final boolean resumable) throws IOException {
        if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", handling exception: " + e.toString());
        }
        if (e instanceof InterruptedIOException && resumable) {
            throw (IOException)e;
        }
        if (this.closeReason != null) {
            if (e instanceof IOException) {
                throw (IOException)e;
            }
            throw Alerts.getSSLException((byte)80, e, "Unexpected exception");
        }
        else {
            final boolean isSSLException = e instanceof SSLException;
            if (!isSSLException && e instanceof IOException) {
                try {
                    this.fatal((byte)10, e);
                }
                catch (final IOException ex) {}
                throw (IOException)e;
            }
            byte alertType;
            if (isSSLException) {
                if (e instanceof SSLHandshakeException) {
                    alertType = 40;
                }
                else {
                    alertType = 10;
                }
            }
            else {
                alertType = 80;
            }
            this.fatal(alertType, e);
        }
    }
    
    void warning(final byte description) {
        this.sendAlert((byte)1, description);
    }
    
    synchronized void fatal(final byte description, final String diagnostic) throws IOException {
        this.fatal(description, diagnostic, null);
    }
    
    synchronized void fatal(final byte description, final Throwable cause) throws IOException {
        this.fatal(description, null, cause);
    }
    
    synchronized void fatal(final byte description, final String diagnostic, final Throwable cause) throws IOException {
        if (this.input != null && this.input.r != null) {
            this.input.r.close();
        }
        this.sess.invalidate();
        if (this.handshakeSession != null) {
            this.handshakeSession.invalidate();
        }
        final int oldState = this.connectionState;
        if (this.connectionState < 4) {
            this.connectionState = 4;
        }
        if (this.closeReason == null) {
            if (oldState == 1) {
                this.sockInput.skip(this.sockInput.available());
            }
            if (description != -1) {
                this.sendAlert((byte)2, description);
            }
            if (cause instanceof SSLException) {
                this.closeReason = (SSLException)cause;
            }
            else {
                this.closeReason = Alerts.getSSLException(description, cause, diagnostic);
            }
        }
        this.closeSocket();
        if (this.connectionState < 6) {
            this.connectionState = ((oldState == 7) ? 7 : 6);
            this.readCipher.dispose();
            this.writeCipher.dispose();
        }
        throw this.closeReason;
    }
    
    private void recvAlert(final InputRecord r) throws IOException {
        final byte level = (byte)r.read();
        final byte description = (byte)r.read();
        if (description == -1) {
            this.fatal((byte)47, "Short alert message");
        }
        if (SSLSocketImpl.debug != null && (Debug.isOn("record") || Debug.isOn("handshake"))) {
            synchronized (System.out) {
                System.out.print(Thread.currentThread().getName());
                System.out.print(", RECV " + this.protocolVersion + " ALERT:  ");
                if (level == 2) {
                    System.out.print("fatal, ");
                }
                else if (level == 1) {
                    System.out.print("warning, ");
                }
                else {
                    System.out.print("<level " + (0xFF & level) + ">, ");
                }
                System.out.println(Alerts.alertDescription(description));
            }
        }
        if (level == 1) {
            if (description == 0) {
                if (this.connectionState == 1) {
                    this.fatal((byte)10, "Received close_notify during handshake");
                }
                else {
                    this.closeInternal(false);
                }
            }
            else if (this.handshaker != null) {
                this.handshaker.handshakeAlert(description);
            }
        }
        else {
            final String reason = "Received fatal alert: " + Alerts.alertDescription(description);
            if (this.closeReason == null) {
                this.closeReason = Alerts.getSSLException(description, reason);
            }
            this.fatal((byte)10, reason);
        }
    }
    
    private void sendAlert(final byte level, final byte description) {
        if (this.connectionState >= 5) {
            return;
        }
        if (this.connectionState == 1 && (this.handshaker == null || !this.handshaker.started())) {
            return;
        }
        final OutputRecord r = new OutputRecord((byte)21);
        r.setVersion(this.protocolVersion);
        final boolean useDebug = SSLSocketImpl.debug != null && Debug.isOn("ssl");
        if (useDebug) {
            synchronized (System.out) {
                System.out.print(Thread.currentThread().getName());
                System.out.print(", SEND " + this.protocolVersion + " ALERT:  ");
                if (level == 2) {
                    System.out.print("fatal, ");
                }
                else if (level == 1) {
                    System.out.print("warning, ");
                }
                else {
                    System.out.print("<level = " + (0xFF & level) + ">, ");
                }
                System.out.println("description = " + Alerts.alertDescription(description));
            }
        }
        r.write(level);
        r.write(description);
        try {
            this.writeRecord(r);
        }
        catch (final IOException e) {
            if (useDebug) {
                System.out.println(Thread.currentThread().getName() + ", Exception sending alert: " + e);
            }
        }
    }
    
    private void changeReadCiphers() throws SSLException {
        if (this.connectionState != 1 && this.connectionState != 3) {
            throw new SSLProtocolException("State error, change cipher specs");
        }
        final CipherBox oldCipher = this.readCipher;
        try {
            this.readCipher = this.handshaker.newReadCipher();
            this.readAuthenticator = this.handshaker.newReadAuthenticator();
        }
        catch (final GeneralSecurityException e) {
            throw new SSLException("Algorithm missing:  ", e);
        }
        oldCipher.dispose();
    }
    
    void changeWriteCiphers() throws SSLException {
        if (this.connectionState != 1 && this.connectionState != 3) {
            throw new SSLProtocolException("State error, change cipher specs");
        }
        final CipherBox oldCipher = this.writeCipher;
        try {
            this.writeCipher = this.handshaker.newWriteCipher();
            this.writeAuthenticator = this.handshaker.newWriteAuthenticator();
        }
        catch (final GeneralSecurityException e) {
            throw new SSLException("Algorithm missing:  ", e);
        }
        oldCipher.dispose();
        this.isFirstAppOutputRecord = true;
    }
    
    synchronized void setVersion(final ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
        this.output.r.setVersion(protocolVersion);
    }
    
    synchronized String getHost() {
        if (this.host == null || this.host.length() == 0) {
            this.useImplicitHost(true);
        }
        return this.host;
    }
    
    private synchronized void useImplicitHost(final boolean noSniUpdate) {
        final InetAddress inetAddress = this.getInetAddress();
        if (inetAddress == null) {
            return;
        }
        final JavaNetAccess jna = SharedSecrets.getJavaNetAccess();
        final String originalHostname = jna.getOriginalHostName(inetAddress);
        if (originalHostname != null && originalHostname.length() != 0) {
            this.host = originalHostname;
            if (!noSniUpdate && this.serverNames.isEmpty() && !this.noSniExtension) {
                this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
                if (!this.roleIsServer && this.handshaker != null && !this.handshaker.started()) {
                    this.handshaker.setSNIServerNames(this.serverNames);
                }
            }
            return;
        }
        if (!SSLSocketImpl.trustNameService) {
            this.host = inetAddress.getHostAddress();
        }
        else {
            this.host = this.getInetAddress().getHostName();
        }
    }
    
    public synchronized void setHost(final String host) {
        this.host = host;
        this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
        if (!this.roleIsServer && this.handshaker != null && !this.handshaker.started()) {
            this.handshaker.setSNIServerNames(this.serverNames);
        }
    }
    
    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (this.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (this.connectionState == 0) {
            throw new SocketException("Socket is not connected");
        }
        return this.input;
    }
    
    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        if (this.isClosed()) {
            throw new SocketException("Socket is closed");
        }
        if (this.connectionState == 0) {
            throw new SocketException("Socket is not connected");
        }
        return this.output;
    }
    
    @Override
    public SSLSession getSession() {
        if (this.getConnectionState() == 1) {
            try {
                this.startHandshake(false);
            }
            catch (final IOException e) {
                if (SSLSocketImpl.debug != null && Debug.isOn("handshake")) {
                    System.out.println(Thread.currentThread().getName() + ", IOException in getSession():  " + e);
                }
            }
        }
        synchronized (this) {
            return this.sess;
        }
    }
    
    @Override
    public synchronized SSLSession getHandshakeSession() {
        return this.handshakeSession;
    }
    
    synchronized void setHandshakeSession(final SSLSessionImpl session) {
        this.handshakeSession = session;
    }
    
    @Override
    public synchronized void setEnableSessionCreation(final boolean flag) {
        this.enableSessionCreation = flag;
        if (this.handshaker != null && !this.handshaker.activated()) {
            this.handshaker.setEnableSessionCreation(this.enableSessionCreation);
        }
    }
    
    @Override
    public synchronized boolean getEnableSessionCreation() {
        return this.enableSessionCreation;
    }
    
    @Override
    public synchronized void setNeedClientAuth(final boolean flag) {
        this.doClientAuth = (byte)(flag ? 2 : 0);
        if (this.handshaker != null && this.handshaker instanceof ServerHandshaker && !this.handshaker.activated()) {
            ((ServerHandshaker)this.handshaker).setClientAuth(this.doClientAuth);
        }
    }
    
    @Override
    public synchronized boolean getNeedClientAuth() {
        return this.doClientAuth == 2;
    }
    
    @Override
    public synchronized void setWantClientAuth(final boolean flag) {
        this.doClientAuth = (byte)(flag ? 1 : 0);
        if (this.handshaker != null && this.handshaker instanceof ServerHandshaker && !this.handshaker.activated()) {
            ((ServerHandshaker)this.handshaker).setClientAuth(this.doClientAuth);
        }
    }
    
    @Override
    public synchronized boolean getWantClientAuth() {
        return this.doClientAuth == 1;
    }
    
    @Override
    public synchronized void setUseClientMode(final boolean flag) {
        switch (this.connectionState) {
            case 0: {
                if (this.roleIsServer != !flag) {
                    if (this.sslContext.isDefaultProtocolList(this.enabledProtocols)) {
                        this.enabledProtocols = this.sslContext.getDefaultProtocolList(!flag);
                    }
                    if (this.sslContext.isDefaultCipherSuiteList(this.enabledCipherSuites)) {
                        this.enabledCipherSuites = this.sslContext.getDefaultCipherSuiteList(!flag);
                    }
                }
                this.roleIsServer = !flag;
                return;
            }
            case 1: {
                assert this.handshaker != null;
                if (!this.handshaker.activated()) {
                    if (this.roleIsServer != !flag && this.sslContext.isDefaultProtocolList(this.enabledProtocols)) {
                        this.enabledProtocols = this.sslContext.getDefaultProtocolList(!flag);
                    }
                    this.roleIsServer = !flag;
                    this.connectionState = 0;
                    this.initHandshaker();
                    return;
                }
                break;
            }
        }
        if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", setUseClientMode() invoked in state = " + this.connectionState);
        }
        throw new IllegalArgumentException("Cannot change mode after SSL traffic has started");
    }
    
    @Override
    public synchronized boolean getUseClientMode() {
        return !this.roleIsServer;
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        return this.sslContext.getSupportedCipherSuiteList().toStringArray();
    }
    
    @Override
    public synchronized void setEnabledCipherSuites(final String[] suites) {
        this.enabledCipherSuites = new CipherSuiteList(suites);
        if (this.handshaker != null && !this.handshaker.activated()) {
            this.handshaker.setEnabledCipherSuites(this.enabledCipherSuites);
        }
    }
    
    @Override
    public synchronized String[] getEnabledCipherSuites() {
        return this.enabledCipherSuites.toStringArray();
    }
    
    @Override
    public String[] getSupportedProtocols() {
        return this.sslContext.getSuportedProtocolList().toStringArray();
    }
    
    @Override
    public synchronized void setEnabledProtocols(final String[] protocols) {
        this.enabledProtocols = new ProtocolList(protocols);
        if (this.handshaker != null && !this.handshaker.activated()) {
            this.handshaker.setEnabledProtocols(this.enabledProtocols);
        }
    }
    
    @Override
    public synchronized String[] getEnabledProtocols() {
        return this.enabledProtocols.toStringArray();
    }
    
    @Override
    public void setSoTimeout(final int timeout) throws SocketException {
        if (SSLSocketImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", setSoTimeout(" + timeout + ") called");
        }
        super.setSoTimeout(timeout);
    }
    
    @Override
    public synchronized void addHandshakeCompletedListener(final HandshakeCompletedListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        if (this.handshakeListeners == null) {
            this.handshakeListeners = new HashMap<HandshakeCompletedListener, AccessControlContext>(4);
        }
        this.handshakeListeners.put(listener, AccessController.getContext());
    }
    
    @Override
    public synchronized void removeHandshakeCompletedListener(final HandshakeCompletedListener listener) {
        if (this.handshakeListeners == null) {
            throw new IllegalArgumentException("no listeners");
        }
        if (this.handshakeListeners.remove(listener) == null) {
            throw new IllegalArgumentException("listener not registered");
        }
        if (this.handshakeListeners.isEmpty()) {
            this.handshakeListeners = null;
        }
    }
    
    @Override
    public synchronized SSLParameters getSSLParameters() {
        final SSLParameters params = super.getSSLParameters();
        params.setEndpointIdentificationAlgorithm(this.identificationProtocol);
        params.setAlgorithmConstraints(this.algorithmConstraints);
        if (this.sniMatchers.isEmpty() && !this.noSniMatcher) {
            params.setSNIMatchers(null);
        }
        else {
            params.setSNIMatchers(this.sniMatchers);
        }
        if (this.serverNames.isEmpty() && !this.noSniExtension) {
            params.setServerNames(null);
        }
        else {
            params.setServerNames(this.serverNames);
        }
        params.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
        params.setApplicationProtocols(this.applicationProtocols);
        return params;
    }
    
    @Override
    public synchronized void setSSLParameters(final SSLParameters params) {
        super.setSSLParameters(params);
        this.identificationProtocol = params.getEndpointIdentificationAlgorithm();
        this.algorithmConstraints = params.getAlgorithmConstraints();
        this.preferLocalCipherSuites = params.getUseCipherSuitesOrder();
        final List<SNIServerName> sniNames = params.getServerNames();
        if (sniNames != null) {
            this.noSniExtension = sniNames.isEmpty();
            this.serverNames = sniNames;
        }
        final Collection<SNIMatcher> matchers = params.getSNIMatchers();
        if (matchers != null) {
            this.noSniMatcher = matchers.isEmpty();
            this.sniMatchers = matchers;
        }
        this.applicationProtocols = params.getApplicationProtocols();
        if (this.handshaker != null && !this.handshaker.started()) {
            this.handshaker.setIdentificationProtocol(this.identificationProtocol);
            this.handshaker.setAlgorithmConstraints(this.algorithmConstraints);
            this.handshaker.setApplicationProtocols(this.applicationProtocols);
            if (this.roleIsServer) {
                this.handshaker.setSNIMatchers(this.sniMatchers);
                this.handshaker.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
            }
            else {
                this.handshaker.setSNIServerNames(this.serverNames);
            }
        }
    }
    
    public synchronized String getApplicationProtocol() {
        return this.applicationProtocol;
    }
    
    public synchronized String getHandshakeApplicationProtocol() {
        if (this.handshaker != null && this.handshaker.started()) {
            return this.handshaker.getHandshakeApplicationProtocol();
        }
        return null;
    }
    
    public synchronized void setHandshakeApplicationProtocolSelector(final BiFunction<SSLSocket, List<String>, String> selector) {
        this.applicationProtocolSelector = selector;
        if (this.handshaker != null && !this.handshaker.activated()) {
            this.handshaker.setApplicationProtocolSelectorSSLSocket(selector);
        }
    }
    
    public synchronized BiFunction<SSLSocket, List<String>, String> getHandshakeApplicationProtocolSelector() {
        return this.applicationProtocolSelector;
    }
    
    @Override
    public String toString() {
        final StringBuffer retval = new StringBuffer(80);
        retval.append(Integer.toHexString(this.hashCode()));
        retval.append("[");
        retval.append(this.sess.getCipherSuite());
        retval.append(": ");
        retval.append(super.toString());
        retval.append("]");
        return retval.toString();
    }
    
    static {
        debug = Debug.getInstance("ssl");
        trustNameService = Debug.getBooleanProperty("jdk.tls.trustNameService", false);
    }
    
    private static class NotifyHandshakeThread extends Thread
    {
        private Set<Map.Entry<HandshakeCompletedListener, AccessControlContext>> targets;
        private HandshakeCompletedEvent event;
        
        NotifyHandshakeThread(final Set<Map.Entry<HandshakeCompletedListener, AccessControlContext>> entrySet, final HandshakeCompletedEvent e) {
            super("HandshakeCompletedNotify-Thread");
            this.targets = new HashSet<Map.Entry<HandshakeCompletedListener, AccessControlContext>>(entrySet);
            this.event = e;
        }
        
        @Override
        public void run() {
            for (final Map.Entry<HandshakeCompletedListener, AccessControlContext> entry : this.targets) {
                final HandshakeCompletedListener l = entry.getKey();
                final AccessControlContext acc = entry.getValue();
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        l.handshakeCompleted(NotifyHandshakeThread.this.event);
                        return null;
                    }
                }, acc);
            }
        }
    }
}
