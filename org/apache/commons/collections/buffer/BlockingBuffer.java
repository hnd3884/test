package org.apache.commons.collections.buffer;

import org.apache.commons.collections.BufferUnderflowException;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import org.apache.commons.collections.Buffer;

public class BlockingBuffer extends SynchronizedBuffer
{
    private static final long serialVersionUID = 1719328905017860541L;
    private final long timeout;
    
    public static Buffer decorate(final Buffer buffer) {
        return new BlockingBuffer(buffer);
    }
    
    public static Buffer decorate(final Buffer buffer, final long timeoutMillis) {
        return new BlockingBuffer(buffer, timeoutMillis);
    }
    
    protected BlockingBuffer(final Buffer buffer) {
        super(buffer);
        this.timeout = 0L;
    }
    
    protected BlockingBuffer(final Buffer buffer, final long timeoutMillis) {
        super(buffer);
        this.timeout = ((timeoutMillis < 0L) ? 0L : timeoutMillis);
    }
    
    public boolean add(final Object o) {
        synchronized (this.lock) {
            final boolean result = this.collection.add(o);
            this.lock.notifyAll();
            return result;
        }
    }
    
    public boolean addAll(final Collection c) {
        synchronized (this.lock) {
            final boolean result = this.collection.addAll(c);
            this.lock.notifyAll();
            return result;
        }
    }
    
    public Object get() {
        synchronized (this.lock) {
            while (this.collection.isEmpty()) {
                try {
                    if (this.timeout <= 0L) {
                        this.lock.wait();
                        continue;
                    }
                    return this.get(this.timeout);
                }
                catch (final InterruptedException e) {
                    final PrintWriter out = new PrintWriter(new StringWriter());
                    e.printStackTrace(out);
                    throw new BufferUnderflowException("Caused by InterruptedException: " + out.toString());
                }
                break;
            }
            return this.getBuffer().get();
        }
    }
    
    public Object get(final long timeout) {
        synchronized (this.lock) {
            final long expiration = System.currentTimeMillis() + timeout;
            long timeLeft = expiration - System.currentTimeMillis();
            while (timeLeft > 0L && this.collection.isEmpty()) {
                try {
                    this.lock.wait(timeLeft);
                    timeLeft = expiration - System.currentTimeMillis();
                    continue;
                }
                catch (final InterruptedException e) {
                    final PrintWriter out = new PrintWriter(new StringWriter());
                    e.printStackTrace(out);
                    throw new BufferUnderflowException("Caused by InterruptedException: " + out.toString());
                }
                break;
            }
            if (this.collection.isEmpty()) {
                throw new BufferUnderflowException("Timeout expired");
            }
            return this.getBuffer().get();
        }
    }
    
    public Object remove() {
        synchronized (this.lock) {
            while (this.collection.isEmpty()) {
                try {
                    if (this.timeout <= 0L) {
                        this.lock.wait();
                        continue;
                    }
                    return this.remove(this.timeout);
                }
                catch (final InterruptedException e) {
                    final PrintWriter out = new PrintWriter(new StringWriter());
                    e.printStackTrace(out);
                    throw new BufferUnderflowException("Caused by InterruptedException: " + out.toString());
                }
                break;
            }
            return this.getBuffer().remove();
        }
    }
    
    public Object remove(final long timeout) {
        synchronized (this.lock) {
            final long expiration = System.currentTimeMillis() + timeout;
            long timeLeft = expiration - System.currentTimeMillis();
            while (timeLeft > 0L && this.collection.isEmpty()) {
                try {
                    this.lock.wait(timeLeft);
                    timeLeft = expiration - System.currentTimeMillis();
                    continue;
                }
                catch (final InterruptedException e) {
                    final PrintWriter out = new PrintWriter(new StringWriter());
                    e.printStackTrace(out);
                    throw new BufferUnderflowException("Caused by InterruptedException: " + out.toString());
                }
                break;
            }
            if (this.collection.isEmpty()) {
                throw new BufferUnderflowException("Timeout expired");
            }
            return this.getBuffer().remove();
        }
    }
}
