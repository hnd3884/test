package com.sun.corba.se.impl.activation;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.HashMap;

public class ProcessMonitorThread extends Thread
{
    private HashMap serverTable;
    private int sleepTime;
    private static ProcessMonitorThread instance;
    
    private ProcessMonitorThread(final HashMap serverTable, final int sleepTime) {
        this.serverTable = serverTable;
        this.sleepTime = sleepTime;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(this.sleepTime);
            }
            catch (final InterruptedException ex) {
                break;
            }
            final Iterator iterator;
            synchronized (this.serverTable) {
                iterator = this.serverTable.values().iterator();
            }
            try {
                this.checkServerHealth(iterator);
            }
            catch (final ConcurrentModificationException ex2) {
                break;
            }
        }
    }
    
    private void checkServerHealth(final Iterator iterator) {
        if (iterator == null) {
            return;
        }
        while (iterator.hasNext()) {
            iterator.next().checkProcessHealth();
        }
    }
    
    static void start(final HashMap hashMap) {
        int int1 = 1000;
        final String property = System.getProperties().getProperty("com.sun.CORBA.activation.ServerPollingTime");
        if (property != null) {
            try {
                int1 = Integer.parseInt(property);
            }
            catch (final Exception ex) {}
        }
        (ProcessMonitorThread.instance = new ProcessMonitorThread(hashMap, int1)).setDaemon(true);
        ProcessMonitorThread.instance.start();
    }
    
    static void interruptThread() {
        ProcessMonitorThread.instance.interrupt();
    }
    
    static {
        ProcessMonitorThread.instance = null;
    }
}
