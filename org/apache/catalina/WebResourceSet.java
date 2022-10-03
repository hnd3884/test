package org.apache.catalina;

import java.net.URL;
import java.io.InputStream;
import java.util.Set;

public interface WebResourceSet extends Lifecycle
{
    WebResource getResource(final String p0);
    
    String[] list(final String p0);
    
    Set<String> listWebAppPaths(final String p0);
    
    boolean mkdir(final String p0);
    
    boolean write(final String p0, final InputStream p1, final boolean p2);
    
    void setRoot(final WebResourceRoot p0);
    
    boolean getClassLoaderOnly();
    
    void setClassLoaderOnly(final boolean p0);
    
    boolean getStaticOnly();
    
    void setStaticOnly(final boolean p0);
    
    URL getBaseUrl();
    
    void setReadOnly(final boolean p0);
    
    boolean isReadOnly();
    
    void gc();
}
