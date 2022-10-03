package org.apache.lucene.store;

import org.apache.lucene.util.IOUtils;
import java.util.Collection;
import java.io.IOException;
import java.io.Closeable;

public abstract class Directory implements Closeable
{
    public abstract String[] listAll() throws IOException;
    
    public abstract void deleteFile(final String p0) throws IOException;
    
    public abstract long fileLength(final String p0) throws IOException;
    
    public abstract IndexOutput createOutput(final String p0, final IOContext p1) throws IOException;
    
    public abstract void sync(final Collection<String> p0) throws IOException;
    
    public abstract void renameFile(final String p0, final String p1) throws IOException;
    
    public abstract IndexInput openInput(final String p0, final IOContext p1) throws IOException;
    
    public ChecksumIndexInput openChecksumInput(final String name, final IOContext context) throws IOException {
        return new BufferedChecksumIndexInput(this.openInput(name, context));
    }
    
    public abstract Lock obtainLock(final String p0) throws IOException;
    
    @Override
    public abstract void close() throws IOException;
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + '@' + Integer.toHexString(this.hashCode());
    }
    
    public void copyFrom(final Directory from, final String src, final String dest, final IOContext context) throws IOException {
        boolean success = false;
        try (final IndexInput is = from.openInput(src, context);
             final IndexOutput os = this.createOutput(dest, context)) {
            os.copyBytes(is, is.length());
            success = true;
        }
        finally {
            if (!success) {
                IOUtils.deleteFilesIgnoringExceptions(this, dest);
            }
        }
    }
    
    protected void ensureOpen() throws AlreadyClosedException {
    }
}
