package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class BaseMpscLinkedAtomicArrayQueueColdProducerFields<E> extends BaseMpscLinkedAtomicArrayQueuePad3<E>
{
    private static final AtomicLongFieldUpdater<BaseMpscLinkedAtomicArrayQueueColdProducerFields> P_LIMIT_UPDATER;
    private volatile long producerLimit;
    protected long producerMask;
    protected AtomicReferenceArray<E> producerBuffer;
    
    final long lvProducerLimit() {
        return this.producerLimit;
    }
    
    final boolean casProducerLimit(final long expect, final long newValue) {
        return BaseMpscLinkedAtomicArrayQueueColdProducerFields.P_LIMIT_UPDATER.compareAndSet(this, expect, newValue);
    }
    
    final void soProducerLimit(final long newValue) {
        BaseMpscLinkedAtomicArrayQueueColdProducerFields.P_LIMIT_UPDATER.lazySet(this, newValue);
    }
    
    static {
        P_LIMIT_UPDATER = AtomicLongFieldUpdater.newUpdater(BaseMpscLinkedAtomicArrayQueueColdProducerFields.class, "producerLimit");
    }
}
