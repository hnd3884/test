package sun.net.httpserver;

import java.io.IOException;
import java.io.InputStream;

class FixedLengthInputStream extends LeftOverInputStream
{
    private long remaining;
    
    FixedLengthInputStream(final ExchangeImpl exchangeImpl, final InputStream inputStream, final long remaining) {
        super(exchangeImpl, inputStream);
        this.remaining = remaining;
    }
    
    @Override
    protected int readImpl(final byte[] array, final int n, int n2) throws IOException {
        this.eof = (this.remaining == 0L);
        if (this.eof) {
            return -1;
        }
        if (n2 > this.remaining) {
            n2 = (int)this.remaining;
        }
        final int read = this.in.read(array, n, n2);
        if (read > -1) {
            this.remaining -= read;
            if (this.remaining == 0L) {
                this.t.getServerImpl().requestCompleted(this.t.getConnection());
            }
        }
        return read;
    }
    
    @Override
    public int available() throws IOException {
        if (this.eof) {
            return 0;
        }
        final int available = this.in.available();
        return (available < this.remaining) ? available : ((int)this.remaining);
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
}
