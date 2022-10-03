package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MpscAtomicArrayQueue<E> extends MpscAtomicArrayQueueL3Pad<E>
{
    public MpscAtomicArrayQueue(final int capacity) {
        super(capacity);
    }
    
    public boolean offerIfBelowThreshold(final E e, final int threshold) {
        if (null == e) {
            throw new NullPointerException();
        }
        final int mask = this.mask;
        final long capacity = mask + 1;
        long producerLimit = this.lvProducerLimit();
        long pIndex;
        do {
            pIndex = this.lvProducerIndex();
            final long available = producerLimit - pIndex;
            long size = capacity - available;
            if (size >= threshold) {
                final long cIndex = this.lvConsumerIndex();
                size = pIndex - cIndex;
                if (size >= threshold) {
                    return false;
                }
                producerLimit = cIndex + capacity;
                this.soProducerLimit(producerLimit);
            }
        } while (!this.casProducerIndex(pIndex, pIndex + 1L));
        final int offset = AtomicQueueUtil.calcCircularRefElementOffset(pIndex, mask);
        AtomicQueueUtil.soRefElement(this.buffer, offset, e);
        return true;
    }
    
    @Override
    public boolean offer(final E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        final int mask = this.mask;
        long producerLimit = this.lvProducerLimit();
        long pIndex;
        do {
            pIndex = this.lvProducerIndex();
            if (pIndex >= producerLimit) {
                final long cIndex = this.lvConsumerIndex();
                producerLimit = cIndex + mask + 1L;
                if (pIndex >= producerLimit) {
                    return false;
                }
                this.soProducerLimit(producerLimit);
            }
        } while (!this.casProducerIndex(pIndex, pIndex + 1L));
        final int offset = AtomicQueueUtil.calcCircularRefElementOffset(pIndex, mask);
        AtomicQueueUtil.soRefElement(this.buffer, offset, e);
        return true;
    }
    
    public final int failFastOffer(final E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        final int mask = this.mask;
        final long capacity = mask + 1;
        final long pIndex = this.lvProducerIndex();
        long producerLimit = this.lvProducerLimit();
        if (pIndex >= producerLimit) {
            final long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + capacity;
            if (pIndex >= producerLimit) {
                return 1;
            }
            this.soProducerLimit(producerLimit);
        }
        if (!this.casProducerIndex(pIndex, pIndex + 1L)) {
            return -1;
        }
        final int offset = AtomicQueueUtil.calcCircularRefElementOffset(pIndex, mask);
        AtomicQueueUtil.soRefElement(this.buffer, offset, e);
        return 0;
    }
    
    @Override
    public E poll() {
        final long cIndex = this.lpConsumerIndex();
        final int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, this.mask);
        final AtomicReferenceArray<E> buffer = this.buffer;
        E e = AtomicQueueUtil.lvRefElement(buffer, offset);
        if (null == e) {
            if (cIndex == this.lvProducerIndex()) {
                return null;
            }
            do {
                e = AtomicQueueUtil.lvRefElement(buffer, offset);
            } while (e == null);
        }
        AtomicQueueUtil.spRefElement(buffer, offset, (E)null);
        this.soConsumerIndex(cIndex + 1L);
        return e;
    }
    
    @Override
    public E peek() {
        final AtomicReferenceArray<E> buffer = this.buffer;
        final long cIndex = this.lpConsumerIndex();
        final int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, this.mask);
        E e = AtomicQueueUtil.lvRefElement(buffer, offset);
        if (null == e) {
            if (cIndex == this.lvProducerIndex()) {
                return null;
            }
            do {
                e = AtomicQueueUtil.lvRefElement(buffer, offset);
            } while (e == null);
        }
        return e;
    }
    
    @Override
    public boolean relaxedOffer(final E e) {
        return this.offer(e);
    }
    
    @Override
    public E relaxedPoll() {
        final AtomicReferenceArray<E> buffer = this.buffer;
        final long cIndex = this.lpConsumerIndex();
        final int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, this.mask);
        final E e = AtomicQueueUtil.lvRefElement(buffer, offset);
        if (null == e) {
            return null;
        }
        AtomicQueueUtil.spRefElement(buffer, offset, (E)null);
        this.soConsumerIndex(cIndex + 1L);
        return e;
    }
    
    @Override
    public E relaxedPeek() {
        final AtomicReferenceArray<E> buffer = this.buffer;
        final int mask = this.mask;
        final long cIndex = this.lpConsumerIndex();
        return AtomicQueueUtil.lvRefElement(buffer, AtomicQueueUtil.calcCircularRefElementOffset(cIndex, mask));
    }
    
    @Override
    public int drain(final MessagePassingQueue.Consumer<E> c, final int limit) {
        if (null == c) {
            throw new IllegalArgumentException("c is null");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit is negative: " + limit);
        }
        if (limit == 0) {
            return 0;
        }
        final AtomicReferenceArray<E> buffer = this.buffer;
        final int mask = this.mask;
        final long cIndex = this.lpConsumerIndex();
        for (int i = 0; i < limit; ++i) {
            final long index = cIndex + i;
            final int offset = AtomicQueueUtil.calcCircularRefElementOffset(index, mask);
            final E e = AtomicQueueUtil.lvRefElement(buffer, offset);
            if (null == e) {
                return i;
            }
            AtomicQueueUtil.spRefElement(buffer, offset, (E)null);
            this.soConsumerIndex(index + 1L);
            c.accept(e);
        }
        return limit;
    }
    
    @Override
    public int fill(final MessagePassingQueue.Supplier<E> s, final int limit) {
        if (null == s) {
            throw new IllegalArgumentException("supplier is null");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit is negative:" + limit);
        }
        if (limit == 0) {
            return 0;
        }
        final int mask = this.mask;
        final long capacity = mask + 1;
        long producerLimit = this.lvProducerLimit();
        int actualLimit = 0;
        long pIndex;
        do {
            pIndex = this.lvProducerIndex();
            long available = producerLimit - pIndex;
            if (available <= 0L) {
                final long cIndex = this.lvConsumerIndex();
                producerLimit = cIndex + capacity;
                available = producerLimit - pIndex;
                if (available <= 0L) {
                    return 0;
                }
                this.soProducerLimit(producerLimit);
            }
            actualLimit = Math.min((int)available, limit);
        } while (!this.casProducerIndex(pIndex, pIndex + actualLimit));
        final AtomicReferenceArray<E> buffer = this.buffer;
        for (int i = 0; i < actualLimit; ++i) {
            final int offset = AtomicQueueUtil.calcCircularRefElementOffset(pIndex + i, mask);
            AtomicQueueUtil.soRefElement(buffer, offset, s.get());
        }
        return actualLimit;
    }
    
    @Override
    public int drain(final MessagePassingQueue.Consumer<E> c) {
        return this.drain(c, this.capacity());
    }
    
    @Override
    public int fill(final MessagePassingQueue.Supplier<E> s) {
        return MessagePassingQueueUtil.fillBounded(this, s);
    }
    
    @Override
    public void drain(final MessagePassingQueue.Consumer<E> c, final MessagePassingQueue.WaitStrategy w, final MessagePassingQueue.ExitCondition exit) {
        MessagePassingQueueUtil.drain(this, c, w, exit);
    }
    
    @Override
    public void fill(final MessagePassingQueue.Supplier<E> s, final MessagePassingQueue.WaitStrategy wait, final MessagePassingQueue.ExitCondition exit) {
        MessagePassingQueueUtil.fill(this, s, wait, exit);
    }
    
    @Deprecated
    public int weakOffer(final E e) {
        return this.failFastOffer(e);
    }
}
