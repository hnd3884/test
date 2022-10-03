package org.apache.tomcat.util.net.openssl;

import java.security.Principal;
import javax.security.cert.CertificateException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import java.util.HashMap;
import javax.net.ssl.SSLSessionContext;
import java.util.Map;
import java.util.HashSet;
import java.util.Collections;
import org.apache.tomcat.jni.SSLContext;
import org.apache.tomcat.jni.Pool;
import java.util.LinkedHashSet;
import org.apache.juli.logging.LogFactory;
import javax.net.ssl.SSLSession;
import java.util.List;
import java.util.ArrayList;
import org.apache.tomcat.util.net.openssl.ciphers.OpenSSLCipherConfigurationParser;
import java.nio.ReadOnlyBufferException;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.apache.tomcat.jni.Buffer;
import java.nio.ByteBuffer;
import org.apache.tomcat.jni.SSL;
import javax.security.cert.X509Certificate;
import java.util.Set;
import java.security.cert.Certificate;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.net.SSLUtil;
import javax.net.ssl.SSLEngine;

public final class OpenSSLEngine extends SSLEngine implements SSLUtil.ProtocolInfo
{
    private static final Log logger;
    private static final StringManager sm;
    private static final Certificate[] EMPTY_CERTIFICATES;
    public static final Set<String> AVAILABLE_CIPHER_SUITES;
    public static final Set<String> IMPLEMENTED_PROTOCOLS_SET;
    private static final int MAX_PLAINTEXT_LENGTH = 16384;
    private static final int MAX_COMPRESSED_LENGTH = 17408;
    private static final int MAX_CIPHERTEXT_LENGTH = 18432;
    static final int VERIFY_DEPTH = 10;
    static final int MAX_ENCRYPTED_PACKET_LENGTH = 18713;
    static final int MAX_ENCRYPTION_OVERHEAD_LENGTH = 2329;
    private static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
    private static final long EMPTY_ADDR;
    private final long ssl;
    private final long networkBIO;
    private Accepted accepted;
    private boolean handshakeFinished;
    private int currentHandshake;
    private boolean receivedShutdown;
    private volatile boolean destroyed;
    private volatile String version;
    private volatile String cipher;
    private volatile String applicationProtocol;
    private volatile Certificate[] peerCerts;
    @Deprecated
    private volatile X509Certificate[] x509PeerCerts;
    private volatile ClientAuthMode clientAuth;
    private boolean isInboundDone;
    private boolean isOutboundDone;
    private boolean engineClosed;
    private boolean sendHandshakeError;
    private final boolean clientMode;
    private final String fallbackApplicationProtocol;
    private final OpenSSLSessionContext sessionContext;
    private final boolean alpn;
    private final boolean initialized;
    private final int certificateVerificationDepth;
    private final boolean certificateVerificationOptionalNoCA;
    private String selectedProtocol;
    private final OpenSSLSession session;
    
    OpenSSLEngine(final long sslCtx, final String fallbackApplicationProtocol, final boolean clientMode, final OpenSSLSessionContext sessionContext, final boolean alpn, final boolean initialized, final int certificateVerificationDepth, final boolean certificateVerificationOptionalNoCA) {
        this.accepted = Accepted.NOT;
        this.clientAuth = ClientAuthMode.NONE;
        this.sendHandshakeError = false;
        this.selectedProtocol = null;
        if (sslCtx == 0L) {
            throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.noSSLContext"));
        }
        this.session = new OpenSSLSession();
        this.ssl = SSL.newSSL(sslCtx, !clientMode);
        this.networkBIO = SSL.makeNetworkBIO(this.ssl);
        this.fallbackApplicationProtocol = fallbackApplicationProtocol;
        this.clientMode = clientMode;
        this.sessionContext = sessionContext;
        this.alpn = alpn;
        this.initialized = initialized;
        this.certificateVerificationDepth = certificateVerificationDepth;
        this.certificateVerificationOptionalNoCA = certificateVerificationOptionalNoCA;
    }
    
    @Override
    public String getNegotiatedProtocol() {
        return this.selectedProtocol;
    }
    
    public synchronized void shutdown() {
        if (!this.destroyed) {
            this.destroyed = true;
            if (this.networkBIO != 0L) {
                SSL.freeBIO(this.networkBIO);
            }
            if (this.ssl != 0L) {
                SSL.freeSSL(this.ssl);
            }
            final boolean isInboundDone = true;
            this.engineClosed = isInboundDone;
            this.isOutboundDone = isInboundDone;
            this.isInboundDone = isInboundDone;
        }
    }
    
