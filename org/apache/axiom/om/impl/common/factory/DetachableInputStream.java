package org.apache.axiom.om.impl.common.factory;

import org.apache.axiom.blob.MemoryBlob;
import org.apache.axiom.ext.io.StreamCopyException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.blob.Blobs;
import java.io.IOException;
import org.apache.axiom.om.impl.builder.Detachable;
import java.io.InputStream;

final class DetachableInputStream extends InputStream implements Detachable
{
    private InputStream target;
    private final boolean closeOnDetach;
    
    DetachableInputStream(final InputStream target, final boolean closeOnDetach) {
        this.target = target;
        this.closeOnDetach = closeOnDetach;
    }
    
    @Override
    public int read() throws IOException {
        return this.target.read();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.target.read(b);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.target.read(b, off, len);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.target.skip(n);
    }
    
    @Override
    public int available() throws IOException {
        return this.target.available();
    }
    
    @Override
    public void close() throws IOException {
        this.target.close();
    }
    
    public void detach() throws OMException {
        final MemoryBlob blob = Blobs.createMemoryBlob();
        try {
            blob.readFrom(this.target);
        }
        catch (final StreamCopyException ex) {
            throw new OMException(ex.getCause());
        }
        if (this.closeOnDetach) {
            try {
                this.target.close();
            }
            catch (final IOException ex2) {
                throw new OMException((Throwable)ex2);
            }
        }
        this.target = blob.readOnce();
    }
}
