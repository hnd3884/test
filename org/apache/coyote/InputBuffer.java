package org.apache.coyote;

import org.apache.tomcat.util.net.ApplicationBufferHandler;
import java.io.IOException;
import org.apache.tomcat.util.buf.ByteChunk;

public interface InputBuffer
{
    @Deprecated
    int doRead(final ByteChunk p0) throws IOException;
    
    int doRead(final ApplicationBufferHandler p0) throws IOException;
    
    int available();
}
