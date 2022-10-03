package org.apache.xmlbeans;

import java.io.InputStream;

public interface ResourceLoader
{
    InputStream getResourceAsStream(final String p0);
    
    void close();
}
