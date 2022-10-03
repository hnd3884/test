package javax.swing;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.Delayed;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.AppContext;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.DelayQueue;

class TimerQueue implements Runnable
{
    private static final Object sharedInstanceKey;
    private static final Object expiredTimersKey;
    private final DelayQueue<DelayedTimer> queue;
    private volatile boolean running;
    private final Lock runningLock;
    private static final Object classLock;
    private static final long NANO_ORIGIN;
    
    public TimerQueue() {
        this.queue = new DelayQueue<DelayedTimer>();
        this.runningLock = new ReentrantLock();
        this.startIfNeeded();
    }
    
    public static TimerQueue sharedInstance() {
        synchronized (TimerQueue.classLock) {
            TimerQueue timerQueue = (TimerQueue)SwingUtilities.appContextGet(TimerQueue.sharedInstanceKey);
            if (timerQueue == null) {
                timerQueue = new TimerQueue();
                SwingUtilities.appContextPut(TimerQueue.sharedInstanceKey, timerQueue);
            }
            return timerQueue;
        }
    }
    
    void startIfNeeded() {
        if (!this.running) {
            this.runningLock.lock();
            if (this.running) {
                return;
            }
            try {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
                    final /* synthetic */ ThreadGroup val$threadGroup = AppContext.getAppContext().getThreadGroup();
                    
                    @Override
                    public Object run() {
                        final Thread thread = new Thread(this.val$threadGroup, TimerQueue.this, "TimerQueue");
                        thread.setDaemon(true);
                        thread.setPriority(5);
                        thread.start();
                        return null;
                    }
                });
                this.running = true;
            }
            finally {
                this.runningLock.unlock();
            }
        }
    }
    
    void addTimer(final Timer timer, final long n) {
        timer.getLock().lock();
        try {
            if (!this.containsTimer(timer)) {
                this.addTimer(new DelayedTimer(timer, TimeUnit.MILLISECONDS.toNanos(n) + now()));
            }
        }
        finally {
            timer.getLock().unlock();
        }
    }
    
    private void addTimer(final DelayedTimer delayedTimer) {
        assert delayedTimer != null && !this.containsTimer(delayedTimer.getTimer());
        final Timer timer = delayedTimer.getTimer();
        timer.getLock().lock();
        try {
            timer.delayedTimer = delayedTimer;
            this.queue.add(delayedTimer);
        }
        finally {
            timer.getLock().unlock();
        }
    }
    
    void removeTimer(final Timer timer) {
        timer.getLock().lock();
        try {
            if (timer.delayedTimer != null) {
                this.queue.remove(timer.delayedTimer);
                timer.delayedTimer = null;
            }
        }
        finally {
            timer.getLock().unlock();
        }
    }
    
    boolean containsTimer(final Timer timer) {
        timer.getLock().lock();
        try {
            return timer.delayedTimer != null;
        }
        finally {
            timer.getLock().unlock();
        }
    }
    
    @Override
    public void run() {
        this.runningLock.lock();
        try {
            while (this.running) {
                try {
                    final DelayedTimer delayedTimer = this.queue.take();
                    final Timer timer = delayedTimer.getTimer();
                    timer.getLock().lock();
                    try {
                        final DelayedTimer delayedTimer2 = timer.delayedTimer;
                        if (delayedTimer2 == delayedTimer) {
                            timer.post();
                            timer.delayedTimer = null;
                            if (timer.isRepeats()) {
                                delayedTimer2.setTime(now() + TimeUnit.MILLISECONDS.toNanos(timer.getDelay()));
                                this.addTimer(delayedTimer2);
                            }
                        }
                        timer.getLock().newCondition().awaitNanos(1L);
                    }
                    catch (final SecurityException ex) {}
                    finally {
                        timer.getLock().unlock();
                    }
                }
                catch (final InterruptedException ex2) {
                    if (AppContext.getAppContext().isDisposed()) {
                        break;
                    }
                    continue;
                }
            }
        }
        catch (final ThreadDeath threadDeath) {
            final Iterator<DelayedTimer> iterator = this.queue.iterator();
            while (iterator.hasNext()) {
                iterator.next().getTimer().cancelEvent();
            }
            throw threadDeath;
        }
        finally {
            this.running = false;
            this.runningLock.unlock();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("TimerQueue (");
        int n = 1;
        for (final DelayedTimer delayedTimer : this.queue) {
            if (n == 0) {
                sb.append(", ");
            }
            sb.append(delayedTimer.getTimer().toString());
            n = 0;
        }
        sb.append(")");
        return sb.toString();
    }
    
    private static long now() {
        return System.nanoTime() - TimerQueue.NANO_ORIGIN;
    }
    
    static {
        sharedInstanceKey = new StringBuffer("TimerQueue.sharedInstanceKey");
        expiredTimersKey = new StringBuffer("TimerQueue.expiredTimersKey");
        classLock = new Object();
        NANO_ORIGIN = System.nanoTime();
    }
    
    static class DelayedTimer implements Delayed
    {
        private static final AtomicLong sequencer;
        private final long sequenceNumber;
        private volatile long time;
        private final Timer timer;
        
        DelayedTimer(final Timer timer, final long time) {
            this.timer = timer;
            this.time = time;
            this.sequenceNumber = DelayedTimer.sequencer.getAndIncrement();
        }
        
        @Override
        public final long getDelay(final TimeUnit timeUnit) {
            return timeUnit.convert(this.time - now(), TimeUnit.NANOSECONDS);
        }
        
        final void setTime(final long time) {
            this.time = time;
        }
        
        final Timer getTimer() {
            return this.timer;
        }
        
        @Override
        public int compareTo(final Delayed delayed) {
            if (delayed == this) {
                return 0;
            }
            if (!(delayed instanceof DelayedTimer)) {
                final long n = this.getDelay(TimeUnit.NANOSECONDS) - delayed.getDelay(TimeUnit.NANOSECONDS);
                return (n == 0L) ? 0 : ((n < 0L) ? -1 : 1);
            }
            final DelayedTimer delayedTimer = (DelayedTimer)delayed;
            final long n2 = this.time - delayedTimer.time;
            if (n2 < 0L) {
                return -1;
            }
            if (n2 > 0L) {
                return 1;
            }
            if (this.sequenceNumber < delayedTimer.sequenceNumber) {
                return -1;
            }
            return 1;
        }
        
        static {
            sequencer = new AtomicLong(0L);
        }
    }
}
