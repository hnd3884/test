package io.netty.channel;

import io.netty.util.internal.ObjectUtil;

public class FixedRecvByteBufAllocator extends DefaultMaxMessagesRecvByteBufAllocator
{
    private final int bufferSize;
    
    public FixedRecvByteBufAllocator(final int bufferSize) {
        ObjectUtil.checkPositive(bufferSize, "bufferSize");
        this.bufferSize = bufferSize;
    }
    
    @Override
    public RecvByteBufAllocator.Handle newHandle() {
        return new HandleImpl(this.bufferSize);
    }
    
    @Override
    public FixedRecvByteBufAllocator respectMaybeMoreData(final boolean respectMaybeMoreData) {
        super.respectMaybeMoreData(respectMaybeMoreData);
        return this;
    }
    
    private final class HandleImpl extends MaxMessageHandle
    {
        private final int bufferSize;
        
        HandleImpl(final int bufferSize) {
            this.bufferSize = bufferSize;
        }
        
        @Override
        public int guess() {
            return this.bufferSize;
        }
    }
}
