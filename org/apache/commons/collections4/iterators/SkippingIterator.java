package org.apache.commons.collections4.iterators;

import java.util.Iterator;

public class SkippingIterator<E> extends AbstractIteratorDecorator<E>
{
    private final long offset;
    private long pos;
    
    public SkippingIterator(final Iterator<E> iterator, final long offset) {
        super(iterator);
        if (offset < 0L) {
            throw new IllegalArgumentException("Offset parameter must not be negative.");
        }
        this.offset = offset;
        this.pos = 0L;
        this.init();
    }
    
    private void init() {
        while (this.pos < this.offset && this.hasNext()) {
            this.next();
        }
    }
    
    @Override
    public E next() {
        final E next = super.next();
        ++this.pos;
        return next;
    }
    
    @Override
    public void remove() {
        if (this.pos <= this.offset) {
            throw new IllegalStateException("remove() can not be called before calling next()");
        }
        super.remove();
    }
}
