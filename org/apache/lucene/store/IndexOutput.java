package org.apache.lucene.store;

import java.io.IOException;
import java.io.Closeable;

public abstract class IndexOutput extends DataOutput implements Closeable
{
    private final String resourceDescription;
    
    protected IndexOutput(final String resourceDescription) {
        if (resourceDescription == null) {
            throw new IllegalArgumentException("resourceDescription must not be null");
        }
        this.resourceDescription = resourceDescription;
    }
    
    @Override
    public abstract void close() throws IOException;
    
    public abstract long getFilePointer();
    
    public abstract long getChecksum() throws IOException;
    
    @Override
    public String toString() {
        return this.resourceDescription;
    }
}
