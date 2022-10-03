package org.apache.lucene.rangetree;

import java.io.IOException;
import java.io.Closeable;

interface SliceWriter extends Closeable
{
    void append(final long p0, final long p1, final int p2) throws IOException;
    
    SliceReader getReader(final long p0) throws IOException;
    
    void destroy() throws IOException;
}
