package org.apache.coyote.http11;

import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.juli.logging.LogFactory;
import java.io.EOFException;
import java.nio.charset.StandardCharsets;
import org.apache.tomcat.util.http.HeaderUtil;
import java.io.IOException;
import org.apache.tomcat.util.buf.ByteChunk;
import java.util.Arrays;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.net.SocketWrapperBase;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.http.MimeHeaders;
import org.apache.coyote.Request;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.coyote.InputBuffer;

public class Http11InputBuffer implements InputBuffer, ApplicationBufferHandler
{
    private static final Log log;
    private static final StringManager sm;
    private static final byte[] CLIENT_PREFACE_START;
    private final Request request;
    private final MimeHeaders headers;
    private final boolean rejectIllegalHeader;
    private volatile boolean parsingHeader;
    private boolean swallowInput;
    private ByteBuffer byteBuffer;
    private int end;
    private SocketWrapperBase<?> wrapper;
    private InputBuffer inputStreamInputBuffer;
    private InputFilter[] filterLibrary;
    private InputFilter[] activeFilters;
    private int lastActiveFilter;
    private byte prevChr;
    private byte chr;
    private volatile boolean parsingRequestLine;
    private int parsingRequestLinePhase;
    private boolean parsingRequestLineEol;
    private int parsingRequestLineStart;
    private int parsingRequestLineQPos;
    private HeaderParsePosition headerParsePos;
    private final HeaderParseData headerData;
    private final HttpParser httpParser;
    private final int headerBufferSize;
    private int socketReadBufferSize;
    
    public Http11InputBuffer(final Request request, final int headerBufferSize, final boolean rejectIllegalHeader, final HttpParser httpParser) {
        this.prevChr = 0;
        this.chr = 0;
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
        this.headerData = new HeaderParseData();
        this.request = request;
        this.headers = request.getMimeHeaders();
        this.headerBufferSize = headerBufferSize;
        this.rejectIllegalHeader = rejectIllegalHeader;
        this.httpParser = httpParser;
        this.filterLibrary = new InputFilter[0];
        this.activeFilters = new InputFilter[0];
        this.lastActiveFilter = -1;
        this.parsingHeader = true;
        this.parsingRequestLine = true;
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
        this.headerParsePos = HeaderParsePosition.HEADER_START;
        this.swallowInput = true;
        this.inputStreamInputBuffer = new SocketInputBuffer();
    }
    
    void addFilter(final InputFilter filter) {
        if (filter == null) {
            throw new NullPointerException(Http11InputBuffer.sm.getString("iib.filter.npe"));
        }
        final InputFilter[] newFilterLibrary = Arrays.copyOf(this.filterLibrary, this.filterLibrary.length + 1);
        newFilterLibrary[this.filterLibrary.length] = filter;
        this.filterLibrary = newFilterLibrary;
        this.activeFilters = new InputFilter[this.filterLibrary.length];
    }
    
    InputFilter[] getFilters() {
        return this.filterLibrary;
    }
    
    void addActiveFilter(final InputFilter filter) {
        if (this.lastActiveFilter == -1) {
            filter.setBuffer(this.inputStreamInputBuffer);
        }
        else {
            for (int i = 0; i <= this.lastActiveFilter; ++i) {
                if (this.activeFilters[i] == filter) {
                    return;
                }
            }
            filter.setBuffer(this.activeFilters[this.lastActiveFilter]);
        }
        (this.activeFilters[++this.lastActiveFilter] = filter).setRequest(this.request);
    }
    
    void setSwallowInput(final boolean swallowInput) {
        this.swallowInput = swallowInput;
    }
    
    @Deprecated
    @Override
    public int doRead(final ByteChunk chunk) throws IOException {
        if (this.lastActiveFilter == -1) {
            return this.inputStreamInputBuffer.doRead(chunk);
        }
        return this.activeFilters[this.lastActiveFilter].doRead(chunk);
    }
    
    @Override
    public int doRead(final ApplicationBufferHandler handler) throws IOException {
        if (this.lastActiveFilter == -1) {
            return this.inputStreamInputBuffer.doRead(handler);
        }
        return this.activeFilters[this.lastActiveFilter].doRead(handler);
    }
    
