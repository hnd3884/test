package jdk.management.jfr;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.Closeable;

final class Stream implements Closeable
{
    private final long identifier;
    private final BufferedInputStream inputStream;
    private final byte[] buffer;
    private volatile long time;
    
    Stream(final InputStream inputStream, final long identifier, final int n) {
        this.inputStream = new BufferedInputStream(inputStream, 50000);
        this.identifier = identifier;
        this.buffer = new byte[n];
    }
    
    private void touch() {
        this.time = System.currentTimeMillis();
    }
    
    public long getLastTouched() {
        return this.time;
    }
    
    public byte[] read() throws IOException {
        this.touch();
        final int read = this.inputStream.read(this.buffer);
        if (read == -1) {
            return null;
        }
        if (read != this.buffer.length) {
            final byte[] array = new byte[read];
            System.arraycopy(this.buffer, 0, array, 0, read);
            return array;
        }
        return this.buffer;
    }
    
    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }
    
    public long getId() {
        return this.identifier;
    }
}
