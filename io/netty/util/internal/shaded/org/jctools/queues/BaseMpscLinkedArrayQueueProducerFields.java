package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class BaseMpscLinkedArrayQueueProducerFields<E> extends BaseMpscLinkedArrayQueuePad1<E>
{
    private static final long P_INDEX_OFFSET;
    private volatile long producerIndex;
    
    @Override
    public final long lvProducerIndex() {
        return this.producerIndex;
    }
    
    final void soProducerIndex(final long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, BaseMpscLinkedArrayQueueProducerFields.P_INDEX_OFFSET, newValue);
    }
    
    final boolean casProducerIndex(final long expect, final long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong(this, BaseMpscLinkedArrayQueueProducerFields.P_INDEX_OFFSET, expect, newValue);
    }
    
    static {
        P_INDEX_OFFSET = UnsafeAccess.fieldOffset(BaseMpscLinkedArrayQueueProducerFields.class, "producerIndex");
    }
}
