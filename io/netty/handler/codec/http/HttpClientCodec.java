package io.netty.handler.codec.http;

import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Queue;
import io.netty.channel.CombinedChannelDuplexHandler;

public final class HttpClientCodec extends CombinedChannelDuplexHandler<HttpResponseDecoder, HttpRequestEncoder> implements HttpClientUpgradeHandler.SourceCodec
{
    public static final boolean DEFAULT_FAIL_ON_MISSING_RESPONSE = false;
    public static final boolean DEFAULT_PARSE_HTTP_AFTER_CONNECT_REQUEST = false;
    private final Queue<HttpMethod> queue;
    private final boolean parseHttpAfterConnectRequest;
    private boolean done;
    private final AtomicLong requestResponseCounter;
    private final boolean failOnMissingResponse;
    
    public HttpClientCodec() {
        this(4096, 8192, 8192, false);
    }
    
    public HttpClientCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, false);
    }
    
    public HttpClientCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean failOnMissingResponse) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, true);
    }
    
    public HttpClientCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean failOnMissingResponse, final boolean validateHeaders) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, validateHeaders, false);
    }
    
    public HttpClientCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean failOnMissingResponse, final boolean validateHeaders, final boolean parseHttpAfterConnectRequest) {
        this.queue = new ArrayDeque<HttpMethod>();
        this.requestResponseCounter = new AtomicLong();
        ((CombinedChannelDuplexHandler<Decoder, Encoder>)this).init(new Decoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders), new Encoder());
        this.failOnMissingResponse = failOnMissingResponse;
        this.parseHttpAfterConnectRequest = parseHttpAfterConnectRequest;
    }
    
    public HttpClientCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean failOnMissingResponse, final boolean validateHeaders, final int initialBufferSize) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, validateHeaders, initialBufferSize, false);
    }
    
    public HttpClientCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean failOnMissingResponse, final boolean validateHeaders, final int initialBufferSize, final boolean parseHttpAfterConnectRequest) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, validateHeaders, initialBufferSize, parseHttpAfterConnectRequest, false);
    }
    
    public HttpClientCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean failOnMissingResponse, final boolean validateHeaders, final int initialBufferSize, final boolean parseHttpAfterConnectRequest, final boolean allowDuplicateContentLengths) {
        this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, validateHeaders, initialBufferSize, parseHttpAfterConnectRequest, allowDuplicateContentLengths, true);
    }
    
    public HttpClientCodec(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean failOnMissingResponse, final boolean validateHeaders, final int initialBufferSize, final boolean parseHttpAfterConnectRequest, final boolean allowDuplicateContentLengths, final boolean allowPartialChunks) {
        this.queue = new ArrayDeque<HttpMethod>();
        this.requestResponseCounter = new AtomicLong();
        ((CombinedChannelDuplexHandler<Decoder, Encoder>)this).init(new Decoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths, allowPartialChunks), new Encoder());
        this.parseHttpAfterConnectRequest = parseHttpAfterConnectRequest;
        this.failOnMissingResponse = failOnMissingResponse;
    }
    
    @Override
    public void prepareUpgradeFrom(final ChannelHandlerContext ctx) {
        ((CombinedChannelDuplexHandler<I, Encoder>)this).outboundHandler().upgraded = true;
    }
    
    @Override
    public void upgradeFrom(final ChannelHandlerContext ctx) {
        final ChannelPipeline p = ctx.pipeline();
        p.remove(this);
    }
    
    public void setSingleDecode(final boolean singleDecode) {
        ((CombinedChannelDuplexHandler<HttpResponseDecoder, O>)this).inboundHandler().setSingleDecode(singleDecode);
    }
    
    public boolean isSingleDecode() {
        return ((CombinedChannelDuplexHandler<HttpResponseDecoder, O>)this).inboundHandler().isSingleDecode();
    }
    
    private final class Encoder extends HttpRequestEncoder
    {
        boolean upgraded;
        
        @Override
        protected void encode(final ChannelHandlerContext ctx, final Object msg, final List<Object> out) throws Exception {
            if (this.upgraded) {
                out.add(ReferenceCountUtil.retain(msg));
                return;
            }
            if (msg instanceof HttpRequest) {
                HttpClientCodec.this.queue.offer(((HttpRequest)msg).method());
            }
            super.encode(ctx, msg, out);
            if (HttpClientCodec.this.failOnMissingResponse && !HttpClientCodec.this.done && msg instanceof LastHttpContent) {
                HttpClientCodec.this.requestResponseCounter.incrementAndGet();
            }
        }
    }
    
    private final class Decoder extends HttpResponseDecoder
    {
        Decoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders);
        }
        
        Decoder(final int maxInitialLineLength, final int maxHeaderSize, final int maxChunkSize, final boolean validateHeaders, final int initialBufferSize, final boolean allowDuplicateContentLengths, final boolean allowPartialChunks) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths, allowPartialChunks);
        }
        
        @Override
        protected void decode(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) throws Exception {
            if (HttpClientCodec.this.done) {
                final int readable = this.actualReadableBytes();
                if (readable == 0) {
                    return;
                }
                out.add(buffer.readBytes(readable));
            }
            else {
                final int oldSize = out.size();
                super.decode(ctx, buffer, out);
                if (HttpClientCodec.this.failOnMissingResponse) {
                    for (int size = out.size(), i = oldSize; i < size; ++i) {
                        this.decrement(out.get(i));
                    }
                }
            }
        }
        
        private void decrement(final Object msg) {
            if (msg == null) {
                return;
            }
            if (msg instanceof LastHttpContent) {
                HttpClientCodec.this.requestResponseCounter.decrementAndGet();
            }
        }
        
        @Override
        protected boolean isContentAlwaysEmpty(final HttpMessage msg) {
            final HttpMethod method = HttpClientCodec.this.queue.poll();
            final int statusCode = ((HttpResponse)msg).status().code();
            if (statusCode >= 100 && statusCode < 200) {
                return super.isContentAlwaysEmpty(msg);
            }
            if (method != null) {
                final char firstChar = method.name().charAt(0);
                switch (firstChar) {
                    case 'H': {
                        if (HttpMethod.HEAD.equals(method)) {
                            return true;
                        }
                        break;
                    }
                    case 'C': {
                        if (statusCode == 200 && HttpMethod.CONNECT.equals(method)) {
                            if (!HttpClientCodec.this.parseHttpAfterConnectRequest) {
                                HttpClientCodec.this.done = true;
                                HttpClientCodec.this.queue.clear();
                            }
                            return true;
                        }
                        break;
                    }
                }
            }
            return super.isContentAlwaysEmpty(msg);
        }
        
        @Override
        public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            if (HttpClientCodec.this.failOnMissingResponse) {
                final long missingResponses = HttpClientCodec.this.requestResponseCounter.get();
                if (missingResponses > 0L) {
                    ctx.fireExceptionCaught((Throwable)new PrematureChannelClosureException("channel gone inactive with " + missingResponses + " missing response(s)"));
                }
            }
        }
    }
}
