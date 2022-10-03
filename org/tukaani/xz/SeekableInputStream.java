package org.tukaani.xz;

import java.io.IOException;
import java.io.InputStream;

public abstract class SeekableInputStream extends InputStream
{
    @Override
    public long skip(long n) throws IOException {
        if (n <= 0L) {
            return 0L;
        }
        final long length = this.length();
        final long position = this.position();
        if (position >= length) {
            return 0L;
        }
        if (length - position < n) {
            n = length - position;
        }
        this.seek(position + n);
        return n;
    }
    
    public abstract long length() throws IOException;
    
    public abstract long position() throws IOException;
    
    public abstract void seek(final long p0) throws IOException;
}
