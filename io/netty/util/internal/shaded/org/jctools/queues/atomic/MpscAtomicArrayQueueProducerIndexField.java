package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicArrayQueueProducerIndexField<E> extends MpscAtomicArrayQueueL1Pad<E>
{
    private static final AtomicLongFieldUpdater<MpscAtomicArrayQueueProducerIndexField> P_INDEX_UPDATER;
    private volatile long producerIndex;
    
    MpscAtomicArrayQueueProducerIndexField(final int capacity) {
        super(capacity);
    }
    
    @Override
    public final long lvProducerIndex() {
        return this.producerIndex;
    }
    
    final boolean casProducerIndex(final long expect, final long newValue) {
        return MpscAtomicArrayQueueProducerIndexField.P_INDEX_UPDATER.compareAndSet(this, expect, newValue);
    }
    
    static {
        P_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(MpscAtomicArrayQueueProducerIndexField.class, "producerIndex");
    }
}
