package org.apache.tomcat;

import java.util.jar.Manifest;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

public interface Jar extends AutoCloseable
{
    URL getJarFileURL();
    
    @Deprecated
    boolean entryExists(final String p0) throws IOException;
    
    InputStream getInputStream(final String p0) throws IOException;
    
    long getLastModified(final String p0) throws IOException;
    
    boolean exists(final String p0) throws IOException;
    
    void close();
    
    void nextEntry();
    
    String getEntryName();
    
    InputStream getEntryInputStream() throws IOException;
    
    String getURL(final String p0);
    
    Manifest getManifest() throws IOException;
    
    void reset() throws IOException;
}
