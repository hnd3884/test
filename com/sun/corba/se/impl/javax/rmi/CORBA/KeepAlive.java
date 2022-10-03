package com.sun.corba.se.impl.javax.rmi.CORBA;

class KeepAlive extends Thread
{
    boolean quit;
    
    public KeepAlive() {
        this.setDaemon(this.quit = false);
    }
    
    @Override
    public synchronized void run() {
        while (!this.quit) {
            try {
                this.wait();
            }
            catch (final InterruptedException ex) {}
        }
    }
    
    public synchronized void quit() {
        this.quit = true;
        this.notifyAll();
    }
}
