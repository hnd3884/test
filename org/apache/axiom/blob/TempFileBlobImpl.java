package org.apache.axiom.blob;

import org.apache.commons.logging.LogFactory;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import org.apache.commons.logging.Log;

final class TempFileBlobImpl extends AbstractWritableBlob
{
    private static final Log log;
    private final TempFileBlobFactory factory;
    private final Throwable trace;
    private File file;
    private State state;
    
    TempFileBlobImpl(final TempFileBlobFactory factory) {
        this.state = State.NEW;
        this.factory = factory;
        this.trace = (TempFileBlobImpl.log.isDebugEnabled() ? new Throwable() : null);
    }
    
    public OutputStream getOutputStream() throws IOException {
        if (this.state != State.NEW) {
            throw new IllegalStateException();
        }
        this.file = this.factory.createTempFile();
        if (TempFileBlobImpl.log.isDebugEnabled()) {
            TempFileBlobImpl.log.debug((Object)("Using temporary file " + this.file));
        }
        this.file.deleteOnExit();
        final OutputStream out = new FileOutputStream(this.file) {
            @Override
            public void close() throws IOException {
                super.close();
                TempFileBlobImpl.this.state = State.COMMITTED;
            }
        };
        this.state = State.UNCOMMITTED;
        return out;
    }
    
    public InputStream getInputStream() throws IOException {
        if (this.state != State.COMMITTED) {
            throw new IllegalStateException();
        }
        return new TempFileInputStream(this.file);
    }
    
    public long getSize() {
        if (this.state != State.COMMITTED) {
            throw new IllegalStateException();
        }
        return this.file.length();
    }
    
    public void release() throws IOException {
        if (this.file != null) {
            if (TempFileBlobImpl.log.isDebugEnabled()) {
                TempFileBlobImpl.log.debug((Object)("Deleting temporary file " + this.file));
            }
            if (!this.file.delete()) {
                throw new IOException("Failed to delete " + this.file);
            }
            this.file = null;
            this.state = State.RELEASED;
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (this.file != null) {
            TempFileBlobImpl.log.warn((Object)("Cleaning up unreleased temporary file " + this.file));
            if (TempFileBlobImpl.log.isDebugEnabled()) {
                TempFileBlobImpl.log.debug((Object)"Blob was created here", this.trace);
            }
            this.file.delete();
        }
    }
    
    static {
        log = LogFactory.getLog((Class)TempFileBlobImpl.class);
    }
}
