package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class MpscArrayQueueProducerLimitField<E> extends MpscArrayQueueMidPad<E>
{
    private static final long P_LIMIT_OFFSET;
    private volatile long producerLimit;
    
    MpscArrayQueueProducerLimitField(final int capacity) {
        super(capacity);
        this.producerLimit = capacity;
    }
    
    final long lvProducerLimit() {
        return this.producerLimit;
    }
    
    final void soProducerLimit(final long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, MpscArrayQueueProducerLimitField.P_LIMIT_OFFSET, newValue);
    }
    
    static {
        P_LIMIT_OFFSET = UnsafeAccess.fieldOffset(MpscArrayQueueProducerLimitField.class, "producerLimit");
    }
}
