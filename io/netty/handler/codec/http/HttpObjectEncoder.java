package io.netty.handler.codec.http;

import io.netty.util.CharsetUtil;
import java.util.Iterator;
import java.util.Map;
import io.netty.buffer.Unpooled;
import io.netty.channel.FileRegion;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.StringUtil;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageEncoder;

public abstract class HttpObjectEncoder<H extends HttpMessage> extends MessageToMessageEncoder<Object>
{
    static final int CRLF_SHORT = 3338;
    private static final int ZERO_CRLF_MEDIUM = 3149066;
    private static final byte[] ZERO_CRLF_CRLF;
    private static final ByteBuf CRLF_BUF;
    private static final ByteBuf ZERO_CRLF_CRLF_BUF;
    private static final float HEADERS_WEIGHT_NEW = 0.2f;
    private static final float HEADERS_WEIGHT_HISTORICAL = 0.8f;
    private static final float TRAILERS_WEIGHT_NEW = 0.2f;
    private static final float TRAILERS_WEIGHT_HISTORICAL = 0.8f;
    private static final int ST_INIT = 0;
    private static final int ST_CONTENT_NON_CHUNK = 1;
    private static final int ST_CONTENT_CHUNK = 2;
    private static final int ST_CONTENT_ALWAYS_EMPTY = 3;
    private int state;
    private float headersEncodedSizeAccumulator;
    private float trailersEncodedSizeAccumulator;
    
