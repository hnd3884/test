package org.apache.coyote.http2;

import org.apache.juli.logging.LogFactory;
import org.apache.coyote.ProtocolException;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

class Http2Parser
{
    private static final Log log;
    private static final StringManager sm;
    static final byte[] CLIENT_PREFACE_START;
    private final String connectionId;
    private final Input input;
    private final Output output;
    private final byte[] frameHeaderBuffer;
    private volatile HpackDecoder hpackDecoder;
    private volatile ByteBuffer headerReadBuffer;
    private volatile int headersCurrentStream;
    private volatile boolean headersEndStream;
    
    Http2Parser(final String connectionId, final Input input, final Output output) {
        this.frameHeaderBuffer = new byte[9];
        this.headerReadBuffer = ByteBuffer.allocate(1024);
        this.headersCurrentStream = -1;
        this.headersEndStream = false;
        this.connectionId = connectionId;
        this.input = input;
        this.output = output;
    }
    
    boolean readFrame(final boolean block) throws Http2Exception, IOException {
        return this.readFrame(block, null);
    }
    
    private boolean readFrame(final boolean block, final FrameType expected) throws IOException, Http2Exception {
        if (!this.input.fill(block, this.frameHeaderBuffer)) {
            return false;
        }
        final int payloadSize = ByteUtil.getThreeBytes(this.frameHeaderBuffer, 0);
        final int frameTypeId = ByteUtil.getOneByte(this.frameHeaderBuffer, 3);
        final FrameType frameType = FrameType.valueOf(frameTypeId);
        final int flags = ByteUtil.getOneByte(this.frameHeaderBuffer, 4);
        final int streamId = ByteUtil.get31Bits(this.frameHeaderBuffer, 5);
        try {
            this.validateFrame(expected, frameType, streamId, flags, payloadSize);
        }
        catch (final StreamException se) {
            this.swallowPayload(streamId, frameTypeId, payloadSize, false);
            throw se;
        }
        switch (frameType) {
            case DATA: {
                this.readDataFrame(streamId, flags, payloadSize);
                break;
            }
            case HEADERS: {
                this.readHeadersFrame(streamId, flags, payloadSize);
                break;
            }
            case PRIORITY: {
                this.readPriorityFrame(streamId);
                break;
            }
            case RST: {
                this.readRstFrame(streamId);
                break;
            }
            case SETTINGS: {
                this.readSettingsFrame(flags, payloadSize);
                break;
            }
            case PUSH_PROMISE: {
                this.readPushPromiseFrame(streamId);
                break;
            }
            case PING: {
                this.readPingFrame(flags);
                break;
            }
            case GOAWAY: {
                this.readGoawayFrame(payloadSize);
                break;
            }
            case WINDOW_UPDATE: {
                this.readWindowUpdateFrame(streamId);
                break;
            }
            case CONTINUATION: {
                this.readContinuationFrame(streamId, flags, payloadSize);
                break;
            }
            case UNKNOWN: {
                this.readUnknownFrame(streamId, frameTypeId, flags, payloadSize);
                break;
            }
        }
        return true;
    }
    
    private void readDataFrame(final int streamId, final int flags, final int payloadSize) throws Http2Exception, IOException {
        int padLength = 0;
        final boolean endOfStream = Flags.isEndOfStream(flags);
        int dataLength;
        if (Flags.hasPadding(flags)) {
            final byte[] b = { 0 };
            this.input.fill(true, b);
            padLength = (b[0] & 0xFF);
            if (padLength >= payloadSize) {
                throw new ConnectionException(Http2Parser.sm.getString("http2Parser.processFrame.tooMuchPadding", new Object[] { this.connectionId, Integer.toString(streamId), Integer.toString(padLength), Integer.toString(payloadSize) }), Http2Error.PROTOCOL_ERROR);
            }
            dataLength = payloadSize - (padLength + 1);
        }
        else {
            dataLength = payloadSize;
        }
        if (Http2Parser.log.isDebugEnabled()) {
            String padding;
            if (Flags.hasPadding(flags)) {
                padding = Integer.toString(padLength);
            }
            else {
                padding = "none";
            }
            Http2Parser.log.debug((Object)Http2Parser.sm.getString("http2Parser.processFrameData.lengths", new Object[] { this.connectionId, Integer.toString(streamId), Integer.toString(dataLength), padding }));
        }
        final ByteBuffer dest = this.output.startRequestBodyFrame(streamId, payloadSize, endOfStream);
        if (dest == null) {
            this.swallowPayload(streamId, FrameType.DATA.getId(), dataLength, false);
            if (Flags.hasPadding(flags)) {
                this.swallowPayload(streamId, FrameType.DATA.getId(), padLength, true);
            }
            if (endOfStream) {
                this.output.receivedEndOfStream(streamId);
            }
        }
        else {
            synchronized (dest) {
                if (dest.remaining() < payloadSize) {
                    this.swallowPayload(streamId, FrameType.DATA.getId(), dataLength, false);
                    if (Flags.hasPadding(flags)) {
                        this.swallowPayload(streamId, FrameType.DATA.getId(), padLength, true);
                    }
                    throw new StreamException(Http2Parser.sm.getString("http2Parser.processFrameData.window", new Object[] { this.connectionId }), Http2Error.FLOW_CONTROL_ERROR, streamId);
                }
                this.input.fill(true, dest, dataLength);
                if (Flags.hasPadding(flags)) {
                    this.swallowPayload(streamId, FrameType.DATA.getId(), padLength, true);
                }
                if (endOfStream) {
                    this.output.receivedEndOfStream(streamId);
                }
                this.output.endRequestBodyFrame(streamId, dataLength);
            }
        }
    }
    
