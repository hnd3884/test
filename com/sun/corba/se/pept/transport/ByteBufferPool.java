package com.sun.corba.se.pept.transport;

import java.nio.ByteBuffer;

public interface ByteBufferPool
{
    ByteBuffer getByteBuffer(final int p0);
    
    void releaseByteBuffer(final ByteBuffer p0);
    
    int activeCount();
}
