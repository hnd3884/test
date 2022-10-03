package io.netty.handler.codec.http.multipart;

import io.netty.util.internal.InternalThreadLocalMap;
import java.util.Iterator;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.internal.StringUtil;
import java.nio.charset.UnsupportedCharsetException;
import java.io.IOException;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.internal.PlatformDependent;
import io.netty.handler.codec.http.HttpContent;
import java.nio.charset.IllegalCharsetNameException;
import io.netty.handler.codec.http.HttpHeaderNames;
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

public class HttpPostMultipartRequestDecoder implements InterfaceHttpPostRequestDecoder
{
    private final HttpDataFactory factory;
    private final HttpRequest request;
    private Charset charset;
    private boolean isLastChunk;
    private final List<InterfaceHttpData> bodyListHttpData;
    private final Map<String, List<InterfaceHttpData>> bodyMapHttpData;
    private ByteBuf undecodedChunk;
    private int bodyListHttpDataRank;
    private final String multipartDataBoundary;
    private String multipartMixedBoundary;
    private HttpPostRequestDecoder.MultiPartStatus currentStatus;
    private Map<CharSequence, Attribute> currentFieldAttributes;
    private FileUpload currentFileUpload;
    private Attribute currentAttribute;
    private boolean destroyed;
    private int discardThreshold;
    private static final String FILENAME_ENCODED;
    
    public HttpPostMultipartRequestDecoder(final HttpRequest request) {
        this(new DefaultHttpDataFactory(16384L), request, HttpConstants.DEFAULT_CHARSET);
    }
    
    public HttpPostMultipartRequestDecoder(final HttpDataFactory factory, final HttpRequest request) {
        this(factory, request, HttpConstants.DEFAULT_CHARSET);
    }
    
