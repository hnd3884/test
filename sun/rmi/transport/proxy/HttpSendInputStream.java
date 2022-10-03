package sun.rmi.transport.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

class HttpSendInputStream extends FilterInputStream
{
    HttpSendSocket owner;
    
    public HttpSendInputStream(final InputStream inputStream, final HttpSendSocket owner) throws IOException {
        super(inputStream);
        this.owner = owner;
    }
    
    public void deactivate() {
        this.in = null;
    }
    
    @Override
    public int read() throws IOException {
        if (this.in == null) {
            this.in = this.owner.readNotify();
        }
        return this.in.read();
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 == 0) {
            return 0;
        }
        if (this.in == null) {
            this.in = this.owner.readNotify();
        }
        return this.in.read(array, n, n2);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (n == 0L) {
            return 0L;
        }
        if (this.in == null) {
            this.in = this.owner.readNotify();
        }
        return this.in.skip(n);
    }
    
    @Override
    public int available() throws IOException {
        if (this.in == null) {
            this.in = this.owner.readNotify();
        }
        return this.in.available();
    }
    
    @Override
    public void close() throws IOException {
        this.owner.close();
    }
    
    @Override
    public synchronized void mark(final int n) {
        if (this.in == null) {
            try {
                this.in = this.owner.readNotify();
            }
            catch (final IOException ex) {
                return;
            }
        }
        this.in.mark(n);
    }
    
    @Override
    public synchronized void reset() throws IOException {
        if (this.in == null) {
            this.in = this.owner.readNotify();
        }
        this.in.reset();
    }
    
    @Override
    public boolean markSupported() {
        if (this.in == null) {
            try {
                this.in = this.owner.readNotify();
            }
            catch (final IOException ex) {
                return false;
            }
        }
        return this.in.markSupported();
    }
}
