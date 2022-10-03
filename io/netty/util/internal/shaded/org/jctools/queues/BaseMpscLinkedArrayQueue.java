package io.netty.util.internal.shaded.org.jctools.queues;

import java.util.NoSuchElementException;
import java.util.Iterator;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;

abstract class BaseMpscLinkedArrayQueue<E> extends BaseMpscLinkedArrayQueueColdProducerFields<E> implements MessagePassingQueue<E>, QueueProgressIndicators
{
    private static final Object JUMP;
    private static final Object BUFFER_CONSUMED;
    private static final int CONTINUE_TO_P_INDEX_CAS = 0;
    private static final int RETRY = 1;
    private static final int QUEUE_FULL = 2;
    private static final int QUEUE_RESIZE = 3;
    
    public BaseMpscLinkedArrayQueue(final int initialCapacity) {
        RangeUtil.checkGreaterThanOrEqual(initialCapacity, 2, "initialCapacity");
        final int p2capacity = Pow2.roundToPowerOfTwo(initialCapacity);
        final long mask = p2capacity - 1 << 1;
        final E[] buffer = UnsafeRefArrayAccess.allocateRefArray(p2capacity + 1);
        this.producerBuffer = buffer;
        this.producerMask = mask;
        this.consumerBuffer = buffer;
        this.soProducerLimit(this.consumerMask = mask);
    }
    
    @Override
    public int size() {
        long after = this.lvConsumerIndex();
        long before;
        long currentProducerIndex;
        do {
            before = after;
            currentProducerIndex = this.lvProducerIndex();
            after = this.lvConsumerIndex();
        } while (before != after);
        final long size = currentProducerIndex - after >> 1;
        if (size > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        return (int)size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.lvConsumerIndex() == this.lvProducerIndex();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName();
    }
    
    @Override
    public boolean offer(final E e) {
        if (null == e) {
            throw new NullPointerException();
        }
        while (true) {
            final long producerLimit = this.lvProducerLimit();
            final long pIndex = this.lvProducerIndex();
            if ((pIndex & 0x1L) == 0x1L) {
                continue;
            }
            final long mask = this.producerMask;
            final E[] buffer = this.producerBuffer;
            if (producerLimit <= pIndex) {
                final int result = this.offerSlowPath(mask, pIndex, producerLimit);
                switch (result) {
                    case 1: {
                        continue;
                    }
                    case 2: {
                        return false;
                    }
                    case 3: {
                        this.resize(mask, buffer, pIndex, e, null);
                        return true;
                    }
                }
            }
            if (this.casProducerIndex(pIndex, pIndex + 2L)) {
                final long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(pIndex, mask);
                UnsafeRefArrayAccess.soRefElement(buffer, offset, e);
                return true;
            }
        }
    }
    
    @Override
    public E poll() {
        final E[] buffer = this.consumerBuffer;
        final long index = this.lpConsumerIndex();
        final long mask = this.consumerMask;
        final long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(index, mask);
        Object e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
        if (e == null) {
            if (index == this.lvProducerIndex()) {
                return null;
            }
            do {
                e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
            } while (e == null);
        }
        if (e == BaseMpscLinkedArrayQueue.JUMP) {
            final E[] nextBuffer = this.nextBuffer(buffer, mask);
            return this.newBufferPoll(nextBuffer, index);
        }
        UnsafeRefArrayAccess.soRefElement(buffer, offset, (E)null);
        this.soConsumerIndex(index + 2L);
        return (E)e;
    }
    
    @Override
    public E peek() {
        final E[] buffer = this.consumerBuffer;
        final long index = this.lpConsumerIndex();
        final long mask = this.consumerMask;
        final long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(index, mask);
        Object e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
        if (e == null && index != this.lvProducerIndex()) {
            do {
                e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
            } while (e == null);
        }
        if (e == BaseMpscLinkedArrayQueue.JUMP) {
            return this.newBufferPeek(this.nextBuffer(buffer, mask), index);
        }
        return (E)e;
    }
    
    private int offerSlowPath(final long mask, final long pIndex, final long producerLimit) {
        final long cIndex = this.lvConsumerIndex();
        final long bufferCapacity = this.getCurrentBufferCapacity(mask);
        if (cIndex + bufferCapacity > pIndex) {
            if (!this.casProducerLimit(producerLimit, cIndex + bufferCapacity)) {
                return 1;
            }
            return 0;
        }
        else {
            if (this.availableInQueue(pIndex, cIndex) <= 0L) {
                return 2;
            }
            if (this.casProducerIndex(pIndex, pIndex + 1L)) {
                return 3;
            }
            return 1;
        }
    }
    
    protected abstract long availableInQueue(final long p0, final long p1);
    
    private E[] nextBuffer(final E[] buffer, final long mask) {
        final long offset = nextArrayOffset(mask);
        final E[] nextBuffer = UnsafeRefArrayAccess.lvRefElement((E[][])(Object)buffer, offset);
        this.consumerBuffer = nextBuffer;
        this.consumerMask = LinkedArrayQueueUtil.length(nextBuffer) - 2 << 1;
        UnsafeRefArrayAccess.soRefElement(buffer, offset, BaseMpscLinkedArrayQueue.BUFFER_CONSUMED);
        return nextBuffer;
    }
    
    private static long nextArrayOffset(final long mask) {
        return LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(mask + 2L, Long.MAX_VALUE);
    }
    
    private E newBufferPoll(final E[] nextBuffer, final long index) {
        final long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(index, this.consumerMask);
        final E n = UnsafeRefArrayAccess.lvRefElement(nextBuffer, offset);
        if (n == null) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        UnsafeRefArrayAccess.soRefElement(nextBuffer, offset, (E)null);
        this.soConsumerIndex(index + 2L);
        return n;
    }
    
    private E newBufferPeek(final E[] nextBuffer, final long index) {
        final long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(index, this.consumerMask);
        final E n = UnsafeRefArrayAccess.lvRefElement(nextBuffer, offset);
        if (null == n) {
            throw new IllegalStateException("new buffer must have at least one element");
        }
        return n;
    }
    
    @Override
    public long currentProducerIndex() {
        return this.lvProducerIndex() / 2L;
    }
    
    @Override
    public long currentConsumerIndex() {
        return this.lvConsumerIndex() / 2L;
    }
    
    @Override
    public abstract int capacity();
    
    @Override
    public boolean relaxedOffer(final E e) {
        return this.offer(e);
    }
    
    @Override
    public E relaxedPoll() {
        final E[] buffer = this.consumerBuffer;
        final long index = this.lpConsumerIndex();
        final long mask = this.consumerMask;
        final long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(index, mask);
        final Object e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
        if (e == null) {
            return null;
        }
        if (e == BaseMpscLinkedArrayQueue.JUMP) {
            final E[] nextBuffer = this.nextBuffer(buffer, mask);
            return this.newBufferPoll(nextBuffer, index);
        }
        UnsafeRefArrayAccess.soRefElement(buffer, offset, (E)null);
        this.soConsumerIndex(index + 2L);
        return (E)e;
    }
    
    @Override
    public E relaxedPeek() {
        final E[] buffer = this.consumerBuffer;
        final long index = this.lpConsumerIndex();
        final long mask = this.consumerMask;
        final long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(index, mask);
        final Object e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
        if (e == BaseMpscLinkedArrayQueue.JUMP) {
            return this.newBufferPeek(this.nextBuffer(buffer, mask), index);
        }
        return (E)e;
    }
    
    @Override
    public int fill(final Supplier<E> s) {
        long result = 0L;
        final int capacity = this.capacity();
        do {
            final int filled = this.fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH);
            if (filled == 0) {
                return (int)result;
            }
            result += filled;
        } while (result <= capacity);
        return (int)result;
    }
    
