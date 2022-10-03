package java.util;

class TimerThread extends Thread
{
    boolean newTasksMayBeScheduled;
    private TaskQueue queue;
    
    TimerThread(final TaskQueue queue) {
        this.newTasksMayBeScheduled = true;
        this.queue = queue;
    }
    
    @Override
    public void run() {
        try {
            this.mainLoop();
        }
        finally {
            synchronized (this.queue) {
                this.newTasksMayBeScheduled = false;
                this.queue.clear();
            }
        }
    }
    
    private void mainLoop() {
        while (true) {
            try {
                while (true) {
                    final TimerTask min;
                    final boolean b;
                    synchronized (this.queue) {
                        while (this.queue.isEmpty() && this.newTasksMayBeScheduled) {
                            this.queue.wait();
                        }
                        if (this.queue.isEmpty()) {
                            break;
                        }
                        min = this.queue.getMin();
                        final long currentTimeMillis;
                        final long nextExecutionTime;
                        synchronized (min.lock) {
                            if (min.state == 3) {
                                this.queue.removeMin();
                                continue;
                            }
                            currentTimeMillis = System.currentTimeMillis();
                            nextExecutionTime = min.nextExecutionTime;
                            if (b = (nextExecutionTime <= currentTimeMillis)) {
                                if (min.period == 0L) {
                                    this.queue.removeMin();
                                    min.state = 2;
                                }
                                else {
                                    this.queue.rescheduleMin((min.period < 0L) ? (currentTimeMillis - min.period) : (nextExecutionTime + min.period));
                                }
                            }
                        }
                        if (!b) {
                            this.queue.wait(nextExecutionTime - currentTimeMillis);
                        }
                    }
                    if (b) {
                        min.run();
                    }
                }
            }
            catch (final InterruptedException min) {
                continue;
            }
            break;
        }
    }
}