    public HttpObjectEncoder() {
        this.state = 0;
        this.headersEncodedSizeAccumulator = 256.0f;
        this.trailersEncodedSizeAccumulator = 256.0f;
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final List<Object> out) throws Exception {
        ByteBuf buf = null;
        if (msg instanceof HttpMessage) {
            if (this.state != 0) {
                throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg) + ", state: " + this.state);
            }
            final H m = (H)msg;
            buf = ctx.alloc().buffer((int)this.headersEncodedSizeAccumulator);
            this.encodeInitialLine(buf, m);
            this.state = (this.isContentAlwaysEmpty(m) ? 3 : (HttpUtil.isTransferEncodingChunked(m) ? 2 : 1));
            this.sanitizeHeadersBeforeEncode(m, this.state == 3);
            this.encodeHeaders(m.headers(), buf);
            ByteBufUtil.writeShortBE(buf, 3338);
            this.headersEncodedSizeAccumulator = 0.2f * padSizeForAccumulation(buf.readableBytes()) + 0.8f * this.headersEncodedSizeAccumulator;
        }
        if (msg instanceof ByteBuf) {
            final ByteBuf potentialEmptyBuf = (ByteBuf)msg;
            if (!potentialEmptyBuf.isReadable()) {
                out.add(potentialEmptyBuf.retain());
                return;
            }
        }
        if (msg instanceof HttpContent || msg instanceof ByteBuf || msg instanceof FileRegion) {
            switch (this.state) {
                case 0: {
                    throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg) + ", state: " + this.state);
                }
                case 1: {
                    final long contentLength = contentLength(msg);
                    if (contentLength <= 0L)
                    if (buf != null && buf.writableBytes() >= contentLength && msg instanceof HttpContent) {
                        buf.writeBytes(((HttpContent)msg).content());
                        out.add(buf);
                    }
                    else {
                        if (buf != null) {
                            out.add(buf);
                        }
                        out.add(encodeAndRetain(msg));
                    }
                    if (msg instanceof LastHttpContent) {
                        this.state = 0;
                        break;
                    }
                    break;
                }
                case 3: {
                    if (buf != null) {
                        out.add(buf);
                        break;
                    }
                    out.add(Unpooled.EMPTY_BUFFER);
                    break;
                }
                case 2: {
                    if (buf != null) {
                        out.add(buf);
                    }
                    this.encodeChunkedContent(ctx, msg, contentLength(msg), out);
                    break;
                }
                default: {
                    throw new Error();
                }
            }
            if (msg instanceof LastHttpContent) {
                this.state = 0;
            }
        }
        else if (buf != null) {
            out.add(buf);
        }
    }
    
    protected void encodeHeaders(final HttpHeaders headers, final ByteBuf buf) {
        final Iterator<Map.Entry<CharSequence, CharSequence>> iter = headers.iteratorCharSequence();
        while (iter.hasNext()) {
            final Map.Entry<CharSequence, CharSequence> header = iter.next();
            HttpHeadersEncoder.encoderHeader(header.getKey(), header.getValue(), buf);
        }
    }
    
    private void encodeChunkedContent(final ChannelHandlerContext ctx, final Object msg, final long contentLength, final List<Object> out) {
        if (contentLength > 0L) {
            final String lengthHex = Long.toHexString(contentLength);
            final ByteBuf buf = ctx.alloc().buffer(lengthHex.length() + 2);
            buf.writeCharSequence(lengthHex, CharsetUtil.US_ASCII);
            ByteBufUtil.writeShortBE(buf, 3338);
            out.add(buf);
            out.add(encodeAndRetain(msg));
            out.add(HttpObjectEncoder.CRLF_BUF.duplicate());
        }
        if (msg instanceof LastHttpContent) {
            final HttpHeaders headers = ((LastHttpContent)msg).trailingHeaders();
            if (headers.isEmpty()) {
                out.add(HttpObjectEncoder.ZERO_CRLF_CRLF_BUF.duplicate());
            }
            else {
                final ByteBuf buf = ctx.alloc().buffer((int)this.trailersEncodedSizeAccumulator);
                ByteBufUtil.writeMediumBE(buf, 3149066);
                this.encodeHeaders(headers, buf);
                ByteBufUtil.writeShortBE(buf, 3338);
                this.trailersEncodedSizeAccumulator = 0.2f * padSizeForAccumulation(buf.readableBytes()) + 0.8f * this.trailersEncodedSizeAccumulator;
                out.add(buf);
            }
        }
        else if (contentLength == 0L) {
            out.add(encodeAndRetain(msg));
        }
    }
    
    protected void sanitizeHeadersBeforeEncode(final H msg, final boolean isAlwaysEmpty) {
    }
    
    protected boolean isContentAlwaysEmpty(final H msg) {
        return false;
    }
    
    @Override
    public boolean acceptOutboundMessage(final Object msg) throws Exception {
        return msg instanceof HttpObject || msg instanceof ByteBuf || msg instanceof FileRegion;
    }
    
    private static Object encodeAndRetain(final Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf)msg).retain();
        }
        if (msg instanceof HttpContent) {
            return ((HttpContent)msg).content().retain();
        }
        if (msg instanceof FileRegion) {
            return ((FileRegion)msg).retain();
        }
        throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
    }
    
    private static long contentLength(final Object msg) {
        if (msg instanceof HttpContent) {
            return ((HttpContent)msg).content().readableBytes();
        }
        if (msg instanceof ByteBuf) {
            return ((ByteBuf)msg).readableBytes();
        }
        if (msg instanceof FileRegion) {
            return ((FileRegion)msg).count();
        }
        throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
    }
    
    private static int padSizeForAccumulation(final int readableBytes) {
        return (readableBytes << 2) / 3;
    }
    
    @Deprecated
    protected static void encodeAscii(final String s, final ByteBuf buf) {
        buf.writeCharSequence(s, CharsetUtil.US_ASCII);
    }
    
    protected abstract void encodeInitialLine(final ByteBuf p0, final H p1) throws Exception;
    
    static {
        ZERO_CRLF_CRLF = new byte[] { 48, 13, 10, 13, 10 };
        CRLF_BUF = Unpooled.unreleasableBuffer(Unpooled.directBuffer(2).writeByte(13).writeByte(10));
        ZERO_CRLF_CRLF_BUF = Unpooled.unreleasableBuffer(Unpooled.directBuffer(HttpObjectEncoder.ZERO_CRLF_CRLF.length).writeBytes(HttpObjectEncoder.ZERO_CRLF_CRLF));
    }
}
