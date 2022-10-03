package org.apache.catalina;

import java.util.List;
import java.net.URL;
import java.io.InputStream;
import java.util.Set;

public interface WebResourceRoot extends Lifecycle
{
    WebResource getResource(final String p0);
    
    WebResource[] getResources(final String p0);
    
    WebResource getClassLoaderResource(final String p0);
    
    WebResource[] getClassLoaderResources(final String p0);
    
    String[] list(final String p0);
    
    Set<String> listWebAppPaths(final String p0);
    
    WebResource[] listResources(final String p0);
    
    boolean mkdir(final String p0);
    
    boolean write(final String p0, final InputStream p1, final boolean p2);
    
    void createWebResourceSet(final ResourceSetType p0, final String p1, final URL p2, final String p3);
    
    void createWebResourceSet(final ResourceSetType p0, final String p1, final String p2, final String p3, final String p4);
    
    void addPreResources(final WebResourceSet p0);
    
    WebResourceSet[] getPreResources();
    
    void addJarResources(final WebResourceSet p0);
    
    WebResourceSet[] getJarResources();
    
    void addPostResources(final WebResourceSet p0);
    
    WebResourceSet[] getPostResources();
    
    Context getContext();
    
    void setContext(final Context p0);
    
    void setAllowLinking(final boolean p0);
    
    boolean getAllowLinking();
    
    void setCachingAllowed(final boolean p0);
    
    boolean isCachingAllowed();
    
    void setCacheTtl(final long p0);
    
    long getCacheTtl();
    
    void setCacheMaxSize(final long p0);
    
    long getCacheMaxSize();
    
    void setCacheObjectMaxSize(final int p0);
    
    int getCacheObjectMaxSize();
    
    void setTrackLockedFiles(final boolean p0);
    
    boolean getTrackLockedFiles();
    
    void backgroundProcess();
    
    void registerTrackedResource(final TrackedWebResource p0);
    
    void deregisterTrackedResource(final TrackedWebResource p0);
    
    List<URL> getBaseUrls();
    
    void gc();
    
    public enum ResourceSetType
    {
        PRE, 
        RESOURCE_JAR, 
        POST, 
        CLASSES_JAR;
    }
}
