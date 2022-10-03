package io.netty.util.internal.shaded.org.jctools.queues;

import java.util.NoSuchElementException;
import java.util.Iterator;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;

abstract class ConcurrentCircularArrayQueue<E> extends ConcurrentCircularArrayQueueL0Pad<E> implements MessagePassingQueue<E>, IndexedQueueSizeUtil.IndexedQueue, QueueProgressIndicators, SupportsIterator
{
    protected final long mask;
    protected final E[] buffer;
    
    ConcurrentCircularArrayQueue(final int capacity) {
        final int actualCapacity = Pow2.roundToPowerOfTwo(capacity);
        this.mask = actualCapacity - 1;
        this.buffer = UnsafeRefArrayAccess.allocateRefArray(actualCapacity);
    }
    
    @Override
    public int size() {
        return IndexedQueueSizeUtil.size(this);
    }
    
    @Override
    public boolean isEmpty() {
        return IndexedQueueSizeUtil.isEmpty(this);
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
    public int capacity() {
        return (int)(this.mask + 1L);
    }
    
    @Override
    public long currentProducerIndex() {
        return this.lvProducerIndex();
    }
    
    @Override
    public long currentConsumerIndex() {
        return this.lvConsumerIndex();
    }
    
    @Override
    public Iterator<E> iterator() {
        final long cIndex = this.lvConsumerIndex();
        final long pIndex = this.lvProducerIndex();
        return new WeakIterator<E>(cIndex, pIndex, this.mask, this.buffer);
    }
    
    private static class WeakIterator<E> implements Iterator<E>
    {
        private final long pIndex;
        private final long mask;
        private final E[] buffer;
        private long nextIndex;
        private E nextElement;
        
        WeakIterator(final long cIndex, final long pIndex, final long mask, final E[] buffer) {
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
            while (this.nextIndex < this.pIndex) {
                final long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(this.nextIndex++, this.mask);
                final E e = UnsafeRefArrayAccess.lvRefElement(this.buffer, offset);
                if (e != null) {
                    return e;
                }
            }
            return null;
        }
    }
}
