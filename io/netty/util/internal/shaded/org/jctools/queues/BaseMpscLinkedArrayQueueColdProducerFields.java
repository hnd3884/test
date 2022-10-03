package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class BaseMpscLinkedArrayQueueColdProducerFields<E> extends BaseMpscLinkedArrayQueuePad3<E>
{
    private static final long P_LIMIT_OFFSET;
    private volatile long producerLimit;
    protected long producerMask;
    protected E[] producerBuffer;
    
    final long lvProducerLimit() {
        return this.producerLimit;
    }
    
    final boolean casProducerLimit(final long expect, final long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong(this, BaseMpscLinkedArrayQueueColdProducerFields.P_LIMIT_OFFSET, expect, newValue);
    }
    
    final void soProducerLimit(final long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, BaseMpscLinkedArrayQueueColdProducerFields.P_LIMIT_OFFSET, newValue);
    }
    
    static {
        P_LIMIT_OFFSET = UnsafeAccess.fieldOffset(BaseMpscLinkedArrayQueueColdProducerFields.class, "producerLimit");
    }
}
