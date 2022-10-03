package sun.net.www.http;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import sun.net.ProgressSource;
import java.io.InputStream;
import sun.net.www.MeteredStream;

public class KeepAliveStream extends MeteredStream implements Hurryable
{
    HttpClient hc;
    boolean hurried;
    protected boolean queuedForCleanup;
    private static final KeepAliveStreamCleaner queue;
    private static Thread cleanerThread;
    
    public KeepAliveStream(final InputStream inputStream, final ProgressSource progressSource, final long n, final HttpClient hc) {
        super(inputStream, progressSource, n);
        this.queuedForCleanup = false;
        this.hc = hc;
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        if (this.queuedForCleanup) {
            return;
        }
        try {
            if (this.expected > this.count) {
                if (this.expected - this.count <= this.available()) {
                    long n;
                    while ((n = this.expected - this.count) > 0L) {
                        if (this.skip(Math.min(n, this.available())) <= 0L) {
                            break;
                        }
                    }
                }
                else if (this.expected <= KeepAliveStreamCleaner.MAX_DATA_REMAINING && !this.hurried) {
                    queueForCleanup(new KeepAliveCleanerEntry(this, this.hc));
                }
                else {
                    this.hc.closeServer();
                }
            }
            if (!this.closed && !this.hurried && !this.queuedForCleanup) {
                this.hc.finished();
            }
        }
        finally {
            if (this.pi != null) {
                this.pi.finishTracking();
            }
            if (!this.queuedForCleanup) {
                this.in = null;
                this.hc = null;
                this.closed = true;
            }
        }
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public void mark(final int n) {
    }
    
    @Override
    public void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }
    
    @Override
    public synchronized boolean hurry() {
        try {
            if (this.closed || this.count >= this.expected) {
                return false;
            }
            if (this.in.available() < this.expected - this.count) {
                return false;
            }
            final byte[] array = new byte[(int)(this.expected - this.count)];
            new DataInputStream(this.in).readFully(array);
            this.in = new ByteArrayInputStream(array);
            return this.hurried = true;
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    private static void queueForCleanup(final KeepAliveCleanerEntry keepAliveCleanerEntry) {
        synchronized (KeepAliveStream.queue) {
            if (!keepAliveCleanerEntry.getQueuedForCleanup()) {
                if (!KeepAliveStream.queue.offer(keepAliveCleanerEntry)) {
                    keepAliveCleanerEntry.getHttpClient().closeServer();
                    return;
                }
                keepAliveCleanerEntry.setQueuedForCleanup();
                KeepAliveStream.queue.notifyAll();
            }
            int n = (KeepAliveStream.cleanerThread == null) ? 1 : 0;
            if (n == 0 && !KeepAliveStream.cleanerThread.isAlive()) {
                n = 1;
            }
            if (n != 0) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        ThreadGroup threadGroup;
                        ThreadGroup parent;
                        for (threadGroup = Thread.currentThread().getThreadGroup(); (parent = threadGroup.getParent()) != null; threadGroup = parent) {}
                        KeepAliveStream.cleanerThread = new Thread(threadGroup, KeepAliveStream.queue, "Keep-Alive-SocketCleaner");
                        KeepAliveStream.cleanerThread.setDaemon(true);
                        KeepAliveStream.cleanerThread.setPriority(8);
                        KeepAliveStream.cleanerThread.setContextClassLoader(null);
                        KeepAliveStream.cleanerThread.start();
                        return null;
                    }
                });
            }
        }
    }
    
    protected long remainingToRead() {
        return this.expected - this.count;
    }
    
    protected void setClosed() {
        this.in = null;
        this.hc = null;
        this.closed = true;
    }
    
    static {
        queue = new KeepAliveStreamCleaner();
    }
}
