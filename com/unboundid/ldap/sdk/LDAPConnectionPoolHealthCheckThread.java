package com.unboundid.ldap.sdk;

import java.util.concurrent.TimeUnit;
import com.unboundid.util.Debug;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class LDAPConnectionPoolHealthCheckThread extends Thread
{
    private final AtomicBoolean stopRequested;
    private final AbstractConnectionPool pool;
    private final LinkedBlockingQueue<Object> queue;
    private volatile Thread thread;
    
    LDAPConnectionPoolHealthCheckThread(final AbstractConnectionPool pool) {
        this.setName("Health Check Thread for " + pool.toString());
        this.setDaemon(true);
        this.pool = pool;
        this.stopRequested = new AtomicBoolean(false);
        this.queue = new LinkedBlockingQueue<Object>(1);
        this.thread = null;
    }
    
    @Override
    public void run() {
        this.thread = Thread.currentThread();
        long lastCheckTime = System.currentTimeMillis();
        while (!this.stopRequested.get()) {
            final long timeSinceLastCheck = System.currentTimeMillis() - lastCheckTime;
            if (timeSinceLastCheck >= this.pool.getHealthCheckIntervalMillis()) {
                try {
                    this.pool.doHealthCheck();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
                try {
                    this.pool.getHealthCheck().performPoolMaintenance(this.pool);
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
                lastCheckTime = System.currentTimeMillis();
            }
            else {
                final long sleepTime = Math.min(this.pool.getHealthCheckIntervalMillis() - timeSinceLastCheck, 30000L);
                try {
                    this.queue.poll(sleepTime, TimeUnit.MILLISECONDS);
                }
                catch (final Exception e2) {
                    Debug.debugException(e2);
                }
            }
        }
        this.thread = null;
    }
    
    void stopRunning(final boolean wait) {
        this.stopRequested.set(true);
        this.wakeUp();
        if (wait) {
            final Thread t = this.thread;
            if (t != null) {
                try {
                    t.join();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }
    
    void wakeUp() {
        this.queue.offer(new Object());
    }
}
