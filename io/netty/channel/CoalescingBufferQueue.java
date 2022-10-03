package io.netty.channel;

import io.netty.buffer.Unpooled;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;

public final class CoalescingBufferQueue extends AbstractCoalescingBufferQueue
{
    private final Channel channel;
    
    public CoalescingBufferQueue(final Channel channel) {
        this(channel, 4);
    }
    
    public CoalescingBufferQueue(final Channel channel, final int initSize) {
        this(channel, initSize, false);
    }
    
    public CoalescingBufferQueue(final Channel channel, final int initSize, final boolean updateWritability) {
        super(updateWritability ? channel : null, initSize);
        this.channel = ObjectUtil.checkNotNull(channel, "channel");
    }
    
    public ByteBuf remove(final int bytes, final ChannelPromise aggregatePromise) {
        return this.remove(this.channel.alloc(), bytes, aggregatePromise);
    }
    
    public void releaseAndFailAll(final Throwable cause) {
        this.releaseAndFailAll(this.channel, cause);
    }
    
    @Override
    protected ByteBuf compose(final ByteBufAllocator alloc, final ByteBuf cumulation, final ByteBuf next) {
        if (cumulation instanceof CompositeByteBuf) {
            final CompositeByteBuf composite = (CompositeByteBuf)cumulation;
            composite.addComponent(true, next);
            return composite;
        }
        return this.composeIntoComposite(alloc, cumulation, next);
    }
    
    @Override
    protected ByteBuf removeEmptyValue() {
        return Unpooled.EMPTY_BUFFER;
    }
}
