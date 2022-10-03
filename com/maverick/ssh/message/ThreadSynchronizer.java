package com.maverick.ssh.message;

public class ThreadSynchronizer
{
    boolean d;
    Thread c;
    boolean b;
    
    public ThreadSynchronizer(final boolean d) {
        this.c = null;
        this.b = Boolean.valueOf(System.getProperty("maverick.verbose", "false"));
        this.d = d;
    }
    
    public synchronized boolean requestBlock(final MessageStore messageStore, final MessageObserver messageObserver, final MessageHolder messageHolder) throws InterruptedException {
        final boolean b = !this.d || this.isBlockOwner(Thread.currentThread());
        messageHolder.msg = messageStore.hasMessage(messageObserver);
        if (messageHolder.msg != null) {
            return false;
        }
        if (b) {
            this.d = true;
            this.c = Thread.currentThread();
        }
        else {
            this.wait(1000L);
        }
        return b;
    }
    
    public synchronized boolean isBlockOwner(final Thread thread) {
        return this.c != null && this.c.equals(thread);
    }
    
    public synchronized void releaseWaiting() {
        this.notifyAll();
    }
    
    public synchronized void releaseBlock() {
        this.d = false;
        this.c = null;
        this.notifyAll();
    }
}
