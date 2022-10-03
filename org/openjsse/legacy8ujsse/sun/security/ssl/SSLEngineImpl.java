package org.openjsse.legacy8ujsse.sun.security.ssl;

import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.crypto.BadPaddingException;
import java.nio.ByteBuffer;
import java.io.IOException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLProtocolException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLEngineResult;
import java.security.AccessController;
import java.util.Collections;
import java.util.function.BiFunction;
import javax.net.ssl.SNIMatcher;
import java.util.Collection;
import javax.net.ssl.SNIServerName;
import java.util.List;
import java.security.AlgorithmConstraints;
import java.security.AccessControlContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLEngine;

public final class SSLEngineImpl extends SSLEngine
{
    private int connectionState;
    private static final int cs_START = 0;
    private static final int cs_HANDSHAKE = 1;
    private static final int cs_DATA = 2;
    private static final int cs_RENEGOTIATE = 3;
    private static final int cs_ERROR = 4;
    private static final int cs_CLOSED = 6;
    private boolean inboundDone;
    EngineWriter writer;
    private SSLContextImpl sslContext;
    private Handshaker handshaker;
    private SSLSessionImpl sess;
    private volatile SSLSessionImpl handshakeSession;
    static final byte clauth_none = 0;
    static final byte clauth_requested = 1;
    static final byte clauth_required = 2;
    private boolean expectingFinished;
    private boolean recvCN;
    private SSLException closeReason;
    private byte doClientAuth;
    private boolean enableSessionCreation;
    EngineInputRecord inputRecord;
    EngineOutputRecord outputRecord;
    private AccessControlContext acc;
    private CipherSuiteList enabledCipherSuites;
    private String identificationProtocol;
    private AlgorithmConstraints algorithmConstraints;
    List<SNIServerName> serverNames;
    Collection<SNIMatcher> sniMatchers;
    String[] applicationProtocols;
    String applicationProtocol;
    BiFunction<SSLEngine, List<String>, String> applicationProtocolSelector;
    private boolean serverModeSet;
    private boolean roleIsServer;
    private ProtocolList enabledProtocols;
    private ProtocolVersion protocolVersion;
    private Authenticator readAuthenticator;
    private Authenticator writeAuthenticator;
    private CipherBox readCipher;
    private CipherBox writeCipher;
    private boolean secureRenegotiation;
    private byte[] clientVerifyData;
    private byte[] serverVerifyData;
    private Object wrapLock;
    private Object unwrapLock;
    Object writeLock;
    private boolean isFirstAppOutputRecord;
    private boolean preferLocalCipherSuites;
    private static final Debug debug;
    
    SSLEngineImpl(final SSLContextImpl ctx) {
        this.inboundDone = false;
        this.enableSessionCreation = true;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.applicationProtocols = new String[0];
        this.applicationProtocol = null;
        this.serverModeSet = false;
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.isFirstAppOutputRecord = true;
        this.preferLocalCipherSuites = false;
        this.init(ctx);
    }
    
    SSLEngineImpl(final SSLContextImpl ctx, final String host, final int port) {
        super(host, port);
        this.inboundDone = false;
        this.enableSessionCreation = true;
        this.identificationProtocol = null;
        this.algorithmConstraints = null;
        this.serverNames = Collections.emptyList();
        this.sniMatchers = (Collection<SNIMatcher>)Collections.emptyList();
        this.applicationProtocols = new String[0];
        this.applicationProtocol = null;
        this.serverModeSet = false;
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.isFirstAppOutputRecord = true;
        this.preferLocalCipherSuites = false;
        this.init(ctx);
    }
    
