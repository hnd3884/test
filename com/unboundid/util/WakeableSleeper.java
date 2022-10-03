package com.unboundid.util;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.Serializable;

@ThreadSafety(level = ThreadSafetyLevel.MOSTLY_NOT_THREADSAFE)
public final class WakeableSleeper implements Serializable
{
    private static final long serialVersionUID = 755656862953269760L;
    private final AtomicBoolean sleeping;
    private final AtomicBoolean shutDown;
    private final AtomicLong wakeupCount;
    
    public WakeableSleeper() {
        this.sleeping = new AtomicBoolean(false);
        this.shutDown = new AtomicBoolean(false);
        this.wakeupCount = new AtomicLong(0L);
    }
    
    public boolean isShutDown() {
        return this.shutDown.get();
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
    public boolean sleep(final long time) {
        synchronized (this.wakeupCount) {
            if (this.isShutDown()) {
                return false;
            }
            Validator.ensureTrue(this.sleeping.compareAndSet(false, true), "WakeableSleeper.sleep() must not be invoked concurrently by multiple threads against the same instance.");
            try {
                final long beforeCount = this.wakeupCount.get();
                this.wakeupCount.wait(time);
                final long afterCount = this.wakeupCount.get();
                return beforeCount == afterCount;
            }
            catch (final InterruptedException ie) {
                Debug.debugException(ie);
                return false;
            }
            finally {
                this.sleeping.set(false);
            }
        }
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
    public void shutDown() {
        this.shutDown.set(true);
        this.wakeup();
    }
    
    @ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
    public void wakeup() {
        synchronized (this.wakeupCount) {
            this.wakeupCount.incrementAndGet();
            this.wakeupCount.notifyAll();
        }
    }
}
