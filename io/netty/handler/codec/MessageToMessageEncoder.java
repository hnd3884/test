package io.netty.handler.codec;

import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.util.internal.StringUtil;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.TypeParameterMatcher;
import io.netty.channel.ChannelOutboundHandlerAdapter;

public abstract class MessageToMessageEncoder<I> extends ChannelOutboundHandlerAdapter
{
    private final TypeParameterMatcher matcher;
    
    protected MessageToMessageEncoder() {
        this.matcher = TypeParameterMatcher.find(this, MessageToMessageEncoder.class, "I");
    }
    
    protected MessageToMessageEncoder(final Class<? extends I> outboundMessageType) {
        this.matcher = TypeParameterMatcher.get(outboundMessageType);
    }
    
    public boolean acceptOutboundMessage(final Object msg) throws Exception {
        return this.matcher.match(msg);
    }
    
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        CodecOutputList out = null;
        try {
            if (this.acceptOutboundMessage(msg)) {
                out = CodecOutputList.newInstance();
                final I cast = (I)msg;
                try {
                    this.encode(ctx, cast, out);
                }
                finally {
                    ReferenceCountUtil.release(cast);
                }
                if (out.isEmpty()) {
                    throw new EncoderException(StringUtil.simpleClassName(this) + " must produce at least one message.");
                }
            }
            else {
                ctx.write(msg, promise);
            }
        }
        catch (final EncoderException e) {
            throw e;
        }
        catch (final Throwable t) {
            throw new EncoderException(t);
        }
        finally {
            if (out != null) {
                try {
                    final int sizeMinusOne = out.size() - 1;
                    if (sizeMinusOne == 0) {
                        ctx.write(out.getUnsafe(0), promise);
                    }
                    else if (sizeMinusOne > 0) {
                        if (promise == ctx.voidPromise()) {
                            writeVoidPromise(ctx, out);
                        }
                        else {
                            writePromiseCombiner(ctx, out, promise);
                        }
                    }
                }
                finally {
                    out.recycle();
                }
            }
        }
    }
    
    private static void writeVoidPromise(final ChannelHandlerContext ctx, final CodecOutputList out) {
        final ChannelPromise voidPromise = ctx.voidPromise();
        for (int i = 0; i < out.size(); ++i) {
            ctx.write(out.getUnsafe(i), voidPromise);
        }
    }
    
    private static void writePromiseCombiner(final ChannelHandlerContext ctx, final CodecOutputList out, final ChannelPromise promise) {
        final PromiseCombiner combiner = new PromiseCombiner(ctx.executor());
        for (int i = 0; i < out.size(); ++i) {
            combiner.add(ctx.write(out.getUnsafe(i)));
        }
        combiner.finish(promise);
    }
    
    protected abstract void encode(final ChannelHandlerContext p0, final I p1, final List<Object> p2) throws Exception;
}
