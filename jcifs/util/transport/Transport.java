package jcifs.util.transport;

import java.io.PrintStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import jcifs.util.LogStream;

public abstract class Transport implements Runnable
{
    static int id;
    static LogStream log;
    int state;
    String name;
    Thread thread;
    TransportException te;
    protected HashMap response_map;
    public Object setupDiscoLock;
    
    public Transport() {
        this.state = 0;
        this.name = "Transport" + Transport.id++;
        this.response_map = new HashMap(4);
        this.setupDiscoLock = new Object();
    }
    
    public static int readn(final InputStream in, final byte[] b, final int off, final int len) throws IOException {
        int i = 0;
        for (int n = -5; i < len; i += n) {
            n = in.read(b, off + i, len - i);
            if (n <= 0) {
                break;
            }
        }
        return i;
    }
    
    protected abstract void makeKey(final Request p0) throws IOException;
    
    protected abstract Request peekKey() throws IOException;
    
    protected abstract void doSend(final Request p0) throws IOException;
    
    protected abstract void doRecv(final Response p0) throws IOException;
    
    protected abstract void doSkip() throws IOException;
    
    public void sendrecv(final Request request, final Response response, long timeout) throws IOException {
        synchronized (this.response_map) {
            this.makeKey(request);
            response.isReceived = false;
            try {
                this.response_map.put(request, response);
                this.doSend(request);
                response.expiration = System.currentTimeMillis() + timeout;
                while (!response.isReceived) {
                    this.response_map.wait(timeout);
                    timeout = response.expiration - System.currentTimeMillis();
                    if (timeout <= 0L) {
                        throw new TransportException(this.name + " timedout waiting for response to " + request);
                    }
                }
            }
            catch (final IOException ioe) {
                final LogStream log = Transport.log;
                if (LogStream.level > 2) {
                    ioe.printStackTrace(Transport.log);
                }
                try {
                    this.disconnect(true);
                }
                catch (final IOException ioe2) {
                    ioe2.printStackTrace(Transport.log);
                }
                throw ioe;
            }
            catch (final InterruptedException ie) {
                throw new TransportException(ie);
            }
            finally {
                this.response_map.remove(request);
            }
        }
    }
    
    private void loop() {
        while (this.thread == Thread.currentThread()) {
            try {
                final Request key = this.peekKey();
                if (key == null) {
                    throw new IOException("end of stream");
                }
                synchronized (this.response_map) {
                    final Response response = this.response_map.get(key);
                    if (response == null) {
                        final LogStream log = Transport.log;
                        if (LogStream.level >= 4) {
                            Transport.log.println("Invalid key, skipping message");
                        }
                        this.doSkip();
                    }
                    else {
                        this.doRecv(response);
                        response.isReceived = true;
                        this.response_map.notifyAll();
                    }
                }
            }
            catch (final Exception ex) {
                final String msg = ex.getMessage();
                final boolean timeout = msg != null && msg.equals("Read timed out");
                final boolean hard = !timeout;
                if (!timeout) {
                    final LogStream log2 = Transport.log;
                    if (LogStream.level >= 3) {
                        ex.printStackTrace(Transport.log);
                    }
                }
                try {
                    this.disconnect(hard);
                }
                catch (final IOException ioe) {
                    ioe.printStackTrace(Transport.log);
                }
            }
        }
    }
    
    protected abstract void doConnect() throws Exception;
    
    protected abstract void doDisconnect(final boolean p0) throws IOException;
    
    public synchronized void connect(final long timeout) throws TransportException {
        try {
            switch (this.state) {
                case 0: {
                    this.state = 1;
                    this.te = null;
                    (this.thread = new Thread(this, this.name)).setDaemon(true);
                    synchronized (this.thread) {
                        this.thread.start();
                        this.thread.wait(timeout);
                        switch (this.state) {
                            case 1: {
                                this.state = 0;
                                this.thread = null;
                                throw new TransportException("Connection timeout");
                            }
                            case 2: {
                                if (this.te != null) {
                                    this.state = 4;
                                    this.thread = null;
                                    throw this.te;
                                }
                                this.state = 3;
                                return;
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    return;
                }
                case 4: {
                    this.state = 0;
                    throw new TransportException("Connection in error", this.te);
                }
                default: {
                    final TransportException te = new TransportException("Invalid state: " + this.state);
                    this.state = 0;
                    throw te;
                }
            }
        }
        catch (final InterruptedException ie) {
            this.state = 0;
            this.thread = null;
            throw new TransportException(ie);
        }
        finally {
            if (this.state != 0 && this.state != 3) {
                final LogStream log = Transport.log;
                if (LogStream.level >= 1) {
                    Transport.log.println("Invalid state: " + this.state);
                }
                this.state = 0;
                this.thread = null;
            }
        }
    }
    
    public void disconnect(boolean hard) throws IOException {
        synchronized (this.setupDiscoLock) {
            synchronized (this) {
                switch (this.state) {
                    case 0: {
                        return;
                    }
                    case 2: {
                        hard = true;
                    }
                    case 3: {
                        if (this.response_map.size() != 0 && !hard) {
                            break;
                        }
                        this.doDisconnect(hard);
                    }
                    case 4: {
                        this.thread = null;
                        this.state = 0;
                        break;
                    }
                    default: {
                        final LogStream log = Transport.log;
                        if (LogStream.level >= 1) {
                            Transport.log.println("Invalid state: " + this.state);
                        }
                        this.thread = null;
                        this.state = 0;
                        break;
                    }
                }
            }
        }
    }
    
    public void run() {
        final Thread run_thread = Thread.currentThread();
        Exception ex0 = null;
        try {
            this.doConnect();
        }
        catch (final Exception ex2) {
            ex0 = ex2;
            return;
        }
        finally {
            synchronized (run_thread) {
                if (run_thread != this.thread) {
                    if (ex0 != null) {
                        ex0.printStackTrace();
                    }
                    return;
                }
                if (ex0 != null) {
                    this.te = new TransportException(ex0);
                }
                this.state = 2;
                run_thread.notify();
            }
        }
        this.loop();
    }
    
    public String toString() {
        return this.name;
    }
    
    static {
        Transport.id = 0;
        Transport.log = LogStream.getInstance();
    }
}
