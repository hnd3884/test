package org.apache.coyote.http2;

import org.apache.coyote.ActionCode;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.net.WriteBuffer;
import org.apache.juli.logging.LogFactory;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import org.apache.coyote.Constants;
import org.apache.coyote.http11.HttpOutputBuffer;
import java.nio.ByteBuffer;
import org.apache.coyote.http11.OutputFilter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.io.IOException;
import org.apache.coyote.CloseNowException;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.parser.Host;
import org.apache.coyote.OutputBuffer;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Response;
import org.apache.coyote.Request;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

class Stream extends AbstractNonZeroStream implements HpackDecoder.HeaderEmitter
{
    private static final Log log;
    private static final StringManager sm;
    private static final int HEADER_STATE_START = 0;
    private static final int HEADER_STATE_PSEUDO = 1;
    private static final int HEADER_STATE_REGULAR = 2;
    private static final int HEADER_STATE_TRAILER = 3;
    private static final MimeHeaders ACK_HEADERS;
    private static final Integer HTTP_UPGRADE_STREAM;
    private volatile long contentLengthReceived;
    private final Http2UpgradeHandler handler;
    private final WindowAllocationManager allocationManager;
    private final Request coyoteRequest;
    private final Response coyoteResponse;
    private final StreamInputBuffer inputBuffer;
    private final StreamOutputBuffer streamOutputBuffer;
    private final Http2OutputBuffer http2OutputBuffer;
    private int headerState;
    private StreamException headerException;
    private volatile StringBuilder cookieHeader;
    private Object pendingWindowUpdateForStreamLock;
    private int pendingWindowUpdateForStream;
    
    Stream(final Integer identifier, final Http2UpgradeHandler handler) {
        this(identifier, handler, null);
    }
    
    Stream(final Integer identifier, final Http2UpgradeHandler handler, final Request coyoteRequest) {
        super(handler.getConnectionId(), identifier);
        this.contentLengthReceived = 0L;
        this.allocationManager = new WindowAllocationManager(this);
        this.coyoteResponse = new Response();
        this.streamOutputBuffer = new StreamOutputBuffer();
        this.http2OutputBuffer = new Http2OutputBuffer(this.coyoteResponse, this.streamOutputBuffer);
        this.headerState = 0;
        this.headerException = null;
        this.cookieHeader = null;
        this.pendingWindowUpdateForStreamLock = new Object();
        this.pendingWindowUpdateForStream = 0;
        (this.handler = handler).addChild(this);
        this.setWindowSize(handler.getRemoteSettings().getInitialWindowSize());
        if (coyoteRequest == null) {
            this.coyoteRequest = new Request();
            this.inputBuffer = new StreamInputBuffer();
            this.coyoteRequest.setInputBuffer(this.inputBuffer);
        }
        else {
            this.coyoteRequest = coyoteRequest;
            this.inputBuffer = null;
            this.state.receivedStartOfHeaders();
            if (Stream.HTTP_UPGRADE_STREAM.equals(identifier)) {
                try {
                    this.prepareRequest();
                }
                catch (final IllegalArgumentException iae) {
                    this.coyoteResponse.setStatus(400);
                    this.coyoteResponse.setError();
                }
            }
            this.state.receivedEndOfStream();
        }
        this.coyoteRequest.setSendfile(false);
        this.coyoteResponse.setOutputBuffer(this.http2OutputBuffer);
        this.coyoteRequest.setResponse(this.coyoteResponse);
        this.coyoteRequest.protocol().setString("HTTP/2.0");
        if (this.coyoteRequest.getStartTime() < 0L) {
            this.coyoteRequest.setStartTime(System.currentTimeMillis());
        }
    }
    
    private void prepareRequest() {
        final MessageBytes hostValueMB = this.coyoteRequest.getMimeHeaders().getUniqueValue("host");
        if (hostValueMB == null) {
            throw new IllegalArgumentException();
        }
        hostValueMB.toBytes();
        final ByteChunk valueBC = hostValueMB.getByteChunk();
        final byte[] valueB = valueBC.getBytes();
        int valueL = valueBC.getLength();
        final int valueS = valueBC.getStart();
        final int colonPos = Host.parse(hostValueMB);
        if (colonPos != -1) {
            int port = 0;
            for (int i = colonPos + 1; i < valueL; ++i) {
                final char c = (char)valueB[i + valueS];
                if (c < '0' || c > '9') {
                    throw new IllegalArgumentException();
                }
                port = port * 10 + c - 48;
            }
            this.coyoteRequest.setServerPort(port);
            valueL = colonPos;
        }
        final char[] hostNameC = new char[valueL];
        for (int i = 0; i < valueL; ++i) {
            hostNameC[i] = (char)valueB[i + valueS];
        }
        this.coyoteRequest.serverName().setChars(hostNameC, 0, valueL);
    }
    
