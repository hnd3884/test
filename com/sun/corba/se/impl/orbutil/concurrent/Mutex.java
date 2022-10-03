package com.sun.corba.se.impl.orbutil.concurrent;

public class Mutex implements Sync
{
    protected boolean inuse_;
    
    public Mutex() {
        this.inuse_ = false;
    }
    
    @Override
    public void acquire() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            try {
                while (this.inuse_) {
                    this.wait();
                }
                this.inuse_ = true;
            }
            catch (final InterruptedException ex) {
                this.notify();
                throw ex;
            }
        }
    }
    
    @Override
    public synchronized void release() {
        this.inuse_ = false;
        this.notify();
    }
    
    @Override
    public boolean attempt(final long n) throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        synchronized (this) {
            if (!this.inuse_) {
                return this.inuse_ = true;
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
                        return this.inuse_ = true;
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
