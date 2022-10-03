package org.apache.commons.httpclient.methods.multipart;

import java.io.IOException;
import java.io.InputStream;

public interface PartSource
{
    long getLength();
    
    String getFileName();
    
    InputStream createInputStream() throws IOException;
}
