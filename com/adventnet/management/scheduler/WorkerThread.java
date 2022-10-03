package com.adventnet.management.scheduler;

class WorkerThread extends Thread
{
    Scheduler scheduler;
    
    public WorkerThread(final Scheduler scheduler, final String s) {
        super(s);
        this.scheduler = null;
        this.scheduler = scheduler;
    }
    
    synchronized void wakeUp() {
        this.notifyAll();
    }
    
    synchronized void waitUntilAsked() {
        while ((this.scheduler.ready_tasks.size() == 0 || Scheduler.SUSPEND_ALL) && !Scheduler.STOP_ALL && !this.scheduler.STOP_THIS) {
            try {
                this.wait(3600000L);
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    public void run() {
        while (!Scheduler.STOP_ALL && !this.scheduler.STOP_THIS) {
            try {
                this.waitUntilAsked();
                final Runnable nextTask = this.scheduler.getNextTask();
                if (nextTask == null) {
                    continue;
                }
                final Scheduler scheduler = this.scheduler;
                ++scheduler.activeThreads;
                final Scheduler scheduler2 = this.scheduler;
                --scheduler2.idleThreads;
                nextTask.run();
                final Scheduler scheduler3 = this.scheduler;
                --scheduler3.activeThreads;
                final Scheduler scheduler4 = this.scheduler;
                ++scheduler4.idleThreads;
            }
            catch (final Throwable t) {
                System.err.println("Exception running task: " + t);
                t.printStackTrace();
            }
        }
        final Scheduler scheduler5 = this.scheduler;
        ++scheduler5.NUM_THREADS_STOPPED;
        --Scheduler.TOTAL_THREADS;
    }
}
