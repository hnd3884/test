package com.sun.corba.se.impl.orbutil.concurrent;

import org.omg.CORBA.INTERNAL;
import com.sun.corba.se.impl.orbutil.ORBUtility;

public class ReentrantMutex implements Sync
{
    protected Thread holder_;
    protected int counter_;
    protected boolean debug;
    
    public ReentrantMutex() {
        this(false);
    }
    
    public ReentrantMutex(final boolean debug) {
        this.holder_ = null;
        this.counter_ = 0;
        this.debug = false;
        this.debug = debug;
    }
    
    @Override
    public void acquire() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            try {
                if (this.debug) {
                    ORBUtility.dprintTrace(this, "acquire enter: holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
                }
                final Thread currentThread = Thread.currentThread();
                if (this.holder_ != currentThread) {
                    try {
                        while (this.counter_ > 0) {
                            this.wait();
                        }
                        if (this.counter_ != 0) {
                            throw new INTERNAL("counter not 0 when first acquiring mutex");
                        }
                        this.holder_ = currentThread;
                    }
                    catch (final InterruptedException ex) {
                        this.notify();
                        throw ex;
                    }
                }
                ++this.counter_;
            }
            finally {
                if (this.debug) {
                    ORBUtility.dprintTrace(this, "acquire exit: holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
                }
            }
        }
    }
    
    void acquireAll(final int counter_) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            try {
                if (this.debug) {
                    ORBUtility.dprintTrace(this, "acquireAll enter: count=" + counter_ + " holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
                }
                final Thread currentThread = Thread.currentThread();
                if (this.holder_ == currentThread) {
                    throw new INTERNAL("Cannot acquireAll while holding the mutex");
                }
                try {
                    while (this.counter_ > 0) {
                        this.wait();
                    }
                    if (this.counter_ != 0) {
                        throw new INTERNAL("counter not 0 when first acquiring mutex");
                    }
                    this.holder_ = currentThread;
                }
                catch (final InterruptedException ex) {
                    this.notify();
                    throw ex;
                }
                this.counter_ = counter_;
            }
            finally {
                if (this.debug) {
                    ORBUtility.dprintTrace(this, "acquireAll exit: count=" + counter_ + " holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
                }
            }
        }
    }
    
    @Override
    public synchronized void release() {
        try {
            if (this.debug) {
                ORBUtility.dprintTrace(this, "release enter:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
            }
            if (Thread.currentThread() != this.holder_) {
                throw new INTERNAL("Attempt to release Mutex by thread not holding the Mutex");
            }
            --this.counter_;
            if (this.counter_ == 0) {
                this.holder_ = null;
                this.notify();
            }
        }
        finally {
            if (this.debug) {
                ORBUtility.dprintTrace(this, "release exit:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
            }
        }
    }
    
    synchronized int releaseAll() {
        try {
            if (this.debug) {
                ORBUtility.dprintTrace(this, "releaseAll enter:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
            }
            if (Thread.currentThread() != this.holder_) {
                throw new INTERNAL("Attempt to releaseAll Mutex by thread not holding the Mutex");
            }
            final int counter_ = this.counter_;
            this.counter_ = 0;
            this.holder_ = null;
            this.notify();
            return counter_;
        }
        finally {
            if (this.debug) {
                ORBUtility.dprintTrace(this, "releaseAll exit:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
            }
        }
    }
    
    @Override
    public boolean attempt(final long n) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            try {
                if (this.debug) {
                    ORBUtility.dprintTrace(this, "attempt enter: msecs=" + n + " holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
                }
                final Thread currentThread = Thread.currentThread();
                if (this.counter_ == 0) {
                    this.holder_ = currentThread;
                    this.counter_ = 1;
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
                        if (this.counter_ == 0) {
                            this.holder_ = currentThread;
                            this.counter_ = 1;
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
            finally {
                if (this.debug) {
                    ORBUtility.dprintTrace(this, "attempt exit:  holder_=" + ORBUtility.getThreadName(this.holder_) + " counter_=" + this.counter_);
                }
            }
        }
    }
}
