package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.NoSuchElementException;
import java.util.Iterator;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import java.util.concurrent.atomic.AtomicReferenceArray;
import io.netty.util.internal.shaded.org.jctools.queues.SupportsIterator;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.queues.IndexedQueueSizeUtil;
import java.util.AbstractQueue;

abstract class AtomicReferenceArrayQueue<E> extends AbstractQueue<E> implements IndexedQueueSizeUtil.IndexedQueue, QueueProgressIndicators, MessagePassingQueue<E>, SupportsIterator
{
    protected final AtomicReferenceArray<E> buffer;
    protected final int mask;
    
    public AtomicReferenceArrayQueue(final int capacity) {
        final int actualCapacity = Pow2.roundToPowerOfTwo(capacity);
        this.mask = actualCapacity - 1;
        this.buffer = new AtomicReferenceArray<E>(actualCapacity);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName();
    }
    
    @Override
    public void clear() {
        while (this.poll() != null) {}
    }
    
    @Override
    public final int capacity() {
        return this.mask + 1;
    }
    
    @Override
    public final int size() {
        return IndexedQueueSizeUtil.size(this);
    }
    
    @Override
    public final boolean isEmpty() {
        return IndexedQueueSizeUtil.isEmpty(this);
    }
    
    @Override
    public final long currentProducerIndex() {
        return this.lvProducerIndex();
    }
    
    @Override
    public final long currentConsumerIndex() {
        return this.lvConsumerIndex();
    }
    
    @Override
    public final Iterator<E> iterator() {
        final long cIndex = this.lvConsumerIndex();
        final long pIndex = this.lvProducerIndex();
        return new WeakIterator<E>(cIndex, pIndex, this.mask, this.buffer);
    }
    
    private static class WeakIterator<E> implements Iterator<E>
    {
        private final long pIndex;
        private final int mask;
        private final AtomicReferenceArray<E> buffer;
        private long nextIndex;
        private E nextElement;
        
        WeakIterator(final long cIndex, final long pIndex, final int mask, final AtomicReferenceArray<E> buffer) {
            this.nextIndex = cIndex;
            this.pIndex = pIndex;
            this.mask = mask;
            this.buffer = buffer;
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
        
        private E getNext() {
            final int mask = this.mask;
            final AtomicReferenceArray<E> buffer = this.buffer;
            while (this.nextIndex < this.pIndex) {
                final int offset = AtomicQueueUtil.calcCircularRefElementOffset(this.nextIndex++, mask);
                final E e = AtomicQueueUtil.lvRefElement(buffer, offset);
                if (e != null) {
                    return e;
                }
            }
            return null;
        }
    }
}
