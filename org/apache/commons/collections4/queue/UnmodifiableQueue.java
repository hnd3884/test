package org.apache.commons.collections4.queue;

import org.apache.commons.collections4.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Queue;
import org.apache.commons.collections4.Unmodifiable;

public final class UnmodifiableQueue<E> extends AbstractQueueDecorator<E> implements Unmodifiable
{
    private static final long serialVersionUID = 1832948656215393357L;
    
    public static <E> Queue<E> unmodifiableQueue(final Queue<? extends E> queue) {
        if (queue instanceof Unmodifiable) {
            final Queue<E> tmpQueue = (Queue<E>)queue;
            return tmpQueue;
        }
        return new UnmodifiableQueue<E>(queue);
    }
    
    private UnmodifiableQueue(final Queue<? extends E> queue) {
        super(queue);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.decorated());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setCollection((Collection<E>)in.readObject());
    }
    
    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator(this.decorated().iterator());
    }
    
    @Override
    public boolean add(final Object object) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean offer(final E obj) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public E poll() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public E remove() {
        throw new UnsupportedOperationException();
    }
}
