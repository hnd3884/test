package io.netty.util.internal.shaded.org.jctools.queues;

import java.util.Iterator;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

public class MpscArrayQueue<E> extends MpscArrayQueueL3Pad<E>
{
    public MpscArrayQueue(final int capacity) {
        super(capacity);
    }
    
    public boolean offerIfBelowThreshold(final E e, final int threshold) {
        if (null == e) {
            throw new NullPointerException();
        }
        final long mask = this.mask;
        final long capacity = mask + 1L;
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
        final long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(pIndex, mask);
        UnsafeRefArrayAccess.soRefElement(this.buffer, offset, e);
        return true;
    }
    
    @Override
    public boolean offer(final E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        final long mask = this.mask;
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
        final long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(pIndex, mask);
        UnsafeRefArrayAccess.soRefElement(this.buffer, offset, e);
        return true;
    }
    
    public final int failFastOffer(final E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        final long mask = this.mask;
        final long capacity = mask + 1L;
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
        final long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(pIndex, mask);
        UnsafeRefArrayAccess.soRefElement(this.buffer, offset, e);
        return 0;
    }
    
    @Override
    public E poll() {
        final long cIndex = this.lpConsumerIndex();
        final long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(cIndex, this.mask);
        final E[] buffer = this.buffer;
        E e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
        if (null == e) {
            if (cIndex == this.lvProducerIndex()) {
                return null;
            }
            do {
                e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
            } while (e == null);
        }
        UnsafeRefArrayAccess.spRefElement(buffer, offset, (E)null);
        this.soConsumerIndex(cIndex + 1L);
        return e;
    }
    
    @Override
    public E peek() {
        final E[] buffer = this.buffer;
        final long cIndex = this.lpConsumerIndex();
        final long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(cIndex, this.mask);
        E e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
        if (null == e) {
            if (cIndex == this.lvProducerIndex()) {
                return null;
            }
            do {
                e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
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
        final E[] buffer = this.buffer;
        final long cIndex = this.lpConsumerIndex();
        final long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(cIndex, this.mask);
        final E e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
        if (null == e) {
            return null;
        }
        UnsafeRefArrayAccess.spRefElement(buffer, offset, (E)null);
        this.soConsumerIndex(cIndex + 1L);
        return e;
    }
    
    @Override
    public E relaxedPeek() {
        final E[] buffer = this.buffer;
        final long mask = this.mask;
        final long cIndex = this.lpConsumerIndex();
        return UnsafeRefArrayAccess.lvRefElement(buffer, UnsafeRefArrayAccess.calcCircularRefElementOffset(cIndex, mask));
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
        final E[] buffer = this.buffer;
        final long mask = this.mask;
        final long cIndex = this.lpConsumerIndex();
        for (int i = 0; i < limit; ++i) {
            final long index = cIndex + i;
            final long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(index, mask);
            final E e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
            if (null == e) {
                return i;
            }
            UnsafeRefArrayAccess.spRefElement(buffer, offset, (E)null);
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
        final long mask = this.mask;
        final long capacity = mask + 1L;
        long producerLimit = this.lvProducerLimit();
        long pIndex;
        int actualLimit;
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
        final E[] buffer = this.buffer;
        for (int i = 0; i < actualLimit; ++i) {
            final long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(pIndex + i, mask);
            UnsafeRefArrayAccess.soRefElement(buffer, offset, s.get());
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
}
