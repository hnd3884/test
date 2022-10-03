package jdk.management.jfr;

import java.util.concurrent.TimeUnit;
import java.util.TimerTask;
import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.Map;

final class StreamManager
{
    public static final long TIME_OUT;
    public static final int DEFAULT_BLOCK_SIZE = 50000;
    private static long idCounter;
    private final Map<Long, Stream> streams;
    private Timer timer;
    
    StreamManager() {
        this.streams = new HashMap<Long, Stream>();
    }
    
    public synchronized Stream getStream(final long n) {
        final Stream stream = this.streams.get(n);
        if (stream == null) {
            throw new IllegalArgumentException("Unknown stream identifier " + n);
        }
        return stream;
    }
    
    public synchronized Stream create(final InputStream inputStream, final int n) {
        ++StreamManager.idCounter;
        final Stream stream = new Stream(inputStream, StreamManager.idCounter, n);
        this.streams.put(stream.getId(), stream);
        this.scheduleAbort(stream, System.currentTimeMillis() + StreamManager.TIME_OUT);
        return stream;
    }
    
    public synchronized void destroy(final Stream stream) {
        try {
            stream.close();
        }
        catch (final IOException ex) {}
        this.streams.remove(stream.getId());
        if (this.streams.isEmpty()) {
            this.timer.cancel();
            this.timer = null;
        }
    }
    
    public synchronized void scheduleAbort(final Stream stream, final long n) {
        if (this.timer == null) {
            this.timer = new Timer(true);
        }
        this.timer.schedule(new StreamCleanupTask(this, stream), new Date(n + StreamManager.TIME_OUT));
    }
    
    static {
        TIME_OUT = TimeUnit.MINUTES.toMillis(2L);
        StreamManager.idCounter = 0L;
    }
}
