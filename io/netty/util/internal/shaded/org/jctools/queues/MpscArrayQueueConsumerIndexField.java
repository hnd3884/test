package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class MpscArrayQueueConsumerIndexField<E> extends MpscArrayQueueL2Pad<E>
{
    private static final long C_INDEX_OFFSET;
    private volatile long consumerIndex;
    
    MpscArrayQueueConsumerIndexField(final int capacity) {
        super(capacity);
    }
    
    @Override
    public final long lvConsumerIndex() {
        return this.consumerIndex;
    }
    
    final long lpConsumerIndex() {
        return UnsafeAccess.UNSAFE.getLong(this, MpscArrayQueueConsumerIndexField.C_INDEX_OFFSET);
    }
    
    final void soConsumerIndex(final long newValue) {
        UnsafeAccess.UNSAFE.putOrderedLong(this, MpscArrayQueueConsumerIndexField.C_INDEX_OFFSET, newValue);
    }
    
    static {
        C_INDEX_OFFSET = UnsafeAccess.fieldOffset(MpscArrayQueueConsumerIndexField.class, "consumerIndex");
    }
}
