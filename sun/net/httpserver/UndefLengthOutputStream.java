package sun.net.httpserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

class UndefLengthOutputStream extends FilterOutputStream
{
    private boolean closed;
    ExchangeImpl t;
    
    UndefLengthOutputStream(final ExchangeImpl t, final OutputStream outputStream) {
        super(outputStream);
        this.closed = false;
        this.t = t;
    }
    
    @Override
    public void write(final int n) throws IOException {
        if (this.closed) {
            throw new IOException("stream closed");
        }
        this.out.write(n);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        if (this.closed) {
            throw new IOException("stream closed");
        }
        this.out.write(array, n, n2);
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.flush();
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
