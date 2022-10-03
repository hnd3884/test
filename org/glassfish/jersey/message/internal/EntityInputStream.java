package org.glassfish.jersey.message.internal;

import java.io.PushbackInputStream;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.io.IOException;
import java.io.InputStream;

public class EntityInputStream extends InputStream
{
    private InputStream input;
    private boolean closed;
    
    public static EntityInputStream create(final InputStream inputStream) {
        if (inputStream instanceof EntityInputStream) {
            return (EntityInputStream)inputStream;
        }
        return new EntityInputStream(inputStream);
    }
    
    public EntityInputStream(final InputStream input) {
        this.closed = false;
        this.input = input;
    }
    
    @Override
    public int read() throws IOException {
        return this.input.read();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.input.read(b);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.input.read(b, off, len);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.input.skip(n);
    }
    
    @Override
    public int available() throws IOException {
        return this.input.available();
    }
    
    @Override
    public void mark(final int readLimit) {
        this.input.mark(readLimit);
    }
    
    @Override
    public boolean markSupported() {
        return this.input.markSupported();
    }
    
    @Override
    public void reset() {
        try {
            this.input.reset();
        }
        catch (final IOException ex) {
            throw new ProcessingException(LocalizationMessages.MESSAGE_CONTENT_BUFFER_RESET_FAILED(), (Throwable)ex);
        }
    }
    
    @Override
    public void close() throws ProcessingException {
        final InputStream in = this.input;
        if (in == null) {
            return;
        }
        if (!this.closed) {
            try {
                in.close();
            }
            catch (final IOException ex) {
                throw new ProcessingException(LocalizationMessages.MESSAGE_CONTENT_INPUT_STREAM_CLOSE_FAILED(), (Throwable)ex);
            }
            finally {
                this.closed = true;
            }
        }
    }
    
    public boolean isEmpty() {
        this.ensureNotClosed();
        final InputStream in = this.input;
        if (in == null) {
            return true;
        }
        try {
            if (in.markSupported()) {
                in.mark(1);
                final int i = in.read();
                in.reset();
                return i == -1;
            }
            try {
                if (in.available() > 0) {
                    return false;
                }
            }
            catch (final IOException ex2) {}
            final int b = in.read();
            if (b == -1) {
                return true;
            }
            PushbackInputStream pbis;
            if (in instanceof PushbackInputStream) {
                pbis = (PushbackInputStream)in;
            }
            else {
                pbis = new PushbackInputStream(in, 1);
                this.input = pbis;
            }
            pbis.unread(b);
            return false;
        }
        catch (final IOException ex) {
            throw new ProcessingException((Throwable)ex);
        }
    }
    
    public void ensureNotClosed() throws IllegalStateException {
        if (this.closed) {
            throw new IllegalStateException(LocalizationMessages.ERROR_ENTITY_STREAM_CLOSED());
        }
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    public final InputStream getWrappedStream() {
        return this.input;
    }
    
    public final void setWrappedStream(final InputStream wrapped) {
        this.input = wrapped;
    }
}