    public HttpPostMultipartRequestDecoder(final HttpDataFactory factory, final HttpRequest request, final Charset charset) {
        this.bodyListHttpData = new ArrayList<InterfaceHttpData>();
        this.bodyMapHttpData = new TreeMap<String, List<InterfaceHttpData>>(CaseIgnoringComparator.INSTANCE);
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.NOTSTARTED;
        this.discardThreshold = 10485760;
        this.request = ObjectUtil.checkNotNull(request, "request");
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
        this.factory = ObjectUtil.checkNotNull(factory, "factory");
        final String contentTypeValue = this.request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if (contentTypeValue == null) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("No '" + (Object)HttpHeaderNames.CONTENT_TYPE + "' header present.");
        }
        final String[] dataBoundary = HttpPostRequestDecoder.getMultipartDataBoundary(contentTypeValue);
        Label_0202: {
            if (dataBoundary != null) {
                this.multipartDataBoundary = dataBoundary[0];
                if (dataBoundary.length <= 1 || dataBoundary[1] == null) {
                    break Label_0202;
                }
                try {
                    this.charset = Charset.forName(dataBoundary[1]);
                    break Label_0202;
                }
                catch (final IllegalCharsetNameException e) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                }
            }
            this.multipartDataBoundary = null;
        }
        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
        try {
            if (request instanceof HttpContent) {
                this.offer((HttpContent)request);
            }
            else {
                this.parseBody();
            }
        }
        catch (final Throwable e2) {
            this.destroy();
            PlatformDependent.throwException(e2);
        }
    }
    
    private void checkDestroyed() {
        if (this.destroyed) {
            throw new IllegalStateException(HttpPostMultipartRequestDecoder.class.getSimpleName() + " was destroyed already");
        }
    }
    
    @Override
    public boolean isMultipart() {
        this.checkDestroyed();
        return true;
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
    public HttpPostMultipartRequestDecoder offer(final HttpContent content) {
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
        if (this.currentFileUpload != null) {
            return this.currentFileUpload;
        }
        return this.currentAttribute;
    }
    
    private void parseBody() {
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE || this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
            if (this.isLastChunk) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.EPILOGUE;
            }
            return;
        }
        this.parseBodyMultipart();
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
    
    private void parseBodyMultipart() {
        if (this.undecodedChunk == null || this.undecodedChunk.readableBytes() == 0) {
            return;
        }
        for (InterfaceHttpData data = this.decodeMultipart(this.currentStatus); data != null; data = this.decodeMultipart(this.currentStatus)) {
            this.addHttpData(data);
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE) {
                break;
            }
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.EPILOGUE) {
                break;
            }
        }
    }
    
    private InterfaceHttpData decodeMultipart(final HttpPostRequestDecoder.MultiPartStatus state) {
        switch (state) {
            case NOTSTARTED: {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
            }
            case PREAMBLE: {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Should not be called with the current getStatus");
            }
            case HEADERDELIMITER: {
                return this.findMultipartDelimiter(this.multipartDataBoundary, HttpPostRequestDecoder.MultiPartStatus.DISPOSITION, HttpPostRequestDecoder.MultiPartStatus.PREEPILOGUE);
            }
            case DISPOSITION: {
                return this.findMultipartDisposition();
            }
            case FIELD: {
                Charset localCharset = null;
                final Attribute charsetAttribute = this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
                if (charsetAttribute != null) {
                    try {
                        localCharset = Charset.forName(charsetAttribute.getValue());
                    }
                    catch (final IOException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    catch (final UnsupportedCharsetException e2) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
                    }
                }
                final Attribute nameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.NAME);
                if (this.currentAttribute == null) {
                    final Attribute lengthAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);
                    long size;
                    try {
                        size = ((lengthAttribute != null) ? Long.parseLong(lengthAttribute.getValue()) : 0L);
                    }
                    catch (final IOException e3) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
                    }
                    catch (final NumberFormatException ignored) {
                        size = 0L;
                    }
                    try {
                        if (size > 0L) {
                            this.currentAttribute = this.factory.createAttribute(this.request, cleanString(nameAttribute.getValue()), size);
                        }
                        else {
                            this.currentAttribute = this.factory.createAttribute(this.request, cleanString(nameAttribute.getValue()));
                        }
                    }
                    catch (final NullPointerException e4) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e4);
                    }
                    catch (final IllegalArgumentException e5) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e5);
                    }
                    catch (final IOException e3) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
                    }
                    if (localCharset != null) {
                        this.currentAttribute.setCharset(localCharset);
                    }
                }
                if (!loadDataMultipartOptimized(this.undecodedChunk, this.multipartDataBoundary, this.currentAttribute)) {
                    return null;
                }
                final Attribute finalAttribute = this.currentAttribute;
                this.currentAttribute = null;
                this.currentFieldAttributes = null;
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
                return finalAttribute;
            }
            case FILEUPLOAD: {
                return this.getFileUpload(this.multipartDataBoundary);
            }
            case MIXEDDELIMITER: {
                return this.findMultipartDelimiter(this.multipartMixedBoundary, HttpPostRequestDecoder.MultiPartStatus.MIXEDDISPOSITION, HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
            }
            case MIXEDDISPOSITION: {
                return this.findMultipartDisposition();
            }
            case MIXEDFILEUPLOAD: {
                return this.getFileUpload(this.multipartMixedBoundary);
            }
            case PREEPILOGUE: {
                return null;
            }
            case EPILOGUE: {
                return null;
            }
            default: {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException("Shouldn't reach here.");
            }
        }
    }
    
    private static void skipControlCharacters(final ByteBuf undecodedChunk) {
        if (!undecodedChunk.hasArray()) {
            try {
                skipControlCharactersStandard(undecodedChunk);
            }
            catch (final IndexOutOfBoundsException e1) {
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e1);
            }
            return;
        }
        final HttpPostBodyUtil.SeekAheadOptimize sao = new HttpPostBodyUtil.SeekAheadOptimize(undecodedChunk);
        while (sao.pos < sao.limit) {
            final char c = (char)(sao.bytes[sao.pos++] & 0xFF);
            if (!Character.isISOControl(c) && !Character.isWhitespace(c)) {
                sao.setReadPosition(1);
                return;
            }
        }
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException("Access out of bounds");
    }
    
    private static void skipControlCharactersStandard(final ByteBuf undecodedChunk) {
        char c;
        do {
            c = (char)undecodedChunk.readUnsignedByte();
        } while (Character.isISOControl(c) || Character.isWhitespace(c));
        undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
    }
    
    private InterfaceHttpData findMultipartDelimiter(final String delimiter, final HttpPostRequestDecoder.MultiPartStatus dispositionStatus, final HttpPostRequestDecoder.MultiPartStatus closeDelimiterStatus) {
        final int readerIndex = this.undecodedChunk.readerIndex();
        try {
            skipControlCharacters(this.undecodedChunk);
        }
        catch (final HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
        }
        this.skipOneLine();
        String newline;
        try {
            newline = readDelimiterOptimized(this.undecodedChunk, delimiter, this.charset);
        }
        catch (final HttpPostRequestDecoder.NotEnoughDataDecoderException ignored2) {
            this.undecodedChunk.readerIndex(readerIndex);
            return null;
        }
        if (newline.equals(delimiter)) {
            this.currentStatus = dispositionStatus;
            return this.decodeMultipart(dispositionStatus);
        }
        if (!newline.equals(delimiter + "--")) {
            this.undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("No Multipart delimiter found");
        }
        this.currentStatus = closeDelimiterStatus;
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER) {
            this.currentFieldAttributes = null;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER);
        }
        return null;
    }
    
    private InterfaceHttpData findMultipartDisposition() {
        final int readerIndex = this.undecodedChunk.readerIndex();
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
            this.currentFieldAttributes = new TreeMap<CharSequence, Attribute>(CaseIgnoringComparator.INSTANCE);
        }
        while (!this.skipOneLine()) {
            String newline;
            try {
                skipControlCharacters(this.undecodedChunk);
                newline = readLineOptimized(this.undecodedChunk, this.charset);
            }
            catch (final HttpPostRequestDecoder.NotEnoughDataDecoderException ignored) {
                this.undecodedChunk.readerIndex(readerIndex);
                return null;
            }
            final String[] contents = splitMultipartHeader(newline);
            if (HttpHeaderNames.CONTENT_DISPOSITION.contentEqualsIgnoreCase(contents[0])) {
                boolean checkSecondArg;
                if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                    checkSecondArg = HttpHeaderValues.FORM_DATA.contentEqualsIgnoreCase(contents[1]);
                }
                else {
                    checkSecondArg = (HttpHeaderValues.ATTACHMENT.contentEqualsIgnoreCase(contents[1]) || HttpHeaderValues.FILE.contentEqualsIgnoreCase(contents[1]));
                }
                if (!checkSecondArg) {
                    continue;
                }
                for (int i = 2; i < contents.length; ++i) {
                    final String[] values = contents[i].split("=", 2);
                    Attribute attribute;
                    try {
                        attribute = this.getContentDispositionAttribute(values);
                    }
                    catch (final NullPointerException e) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                    }
                    catch (final IllegalArgumentException e2) {
                        throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
                    }
                    this.currentFieldAttributes.put(attribute.getName(), attribute);
                }
            }
            else if (HttpHeaderNames.CONTENT_TRANSFER_ENCODING.contentEqualsIgnoreCase(contents[0])) {
                Attribute attribute2;
                try {
                    attribute2 = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_TRANSFER_ENCODING.toString(), cleanString(contents[1]));
                }
                catch (final NullPointerException e3) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
                }
                catch (final IllegalArgumentException e4) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e4);
                }
                this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_TRANSFER_ENCODING, attribute2);
            }
            else if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(contents[0])) {
                Attribute attribute2;
                try {
                    attribute2 = this.factory.createAttribute(this.request, HttpHeaderNames.CONTENT_LENGTH.toString(), cleanString(contents[1]));
                }
                catch (final NullPointerException e3) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
                }
                catch (final IllegalArgumentException e4) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e4);
                }
                this.currentFieldAttributes.put(HttpHeaderNames.CONTENT_LENGTH, attribute2);
            }
            else {
                if (!HttpHeaderNames.CONTENT_TYPE.contentEqualsIgnoreCase(contents[0])) {
                    continue;
                }
                if (HttpHeaderValues.MULTIPART_MIXED.contentEqualsIgnoreCase(contents[1])) {
                    if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
                        final String values2 = StringUtil.substringAfter(contents[2], '=');
                        this.multipartMixedBoundary = "--" + values2;
                        this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                        return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER);
                    }
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("Mixed Multipart found in a previous Mixed Multipart");
                }
                else {
                    for (int j = 1; j < contents.length; ++j) {
                        final String charsetHeader = HttpHeaderValues.CHARSET.toString();
                        if (contents[j].regionMatches(true, 0, charsetHeader, 0, charsetHeader.length())) {
                            final String values3 = StringUtil.substringAfter(contents[j], '=');
                            Attribute attribute;
                            try {
                                attribute = this.factory.createAttribute(this.request, charsetHeader, cleanString(values3));
                            }
                            catch (final NullPointerException e) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                            }
                            catch (final IllegalArgumentException e2) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
                            }
                            this.currentFieldAttributes.put(HttpHeaderValues.CHARSET, attribute);
                        }
                        else {
                            Attribute attribute3;
                            try {
                                attribute3 = this.factory.createAttribute(this.request, cleanString(contents[0]), contents[j]);
                            }
                            catch (final NullPointerException e5) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e5);
                            }
                            catch (final IllegalArgumentException e6) {
                                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e6);
                            }
                            this.currentFieldAttributes.put(attribute3.getName(), attribute3);
                        }
                    }
                }
            }
        }
        final Attribute filenameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
        if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.DISPOSITION) {
            if (filenameAttribute != null) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD;
                return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD);
            }
            this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.FIELD;
            return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.FIELD);
        }
        else {
            if (filenameAttribute != null) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD;
                return this.decodeMultipart(HttpPostRequestDecoder.MultiPartStatus.MIXEDFILEUPLOAD);
            }
            throw new HttpPostRequestDecoder.ErrorDataDecoderException("Filename not found");
        }
    }
    
    private Attribute getContentDispositionAttribute(final String... values) {
        String name = cleanString(values[0]);
        String value = values[1];
        if (HttpHeaderValues.FILENAME.contentEquals(name)) {
            final int last = value.length() - 1;
            if (last > 0 && value.charAt(0) == '\"' && value.charAt(last) == '\"') {
                value = value.substring(1, last);
            }
        }
        else {
            if (HttpPostMultipartRequestDecoder.FILENAME_ENCODED.equals(name)) {
                try {
                    name = HttpHeaderValues.FILENAME.toString();
                    final String[] split = cleanString(value).split("'", 3);
                    value = QueryStringDecoder.decodeComponent(split[2], Charset.forName(split[0]));
                    return this.factory.createAttribute(this.request, name, value);
                }
                catch (final ArrayIndexOutOfBoundsException e) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
                }
                catch (final UnsupportedCharsetException e2) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
                }
            }
            value = cleanString(value);
        }
        return this.factory.createAttribute(this.request, name, value);
    }
    
    protected InterfaceHttpData getFileUpload(final String delimiter) {
        final Attribute encoding = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
        Charset localCharset = this.charset;
        HttpPostBodyUtil.TransferEncodingMechanism mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT7;
        if (encoding != null) {
            String code;
            try {
                code = encoding.getValue().toLowerCase();
            }
            catch (final IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT7.value())) {
                localCharset = CharsetUtil.US_ASCII;
            }
            else if (code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BIT8.value())) {
                localCharset = CharsetUtil.ISO_8859_1;
                mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BIT8;
            }
            else {
                if (!code.equals(HttpPostBodyUtil.TransferEncodingMechanism.BINARY.value())) {
                    throw new HttpPostRequestDecoder.ErrorDataDecoderException("TransferEncoding Unknown: " + code);
                }
                mechanism = HttpPostBodyUtil.TransferEncodingMechanism.BINARY;
            }
        }
        final Attribute charsetAttribute = this.currentFieldAttributes.get(HttpHeaderValues.CHARSET);
        if (charsetAttribute != null) {
            try {
                localCharset = Charset.forName(charsetAttribute.getValue());
            }
            catch (final IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            catch (final UnsupportedCharsetException e2) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
            }
        }
        if (this.currentFileUpload == null) {
            final Attribute filenameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.FILENAME);
            final Attribute nameAttribute = this.currentFieldAttributes.get(HttpHeaderValues.NAME);
            final Attribute contentTypeAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_TYPE);
            final Attribute lengthAttribute = this.currentFieldAttributes.get(HttpHeaderNames.CONTENT_LENGTH);
            long size;
            try {
                size = ((lengthAttribute != null) ? Long.parseLong(lengthAttribute.getValue()) : 0L);
            }
            catch (final IOException e3) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
            }
            catch (final NumberFormatException ignored) {
                size = 0L;
            }
            try {
                String contentType;
                if (contentTypeAttribute != null) {
                    contentType = contentTypeAttribute.getValue();
                }
                else {
                    contentType = "application/octet-stream";
                }
                this.currentFileUpload = this.factory.createFileUpload(this.request, cleanString(nameAttribute.getValue()), cleanString(filenameAttribute.getValue()), contentType, mechanism.value(), localCharset, size);
            }
            catch (final NullPointerException e4) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e4);
            }
            catch (final IllegalArgumentException e5) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e5);
            }
            catch (final IOException e3) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e3);
            }
        }
        if (!loadDataMultipartOptimized(this.undecodedChunk, delimiter, this.currentFileUpload)) {
            return null;
        }
        if (this.currentFileUpload.isCompleted()) {
            if (this.currentStatus == HttpPostRequestDecoder.MultiPartStatus.FILEUPLOAD) {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.HEADERDELIMITER;
                this.currentFieldAttributes = null;
            }
            else {
                this.currentStatus = HttpPostRequestDecoder.MultiPartStatus.MIXEDDELIMITER;
                this.cleanMixedAttributes();
            }
            final FileUpload fileUpload = this.currentFileUpload;
            this.currentFileUpload = null;
            return fileUpload;
        }
        return null;
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
    
    private void cleanMixedAttributes() {
        this.currentFieldAttributes.remove(HttpHeaderValues.CHARSET);
        this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_LENGTH);
        this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TRANSFER_ENCODING);
        this.currentFieldAttributes.remove(HttpHeaderNames.CONTENT_TYPE);
        this.currentFieldAttributes.remove(HttpHeaderValues.FILENAME);
    }
    
    private static String readLineOptimized(final ByteBuf undecodedChunk, final Charset charset) {
        final int readerIndex = undecodedChunk.readerIndex();
        ByteBuf line = null;
        try {
            if (undecodedChunk.isReadable()) {
                final int posLfOrCrLf = HttpPostBodyUtil.findLineBreak(undecodedChunk, undecodedChunk.readerIndex());
                if (posLfOrCrLf <= 0) {
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                try {
                    line = undecodedChunk.alloc().heapBuffer(posLfOrCrLf);
                    line.writeBytes(undecodedChunk, posLfOrCrLf);
                    final byte nextByte = undecodedChunk.readByte();
                    if (nextByte == 13) {
                        undecodedChunk.readByte();
                    }
                    return line.toString(charset);
                }
                finally {
                    line.release();
                }
            }
        }
        catch (final IndexOutOfBoundsException e) {
            undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
        }
        undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }
    
    private static String readDelimiterOptimized(final ByteBuf undecodedChunk, final String delimiter, final Charset charset) {
        final int readerIndex = undecodedChunk.readerIndex();
        final byte[] bdelimiter = delimiter.getBytes(charset);
        final int delimiterLength = bdelimiter.length;
        try {
            final int delimiterPos = HttpPostBodyUtil.findDelimiter(undecodedChunk, readerIndex, bdelimiter, false);
            if (delimiterPos < 0) {
                undecodedChunk.readerIndex(readerIndex);
                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
            }
            final StringBuilder sb = new StringBuilder(delimiter);
            undecodedChunk.readerIndex(readerIndex + delimiterPos + delimiterLength);
            if (undecodedChunk.isReadable()) {
                byte nextByte = undecodedChunk.readByte();
                if (nextByte == 13) {
                    nextByte = undecodedChunk.readByte();
                    if (nextByte == 10) {
                        return sb.toString();
                    }
                    undecodedChunk.readerIndex(readerIndex);
                    throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                }
                else {
                    if (nextByte == 10) {
                        return sb.toString();
                    }
                    if (nextByte == 45) {
                        sb.append('-');
                        nextByte = undecodedChunk.readByte();
                        if (nextByte == 45) {
                            sb.append('-');
                            if (!undecodedChunk.isReadable()) {
                                return sb.toString();
                            }
                            nextByte = undecodedChunk.readByte();
                            if (nextByte == 13) {
                                nextByte = undecodedChunk.readByte();
                                if (nextByte == 10) {
                                    return sb.toString();
                                }
                                undecodedChunk.readerIndex(readerIndex);
                                throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
                            }
                            else {
                                if (nextByte == 10) {
                                    return sb.toString();
                                }
                                undecodedChunk.readerIndex(undecodedChunk.readerIndex() - 1);
                                return sb.toString();
                            }
                        }
                    }
                }
            }
        }
        catch (final IndexOutOfBoundsException e) {
            undecodedChunk.readerIndex(readerIndex);
            throw new HttpPostRequestDecoder.NotEnoughDataDecoderException(e);
        }
        undecodedChunk.readerIndex(readerIndex);
        throw new HttpPostRequestDecoder.NotEnoughDataDecoderException();
    }
    
    private static void rewriteCurrentBuffer(final ByteBuf buffer, final int lengthToSkip) {
        if (lengthToSkip == 0) {
            return;
        }
        final int readerIndex = buffer.readerIndex();
        final int readableBytes = buffer.readableBytes();
        if (readableBytes == lengthToSkip) {
            buffer.readerIndex(readerIndex);
            buffer.writerIndex(readerIndex);
            return;
        }
        buffer.setBytes(readerIndex, buffer, readerIndex + lengthToSkip, readableBytes - lengthToSkip);
        buffer.readerIndex(readerIndex);
        buffer.writerIndex(readerIndex + readableBytes - lengthToSkip);
    }
    
    private static boolean loadDataMultipartOptimized(final ByteBuf undecodedChunk, final String delimiter, final HttpData httpData) {
        if (!undecodedChunk.isReadable()) {
            return false;
        }
        final int startReaderIndex = undecodedChunk.readerIndex();
        final byte[] bdelimiter = delimiter.getBytes(httpData.getCharset());
        int posDelimiter = HttpPostBodyUtil.findDelimiter(undecodedChunk, startReaderIndex, bdelimiter, true);
        if (posDelimiter >= 0) {
            final ByteBuf content = undecodedChunk.copy(startReaderIndex, posDelimiter);
            try {
                httpData.addContent(content, true);
            }
            catch (final IOException e) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e);
            }
            rewriteCurrentBuffer(undecodedChunk, posDelimiter);
            return true;
        }
        int lastPosition = undecodedChunk.readableBytes() - bdelimiter.length - 1;
        if (lastPosition < 0) {
            lastPosition = 0;
        }
        posDelimiter = HttpPostBodyUtil.findLastLineBreak(undecodedChunk, startReaderIndex + lastPosition);
        if (posDelimiter < 0) {
            final ByteBuf content2 = undecodedChunk.copy();
            try {
                httpData.addContent(content2, false);
            }
            catch (final IOException e2) {
                throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
            }
            undecodedChunk.readerIndex(startReaderIndex);
            undecodedChunk.writerIndex(startReaderIndex);
            return false;
        }
        posDelimiter += lastPosition;
        if (posDelimiter == 0) {
            return false;
        }
        final ByteBuf content2 = undecodedChunk.copy(startReaderIndex, posDelimiter);
        try {
            httpData.addContent(content2, false);
        }
        catch (final IOException e2) {
            throw new HttpPostRequestDecoder.ErrorDataDecoderException(e2);
        }
        rewriteCurrentBuffer(undecodedChunk, posDelimiter);
        return false;
    }
    
    private static String cleanString(final String field) {
        final int size = field.length();
        final StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; ++i) {
            final char nextChar = field.charAt(i);
            switch (nextChar) {
                case '\t':
                case ',':
                case ':':
                case ';':
                case '=': {
                    sb.append(' ');
                    break;
                }
                case '\"': {
                    break;
                }
                default: {
                    sb.append(nextChar);
                    break;
                }
            }
        }
        return sb.toString().trim();
    }
    
    private boolean skipOneLine() {
        if (!this.undecodedChunk.isReadable()) {
            return false;
        }
        byte nextByte = this.undecodedChunk.readByte();
        if (nextByte == 13) {
            if (!this.undecodedChunk.isReadable()) {
                this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
                return false;
            }
            nextByte = this.undecodedChunk.readByte();
            if (nextByte == 10) {
                return true;
            }
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 2);
            return false;
        }
        else {
            if (nextByte == 10) {
                return true;
            }
            this.undecodedChunk.readerIndex(this.undecodedChunk.readerIndex() - 1);
            return false;
        }
    }
    
    private static String[] splitMultipartHeader(final String sb) {
        final ArrayList<String> headers = new ArrayList<String>(1);
        int nameEnd;
        int nameStart;
        for (nameStart = (nameEnd = HttpPostBodyUtil.findNonWhitespace(sb, 0)); nameEnd < sb.length(); ++nameEnd) {
            final char ch = sb.charAt(nameEnd);
            if (ch == ':') {
                break;
            }
            if (Character.isWhitespace(ch)) {
                break;
            }
        }
        int colonEnd;
        for (colonEnd = nameEnd; colonEnd < sb.length(); ++colonEnd) {
            if (sb.charAt(colonEnd) == ':') {
                ++colonEnd;
                break;
            }
        }
        final int valueStart = HttpPostBodyUtil.findNonWhitespace(sb, colonEnd);
        final int valueEnd = HttpPostBodyUtil.findEndOfString(sb);
        headers.add(sb.substring(nameStart, nameEnd));
        final String svalue = (valueStart >= valueEnd) ? "" : sb.substring(valueStart, valueEnd);
        String[] values;
        if (svalue.indexOf(59) >= 0) {
            values = splitMultipartHeaderValues(svalue);
        }
        else {
            values = svalue.split(",");
        }
        for (final String value : values) {
            headers.add(value.trim());
        }
        final String[] array = new String[headers.size()];
        for (int i = 0; i < headers.size(); ++i) {
            array[i] = headers.get(i);
        }
        return array;
    }
    
    private static String[] splitMultipartHeaderValues(final String svalue) {
        final List<String> values = (List<String>)InternalThreadLocalMap.get().arrayList(1);
        boolean inQuote = false;
        boolean escapeNext = false;
        int start = 0;
        for (int i = 0; i < svalue.length(); ++i) {
            final char c = svalue.charAt(i);
            if (inQuote) {
                if (escapeNext) {
                    escapeNext = false;
                }
                else if (c == '\\') {
                    escapeNext = true;
                }
                else if (c == '\"') {
                    inQuote = false;
                }
            }
            else if (c == '\"') {
                inQuote = true;
            }
            else if (c == ';') {
                values.add(svalue.substring(start, i));
                start = i + 1;
            }
        }
        values.add(svalue.substring(start));
        return values.toArray(new String[0]);
    }
    
    int getCurrentAllocatedCapacity() {
        return this.undecodedChunk.capacity();
    }
    
    static {
        FILENAME_ENCODED = HttpHeaderValues.FILENAME.toString() + '*';
    }
}
