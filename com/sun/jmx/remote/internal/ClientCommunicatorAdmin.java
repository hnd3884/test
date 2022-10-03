package com.sun.jmx.remote.internal;

import com.sun.jmx.remote.util.EnvHelp;
import java.io.InterruptedIOException;
import java.io.IOException;
import com.sun.jmx.remote.util.ClassLogger;

public abstract class ClientCommunicatorAdmin
{
    private static volatile long threadNo;
    private final Checker checker;
    private long period;
    private static final int CONNECTED = 0;
    private static final int RE_CONNECTING = 1;
    private static final int FAILED = 2;
    private static final int TERMINATED = 3;
    private int state;
    private final int[] lock;
    private static final ClassLogger logger;
    
    public ClientCommunicatorAdmin(final long period) {
        this.state = 0;
        this.lock = new int[0];
        this.period = period;
        if (period > 0L) {
            this.checker = new Checker();
            final Thread thread = new Thread(this.checker, "JMX client heartbeat " + ++ClientCommunicatorAdmin.threadNo);
            thread.setDaemon(true);
            thread.start();
        }
        else {
            this.checker = null;
        }
    }
    
    public void gotIOException(final IOException ex) throws IOException {
        this.restart(ex);
    }
    
    protected abstract void checkConnection() throws IOException;
    
    protected abstract void doStart() throws IOException;
    
    protected abstract void doStop();
    
    public void terminate() {
        synchronized (this.lock) {
            if (this.state == 3) {
                return;
            }
            this.state = 3;
            this.lock.notifyAll();
            if (this.checker != null) {
                this.checker.stop();
            }
        }
    }
    
    private void restart(final IOException ex) throws IOException {
        synchronized (this.lock) {
            if (this.state == 3) {
                throw new IOException("The client has been closed.");
            }
            if (this.state == 2) {
                throw ex;
            }
            if (this.state == 1) {
                while (this.state == 1) {
                    try {
                        this.lock.wait();
                        continue;
                    }
                    catch (final InterruptedException ex2) {
                        final InterruptedIOException ex3 = new InterruptedIOException(ex2.toString());
                        EnvHelp.initCause(ex3, ex2);
                        throw ex3;
                    }
                    break;
                }
                if (this.state == 3) {
                    throw new IOException("The client has been closed.");
                }
                if (this.state != 0) {
                    throw ex;
                }
                return;
            }
            else {
                this.state = 1;
                this.lock.notifyAll();
            }
        }
        try {
            this.doStart();
            synchronized (this.lock) {
                if (this.state == 3) {
                    throw new IOException("The client has been closed.");
                }
                this.state = 0;
                this.lock.notifyAll();
            }
        }
        catch (final Exception ex4) {
            ClientCommunicatorAdmin.logger.warning("restart", "Failed to restart: " + ex4);
            ClientCommunicatorAdmin.logger.debug("restart", ex4);
            synchronized (this.lock) {
                if (this.state == 3) {
                    throw new IOException("The client has been closed.");
                }
                this.state = 2;
                this.lock.notifyAll();
            }
            try {
                this.doStop();
            }
            catch (final Exception ex5) {}
            this.terminate();
            throw ex;
        }
    }
    
    static {
        ClientCommunicatorAdmin.threadNo = 1L;
        logger = new ClassLogger("javax.management.remote.misc", "ClientCommunicatorAdmin");
    }
    
    private class Checker implements Runnable
    {
        private Thread myThread;
        
        @Override
        public void run() {
            this.myThread = Thread.currentThread();
            while (ClientCommunicatorAdmin.this.state != 3 && !this.myThread.isInterrupted()) {
                try {
                    Thread.sleep(ClientCommunicatorAdmin.this.period);
                }
                catch (final InterruptedException ex) {}
                if (ClientCommunicatorAdmin.this.state == 3) {
                    break;
                }
                if (this.myThread.isInterrupted()) {
                    break;
                }
                try {
                    ClientCommunicatorAdmin.this.checkConnection();
                }
                catch (final Exception ex2) {
                    synchronized (ClientCommunicatorAdmin.this.lock) {
                        if (ClientCommunicatorAdmin.this.state == 3 || this.myThread.isInterrupted()) {
                            break;
                        }
                    }
                    final Exception ex3 = (Exception)EnvHelp.getCause(ex2);
                    Label_0209: {
                        if (ex3 instanceof IOException && !(ex3 instanceof InterruptedIOException)) {
                            try {
                                ClientCommunicatorAdmin.this.gotIOException((IOException)ex3);
                                continue;
                            }
                            catch (final Exception ex4) {
                                ClientCommunicatorAdmin.logger.warning("Checker-run", "Failed to check connection: " + ex3);
                                ClientCommunicatorAdmin.logger.warning("Checker-run", "stopping");
                                ClientCommunicatorAdmin.logger.debug("Checker-run", ex3);
                                break;
                            }
                            break Label_0209;
                            continue;
                        }
                    }
                    ClientCommunicatorAdmin.logger.warning("Checker-run", "Failed to check the connection: " + ex3);
                    ClientCommunicatorAdmin.logger.debug("Checker-run", ex3);
                    break;
                }
            }
            if (ClientCommunicatorAdmin.logger.traceOn()) {
                ClientCommunicatorAdmin.logger.trace("Checker-run", "Finished.");
            }
        }
        
        private void stop() {
            if (this.myThread != null && this.myThread != Thread.currentThread()) {
                this.myThread.interrupt();
            }
        }
    }
}
