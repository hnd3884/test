package org.apache.tomcat.util.net;

import java.nio.ByteBuffer;

public interface ApplicationBufferHandler
{
    void setByteBuffer(final ByteBuffer p0);
    
    ByteBuffer getByteBuffer();
    
    void expand(final int p0);
}
