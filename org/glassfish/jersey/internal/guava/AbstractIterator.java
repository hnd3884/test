package org.glassfish.jersey.internal.guava;

import java.util.NoSuchElementException;

public abstract class AbstractIterator<T> extends UnmodifiableIterator<T>
{
    private State state;
    private T next;
    
    AbstractIterator() {
        this.state = State.NOT_READY;
    }
    
    protected abstract T computeNext();
    
    final T endOfData() {
        this.state = State.DONE;
        return null;
    }
    
    @Override
    public final boolean hasNext() {
        Preconditions.checkState(this.state != State.FAILED);
        switch (this.state) {
            case DONE: {
                return false;
            }
            case READY: {
                return true;
            }
            default: {
                return this.tryToComputeNext();
            }
        }
    }
    
    private boolean tryToComputeNext() {
        this.state = State.FAILED;
        this.next = this.computeNext();
        if (this.state != State.DONE) {
            this.state = State.READY;
            return true;
        }
        return false;
    }
    
    @Override
    public final T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.state = State.NOT_READY;
        final T result = this.next;
        this.next = null;
        return result;
    }
    
    private enum State
    {
        READY, 
        NOT_READY, 
        DONE, 
        FAILED;
    }
}
