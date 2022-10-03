package com.sun.corba.se.impl.orbutil.concurrent;

import com.sun.corba.se.impl.orbutil.ORBUtility;

public class CondVar
{
    protected boolean debug_;
    protected final Sync mutex_;
    protected final ReentrantMutex remutex_;
    
    private int releaseMutex() {
        int releaseAll = 1;
        if (this.remutex_ != null) {
            releaseAll = this.remutex_.releaseAll();
        }
        else {
            this.mutex_.release();
        }
        return releaseAll;
    }
    
    private void acquireMutex(final int n) throws InterruptedException {
        if (this.remutex_ != null) {
            this.remutex_.acquireAll(n);
        }
        else {
            this.mutex_.acquire();
        }
    }
    
    public CondVar(final Sync mutex_, final boolean debug_) {
        this.debug_ = debug_;
        this.mutex_ = mutex_;
        if (mutex_ instanceof ReentrantMutex) {
            this.remutex_ = (ReentrantMutex)mutex_;
        }
        else {
            this.remutex_ = null;
        }
    }
    
    public CondVar(final Sync sync) {
        this(sync, false);
    }
    
    public void await() throws InterruptedException {
        int releaseMutex = 0;
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        try {
            if (this.debug_) {
                ORBUtility.dprintTrace(this, "await enter");
            }
            synchronized (this) {
                releaseMutex = this.releaseMutex();
                try {
                    this.wait();
                }
                catch (final InterruptedException ex) {
                    this.notify();
                    throw ex;
                }
            }
        }
        finally {
            boolean b = false;
            while (true) {
                try {
                    this.acquireMutex(releaseMutex);
                }
                catch (final InterruptedException ex2) {
                    b = true;
                    continue;
                }
                break;
            }
            if (b) {
                Thread.currentThread().interrupt();
            }
            if (this.debug_) {
                ORBUtility.dprintTrace(this, "await exit");
            }
        }
    }
    
    public boolean timedwait(final long n) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        boolean b = false;
        int releaseMutex = 0;
        try {
            if (this.debug_) {
                ORBUtility.dprintTrace(this, "timedwait enter");
            }
            synchronized (this) {
                releaseMutex = this.releaseMutex();
                try {
                    if (n > 0L) {
                        final long currentTimeMillis = System.currentTimeMillis();
                        this.wait(n);
                        b = (System.currentTimeMillis() - currentTimeMillis <= n);
                    }
                }
                catch (final InterruptedException ex) {
                    this.notify();
                    throw ex;
                }
            }
        }
        finally {
            boolean b2 = false;
            while (true) {
                try {
                    this.acquireMutex(releaseMutex);
                }
                catch (final InterruptedException ex2) {
                    b2 = true;
                    continue;
                }
                break;
            }
            if (b2) {
                Thread.currentThread().interrupt();
            }
            if (this.debug_) {
                ORBUtility.dprintTrace(this, "timedwait exit");
            }
        }
        return b;
    }
    
    public synchronized void signal() {
        this.notify();
    }
    
    public synchronized void broadcast() {
        this.notifyAll();
    }
}