    private void readHeadersFrame(final int streamId, final int flags, int payloadSize) throws Http2Exception, IOException {
        this.headersEndStream = Flags.isEndOfStream(flags);
        if (this.hpackDecoder == null) {
            this.hpackDecoder = this.output.getHpackDecoder();
        }
        try {
            this.hpackDecoder.setHeaderEmitter(this.output.headersStart(streamId, this.headersEndStream));
        }
        catch (final StreamException se) {
            this.swallowPayload(streamId, FrameType.HEADERS.getId(), payloadSize, false);
            throw se;
        }
        int padLength = 0;
        final boolean padding = Flags.hasPadding(flags);
        final boolean priority = Flags.hasPriority(flags);
        int optionalLen = 0;
        if (padding) {
            optionalLen = 1;
        }
        if (priority) {
            optionalLen += 5;
        }
        if (optionalLen > 0) {
            final byte[] optional = new byte[optionalLen];
            this.input.fill(true, optional);
            int optionalPos = 0;
            if (padding) {
                padLength = ByteUtil.getOneByte(optional, optionalPos++);
                if (padLength >= payloadSize) {
                    throw new ConnectionException(Http2Parser.sm.getString("http2Parser.processFrame.tooMuchPadding", new Object[] { this.connectionId, Integer.toString(streamId), Integer.toString(padLength), Integer.toString(payloadSize) }), Http2Error.PROTOCOL_ERROR);
                }
            }
            if (priority) {
                final boolean exclusive = ByteUtil.isBit7Set(optional[optionalPos]);
                final int parentStreamId = ByteUtil.get31Bits(optional, optionalPos);
                final int weight = ByteUtil.getOneByte(optional, optionalPos + 4) + 1;
                this.output.reprioritise(streamId, parentStreamId, exclusive, weight);
            }
            payloadSize -= optionalLen;
            payloadSize -= padLength;
        }
        this.readHeaderPayload(streamId, payloadSize);
        this.swallowPayload(streamId, FrameType.HEADERS.getId(), padLength, true);
        if (Flags.isEndOfHeaders(flags)) {
            this.onHeadersComplete(streamId);
        }
        else {
            this.headersCurrentStream = streamId;
        }
    }
    
    private void readPriorityFrame(final int streamId) throws Http2Exception, IOException {
        final byte[] payload = new byte[5];
        this.input.fill(true, payload);
        final boolean exclusive = ByteUtil.isBit7Set(payload[0]);
        final int parentStreamId = ByteUtil.get31Bits(payload, 0);
        final int weight = ByteUtil.getOneByte(payload, 4) + 1;
        if (streamId == parentStreamId) {
            throw new StreamException(Http2Parser.sm.getString("http2Parser.processFramePriority.invalidParent", new Object[] { this.connectionId, streamId }), Http2Error.PROTOCOL_ERROR, streamId);
        }
        this.output.reprioritise(streamId, parentStreamId, exclusive, weight);
    }
    
    private void readRstFrame(final int streamId) throws Http2Exception, IOException {
        final byte[] payload = new byte[4];
        this.input.fill(true, payload);
        final long errorCode = ByteUtil.getFourBytes(payload, 0);
        this.output.reset(streamId, errorCode);
        this.headersCurrentStream = -1;
        this.headersEndStream = false;
    }
    