    void recycle() {
        this.wrapper = null;
        this.request.recycle();
        for (int i = 0; i <= this.lastActiveFilter; ++i) {
            this.activeFilters[i].recycle();
        }
        this.byteBuffer.limit(0).position(0);
        this.lastActiveFilter = -1;
        this.swallowInput = true;
        this.chr = 0;
        this.prevChr = 0;
        this.headerParsePos = HeaderParsePosition.HEADER_START;
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
        this.headerData.recycle();
        this.parsingRequestLine = true;
        this.parsingHeader = true;
    }
    
    void nextRequest() {
        this.request.recycle();
        if (this.byteBuffer.position() > 0) {
            if (this.byteBuffer.remaining() > 0) {
                this.byteBuffer.compact();
                this.byteBuffer.flip();
            }
            else {
                this.byteBuffer.position(0).limit(0);
            }
        }
        for (int i = 0; i <= this.lastActiveFilter; ++i) {
            this.activeFilters[i].recycle();
        }
        this.lastActiveFilter = -1;
        this.parsingHeader = true;
        this.swallowInput = true;
        this.headerParsePos = HeaderParsePosition.HEADER_START;
        this.parsingRequestLine = true;
        this.parsingRequestLinePhase = 0;
        this.parsingRequestLineEol = false;
        this.parsingRequestLineStart = 0;
        this.parsingRequestLineQPos = -1;
        this.headerData.recycle();
    }
    
