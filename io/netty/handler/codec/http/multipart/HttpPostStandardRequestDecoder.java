package io.netty.handler.codec.http.multipart;

import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import io.netty.util.ByteProcessor;
import io.netty.handler.codec.http.QueryStringDecoder;
import java.io.IOException;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.internal.PlatformDependent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.util.internal.ObjectUtil;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.ArrayList;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.buffer.ByteBuf;
import java.util.Map;
import java.util.List;
import java.nio.charset.Charset;
import io.netty.handler.codec.http.HttpRequest;

public class HttpPostStandardRequestDecoder implements InterfaceHttpPostRequestDecoder
{
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private final Charset charset;
    private boolean isLastChunk;
    private final List<InterfaceHttpData> bodyListHttpData;
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData;
    private ByteBuf undecodedChunk;
    private int bodyListHttpDataRank;
    private HttpPostRequestDecoder.MultiPartStatus currentStatus;
    private Attribute currentAttribute;
    private boolean destroyed;
    private int discardThreshold;
    
    public HttpPostStandardRequestDecoder(final HttpRequest request) {
        this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
    }
    
    public HttpPostStandardRequestDecoder(final HttpDataFactory factory, final HttpRequest request) {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }
    
    public HttpPostStandardRequestDecoder(final HttpDataFactory factory, final HttpRequest request, final Charset charset) {
        this.bodyListHttpData = new ArrayList<InterfaceHttpData>();
        this.bodyMapHttpData = new TreeMap<String, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
        this.discardThreshold = 10485760;
        this.request = ObjectUtil.checkNotNull(request, "request");
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
        this.factory = ObjectUtil.checkNotNull(factory, "factory");
        try {
            if (request instanceof HttpContent) {
                this.offer((HttpContent)request);
            }
            else {
                this.parseBody();
            }
        }
        catch (final Throwable e) {
            this.destroy();
            PlatformDependent.throwException(e);
        }
    }
    
    private void checkDestroyed() {
        if (this.destroyed) {
            throw new IllegalStateException(HttpPostStandardRequestDecoder.class.getSimpleName() + " was destroyed already");
        }
    }
    
    @Override
    public boolean isMultipart() {
        this.checkDestroyed();
        return false;
    }
    
    @Override
    public void setDiscardThreshold(final int discardThreshold) {
        this.discardThreshold = ObjectUtil.checkPositiveOrZero(discardThreshold, "discardThreshold");
    }
    
    @Override
    public int getDiscardThreshold() {
        return this.discardThreshold;
    }
    
    @Override
    public List<InterfaceHttpData> getBodyHttpDatas() {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        return this.bodyListHttpData;
    }
    
    @Override
    public List<InterfaceHttpData> getBodyHttpDatas(final String name) {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        return this.bodyMapHttpData.get(name);
    }
    
    @Override
    public InterfaceHttpData getBodyHttpData(final String name) {
        this.checkDestroyed();
        if (!this.isLastChunk) {
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
        }
        final List<InterfaceHttpData> list = this.bodyMapHttpData.get(name);
        if (list != null) {
            return list.get(0);
        }
        return null;
    }
    
    @Override
    public HttpPostStandardRequestDecoder offer(final HttpContent content) {
        this.checkDestroyed();
        if (content instanceof LastHttpContent) {
            this.isLastChunk = true;
        }
        final ByteBuf buf = content.content();
        if (this.undecodedChunk == null) {
            this.undecodedChunk = buf.alloc().buffer(buf.readableBytes()).writeBytes(buf);
        }
        else {
            this.undecodedChunk.writeBytes(buf);
        }
        this.parseBody();
        if (this.undecodedChunk != null && this.undecodedChunk.writerIndex() > this.discardThreshold) {
            if (this.undecodedChunk.refCnt() == 1) {
                this.undecodedChunk.discardReadBytes();
            }
            else {
                final ByteBuf buffer = this.undecodedChunk.alloc().buffer(this.undecodedChunk.readableBytes());
                buffer.writeBytes(this.undecodedChunk);
                this.undecodedChunk.release();
                this.undecodedChunk = buffer;
            }
        }
        return this;
    }
    