    @Override
    public int fill(final Supplier<E> s, final int limit) {
        if (null == s) {
            throw new IllegalArgumentException("supplier is null");
        }
        if (limit < 0) {
            throw new IllegalArgumentException("limit is negative:" + limit);
        }
        if (limit == 0) {
            return 0;
        }
        while (true) {
            final long producerLimit = this.lvProducerLimit();
            final long pIndex = this.lvProducerIndex();
            if ((pIndex & 0x1L) == 0x1L) {
                continue;
            }
            final long mask = this.producerMask;
            final E[] buffer = this.producerBuffer;
            final long batchIndex = Math.min(producerLimit, pIndex + 2L * limit);
            if (pIndex >= producerLimit) {
                final int result = this.offerSlowPath(mask, pIndex, producerLimit);
                switch (result) {
                    case 0:
                    case 1: {
                        continue;
                    }
                    case 2: {
                        return 0;
                    }
                    case 3: {
                        this.resize(mask, buffer, pIndex, null, s);
                        return 1;
                    }
                }
            }
            if (this.casProducerIndex(pIndex, batchIndex)) {
                final int claimedSlots = (int)((batchIndex - pIndex) / 2L);
                for (int i = 0; i < claimedSlots; ++i) {
                    final long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(pIndex + 2L * i, mask);
                    UnsafeRefArrayAccess.soRefElement(buffer, offset, s.get());
                }
                return claimedSlots;
            }
        }
    }
    
