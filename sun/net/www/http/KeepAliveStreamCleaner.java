package sun.net.www.http;

import java.security.AccessController;
import sun.net.NetProperties;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.util.LinkedList;

class KeepAliveStreamCleaner extends LinkedList<KeepAliveCleanerEntry> implements Runnable
{
    protected static int MAX_DATA_REMAINING;
    protected static int MAX_CAPACITY;
    protected static final int TIMEOUT = 5000;
    private static final int MAX_RETRIES = 5;
    
    @Override
    public boolean offer(final KeepAliveCleanerEntry keepAliveCleanerEntry) {
        return this.size() < KeepAliveStreamCleaner.MAX_CAPACITY && super.offer(keepAliveCleanerEntry);
    }
    
    @Override
    public void run() {
        KeepAliveCleanerEntry keepAliveCleanerEntry = null;
        do {
            try {
                synchronized (this) {
                    long currentTimeMillis = System.currentTimeMillis();
                    long n = 5000L;
                    while ((keepAliveCleanerEntry = this.poll()) == null) {
                        this.wait(n);
                        final long currentTimeMillis2 = System.currentTimeMillis();
                        final long n2 = currentTimeMillis2 - currentTimeMillis;
                        if (n2 > n) {
                            keepAliveCleanerEntry = this.poll();
                            break;
                        }
                        currentTimeMillis = currentTimeMillis2;
                        n -= n2;
                    }
                }
                if (keepAliveCleanerEntry == null) {
                    break;
                }
                final KeepAliveStream keepAliveStream = keepAliveCleanerEntry.getKeepAliveStream();
                if (keepAliveStream != null) {
                    synchronized (keepAliveStream) {
                        final HttpClient httpClient = keepAliveCleanerEntry.getHttpClient();
                        try {
                            if (httpClient != null && !httpClient.isInKeepAliveCache()) {
                                final int readTimeout = httpClient.getReadTimeout();
                                httpClient.setReadTimeout(5000);
                                long remainingToRead = keepAliveStream.remainingToRead();
                                if (remainingToRead > 0L) {
                                    long skip = 0L;
                                    for (int n3 = 0; skip < remainingToRead && n3 < 5; ++n3) {
                                        remainingToRead -= skip;
                                        skip = keepAliveStream.skip(remainingToRead);
                                        if (skip == 0L) {}
                                    }
                                    remainingToRead -= skip;
                                }
                                if (remainingToRead == 0L) {
                                    httpClient.setReadTimeout(readTimeout);
                                    httpClient.finished();
                                }
                                else {
                                    httpClient.closeServer();
                                }
                            }
                        }
                        catch (final IOException ex) {
                            httpClient.closeServer();
                        }
                        finally {
                            keepAliveStream.setClosed();
                        }
                    }
                }
            }
            catch (final InterruptedException ex2) {}
        } while (keepAliveCleanerEntry != null);
    }
    
    static {
        KeepAliveStreamCleaner.MAX_DATA_REMAINING = 512;
        KeepAliveStreamCleaner.MAX_CAPACITY = 10;
        KeepAliveStreamCleaner.MAX_DATA_REMAINING = AccessController.doPrivileged((PrivilegedAction<Integer>)new PrivilegedAction<Integer>() {
            @Override
            public Integer run() {
                return NetProperties.getInteger("http.KeepAlive.remainingData", KeepAliveStreamCleaner.MAX_DATA_REMAINING);
            }
        }) * 1024;
        KeepAliveStreamCleaner.MAX_CAPACITY = AccessController.doPrivileged((PrivilegedAction<Integer>)new PrivilegedAction<Integer>() {
            @Override
            public Integer run() {
                return NetProperties.getInteger("http.KeepAlive.queuedConnections", KeepAliveStreamCleaner.MAX_CAPACITY);
            }
        });
    }
}
