package org.glassfish.hk2.osgiresourcelocator;

import java.util.List;
import java.net.URL;

public abstract class ResourceFinder
{
    private static ResourceFinder _me;
    
    public static void initialize(final ResourceFinder singleton) {
        if (singleton == null) {
            throw new NullPointerException("Did you intend to call reset()?");
        }
        if (ResourceFinder._me != null) {
            throw new IllegalStateException("Already initialzed with [" + ResourceFinder._me + "]");
        }
        ResourceFinder._me = singleton;
    }
    
    public static synchronized void reset() {
        if (ResourceFinder._me == null) {
            throw new IllegalStateException("Not yet initialized");
        }
        ResourceFinder._me = null;
    }
    
    public static URL findEntry(final String path) {
        if (ResourceFinder._me == null) {
            return null;
        }
        return ResourceFinder._me.findEntry1(path);
    }
    
    public static List<URL> findEntries(final String path) {
        if (ResourceFinder._me == null) {
            return null;
        }
        return ResourceFinder._me.findEntries1(path);
    }
    
    abstract URL findEntry1(final String p0);
    
    abstract List<URL> findEntries1(final String p0);
}
