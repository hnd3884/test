package org.apache.commons.fileupload.util;

import java.io.IOException;

public interface Closeable
{
    void close() throws IOException;
    
    boolean isClosed() throws IOException;
}