    private void init(final SSLContextImpl ctx) {
        if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println("Using SSLEngineImpl.");
        }
        this.sslContext = ctx;
        this.sess = new SSLSessionImpl();
        this.handshakeSession = null;
        this.roleIsServer = true;
        this.connectionState = 0;
        this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.getPeerHost());
        this.readCipher = CipherBox.NULL;
        this.readAuthenticator = MAC.NULL;
        this.writeCipher = CipherBox.NULL;
        this.writeAuthenticator = MAC.NULL;
        this.secureRenegotiation = false;
        this.clientVerifyData = new byte[0];
        this.serverVerifyData = new byte[0];
        this.enabledCipherSuites = this.sslContext.getDefaultCipherSuiteList(this.roleIsServer);
        this.enabledProtocols = this.sslContext.getDefaultProtocolList(this.roleIsServer);
        this.wrapLock = new Object();
        this.unwrapLock = new Object();
        this.writeLock = new Object();
        this.acc = AccessController.getContext();
        this.outputRecord = new EngineOutputRecord((byte)23, this);
        (this.inputRecord = new EngineInputRecord(this)).enableFormatChecks();
        this.writer = new EngineWriter();
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
                this.handshaker.setApplicationProtocolSelectorSSLEngine(this.applicationProtocolSelector);
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
    
    private SSLEngineResult.HandshakeStatus getHSStatus(final SSLEngineResult.HandshakeStatus hss) {
        if (hss != null) {
            return hss;
        }
        synchronized (this) {
            if (this.writer.hasOutboundData()) {
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            if (this.handshaker != null) {
                if (this.handshaker.taskOutstanding()) {
                    return SSLEngineResult.HandshakeStatus.NEED_TASK;
                }
                return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
            }
            else {
                if (this.connectionState == 6 && !this.isInboundDone()) {
                    return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
                }
                return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
            }
        }
    }
    
    private synchronized void checkTaskThrown() throws SSLException {
        if (this.handshaker != null) {
            this.handshaker.checkThrown();
        }
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
    
    @Override
    public SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        return this.getHSStatus(null);
    }
    
    private void changeReadCiphers() throws SSLException {
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
        this.outputRecord.setVersion(protocolVersion);
    }
    
    private synchronized void kickstartHandshake() throws IOException {
        switch (this.connectionState) {
            case 0: {
                if (!this.serverModeSet) {
                    throw new IllegalStateException("Client/Server mode not yet set.");
                }
                this.initHandshaker();
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                if (!this.secureRenegotiation && !Handshaker.allowUnsafeRenegotiation) {
                    throw new SSLHandshakeException("Insecure renegotiation is not allowed");
                }
                if (!this.secureRenegotiation && SSLEngineImpl.debug != null && Debug.isOn("handshake")) {
                    System.out.println("Warning: Using insecure renegotiation");
                }
                this.initHandshaker();
                break;
            }
            case 3: {
                return;
            }
            default: {
                throw new SSLException("SSLEngine is closing/closed");
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
    public void beginHandshake() throws SSLException {
        try {
            this.kickstartHandshake();
        }
        catch (final Exception e) {
            this.fatal((byte)40, "Couldn't kickstart handshaking", e);
        }
    }
    
    @Override
    public SSLEngineResult unwrap(final ByteBuffer netData, final ByteBuffer[] appData, final int offset, final int length) throws SSLException {
        final EngineArgs ea = new EngineArgs(netData, appData, offset, length);
        try {
            synchronized (this.unwrapLock) {
                return this.readNetRecord(ea);
            }
        }
        catch (final SSLProtocolException spe) {
            this.fatal((byte)10, spe.getMessage(), spe);
            return null;
        }
        catch (final Exception e) {
            this.fatal((byte)80, "problem unwrapping net record", e);
            return null;
        }
        finally {
            ea.resetLim();
        }
    }
    
    private SSLEngineResult readNetRecord(final EngineArgs ea) throws IOException {
        SSLEngineResult.Status status = null;
        SSLEngineResult.HandshakeStatus hsStatus = null;
        this.checkTaskThrown();
        if (this.isInboundDone()) {
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, this.getHSStatus(null), 0, 0);
        }
        synchronized (this) {
            if (this.connectionState == 1 || this.connectionState == 0) {
                this.kickstartHandshake();
                hsStatus = this.getHSStatus(null);
                if (hsStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
                    return new SSLEngineResult(SSLEngineResult.Status.OK, hsStatus, 0, 0);
                }
            }
        }
        if (hsStatus == null) {
            hsStatus = this.getHSStatus(null);
        }
        if (hsStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
            return new SSLEngineResult(SSLEngineResult.Status.OK, hsStatus, 0, 0);
        }
        final int packetLen = this.inputRecord.bytesInCompletePacket(ea.netData);
        if (packetLen > this.sess.getPacketBufferSize()) {
            if (packetLen > 33305) {
                throw new SSLProtocolException("Input SSL/TLS record too big: max = 33305 len = " + packetLen);
            }
            this.sess.expandBufferSizes();
        }
        if (packetLen - 5 > ea.getAppRemaining()) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, hsStatus, 0, 0);
        }
        if (packetLen == -1 || ea.netData.remaining() < packetLen) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, hsStatus, 0, 0);
        }
        try {
            hsStatus = this.readRecord(ea);
        }
        catch (final SSLException e) {
            throw e;
        }
        catch (final IOException e2) {
            throw new SSLException("readRecord", e2);
        }
        status = (this.isInboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK);
        hsStatus = this.getHSStatus(hsStatus);
        return new SSLEngineResult(status, hsStatus, ea.deltaNet(), ea.deltaApp());
    }
    
    private SSLEngineResult.HandshakeStatus readRecord(final EngineArgs ea) throws IOException {
        SSLEngineResult.HandshakeStatus hsStatus = null;
        ByteBuffer readBB = null;
        ByteBuffer decryptedBB = null;
        if (this.getConnectionState() != 4) {
            try {
                readBB = this.inputRecord.read(ea.netData);
            }
            catch (final IOException e) {
                this.fatal((byte)10, e);
            }
            try {
                decryptedBB = this.inputRecord.decrypt(this.readAuthenticator, this.readCipher, readBB);
            }
            catch (final BadPaddingException e2) {
                final byte alertType = (byte)((this.inputRecord.contentType() == 22) ? 40 : 20);
                this.fatal(alertType, e2.getMessage(), e2);
            }
            synchronized (this) {
                switch (this.inputRecord.contentType()) {
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
                        this.handshaker.process_record(this.inputRecord, this.expectingFinished);
                        this.expectingFinished = false;
                        if (this.handshaker.invalidated) {
                            this.handshaker = null;
                            if (this.connectionState == 3) {
                                this.connectionState = 2;
                                break;
                            }
                            break;
                        }
                        else {
                            if (this.handshaker.isDone()) {
                                this.secureRenegotiation = this.handshaker.isSecureRenegotiation();
                                this.clientVerifyData = this.handshaker.getClientVerifyData();
                                this.serverVerifyData = this.handshaker.getServerVerifyData();
                                this.applicationProtocol = this.handshaker.getHandshakeApplicationProtocol();
                                this.sess = this.handshaker.getSession();
                                this.handshakeSession = null;
                                if (!this.writer.hasOutboundData()) {
                                    hsStatus = SSLEngineResult.HandshakeStatus.FINISHED;
                                }
                                this.handshaker = null;
                                this.connectionState = 2;
                                break;
                            }
                            if (this.handshaker.taskOutstanding()) {
                                hsStatus = SSLEngineResult.HandshakeStatus.NEED_TASK;
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case 23: {
                        if (this.connectionState != 2 && this.connectionState != 3 && this.connectionState != 6) {
                            throw new SSLProtocolException("Data received in non-data state: " + this.connectionState);
                        }
                        if (this.expectingFinished) {
                            throw new SSLProtocolException("Expecting finished message, received data");
                        }
                        if (!this.inboundDone) {
                            ea.scatter(decryptedBB.slice());
                            break;
                        }
                        break;
                    }
                    case 21: {
                        this.recvAlert();
                        break;
                    }
                    case 20: {
                        if (this.connectionState != 1 && this.connectionState != 3) {
                            this.fatal((byte)10, "illegal change cipher spec msg, conn state = " + this.connectionState);
                        }
                        else if (this.inputRecord.available() != 1 || this.inputRecord.read() != 1) {
                            this.fatal((byte)10, "Malformed change cipher spec msg");
                        }
                        this.handshaker.receiveChangeCipherSpec();
                        this.changeReadCiphers();
                        this.expectingFinished = true;
                        break;
                    }
                    default: {
                        if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
                            System.out.println(Thread.currentThread().getName() + ", Received record type: " + this.inputRecord.contentType());
                            break;
                        }
                        break;
                    }
                }
                hsStatus = this.getHSStatus(hsStatus);
                if (this.connectionState < 4 && !this.isInboundDone() && hsStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING && this.checkSequenceNumber(this.readAuthenticator, this.inputRecord.contentType())) {
                    hsStatus = this.getHSStatus(null);
                }
            }
        }
        return hsStatus;
    }
    
    @Override
    public SSLEngineResult wrap(final ByteBuffer[] appData, final int offset, final int length, final ByteBuffer netData) throws SSLException {
        final EngineArgs ea = new EngineArgs(appData, offset, length, netData);
        if (netData.remaining() < 16921) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHSStatus(null), 0, 0);
        }
        try {
            synchronized (this.wrapLock) {
                return this.writeAppRecord(ea);
            }
        }
        catch (final SSLProtocolException spe) {
            this.fatal((byte)10, spe.getMessage(), spe);
            return null;
        }
        catch (final Exception e) {
            ea.resetPos();
            this.fatal((byte)80, "problem wrapping app data", e);
            return null;
        }
        finally {
            ea.resetLim();
        }
    }
    
    private SSLEngineResult writeAppRecord(final EngineArgs ea) throws IOException {
        SSLEngineResult.Status status = null;
        SSLEngineResult.HandshakeStatus hsStatus = null;
        this.checkTaskThrown();
        if (this.writer.isOutboundDone()) {
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, this.getHSStatus(null), 0, 0);
        }
        synchronized (this) {
            if (this.connectionState == 1 || this.connectionState == 0) {
                this.kickstartHandshake();
                hsStatus = this.getHSStatus(null);
                if (hsStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                    return new SSLEngineResult(SSLEngineResult.Status.OK, hsStatus, 0, 0);
                }
            }
        }
        if (hsStatus == null) {
            hsStatus = this.getHSStatus(null);
        }
        if (hsStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
            return new SSLEngineResult(SSLEngineResult.Status.OK, hsStatus, 0, 0);
        }
        try {
            synchronized (this.writeLock) {
                hsStatus = this.writeRecord(this.outputRecord, ea);
            }
        }
        catch (final SSLException e) {
            throw e;
        }
        catch (final IOException e2) {
            throw new SSLException("Write problems", e2);
        }
        status = (this.isOutboundDone() ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK);
        hsStatus = this.getHSStatus(hsStatus);
        return new SSLEngineResult(status, hsStatus, ea.deltaApp(), ea.deltaNet());
    }
    
    private SSLEngineResult.HandshakeStatus writeRecord(final EngineOutputRecord eor, final EngineArgs ea) throws IOException {
        SSLEngineResult.HandshakeStatus hsStatus = this.writer.writeRecord(eor, ea, this.writeAuthenticator, this.writeCipher);
        hsStatus = this.getHSStatus(hsStatus);
        if (this.connectionState < 4 && !this.isOutboundDone() && hsStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING && this.checkSequenceNumber(this.writeAuthenticator, eor.contentType())) {
            hsStatus = this.getHSStatus(null);
        }
        if (this.isFirstAppOutputRecord && ea.deltaApp() > 0) {
            this.isFirstAppOutputRecord = false;
        }
        return hsStatus;
    }
    
    boolean needToSplitPayload(final CipherBox cipher, final ProtocolVersion protocol) {
        return protocol.v <= ProtocolVersion.TLS10.v && cipher.isCBCMode() && !this.isFirstAppOutputRecord && Record.enableCBCProtection;
    }
    
    void writeRecord(final EngineOutputRecord eor) throws IOException {
        this.writer.writeRecord(eor, this.writeAuthenticator, this.writeCipher);
        if (this.connectionState < 4 && !this.isOutboundDone()) {
            this.checkSequenceNumber(this.writeAuthenticator, eor.contentType());
        }
    }
    
    private boolean checkSequenceNumber(final Authenticator authenticator, final byte type) throws IOException {
        if (this.connectionState >= 4 || authenticator == MAC.NULL) {
            return false;
        }
        if (authenticator.seqNumOverflow()) {
            if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
                System.out.println(Thread.currentThread().getName() + ", sequence number extremely close to overflow (2^64-1 packets). Closing connection.");
            }
            this.fatal((byte)40, "sequence number overflow");
            return true;
        }
        if (type != 22 && authenticator.seqNumIsHuge()) {
            if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
                System.out.println(Thread.currentThread().getName() + ", request renegotiation to avoid sequence number overflow");
            }
            this.beginHandshake();
            return true;
        }
        return false;
    }
    
    private void closeOutboundInternal() {
        if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", closeOutboundInternal()");
        }
        if (this.writer.isOutboundDone()) {
            return;
        }
        switch (this.connectionState) {
            case 0: {
                this.writer.closeOutbound();
                this.inboundDone = true;
                break;
            }
            case 4:
            case 6: {
                break;
            }
            default: {
                this.warning((byte)0);
                this.writer.closeOutbound();
                break;
            }
        }
        this.writeCipher.dispose();
        this.connectionState = 6;
    }
    
    @Override
    public synchronized void closeOutbound() {
        if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", called closeOutbound()");
        }
        this.closeOutboundInternal();
    }
    
    @Override
    public boolean isOutboundDone() {
        return this.writer.isOutboundDone();
    }
    
    private void closeInboundInternal() {
        if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", closeInboundInternal()");
        }
        if (this.inboundDone) {
            return;
        }
        this.closeOutboundInternal();
        this.inboundDone = true;
        this.readCipher.dispose();
        this.connectionState = 6;
    }
    
    @Override
    public synchronized void closeInbound() throws SSLException {
        if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", called closeInbound()");
        }
        if (this.connectionState != 0 && !this.recvCN) {
            this.recvCN = true;
            this.fatal((byte)80, "Inbound closed before receiving peer's close_notify: possible truncation attack?");
        }
        else {
            this.closeInboundInternal();
        }
    }
    
    @Override
    public synchronized boolean isInboundDone() {
        return this.inboundDone;
    }
    
    @Override
    public synchronized SSLSession getSession() {
        return this.sess;
    }
    
    @Override
    public synchronized SSLSession getHandshakeSession() {
        return this.handshakeSession;
    }
    
    synchronized void setHandshakeSession(final SSLSessionImpl session) {
        this.handshakeSession = session;
    }
    
    @Override
    public synchronized Runnable getDelegatedTask() {
        if (this.handshaker != null) {
            return this.handshaker.getTask();
        }
        return null;
    }
    
    void warning(final byte description) {
        this.sendAlert((byte)1, description);
    }
    
    synchronized void fatal(final byte description, final String diagnostic) throws SSLException {
        this.fatal(description, diagnostic, null);
    }
    
    synchronized void fatal(final byte description, final Throwable cause) throws SSLException {
        this.fatal(description, null, cause);
    }
    
    synchronized void fatal(final byte description, String diagnostic, Throwable cause) throws SSLException {
        if (diagnostic == null) {
            diagnostic = "General SSLEngine problem";
        }
        if (cause == null) {
            cause = Alerts.getSSLException(description, cause, diagnostic);
        }
        if (this.closeReason != null) {
            if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
                System.out.println(Thread.currentThread().getName() + ", fatal: engine already closed.  Rethrowing " + cause.toString());
            }
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            }
            if (cause instanceof SSLException) {
                throw (SSLException)cause;
            }
            if (cause instanceof Exception) {
                throw new SSLException("fatal SSLEngine condition", cause);
            }
        }
        if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", fatal error: " + description + ": " + diagnostic + "\n" + cause.toString());
        }
        final int oldState = this.connectionState;
        this.connectionState = 4;
        this.inboundDone = true;
        this.sess.invalidate();
        if (this.handshakeSession != null) {
            this.handshakeSession.invalidate();
        }
        if (oldState != 0) {
            this.sendAlert((byte)2, description);
        }
        if (cause instanceof SSLException) {
            this.closeReason = (SSLException)cause;
        }
        else {
            this.closeReason = Alerts.getSSLException(description, cause, diagnostic);
        }
        this.writer.closeOutbound();
        this.connectionState = 6;
        this.readCipher.dispose();
        this.writeCipher.dispose();
        if (cause instanceof RuntimeException) {
            throw (RuntimeException)cause;
        }
        throw this.closeReason;
    }
    
    private void recvAlert() throws IOException {
        final byte level = (byte)this.inputRecord.read();
        final byte description = (byte)this.inputRecord.read();
        if (description == -1) {
            this.fatal((byte)47, "Short alert message");
        }
        if (SSLEngineImpl.debug != null && (Debug.isOn("record") || Debug.isOn("handshake"))) {
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
                    this.recvCN = true;
                    this.closeInboundInternal();
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
        if (this.connectionState >= 6) {
            return;
        }
        if (this.connectionState == 1 && (this.handshaker == null || !this.handshaker.started())) {
            return;
        }
        final EngineOutputRecord r = new EngineOutputRecord((byte)21, this);
        r.setVersion(this.protocolVersion);
        final boolean useDebug = SSLEngineImpl.debug != null && Debug.isOn("ssl");
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
                this.serverModeSet = true;
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
        if (SSLEngineImpl.debug != null && Debug.isOn("ssl")) {
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
    public synchronized SSLParameters getSSLParameters() {
        final SSLParameters params = super.getSSLParameters();
        params.setEndpointIdentificationAlgorithm(this.identificationProtocol);
        params.setAlgorithmConstraints(this.algorithmConstraints);
        params.setSNIMatchers(this.sniMatchers);
        params.setServerNames(this.serverNames);
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
            this.serverNames = sniNames;
        }
        final Collection<SNIMatcher> matchers = params.getSNIMatchers();
        if (matchers != null) {
            this.sniMatchers = matchers;
        }
        this.applicationProtocols = params.getApplicationProtocols();
        if (this.handshaker != null && !this.handshaker.started()) {
            this.handshaker.setIdentificationProtocol(this.identificationProtocol);
            this.handshaker.setAlgorithmConstraints(this.algorithmConstraints);
            this.applicationProtocols = params.getApplicationProtocols();
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
    
    public synchronized void setHandshakeApplicationProtocolSelector(final BiFunction<SSLEngine, List<String>, String> selector) {
        this.applicationProtocolSelector = selector;
        if (this.handshaker != null && !this.handshaker.activated()) {
            this.handshaker.setApplicationProtocolSelectorSSLEngine(selector);
        }
    }
    
    public synchronized BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
        return this.applicationProtocolSelector;
    }
    
    @Override
    public String toString() {
        final StringBuilder retval = new StringBuilder(80);
        retval.append(Integer.toHexString(this.hashCode()));
        retval.append("[");
        retval.append("SSLEngine[hostname=");
        final String host = this.getPeerHost();
        retval.append((host == null) ? "null" : host);
        retval.append(" port=");
        retval.append(Integer.toString(this.getPeerPort()));
        retval.append("] ");
        retval.append(this.getSession().getCipherSuite());
        retval.append("]");
        return retval.toString();
    }
    
    static {
        debug = Debug.getInstance("ssl");
    }
}
