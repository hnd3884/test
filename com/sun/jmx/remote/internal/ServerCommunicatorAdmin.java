package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.ClassLogger;

public abstract class ServerCommunicatorAdmin
{
    private long timestamp;
    private final int[] lock;
    private int currentJobs;
    private long timeout;
    private boolean terminated;
    private static final ClassLogger logger;
    private static final ClassLogger timelogger;
    
    public ServerCommunicatorAdmin(final long timeout) {
        this.lock = new int[0];
        this.currentJobs = 0;
        this.terminated = false;
        if (ServerCommunicatorAdmin.logger.traceOn()) {
            ServerCommunicatorAdmin.logger.trace("Constructor", "Creates a new ServerCommunicatorAdmin object with the timeout " + timeout);
        }
        this.timeout = timeout;
        this.timestamp = 0L;
        if (timeout < Long.MAX_VALUE) {
            final Thread thread = new Thread(new Timeout());
            thread.setName("JMX server connection timeout " + thread.getId());
            thread.setDaemon(true);
            thread.start();
        }
    }
    
    public boolean reqIncoming() {
        if (ServerCommunicatorAdmin.logger.traceOn()) {
            ServerCommunicatorAdmin.logger.trace("reqIncoming", "Receive a new request.");
        }
        synchronized (this.lock) {
            if (this.terminated) {
                ServerCommunicatorAdmin.logger.warning("reqIncoming", "The server has decided to close this client connection.");
            }
            ++this.currentJobs;
            return this.terminated;
        }
    }
    
    public boolean rspOutgoing() {
        if (ServerCommunicatorAdmin.logger.traceOn()) {
            ServerCommunicatorAdmin.logger.trace("reqIncoming", "Finish a request.");
        }
        synchronized (this.lock) {
            final int currentJobs = this.currentJobs - 1;
            this.currentJobs = currentJobs;
            if (currentJobs == 0) {
                this.logtime("Admin: Timestamp=", this.timestamp = System.currentTimeMillis());
                this.lock.notify();
            }
            return this.terminated;
        }
    }
    
    protected abstract void doStop();
    
    public void terminate() {
        if (ServerCommunicatorAdmin.logger.traceOn()) {
            ServerCommunicatorAdmin.logger.trace("terminate", "terminate the ServerCommunicatorAdmin object.");
        }
        synchronized (this.lock) {
            if (this.terminated) {
                return;
            }
            this.terminated = true;
            this.lock.notify();
        }
    }
    
    private void logtime(final String s, final long n) {
        ServerCommunicatorAdmin.timelogger.trace("synchro", s + n);
    }
    
    static {
        logger = new ClassLogger("javax.management.remote.misc", "ServerCommunicatorAdmin");
        timelogger = new ClassLogger("javax.management.remote.timeout", "ServerCommunicatorAdmin");
    }
    
    private class Timeout implements Runnable
    {
        @Override
        public void run() {
            boolean b = false;
            synchronized (ServerCommunicatorAdmin.this.lock) {
                if (ServerCommunicatorAdmin.this.timestamp == 0L) {
                    ServerCommunicatorAdmin.this.timestamp = System.currentTimeMillis();
                }
                ServerCommunicatorAdmin.this.logtime("Admin: timeout=", ServerCommunicatorAdmin.this.timeout);
                ServerCommunicatorAdmin.this.logtime("Admin: Timestamp=", ServerCommunicatorAdmin.this.timestamp);
                while (!ServerCommunicatorAdmin.this.terminated) {
                    try {
                        while (!ServerCommunicatorAdmin.this.terminated && ServerCommunicatorAdmin.this.currentJobs != 0) {
                            if (ServerCommunicatorAdmin.logger.traceOn()) {
                                ServerCommunicatorAdmin.logger.trace("Timeout-run", "Waiting without timeout.");
                            }
                            ServerCommunicatorAdmin.this.lock.wait();
                        }
                        if (ServerCommunicatorAdmin.this.terminated) {
                            return;
                        }
                        final long n = ServerCommunicatorAdmin.this.timeout - (System.currentTimeMillis() - ServerCommunicatorAdmin.this.timestamp);
                        ServerCommunicatorAdmin.this.logtime("Admin: remaining timeout=", n);
                        if (n > 0L) {
                            if (ServerCommunicatorAdmin.logger.traceOn()) {
                                ServerCommunicatorAdmin.logger.trace("Timeout-run", "Waiting with timeout: " + n + " ms remaining");
                            }
                            ServerCommunicatorAdmin.this.lock.wait(n);
                        }
                        if (ServerCommunicatorAdmin.this.currentJobs > 0) {
                            continue;
                        }
                        final long n2 = System.currentTimeMillis() - ServerCommunicatorAdmin.this.timestamp;
                        ServerCommunicatorAdmin.this.logtime("Admin: elapsed=", n2);
                        if (ServerCommunicatorAdmin.this.terminated || n2 <= ServerCommunicatorAdmin.this.timeout) {
                            continue;
                        }
                        if (ServerCommunicatorAdmin.logger.traceOn()) {
                            ServerCommunicatorAdmin.logger.trace("Timeout-run", "timeout elapsed");
                        }
                        ServerCommunicatorAdmin.this.logtime("Admin: timeout elapsed! " + n2 + ">", ServerCommunicatorAdmin.this.timeout);
                        ServerCommunicatorAdmin.this.terminated = true;
                        b = true;
                    }
                    catch (final InterruptedException ex) {
                        ServerCommunicatorAdmin.logger.warning("Timeout-run", "Unexpected Exception: " + ex);
                        ServerCommunicatorAdmin.logger.debug("Timeout-run", ex);
                        return;
                    }
                    break;
                }
            }
            if (b) {
                if (ServerCommunicatorAdmin.logger.traceOn()) {
                    ServerCommunicatorAdmin.logger.trace("Timeout-run", "Call the doStop.");
                }
                ServerCommunicatorAdmin.this.doStop();
            }
        }
    }
}
