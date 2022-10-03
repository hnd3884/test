package org.apache.coyote;

import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.tomcat.util.buf.ByteChunk;

public interface OutputBuffer
{
    @Deprecated
    int doWrite(final ByteChunk p0) throws IOException;
    
    int doWrite(final ByteBuffer p0) throws IOException;
    
    long getBytesWritten();
}
