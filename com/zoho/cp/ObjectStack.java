package com.zoho.cp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingDeque;
import java.util.logging.Logger;

public class ObjectStack<E>
{
    private static final Logger OBJECT_STACK_LOGGER;
    private BlockingDeque<E> objStack;
    private Semaphore semaphore;
    
    public ObjectStack(final int initialCapacity, final boolean fairness) {
        this.objStack = new LinkedBlockingDeque<E>(700);
        this.semaphore = new Semaphore(initialCapacity, fairness);
    }
    
    public E acquirePermitAndPollLastElement(final int blockingTimeout) throws InterruptedException, NoPermitsAvailableException {
        final int initialPermits = this.semaphore.availablePermits();
        if (!this.semaphore.tryAcquire(blockingTimeout, TimeUnit.SECONDS)) {
            throw new NoPermitsAvailableException("No permit available within configured blocking timeout");
        }
        final E e = this.objStack.pollLast();
        ObjectStack.OBJECT_STACK_LOGGER.log(Level.FINEST, "acquirePermitAndPollLastElement :: acquire case :: Initial :: {0}, Later :: {1}, {2}", new Object[] { initialPermits, this.semaphore.availablePermits(), e });
        return e;
    }
    
    public E acquirePermitAndPollFirstElement() throws NoPermitsAvailableException {
        final int initialPermits = this.semaphore.availablePermits();
        if (!this.semaphore.tryAcquire()) {
            throw new NoPermitsAvailableException("No permit available");
        }
        final E e = this.objStack.pollFirst();
        ObjectStack.OBJECT_STACK_LOGGER.log(Level.FINEST, "acquirePermitAndPollFirstElement :: acquire case :: Initial :: {0}, Later :: {1}, {2}", new Object[] { initialPermits, this.semaphore.availablePermits(), e });
        return e;
    }
    
    public void addElementAtLastAndReleasePermit(final E element) {
        this.objStack.addLast(element);
        final int initialPermits = this.semaphore.availablePermits();
        this.releasePermit();
        ObjectStack.OBJECT_STACK_LOGGER.log(Level.FINEST, "addElementAtLastAndReleasePermit :: release case :: Initial :: {0}, Later :: {1}, {2}", new Object[] { initialPermits, this.semaphore.availablePermits(), element });
    }
    
    public void addElementAtFirstAndReleasePermit(final E element) {
        this.objStack.addFirst(element);
        final int initialPermits = this.semaphore.availablePermits();
        this.releasePermit();
        ObjectStack.OBJECT_STACK_LOGGER.log(Level.FINEST, "addElementAtFirstAndReleasePermit :: release case :: Initial :: {0}, Later :: {1}, {2}", new Object[] { initialPermits, this.semaphore.availablePermits(), element });
    }
    
    public void releasePermit() {
        final int initialPermits = this.semaphore.availablePermits();
        this.semaphore.release();
        ObjectStack.OBJECT_STACK_LOGGER.log(Level.FINEST, "releasePermit :: release case :: Initial :: {0}, Later :: {1}", new Object[] { initialPermits, this.semaphore.availablePermits() });
    }
    
    public void increaseCapacity(final int capacity) {
        this.semaphore.release(capacity);
    }
    
    public List<E> removeAndReturnAllElements() {
        final List<E> elements = new LinkedList<E>();
        while (true) {
            final E element = this.objStack.pollFirst();
            if (element == null) {
                break;
            }
            elements.add(element);
        }
        return elements;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("Permits :: ").append(this.semaphore.availablePermits()).append(", ");
        sb.append("Queue :: ").append("[");
        for (final E e : this.objStack) {
            sb.append(e).append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
    
    public int availablePermits() {
        return this.semaphore.availablePermits();
    }
    
    static {
        OBJECT_STACK_LOGGER = Logger.getLogger("ObjectStackLogger");
    }
}