    private void readSettingsFrame(final int flags, final int payloadSize) throws Http2Exception, IOException {
        final boolean ack = Flags.isAck(flags);
        if (payloadSize > 0 && ack) {
            throw new ConnectionException(Http2Parser.sm.getString("http2Parser.processFrameSettings.ackWithNonZeroPayload"), Http2Error.FRAME_SIZE_ERROR);
        }
        if (payloadSize == 0 && !ack) {
            this.output.setting(null, 0L);
        }
        else {
            final byte[] setting = new byte[6];
            for (int i = 0; i < payloadSize / 6; ++i) {
                this.input.fill(true, setting);
                final int id = ByteUtil.getTwoBytes(setting, 0);
                final long value = ByteUtil.getFourBytes(setting, 2);
                this.output.setting(Setting.valueOf(id), value);
            }
        }
        this.output.settingsEnd(ack);
    }
    
    private void readPushPromiseFrame(final int streamId) throws Http2Exception {
        throw new ConnectionException(Http2Parser.sm.getString("http2Parser.processFramePushPromise", new Object[] { this.connectionId, streamId }), Http2Error.PROTOCOL_ERROR);
    }
    
    private void readPingFrame(final int flags) throws IOException {
        final byte[] payload = new byte[8];
        this.input.fill(true, payload);
        this.output.pingReceive(payload, Flags.isAck(flags));
    }
    
    private void readGoawayFrame(final int payloadSize) throws IOException {
        final byte[] payload = new byte[payloadSize];
        this.input.fill(true, payload);
        final int lastStreamId = ByteUtil.get31Bits(payload, 0);
        final long errorCode = ByteUtil.getFourBytes(payload, 4);
        String debugData = null;
        if (payloadSize > 8) {
            debugData = new String(payload, 8, payloadSize - 8, StandardCharsets.UTF_8);
        }
        this.output.goaway(lastStreamId, errorCode, debugData);
    }
    
    private void readWindowUpdateFrame(final int streamId) throws Http2Exception, IOException {
        final byte[] payload = new byte[4];
        this.input.fill(true, payload);
        final int windowSizeIncrement = ByteUtil.get31Bits(payload, 0);
        if (Http2Parser.log.isDebugEnabled()) {
            Http2Parser.log.debug((Object)Http2Parser.sm.getString("http2Parser.processFrameWindowUpdate.debug", new Object[] { this.connectionId, Integer.toString(streamId), Integer.toString(windowSizeIncrement) }));
        }
        if (windowSizeIncrement != 0) {
            this.output.incrementWindowSize(streamId, windowSizeIncrement);
            return;
        }
        if (streamId == 0) {
            throw new ConnectionException(Http2Parser.sm.getString("http2Parser.processFrameWindowUpdate.invalidIncrement"), Http2Error.PROTOCOL_ERROR);
        }
        throw new StreamException(Http2Parser.sm.getString("http2Parser.processFrameWindowUpdate.invalidIncrement"), Http2Error.PROTOCOL_ERROR, streamId);
    }
    
    private void readContinuationFrame(final int streamId, final int flags, final int payloadSize) throws Http2Exception, IOException {
        if (this.headersCurrentStream == -1) {
            throw new ConnectionException(Http2Parser.sm.getString("http2Parser.processFrameContinuation.notExpected", new Object[] { this.connectionId, Integer.toString(streamId) }), Http2Error.PROTOCOL_ERROR);
        }
        final boolean endOfHeaders = Flags.isEndOfHeaders(flags);
        this.output.headersContinue(payloadSize, endOfHeaders);
        this.readHeaderPayload(streamId, payloadSize);
        if (endOfHeaders) {
            this.headersCurrentStream = -1;
            this.onHeadersComplete(streamId);
        }
    }
    
