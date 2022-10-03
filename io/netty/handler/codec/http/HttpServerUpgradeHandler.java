package io.netty.handler.codec.http;

import io.netty.util.ReferenceCounted;
import java.util.ArrayList;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import java.util.Iterator;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import java.util.Collection;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;

public class HttpServerUpgradeHandler extends HttpObjectAggregator
{
    private final SourceCodec sourceCodec;
    private final UpgradeCodecFactory upgradeCodecFactory;
    private final boolean validateHeaders;
    private boolean handlingUpgrade;
    
    public HttpServerUpgradeHandler(final SourceCodec sourceCodec, final UpgradeCodecFactory upgradeCodecFactory) {
        this(sourceCodec, upgradeCodecFactory, 0);
    }
    
    public HttpServerUpgradeHandler(final SourceCodec sourceCodec, final UpgradeCodecFactory upgradeCodecFactory, final int maxContentLength) {
        this(sourceCodec, upgradeCodecFactory, maxContentLength, true);
    }
    
    public HttpServerUpgradeHandler(final SourceCodec sourceCodec, final UpgradeCodecFactory upgradeCodecFactory, final int maxContentLength, final boolean validateHeaders) {
        super(maxContentLength);
        this.sourceCodec = ObjectUtil.checkNotNull(sourceCodec, "sourceCodec");
        this.upgradeCodecFactory = ObjectUtil.checkNotNull(upgradeCodecFactory, "upgradeCodecFactory");
        this.validateHeaders = validateHeaders;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final HttpObject msg, final List<Object> out) throws Exception {
        if (!this.handlingUpgrade) {
            if (!(msg instanceof HttpRequest)) {
                ReferenceCountUtil.retain(msg);
                ctx.fireChannelRead((Object)msg);
                return;
            }
            final HttpRequest req = (HttpRequest)msg;
            if (!req.headers().contains(HttpHeaderNames.UPGRADE) || !this.shouldHandleUpgradeRequest(req)) {
                ReferenceCountUtil.retain(msg);
                ctx.fireChannelRead((Object)msg);
                return;
            }
            this.handlingUpgrade = true;
        }
        FullHttpRequest fullRequest;
        if (msg instanceof FullHttpRequest) {
            fullRequest = (FullHttpRequest)msg;
            ReferenceCountUtil.retain(msg);
            out.add(msg);
        }
        else {
            super.decode(ctx, msg, out);
            if (out.isEmpty()) {
                return;
            }
            assert out.size() == 1;
            this.handlingUpgrade = false;
            fullRequest = out.get(0);
        }
        if (this.upgrade(ctx, fullRequest)) {
            out.clear();
        }
    }
    
    protected boolean shouldHandleUpgradeRequest(final HttpRequest req) {
        return true;
    }
    
    private boolean upgrade(final ChannelHandlerContext ctx, final FullHttpRequest request) {
        final List<CharSequence> requestedProtocols = splitHeader(request.headers().get(HttpHeaderNames.UPGRADE));
        final int numRequestedProtocols = requestedProtocols.size();
        UpgradeCodec upgradeCodec = null;
        CharSequence upgradeProtocol = null;
        for (int i = 0; i < numRequestedProtocols; ++i) {
            final CharSequence p = requestedProtocols.get(i);
            final UpgradeCodec c = this.upgradeCodecFactory.newUpgradeCodec(p);
            if (c != null) {
                upgradeProtocol = p;
                upgradeCodec = c;
                break;
            }
        }
        if (upgradeCodec == null) {
            return false;
        }
        final List<String> connectionHeaderValues = request.headers().getAll(HttpHeaderNames.CONNECTION);
        if (connectionHeaderValues == null || connectionHeaderValues.isEmpty()) {
            return false;
        }
        final StringBuilder concatenatedConnectionValue = new StringBuilder(connectionHeaderValues.size() * 10);
        for (final CharSequence connectionHeaderValue : connectionHeaderValues) {
            concatenatedConnectionValue.append(connectionHeaderValue).append(',');
        }
        concatenatedConnectionValue.setLength(concatenatedConnectionValue.length() - 1);
        final Collection<CharSequence> requiredHeaders = upgradeCodec.requiredUpgradeHeaders();
        final List<CharSequence> values = splitHeader(concatenatedConnectionValue);
        if (!AsciiString.containsContentEqualsIgnoreCase(values, HttpHeaderNames.UPGRADE) || !AsciiString.containsAllContentEqualsIgnoreCase(values, requiredHeaders)) {
            return false;
        }
        for (final CharSequence requiredHeader : requiredHeaders) {
            if (!request.headers().contains(requiredHeader)) {
                return false;
            }
        }
        final FullHttpResponse upgradeResponse = this.createUpgradeResponse(upgradeProtocol);
        if (!upgradeCodec.prepareUpgradeResponse(ctx, request, upgradeResponse.headers())) {
            return false;
        }
        final UpgradeEvent event = new UpgradeEvent(upgradeProtocol, request);
        try {
            final ChannelFuture writeComplete = ctx.writeAndFlush(upgradeResponse);
            this.sourceCodec.upgradeFrom(ctx);
            upgradeCodec.upgradeTo(ctx, request);
            ctx.pipeline().remove(this);
            ctx.fireUserEventTriggered((Object)event.retain());
            writeComplete.addListener((GenericFutureListener<? extends Future<? super Void>>)ChannelFutureListener.CLOSE_ON_FAILURE);
        }
        finally {
            event.release();
        }
        return true;
    }
    