    @Override
    public void fill(final Supplier<E> s, final WaitStrategy wait, final ExitCondition exit) {
        MessagePassingQueueUtil.fill(this, s, wait, exit);
    }
    
    @Override
    public int drain(final Consumer<E> c) {
        return this.drain(c, this.capacity());
    }
    
    @Override
    public int drain(final Consumer<E> c, final int limit) {
        return MessagePassingQueueUtil.drain(this, c, limit);
    }
    
    @Override
    public void drain(final Consumer<E> c, final WaitStrategy wait, final ExitCondition exit) {
        MessagePassingQueueUtil.drain(this, c, wait, exit);
    }
    
    @Override
    public Iterator<E> iterator() {
        return new WeakIterator<E>(this.consumerBuffer, this.lvConsumerIndex(), this.lvProducerIndex());
    }
    
    private void resize(final long oldMask, final E[] oldBuffer, final long pIndex, final E e, final Supplier<E> s) {
        assert s != null;
        final int newBufferLength = this.getNextBufferSize(oldBuffer);
        E[] newBuffer;
        try {
            newBuffer = UnsafeRefArrayAccess.allocateRefArray(newBufferLength);
        }
        catch (final OutOfMemoryError oom) {
            assert this.lvProducerIndex() == pIndex + 1L;
            this.soProducerIndex(pIndex);
            throw oom;
        }
        this.producerBuffer = newBuffer;
        final int newMask = newBufferLength - 2 << 1;
        this.producerMask = newMask;
        final long offsetInOld = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(pIndex, oldMask);
        final long offsetInNew = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(pIndex, newMask);
        UnsafeRefArrayAccess.soRefElement(newBuffer, offsetInNew, (e == null) ? s.get() : e);
        UnsafeRefArrayAccess.soRefElement(oldBuffer, nextArrayOffset(oldMask), newBuffer);
        final long cIndex = this.lvConsumerIndex();
        final long availableInQueue = this.availableInQueue(pIndex, cIndex);
        RangeUtil.checkPositive(availableInQueue, "availableInQueue");
        this.soProducerLimit(pIndex + Math.min(newMask, availableInQueue));
        this.soProducerIndex(pIndex + 2L);
        UnsafeRefArrayAccess.soRefElement(oldBuffer, offsetInOld, BaseMpscLinkedArrayQueue.JUMP);
    }
    
    protected abstract int getNextBufferSize(final E[] p0);
    
    protected abstract long getCurrentBufferCapacity(final long p0);
    
    static {
        JUMP = new Object();
        BUFFER_CONSUMED = new Object();
    }
    
    private static class WeakIterator<E> implements Iterator<E>
    {
        private final long pIndex;
        private long nextIndex;
        private E nextElement;
        private E[] currentBuffer;
        private int mask;
        
        WeakIterator(final E[] currentBuffer, final long cIndex, final long pIndex) {
            this.pIndex = pIndex >> 1;
            this.nextIndex = cIndex >> 1;
            this.setBuffer(currentBuffer);
            this.nextElement = this.getNext();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }
        
        @Override
        public boolean hasNext() {
            return this.nextElement != null;
        }
        
        @Override
        public E next() {
            final E e = this.nextElement;
            if (e == null) {
                throw new NoSuchElementException();
            }
            this.nextElement = this.getNext();
            return e;
        }
        
        private void setBuffer(final E[] buffer) {
            this.currentBuffer = buffer;
            this.mask = LinkedArrayQueueUtil.length(buffer) - 2;
        }
        
        private E getNext() {
            while (this.nextIndex < this.pIndex) {
                final long index = this.nextIndex++;
                E e = UnsafeRefArrayAccess.lvRefElement(this.currentBuffer, UnsafeRefArrayAccess.calcCircularRefElementOffset(index, this.mask));
                if (e == null) {
                    continue;
                }
                if (e != BaseMpscLinkedArrayQueue.JUMP) {
                    return e;
                }
                final int nextBufferIndex = this.mask + 1;
                final Object nextBuffer = UnsafeRefArrayAccess.lvRefElement(this.currentBuffer, UnsafeRefArrayAccess.calcRefElementOffset(nextBufferIndex));
                if (nextBuffer == BaseMpscLinkedArrayQueue.BUFFER_CONSUMED || nextBuffer == null) {
                    return null;
                }
                this.setBuffer((Object[])nextBuffer);
                e = UnsafeRefArrayAccess.lvRefElement(this.currentBuffer, UnsafeRefArrayAccess.calcCircularRefElementOffset(index, this.mask));
                if (e == null) {
                    continue;
                }
                return e;
            }
            return null;
        }
    }
}
