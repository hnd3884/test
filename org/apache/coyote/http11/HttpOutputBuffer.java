package org.apache.coyote.http11;

import java.io.IOException;
import org.apache.coyote.OutputBuffer;

public interface HttpOutputBuffer extends OutputBuffer
{
    void end() throws IOException;
    
    void flush() throws IOException;
}
