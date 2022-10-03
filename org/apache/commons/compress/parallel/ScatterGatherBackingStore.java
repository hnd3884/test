package org.apache.commons.compress.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.Closeable;

public interface ScatterGatherBackingStore extends Closeable
{
    InputStream getInputStream() throws IOException;
    
    void writeOut(final byte[] p0, final int p1, final int p2) throws IOException;
    
    void closeForWriting() throws IOException;
}