    final void receiveReset(final long errorCode) {
        if (Stream.log.isDebugEnabled()) {
            Stream.log.debug((Object)Stream.sm.getString("stream.reset.receive", new Object[] { this.getConnectionId(), this.getIdAsString(), Long.toString(errorCode) }));
        }
        this.state.receivedReset();
        if (this.inputBuffer != null) {
            this.inputBuffer.receiveReset();
        }
        this.cancelAllocationRequests();
    }
    
    final void cancelAllocationRequests() {
        this.allocationManager.notifyAny();
    }
    
    @Override
    final synchronized void incrementWindowSize(final int windowSizeIncrement) throws Http2Exception {
        final boolean notify = this.getWindowSize() < 1L;
        super.incrementWindowSize(windowSizeIncrement);
        if (notify && this.getWindowSize() > 0L) {
            this.allocationManager.notifyStream();
        }
    }
    
    private synchronized int reserveWindowSize(final int reservation, final boolean block) throws IOException {
        long windowSize = this.getWindowSize();
        while (windowSize < 1L) {
            if (!this.canWrite()) {
                throw new CloseNowException(Stream.sm.getString("stream.notWritable", new Object[] { this.getConnectionId(), this.getIdAsString() }));
            }
            if (block) {
                try {
                    final long writeTimeout = this.handler.getProtocol().getStreamWriteTimeout();
                    this.allocationManager.waitForStream(writeTimeout);
                    windowSize = this.getWindowSize();
                    if (windowSize != 0L) {
                        continue;
                    }
                    this.doStreamCancel(Stream.sm.getString("stream.writeTimeout"), Http2Error.ENHANCE_YOUR_CALM);
                    continue;
                }
                catch (final InterruptedException e) {
                    throw new IOException(e);
                }
            }
            this.allocationManager.waitForStreamNonBlocking();
            return 0;
        }
        int allocation;
        if (windowSize < reservation) {
            allocation = (int)windowSize;
        }
        else {
            allocation = reservation;
        }
        this.decrementWindowSize(allocation);
        return allocation;
    }
    
    void doStreamCancel(final String msg, final Http2Error error) throws CloseNowException {
        final StreamException se = new StreamException(msg, error, this.getIdAsInt());
        this.streamOutputBuffer.closed = true;
        this.coyoteResponse.setError();
        this.coyoteResponse.setErrorReported();
        this.streamOutputBuffer.reset = se;
        throw new CloseNowException(msg, se);
    }
    
    void waitForConnectionAllocation(final long timeout) throws InterruptedException {
        this.allocationManager.waitForConnection(timeout);
    }
    
    void waitForConnectionAllocationNonBlocking() {
        this.allocationManager.waitForConnectionNonBlocking();
    }
    
    void notifyConnection() {
        this.allocationManager.notifyConnection();
    }
    