    boolean parseRequestLine(final boolean keptAlive) throws IOException {
        if (!this.parsingRequestLine) {
            return true;
        }
        if (this.parsingRequestLinePhase < 2) {
            do {
                if (this.byteBuffer.position() >= this.byteBuffer.limit()) {
                    if (keptAlive) {
                        this.wrapper.setReadTimeout(this.wrapper.getEndpoint().getKeepAliveTimeout());
                    }
                    if (!this.fill(false)) {
                        this.parsingRequestLinePhase = 1;
                        return false;
                    }
                    this.wrapper.setReadTimeout(this.wrapper.getEndpoint().getConnectionTimeout());
                }
                if (!keptAlive && this.byteBuffer.position() == 0 && this.byteBuffer.limit() >= Http11InputBuffer.CLIENT_PREFACE_START.length - 1) {
                    boolean prefaceMatch = true;
                    for (int i = 0; i < Http11InputBuffer.CLIENT_PREFACE_START.length && prefaceMatch; ++i) {
                        if (Http11InputBuffer.CLIENT_PREFACE_START[i] != this.byteBuffer.get(i)) {
                            prefaceMatch = false;
                        }
                    }
                    if (prefaceMatch) {
                        this.parsingRequestLinePhase = -1;
                        return false;
                    }
                }
                if (this.request.getStartTime() < 0L) {
                    this.request.setStartTime(System.currentTimeMillis());
                }
                this.chr = this.byteBuffer.get();
            } while (this.chr == 13 || this.chr == 10);
            this.byteBuffer.position(this.byteBuffer.position() - 1);
            this.parsingRequestLineStart = this.byteBuffer.position();
            this.parsingRequestLinePhase = 2;
        }
        if (this.parsingRequestLinePhase == 2) {
            boolean space = false;
            while (!space) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !this.fill(false)) {
                    return false;
                }
                final int pos = this.byteBuffer.position();
                this.chr = this.byteBuffer.get();
                if (this.chr == 32 || this.chr == 9) {
                    space = true;
                    this.request.method().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, pos - this.parsingRequestLineStart);
                }
                else {
                    if (!HttpParser.isToken(this.chr)) {
                        this.request.protocol().setString("HTTP/1.1");
                        final String invalidMethodValue = this.parseInvalid(this.parsingRequestLineStart, this.byteBuffer);
                        throw new IllegalArgumentException(Http11InputBuffer.sm.getString("iib.invalidmethod", new Object[] { invalidMethodValue }));
                    }
                    continue;
                }
            }
            this.parsingRequestLinePhase = 3;
        }
        if (this.parsingRequestLinePhase == 3) {
            boolean space = true;
            while (space) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !this.fill(false)) {
                    return false;
                }
                this.chr = this.byteBuffer.get();
                if (this.chr == 32 || this.chr == 9) {
                    continue;
                }
                space = false;
                this.byteBuffer.position(this.byteBuffer.position() - 1);
            }
            this.parsingRequestLineStart = this.byteBuffer.position();
            this.parsingRequestLinePhase = 4;
        }
        if (this.parsingRequestLinePhase == 4) {
            int end = 0;
            boolean space2 = false;
            while (!space2) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !this.fill(false)) {
                    return false;
                }
                final int pos2 = this.byteBuffer.position();
                this.prevChr = this.chr;
                this.chr = this.byteBuffer.get();
                if (this.prevChr == 13 && this.chr != 10) {
                    this.request.protocol().setString("HTTP/1.1");
                    final String invalidRequestTarget = this.parseInvalid(this.parsingRequestLineStart, this.byteBuffer);
                    throw new IllegalArgumentException(Http11InputBuffer.sm.getString("iib.invalidRequestTarget", new Object[] { invalidRequestTarget }));
                }
                if (this.chr == 32 || this.chr == 9) {
                    space2 = true;
                    end = pos2;
                }
                else {
                    if (this.chr == 13) {
                        continue;
                    }
                    if (this.chr == 10) {
                        space2 = true;
                        this.request.protocol().setString("");
                        this.parsingRequestLinePhase = 7;
                        if (this.prevChr == 13) {
                            end = pos2 - 1;
                        }
                        else {
                            end = pos2;
                        }
                    }
                    else if (this.chr == 63 && this.parsingRequestLineQPos == -1) {
                        this.parsingRequestLineQPos = pos2;
                    }
                    else {
                        if (this.parsingRequestLineQPos != -1 && !this.httpParser.isQueryRelaxed(this.chr)) {
                            this.request.protocol().setString("HTTP/1.1");
                            final String invalidRequestTarget = this.parseInvalid(this.parsingRequestLineStart, this.byteBuffer);
                            throw new IllegalArgumentException(Http11InputBuffer.sm.getString("iib.invalidRequestTarget", new Object[] { invalidRequestTarget }));
                        }
                        if (this.httpParser.isNotRequestTargetRelaxed(this.chr)) {
                            this.request.protocol().setString("HTTP/1.1");
                            final String invalidRequestTarget = this.parseInvalid(this.parsingRequestLineStart, this.byteBuffer);
                            throw new IllegalArgumentException(Http11InputBuffer.sm.getString("iib.invalidRequestTarget", new Object[] { invalidRequestTarget }));
                        }
                        continue;
                    }
                }
            }
            if (this.parsingRequestLineQPos >= 0) {
                this.request.queryString().setBytes(this.byteBuffer.array(), this.parsingRequestLineQPos + 1, end - this.parsingRequestLineQPos - 1);
                this.request.requestURI().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, this.parsingRequestLineQPos - this.parsingRequestLineStart);
            }
            else {
                this.request.requestURI().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, end - this.parsingRequestLineStart);
            }
            if (this.parsingRequestLinePhase == 4) {
                this.parsingRequestLinePhase = 5;
            }
        }
        if (this.parsingRequestLinePhase == 5) {
            boolean space = true;
            while (space) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !this.fill(false)) {
                    return false;
                }
                final byte chr = this.byteBuffer.get();
                if (chr == 32 || chr == 9) {
                    continue;
                }
                space = false;
                this.byteBuffer.position(this.byteBuffer.position() - 1);
            }
            this.parsingRequestLineStart = this.byteBuffer.position();
            this.parsingRequestLinePhase = 6;
            this.end = 0;
        }
        if (this.parsingRequestLinePhase == 6) {
            while (!this.parsingRequestLineEol) {
                if (this.byteBuffer.position() >= this.byteBuffer.limit() && !this.fill(false)) {
                    return false;
                }
                final int pos3 = this.byteBuffer.position();
                this.prevChr = this.chr;
                this.chr = this.byteBuffer.get();
                if (this.chr == 13) {
                    continue;
                }
                if (this.prevChr == 13 && this.chr == 10) {
                    this.end = pos3 - 1;
                    this.parsingRequestLineEol = true;
                }
                else if (this.chr == 10) {
                    this.end = pos3;
                    this.parsingRequestLineEol = true;
                }
                else {
                    if (this.prevChr == 13 || !HttpParser.isHttpProtocol(this.chr)) {
                        final String invalidProtocol = this.parseInvalid(this.parsingRequestLineStart, this.byteBuffer);
                        throw new IllegalArgumentException(Http11InputBuffer.sm.getString("iib.invalidHttpProtocol", new Object[] { invalidProtocol }));
                    }
                    continue;
                }
            }
            if (this.end - this.parsingRequestLineStart > 0) {
                this.request.protocol().setBytes(this.byteBuffer.array(), this.parsingRequestLineStart, this.end - this.parsingRequestLineStart);
                this.parsingRequestLinePhase = 7;
            }
        }
        if (this.parsingRequestLinePhase == 7) {
            this.parsingRequestLine = false;
            this.parsingRequestLinePhase = 0;
            this.parsingRequestLineEol = false;
            this.parsingRequestLineStart = 0;
            return true;
        }
        throw new IllegalStateException(Http11InputBuffer.sm.getString("iib.invalidPhase", new Object[] { this.parsingRequestLinePhase }));
    }
    
    boolean parseHeaders() throws IOException {
        if (!this.parsingHeader) {
            throw new IllegalStateException(Http11InputBuffer.sm.getString("iib.parseheaders.ise.error"));
        }
        HeaderParseStatus status = HeaderParseStatus.HAVE_MORE_HEADERS;
        do {
            status = this.parseHeader();
            if (this.byteBuffer.position() > this.headerBufferSize || this.byteBuffer.capacity() - this.byteBuffer.position() < this.socketReadBufferSize) {
                throw new IllegalArgumentException(Http11InputBuffer.sm.getString("iib.requestheadertoolarge.error"));
            }
        } while (status == HeaderParseStatus.HAVE_MORE_HEADERS);
        if (status == HeaderParseStatus.DONE) {
            this.parsingHeader = false;
            this.end = this.byteBuffer.position();
            return true;
        }
        return false;
    }
    
    int getParsingRequestLinePhase() {
        return this.parsingRequestLinePhase;
    }
    
    private String parseInvalid(final int startPos, final ByteBuffer buffer) {
        byte b;
        for (b = 0; buffer.hasRemaining() && b != 32; b = buffer.get()) {}
        String result = HeaderUtil.toPrintableString(buffer.array(), buffer.arrayOffset() + startPos, buffer.position() - startPos);
        if (b != 32) {
            result += "...";
        }
        return result;
    }
    
    void endRequest() throws IOException {
        if (this.swallowInput && this.lastActiveFilter != -1) {
            final int extraBytes = (int)this.activeFilters[this.lastActiveFilter].end();
            this.byteBuffer.position(this.byteBuffer.position() - extraBytes);
        }
    }
    
    @Override
    public int available() {
        return this.available(false);
    }
    
    int available(final boolean read) {
        int available;
        if (this.lastActiveFilter == -1) {
            available = this.inputStreamInputBuffer.available();
        }
        else {
            available = this.activeFilters[this.lastActiveFilter].available();
        }
        try {
            if (available == 0 && read && !this.byteBuffer.hasRemaining() && this.wrapper.hasDataToRead()) {
                this.fill(false);
                available = this.byteBuffer.remaining();
            }
        }
        catch (final IOException ioe) {
            if (Http11InputBuffer.log.isDebugEnabled()) {
                Http11InputBuffer.log.debug((Object)Http11InputBuffer.sm.getString("iib.available.readFail"), (Throwable)ioe);
            }
            available = 1;
        }
        return available;
    }
    
    boolean isFinished() {
        return this.lastActiveFilter >= 0 && this.activeFilters[this.lastActiveFilter].isFinished();
    }
    
    ByteBuffer getLeftover() {
        final int available = this.byteBuffer.remaining();
        if (available > 0) {
            return ByteBuffer.wrap(this.byteBuffer.array(), this.byteBuffer.position(), available);
        }
        return null;
    }
    
    void init(final SocketWrapperBase<?> socketWrapper) {
        (this.wrapper = socketWrapper).setAppReadBufHandler(this);
        final int bufLength = this.headerBufferSize + this.wrapper.getSocketBufferHandler().getReadBuffer().capacity();
        if (this.byteBuffer == null || this.byteBuffer.capacity() < bufLength) {
            this.byteBuffer = ByteBuffer.allocate(bufLength);
            this.byteBuffer.position(0).limit(0);
        }
    }
    
    private boolean fill(final boolean block) throws IOException {
        if (Http11InputBuffer.log.isDebugEnabled()) {
            Http11InputBuffer.log.debug((Object)("Before fill(): parsingHeader: [" + this.parsingHeader + "], parsingRequestLine: [" + this.parsingRequestLine + "], parsingRequestLinePhase: [" + this.parsingRequestLinePhase + "], parsingRequestLineStart: [" + this.parsingRequestLineStart + "], byteBuffer.position(): [" + this.byteBuffer.position() + "], byteBuffer.limit(): [" + this.byteBuffer.limit() + "], end: [" + this.end + "]"));
        }
        if (this.parsingHeader) {
            if (this.byteBuffer.limit() >= this.headerBufferSize) {
                if (this.parsingRequestLine) {
                    this.request.protocol().setString("HTTP/1.1");
                }
                throw new IllegalArgumentException(Http11InputBuffer.sm.getString("iib.requestheadertoolarge.error"));
            }
        }
        else {
            this.byteBuffer.limit(this.end).position(this.end);
        }
        int nRead = -1;
        this.byteBuffer.mark();
        try {
            if (this.byteBuffer.position() < this.byteBuffer.limit()) {
                this.byteBuffer.position(this.byteBuffer.limit());
            }
            this.byteBuffer.limit(this.byteBuffer.capacity());
            nRead = this.wrapper.read(block, this.byteBuffer);
        }
        finally {
            this.byteBuffer.limit(this.byteBuffer.position()).reset();
        }
        if (Http11InputBuffer.log.isDebugEnabled()) {
            Http11InputBuffer.log.debug((Object)("Received [" + new String(this.byteBuffer.array(), this.byteBuffer.position(), this.byteBuffer.remaining(), StandardCharsets.ISO_8859_1) + "]"));
        }
        if (nRead > 0) {
            return true;
        }
        if (nRead == -1) {
            throw new EOFException(Http11InputBuffer.sm.getString("iib.eof.error"));
        }
        return false;
    }
    
    private HeaderParseStatus parseHeader() throws IOException {
        while (this.headerParsePos == HeaderParsePosition.HEADER_START) {
            if (this.byteBuffer.position() >= this.byteBuffer.limit() && !this.fill(false)) {
                this.headerParsePos = HeaderParsePosition.HEADER_START;
                return HeaderParseStatus.NEED_MORE_DATA;
            }
            this.prevChr = this.chr;
            this.chr = this.byteBuffer.get();
            if (this.chr == 13 && this.prevChr != 13) {
                continue;
            }
            if (this.chr == 10) {
                return HeaderParseStatus.DONE;
            }
            if (this.prevChr == 13) {
                this.byteBuffer.position(this.byteBuffer.position() - 2);
                break;
            }
            this.byteBuffer.position(this.byteBuffer.position() - 1);
            break;
        }
        if (this.headerParsePos == HeaderParsePosition.HEADER_START) {
            this.headerData.start = this.byteBuffer.position();
            this.headerData.lineStart = this.headerData.start;
            this.headerParsePos = HeaderParsePosition.HEADER_NAME;
        }
        while (this.headerParsePos == HeaderParsePosition.HEADER_NAME) {
            if (this.byteBuffer.position() >= this.byteBuffer.limit() && !this.fill(false)) {
                return HeaderParseStatus.NEED_MORE_DATA;
            }
            int pos = this.byteBuffer.position();
            this.chr = this.byteBuffer.get();
            if (this.chr == 58) {
                this.headerParsePos = HeaderParsePosition.HEADER_VALUE_START;
                this.headerData.headerValue = this.headers.addValue(this.byteBuffer.array(), this.headerData.start, pos - this.headerData.start);
                pos = this.byteBuffer.position();
                this.headerData.start = pos;
                this.headerData.realPos = pos;
                this.headerData.lastSignificantChar = pos;
                break;
            }
            if (!HttpParser.isToken(this.chr)) {
                this.headerData.lastSignificantChar = pos;
                this.byteBuffer.position(this.byteBuffer.position() - 1);
                return this.skipLine();
            }
            if (this.chr < 65 || this.chr > 90) {
                continue;
            }
            this.byteBuffer.put(pos, (byte)(this.chr + 32));
        }
        if (this.headerParsePos == HeaderParsePosition.HEADER_SKIPLINE) {
            return this.skipLine();
        }
        while (this.headerParsePos == HeaderParsePosition.HEADER_VALUE_START || this.headerParsePos == HeaderParsePosition.HEADER_VALUE || this.headerParsePos == HeaderParsePosition.HEADER_MULTI_LINE) {
            Label_0557: {
                if (this.headerParsePos == HeaderParsePosition.HEADER_VALUE_START) {
                    while (this.byteBuffer.position() < this.byteBuffer.limit() || this.fill(false)) {
                        this.chr = this.byteBuffer.get();
                        if (this.chr != 32 && this.chr != 9) {
                            this.headerParsePos = HeaderParsePosition.HEADER_VALUE;
                            this.byteBuffer.position(this.byteBuffer.position() - 1);
                            break Label_0557;
                        }
                    }
                    return HeaderParseStatus.NEED_MORE_DATA;
                }
            }
            if (this.headerParsePos == HeaderParsePosition.HEADER_VALUE) {
                boolean eol = false;
                while (!eol) {
                    if (this.byteBuffer.position() >= this.byteBuffer.limit() && !this.fill(false)) {
                        return HeaderParseStatus.NEED_MORE_DATA;
                    }
                    this.prevChr = this.chr;
                    this.chr = this.byteBuffer.get();
                    if (this.chr == 13) {
                        continue;
                    }
                    if (this.chr == 10) {
                        eol = true;
                    }
                    else {
                        if (this.prevChr == 13) {
                            this.headers.removeHeader(this.headers.size() - 1);
                            return this.skipLine();
                        }
                        if (this.chr != 9 && HttpParser.isControl(this.chr)) {
                            this.headers.removeHeader(this.headers.size() - 1);
                            return this.skipLine();
                        }
                        if (this.chr == 32 || this.chr == 9) {
                            this.byteBuffer.put(this.headerData.realPos, this.chr);
                            final HeaderParseData headerData = this.headerData;
                            ++headerData.realPos;
                        }
                        else {
                            this.byteBuffer.put(this.headerData.realPos, this.chr);
                            final HeaderParseData headerData2 = this.headerData;
                            ++headerData2.realPos;
                            this.headerData.lastSignificantChar = this.headerData.realPos;
                        }
                    }
                }
                this.headerData.realPos = this.headerData.lastSignificantChar;
                this.headerParsePos = HeaderParsePosition.HEADER_MULTI_LINE;
            }
            if (this.byteBuffer.position() >= this.byteBuffer.limit() && !this.fill(false)) {
                return HeaderParseStatus.NEED_MORE_DATA;
            }
            final byte peek = this.byteBuffer.get(this.byteBuffer.position());
            if (this.headerParsePos != HeaderParsePosition.HEADER_MULTI_LINE) {
                continue;
            }
            if (peek != 32 && peek != 9) {
                this.headerParsePos = HeaderParsePosition.HEADER_START;
                break;
            }
            this.byteBuffer.put(this.headerData.realPos, peek);
            final HeaderParseData headerData3 = this.headerData;
            ++headerData3.realPos;
            this.headerParsePos = HeaderParsePosition.HEADER_VALUE_START;
        }
        this.headerData.headerValue.setBytes(this.byteBuffer.array(), this.headerData.start, this.headerData.lastSignificantChar - this.headerData.start);
        this.headerData.recycle();
        return HeaderParseStatus.HAVE_MORE_HEADERS;
    }
    
    private HeaderParseStatus skipLine() throws IOException {
        this.headerParsePos = HeaderParsePosition.HEADER_SKIPLINE;
        boolean eol = false;
        while (!eol) {
            if (this.byteBuffer.position() >= this.byteBuffer.limit() && !this.fill(false)) {
                return HeaderParseStatus.NEED_MORE_DATA;
            }
            final int pos = this.byteBuffer.position();
            this.prevChr = this.chr;
            this.chr = this.byteBuffer.get();
            if (this.chr == 13) {
                continue;
            }
            if (this.chr == 10) {
                eol = true;
            }
            else {
                this.headerData.lastSignificantChar = pos;
            }
        }
        if (this.rejectIllegalHeader || Http11InputBuffer.log.isDebugEnabled()) {
            final String message = Http11InputBuffer.sm.getString("iib.invalidheader", new Object[] { HeaderUtil.toPrintableString(this.byteBuffer.array(), this.headerData.lineStart, this.headerData.lastSignificantChar - this.headerData.lineStart + 1) });
            if (this.rejectIllegalHeader) {
                throw new IllegalArgumentException(message);
            }
            Http11InputBuffer.log.debug((Object)message);
        }
        this.headerParsePos = HeaderParsePosition.HEADER_START;
        return HeaderParseStatus.HAVE_MORE_HEADERS;
    }
    
    @Override
    public void setByteBuffer(final ByteBuffer buffer) {
        this.byteBuffer = buffer;
    }
    
    @Override
    public ByteBuffer getByteBuffer() {
        return this.byteBuffer;
    }
    
    @Override
    public void expand(final int size) {
        if (this.byteBuffer.capacity() >= size) {
            this.byteBuffer.limit(size);
        }
        ByteBuffer temp = ByteBuffer.allocate(size);
        temp.put(this.byteBuffer);
        (this.byteBuffer = temp).mark();
        temp = null;
    }
    
    static {
        log = LogFactory.getLog((Class)Http11InputBuffer.class);
        sm = StringManager.getManager((Class)Http11InputBuffer.class);
        CLIENT_PREFACE_START = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n".getBytes(StandardCharsets.ISO_8859_1);
    }
    
    private enum HeaderParseStatus
    {
        DONE, 
        HAVE_MORE_HEADERS, 
        NEED_MORE_DATA;
    }
    
    private enum HeaderParsePosition
    {
        HEADER_START, 
        HEADER_NAME, 
        HEADER_VALUE_START, 
        HEADER_VALUE, 
        HEADER_MULTI_LINE, 
        HEADER_SKIPLINE;
    }
    
    private static class HeaderParseData
    {
        int lineStart;
        int start;
        int realPos;
        int lastSignificantChar;
        MessageBytes headerValue;
        
        private HeaderParseData() {
            this.lineStart = 0;
            this.start = 0;
            this.realPos = 0;
            this.lastSignificantChar = 0;
            this.headerValue = null;
        }
        
        public void recycle() {
            this.lineStart = 0;
            this.start = 0;
            this.realPos = 0;
            this.lastSignificantChar = 0;
            this.headerValue = null;
        }
    }
    
    private class SocketInputBuffer implements InputBuffer
    {
        @Deprecated
        @Override
        public int doRead(final ByteChunk chunk) throws IOException {
            if (Http11InputBuffer.this.byteBuffer.position() >= Http11InputBuffer.this.byteBuffer.limit() && !Http11InputBuffer.this.fill(true)) {
                return -1;
            }
            final int length = Http11InputBuffer.this.byteBuffer.remaining();
            chunk.setBytes(Http11InputBuffer.this.byteBuffer.array(), Http11InputBuffer.this.byteBuffer.position(), length);
            Http11InputBuffer.this.byteBuffer.position(Http11InputBuffer.this.byteBuffer.limit());
            return length;
        }
        
        @Override
        public int doRead(final ApplicationBufferHandler handler) throws IOException {
            if (Http11InputBuffer.this.byteBuffer.position() >= Http11InputBuffer.this.byteBuffer.limit()) {
                final boolean block = Http11InputBuffer.this.request.getReadListener() == null;
                if (!Http11InputBuffer.this.fill(block)) {
                    if (block) {
                        return -1;
                    }
                    return 0;
                }
            }
            final int length = Http11InputBuffer.this.byteBuffer.remaining();
            handler.setByteBuffer(Http11InputBuffer.this.byteBuffer.duplicate());
            Http11InputBuffer.this.byteBuffer.position(Http11InputBuffer.this.byteBuffer.limit());
            return length;
        }
        
        @Override
        public int available() {
            return Http11InputBuffer.this.byteBuffer.remaining();
        }
    }
}
