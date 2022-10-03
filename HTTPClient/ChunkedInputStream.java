package HTTPClient;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

class ChunkedInputStream extends FilterInputStream
{
    byte[] one;
    private long chunk_len;
    private boolean eof;
    
    ChunkedInputStream(final InputStream is) {
        super(is);
        this.one = new byte[1];
        this.chunk_len = -1L;
        this.eof = false;
    }
    
    public synchronized int read() throws IOException {
        final int b = this.read(this.one, 0, 1);
        if (b == 1) {
            return this.one[0] & 0xFF;
        }
        return -1;
    }
    
    public synchronized int read(final byte[] buf, final int off, int len) throws IOException {
        if (this.eof) {
            return -1;
        }
        if (this.chunk_len == -1L) {
            try {
                this.chunk_len = Codecs.getChunkLength(super.in);
            }
            catch (final ParseException pe) {
                throw new IOException(pe.toString());
            }
        }
        if (this.chunk_len <= 0L) {
            final Request dummy = new Request(null, null, null, null, null, null, false);
            new Response(dummy, null).readTrailers(super.in);
            this.eof = true;
            return -1;
        }
        if (len > this.chunk_len) {
            len = (int)this.chunk_len;
        }
        final int rcvd = super.in.read(buf, off, len);
        if (rcvd == -1) {
            throw new EOFException("Premature EOF encountered");
        }
        this.chunk_len -= rcvd;
        if (this.chunk_len == 0L) {
            super.in.read();
            super.in.read();
            this.chunk_len = -1L;
        }
        return rcvd;
    }
    
    public synchronized long skip(final long num) throws IOException {
        final byte[] tmp = new byte[(int)num];
        final int got = this.read(tmp, 0, (int)num);
        if (got > 0) {
            return got;
        }
        return 0L;
    }
    
    public synchronized int available() throws IOException {
        if (this.eof) {
            return 0;
        }
        if (this.chunk_len != -1L) {
            return (int)this.chunk_len + super.in.available();
        }
        return super.in.available();
    }
}