    @Override
    public final void emitHeader(final String name, final String value) throws HpackException {
        if (Stream.log.isDebugEnabled()) {
            Stream.log.debug((Object)Stream.sm.getString("stream.header.debug", new Object[] { this.getConnectionId(), this.getIdAsString(), name, value }));
        }
        if (!name.toLowerCase(Locale.US).equals(name)) {
            throw new HpackException(Stream.sm.getString("stream.header.case", new Object[] { this.getConnectionId(), this.getIdAsString(), name }));
        }
        if ("connection".equals(name)) {
            throw new HpackException(Stream.sm.getString("stream.header.connection", new Object[] { this.getConnectionId(), this.getIdAsString() }));
        }
        if ("te".equals(name) && !"trailers".equals(value)) {
            throw new HpackException(Stream.sm.getString("stream.header.te", new Object[] { this.getConnectionId(), this.getIdAsString(), value }));
        }
        if (this.headerException != null) {
            return;
        }
        if (name.length() == 0) {
            throw new HpackException(Stream.sm.getString("stream.header.empty", new Object[] { this.getConnectionId(), this.getIdAsString() }));
        }
        final boolean pseudoHeader = name.charAt(0) == ':';
        if (pseudoHeader && this.headerState != 1) {
            this.headerException = new StreamException(Stream.sm.getString("stream.header.unexpectedPseudoHeader", new Object[] { this.getConnectionId(), this.getIdAsString(), name }), Http2Error.PROTOCOL_ERROR, this.getIdAsInt());
            return;
        }
        if (this.headerState == 1 && !pseudoHeader) {
            this.headerState = 2;
        }
        switch (name) {
            case ":method": {
                if (this.coyoteRequest.method().isNull()) {
                    this.coyoteRequest.method().setString(value);
                    break;
                }
                throw new HpackException(Stream.sm.getString("stream.header.duplicate", new Object[] { this.getConnectionId(), this.getIdAsString(), ":method" }));
            }
            case ":scheme": {
                if (this.coyoteRequest.scheme().isNull()) {
                    this.coyoteRequest.scheme().setString(value);
                    break;
                }
                throw new HpackException(Stream.sm.getString("stream.header.duplicate", new Object[] { this.getConnectionId(), this.getIdAsString(), ":scheme" }));
            }
            case ":path": {
                if (!this.coyoteRequest.requestURI().isNull()) {
                    throw new HpackException(Stream.sm.getString("stream.header.duplicate", new Object[] { this.getConnectionId(), this.getIdAsString(), ":path" }));
                }
                if (value.length() == 0) {
                    throw new HpackException(Stream.sm.getString("stream.header.noPath", new Object[] { this.getConnectionId(), this.getIdAsString() }));
                }
                final int queryStart = value.indexOf(63);
                String uri;
                if (queryStart == -1) {
                    uri = value;
                }
                else {
                    uri = value.substring(0, queryStart);
                    final String query = value.substring(queryStart + 1);
                    this.coyoteRequest.queryString().setString(query);
                }
                final byte[] uriBytes = uri.getBytes(StandardCharsets.ISO_8859_1);
                this.coyoteRequest.requestURI().setBytes(uriBytes, 0, uriBytes.length);
                break;
            }
            case ":authority": {
                if (this.coyoteRequest.serverName().isNull()) {
                    int i;
                    try {
                        i = Host.parse(value);
                    }
                    catch (final IllegalArgumentException iae) {
                        throw new HpackException(Stream.sm.getString("stream.header.invalid", new Object[] { this.getConnectionId(), this.getIdAsString(), ":authority", value }));
                    }
                    if (i > -1) {
                        this.coyoteRequest.serverName().setString(value.substring(0, i));
                        this.coyoteRequest.setServerPort(Integer.parseInt(value.substring(i + 1)));
                    }
                    else {
                        this.coyoteRequest.serverName().setString(value);
                    }
                    break;
                }
                throw new HpackException(Stream.sm.getString("stream.header.duplicate", new Object[] { this.getConnectionId(), this.getIdAsString(), ":authority" }));
            }
            case "cookie": {
                if (this.cookieHeader == null) {
                    this.cookieHeader = new StringBuilder();
                }
                else {
                    this.cookieHeader.append("; ");
                }
                this.cookieHeader.append(value);
                break;
            }
            default: {
                if (this.headerState == 3 && !this.handler.isTrailerHeaderAllowed(name)) {
                    break;
                }
                if ("expect".equals(name) && "100-continue".equals(value)) {
                    this.coyoteRequest.setExpectation(true);
                }
                if (pseudoHeader) {
                    this.headerException = new StreamException(Stream.sm.getString("stream.header.unknownPseudoHeader", new Object[] { this.getConnectionId(), this.getIdAsString(), name }), Http2Error.PROTOCOL_ERROR, this.getIdAsInt());
                }
                this.coyoteRequest.getMimeHeaders().addValue(name).setString(value);
                break;
            }
        }
    }
    
    @Override
    public void setHeaderException(final StreamException streamException) {
        if (this.headerException == null) {
            this.headerException = streamException;
        }
    }
    
    @Override
    public void validateHeaders() throws StreamException {
        if (this.headerException == null) {
            return;
        }
        throw this.headerException;
    }
    
    final boolean receivedEndOfHeaders() throws ConnectionException {
        if (this.coyoteRequest.method().isNull() || this.coyoteRequest.scheme().isNull() || this.coyoteRequest.requestURI().isNull()) {
            throw new ConnectionException(Stream.sm.getString("stream.header.required", new Object[] { this.getConnectionId(), this.getIdAsString() }), Http2Error.PROTOCOL_ERROR);
        }
        if (this.cookieHeader != null) {
            this.coyoteRequest.getMimeHeaders().addValue("cookie").setString(this.cookieHeader.toString());
        }
        return this.headerState == 2 || this.headerState == 1;
    }
    
    final void writeHeaders() throws IOException {
        final boolean endOfStream = this.streamOutputBuffer.hasNoBody();
        this.handler.writeHeaders(this, 0, this.coyoteResponse.getMimeHeaders(), endOfStream, 1024);
    }
    
    final void addOutputFilter(final OutputFilter filter) {
        this.http2OutputBuffer.addFilter(filter);
    }
    
