package org.apache.commons.collections.buffer;

import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.BufferUnderflowException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.commons.collections.BufferOverflowException;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.collections.Buffer;
import org.apache.commons.collections.BoundedCollection;

public class BoundedBuffer extends SynchronizedBuffer implements BoundedCollection
{
    private static final long serialVersionUID = 1536432911093974264L;
    private final int maximumSize;
    private final long timeout;
    
    public static BoundedBuffer decorate(final Buffer buffer, final int maximumSize) {
        return new BoundedBuffer(buffer, maximumSize, 0L);
    }
    
    public static BoundedBuffer decorate(final Buffer buffer, final int maximumSize, final long timeout) {
        return new BoundedBuffer(buffer, maximumSize, timeout);
    }
    
    protected BoundedBuffer(final Buffer buffer, final int maximumSize, final long timeout) {
        super(buffer);
        if (maximumSize < 1) {
            throw new IllegalArgumentException();
        }
        this.maximumSize = maximumSize;
        this.timeout = timeout;
    }
    
    public Object remove() {
        synchronized (this.lock) {
            final Object returnValue = this.getBuffer().remove();
            this.lock.notifyAll();
            return returnValue;
        }
    }
    
    public boolean add(final Object o) {
        synchronized (this.lock) {
            this.timeoutWait(1);
            return this.getBuffer().add(o);
        }
    }
    
    public boolean addAll(final Collection c) {
        synchronized (this.lock) {
            this.timeoutWait(c.size());
            return this.getBuffer().addAll(c);
        }
    }
    
    public Iterator iterator() {
        return new NotifyingIterator(this.collection.iterator());
    }
    
    private void timeoutWait(final int nAdditions) {
        if (nAdditions > this.maximumSize) {
            throw new BufferOverflowException("Buffer size cannot exceed " + this.maximumSize);
        }
        if (this.timeout <= 0L) {
            if (this.getBuffer().size() + nAdditions > this.maximumSize) {
                throw new BufferOverflowException("Buffer size cannot exceed " + this.maximumSize);
            }
        }
        else {
            final long expiration = System.currentTimeMillis() + this.timeout;
            long timeLeft = expiration - System.currentTimeMillis();
            while (timeLeft > 0L && this.getBuffer().size() + nAdditions > this.maximumSize) {
                try {
                    this.lock.wait(timeLeft);
                    timeLeft = expiration - System.currentTimeMillis();
                    continue;
                }
                catch (final InterruptedException ex) {
                    final PrintWriter out = new PrintWriter(new StringWriter());
                    ex.printStackTrace(out);
                    throw new BufferUnderflowException("Caused by InterruptedException: " + out.toString());
                }
                break;
            }
            if (this.getBuffer().size() + nAdditions > this.maximumSize) {
                throw new BufferOverflowException("Timeout expired");
            }
        }
    }
    
    public boolean isFull() {
        return this.size() == this.maxSize();
    }
    
    public int maxSize() {
        return this.maximumSize;
    }
    
    private class NotifyingIterator extends AbstractIteratorDecorator
    {
        public NotifyingIterator(final Iterator it) {
            super(it);
        }
        
        public void remove() {
            synchronized (BoundedBuffer.this.lock) {
                this.iterator.remove();
                BoundedBuffer.this.lock.notifyAll();
            }
        }
    }
}
