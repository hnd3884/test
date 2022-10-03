package sun.misc;

public final class ConditionLock extends Lock
{
    private int state;
    
    public ConditionLock() {
        this.state = 0;
    }
    
    public ConditionLock(final int state) {
        this.state = 0;
        this.state = state;
    }
    
    public synchronized void lockWhen(final int n) throws InterruptedException {
        while (this.state != n) {
            this.wait();
        }
        this.lock();
    }
    
    public synchronized void unlockWith(final int state) {
        this.state = state;
        this.unlock();
    }
}
