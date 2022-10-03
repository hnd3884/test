package com.unboundid.ldap.sdk;

import java.util.Iterator;
import com.unboundid.util.Debug;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Timer;
import com.unboundid.util.ObjectPair;
import java.util.concurrent.atomic.AtomicReference;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import java.util.TimerTask;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class WriteTimeoutHandler extends TimerTask
{
    private static final AtomicReference<ObjectPair<Timer, AtomicLong>> TIMER_REFERENCE;
    private static final long TIMER_INTERVAL_MILLIS = 100L;
    private final AtomicBoolean destroyed;
    private final AtomicLong connectionsUsingTimer;
    private final AtomicLong counter;
    private final ConcurrentHashMap<Long, Long> writeTimeouts;
    private final LDAPConnection connection;
    private final Timer timer;
    
    WriteTimeoutHandler(final LDAPConnection connection) {
        this.connection = connection;
        this.destroyed = new AtomicBoolean(false);
        this.counter = new AtomicLong(0L);
        this.writeTimeouts = new ConcurrentHashMap<Long, Long>(10);
        synchronized (WriteTimeoutHandler.TIMER_REFERENCE) {
            final ObjectPair<Timer, AtomicLong> timerPair = WriteTimeoutHandler.TIMER_REFERENCE.get();
            if (timerPair == null) {
                this.timer = new Timer("Write Timeout Handler Timer", true);
                this.connectionsUsingTimer = new AtomicLong(1L);
                WriteTimeoutHandler.TIMER_REFERENCE.set(new ObjectPair<Timer, AtomicLong>(this.timer, this.connectionsUsingTimer));
            }
            else {
                this.timer = timerPair.getFirst();
                (this.connectionsUsingTimer = timerPair.getSecond()).incrementAndGet();
            }
            this.timer.schedule(this, 100L, 100L);
        }
    }
    
    @Override
    public void run() {
        final long currentTime = System.currentTimeMillis();
        final Iterator<Map.Entry<Long, Long>> iterator = this.writeTimeouts.entrySet().iterator();
        while (iterator.hasNext()) {
            final long closeTime = (long)iterator.next().getValue();
            if (currentTime > closeTime) {
                try {
                    this.connection.getConnectionInternals(true).getSocket().close();
                }
                catch (final Exception e) {
                    Debug.debugException(e);
                }
            }
        }
    }
    
    @Override
    public boolean cancel() {
        final boolean result = super.cancel();
        this.timer.purge();
        return result;
    }
    
    void destroy() {
        this.cancel();
        if (this.destroyed.getAndSet(true)) {
            return;
        }
        synchronized (WriteTimeoutHandler.TIMER_REFERENCE) {
            final long remainingConnectionsUsingTimer = this.connectionsUsingTimer.decrementAndGet();
            if (remainingConnectionsUsingTimer <= 0L) {
                WriteTimeoutHandler.TIMER_REFERENCE.set(null);
                this.timer.cancel();
            }
        }
    }
    
    long beginWrite(final long timeoutMillis) {
        final long id = this.counter.getAndIncrement();
        final long writeExpirationTime = System.currentTimeMillis() + timeoutMillis;
        this.writeTimeouts.put(id, writeExpirationTime);
        return id;
    }
    
    void writeCompleted(final long writeID) {
        this.writeTimeouts.remove(writeID);
    }
    
    static {
        TIMER_REFERENCE = new AtomicReference<ObjectPair<Timer, AtomicLong>>();
    }
}
