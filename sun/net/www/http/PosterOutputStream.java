package sun.net.www.http;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class PosterOutputStream extends ByteArrayOutputStream
{
    private boolean closed;
    
    public PosterOutputStream() {
        super(256);
    }
    
    @Override
    public synchronized void write(final int n) {
        if (this.closed) {
            return;
        }
        super.write(n);
    }
    
    @Override
    public synchronized void write(final byte[] array, final int n, final int n2) {
        if (this.closed) {
            return;
        }
        super.write(array, n, n2);
    }
    
    @Override
    public synchronized void reset() {
        if (this.closed) {
            return;
        }
        super.reset();
    }
    
    @Override
    public synchronized void close() throws IOException {
        this.closed = true;
        super.close();
    }
}
