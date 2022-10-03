package org.apache.xmlbeans.impl.common;

public class Mutex
{
    private Thread owner;
    private int lock_count;
    
    public Mutex() {
        this.owner = null;
        this.lock_count = 0;
    }
    
    public synchronized void acquire() throws InterruptedException {
        while (!this.tryToAcquire()) {
            this.wait();
        }
    }
    
    public synchronized boolean tryToAcquire() {
        if (this.owner == null) {
            this.owner = Thread.currentThread();
            this.lock_count = 1;
            return true;
        }
        if (this.owner == Thread.currentThread()) {
            ++this.lock_count;
            return true;
        }
        return false;
    }
    
    public synchronized void release() {
        if (this.owner != Thread.currentThread()) {
            throw new IllegalStateException("Thread calling release() doesn't own mutex");
        }
        if (--this.lock_count <= 0) {
            this.owner = null;
            this.notify();
        }
    }
}
