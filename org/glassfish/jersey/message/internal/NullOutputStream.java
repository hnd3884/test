package org.glassfish.jersey.message.internal;

import org.glassfish.jersey.internal.LocalizationMessages;
import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream
{
    private boolean isClosed;
    
    @Override
    public void write(final int b) throws IOException {
        this.checkClosed();
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.checkClosed();
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.checkClosed();
    }
    
    private void checkClosed() throws IOException {
        if (this.isClosed) {
            throw new IOException(LocalizationMessages.OUTPUT_STREAM_CLOSED());
        }
    }
    
    @Override
    public void close() throws IOException {
        this.isClosed = true;
    }
}
