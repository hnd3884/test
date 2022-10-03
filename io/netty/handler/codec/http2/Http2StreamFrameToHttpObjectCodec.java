package io.netty.handler.codec.http2;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.Attribute;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.util.AttributeKey;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.MessageToMessageCodec;

@ChannelHandler.Sharable
public class Http2StreamFrameToHttpObjectCodec extends MessageToMessageCodec<Http2StreamFrame, HttpObject>
{
    private static final AttributeKey<HttpScheme> SCHEME_ATTR_KEY;
    private final boolean isServer;
    private final boolean validateHeaders;
    
    public Http2StreamFrameToHttpObjectCodec(final boolean isServer, final boolean validateHeaders) {
        this.isServer = isServer;
        this.validateHeaders = validateHeaders;
    }
    
    public Http2StreamFrameToHttpObjectCodec(final boolean isServer) {
        this(isServer, true);
    }
    
    @Override
    public boolean acceptInboundMessage(final Object msg) throws Exception {
        return msg instanceof Http2HeadersFrame || msg instanceof Http2DataFrame;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final Http2StreamFrame frame, final List<Object> out) throws Exception {
        if (frame instanceof Http2HeadersFrame) {
            final Http2HeadersFrame headersFrame = (Http2HeadersFrame)frame;
            final Http2Headers headers = headersFrame.headers();
            final Http2FrameStream stream = headersFrame.stream();
            final int id = (stream == null) ? 0 : stream.id();
            final CharSequence status = headers.status();
            if (null != status && HttpResponseStatus.CONTINUE.codeAsText().contentEquals(status)) {
                final FullHttpMessage fullMsg = this.newFullMessage(id, headers, ctx.alloc());
                out.add(fullMsg);
                return;
            }
            if (headersFrame.isEndStream()) {
                if (headers.method() == null && status == null) {
                    final LastHttpContent last = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, this.validateHeaders);
                    HttpConversionUtil.addHttp2ToHttpHeaders(id, headers, last.trailingHeaders(), HttpVersion.HTTP_1_1, true, true);
                    out.add(last);
                }
                else {
                    final FullHttpMessage full = this.newFullMessage(id, headers, ctx.alloc());
                    out.add(full);
                }
            }
            else {
                final HttpMessage req = this.newMessage(id, headers);
                if (!HttpUtil.isContentLengthSet(req)) {
                    req.headers().add(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
                }
                out.add(req);
            }
        }
        else if (frame instanceof Http2DataFrame) {
            final Http2DataFrame dataFrame = (Http2DataFrame)frame;
            if (dataFrame.isEndStream()) {
                out.add(new DefaultLastHttpContent(dataFrame.content().retain(), this.validateHeaders));
            }
            else {
                out.add(new DefaultHttpContent(dataFrame.content().retain()));
            }
        }
    }
    
    private void encodeLastContent(final LastHttpContent last, final List<Object> out) {
        final boolean needFiller = !(last instanceof FullHttpMessage) && last.trailingHeaders().isEmpty();
        if (last.content().isReadable() || needFiller) {
            out.add(new DefaultHttp2DataFrame(last.content().retain(), last.trailingHeaders().isEmpty()));
        }
        if (!last.trailingHeaders().isEmpty()) {
            final Http2Headers headers = HttpConversionUtil.toHttp2Headers(last.trailingHeaders(), this.validateHeaders);
            out.add(new DefaultHttp2HeadersFrame(headers, true));
        }
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final HttpObject obj, final List<Object> out) throws Exception {
        if (obj instanceof HttpResponse) {
            final HttpResponse res = (HttpResponse)obj;
            if (res.status().equals(HttpResponseStatus.CONTINUE)) {
                if (res instanceof FullHttpResponse) {
                    final Http2Headers headers = this.toHttp2Headers(ctx, res);
                    out.add(new DefaultHttp2HeadersFrame(headers, false));
                    return;
                }
                throw new EncoderException(HttpResponseStatus.CONTINUE + " must be a FullHttpResponse");
            }
        }
        if (obj instanceof HttpMessage) {
            final Http2Headers headers2 = this.toHttp2Headers(ctx, (HttpMessage)obj);
            boolean noMoreFrames = false;
            if (obj instanceof FullHttpMessage) {
                final FullHttpMessage full = (FullHttpMessage)obj;
                noMoreFrames = (!full.content().isReadable() && full.trailingHeaders().isEmpty());
            }
            out.add(new DefaultHttp2HeadersFrame(headers2, noMoreFrames));
        }
        if (obj instanceof LastHttpContent) {
            final LastHttpContent last = (LastHttpContent)obj;
            this.encodeLastContent(last, out);
        }
        else if (obj instanceof HttpContent) {
            final HttpContent cont = (HttpContent)obj;
            out.add(new DefaultHttp2DataFrame(cont.content().retain(), false));
        }
    }
    
    private Http2Headers toHttp2Headers(final ChannelHandlerContext ctx, final HttpMessage msg) {
        if (msg instanceof HttpRequest) {
            msg.headers().set(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), connectionScheme(ctx));
        }
        return HttpConversionUtil.toHttp2Headers(msg, this.validateHeaders);
    }
    
    private HttpMessage newMessage(final int id, final Http2Headers headers) throws Http2Exception {
        return (HttpMessage)(this.isServer ? HttpConversionUtil.toHttpRequest(id, headers, this.validateHeaders) : HttpConversionUtil.toHttpResponse(id, headers, this.validateHeaders));
    }
    
    private FullHttpMessage newFullMessage(final int id, final Http2Headers headers, final ByteBufAllocator alloc) throws Http2Exception {
        return (FullHttpMessage)(this.isServer ? HttpConversionUtil.toFullHttpRequest(id, headers, alloc, this.validateHeaders) : HttpConversionUtil.toFullHttpResponse(id, headers, alloc, this.validateHeaders));
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        final Attribute<HttpScheme> schemeAttribute = connectionSchemeAttribute(ctx);
        if (schemeAttribute.get() == null) {
            final HttpScheme scheme = this.isSsl(ctx) ? HttpScheme.HTTPS : HttpScheme.HTTP;
            schemeAttribute.set(scheme);
        }
    }
    
    protected boolean isSsl(final ChannelHandlerContext ctx) {
        final Channel connChannel = connectionChannel(ctx);
        return null != connChannel.pipeline().get(SslHandler.class);
    }
    
    private static HttpScheme connectionScheme(final ChannelHandlerContext ctx) {
        final HttpScheme scheme = connectionSchemeAttribute(ctx).get();
        return (scheme == null) ? HttpScheme.HTTP : scheme;
    }
    
    private static Attribute<HttpScheme> connectionSchemeAttribute(final ChannelHandlerContext ctx) {
        final Channel ch = connectionChannel(ctx);
        return ch.attr(Http2StreamFrameToHttpObjectCodec.SCHEME_ATTR_KEY);
    }
    
    private static Channel connectionChannel(final ChannelHandlerContext ctx) {
        final Channel ch = ctx.channel();
        return (ch instanceof Http2StreamChannel) ? ch.parent() : ch;
    }
    
    static {
        SCHEME_ATTR_KEY = AttributeKey.valueOf(HttpScheme.class, "STREAMFRAMECODEC_SCHEME");
    }
}