    private void readHeaderPayload(final int streamId, final int payloadSize) throws Http2Exception, IOException {
        if (Http2Parser.log.isDebugEnabled()) {
            Http2Parser.log.debug((Object)Http2Parser.sm.getString("http2Parser.processFrameHeaders.payload", new Object[] { this.connectionId, streamId, payloadSize }));
        }
        int remaining = payloadSize;
        while (remaining > 0) {
            if (this.headerReadBuffer.remaining() == 0) {
                int newSize;
                if (this.headerReadBuffer.capacity() < payloadSize) {
                    newSize = payloadSize;
                }
                else {
                    newSize = this.headerReadBuffer.capacity() * 2;
                }
                this.headerReadBuffer = ByteBufferUtils.expand(this.headerReadBuffer, newSize);
            }
            final int toRead = Math.min(this.headerReadBuffer.remaining(), remaining);
            this.input.fill(true, this.headerReadBuffer, toRead);
            this.headerReadBuffer.flip();
            try {
                this.hpackDecoder.decode(this.headerReadBuffer);
            }
            catch (final HpackException hpe) {
                throw new ConnectionException(Http2Parser.sm.getString("http2Parser.processFrameHeaders.decodingFailed"), Http2Error.COMPRESSION_ERROR, hpe);
            }
            this.headerReadBuffer.compact();
            remaining -= toRead;
            if (this.hpackDecoder.isHeaderCountExceeded()) {
                final StreamException headerException = new StreamException(Http2Parser.sm.getString("http2Parser.headerLimitCount", new Object[] { this.connectionId, streamId }), Http2Error.ENHANCE_YOUR_CALM, streamId);
                this.hpackDecoder.getHeaderEmitter().setHeaderException(headerException);
            }
            if (this.hpackDecoder.isHeaderSizeExceeded(this.headerReadBuffer.position())) {
                final StreamException headerException = new StreamException(Http2Parser.sm.getString("http2Parser.headerLimitSize", new Object[] { this.connectionId, streamId }), Http2Error.ENHANCE_YOUR_CALM, streamId);
                this.hpackDecoder.getHeaderEmitter().setHeaderException(headerException);
            }
            if (this.hpackDecoder.isHeaderSwallowSizeExceeded(this.headerReadBuffer.position())) {
                throw new ConnectionException(Http2Parser.sm.getString("http2Parser.headerLimitSize", new Object[] { this.connectionId, streamId }), Http2Error.ENHANCE_YOUR_CALM);
            }
        }
    }
    
    protected void readUnknownFrame(final int streamId, final int frameTypeId, final int flags, final int payloadSize) throws IOException {
        try {
            this.swallowPayload(streamId, frameTypeId, payloadSize, false);
        }
        catch (final ConnectionException ex) {}
        finally {
            this.output.onSwallowedUnknownFrame(streamId, frameTypeId, flags, payloadSize);
        }
    }
    
    protected void swallowPayload(final int streamId, final int frameTypeId, int len, final boolean isPadding) throws IOException, ConnectionException {
        if (Http2Parser.log.isDebugEnabled()) {
            Http2Parser.log.debug((Object)Http2Parser.sm.getString("http2Parser.swallow.debug", new Object[] { this.connectionId, Integer.toString(streamId), Integer.toString(len) }));
        }
        int read = 0;
        int thisTime = 0;
        try {
            if (len == 0) {
                return;
            }
            final byte[] buffer = new byte[1024];
            while (read < len) {
                thisTime = Math.min(buffer.length, len - read);
                this.input.fill(true, buffer, 0, thisTime);
                if (isPadding) {
                    for (int i = 0; i < thisTime; ++i) {
                        if (buffer[i] != 0) {
                            throw new ConnectionException(Http2Parser.sm.getString("http2Parser.nonZeroPadding", new Object[] { this.connectionId, Integer.toString(streamId) }), Http2Error.PROTOCOL_ERROR);
                        }
                    }
                }
                read += thisTime;
            }
        }
        finally {
            if (FrameType.DATA.getIdByte() == frameTypeId) {
                if (isPadding) {
                    ++len;
                }
                if (len > 0) {
                    this.output.onSwallowedDataFramePayload(streamId, len);
                }
            }
            read += thisTime;
        }
    }
    
    private void onHeadersComplete(final int streamId) throws Http2Exception {
        if (this.headerReadBuffer.position() > 0) {
            throw new ConnectionException(Http2Parser.sm.getString("http2Parser.processFrameHeaders.decodingDataLeft"), Http2Error.COMPRESSION_ERROR);
        }
        this.hpackDecoder.getHeaderEmitter().validateHeaders();
        this.output.headersEnd(streamId);
        if (this.headersEndStream) {
            this.output.receivedEndOfStream(streamId);
            this.headersEndStream = false;
        }
        if (this.headerReadBuffer.capacity() > 1024) {
            this.headerReadBuffer = ByteBuffer.allocate(1024);
        }
    }
    
