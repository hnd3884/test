package sun.rmi.log;

import java.io.IOException;
import java.io.InputStream;

public class LogInputStream extends InputStream
{
    private InputStream in;
    private int length;
    
    public LogInputStream(final InputStream in, final int length) throws IOException {
        this.in = in;
        this.length = length;
    }
    
    @Override
    public int read() throws IOException {
        if (this.length == 0) {
            return -1;
        }
        final int read = this.in.read();
        this.length = ((read != -1) ? (this.length - 1) : 0);
        return read;
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    @Override
    public int read(final byte[] array, final int n, int n2) throws IOException {
        if (this.length == 0) {
            return -1;
        }
        n2 = ((this.length < n2) ? this.length : n2);
        final int read = this.in.read(array, n, n2);
        this.length = ((read != -1) ? (this.length - read) : 0);
        return read;
    }
    
    @Override
    public long skip(long skip) throws IOException {
        if (skip > 2147483647L) {
            throw new IOException("Too many bytes to skip - " + skip);
        }
        if (this.length == 0) {
            return 0L;
        }
        skip = ((this.length < skip) ? this.length : skip);
        skip = this.in.skip(skip);
        this.length -= (int)skip;
        return skip;
    }
    
    @Override
    public int available() throws IOException {
        final int available = this.in.available();
        return (this.length < available) ? this.length : available;
    }
    
    @Override
    public void close() {
        this.length = 0;
    }
    
    @Override
    protected void finalize() throws IOException {
        this.close();
    }
}
