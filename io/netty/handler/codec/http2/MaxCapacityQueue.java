package io.netty.handler.codec.http2;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

final class MaxCapacityQueue<E> implements Queue<E>
{
    private final Queue<E> queue;
    private final int maxCapacity;
    
    MaxCapacityQueue(final Queue<E> queue, final int maxCapacity) {
        this.queue = queue;
        this.maxCapacity = maxCapacity;
    }
    
    @Override
    public boolean add(final E element) {
        if (this.offer(element)) {
            return true;
        }
        throw new IllegalStateException();
    }
    
    @Override
    public boolean offer(final E element) {
        return this.maxCapacity > this.queue.size() && this.queue.offer(element);
    }
    
    @Override
    public E remove() {
        return this.queue.remove();
    }
    
    @Override
    public E poll() {
        return this.queue.poll();
    }
    
    @Override
    public E element() {
        return this.queue.element();
    }
    
    @Override
    public E peek() {
        return this.queue.peek();
    }
    
    @Override
    public int size() {
        return this.queue.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.queue.contains(o);
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.queue.iterator();
    }
    
    @Override
    public Object[] toArray() {
        return this.queue.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        return this.queue.toArray(a);
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.queue.remove(o);
    }
    
    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.queue.containsAll(c);
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> c) {
        if (this.maxCapacity >= this.size() + c.size()) {
            return this.queue.addAll((Collection<?>)c);
        }
        throw new IllegalStateException();
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        return this.queue.removeAll(c);
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        return this.queue.retainAll(c);
    }
    
    @Override
    public void clear() {
        this.queue.clear();
    }
}