    void writeAck() throws IOException {
        this.handler.writeHeaders(this, 0, Stream.ACK_HEADERS, false, 64);
    }
    
    @Override
    final String getConnectionId() {
        return this.handler.getConnectionId();
    }
    
    final Request getCoyoteRequest() {
        return this.coyoteRequest;
    }
    
    final Response getCoyoteResponse() {
        return this.coyoteResponse;
    }
    
    @Override
    final ByteBuffer getInputByteBuffer() {
        if (this.inputBuffer == null) {
            return Stream.ZERO_LENGTH_BYTEBUFFER;
        }
        return this.inputBuffer.getInBuffer();
    }
    
    final void receivedStartOfHeaders(final boolean headersEndStream) throws Http2Exception {
        if (this.headerState == 0) {
            this.headerState = 1;
            this.handler.getHpackDecoder().setMaxHeaderCount(this.handler.getMaxHeaderCount());
            this.handler.getHpackDecoder().setMaxHeaderSize(this.handler.getMaxHeaderSize());
        }
        else if (this.headerState == 1 || this.headerState == 2) {
            if (!headersEndStream) {
                throw new ConnectionException(Stream.sm.getString("stream.trailerHeader.noEndOfStream", new Object[] { this.getConnectionId(), this.getIdAsString() }), Http2Error.PROTOCOL_ERROR);
            }
            this.headerState = 3;
            this.handler.getHpackDecoder().setMaxHeaderCount(this.handler.getMaxTrailerCount());
            this.handler.getHpackDecoder().setMaxHeaderSize(this.handler.getMaxTrailerSize());
        }
        this.state.receivedStartOfHeaders();
    }
    
    @Override
    final void receivedData(final int payloadSize) throws Http2Exception {
        this.contentLengthReceived += payloadSize;
        final long contentLengthHeader = this.coyoteRequest.getContentLengthLong();
        if (contentLengthHeader > -1L && this.contentLengthReceived > contentLengthHeader) {
            throw new ConnectionException(Stream.sm.getString("stream.header.contentLength", new Object[] { this.getConnectionId(), this.getIdAsString(), contentLengthHeader, this.contentLengthReceived }), Http2Error.PROTOCOL_ERROR);
        }
    }
    
    final void receivedEndOfStream() throws ConnectionException {
        if (this.isContentLengthInconsistent()) {
            throw new ConnectionException(Stream.sm.getString("stream.header.contentLength", new Object[] { this.getConnectionId(), this.getIdAsString(), this.coyoteRequest.getContentLengthLong(), this.contentLengthReceived }), Http2Error.PROTOCOL_ERROR);
        }
        this.state.receivedEndOfStream();
        if (this.inputBuffer != null) {
            this.inputBuffer.notifyEof();
        }
    }
    
    final boolean isContentLengthInconsistent() {
        final long contentLengthHeader = this.coyoteRequest.getContentLengthLong();
        return contentLengthHeader > -1L && this.contentLengthReceived != contentLengthHeader;
    }
    
    final void sentHeaders() {
        this.state.sentHeaders();
    }
    
    final void sentEndOfStream() {
        this.streamOutputBuffer.endOfStreamSent = true;
        this.state.sentEndOfStream();
    }
    
    final boolean isReadyForWrite() {
        return this.streamOutputBuffer.isReady();
    }
    
    final boolean flush(final boolean block) throws IOException {
        return this.streamOutputBuffer.flush(block);
    }
    
    final StreamInputBuffer getInputBuffer() {
        return this.inputBuffer;
    }
    
    final HttpOutputBuffer getOutputBuffer() {
        return this.http2OutputBuffer;
    }
    
    final void sentPushPromise() {
        this.state.sentPushPromise();
    }
    
    final boolean isActive() {
        return this.state.isActive();
    }
    
    final boolean canWrite() {
        return this.state.canWrite();
    }
    
    final void closeIfIdle() {
        this.state.closeIfIdle();
    }
    
    final boolean isInputFinished() {
        return !this.state.isFrameTypePermitted(FrameType.DATA);
    }
    
    final void close(final Http2Exception http2Exception) {
        if (http2Exception instanceof StreamException) {
            try {
                final StreamException se = (StreamException)http2Exception;
                if (Stream.log.isDebugEnabled()) {
                    Stream.log.debug((Object)Stream.sm.getString("stream.reset.send", new Object[] { this.getConnectionId(), this.getIdAsString(), se.getError() }));
                }
                this.state.sendReset();
                this.handler.sendStreamReset(se);
                this.cancelAllocationRequests();
                if (this.inputBuffer != null) {
                    this.inputBuffer.swallowUnread();
                }
            }
            catch (final IOException ioe) {
                final ConnectionException ce = new ConnectionException(Stream.sm.getString("stream.reset.fail", new Object[] { this.getConnectionId(), this.getIdAsString() }), Http2Error.PROTOCOL_ERROR, ioe);
                this.handler.closeConnection(ce);
            }
        }
        else {
            this.handler.closeConnection(http2Exception);
        }
        if (this.inputBuffer != null) {
            this.inputBuffer.receiveReset();
        }
        this.recycle();
    }
    
