package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class MpscArrayQueueProducerIndexField<E> extends MpscArrayQueueL1Pad<E>
{
    private static final long P_INDEX_OFFSET;
    private volatile long producerIndex;
    
    MpscArrayQueueProducerIndexField(final int capacity) {
        super(capacity);
    }
    
    @Override
    public final long lvProducerIndex() {
        return this.producerIndex;
    }
    
    final boolean casProducerIndex(final long expect, final long newValue) {
        return UnsafeAccess.UNSAFE.compareAndSwapLong(this, MpscArrayQueueProducerIndexField.P_INDEX_OFFSET, expect, newValue);
    }
    
    static {
        P_INDEX_OFFSET = UnsafeAccess.fieldOffset(MpscArrayQueueProducerIndexField.class, "producerIndex");
    }
}
