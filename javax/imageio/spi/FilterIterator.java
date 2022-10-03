package javax.imageio.spi;

import java.util.NoSuchElementException;
import java.util.Iterator;

class FilterIterator<T> implements Iterator<T>
{
    private Iterator<T> iter;
    private ServiceRegistry.Filter filter;
    private T next;
    
    public FilterIterator(final Iterator<T> iter, final ServiceRegistry.Filter filter) {
        this.next = null;
        this.iter = iter;
        this.filter = filter;
        this.advance();
    }
    
    private void advance() {
        while (this.iter.hasNext()) {
            final T next = this.iter.next();
            if (this.filter.filter(next)) {
                this.next = next;
                return;
            }
        }
        this.next = null;
    }
    
    @Override
    public boolean hasNext() {
        return this.next != null;
    }
    
    @Override
    public T next() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        final T next = this.next;
        this.advance();
        return next;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