    @Override
    public boolean hasNext() {
        this.checkDestroyed();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE && this.bodyListHttpDataRank >= this.bodyListHttpData.size()) {
            throw new HttpPostRequestDecoder.EndOfDataDecoderException();
        }
        return !this.bodyListHttpData.isEmpty() && this.bodyListHttpDataRank < this.bodyListHttpData.size();
    }
    
    @Override
    public InterfaceHttpData next() {
        this.checkDestroyed();
        if (this.hasNext()) {
            return this.bodyListHttpData.get(this.bodyListHttpDataRank++);
        }
        return null;
    }
    
    @Override
    public InterfaceHttpData currentPartialHttpData() {
        return this.currentAttribute;
    }
    
    private void parseBody() {
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
            if (this.isLastChunk) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            }
            return;
        }
        this.parseBodyAttributes();
    }
    
    protected void addHttpData(final InterfaceHttpData data) {
        if (data == null) {
            return;
        }
        List<InterfaceHttpData> datas = this.bodyMapHttpData.get(data.getName());
        if (datas == null) {
            datas = new ArrayList<InterfaceHttpData>(1);
            this.bodyMapHttpData.put(data.getName(), datas);
        }
        datas.add(data);
        this.bodyListHttpData.add(data);
    }
    
    private void parseBodyAttributesStandard() {
        int currentpos;
        int firstpos = currentpos = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        try {
            while (this.undecodedChunk.isReadable() && contRead) {
                char read = (char)this.undecodedChunk.readUnsignedByte();
                ++currentpos;
                switch (this.currentStatus) {
                    case DISPOSITION: {
                        if (read == '=') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                            final int equalpos = currentpos - 1;
                            final String key = decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, key);
                            firstpos = currentpos;
                            continue;
                        }
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            final int ampersandpos = currentpos - 1;
                            final String key = decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                            (this.currentAttribute = this.factory.createAttribute(this.request, key)).setValue("");
                            this.addHttpData(this.currentAttribute);
                            this.currentAttribute = null;
                            firstpos = currentpos;
                            contRead = true;
                            continue;
                        }
                        continue;
                    }
                    case FIELD: {
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            final int ampersandpos = currentpos - 1;
                            this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                            firstpos = currentpos;
                            contRead = true;
                            continue;
                        }
                        if (read == '\r') {
                            if (!this.undecodedChunk.isReadable()) {
                                --currentpos;
                                continue;
                            }
                            read = (char)this.undecodedChunk.readUnsignedByte();
                            ++currentpos;
                            if (read == '\n') {
                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                final int ampersandpos = currentpos - 2;
                                this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                continue;
                            }
                            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                        }
                        else {
                            if (read == '\n') {
                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                final int ampersandpos = currentpos - 1;
                                this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                continue;
                            }
                            continue;
                        }
                        break;
                    }
                    default: {
                        contRead = false;
                        continue;
                    }
                }
            }
            if (this.isLastChunk && this.currentAttribute != null) {
                final int ampersandpos = currentpos;
                if (ampersandpos > firstpos) {
                    this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                }
                else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
                }
                firstpos = currentpos;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            }
            else if (contRead && this.currentAttribute != null && this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                this.currentAttribute.addContent(this.undecodedChunk.retainedSlice(firstpos, currentpos - firstpos), false);
                firstpos = currentpos;
            }
            this.undecodedChunk.readerIndex(firstpos);
        }
        catch (final HttpPostRequestDecoder.ErrorDataDecoderException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw e;
        }
        catch (final IOException e2) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
        }
        catch (final IllegalArgumentException e3) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
        }
    }
    
    private void parseBodyAttributes() {
        if (this.undecodedChunk == null) {
            return;
        }
        if (!this.undecodedChunk.hasArray()) {
            this.parseBodyAttributesStandard();
            return;
        }
        final HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(this.undecodedChunk);
        int currentpos;
        int firstpos = currentpos = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED) {
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
        }
        boolean contRead = true;
        try {
        Label_0528:
            while (sao.pos < sao.limit) {
                char read = (char)(sao.bytes[sao.pos++] & 0xFF);
                ++currentpos;
                switch (this.currentStatus) {
                    case DISPOSITION: {
                        if (read == '=') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
                            final int equalpos = currentpos - 1;
                            final String key = decodeAttribute(this.undecodedChunk.toString(firstpos, equalpos - firstpos, this.charset), this.charset);
                            this.currentAttribute = this.factory.createAttribute(this.request, key);
                            firstpos = currentpos;
                            continue;
                        }
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            final int ampersandpos = currentpos - 1;
                            final String key = decodeAttribute(this.undecodedChunk.toString(firstpos, ampersandpos - firstpos, this.charset), this.charset);
                            (this.currentAttribute = this.factory.createAttribute(this.request, key)).setValue("");
                            this.addHttpData(this.currentAttribute);
                            this.currentAttribute = null;
                            firstpos = currentpos;
                            contRead = true;
                            continue;
                        }
                        continue;
                    }
                    case FIELD: {
                        if (read == '&') {
                            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.DISPOSITION;
                            final int ampersandpos = currentpos - 1;
                            this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                            firstpos = currentpos;
                            contRead = true;
                            continue;
                        }
                        if (read == '\r') {
                            if (sao.pos < sao.limit) {
                                read = (char)(sao.bytes[sao.pos++] & 0xFF);
                                ++currentpos;
                                if (read == '\n') {
                                    this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                    final int ampersandpos = currentpos - 2;
                                    sao.setReadPosition(0);
                                    this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                                    firstpos = currentpos;
                                    contRead = false;
                                    break Label_0528;
                                }
                                sao.setReadPosition(0);
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad end of line");
                            }
                            else {
                                if (sao.limit > 0) {
                                    --currentpos;
                                    continue;
                                }
                                continue;
                            }
                        }
                        else {
                            if (read == '\n') {
                                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE;
                                final int ampersandpos = currentpos - 1;
                                sao.setReadPosition(0);
                                this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                                firstpos = currentpos;
                                contRead = false;
                                break Label_0528;
                            }
                            continue;
                        }
                        break;
                    }
                    default: {
                        sao.setReadPosition(0);
                        contRead = false;
                        break Label_0528;
                    }
                }
            }
            if (this.isLastChunk && this.currentAttribute != null) {
                final int ampersandpos = currentpos;
                if (ampersandpos > firstpos) {
                    this.setFinalBuffer(this.undecodedChunk.retainedSlice(firstpos, ampersandpos - firstpos));
                }
                else if (!this.currentAttribute.isCompleted()) {
                    this.setFinalBuffer(Unpooled.EMPTY_BUFFER);
                }
                firstpos = currentpos;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            }
            else if (contRead && this.currentAttribute != null && this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FIELD) {
                this.currentAttribute.addContent(this.undecodedChunk.retainedSlice(firstpos, currentpos - firstpos), false);
                firstpos = currentpos;
            }
            this.undecodedChunk.readerIndex(firstpos);
        }
        catch (final HttpPostRequestDecoder.ErrorDataDecoderException e) {
            this.undecodedChunk.readerIndex(firstpos);
            throw e;
        }
        catch (final IOException e2) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
        }
        catch (final IllegalArgumentException e3) {
            this.undecodedChunk.readerIndex(firstpos);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
        }
    }
    
    private void setFinalBuffer(final ByteBuf buffer) throws IOException {
        this.currentAttribute.addContent(buffer, true);
        final ByteBuf decodedBuf = decodeAttribute(this.currentAttribute.getByteBuf(), this.charset);
        if (decodedBuf != null) {
            this.currentAttribute.setContent(decodedBuf);
        }
        this.addHttpData(this.currentAttribute);
        this.currentAttribute = null;
    }
    
    private static String decodeAttribute(final String s, final Charset charset) {
        try {
            return QueryStringDecoder.decodeComponent(s, charset);
        }
        catch (final IllegalArgumentException e) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Bad string: '" + s + '\'', e);
        }
    }
    
    private static ByteBuf decodeAttribute(final ByteBuf b, final Charset charset) {
        final int firstEscaped = b.forEachByte(new UrlEncodedDetector());
        if (firstEscaped == -1) {
            return null;
        }
        final ByteBuf buf = b.alloc().buffer(b.readableBytes());
        final UrlDecoder urlDecode = new UrlDecoder(buf);
        int idx = b.forEachByte(urlDecode);
        if (urlDecode.nextEscapedIdx != 0) {
            if (idx == -1) {
                idx = b.readableBytes() - 1;
            }
            idx -= urlDecode.nextEscapedIdx - 1;
            buf.release();
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(String.format("Invalid hex byte at index '%d' in string: '%s'", idx, b.toString(charset)));
        }
        return buf;
    }
    
    @Override
    public void destroy() {
        this.cleanFiles();
        for (final InterfaceHttpData httpData : this.bodyListHttpData) {
            if (httpData.refCnt() > 0) {
                httpData.release();
            }
        }
        this.destroyed = true;
        if (this.undecodedChunk != null && this.undecodedChunk.refCnt() > 0) {
            this.undecodedChunk.release();
            this.undecodedChunk = null;
        }
    }
    
    @Override
    public void cleanFiles() {
        this.checkDestroyed();
        this.factory.cleanRequestHttpData(this.request);
    }
    
    @Override
    public void removeHttpDataFromClean(final InterfaceHttpData data) {
        this.checkDestroyed();
        this.factory.removeHttpDataFromClean(this.request, data);
    }
    
    private static final class UrlEncodedDetector implements ByteProcessor
    {
        @Override
        public boolean process(final byte value) throws Exception {
            return value != 37 && value != 43;
        }
    }
    
    private static final class UrlDecoder implements ByteProcessor
    {
        private final ByteBuf output;
        private int nextEscapedIdx;
        private byte hiByte;
        
        UrlDecoder(final ByteBuf output) {
            this.output = output;
        }
        
        @Override
        public boolean process(final byte value) {
            if (this.nextEscapedIdx != 0) {
                if (this.nextEscapedIdx == 1) {
                    this.hiByte = value;
                    ++this.nextEscapedIdx;
                }
                else {
                    final int hi = StringUtil.decodeHexNibble((char)this.hiByte);
                    final int lo = StringUtil.decodeHexNibble((char)value);
                    if (hi == -1 || lo == -1) {
                        ++this.nextEscapedIdx;
                        return false;
                    }
                    this.output.writeByte((hi << 4) + lo);
                    this.nextEscapedIdx = 0;
                }
            }
            else if (value == 37) {
                this.nextEscapedIdx = 1;
            }
            else if (value == 43) {
                this.output.writeByte(32);
            }
            else {
                this.output.writeByte(value);
            }
            return true;
        }
    }
}
