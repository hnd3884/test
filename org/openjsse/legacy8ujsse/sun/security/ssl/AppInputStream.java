package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.io.IOException;
import java.io.InputStream;

class AppInputStream extends InputStream
{
    private static final byte[] SKIP_ARRAY;
    private SSLSocketImpl c;
    InputRecord r;
    private final byte[] oneByte;
    
    AppInputStream(final SSLSocketImpl conn) {
        this.oneByte = new byte[1];
        this.r = new InputRecord();
        this.c = conn;
    }
    
    @Override
    public int available() throws IOException {
        if (this.c.checkEOF() || !this.r.isAppDataValid()) {
            return 0;
        }
        return this.r.available();
    }
    
    @Override
    public synchronized int read() throws IOException {
        final int n = this.read(this.oneByte, 0, 1);
        if (n <= 0) {
            return -1;
        }
        return this.oneByte[0] & 0xFF;
    }
    
    @Override
    public synchronized int read(final byte[] b, final int off, final int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (this.c.checkEOF()) {
            return -1;
        }
        try {
            while (this.r.available() == 0) {
                this.c.readDataRecord(this.r);
                if (this.c.checkEOF()) {
                    return -1;
                }
            }
            int howmany = Math.min(len, this.r.available());
            howmany = this.r.read(b, off, howmany);
            return howmany;
        }
        catch (final Exception e) {
            this.c.handleException(e);
            return -1;
        }
    }
    
    @Override
    public synchronized long skip(long n) throws IOException {
        long skipped;
        int r;
        for (skipped = 0L; n > 0L; n -= r, skipped += r) {
            final int len = (int)Math.min(n, AppInputStream.SKIP_ARRAY.length);
            r = this.read(AppInputStream.SKIP_ARRAY, 0, len);
            if (r <= 0) {
                break;
            }
        }
        return skipped;
    }
    
    @Override
    public void close() throws IOException {
        this.c.close();
    }
    
    static {
        SKIP_ARRAY = new byte[1024];
    }
}