    private FullHttpResponse createUpgradeResponse(final CharSequence upgradeProtocol) {
        final DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, Unpooled.EMPTY_BUFFER, this.validateHeaders);
        res.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
        res.headers().add(HttpHeaderNames.UPGRADE, upgradeProtocol);
        return res;
    }
    
    private static List<CharSequence> splitHeader(final CharSequence header) {
        final StringBuilder builder = new StringBuilder(header.length());
        final List<CharSequence> protocols = new ArrayList<CharSequence>(4);
        for (int i = 0; i < header.length(); ++i) {
            final char c = header.charAt(i);
            if (!Character.isWhitespace(c)) {
                if (c == ',') {
                    protocols.add(builder.toString());
                    builder.setLength(0);
                }
                else {
                    builder.append(c);
                }
            }
        }
        if (builder.length() > 0) {
            protocols.add(builder.toString());
        }
        return protocols;
    }
    
    public static final class UpgradeEvent implements ReferenceCounted
    {
        private final CharSequence protocol;
        private final FullHttpRequest upgradeRequest;
        
        UpgradeEvent(final CharSequence protocol, final FullHttpRequest upgradeRequest) {
            this.protocol = protocol;
            this.upgradeRequest = upgradeRequest;
        }
        
        public CharSequence protocol() {
            return this.protocol;
        }
        
        public FullHttpRequest upgradeRequest() {
            return this.upgradeRequest;
        }
        
        @Override
        public int refCnt() {
            return this.upgradeRequest.refCnt();
        }
        
        @Override
        public UpgradeEvent retain() {
            this.upgradeRequest.retain();
            return this;
        }
        
        @Override
        public UpgradeEvent retain(final int increment) {
            this.upgradeRequest.retain(increment);
            return this;
        }
        
        @Override
        public UpgradeEvent touch() {
            this.upgradeRequest.touch();
            return this;
        }
        
        @Override
        public UpgradeEvent touch(final Object hint) {
            this.upgradeRequest.touch(hint);
            return this;
        }
        
        @Override
        public boolean release() {
            return this.upgradeRequest.release();
        }
        
        @Override
        public boolean release(final int decrement) {
            return this.upgradeRequest.release(decrement);
        }
        
        @Override
        public String toString() {
            return "UpgradeEvent [protocol=" + (Object)this.protocol + ", upgradeRequest=" + this.upgradeRequest + ']';
        }
    }
    
    public interface SourceCodec
    {
        void upgradeFrom(final ChannelHandlerContext p0);
    }
    
    public interface UpgradeCodec
    {
        Collection<CharSequence> requiredUpgradeHeaders();
        
        boolean prepareUpgradeResponse(final ChannelHandlerContext p0, final FullHttpRequest p1, final HttpHeaders p2);
        
        void upgradeTo(final ChannelHandlerContext p0, final FullHttpRequest p1);
    }
    
    public interface UpgradeCodecFactory
    {
        UpgradeCodec newUpgradeCodec(final CharSequence p0);
    }
}
