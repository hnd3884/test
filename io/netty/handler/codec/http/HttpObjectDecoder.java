package io.netty.handler.codec.http;

import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ByteProcessor;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.buffer.Unpooled;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.AppendableCharSequence;
import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.ByteToMessageDecoder;

public abstract class HttpObjectDecoder extends ByteToMessageDecoder
{
    public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
    public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
    public static final boolean DEFAULT_CHUNKED_SUPPORTED = true;
    public static final boolean DEFAULT_ALLOW_PARTIAL_CHUNKS = true;
    public static final int DEFAULT_MAX_CHUNK_SIZE = 8192;
    public static final boolean DEFAULT_VALIDATE_HEADERS = true;
    public static final int DEFAULT_INITIAL_BUFFER_SIZE = 128;
    public static final boolean DEFAULT_ALLOW_DUPLICATE_CONTENT_LENGTHS = false;
    private static final String EMPTY_VALUE = "";
    private final int maxChunkSize;
    private final boolean chunkedSupported;
    private final boolean allowPartialChunks;
    protected final boolean validateHeaders;
    private final boolean allowDuplicateContentLengths;
    private final HeaderParser headerParser;
    private final LineParser lineParser;
    private HttpMessage message;
    private long chunkSize;
    private long contentLength;
    private volatile boolean resetRequested;
    private CharSequence name;
    private CharSequence value;
    private LastHttpContent trailer;
    private State currentState;
    
    protected HttpObjectDecoder() {
        this(4096, 8192, 8192, true);
    }
    
