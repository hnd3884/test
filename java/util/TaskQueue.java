package java.util;

class TaskQueue
{
    private TimerTask[] queue;
    private int size;
    
    TaskQueue() {
        this.queue = new TimerTask[128];
        this.size = 0;
    }
    
    int size() {
        return this.size;
    }
    
    void add(final TimerTask timerTask) {
        if (this.size + 1 == this.queue.length) {
            this.queue = Arrays.copyOf(this.queue, 2 * this.queue.length);
        }
        this.queue[++this.size] = timerTask;
        this.fixUp(this.size);
    }
    
    TimerTask getMin() {
        return this.queue[1];
    }
    
    TimerTask get(final int n) {
        return this.queue[n];
    }
    
    void removeMin() {
        this.queue[1] = this.queue[this.size];
        this.queue[this.size--] = null;
        this.fixDown(1);
    }
    
    void quickRemove(final int n) {
        assert n <= this.size;
        this.queue[n] = this.queue[this.size];
        this.queue[this.size--] = null;
    }
    
    void rescheduleMin(final long nextExecutionTime) {
        this.queue[1].nextExecutionTime = nextExecutionTime;
        this.fixDown(1);
    }
    
    boolean isEmpty() {
        return this.size == 0;
    }
    
    void clear() {
        for (int i = 1; i <= this.size; ++i) {
            this.queue[i] = null;
        }
        this.size = 0;
    }
    
    private void fixUp(int i) {
        while (i > 1) {
            final int n = i >> 1;
            if (this.queue[n].nextExecutionTime <= this.queue[i].nextExecutionTime) {
                break;
            }
            final TimerTask timerTask = this.queue[n];
            this.queue[n] = this.queue[i];
            this.queue[i] = timerTask;
            i = n;
        }
    }
    
    private void fixDown(int n) {
        int n2;
        while ((n2 = n << 1) <= this.size && n2 > 0) {
            if (n2 < this.size && this.queue[n2].nextExecutionTime > this.queue[n2 + 1].nextExecutionTime) {
                ++n2;
            }
            if (this.queue[n].nextExecutionTime <= this.queue[n2].nextExecutionTime) {
                break;
            }
            final TimerTask timerTask = this.queue[n2];
            this.queue[n2] = this.queue[n];
            this.queue[n] = timerTask;
            n = n2;
        }
    }
    
    void heapify() {
        for (int i = this.size / 2; i >= 1; --i) {
            this.fixDown(i);
        }
    }
}