    private int writePlaintextData(final long ssl, final ByteBuffer src) throws SSLException {
        clearLastError();
        final int pos = src.position();
        final int limit = src.limit();
        final int len = Math.min(limit - pos, 16384);
        int sslWrote;
        if (src.isDirect()) {
            final long addr = Buffer.address(src) + pos;
            sslWrote = SSL.writeToSSL(ssl, addr, len);
            if (sslWrote <= 0) {
                this.checkLastError();
            }
            if (sslWrote >= 0) {
                src.position(pos + sslWrote);
                return sslWrote;
            }
        }
        else {
            final ByteBuffer buf = ByteBuffer.allocateDirect(len);
            try {
                final long addr2 = Buffer.address(buf);
                src.limit(pos + len);
                buf.put(src);
                src.limit(limit);
                sslWrote = SSL.writeToSSL(ssl, addr2, len);
                if (sslWrote <= 0) {
                    this.checkLastError();
                }
                if (sslWrote >= 0) {
                    src.position(pos + sslWrote);
                    return sslWrote;
                }
                src.position(pos);
            }
            finally {
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer(buf);
            }
        }
        throw new IllegalStateException(OpenSSLEngine.sm.getString("engine.writeToSSLFailed", new Object[] { Integer.toString(sslWrote) }));
    }
    
    private int writeEncryptedData(final long networkBIO, final ByteBuffer src) throws SSLException {
        clearLastError();
        final int pos = src.position();
        final int len = src.remaining();
        if (src.isDirect()) {
            final long addr = Buffer.address(src) + pos;
            final int netWrote = SSL.writeToBIO(networkBIO, addr, len);
            if (netWrote <= 0) {
                this.checkLastError();
            }
            if (netWrote >= 0) {
                src.position(pos + netWrote);
                return netWrote;
            }
        }
        else {
            final ByteBuffer buf = ByteBuffer.allocateDirect(len);
            try {
                final long addr2 = Buffer.address(buf);
                buf.put(src);
                final int netWrote2 = SSL.writeToBIO(networkBIO, addr2, len);
                if (netWrote2 <= 0) {
                    this.checkLastError();
                }
                if (netWrote2 >= 0) {
                    src.position(pos + netWrote2);
                    return netWrote2;
                }
                src.position(pos);
            }
            finally {
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer(buf);
            }
        }
        return 0;
    }
    
    private int readPlaintextData(final long ssl, final ByteBuffer dst) throws SSLException {
        clearLastError();
        if (dst.isDirect()) {
            final int pos = dst.position();
            final long addr = Buffer.address(dst) + pos;
            final int len = dst.limit() - pos;
            final int sslRead = SSL.readFromSSL(ssl, addr, len);
            if (sslRead > 0) {
                dst.position(pos + sslRead);
                return sslRead;
            }
            this.checkLastError();
        }
        else {
            final int pos = dst.position();
            final int limit = dst.limit();
            final int len2 = Math.min(18713, limit - pos);
            final ByteBuffer buf = ByteBuffer.allocateDirect(len2);
            try {
                final long addr2 = Buffer.address(buf);
                final int sslRead2 = SSL.readFromSSL(ssl, addr2, len2);
                if (sslRead2 > 0) {
                    buf.limit(sslRead2);
                    dst.limit(pos + sslRead2);
                    dst.put(buf);
                    dst.limit(limit);
                    return sslRead2;
                }
                this.checkLastError();
            }
            finally {
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer(buf);
            }
        }
        return 0;
    }
    
    private int readEncryptedData(final long networkBIO, final ByteBuffer dst, final int pending) throws SSLException {
        clearLastError();
        if (dst.isDirect() && dst.remaining() >= pending) {
            final int pos = dst.position();
            final long addr = Buffer.address(dst) + pos;
            final int bioRead = SSL.readFromBIO(networkBIO, addr, pending);
            if (bioRead > 0) {
                dst.position(pos + bioRead);
                return bioRead;
            }
            this.checkLastError();
        }
        else {
            final ByteBuffer buf = ByteBuffer.allocateDirect(pending);
            try {
                final long addr = Buffer.address(buf);
                final int bioRead = SSL.readFromBIO(networkBIO, addr, pending);
                if (bioRead > 0) {
                    buf.limit(bioRead);
                    final int oldLimit = dst.limit();
                    dst.limit(dst.position() + bioRead);
                    dst.put(buf);
                    dst.limit(oldLimit);
                    return bioRead;
                }
                this.checkLastError();
            }
            finally {
                buf.clear();
                ByteBufferUtils.cleanDirectBuffer(buf);
            }
        }
        return 0;
    }
    
