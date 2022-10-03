package org.msgpack.io;

import java.io.IOException;
import java.io.Closeable;

public interface Input extends Closeable
{
    int read(final byte[] p0, final int p1, final int p2) throws IOException;
    
    boolean tryRefer(final BufferReferer p0, final int p1) throws IOException;
    
    byte readByte() throws IOException;
    
    void advance();
    
    byte getByte() throws IOException;
    
    short getShort() throws IOException;
    
    int getInt() throws IOException;
    
    long getLong() throws IOException;
    
    float getFloat() throws IOException;
    
    double getDouble() throws IOException;
    
    int getReadByteCount();
    
    void resetReadByteCount();
}
