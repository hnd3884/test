package org.apache.tomcat.util.net;

import org.apache.juli.logging.LogFactory;
import javax.net.ssl.SSLSession;
import java.nio.channels.SelectionKey;
import java.net.SocketTimeoutException;
import java.io.EOFException;
import org.apache.tomcat.util.net.openssl.ciphers.Cipher;
import java.util.Collections;
import javax.net.ssl.SSLException;
import org.apache.tomcat.util.compat.JreCompat;
import java.nio.channels.Selector;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import java.io.IOException;
import java.util.HashMap;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngine;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class SecureNioChannel extends NioChannel
{
    private static final Log log;
    private static final StringManager sm;
    private static final int DEFAULT_NET_BUFFER_SIZE = 16921;
    protected ByteBuffer netInBuffer;
    protected ByteBuffer netOutBuffer;
    protected SSLEngine sslEngine;
    protected boolean sniComplete;
    protected boolean handshakeComplete;
    protected SSLEngineResult.HandshakeStatus handshakeStatus;
    protected boolean closed;
    protected boolean closing;
    private final Map<String, List<String>> additionalTlsAttributes;
    protected NioSelectorPool pool;
    private final NioEndpoint endpoint;
    
    public SecureNioChannel(final SocketChannel channel, final SocketBufferHandler bufHandler, final NioSelectorPool pool, final NioEndpoint endpoint) {
        super(channel, bufHandler);
        this.sniComplete = false;
        this.handshakeComplete = false;
        this.closed = false;
        this.closing = false;
        this.additionalTlsAttributes = new HashMap<String, List<String>>();
        if (endpoint.getSocketProperties().getDirectSslBuffer()) {
            this.netInBuffer = ByteBuffer.allocateDirect(16921);
            this.netOutBuffer = ByteBuffer.allocateDirect(16921);
        }
        else {
            this.netInBuffer = ByteBuffer.allocate(16921);
            this.netOutBuffer = ByteBuffer.allocate(16921);
        }
        this.pool = pool;
        this.endpoint = endpoint;
    }
    
    @Override
    public void reset() throws IOException {
        super.reset();
        this.sslEngine = null;
        this.sniComplete = false;
        this.handshakeComplete = false;
        this.closed = false;
        this.closing = false;
        this.netInBuffer.clear();
    }
    
    @Override
    public void free() {
        super.free();
        if (this.endpoint.getSocketProperties().getDirectSslBuffer()) {
            ByteBufferUtils.cleanDirectBuffer(this.netInBuffer);
            ByteBufferUtils.cleanDirectBuffer(this.netOutBuffer);
        }
    }
    
    @Override
    public boolean flush(final boolean block, final Selector s, final long timeout) throws IOException {
        if (!block) {
            this.flush(this.netOutBuffer);
        }
        else {
            this.pool.write(this.netOutBuffer, this, s, timeout, block);
        }
        return !this.netOutBuffer.hasRemaining();
    }
    
    protected boolean flush(final ByteBuffer buf) throws IOException {
        final int remaining = buf.remaining();
        return remaining <= 0 || this.sc.write(buf) >= remaining;
    }
    
    @Override
    public int handshake(final boolean read, final boolean write) throws IOException {
        if (this.handshakeComplete) {
            return 0;
        }
        if (!this.sniComplete) {
            final int sniResult = this.processSNI();
            if (sniResult != 0) {
                return sniResult;
            }
            this.sniComplete = true;
        }
        if (!this.flush(this.netOutBuffer)) {
            return 4;
        }
        SSLEngineResult handshake = null;
        while (!this.handshakeComplete) {
            switch (this.handshakeStatus) {
                case NOT_HANDSHAKING: {
                    throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.notHandshaking"));
                }
                case FINISHED: {
                    if (this.endpoint.hasNegotiableProtocols()) {
                        if (this.sslEngine instanceof SSLUtil.ProtocolInfo) {
                            this.socketWrapper.setNegotiatedProtocol(((SSLUtil.ProtocolInfo)this.sslEngine).getNegotiatedProtocol());
                        }
                        else if (JreCompat.isAlpnSupported()) {
                            this.socketWrapper.setNegotiatedProtocol(JreCompat.getInstance().getApplicationProtocol(this.sslEngine));
                        }
                    }
                    this.handshakeComplete = !this.netOutBuffer.hasRemaining();
                    return this.handshakeComplete ? 0 : 4;
                }
                case NEED_WRAP: {
                    try {
                        handshake = this.handshakeWrap(write);
                    }
                    catch (final SSLException e) {
                        if (SecureNioChannel.log.isDebugEnabled()) {
                            SecureNioChannel.log.debug((Object)SecureNioChannel.sm.getString("channel.nio.ssl.wrapException"), (Throwable)e);
                        }
                        handshake = this.handshakeWrap(write);
                    }
                    if (handshake.getStatus() == SSLEngineResult.Status.OK) {
                        if (this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                            this.handshakeStatus = this.tasks();
                        }
                        if (this.handshakeStatus != SSLEngineResult.HandshakeStatus.NEED_UNWRAP || !this.flush(this.netOutBuffer)) {
                            return 4;
                        }
                    }
                    else {
                        if (handshake.getStatus() == SSLEngineResult.Status.CLOSED) {
                            this.flush(this.netOutBuffer);
                            return -1;
                        }
                        throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.unexpectedStatusDuringWrap", new Object[] { handshake.getStatus() }));
                    }
                    break;
                }
                case NEED_UNWRAP: {
                    handshake = this.handshakeUnwrap(read);
                    if (handshake.getStatus() == SSLEngineResult.Status.OK) {
                        if (this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                            this.handshakeStatus = this.tasks();
                            continue;
                        }
                        continue;
                    }
                    else {
                        if (handshake.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                            return 1;
                        }
                        throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.unexpectedStatusDuringWrap", new Object[] { handshake.getStatus() }));
                    }
                    break;
                }
                case NEED_TASK: {
                    this.handshakeStatus = this.tasks();
                    continue;
                }
                default: {
                    throw new IllegalStateException(SecureNioChannel.sm.getString("channel.nio.ssl.invalidStatus", new Object[] { this.handshakeStatus }));
                }
            }
        }
        return 0;
    }
    
    private int processSNI() throws IOException {
        final int bytesRead = this.sc.read(this.netInBuffer);
        if (bytesRead == -1) {
            return -1;
        }
        TLSClientHelloExtractor extractor;
        for (extractor = new TLSClientHelloExtractor(this.netInBuffer); extractor.getResult() == TLSClientHelloExtractor.ExtractorResult.UNDERFLOW && this.netInBuffer.capacity() < this.endpoint.getSniParseLimit(); extractor = new TLSClientHelloExtractor(this.netInBuffer)) {
            final int newLimit = Math.min(this.netInBuffer.capacity() * 2, this.endpoint.getSniParseLimit());
            SecureNioChannel.log.info((Object)SecureNioChannel.sm.getString("channel.nio.ssl.expandNetInBuffer", new Object[] { Integer.toString(newLimit) }));
            this.netInBuffer = ByteBufferUtils.expand(this.netInBuffer, newLimit);
            this.sc.read(this.netInBuffer);
        }
        String hostName = null;
        List<Cipher> clientRequestedCiphers = null;
        List<String> clientRequestedApplicationProtocols = null;
        switch (extractor.getResult()) {
            case COMPLETE: {
                hostName = extractor.getSNIValue();
                clientRequestedApplicationProtocols = extractor.getClientRequestedApplicationProtocols();
            }
            case NOT_PRESENT: {
                clientRequestedCiphers = extractor.getClientRequestedCiphers();
                break;
            }
            case NEED_READ: {
                return 1;
            }
            case UNDERFLOW: {
                if (SecureNioChannel.log.isDebugEnabled()) {
                    SecureNioChannel.log.debug((Object)SecureNioChannel.sm.getString("channel.nio.ssl.sniDefault"));
                }
                hostName = this.endpoint.getDefaultSSLHostConfigName();
                clientRequestedCiphers = Collections.emptyList();
                break;
            }
            case NON_SECURE: {
                this.netOutBuffer.clear();
                this.netOutBuffer.put(TLSClientHelloExtractor.USE_TLS_RESPONSE);
                this.netOutBuffer.flip();
                this.flushOutbound();
                throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.foundHttp"));
            }
        }
        if (SecureNioChannel.log.isDebugEnabled()) {
            SecureNioChannel.log.debug((Object)SecureNioChannel.sm.getString("channel.nio.ssl.sniHostName", new Object[] { this.sc, hostName }));
        }
        this.sslEngine = this.endpoint.createSSLEngine(hostName, clientRequestedCiphers, clientRequestedApplicationProtocols);
        this.additionalTlsAttributes.put("org.apache.tomcat.util.net.secure_requested_protocol_versions", extractor.getClientRequestedProtocols());
        this.additionalTlsAttributes.put("org.apache.tomcat.util.net.secure_requested_ciphers", extractor.getClientRequestedCipherNames());
        this.getBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
        if (this.netOutBuffer.capacity() < this.sslEngine.getSession().getApplicationBufferSize()) {
            SecureNioChannel.log.info((Object)SecureNioChannel.sm.getString("channel.nio.ssl.expandNetOutBuffer", new Object[] { Integer.toString(this.sslEngine.getSession().getApplicationBufferSize()) }));
        }
        this.netInBuffer = ByteBufferUtils.expand(this.netInBuffer, this.sslEngine.getSession().getPacketBufferSize());
        (this.netOutBuffer = ByteBufferUtils.expand(this.netOutBuffer, this.sslEngine.getSession().getPacketBufferSize())).position(0);
        this.netOutBuffer.limit(0);
        this.sslEngine.beginHandshake();
        this.handshakeStatus = this.sslEngine.getHandshakeStatus();
        return 0;
    }
    
    public void rehandshake(final long timeout) throws IOException {
        if (this.netInBuffer.position() > 0 && this.netInBuffer.position() < this.netInBuffer.limit()) {
            throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.netInputNotEmpty"));
        }
        if (this.netOutBuffer.position() > 0 && this.netOutBuffer.position() < this.netOutBuffer.limit()) {
            throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.netOutputNotEmpty"));
        }
        if (!this.getBufHandler().isReadBufferEmpty()) {
            throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.appInputNotEmpty"));
        }
        if (!this.getBufHandler().isWriteBufferEmpty()) {
            throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.appOutputNotEmpty"));
        }
        this.handshakeComplete = false;
        boolean isReadable = false;
        boolean isWriteable = false;
        boolean handshaking = true;
        Selector selector = null;
        SelectionKey key = null;
        try {
            this.sslEngine.beginHandshake();
            this.handshakeStatus = this.sslEngine.getHandshakeStatus();
            while (handshaking) {
                final int hsStatus = this.handshake(isReadable, isWriteable);
                switch (hsStatus) {
                    case -1: {
                        throw new EOFException(SecureNioChannel.sm.getString("channel.nio.ssl.eofDuringHandshake"));
                    }
                    case 0: {
                        handshaking = false;
                        continue;
                    }
                    default: {
                        final long now = System.currentTimeMillis();
                        if (selector == null) {
                            selector = Selector.open();
                            key = this.getIOChannel().register(selector, hsStatus);
                        }
                        else {
                            key.interestOps(hsStatus);
                        }
                        final int keyCount = selector.select(timeout);
                        if (keyCount == 0 && System.currentTimeMillis() - now >= timeout) {
                            throw new SocketTimeoutException(SecureNioChannel.sm.getString("channel.nio.ssl.timeoutDuringHandshake"));
                        }
                        isReadable = key.isReadable();
                        isWriteable = key.isWritable();
                        continue;
                    }
                }
            }
        }
        catch (final IOException x) {
            this.closeSilently();
            throw x;
        }
        catch (final Exception cx) {
            this.closeSilently();
            final IOException x2 = new IOException(cx);
            throw x2;
        }
        finally {
            if (key != null) {
                try {
                    key.cancel();
                }
                catch (final Exception ex) {}
            }
            if (selector != null) {
                try {
                    selector.close();
                }
                catch (final Exception ex2) {}
            }
        }
    }
    
    protected SSLEngineResult.HandshakeStatus tasks() {
        Runnable r = null;
        while ((r = this.sslEngine.getDelegatedTask()) != null) {
            r.run();
        }
        return this.sslEngine.getHandshakeStatus();
    }
    
    protected SSLEngineResult handshakeWrap(final boolean doWrite) throws IOException {
        this.netOutBuffer.clear();
        this.getBufHandler().configureWriteBufferForRead();
        final SSLEngineResult result = this.sslEngine.wrap(this.getBufHandler().getWriteBuffer(), this.netOutBuffer);
        this.netOutBuffer.flip();
        this.handshakeStatus = result.getHandshakeStatus();
        if (doWrite) {
            this.flush(this.netOutBuffer);
        }
        return result;
    }
    
    protected SSLEngineResult handshakeUnwrap(final boolean doread) throws IOException {
        if (this.netInBuffer.position() == this.netInBuffer.limit()) {
            this.netInBuffer.clear();
        }
        if (doread) {
            final int read = this.sc.read(this.netInBuffer);
            if (read == -1) {
                throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.eofDuringHandshake"));
            }
        }
        boolean cont = false;
        SSLEngineResult result;
        do {
            this.netInBuffer.flip();
            this.getBufHandler().configureReadBufferForWrite();
            result = this.sslEngine.unwrap(this.netInBuffer, this.getBufHandler().getReadBuffer());
            this.netInBuffer.compact();
            this.handshakeStatus = result.getHandshakeStatus();
            if (result.getStatus() == SSLEngineResult.Status.OK && result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                this.handshakeStatus = this.tasks();
            }
            cont = (result.getStatus() == SSLEngineResult.Status.OK && this.handshakeStatus == SSLEngineResult.HandshakeStatus.NEED_UNWRAP);
        } while (cont);
        return result;
    }
    
    public SSLSupport getSSLSupport() {
        if (this.sslEngine != null) {
            final SSLSession session = this.sslEngine.getSession();
            return this.endpoint.getSslImplementation().getSSLSupport(session, this.additionalTlsAttributes);
        }
        return null;
    }
    
    @Override
    public void close() throws IOException {
        if (this.closing) {
            return;
        }
        this.closing = true;
        if (this.sslEngine == null) {
            this.netOutBuffer.clear();
            this.closed = true;
            return;
        }
        this.sslEngine.closeOutbound();
        if (!this.flush(this.netOutBuffer)) {
            throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.remainingDataDuringClose"));
        }
        this.netOutBuffer.clear();
        final SSLEngineResult handshake = this.sslEngine.wrap(this.getEmptyBuf(), this.netOutBuffer);
        if (handshake.getStatus() != SSLEngineResult.Status.CLOSED) {
            throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.invalidCloseState"));
        }
        this.netOutBuffer.flip();
        this.flush(this.netOutBuffer);
        this.closed = (!this.netOutBuffer.hasRemaining() && handshake.getHandshakeStatus() != SSLEngineResult.HandshakeStatus.NEED_WRAP);
    }
    
    @Override
    public void close(final boolean force) throws IOException {
        try {
            this.close();
        }
        finally {
            if (force || this.closed) {
                this.closed = true;
                this.sc.socket().close();
                this.sc.close();
            }
        }
    }
    
    private void closeSilently() {
        try {
            this.close(true);
        }
        catch (final IOException ioe) {
            SecureNioChannel.log.debug((Object)SecureNioChannel.sm.getString("channel.nio.ssl.closeSilentError"), (Throwable)ioe);
        }
    }
    
    @Override
    public int read(ByteBuffer dst) throws IOException {
        if (this.closing || this.closed) {
            return -1;
        }
        if (!this.handshakeComplete) {
            throw new IllegalStateException(SecureNioChannel.sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        final int netread = this.sc.read(this.netInBuffer);
        if (netread == -1) {
            return -1;
        }
        int read = 0;
        do {
            this.netInBuffer.flip();
            final SSLEngineResult unwrap = this.sslEngine.unwrap(this.netInBuffer, dst);
            this.netInBuffer.compact();
            if (unwrap.getStatus() == SSLEngineResult.Status.OK || unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                read += unwrap.bytesProduced();
                if (unwrap.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    this.tasks();
                }
                if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                    break;
                }
                continue;
            }
            else if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_OVERFLOW) {
                if (read > 0) {
                    break;
                }
                if (dst == this.getBufHandler().getReadBuffer()) {
                    this.getBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                    dst = this.getBufHandler().getReadBuffer();
                }
                else {
                    if (dst != this.getAppReadBufHandler().getByteBuffer()) {
                        throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.unwrapFailResize", new Object[] { unwrap.getStatus() }));
                    }
                    this.getAppReadBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                    dst = this.getAppReadBufHandler().getByteBuffer();
                }
            }
            else {
                if (unwrap.getStatus() == SSLEngineResult.Status.CLOSED && this.netInBuffer.position() == 0 && read > 0) {
                    continue;
                }
                throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.unwrapFail", new Object[] { unwrap.getStatus() }));
            }
        } while (this.netInBuffer.position() != 0);
        return read;
    }
    
    @Override
    public long read(ByteBuffer[] dsts, final int offset, int length) throws IOException {
        if (this.closing || this.closed) {
            return -1L;
        }
        if (!this.handshakeComplete) {
            throw new IllegalStateException(SecureNioChannel.sm.getString("channel.nio.ssl.incompleteHandshake"));
        }
        final int netread = this.sc.read(this.netInBuffer);
        if (netread == -1) {
            return -1L;
        }
        int read = 0;
        boolean processOverflow = false;
        do {
            boolean useOverflow = false;
            if (processOverflow) {
                useOverflow = true;
            }
            processOverflow = false;
            this.netInBuffer.flip();
            final SSLEngineResult unwrap = this.sslEngine.unwrap(this.netInBuffer, dsts, offset, length);
            this.netInBuffer.compact();
            if (unwrap.getStatus() == SSLEngineResult.Status.OK || unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                read += unwrap.bytesProduced();
                if (useOverflow) {
                    read -= this.getBufHandler().getReadBuffer().position();
                }
                if (unwrap.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                    this.tasks();
                }
                if (unwrap.getStatus() == SSLEngineResult.Status.BUFFER_UNDERFLOW) {
                    break;
                }
                continue;
            }
            else {
                if (unwrap.getStatus() != SSLEngineResult.Status.BUFFER_OVERFLOW) {
                    throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.unwrapFail", new Object[] { unwrap.getStatus() }));
                }
                if (read > 0) {
                    break;
                }
                final ByteBuffer readBuffer = this.getBufHandler().getReadBuffer();
                boolean found = false;
                boolean resized = true;
                for (int i = 0; i < length; ++i) {
                    if (dsts[offset + i] == this.getBufHandler().getReadBuffer()) {
                        this.getBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                        if (dsts[offset + i] == this.getBufHandler().getReadBuffer()) {
                            resized = false;
                        }
                        dsts[offset + i] = this.getBufHandler().getReadBuffer();
                        found = true;
                    }
                    else if (this.getAppReadBufHandler() != null && dsts[offset + i] == this.getAppReadBufHandler().getByteBuffer()) {
                        this.getAppReadBufHandler().expand(this.sslEngine.getSession().getApplicationBufferSize());
                        if (dsts[offset + i] == this.getAppReadBufHandler().getByteBuffer()) {
                            resized = false;
                        }
                        dsts[offset + i] = this.getAppReadBufHandler().getByteBuffer();
                        found = true;
                    }
                }
                if (found) {
                    if (!resized) {
                        throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.unwrapFail", new Object[] { unwrap.getStatus() }));
                    }
                    continue;
                }
                else {
                    final ByteBuffer[] dsts2 = new ByteBuffer[dsts.length + 1];
                    int dstOffset = 0;
                    for (int j = 0; j < dsts.length + 1; ++j) {
                        if (j == offset + length) {
                            dsts2[j] = readBuffer;
                            dstOffset = -1;
                        }
                        else {
                            dsts2[j] = dsts[j + dstOffset];
                        }
                    }
                    dsts = dsts2;
                    ++length;
                    this.getBufHandler().configureReadBufferForWrite();
                    processOverflow = true;
                }
            }
        } while (this.netInBuffer.position() != 0 || processOverflow);
        return read;
    }
    
    @Override
    public int write(final ByteBuffer src) throws IOException {
        this.checkInterruptStatus();
        if (src == this.netOutBuffer) {
            final int written = this.sc.write(src);
            return written;
        }
        if (this.closing || this.closed) {
            throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.closing"));
        }
        if (!this.flush(this.netOutBuffer)) {
            return 0;
        }
        this.netOutBuffer.clear();
        final SSLEngineResult result = this.sslEngine.wrap(src, this.netOutBuffer);
        final int written2 = result.bytesConsumed();
        this.netOutBuffer.flip();
        if (result.getStatus() == SSLEngineResult.Status.OK) {
            if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                this.tasks();
            }
            this.flush(this.netOutBuffer);
            return written2;
        }
        throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.wrapFail", new Object[] { result.getStatus() }));
    }
    
    @Override
    public long write(final ByteBuffer[] srcs, final int offset, final int length) throws IOException {
        this.checkInterruptStatus();
        if (this.closing || this.closed) {
            throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.closing"));
        }
        if (!this.flush(this.netOutBuffer)) {
            return 0L;
        }
        this.netOutBuffer.clear();
        final SSLEngineResult result = this.sslEngine.wrap(srcs, offset, length, this.netOutBuffer);
        final int written = result.bytesConsumed();
        this.netOutBuffer.flip();
        if (result.getStatus() == SSLEngineResult.Status.OK) {
            if (result.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_TASK) {
                this.tasks();
            }
            this.flush(this.netOutBuffer);
            return written;
        }
        throw new IOException(SecureNioChannel.sm.getString("channel.nio.ssl.wrapFail", new Object[] { result.getStatus() }));
    }
    
    @Override
    public int getOutboundRemaining() {
        return this.netOutBuffer.remaining();
    }
    
    @Override
    public boolean flushOutbound() throws IOException {
        final int remaining = this.netOutBuffer.remaining();
        this.flush(this.netOutBuffer);
        final int remaining2 = this.netOutBuffer.remaining();
        return remaining2 < remaining;
    }
    
    @Override
    public boolean isHandshakeComplete() {
        return this.handshakeComplete;
    }
    
    @Override
    public boolean isClosing() {
        return this.closing;
    }
    
    public SSLEngine getSslEngine() {
        return this.sslEngine;
    }
    
    public ByteBuffer getEmptyBuf() {
        return SecureNioChannel.emptyBuf;
    }
    
    static {
        log = LogFactory.getLog((Class)SecureNioChannel.class);
        sm = StringManager.getManager((Class)SecureNioChannel.class);
    }
}
