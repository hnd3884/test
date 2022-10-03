package org.apache.lucene.bkdtree;

import java.io.IOException;
import java.io.Closeable;

interface LatLonWriter extends Closeable
{
    void append(final int p0, final int p1, final long p2, final int p3) throws IOException;
    
    LatLonReader getReader(final long p0) throws IOException;
    
    void destroy() throws IOException;
}
