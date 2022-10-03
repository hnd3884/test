package org.apache.lucene.store;

import java.io.IOException;

public interface RandomAccessInput
{
    byte readByte(final long p0) throws IOException;
    
    short readShort(final long p0) throws IOException;
    
    int readInt(final long p0) throws IOException;
    
    long readLong(final long p0) throws IOException;
}
