package sun.net.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

class FixedLengthOutputStream extends FilterOutputStream
{
    private long remaining;
    private boolean eof;
    private boolean closed;
    ExchangeImpl t;
    
    FixedLengthOutputStream(final ExchangeImpl t, final OutputStream outputStream, final long remaining) {
        super(outputStream);
        this.eof = false;
        this.closed = false;
        this.t = t;
        this.remaining = remaining;
    }
    
    @Override
    public void write(final int n) throws IOException {
        if (this.closed) {
            throw new IOException("stream closed");
        }
        this.eof = (this.remaining == 0L);
        if (this.eof) {
            throw new StreamClosedException();
        }
        this.out.write(n);
        --this.remaining;
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        if (this.closed) {
            throw new IOException("stream closed");
        }
        this.eof = (this.remaining == 0L);
        if (this.eof) {
            throw new StreamClosedException();
        }
        if (n2 > this.remaining) {
            throw new IOException("too many bytes to write to stream");
        }
        this.out.write(array, n, n2);
        this.remaining -= n2;
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.remaining > 0L) {
            this.t.close();
            throw new IOException("insufficient bytes written to stream");
        }
        this.flush();
        this.eof = true;
        final LeftOverInputStream originalInputStream = this.t.getOriginalInputStream();
        if (!originalInputStream.isClosed()) {
            try {
                originalInputStream.close();
            }
            catch (final IOException ex) {}
        }
        this.t.getHttpContext().getServerImpl().addEvent(new WriteFinishedEvent(this.t));
    }
}
