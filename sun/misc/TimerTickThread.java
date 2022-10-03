package sun.misc;

class TimerTickThread extends Thread
{
    static final int MAX_POOL_SIZE = 3;
    static int curPoolSize;
    static TimerTickThread pool;
    TimerTickThread next;
    Timer timer;
    long lastSleepUntil;
    
    TimerTickThread() {
        this.next = null;
    }
    
    protected static synchronized TimerTickThread call(final Timer timer, final long n) {
        TimerTickThread pool = TimerTickThread.pool;
        if (pool == null) {
            pool = new TimerTickThread();
            pool.timer = timer;
            pool.lastSleepUntil = n;
            pool.start();
        }
        else {
            TimerTickThread.pool = TimerTickThread.pool.next;
            pool.timer = timer;
            pool.lastSleepUntil = n;
            synchronized (pool) {
                pool.notify();
            }
        }
        return pool;
    }
    
    private boolean returnToPool() {
        synchronized (this.getClass()) {
            if (TimerTickThread.curPoolSize >= 3) {
                return false;
            }
            this.next = TimerTickThread.pool;
            TimerTickThread.pool = this;
            ++TimerTickThread.curPoolSize;
            this.timer = null;
        }
        while (this.timer == null) {
            synchronized (this) {
                try {
                    this.wait();
                }
                catch (final InterruptedException ex) {}
            }
        }
        synchronized (this.getClass()) {
            --TimerTickThread.curPoolSize;
        }
        return true;
    }
    
    @Override
    public void run() {
        do {
            this.timer.owner.tick(this.timer);
            synchronized (TimerThread.timerThread) {
                synchronized (this.timer) {
                    if (this.lastSleepUntil != this.timer.sleepUntil) {
                        continue;
                    }
                    TimerThread.requeue(this.timer);
                }
            }
        } while (this.returnToPool());
    }
    
    static {
        TimerTickThread.curPoolSize = 0;
        TimerTickThread.pool = null;
    }
}
