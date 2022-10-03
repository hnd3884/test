package com.zoho.clustering.agent.remotemonitor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Monitor
{
    private static Logger logger;
    public final String slaveId;
    public final int keepAliveInMillis;
    private Handler handler;
    private Thread thread;
    private long lastAccessTimeInMillis;
    
    public Monitor(final String slaveId, final int keepAliveInMins, final Handler handler) {
        this.slaveId = slaveId;
        this.keepAliveInMillis = keepAliveInMins * 60 * 1000;
        this.handler = handler;
    }
    
    public void start() {
        (this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(Monitor.this.keepAliveInMillis);
                        if (System.currentTimeMillis() - Monitor.this.lastAccessTimeInMillis > Monitor.this.keepAliveInMillis && Monitor.this.handler.handleTimeout(Monitor.this.slaveId)) {
                            MonitorPool.getInst().removeMonitor(Monitor.this.slaveId);
                        }
                    }
                }
                catch (final InterruptedException exp) {}
            }
        }, "RemoteMonitor-" + this.slaveId)).start();
        Monitor.logger.log(Level.INFO, "Monitor[{0}]: started", this.slaveId);
    }
    
    public void updateLastAccessTime() {
        this.lastAccessTimeInMillis = System.currentTimeMillis();
    }
    
    public void stop() {
        if (this.thread != null && this.thread.isAlive()) {
            this.thread.interrupt();
            this.thread = null;
            Monitor.logger.log(Level.INFO, "Monitor[{0}]: stopped", this.slaveId);
        }
        else {
            Monitor.logger.log(Level.WARNING, "Monitor[{0}]: already stopped", this.slaveId);
        }
    }
    
    static {
        Monitor.logger = Logger.getLogger(Monitor.class.getName());
    }
    
    public interface Handler
    {
        boolean handleTimeout(final String p0);
    }
}
