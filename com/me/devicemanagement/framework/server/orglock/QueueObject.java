package com.me.devicemanagement.framework.server.orglock;

class QueueObject
{
    private final String key;
    private boolean isNotified;
    
    public QueueObject(final String key) {
        this.isNotified = false;
        this.key = key;
    }
    
    public synchronized void doWait() throws InterruptedException {
        while (!this.isNotified) {
            this.wait();
        }
        this.isNotified = false;
    }
    
    public synchronized void doNotify() {
        this.isNotified = true;
        this.notify();
    }
}
