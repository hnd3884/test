package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

class CountingInputStream extends CloseIgnoringInputStream
{
    private long size;
    
    public CountingInputStream(final InputStream inputStream) {
        super(inputStream);
        this.size = 0L;
    }
    
    @Override
    public int read() throws IOException {
        final int read = this.in.read();
        if (read != -1 && this.size >= 0L) {
            ++this.size;
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        final int read = this.in.read(array, n, n2);
        if (read > 0 && this.size >= 0L) {
            this.size += read;
        }
        return read;
    }
    
    public long getSize() {
        return this.size;
    }
}
