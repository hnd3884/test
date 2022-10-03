package io.netty.handler.ssl;

import io.netty.util.concurrent.Promise;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.DomainNameMapping;
import io.netty.util.Mapping;
import io.netty.util.AsyncMapping;

public class SniHandler extends AbstractSniHandler<SslContext>
{
    private static final Selection EMPTY_SELECTION;
    protected final AsyncMapping<String, SslContext> mapping;
    private volatile Selection selection;
    
    public SniHandler(final Mapping<? super String, ? extends SslContext> mapping) {
        this(new AsyncMappingAdapter((Mapping)mapping));
    }
    
    public SniHandler(final DomainNameMapping<? extends SslContext> mapping) {
        this((Mapping<? super String, ? extends SslContext>)mapping);
    }
    
    public SniHandler(final AsyncMapping<? super String, ? extends SslContext> mapping) {
        this.selection = SniHandler.EMPTY_SELECTION;
        this.mapping = ObjectUtil.checkNotNull(mapping, "mapping");
    }
    
    public String hostname() {
        return this.selection.hostname;
    }
    
    public SslContext sslContext() {
        return this.selection.context;
    }
    
    @Override
    protected Future<SslContext> lookup(final ChannelHandlerContext ctx, final String hostname) throws Exception {
        return this.mapping.map(hostname, ctx.executor().newPromise());
    }
    
    @Override
    protected final void onLookupComplete(final ChannelHandlerContext ctx, final String hostname, final Future<SslContext> future) throws Exception {
        if (future.isSuccess()) {
            final SslContext sslContext = future.getNow();
            this.selection = new Selection(sslContext, hostname);
            try {
                this.replaceHandler(ctx, hostname, sslContext);
            }
            catch (final Throwable cause) {
                this.selection = SniHandler.EMPTY_SELECTION;
                PlatformDependent.throwException(cause);
            }
            return;
        }
        final Throwable cause2 = future.cause();
        if (cause2 instanceof Error) {
            throw (Error)cause2;
        }
        throw new DecoderException("failed to get the SslContext for " + hostname, cause2);
    }
    
    protected void replaceHandler(final ChannelHandlerContext ctx, final String hostname, final SslContext sslContext) throws Exception {
        SslHandler sslHandler = null;
        try {
            sslHandler = this.newSslHandler(sslContext, ctx.alloc());
            ctx.pipeline().replace(this, SslHandler.class.getName(), sslHandler);
            sslHandler = null;
        }
        finally {
            if (sslHandler != null) {
                ReferenceCountUtil.safeRelease(sslHandler.engine());
            }
        }
    }
    
    protected SslHandler newSslHandler(final SslContext context, final ByteBufAllocator allocator) {
        return context.newHandler(allocator);
    }
    
    static {
        EMPTY_SELECTION = new Selection(null, null);
    }
    
    private static final class AsyncMappingAdapter implements AsyncMapping<String, SslContext>
    {
        private final Mapping<? super String, ? extends SslContext> mapping;
        
        private AsyncMappingAdapter(final Mapping<? super String, ? extends SslContext> mapping) {
            this.mapping = ObjectUtil.checkNotNull(mapping, "mapping");
        }
        
        @Override
        public Future<SslContext> map(final String input, final Promise<SslContext> promise) {
            SslContext context;
            try {
                context = (SslContext)this.mapping.map(input);
            }
            catch (final Throwable cause) {
                return promise.setFailure(cause);
            }
            return promise.setSuccess(context);
        }
    }
    
    private static final class Selection
    {
        final SslContext context;
        final String hostname;
        
        Selection(final SslContext context, final String hostname) {
            this.context = context;
            this.hostname = hostname;
        }
    }
}
