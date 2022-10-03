package io.netty.channel;

import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;

public final class DefaultMessageSizeEstimator implements MessageSizeEstimator
{
    public static final MessageSizeEstimator DEFAULT;
    private final Handle handle;
    
    public DefaultMessageSizeEstimator(final int unknownSize) {
        ObjectUtil.checkPositiveOrZero(unknownSize, "unknownSize");
        this.handle = new HandleImpl(unknownSize);
    }
    
    @Override
    public Handle newHandle() {
        return this.handle;
    }
    
    static {
        DEFAULT = new DefaultMessageSizeEstimator(8);
    }
    
    private static final class HandleImpl implements Handle
    {
        private final int unknownSize;
        
        private HandleImpl(final int unknownSize) {
            this.unknownSize = unknownSize;
        }
        
        @Override
        public int size(final Object msg) {
            if (msg instanceof ByteBuf) {
                return ((ByteBuf)msg).readableBytes();
            }
            if (msg instanceof ByteBufHolder) {
                return ((ByteBufHolder)msg).content().readableBytes();
            }
            if (msg instanceof FileRegion) {
                return 0;
            }
            return this.unknownSize;
        }
    }
}
