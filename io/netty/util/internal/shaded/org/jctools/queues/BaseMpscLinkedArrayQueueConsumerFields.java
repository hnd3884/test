package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class BaseMpscLinkedArrayQueueConsumerFields<E> extends BaseMpscLinkedArrayQueuePad2<E>
{
    private static final long C_INDEX_OFFSET;
    private volatile long consumerIndex;
    protected long consumerMask;
    protected E[] consumerBuffer;
    
    @Override
    public final long lvConsumerIndex() {
        return this.consumerIndex;
    }
    
    final long lpConsumerIndex() {
        return UnsafeAccess.UNSAFE.getLong(this, BaseMpscLinkedArrayQueueConsumerFields.C_INDEX_OFFSET);
    }
    
    final void soConsumerIndex(final long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, BaseMpscLinkedArrayQueueConsumerFields.C_INDEX_OFFSET, newValue);
    }
    
    static {
        C_INDEX_OFFSET = UnsafeAccess.fieldOffset(BaseMpscLinkedArrayQueueConsumerFields.class, "consumerIndex");
    }
}