    final void recycle() {
        if (Stream.log.isDebugEnabled()) {
            Stream.log.debug((Object)Stream.sm.getString("stream.recycle", new Object[] { this.getConnectionId(), this.getIdAsString() }));
        }
        final ByteBuffer inputByteBuffer = this.getInputByteBuffer();
        int remaining;
        if (inputByteBuffer == null) {
            remaining = 0;
        }
        else {
            remaining = inputByteBuffer.remaining();
        }
        this.handler.replaceStream(this, new RecycledStream(this.getConnectionId(), this.getIdentifier(), this.state, remaining));
    }
    
    final boolean isPushSupported() {
        return this.handler.getRemoteSettings().getEnablePush();
    }
    
    final void push(final Request request) throws IOException {
        if (!this.isPushSupported() || this.getIdAsInt() % 2 == 0) {
            return;
        }
        request.getMimeHeaders().addValue(":method").duplicate(request.method());
        request.getMimeHeaders().addValue(":scheme").duplicate(request.scheme());
        final StringBuilder path = new StringBuilder(request.requestURI().toString());
        if (!request.queryString().isNull()) {
            path.append('?');
            path.append(request.queryString().toString());
        }
        request.getMimeHeaders().addValue(":path").setString(path.toString());
        if ((!request.scheme().equals("http") || request.getServerPort() != 80) && (!request.scheme().equals("https") || request.getServerPort() != 443)) {
            request.getMimeHeaders().addValue(":authority").setString(request.serverName().getString() + ":" + request.getServerPort());
        }
        else {
            request.getMimeHeaders().addValue(":authority").duplicate(request.serverName());
        }
        push(this.handler, request, this);
    }
    
    StreamException getResetException() {
        return this.streamOutputBuffer.reset;
    }
    
    int getWindowUpdateSizeToWrite(final int increment) {
        final int threshold = this.handler.getProtocol().getOverheadWindowUpdateThreshold();
        int result;
        synchronized (this.pendingWindowUpdateForStreamLock) {
            if (increment > threshold) {
                result = increment + this.pendingWindowUpdateForStream;
                this.pendingWindowUpdateForStream = 0;
            }
            else {
                this.pendingWindowUpdateForStream += increment;
                if (this.pendingWindowUpdateForStream > threshold) {
                    result = this.pendingWindowUpdateForStream;
                    this.pendingWindowUpdateForStream = 0;
                }
                else {
                    result = 0;
                }
            }
        }
        return result;
    }
    