    private void validateFrame(final FrameType expected, final FrameType frameType, final int streamId, final int flags, final int payloadSize) throws Http2Exception {
        if (Http2Parser.log.isDebugEnabled()) {
            Http2Parser.log.debug((Object)Http2Parser.sm.getString("http2Parser.processFrame", new Object[] { this.connectionId, Integer.toString(streamId), frameType, Integer.toString(flags), Integer.toString(payloadSize) }));
        }
        if (expected != null && frameType != expected) {
            throw new StreamException(Http2Parser.sm.getString("http2Parser.processFrame.unexpectedType", new Object[] { expected, frameType }), Http2Error.PROTOCOL_ERROR, streamId);
        }
        final int maxFrameSize = this.input.getMaxFrameSize();
        if (payloadSize > maxFrameSize) {
            throw new ConnectionException(Http2Parser.sm.getString("http2Parser.payloadTooBig", new Object[] { Integer.toString(payloadSize), Integer.toString(maxFrameSize) }), Http2Error.FRAME_SIZE_ERROR);
        }
        if (this.headersCurrentStream != -1) {
            if (this.headersCurrentStream != streamId) {
                throw new ConnectionException(Http2Parser.sm.getString("http2Parser.headers.wrongStream", new Object[] { this.connectionId, Integer.toString(this.headersCurrentStream), Integer.toString(streamId) }), Http2Error.COMPRESSION_ERROR);
            }
            if (frameType != FrameType.RST) {
                if (frameType != FrameType.CONTINUATION) {
                    throw new ConnectionException(Http2Parser.sm.getString("http2Parser.headers.wrongFrameType", new Object[] { this.connectionId, Integer.toString(this.headersCurrentStream), frameType }), Http2Error.COMPRESSION_ERROR);
                }
            }
        }
        frameType.check(streamId, payloadSize);
    }
    
    void readConnectionPreface() throws Http2Exception {
        final byte[] data = new byte[Http2Parser.CLIENT_PREFACE_START.length];
        try {
            this.input.fill(true, data);
            for (int i = 0; i < Http2Parser.CLIENT_PREFACE_START.length; ++i) {
                if (Http2Parser.CLIENT_PREFACE_START[i] != data[i]) {
                    throw new ProtocolException(Http2Parser.sm.getString("http2Parser.preface.invalid"));
                }
            }
            this.readFrame(true, FrameType.SETTINGS);
        }
        catch (final IOException ioe) {
            throw new ProtocolException(Http2Parser.sm.getString("http2Parser.preface.io"), ioe);
        }
    }
    
    static {
        log = LogFactory.getLog((Class)Http2Parser.class);
        sm = StringManager.getManager((Class)Http2Parser.class);
        CLIENT_PREFACE_START = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);
    }
    
    interface Output
    {
        HpackDecoder getHpackDecoder();
        
        ByteBuffer startRequestBodyFrame(final int p0, final int p1, final boolean p2) throws Http2Exception;
        
        void endRequestBodyFrame(final int p0, final int p1) throws Http2Exception, IOException;
        
        void receivedEndOfStream(final int p0) throws ConnectionException;
        
        void onSwallowedDataFramePayload(final int p0, final int p1) throws ConnectionException, IOException;
        
        HpackDecoder.HeaderEmitter headersStart(final int p0, final boolean p1) throws Http2Exception, IOException;
        
        void headersContinue(final int p0, final boolean p1);
        
        void headersEnd(final int p0) throws Http2Exception;
        
        void reprioritise(final int p0, final int p1, final boolean p2, final int p3) throws Http2Exception;
        
        void reset(final int p0, final long p1) throws Http2Exception;
        
        void setting(final Setting p0, final long p1) throws ConnectionException;
        
        void settingsEnd(final boolean p0) throws IOException;
        
        void pingReceive(final byte[] p0, final boolean p1) throws IOException;
        
        void goaway(final int p0, final long p1, final String p2);
        
        void incrementWindowSize(final int p0, final int p1) throws Http2Exception;
        
        void onSwallowedUnknownFrame(final int p0, final int p1, final int p2, final int p3) throws IOException;
    }
    
    interface Input
    {
        boolean fill(final boolean p0, final byte[] p1, final int p2, final int p3) throws IOException;
        
        boolean fill(final boolean p0, final byte[] p1) throws IOException;
        
        boolean fill(final boolean p0, final ByteBuffer p1, final int p2) throws IOException;
        
        int getMaxFrameSize();
    }
}
