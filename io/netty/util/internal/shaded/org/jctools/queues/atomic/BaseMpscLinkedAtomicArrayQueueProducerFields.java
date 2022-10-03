package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class BaseMpscLinkedAtomicArrayQueueProducerFields<E> extends BaseMpscLinkedAtomicArrayQueuePad1<E>
{
    private static final AtomicLongFieldUpdater<BaseMpscLinkedAtomicArrayQueueProducerFields> P_INDEX_UPDATER;
    private volatile long producerIndex;
    
    @Override
    public final long lvProducerIndex() {
        return this.producerIndex;
    }
    
    final void soProducerIndex(final long newValue) {
        BaseMpscLinkedAtomicArrayQueueProducerFields.P_INDEX_UPDATER.lazySet(this, newValue);
    }
    
    final boolean casProducerIndex(final long expect, final long newValue) {
        return BaseMpscLinkedAtomicArrayQueueProducerFields.P_INDEX_UPDATER.compareAndSet(this, expect, newValue);
    }
    
    static {
        P_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(BaseMpscLinkedAtomicArrayQueueProducerFields.class, "producerIndex");
    }
}
