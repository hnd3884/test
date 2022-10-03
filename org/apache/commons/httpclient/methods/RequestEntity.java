package org.apache.commons.httpclient.methods;

import java.io.IOException;
import java.io.OutputStream;

public interface RequestEntity
{
    boolean isRepeatable();
    
    void writeRequest(final OutputStream p0) throws IOException;
    
    long getContentLength();
    
    String getContentType();
}
