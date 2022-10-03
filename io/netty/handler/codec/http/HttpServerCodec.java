package io.netty.handler.codec.http;

import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayDeque;
import java.util.Queue;
import io.netty.channel.CombinedChannelDuplexHandler;

public final class HttpServerCodec extends CombinedChannelDuplexHandler<HttpRequestDecoder, HttpResponseEncoder> implements HttpServerUpgradeHandler.SourceCodec
{
    private final Queue<HttpMethod> queue;
    
    public HttpServerCodec() {
        this(4096, 8192, 8192);
    }
    
    public HttpServerCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize) {
        this.queue = new ArrayDeque<HttpMethod>();
        ((CombinedChannelDuplexHandler<HttpServerRequestDecoder, HttpServerResponseEncoder>)this).init(new HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize), new HttpServerResponseEncoder());
    }
    
    public HttpServerCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders) {
        this.queue = new ArrayDeque<HttpMethod>();
        ((CombinedChannelDuplexHandler<HttpServerRequestDecoder, HttpServerResponseEncoder>)this).init(new HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders), new HttpServerResponseEncoder());
    }
    
    public HttpServerCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders, final int initialBufferSize) {
        this.queue = new ArrayDeque<HttpMethod>();
        ((CombinedChannelDuplexHandler<HttpServerRequestDecoder, HttpServerResponseEncoder>)this).init(new HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize), new HttpServerResponseEncoder());
    }
    
    public HttpServerCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders, final int initialBufferSize, final boolean allowDuplicateContentLengths) {
        this.queue = new ArrayDeque<HttpMethod>();
        ((CombinedChannelDuplexHandler<HttpServerRequestDecoder, HttpServerResponseEncoder>)this).init(new HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths), new HttpServerResponseEncoder());
    }
    
    public HttpServerCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders, final int initialBufferSize, final boolean allowDuplicateContentLengths, final boolean allowPartialChunks) {
        this.queue = new ArrayDeque<HttpMethod>();
        ((CombinedChannelDuplexHandler<HttpServerRequestDecoder, HttpServerResponseEncoder>)this).init(new HttpServerRequestDecoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths, allowPartialChunks), new HttpServerResponseEncoder());
    }
    
    @Override
    public void upgradeFrom(final ChannelHandlerContext ctx) {
        ctx.pipeline().remove(this);
    }
    
    private final class HttpServerRequestDecoder extends HttpRequestDecoder
    {
        HttpServerRequestDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
        }
        
        HttpServerRequestDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders);
        }
        
        HttpServerRequestDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders, final int initialBufferSize) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize);
        }
        
        HttpServerRequestDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders, final int initialBufferSize, final boolean allowDuplicateContentLengths) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths);
        }
        
        HttpServerRequestDecoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders, final int initialBufferSize, final boolean allowDuplicateContentLengths, final boolean allowPartialChunks) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths, allowPartialChunks);
        }
        
        @Override
        protected void decode(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) throws Exception {
            final int oldSize = out.size();
            super.decode(ctx, buffer, out);
            for (int size = out.size(), i = oldSize; i < size; ++i) {
                final Object obj = out.get(i);
                if (obj instanceof HttpRequest) {
                    HttpServerCodec.this.queue.add(((HttpRequest)obj).method());
                }
            }
        }
    }
    
    private final class HttpServerResponseEncoder extends HttpResponseEncoder
    {
        private HttpMethod method;
        
        @Override
        protected void sanitizeHeadersBeforeEncode(final HttpResponse msg, final boolean isAlwaysEmpty) {
            if (!isAlwaysEmpty && HttpMethod.CONNECT.equals(this.method) && msg.status().codeClass() == HttpStatusClass.SUCCESS) {
                msg.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
                return;
            }
            super.sanitizeHeadersBeforeEncode(msg, isAlwaysEmpty);
        }
        
        @Override
        protected boolean isContentAlwaysEmpty(final HttpResponse msg) {
            this.method = HttpServerCodec.this.queue.poll();
            return HttpMethod.HEAD.equals(this.method) || super.isContentAlwaysEmpty(msg);
        }
    }
}
