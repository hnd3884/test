package com.sun.corba.se.impl.orbutil.concurrent;

import org.omg.CORBA.INTERNAL;

public class DebugMutex implements Sync
{
    protected boolean inuse_;
    protected Thread holder_;
    
    public DebugMutex() {
        this.inuse_ = false;
        this.holder_ = null;
    }
    
    @Override
    public void acquire() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            if (this.holder_ == Thread.currentThread()) {
                throw new INTERNAL("Attempt to acquire Mutex by thread holding the Mutex");
            }
            try {
                while (this.inuse_) {
                    this.wait();
                }
                this.inuse_ = true;
                this.holder_ = Thread.currentThread();
            }
            catch (final InterruptedException ex) {
                this.notify();
                throw ex;
            }
        }
    }
    
    @Override
    public synchronized void release() {
        if (Thread.currentThread() != this.holder_) {
            throw new INTERNAL("Attempt to release Mutex by thread not holding the Mutex");
        }
        this.holder_ = null;
        this.inuse_ = false;
        this.notify();
    }
    
    @Override
    public boolean attempt(final long n) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            final Thread currentThread = Thread.currentThread();
            if (!this.inuse_) {
                this.inuse_ = true;
                this.holder_ = currentThread;
                return true;
            }
            if (n <= 0L) {
                return false;
            }
            long n2 = n;
            final long currentTimeMillis = System.currentTimeMillis();
            try {
                do {
                    this.wait(n2);
                    if (!this.inuse_) {
                        this.inuse_ = true;
                        this.holder_ = currentThread;
                        return true;
                    }
                    n2 = n - (System.currentTimeMillis() - currentTimeMillis);
                } while (n2 > 0L);
                return false;
            }
            catch (final InterruptedException ex) {
                this.notify();
                throw ex;
            }
        }
    }
}