    private static void push(final Http2UpgradeHandler handler, final Request request, final Stream stream) throws IOException {
        if (Constants.IS_SECURITY_ENABLED) {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedPush(handler, request, stream));
                return;
            }
            catch (final PrivilegedActionException ex) {
                final Exception e = ex.getException();
                if (e instanceof IOException) {
                    throw (IOException)e;
                }
                throw new IOException(ex);
            }
        }
        handler.push(request, stream);
    }
    
    static {
        log = LogFactory.getLog((Class)Stream.class);
        sm = StringManager.getManager((Class)Stream.class);
        HTTP_UPGRADE_STREAM = 1;
        final Response response = new Response();
        response.setStatus(100);
        StreamProcessor.prepareHeaders(null, response, null, null);
        ACK_HEADERS = response.getMimeHeaders();
    }
    
    private static class PrivilegedPush implements PrivilegedExceptionAction<Void>
    {
        private final Http2UpgradeHandler handler;
        private final Request request;
        private final Stream stream;
        
        public PrivilegedPush(final Http2UpgradeHandler handler, final Request request, final Stream stream) {
            this.handler = handler;
            this.request = request;
            this.stream = stream;
        }
        
        @Override
        public Void run() throws IOException {
            this.handler.push(this.request, this.stream);
            return null;
        }
    }
    
    class StreamOutputBuffer implements HttpOutputBuffer, WriteBuffer.Sink
    {
        private final ByteBuffer buffer;
        private final WriteBuffer writeBuffer;
        private boolean dataLeft;
        private volatile long written;
        private volatile int streamReservation;
        private volatile boolean closed;
        private volatile StreamException reset;
        private volatile boolean endOfStreamSent;
        
        StreamOutputBuffer() {
            this.buffer = ByteBuffer.allocate(8192);
            this.writeBuffer = new WriteBuffer(32768);
            this.written = 0L;
            this.streamReservation = 0;
            this.closed = false;
            this.reset = null;
            this.endOfStreamSent = false;
        }
        
        @Deprecated
        @Override
        public synchronized int doWrite(final ByteChunk chunk) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(Stream.sm.getString("stream.closed", new Object[] { Stream.this.getConnectionId(), Stream.this.getIdentifier() }));
            }
            if (!Stream.this.coyoteResponse.isCommitted()) {
                Stream.this.coyoteResponse.sendHeaders();
            }
            int len = chunk.getLength();
            int offset = 0;
            while (len > 0) {
                final int thisTime = Math.min(this.buffer.remaining(), len);
                this.buffer.put(chunk.getBytes(), chunk.getOffset() + offset, thisTime);
                offset += thisTime;
                len -= thisTime;
                if (len > 0 && !this.buffer.hasRemaining() && this.flush(true, Stream.this.coyoteResponse.getWriteListener() == null)) {
                    break;
                }
            }
            this.written += offset;
            return offset;
        }
        
        @Override
        public final synchronized int doWrite(final ByteBuffer chunk) throws IOException {
            if (this.closed) {
                throw new IllegalStateException(Stream.sm.getString("stream.closed", new Object[] { Stream.this.getConnectionId(), Stream.this.getIdAsString() }));
            }
            int totalThisTime = 0;
            if (this.writeBuffer.isEmpty()) {
                final int chunkLimit = chunk.limit();
                while (chunk.remaining() > 0) {
                    final int thisTime = Math.min(this.buffer.remaining(), chunk.remaining());
                    chunk.limit(chunk.position() + thisTime);
                    this.buffer.put(chunk);
                    chunk.limit(chunkLimit);
                    totalThisTime += thisTime;
                    if (chunk.remaining() > 0 && !this.buffer.hasRemaining() && this.flush(true, Stream.this.coyoteResponse.getWriteListener() == null)) {
                        totalThisTime = chunk.remaining();
                        this.writeBuffer.add(chunk);
                        this.dataLeft = true;
                        break;
                    }
                }
            }
            else {
                totalThisTime = chunk.remaining();
                this.writeBuffer.add(chunk);
            }
            this.written += totalThisTime;
            return totalThisTime;
        }
        
        final synchronized boolean flush(final boolean block) throws IOException {
            boolean dataInBuffer = this.buffer.position() > 0;
            boolean flushed = false;
            if (dataInBuffer) {
                dataInBuffer = this.flush(false, block);
                flushed = true;
            }
            if (dataInBuffer) {
                this.dataLeft = true;
            }
            else if (this.writeBuffer.isEmpty()) {
                if (flushed) {
                    this.dataLeft = false;
                }
                else {
                    this.dataLeft = this.flush(false, block);
                }
            }
            else {
                this.dataLeft = this.writeBuffer.write(this, block);
            }
            return this.dataLeft;
        }
        
        private final synchronized boolean flush(final boolean writeInProgress, final boolean block) throws IOException {
            if (Stream.log.isDebugEnabled()) {
                Stream.log.debug((Object)Stream.sm.getString("stream.outputBuffer.flush.debug", new Object[] { Stream.this.getConnectionId(), Stream.this.getIdAsString(), Integer.toString(this.buffer.position()), Boolean.toString(writeInProgress), Boolean.toString(this.closed) }));
            }
            if (this.buffer.position() == 0) {
                if (this.closed && !this.endOfStreamSent) {
                    Stream.this.handler.writeBody(Stream.this, this.buffer, 0, true);
                }
                return false;
            }
            this.buffer.flip();
            int left = this.buffer.remaining();
            while (left > 0) {
                if (this.streamReservation == 0) {
                    this.streamReservation = Stream.this.reserveWindowSize(left, block);
                    if (this.streamReservation == 0) {
                        this.buffer.compact();
                        return true;
                    }
                }
                while (this.streamReservation > 0) {
                    final int connectionReservation = Stream.this.handler.reserveWindowSize(Stream.this, this.streamReservation, block);
                    if (connectionReservation == 0) {
                        this.buffer.compact();
                        return true;
                    }
                    Stream.this.handler.writeBody(Stream.this, this.buffer, connectionReservation, !writeInProgress && this.closed && left == connectionReservation);
                    this.streamReservation -= connectionReservation;
                    left -= connectionReservation;
                }
            }
            this.buffer.clear();
            return false;
        }
        
        final synchronized boolean isReady() {
            return (Stream.this.getWindowSize() <= 0L || !Stream.this.allocationManager.isWaitingForStream()) && (Stream.this.handler.getWindowSize() <= 0L || !Stream.this.allocationManager.isWaitingForConnection()) && !this.dataLeft;
        }
        
        @Override
        public final long getBytesWritten() {
            return this.written;
        }
        
        @Override
        public final void end() throws IOException {
            if (this.reset != null) {
                throw new CloseNowException(this.reset);
            }
            if (!this.closed) {
                this.flush(this.closed = true);
            }
        }
        
        final boolean hasNoBody() {
            return this.written == 0L && this.closed;
        }
        
        @Override
        public void flush() throws IOException {
            this.flush(Stream.this.getCoyoteResponse().getWriteListener() == null);
        }
        
        @Override
        public synchronized boolean writeFromBuffer(final ByteBuffer src, final boolean blocking) throws IOException {
            final int chunkLimit = src.limit();
            while (src.remaining() > 0) {
                final int thisTime = Math.min(this.buffer.remaining(), src.remaining());
                src.limit(src.position() + thisTime);
                this.buffer.put(src);
                src.limit(chunkLimit);
                if (this.flush(false, blocking)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    class StreamInputBuffer implements InputBuffer
    {
        private byte[] outBuffer;
        private volatile ByteBuffer inBuffer;
        private volatile boolean readInterest;
        private volatile boolean closed;
        private boolean resetReceived;
        
        @Deprecated
        @Override
        public int doRead(final ByteChunk chunk) throws IOException {
            this.ensureBuffersExist();
            int written = -1;
            synchronized (this.inBuffer) {
                boolean canRead = false;
                while (this.inBuffer.position() == 0 && (canRead = (Stream.this.isActive() && !Stream.this.isInputFinished()))) {
                    try {
                        if (Stream.log.isDebugEnabled()) {
                            Stream.log.debug((Object)Stream.sm.getString("stream.inputBuffer.empty"));
                        }
                        final long readTimeout = Stream.this.handler.getProtocol().getStreamReadTimeout();
                        if (readTimeout < 0L) {
                            this.inBuffer.wait();
                        }
                        else {
                            this.inBuffer.wait(readTimeout);
                        }
                        if (this.resetReceived) {
                            throw new IOException(Stream.sm.getString("stream.inputBuffer.reset"));
                        }
                        if (this.inBuffer.position() == 0) {
                            final String msg = Stream.sm.getString("stream.inputBuffer.readTimeout");
                            final StreamException se = new StreamException(msg, Http2Error.ENHANCE_YOUR_CALM, Stream.this.getIdAsInt());
                            Stream.this.coyoteResponse.setError();
                            Stream.this.streamOutputBuffer.reset = se;
                            throw new CloseNowException(msg, se);
                        }
                        continue;
                    }
                    catch (final InterruptedException e) {
                        throw new IOException(e);
                    }
                    break;
                }
                if (this.inBuffer.position() > 0) {
                    this.inBuffer.flip();
                    written = this.inBuffer.remaining();
                    if (Stream.log.isDebugEnabled()) {
                        Stream.log.debug((Object)Stream.sm.getString("stream.inputBuffer.copy", new Object[] { Integer.toString(written) }));
                    }
                    this.inBuffer.get(this.outBuffer, 0, written);
                    this.inBuffer.clear();
                }
                else {
                    if (!canRead) {
                        return -1;
                    }
                    throw new IllegalStateException();
                }
            }
            chunk.setBytes(this.outBuffer, 0, written);
            Stream.this.handler.writeWindowUpdate(Stream.this, written, true);
            return written;
        }
        
        @Override
        public final int doRead(final ApplicationBufferHandler applicationBufferHandler) throws IOException {
            this.ensureBuffersExist();
            int written = -1;
            final ByteBuffer tmpInBuffer = this.inBuffer;
            if (tmpInBuffer == null) {
                return -1;
            }
            synchronized (tmpInBuffer) {
                if (this.inBuffer == null) {
                    return -1;
                }
                boolean canRead = false;
                while (this.inBuffer.position() == 0 && (canRead = (Stream.this.isActive() && !Stream.this.isInputFinished()))) {
                    try {
                        if (Stream.log.isDebugEnabled()) {
                            Stream.log.debug((Object)Stream.sm.getString("stream.inputBuffer.empty"));
                        }
                        final long readTimeout = Stream.this.handler.getProtocol().getStreamReadTimeout();
                        if (readTimeout < 0L) {
                            this.inBuffer.wait();
                        }
                        else {
                            this.inBuffer.wait(readTimeout);
                        }
                        if (this.resetReceived) {
                            throw new IOException(Stream.sm.getString("stream.inputBuffer.reset"));
                        }
                        if (this.inBuffer.position() == 0 && Stream.this.isActive() && !Stream.this.isInputFinished()) {
                            final String msg = Stream.sm.getString("stream.inputBuffer.readTimeout");
                            final StreamException se = new StreamException(msg, Http2Error.ENHANCE_YOUR_CALM, Stream.this.getIdAsInt());
                            Stream.this.coyoteResponse.setError();
                            Stream.this.streamOutputBuffer.reset = se;
                            throw new CloseNowException(msg, se);
                        }
                        continue;
                    }
                    catch (final InterruptedException e) {
                        throw new IOException(e);
                    }
                    break;
                }
                if (this.inBuffer.position() > 0) {
                    this.inBuffer.flip();
                    written = this.inBuffer.remaining();
                    if (Stream.log.isDebugEnabled()) {
                        Stream.log.debug((Object)Stream.sm.getString("stream.inputBuffer.copy", new Object[] { Integer.toString(written) }));
                    }
                    this.inBuffer.get(this.outBuffer, 0, written);
                    this.inBuffer.clear();
                }
                else {
                    if (!canRead) {
                        return -1;
                    }
                    throw new IllegalStateException();
                }
            }
            applicationBufferHandler.setByteBuffer(ByteBuffer.wrap(this.outBuffer, 0, written));
            Stream.this.handler.writeWindowUpdate(Stream.this, written, true);
            return written;
        }
        
        final boolean isReadyForRead() {
            this.ensureBuffersExist();
            synchronized (this) {
                if (this.available() > 0) {
                    return true;
                }
                if (!this.isRequestBodyFullyRead()) {
                    this.readInterest = true;
                }
                return false;
            }
        }
        
        final synchronized boolean isRequestBodyFullyRead() {
            return (this.inBuffer == null || this.inBuffer.position() == 0) && Stream.this.isInputFinished();
        }
        
        @Override
        public final synchronized int available() {
            if (this.inBuffer == null) {
                return 0;
            }
            return this.inBuffer.position();
        }
        
        final synchronized void onDataAvailable() throws IOException {
            if (this.closed) {
                this.swallowUnread();
            }
            else if (this.readInterest) {
                if (Stream.log.isDebugEnabled()) {
                    Stream.log.debug((Object)Stream.sm.getString("stream.inputBuffer.dispatch"));
                }
                this.readInterest = false;
                Stream.this.coyoteRequest.action(ActionCode.DISPATCH_READ, null);
                Stream.this.coyoteRequest.action(ActionCode.DISPATCH_EXECUTE, null);
            }
            else {
                if (Stream.log.isDebugEnabled()) {
                    Stream.log.debug((Object)Stream.sm.getString("stream.inputBuffer.signal"));
                }
                synchronized (this.inBuffer) {
                    this.inBuffer.notifyAll();
                }
            }
        }
        
        private final ByteBuffer getInBuffer() {
            this.ensureBuffersExist();
            return this.inBuffer;
        }
        
        final synchronized void insertReplayedBody(final ByteChunk body) {
            this.inBuffer = ByteBuffer.wrap(body.getBytes(), body.getOffset(), body.getLength());
        }
        
        private final void ensureBuffersExist() {
            if (this.inBuffer == null && !this.closed) {
                final int size = Stream.this.handler.getLocalSettings().getInitialWindowSize();
                synchronized (this) {
                    if (this.inBuffer == null && !this.closed) {
                        this.inBuffer = ByteBuffer.allocate(size);
                        this.outBuffer = new byte[size];
                    }
                }
            }
        }
        
        private final void receiveReset() {
            if (this.inBuffer != null) {
                synchronized (this.inBuffer) {
                    this.resetReceived = true;
                    this.inBuffer.notifyAll();
                }
            }
        }
        
        private final void notifyEof() {
            if (this.inBuffer != null) {
                synchronized (this.inBuffer) {
                    this.inBuffer.notifyAll();
                }
            }
        }
        
        private final void swallowUnread() throws IOException {
            synchronized (this) {
                this.closed = true;
            }
            if (this.inBuffer != null) {
                synchronized (this.inBuffer) {
                    final int unreadByteCount = this.inBuffer.position();
                    if (Stream.log.isDebugEnabled()) {
                        Stream.log.debug((Object)Stream.sm.getString("stream.inputBuffer.swallowUnread", new Object[] { unreadByteCount }));
                    }
                    if (unreadByteCount > 0) {
                        this.inBuffer.position(0);
                        this.inBuffer.limit(this.inBuffer.limit() - unreadByteCount);
                        Stream.this.handler.onSwallowedDataFramePayload(Stream.this.getIdAsInt(), unreadByteCount);
                    }
                }
            }
        }
    }
}