    protected HttpObjectDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean chunkedSupported) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, true);
    }
    
    protected HttpObjectDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean chunkedSupported, final boolean validateHeaders) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, validateHeaders, 128);
    }
    
    protected HttpObjectDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean chunkedSupported, final boolean validateHeaders, final int initialBufferSize) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, validateHeaders, initialBufferSize, false);
    }
    
    protected HttpObjectDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean chunkedSupported, final boolean validateHeaders, final int initialBufferSize, final boolean allowDuplicateContentLengths) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, chunkedSupported, validateHeaders, initialBufferSize, allowDuplicateContentLengths, true);
    }
    
    protected HttpObjectDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean chunkedSupported, final boolean validateHeaders, final int initialBufferSize, final boolean allowDuplicateContentLengths, final boolean allowPartialChunks) {
        this.contentLength = Long.MIN_VALUE;
        this.currentState = State.SKIP_CONTROL_CHARS;
        ObjectUtil.checkPositive(maxInitialLineLength, "maxInitialLineLength");
        ObjectUtil.checkPositive(maxHeaderSize, "maxHeaderSize");
        ObjectUtil.checkPositive(maxChunkSize, "maxChunkSize");
        final AppendableCharSequence seq = new AppendableCharSequence(initialBufferSize);
        this.lineParser = new LineParser(seq, maxInitialLineLength);
        this.headerParser = new HeaderParser(seq, maxHeaderSize);
        this.maxChunkSize = maxChunkSize;
        this.chunkedSupported = chunkedSupported;
        this.validateHeaders = validateHeaders;
        this.allowDuplicateContentLengths = allowDuplicateContentLengths;
        this.allowPartialChunks = allowPartialChunks;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) throws Exception {
        if (this.resetRequested) {
            this.resetNow();
        }
        switch (this.currentState) {
            case SKIP_CONTROL_CHARS:
            case READ_INITIAL: {
                try {
                    final AppendableCharSequence line = this.lineParser.parse(buffer);
                    if (line == null) {
                        return;
                    }
                    final String[] initialLine = splitInitialLine(line);
                    if (initialLine.length < 3) {
                        this.currentState = State.SKIP_CONTROL_CHARS;
                        return;
                    }
                    this.message = this.createMessage(initialLine);
                    this.currentState = State.READ_HEADER;
                }
                catch (final Exception e) {
                    out.add(this.invalidMessage(buffer, e));
                }
            }
            case READ_HEADER: {
                try {
                    final State nextState = this.readHeaders(buffer);
                    if (nextState == null) {
                        return;
                    }
                    this.currentState = nextState;
                    switch (nextState) {
                        case SKIP_CONTROL_CHARS: {
                            out.add(this.message);
                            out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                            this.resetNow();
                            return;
                        }
                        case READ_CHUNK_SIZE: {
                            if (!this.chunkedSupported) {
                                throw new IllegalArgumentException("Chunked messages not supported");
                            }
                            out.add(this.message);
                            return;
                        }
                        default: {
                            final long contentLength = this.contentLength();
                            if (contentLength == 0L || (contentLength == -1L && this.isDecodingRequest())) {
                                out.add(this.message);
                                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                                this.resetNow();
                                return;
                            }
                            assert nextState == State.READ_VARIABLE_LENGTH_CONTENT;
                            out.add(this.message);
                            if (nextState == State.READ_FIXED_LENGTH_CONTENT) {
                                this.chunkSize = contentLength;
                            }
                        }
                    }
                }
                catch (final Exception e) {
                    out.add(this.invalidMessage(buffer, e));
                }
            }
            case READ_VARIABLE_LENGTH_CONTENT: {
                final int toRead = Math.min(buffer.readableBytes(), this.maxChunkSize);
                if (toRead > 0) {
                    final ByteBuf content = buffer.readRetainedSlice(toRead);
                    out.add(new DefaultHttpContent(content));
                }
                return;
            }
            case READ_FIXED_LENGTH_CONTENT: {
                final int readLimit = buffer.readableBytes();
                if (readLimit == 0) {
                    return;
                }
                int toRead2 = Math.min(readLimit, this.maxChunkSize);
                if (toRead2 > this.chunkSize) {
                    toRead2 = (int)this.chunkSize;
                }
                final ByteBuf content2 = buffer.readRetainedSlice(toRead2);
                this.chunkSize -= toRead2;
                if (this.chunkSize == 0L) {
                    out.add(new DefaultLastHttpContent(content2, this.validateHeaders));
                    this.resetNow();
                }
                else {
                    out.add(new DefaultHttpContent(content2));
                }
                return;
            }
            case READ_CHUNK_SIZE: {
                try {
                    final AppendableCharSequence line = this.lineParser.parse(buffer);
                    if (line == null) {
                        return;
                    }
                    final int chunkSize = getChunkSize(line.toString());
                    this.chunkSize = chunkSize;
                    if (chunkSize == 0) {
                        this.currentState = State.READ_CHUNK_FOOTER;
                        return;
                    }
                    this.currentState = State.READ_CHUNKED_CONTENT;
                }
                catch (final Exception e) {
                    out.add(this.invalidChunk(buffer, e));
                }
            }
            case READ_CHUNKED_CONTENT: {
                assert this.chunkSize <= 2147483647L;
                int toRead = Math.min((int)this.chunkSize, this.maxChunkSize);
                if (!this.allowPartialChunks && buffer.readableBytes() < toRead) {
                    return;
                }
                toRead = Math.min(toRead, buffer.readableBytes());
                if (toRead == 0) {
                    return;
                }
                final HttpContent chunk = new DefaultHttpContent(buffer.readRetainedSlice(toRead));
                this.chunkSize -= toRead;
                out.add(chunk);
                if (this.chunkSize != 0L) {
                    return;
                }
                this.currentState = State.READ_CHUNK_DELIMITER;
            }
            case READ_CHUNK_DELIMITER: {
                final int wIdx = buffer.writerIndex();
                int rIdx = buffer.readerIndex();
                while (wIdx > rIdx) {
                    final byte next = buffer.getByte(rIdx++);
                    if (next == 10) {
                        this.currentState = State.READ_CHUNK_SIZE;
                        break;
                    }
                }
                buffer.readerIndex(rIdx);
                return;
            }
            case READ_CHUNK_FOOTER: {
                try {
                    final LastHttpContent trailer = this.readTrailingHeaders(buffer);
                    if (trailer == null) {
                        return;
                    }
                    out.add(trailer);
                    this.resetNow();
                }
                catch (final Exception e) {
                    out.add(this.invalidChunk(buffer, e));
                }
            }
            case BAD_MESSAGE: {
                buffer.skipBytes(buffer.readableBytes());
                break;
            }
            case UPGRADED: {
                final int readableBytes = buffer.readableBytes();
                if (readableBytes > 0) {
                    out.add(buffer.readBytes(readableBytes));
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    protected void decodeLast(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        super.decodeLast(ctx, in, out);
        if (this.resetRequested) {
            this.resetNow();
        }
        if (this.message != null) {
            final boolean chunked = HttpUtil.isTransferEncodingChunked(this.message);
            if (this.currentState == State.READ_VARIABLE_LENGTH_CONTENT && !in.isReadable() && !chunked) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                this.resetNow();
                return;
            }
            if (this.currentState == State.READ_HEADER) {
                out.add(this.invalidMessage(Unpooled.EMPTY_BUFFER, new PrematureChannelClosureException("Connection closed before received headers")));
                this.resetNow();
                return;
            }
            final boolean prematureClosure = this.isDecodingRequest() || chunked || this.contentLength() > 0L;
            if (!prematureClosure) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
            }
            this.resetNow();
        }
    }
    
    @Override
    public void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt instanceof HttpExpectationFailedEvent) {
            switch (this.currentState) {
                case READ_CHUNK_SIZE:
                case READ_VARIABLE_LENGTH_CONTENT:
                case READ_FIXED_LENGTH_CONTENT: {
                    this.reset();
                    break;
                }
            }
        }
        super.userEventTriggered(ctx, evt);
    }
    
    protected boolean isContentAlwaysEmpty(final HttpMessage msg) {
        if (!(msg instanceof HttpResponse)) {
            return false;
        }
        final HttpResponse res = (HttpResponse)msg;
        final int code = res.status().code();
        if (code >= 100 && code < 200) {
            return code != 101 || res.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_ACCEPT) || !res.headers().contains(HttpHeaderNames.UPGRADE, HttpHeaderValues.WEBSOCKET, true);
        }
        switch (code) {
            case 204:
            case 304: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    protected boolean isSwitchingToNonHttp1Protocol(final HttpResponse msg) {
        if (msg.status().code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code()) {
            return false;
        }
        final String newProtocol = msg.headers().get(HttpHeaderNames.UPGRADE);
        return newProtocol == null || (!newProtocol.contains(HttpVersion.HTTP_1_0.text()) && !newProtocol.contains(HttpVersion.HTTP_1_1.text()));
    }
    
    public void reset() {
        this.resetRequested = true;
    }
    
    private void resetNow() {
        final HttpMessage message = this.message;
        this.message = null;
        this.name = null;
        this.value = null;
        this.contentLength = Long.MIN_VALUE;
        this.lineParser.reset();
        this.headerParser.reset();
        this.trailer = null;
        if (!this.isDecodingRequest()) {
            final HttpResponse res = (HttpResponse)message;
            if (res != null && this.isSwitchingToNonHttp1Protocol(res)) {
                this.currentState = State.UPGRADED;
                return;
            }
        }
        this.resetRequested = false;
        this.currentState = State.SKIP_CONTROL_CHARS;
    }
    
    private HttpMessage invalidMessage(final ByteBuf in, final Exception cause) {
        this.currentState = State.BAD_MESSAGE;
        in.skipBytes(in.readableBytes());
        if (this.message == null) {
            this.message = this.createInvalidMessage();
        }
        this.message.setDecoderResult(DecoderResult.failure(cause));
        final HttpMessage ret = this.message;
        this.message = null;
        return ret;
    }
    
    private HttpContent invalidChunk(final ByteBuf in, final Exception cause) {
        this.currentState = State.BAD_MESSAGE;
        in.skipBytes(in.readableBytes());
        final HttpContent chunk = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
        chunk.setDecoderResult(DecoderResult.failure(cause));
        this.message = null;
        this.trailer = null;
        return chunk;
    }
    
    private State readHeaders(final ByteBuf buffer) {
        final HttpMessage message = this.message;
        final HttpHeaders headers = message.headers();
        AppendableCharSequence line = this.headerParser.parse(buffer);
        if (line == null) {
            return null;
        }
        if (line.length() > 0) {
            do {
                final char firstChar = line.charAtUnsafe(0);
                if (this.name != null && (firstChar == ' ' || firstChar == '\t')) {
                    final String trimmedLine = line.toString().trim();
                    final String valueStr = String.valueOf(this.value);
                    this.value = valueStr + ' ' + trimmedLine;
                }
                else {
                    if (this.name != null) {
                        headers.add(this.name, this.value);
                    }
                    this.splitHeader(line);
                }
                line = this.headerParser.parse(buffer);
                if (line == null) {
                    return null;
                }
            } while (line.length() > 0);
        }
        if (this.name != null) {
            headers.add(this.name, this.value);
        }
        this.name = null;
        this.value = null;
        final HttpMessageDecoderResult decoderResult = new HttpMessageDecoderResult(this.lineParser.size, this.headerParser.size);
        message.setDecoderResult(decoderResult);
        final List<String> contentLengthFields = headers.getAll(HttpHeaderNames.CONTENT_LENGTH);
        if (!contentLengthFields.isEmpty()) {
            final HttpVersion version = message.protocolVersion();
            final boolean isHttp10OrEarlier = version.majorVersion() < 1 || (version.majorVersion() == 1 && version.minorVersion() == 0);
            this.contentLength = HttpUtil.normalizeAndGetContentLength(contentLengthFields, isHttp10OrEarlier, this.allowDuplicateContentLengths);
            if (this.contentLength != -1L) {
                headers.set(HttpHeaderNames.CONTENT_LENGTH, this.contentLength);
            }
        }
        if (this.isContentAlwaysEmpty(message)) {
            HttpUtil.setTransferEncodingChunked(message, false);
            return State.SKIP_CONTROL_CHARS;
        }
        if (HttpUtil.isTransferEncodingChunked(message)) {
            if (!contentLengthFields.isEmpty() && message.protocolVersion() == HttpVersion.HTTP_1_1) {
                this.handleTransferEncodingChunkedWithContentLength(message);
            }
            return State.READ_CHUNK_SIZE;
        }
        if (this.contentLength() >= 0L) {
            return State.READ_FIXED_LENGTH_CONTENT;
        }
        return State.READ_VARIABLE_LENGTH_CONTENT;
    }
    
    protected void handleTransferEncodingChunkedWithContentLength(final HttpMessage message) {
        message.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
        this.contentLength = Long.MIN_VALUE;
    }
    
    private long contentLength() {
        if (this.contentLength == Long.MIN_VALUE) {
            this.contentLength = HttpUtil.getContentLength(this.message, -1L);
        }
        return this.contentLength;
    }
    
    private LastHttpContent readTrailingHeaders(final ByteBuf buffer) {
        AppendableCharSequence line = this.headerParser.parse(buffer);
        if (line == null) {
            return null;
        }
        LastHttpContent trailer = this.trailer;
        if (line.length() == 0 && trailer == null) {
            return LastHttpContent.EMPTY_LAST_CONTENT;
        }
        CharSequence lastHeader = null;
        if (trailer == null) {
            final DefaultLastHttpContent trailer2 = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);
            this.trailer = trailer2;
            trailer = trailer2;
        }
        while (line.length() > 0) {
            final char firstChar = line.charAtUnsafe(0);
            if (lastHeader != null && (firstChar == ' ' || firstChar == '\t')) {
                final List<String> current = trailer.trailingHeaders().getAll(lastHeader);
                if (!current.isEmpty()) {
                    final int lastPos = current.size() - 1;
                    final String lineTrimmed = line.toString().trim();
                    final String currentLastPos = current.get(lastPos);
                    current.set(lastPos, currentLastPos + lineTrimmed);
                }
            }
            else {
                this.splitHeader(line);
                final CharSequence headerName = this.name;
                if (!HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(headerName) && !HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(headerName) && !HttpHeaderNames.TRAILER.contentEqualsIgnoreCase(headerName)) {
                    trailer.trailingHeaders().add(headerName, this.value);
                }
                lastHeader = this.name;
                this.name = null;
                this.value = null;
            }
            line = this.headerParser.parse(buffer);
            if (line == null) {
                return null;
            }
        }
        this.trailer = null;
        return trailer;
    }
    
    protected abstract boolean isDecodingRequest();
    
    protected abstract HttpMessage createMessage(final String[] p0) throws Exception;
    
    protected abstract HttpMessage createInvalidMessage();
    
    private static int getChunkSize(String hex) {
        hex = hex.trim();
        for (int i = 0; i < hex.length(); ++i) {
            final char c = hex.charAt(i);
            if (c == ';' || Character.isWhitespace(c) || Character.isISOControl(c)) {
                hex = hex.substring(0, i);
                break;
            }
        }
        return Integer.parseInt(hex, 16);
    }
    
    private static String[] splitInitialLine(final AppendableCharSequence sb) {
        final int aStart = findNonSPLenient(sb, 0);
        final int aEnd = findSPLenient(sb, aStart);
        final int bStart = findNonSPLenient(sb, aEnd);
        final int bEnd = findSPLenient(sb, bStart);
        final int cStart = findNonSPLenient(sb, bEnd);
        final int cEnd = findEndOfString(sb);
        return new String[] { sb.subStringUnsafe(aStart, aEnd), sb.subStringUnsafe(bStart, bEnd), (cStart < cEnd) ? sb.subStringUnsafe(cStart, cEnd) : "" };
    }
    
    private void splitHeader(final AppendableCharSequence sb) {
        int length;
        int nameEnd;
        int nameStart;
        for (length = sb.length(), nameStart = (nameEnd = findNonWhitespace(sb, 0, false)); nameEnd < length; ++nameEnd) {
            final char ch = sb.charAtUnsafe(nameEnd);
            if (ch == ':') {
                break;
            }
            if (!this.isDecodingRequest() && isOWS(ch)) {
                break;
            }
        }
        if (nameEnd == length) {
            throw new IllegalArgumentException("No colon found");
        }
        int colonEnd;
        for (colonEnd = nameEnd; colonEnd < length; ++colonEnd) {
            if (sb.charAtUnsafe(colonEnd) == ':') {
                ++colonEnd;
                break;
            }
        }
        this.name = sb.subStringUnsafe(nameStart, nameEnd);
        final int valueStart = findNonWhitespace(sb, colonEnd, true);
        if (valueStart == length) {
            this.value = "";
        }
        else {
            final int valueEnd = findEndOfString(sb);
            this.value = sb.subStringUnsafe(valueStart, valueEnd);
        }
    }
    
    private static int findNonSPLenient(final AppendableCharSequence sb, final int offset) {
        int result = offset;
        while (result < sb.length()) {
            final char c = sb.charAtUnsafe(result);
            if (isSPLenient(c)) {
                ++result;
            }
            else {
                if (Character.isWhitespace(c)) {
                    throw new IllegalArgumentException("Invalid separator");
                }
                return result;
            }
        }
        return sb.length();
    }
    
    private static int findSPLenient(final AppendableCharSequence sb, final int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (isSPLenient(sb.charAtUnsafe(result))) {
                return result;
            }
        }
        return sb.length();
    }
    
    private static boolean isSPLenient(final char c) {
        return c == ' ' || c == '\t' || c == '\u000b' || c == '\f' || c == '\r';
    }
    
    private static int findNonWhitespace(final AppendableCharSequence sb, final int offset, final boolean validateOWS) {
        for (int result = offset; result < sb.length(); ++result) {
            final char c = sb.charAtUnsafe(result);
            if (!Character.isWhitespace(c)) {
                return result;
            }
            if (validateOWS && !isOWS(c)) {
                throw new IllegalArgumentException("Invalid separator, only a single space or horizontal tab allowed, but received a '" + c + "' (0x" + Integer.toHexString(c) + ")");
            }
        }
        return sb.length();
    }
    
    private static int findEndOfString(final AppendableCharSequence sb) {
        for (int result = sb.length() - 1; result > 0; --result) {
            if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
                return result + 1;
            }
        }
        return 0;
    }
    
    private static boolean isOWS(final char ch) {
        return ch == ' ' || ch == '\t';
    }
    
    private enum State
    {
        SKIP_CONTROL_CHARS, 
        READ_INITIAL, 
        READ_HEADER, 
        READ_VARIABLE_LENGTH_CONTENT, 
        READ_FIXED_LENGTH_CONTENT, 
        READ_CHUNK_SIZE, 
        READ_CHUNKED_CONTENT, 
        READ_CHUNK_DELIMITER, 
        READ_CHUNK_FOOTER, 
        BAD_MESSAGE, 
        UPGRADED;
    }
    
    private static class HeaderParser implements ByteProcessor
    {
        private final AppendableCharSequence seq;
        private final int maxLength;
        int size;
        
        HeaderParser(final AppendableCharSequence seq, final int maxLength) {
            this.seq = seq;
            this.maxLength = maxLength;
        }
        
        public AppendableCharSequence parse(final ByteBuf buffer) {
            final int oldSize = this.size;
            this.seq.reset();
            final int i = buffer.forEachByte(this);
            if (i == -1) {
                this.size = oldSize;
                return null;
            }
            buffer.readerIndex(i + 1);
            return this.seq;
        }
        
        public void reset() {
            this.size = 0;
        }
        
        @Override
        public boolean process(final byte value) throws Exception {
            final char nextByte = (char)(value & 0xFF);
            if (nextByte == '\n') {
                final int len = this.seq.length();
                if (len >= 1 && this.seq.charAtUnsafe(len - 1) == '\r') {
                    --this.size;
                    this.seq.setLength(len - 1);
                }
                return false;
            }
            this.increaseCount();
            this.seq.append(nextByte);
            return true;
        }
        
        protected final void increaseCount() {
            if (++this.size > this.maxLength) {
                throw this.newException(this.maxLength);
            }
        }
        
        protected TooLongFrameException newException(final int maxLength) {
            return new TooLongFrameException("HTTP header is larger than " + maxLength + " bytes.");
        }
    }
    
    private final class LineParser extends HeaderParser
    {
        LineParser(final AppendableCharSequence seq, final int maxLength) {
            super(seq, maxLength);
        }
        
        @Override
        public AppendableCharSequence parse(final ByteBuf buffer) {
            this.reset();
            return super.parse(buffer);
        }
        
        @Override
        public boolean process(final byte value) throws Exception {
            if (HttpObjectDecoder.this.currentState == State.SKIP_CONTROL_CHARS) {
                final char c = (char)(value & 0xFF);
                if (Character.isISOControl(c) || Character.isWhitespace(c)) {
                    this.increaseCount();
                    return true;
                }
                HttpObjectDecoder.this.currentState = State.READ_INITIAL;
            }
            return super.process(value);
        }
        
        @Override
        protected TooLongFrameException newException(final int maxLength) {
            return new TooLongFrameException("An HTTP line is larger than " + maxLength + " bytes.");
        }
    }
}
