package java.nio.file;

import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.Closeable;

public interface WatchService extends Closeable
{
    void close() throws IOException;
    
    WatchKey poll();
    
    WatchKey poll(final long p0, final TimeUnit p1) throws InterruptedException;
    
    WatchKey take() throws InterruptedException;
}
