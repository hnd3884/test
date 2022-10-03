package org.apache.lucene.store;

import java.io.IOException;
import java.io.Closeable;

public abstract class Lock implements Closeable
{
    @Override
    public abstract void close() throws IOException;
    
    public abstract void ensureValid() throws IOException;
}