    @Override
    public synchronized SSLEngineResult wrap(final ByteBuffer[] srcs, final int offset, final int length, final ByteBuffer dst) throws SSLException {
        if (this.destroyed) {
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
        }
        if (srcs == null || dst == null) {
            throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullBuffer"));
        }
        if (offset >= srcs.length || offset + length > srcs.length) {
            throw new IndexOutOfBoundsException(OpenSSLEngine.sm.getString("engine.invalidBufferArray", new Object[] { Integer.toString(offset), Integer.toString(length), Integer.toString(srcs.length) }));
        }
        if (dst.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        if (this.accepted == Accepted.NOT) {
            this.beginHandshakeImplicitly();
        }
        final SSLEngineResult.HandshakeStatus handshakeStatus = this.getHandshakeStatus();
        if ((!this.handshakeFinished || this.engineClosed) && handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
            return new SSLEngineResult(this.getEngineStatus(), SSLEngineResult.HandshakeStatus.NEED_UNWRAP, 0, 0);
        }
        int bytesProduced = 0;
        int pendingNet = SSL.pendingWrittenBytesInBIO(this.networkBIO);
        if (pendingNet <= 0) {
            int bytesConsumed = 0;
            for (int endOffset = offset + length, i = offset; i < endOffset; ++i) {
                final ByteBuffer src = srcs[i];
                if (src == null) {
                    throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullBufferInArray"));
                }
                while (src.hasRemaining()) {
                    try {
                        bytesConsumed += this.writePlaintextData(this.ssl, src);
                    }
                    catch (final Exception e) {
                        throw new SSLException(e);
                    }
                    pendingNet = SSL.pendingWrittenBytesInBIO(this.networkBIO);
                    if (pendingNet > 0) {
                        final int capacity = dst.remaining();
                        if (capacity < pendingNet) {
                            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), bytesConsumed, bytesProduced);
                        }
                        try {
                            bytesProduced += this.readEncryptedData(this.networkBIO, dst, pendingNet);
                        }
                        catch (final Exception e2) {
                            throw new SSLException(e2);
                        }
                        return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), bytesConsumed, bytesProduced);
                    }
                }
            }
            return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), bytesConsumed, bytesProduced);
        }
        final int capacity2 = dst.remaining();
        if (capacity2 < pendingNet) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, handshakeStatus, 0, 0);
        }
        try {
            bytesProduced = this.readEncryptedData(this.networkBIO, dst, pendingNet);
        }
        catch (final Exception e3) {
            throw new SSLException(e3);
        }
        if (this.isOutboundDone) {
            this.shutdown();
        }
        return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), 0, bytesProduced);
    }
    
    @Override
    public synchronized SSLEngineResult unwrap(final ByteBuffer src, final ByteBuffer[] dsts, final int offset, final int length) throws SSLException {
        if (this.destroyed) {
            return new SSLEngineResult(SSLEngineResult.Status.CLOSED, SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING, 0, 0);
        }
        if (src == null || dsts == null) {
            throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullBuffer"));
        }
        if (offset >= dsts.length || offset + length > dsts.length) {
            throw new IndexOutOfBoundsException(OpenSSLEngine.sm.getString("engine.invalidBufferArray", new Object[] { Integer.toString(offset), Integer.toString(length), Integer.toString(dsts.length) }));
        }
        int capacity = 0;
        final int endOffset = offset + length;
        for (int i = offset; i < endOffset; ++i) {
            final ByteBuffer dst = dsts[i];
            if (dst == null) {
                throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullBufferInArray"));
            }
            if (dst.isReadOnly()) {
                throw new ReadOnlyBufferException();
            }
            capacity += dst.remaining();
        }
        if (this.accepted == Accepted.NOT) {
            this.beginHandshakeImplicitly();
        }
        final SSLEngineResult.HandshakeStatus handshakeStatus = this.getHandshakeStatus();
        if ((!this.handshakeFinished || this.engineClosed) && handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
            return new SSLEngineResult(this.getEngineStatus(), SSLEngineResult.HandshakeStatus.NEED_WRAP, 0, 0);
        }
        final int len = src.remaining();
        if (len > 18713) {
            this.isInboundDone = true;
            this.isOutboundDone = true;
            this.engineClosed = true;
            this.shutdown();
            throw new SSLException(OpenSSLEngine.sm.getString("engine.oversizedPacket"));
        }
        int written = 0;
        try {
            written = this.writeEncryptedData(this.networkBIO, src);
        }
        catch (final Exception e) {
            throw new SSLException(e);
        }
        int pendingApp = this.pendingReadableBytesInSSL();
        if (!this.handshakeFinished) {
            pendingApp = 0;
        }
        int bytesProduced = 0;
        int idx = offset;
        if (capacity == 0) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_OVERFLOW, this.getHandshakeStatus(), written, 0);
        }
        while (pendingApp > 0) {
            if (idx == endOffset) {
                throw new IllegalStateException(OpenSSLEngine.sm.getString("engine.invalidDestinationBuffersState"));
            }
            while (idx < endOffset) {
                final ByteBuffer dst2 = dsts[idx];
                if (!dst2.hasRemaining()) {
                    ++idx;
                }
                else {
                    if (pendingApp <= 0) {
                        break;
                    }
                    int bytesRead;
                    try {
                        bytesRead = this.readPlaintextData(this.ssl, dst2);
                    }
                    catch (final Exception e2) {
                        throw new SSLException(e2);
                    }
                    if (bytesRead == 0) {
                        throw new IllegalStateException(OpenSSLEngine.sm.getString("engine.failedToReadAvailableBytes"));
                    }
                    bytesProduced += bytesRead;
                    pendingApp -= bytesRead;
                    capacity -= bytesRead;
                    if (dst2.hasRemaining()) {
                        continue;
                    }
                    ++idx;
                }
            }
            if (capacity == 0) {
                break;
            }
            if (pendingApp != 0) {
                continue;
            }
            pendingApp = this.pendingReadableBytesInSSL();
        }
        if (!this.receivedShutdown && (SSL.getShutdown(this.ssl) & 0x2) == 0x2) {
            this.receivedShutdown = true;
            this.closeOutbound();
            this.closeInbound();
        }
        if (bytesProduced == 0 && (written == 0 || (written > 0 && !src.hasRemaining() && this.handshakeFinished))) {
            return new SSLEngineResult(SSLEngineResult.Status.BUFFER_UNDERFLOW, this.getHandshakeStatus(), written, 0);
        }
        return new SSLEngineResult(this.getEngineStatus(), this.getHandshakeStatus(), written, bytesProduced);
    }
    
    private int pendingReadableBytesInSSL() throws SSLException {
        clearLastError();
        int lastPrimingReadResult = SSL.readFromSSL(this.ssl, OpenSSLEngine.EMPTY_ADDR, 0);
        if (lastPrimingReadResult <= 0) {
            this.checkLastError();
        }
        int pendingReadableBytesInSSL = SSL.pendingReadableBytesInSSL(this.ssl);
        if ("TLSv1".equals(this.version) && lastPrimingReadResult == 0 && pendingReadableBytesInSSL == 0) {
            lastPrimingReadResult = SSL.readFromSSL(this.ssl, OpenSSLEngine.EMPTY_ADDR, 0);
            if (lastPrimingReadResult <= 0) {
                this.checkLastError();
            }
            pendingReadableBytesInSSL = SSL.pendingReadableBytesInSSL(this.ssl);
        }
        return pendingReadableBytesInSSL;
    }
    
    @Override
    public Runnable getDelegatedTask() {
        return null;
    }
    
    @Override
    public synchronized void closeInbound() throws SSLException {
        if (this.isInboundDone) {
            return;
        }
        this.isInboundDone = true;
        this.engineClosed = true;
        this.shutdown();
        if (this.accepted != Accepted.NOT && !this.receivedShutdown) {
            throw new SSLException(OpenSSLEngine.sm.getString("engine.inboundClose"));
        }
    }
    
    @Override
    public synchronized boolean isInboundDone() {
        return this.isInboundDone || this.engineClosed;
    }
    
    @Override
    public synchronized void closeOutbound() {
        if (this.isOutboundDone) {
            return;
        }
        this.isOutboundDone = true;
        this.engineClosed = true;
        if (this.accepted != Accepted.NOT && !this.destroyed) {
            final int mode = SSL.getShutdown(this.ssl);
            if ((mode & 0x1) != 0x1) {
                SSL.shutdownSSL(this.ssl);
            }
        }
        else {
            this.shutdown();
        }
    }
    
    @Override
    public synchronized boolean isOutboundDone() {
        return this.isOutboundDone;
    }
    
    @Override
    public String[] getSupportedCipherSuites() {
        final Set<String> availableCipherSuites = OpenSSLEngine.AVAILABLE_CIPHER_SUITES;
        return availableCipherSuites.toArray(new String[0]);
    }
    
    @Override
    public synchronized String[] getEnabledCipherSuites() {
        if (this.destroyed) {
            return new String[0];
        }
        final String[] enabled = SSL.getCiphers(this.ssl);
        if (enabled == null) {
            return new String[0];
        }
        for (int i = 0; i < enabled.length; ++i) {
            final String mapped = OpenSSLCipherConfigurationParser.openSSLToJsse(enabled[i]);
            if (mapped != null) {
                enabled[i] = mapped;
            }
        }
        return enabled;
    }
    
    @Override
    public synchronized void setEnabledCipherSuites(final String[] cipherSuites) {
        if (this.initialized) {
            return;
        }
        if (cipherSuites == null) {
            throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullCipherSuite"));
        }
        if (this.destroyed) {
            return;
        }
        final StringBuilder buf = new StringBuilder();
        for (String cipherSuite : cipherSuites) {
            if (cipherSuite == null) {
                break;
            }
            final String converted = OpenSSLCipherConfigurationParser.jsseToOpenSSL(cipherSuite);
            if (!OpenSSLEngine.AVAILABLE_CIPHER_SUITES.contains(cipherSuite)) {
                OpenSSLEngine.logger.debug((Object)OpenSSLEngine.sm.getString("engine.unsupportedCipher", new Object[] { cipherSuite, converted }));
            }
            if (converted != null) {
                cipherSuite = converted;
            }
            buf.append(cipherSuite);
            buf.append(':');
        }
        if (buf.length() == 0) {
            throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.emptyCipherSuite"));
        }
        buf.setLength(buf.length() - 1);
        final String cipherSuiteSpec = buf.toString();
        try {
            SSL.setCipherSuites(this.ssl, cipherSuiteSpec);
        }
        catch (final Exception e) {
            throw new IllegalStateException(OpenSSLEngine.sm.getString("engine.failedCipherSuite", new Object[] { cipherSuiteSpec }), e);
        }
    }
    
    @Override
    public String[] getSupportedProtocols() {
        return OpenSSLEngine.IMPLEMENTED_PROTOCOLS_SET.toArray(new String[0]);
    }
    
    @Override
    public synchronized String[] getEnabledProtocols() {
        if (this.destroyed) {
            return new String[0];
        }
        final List<String> enabled = new ArrayList<String>();
        enabled.add("SSLv2Hello");
        final int opts = SSL.getOptions(this.ssl);
        if ((opts & 0x4000000) == 0x0) {
            enabled.add("TLSv1");
        }
        if ((opts & 0x10000000) == 0x0) {
            enabled.add("TLSv1.1");
        }
        if ((opts & 0x8000000) == 0x0) {
            enabled.add("TLSv1.2");
        }
        if ((opts & 0x1000000) == 0x0) {
            enabled.add("SSLv2");
        }
        if ((opts & 0x2000000) == 0x0) {
            enabled.add("SSLv3");
        }
        final int size = enabled.size();
        if (size == 0) {
            return new String[0];
        }
        return enabled.toArray(new String[size]);
    }
    
    @Override
    public synchronized void setEnabledProtocols(final String[] protocols) {
        if (this.initialized) {
            return;
        }
        if (protocols == null) {
            throw new IllegalArgumentException();
        }
        if (this.destroyed) {
            return;
        }
        boolean sslv2 = false;
        boolean sslv3 = false;
        boolean tlsv1 = false;
        boolean tlsv1_1 = false;
        boolean tlsv1_2 = false;
        for (final String p : protocols) {
            if (!OpenSSLEngine.IMPLEMENTED_PROTOCOLS_SET.contains(p)) {
                throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.unsupportedProtocol", new Object[] { p }));
            }
            if (p.equals("SSLv2")) {
                sslv2 = true;
            }
            else if (p.equals("SSLv3")) {
                sslv3 = true;
            }
            else if (p.equals("TLSv1")) {
                tlsv1 = true;
            }
            else if (p.equals("TLSv1.1")) {
                tlsv1_1 = true;
            }
            else if (p.equals("TLSv1.2")) {
                tlsv1_2 = true;
            }
        }
        SSL.setOptions(this.ssl, 4095);
        if (!sslv2) {
            SSL.setOptions(this.ssl, 16777216);
        }
        if (!sslv3) {
            SSL.setOptions(this.ssl, 33554432);
        }
        if (!tlsv1) {
            SSL.setOptions(this.ssl, 67108864);
        }
        if (!tlsv1_1) {
            SSL.setOptions(this.ssl, 268435456);
        }
        if (!tlsv1_2) {
            SSL.setOptions(this.ssl, 134217728);
        }
    }
    
    @Override
    public SSLSession getSession() {
        return this.session;
    }
    
    @Override
    public synchronized void beginHandshake() throws SSLException {
        if (this.engineClosed || this.destroyed) {
            throw new SSLException(OpenSSLEngine.sm.getString("engine.engineClosed"));
        }
        switch (this.accepted) {
            case NOT: {
                this.handshake();
                this.accepted = Accepted.EXPLICIT;
                break;
            }
            case IMPLICIT: {
                this.accepted = Accepted.EXPLICIT;
                break;
            }
            case EXPLICIT: {
                this.renegotiate();
                break;
            }
        }
    }
    
    private void beginHandshakeImplicitly() throws SSLException {
        this.handshake();
        this.accepted = Accepted.IMPLICIT;
    }
    
    private void handshake() throws SSLException {
        this.currentHandshake = SSL.getHandshakeCount(this.ssl);
        clearLastError();
        final int code = SSL.doHandshake(this.ssl);
        if (code <= 0) {
            this.checkLastError();
        }
        else {
            if (this.alpn) {
                this.selectedProtocol = SSL.getAlpnSelected(this.ssl);
                if (this.selectedProtocol == null) {
                    this.selectedProtocol = SSL.getNextProtoNegotiated(this.ssl);
                }
            }
            this.session.lastAccessedTime = System.currentTimeMillis();
            this.handshakeFinished = true;
        }
    }
    
    private synchronized void renegotiate() throws SSLException {
        clearLastError();
        int code;
        if (SSL.getVersion(this.ssl).equals("TLSv1.3")) {
            code = SSL.verifyClientPostHandshake(this.ssl);
        }
        else {
            code = SSL.renegotiate(this.ssl);
        }
        if (code <= 0) {
            this.checkLastError();
        }
        this.handshakeFinished = false;
        this.peerCerts = null;
        this.x509PeerCerts = null;
        this.currentHandshake = SSL.getHandshakeCount(this.ssl);
        final int code2 = SSL.doHandshake(this.ssl);
        if (code2 <= 0) {
            this.checkLastError();
        }
    }
    
    private void checkLastError() throws SSLException {
        final String sslError = getLastError();
        if (sslError != null) {
            if (this.handshakeFinished) {
                throw new SSLException(sslError);
            }
            this.sendHandshakeError = true;
        }
    }
    
    private static void clearLastError() {
        getLastError();
    }
    
    private static String getLastError() {
        String sslError = null;
        long error;
        while ((error = SSL.getLastErrorNumber()) != 0L) {
            final String err = SSL.getErrorString(error);
            if (sslError == null) {
                sslError = err;
            }
            if (OpenSSLEngine.logger.isDebugEnabled()) {
                OpenSSLEngine.logger.debug((Object)OpenSSLEngine.sm.getString("engine.openSSLError", new Object[] { Long.toString(error), err }));
            }
        }
        return sslError;
    }
    
    private SSLEngineResult.Status getEngineStatus() {
        return this.engineClosed ? SSLEngineResult.Status.CLOSED : SSLEngineResult.Status.OK;
    }
    
    @Override
    public synchronized SSLEngineResult.HandshakeStatus getHandshakeStatus() {
        if (this.accepted == Accepted.NOT || this.destroyed) {
            return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
        }
        if (!this.handshakeFinished) {
            if (this.sendHandshakeError || SSL.pendingWrittenBytesInBIO(this.networkBIO) != 0) {
                if (this.sendHandshakeError) {
                    this.sendHandshakeError = false;
                    ++this.currentHandshake;
                }
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            final int handshakeCount = SSL.getHandshakeCount(this.ssl);
            if (handshakeCount != this.currentHandshake && SSL.renegotiatePending(this.ssl) == 0 && SSL.getPostHandshakeAuthInProgress(this.ssl) == 0) {
                if (this.alpn) {
                    this.selectedProtocol = SSL.getAlpnSelected(this.ssl);
                    if (this.selectedProtocol == null) {
                        this.selectedProtocol = SSL.getNextProtoNegotiated(this.ssl);
                    }
                }
                this.session.lastAccessedTime = System.currentTimeMillis();
                this.version = SSL.getVersion(this.ssl);
                this.handshakeFinished = true;
                return SSLEngineResult.HandshakeStatus.FINISHED;
            }
            return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
        }
        else {
            if (!this.engineClosed) {
                return SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING;
            }
            if (SSL.pendingWrittenBytesInBIO(this.networkBIO) != 0) {
                return SSLEngineResult.HandshakeStatus.NEED_WRAP;
            }
            return SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
        }
    }
    
    @Override
    public void setUseClientMode(final boolean clientMode) {
        if (clientMode != this.clientMode) {
            throw new UnsupportedOperationException();
        }
    }
    
    @Override
    public boolean getUseClientMode() {
        return this.clientMode;
    }
    
    @Override
    public void setNeedClientAuth(final boolean b) {
        this.setClientAuth(b ? ClientAuthMode.REQUIRE : ClientAuthMode.NONE);
    }
    
    @Override
    public boolean getNeedClientAuth() {
        return this.clientAuth == ClientAuthMode.REQUIRE;
    }
    
    @Override
    public void setWantClientAuth(final boolean b) {
        this.setClientAuth(b ? ClientAuthMode.OPTIONAL : ClientAuthMode.NONE);
    }
    
    @Override
    public boolean getWantClientAuth() {
        return this.clientAuth == ClientAuthMode.OPTIONAL;
    }
    
    private void setClientAuth(final ClientAuthMode mode) {
        if (this.clientMode) {
            return;
        }
        synchronized (this) {
            if (this.clientAuth == mode) {
                return;
            }
            switch (mode) {
                case NONE: {
                    SSL.setVerify(this.ssl, 0, this.certificateVerificationDepth);
                    break;
                }
                case REQUIRE: {
                    SSL.setVerify(this.ssl, 2, this.certificateVerificationDepth);
                    break;
                }
                case OPTIONAL: {
                    SSL.setVerify(this.ssl, this.certificateVerificationOptionalNoCA ? 3 : 1, this.certificateVerificationDepth);
                    break;
                }
            }
            this.clientAuth = mode;
        }
    }
    
    @Override
    public void setEnableSessionCreation(final boolean b) {
        if (!b) {
            final String msg = OpenSSLEngine.sm.getString("engine.noRestrictSessionCreation");
            throw new UnsupportedOperationException(msg);
        }
    }
    
    @Override
    public boolean getEnableSessionCreation() {
        return true;
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.shutdown();
    }
    
    static {
        logger = LogFactory.getLog((Class)OpenSSLEngine.class);
        sm = StringManager.getManager((Class)OpenSSLEngine.class);
        EMPTY_CERTIFICATES = new Certificate[0];
        final Set<String> availableCipherSuites = new LinkedHashSet<String>(128);
        final long aprPool = Pool.create(0L);
        try {
            final long sslCtx = SSLContext.make(aprPool, SSL.SSL_PROTOCOL_ALL, 1);
            try {
                SSLContext.setOptions(sslCtx, 4095);
                SSLContext.setCipherSuite(sslCtx, "ALL");
                final long ssl = SSL.newSSL(sslCtx, true);
                try {
                    for (final String c : SSL.getCiphers(ssl)) {
                        if (c != null && c.length() != 0) {
                            if (!availableCipherSuites.contains(c)) {
                                availableCipherSuites.add(OpenSSLCipherConfigurationParser.openSSLToJsse(c));
                            }
                        }
                    }
                }
                finally {
                    SSL.freeSSL(ssl);
                }
            }
            finally {
                SSLContext.free(sslCtx);
            }
        }
        catch (final Exception e) {
            OpenSSLEngine.logger.warn((Object)OpenSSLEngine.sm.getString("engine.ciphersFailure"), (Throwable)e);
        }
        finally {
            Pool.destroy(aprPool);
        }
        AVAILABLE_CIPHER_SUITES = Collections.unmodifiableSet((Set<? extends String>)availableCipherSuites);
        final HashSet<String> protocols = new HashSet<String>();
        protocols.add("SSLv2Hello");
        protocols.add("SSLv2");
        protocols.add("SSLv3");
        protocols.add("TLSv1");
        protocols.add("TLSv1.1");
        protocols.add("TLSv1.2");
        if (SSL.version() >= 269488143) {
            protocols.add("TLSv1.3");
        }
        IMPLEMENTED_PROTOCOLS_SET = Collections.unmodifiableSet((Set<? extends String>)protocols);
        EMPTY_ADDR = Buffer.address(ByteBuffer.allocate(0));
    }
    
    enum ClientAuthMode
    {
        NONE, 
        OPTIONAL, 
        REQUIRE;
    }
    
    private enum Accepted
    {
        NOT, 
        IMPLICIT, 
        EXPLICIT;
    }
    
    private class OpenSSLSession implements SSLSession
    {
        private Map<String, Object> values;
        private long lastAccessedTime;
        
        private OpenSSLSession() {
            this.lastAccessedTime = -1L;
        }
        
        @Override
        public byte[] getId() {
            byte[] id = null;
            synchronized (OpenSSLEngine.this) {
                if (!OpenSSLEngine.this.destroyed) {
                    id = SSL.getSessionId(OpenSSLEngine.this.ssl);
                }
            }
            return id;
        }
        
        @Override
        public SSLSessionContext getSessionContext() {
            return OpenSSLEngine.this.sessionContext;
        }
        
        @Override
        public long getCreationTime() {
            long creationTime = 0L;
            synchronized (OpenSSLEngine.this) {
                if (!OpenSSLEngine.this.destroyed) {
                    creationTime = SSL.getTime(OpenSSLEngine.this.ssl);
                }
            }
            return creationTime * 1000L;
        }
        
        @Override
        public long getLastAccessedTime() {
            return (this.lastAccessedTime > 0L) ? this.lastAccessedTime : this.getCreationTime();
        }
        
        @Override
        public void invalidate() {
        }
        
        @Override
        public boolean isValid() {
            return false;
        }
        
        @Override
        public void putValue(final String name, final Object value) {
            if (name == null) {
                throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullName"));
            }
            if (value == null) {
                throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullValue"));
            }
            Map<String, Object> values = this.values;
            if (values == null) {
                final HashMap<String, Object> values2 = new HashMap<String, Object>(2);
                this.values = values2;
                values = values2;
            }
            final Object old = values.put(name, value);
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener)value).valueBound(new SSLSessionBindingEvent(this, name));
            }
            this.notifyUnbound(old, name);
        }
        
        @Override
        public Object getValue(final String name) {
            if (name == null) {
                throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullName"));
            }
            if (this.values == null) {
                return null;
            }
            return this.values.get(name);
        }
        
        @Override
        public void removeValue(final String name) {
            if (name == null) {
                throw new IllegalArgumentException(OpenSSLEngine.sm.getString("engine.nullName"));
            }
            final Map<String, Object> values = this.values;
            if (values == null) {
                return;
            }
            final Object old = values.remove(name);
            this.notifyUnbound(old, name);
        }
        
        @Override
        public String[] getValueNames() {
            final Map<String, Object> values = this.values;
            if (values == null || values.isEmpty()) {
                return new String[0];
            }
            return values.keySet().toArray(new String[0]);
        }
        
        private void notifyUnbound(final Object value, final String name) {
            if (value instanceof SSLSessionBindingListener) {
                ((SSLSessionBindingListener)value).valueUnbound(new SSLSessionBindingEvent(this, name));
            }
        }
        
        @Override
        public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
            Certificate[] c = OpenSSLEngine.this.peerCerts;
            if (c == null) {
                final byte[][] chain;
                byte[] clientCert;
                synchronized (OpenSSLEngine.this) {
                    if (OpenSSLEngine.this.destroyed || SSL.isInInit(OpenSSLEngine.this.ssl) != 0) {
                        throw new SSLPeerUnverifiedException(OpenSSLEngine.sm.getString("engine.unverifiedPeer"));
                    }
                    chain = SSL.getPeerCertChain(OpenSSLEngine.this.ssl);
                    if (!OpenSSLEngine.this.clientMode) {
                        clientCert = SSL.getPeerCertificate(OpenSSLEngine.this.ssl);
                    }
                    else {
                        clientCert = null;
                    }
                }
                if (chain == null && clientCert == null) {
                    return null;
                }
                int len = 0;
                if (chain != null) {
                    len += chain.length;
                }
                int i = 0;
                Certificate[] certificates;
                if (clientCert != null) {
                    certificates = new Certificate[++len];
                    certificates[i++] = new OpenSSLX509Certificate(clientCert);
                }
                else {
                    certificates = new Certificate[len];
                }
                if (chain != null) {
                    int a = 0;
                    while (i < certificates.length) {
                        certificates[i] = new OpenSSLX509Certificate(chain[a++]);
                        ++i;
                    }
                }
                c = (OpenSSLEngine.this.peerCerts = certificates);
            }
            return c;
        }
        
        @Override
        public Certificate[] getLocalCertificates() {
            return OpenSSLEngine.EMPTY_CERTIFICATES;
        }
        
        @Deprecated
        @Override
        public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
            X509Certificate[] c = OpenSSLEngine.this.x509PeerCerts;
            if (c == null) {
                final byte[][] chain;
                synchronized (OpenSSLEngine.this) {
                    if (OpenSSLEngine.this.destroyed || SSL.isInInit(OpenSSLEngine.this.ssl) != 0) {
                        throw new SSLPeerUnverifiedException(OpenSSLEngine.sm.getString("engine.unverifiedPeer"));
                    }
                    chain = SSL.getPeerCertChain(OpenSSLEngine.this.ssl);
                }
                if (chain == null) {
                    throw new SSLPeerUnverifiedException(OpenSSLEngine.sm.getString("engine.unverifiedPeer"));
                }
                final X509Certificate[] peerCerts = new X509Certificate[chain.length];
                for (int i = 0; i < peerCerts.length; ++i) {
                    try {
                        peerCerts[i] = X509Certificate.getInstance(chain[i]);
                    }
                    catch (final CertificateException e) {
                        throw new IllegalStateException(e);
                    }
                }
                c = (OpenSSLEngine.this.x509PeerCerts = peerCerts);
            }
            return c;
        }
        
        @Override
        public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
            final Certificate[] peer = this.getPeerCertificates();
            if (peer == null || peer.length == 0) {
                return null;
            }
            return this.principal(peer);
        }
        
        @Override
        public Principal getLocalPrincipal() {
            final Certificate[] local = this.getLocalCertificates();
            if (local == null || local.length == 0) {
                return null;
            }
            return this.principal(local);
        }
        
        private Principal principal(final Certificate[] certs) {
            return ((java.security.cert.X509Certificate)certs[0]).getIssuerX500Principal();
        }
        
        @Override
        public String getCipherSuite() {
            if (OpenSSLEngine.this.cipher == null) {
                final String ciphers;
                synchronized (OpenSSLEngine.this) {
                    if (!OpenSSLEngine.this.handshakeFinished) {
                        return "SSL_NULL_WITH_NULL_NULL";
                    }
                    if (OpenSSLEngine.this.destroyed) {
                        return "SSL_NULL_WITH_NULL_NULL";
                    }
                    ciphers = SSL.getCipherForSSL(OpenSSLEngine.this.ssl);
                }
                final String c = OpenSSLCipherConfigurationParser.openSSLToJsse(ciphers);
                if (c != null) {
                    OpenSSLEngine.this.cipher = c;
                }
            }
            return OpenSSLEngine.this.cipher;
        }
        
        @Override
        public String getProtocol() {
            String applicationProtocol = OpenSSLEngine.this.applicationProtocol;
            if (applicationProtocol == null) {
                synchronized (OpenSSLEngine.this) {
                    if (!OpenSSLEngine.this.destroyed) {
                        applicationProtocol = SSL.getNextProtoNegotiated(OpenSSLEngine.this.ssl);
                    }
                }
                if (applicationProtocol == null) {
                    applicationProtocol = OpenSSLEngine.this.fallbackApplicationProtocol;
                }
                if (applicationProtocol != null) {
                    OpenSSLEngine.this.applicationProtocol = applicationProtocol.replace(':', '_');
                }
                else {
                    OpenSSLEngine.this.applicationProtocol = (applicationProtocol = "");
                }
            }
            String version = null;
            synchronized (OpenSSLEngine.this) {
                if (!OpenSSLEngine.this.destroyed) {
                    version = SSL.getVersion(OpenSSLEngine.this.ssl);
                }
            }
            if (applicationProtocol.isEmpty()) {
                return version;
            }
            return version + ':' + applicationProtocol;
        }
        
        @Override
        public String getPeerHost() {
            return null;
        }
        
        @Override
        public int getPeerPort() {
            return 0;
        }
        
        @Override
        public int getPacketBufferSize() {
            return 18713;
        }
        
        @Override
        public int getApplicationBufferSize() {
            return 16384;
        }
    }
}
