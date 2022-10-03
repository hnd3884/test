package sun.misc;

public class Timer
{
    public Timeable owner;
    long interval;
    long sleepUntil;
    long remainingTime;
    boolean regular;
    boolean stopped;
    Timer next;
    static TimerThread timerThread;
    
    public Timer(final Timeable owner, final long n) {
        this.owner = owner;
        this.interval = n;
        this.remainingTime = n;
        this.regular = true;
        this.sleepUntil = System.currentTimeMillis();
        this.stopped = true;
        synchronized (this.getClass()) {
            if (Timer.timerThread == null) {
                Timer.timerThread = new TimerThread();
            }
        }
    }
    
    public synchronized boolean isStopped() {
        return this.stopped;
    }
    
    public void stop() {
        final long currentTimeMillis = System.currentTimeMillis();
        synchronized (Timer.timerThread) {
            synchronized (this) {
                if (!this.stopped) {
                    TimerThread.dequeue(this);
                    this.remainingTime = Math.max(0L, this.sleepUntil - currentTimeMillis);
                    this.sleepUntil = currentTimeMillis;
                    this.stopped = true;
                }
            }
        }
    }
    
    public void cont() {
        synchronized (Timer.timerThread) {
            synchronized (this) {
                if (this.stopped) {
                    this.sleepUntil = Math.max(this.sleepUntil + 1L, System.currentTimeMillis() + this.remainingTime);
                    TimerThread.enqueue(this);
                    this.stopped = false;
                }
            }
        }
    }
    
    public void reset() {
        synchronized (Timer.timerThread) {
            synchronized (this) {
                this.setRemainingTime(this.interval);
            }
        }
    }
    
    public synchronized long getStopTime() {
        return this.sleepUntil;
    }
    
    public synchronized long getInterval() {
        return this.interval;
    }
    
    public synchronized void setInterval(final long interval) {
        this.interval = interval;
    }
    
    public synchronized long getRemainingTime() {
        return this.remainingTime;
    }
    
    public void setRemainingTime(final long n) {
        synchronized (Timer.timerThread) {
            synchronized (this) {
                if (this.stopped) {
                    this.remainingTime = n;
                }
                else {
                    this.stop();
                    this.remainingTime = n;
                    this.cont();
                }
            }
        }
    }
    
    public synchronized void setRegular(final boolean regular) {
        this.regular = regular;
    }
    
    protected Thread getTimerThread() {
        return TimerThread.timerThread;
    }
    
    static {
        Timer.timerThread = null;
    }
}
