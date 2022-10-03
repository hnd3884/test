package com.jeans.trayicon;

public class TrayIconKeeper extends Thread
{
    public synchronized void doNotify() {
        this.notifyAll();
    }
    
    public synchronized void doWait() throws InterruptedException {
        this.wait();
    }
    
    public void run() {
        try {
            this.doWait();
        }
        catch (final InterruptedException ex) {}
    }
}
