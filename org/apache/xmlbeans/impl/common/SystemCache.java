package org.apache.xmlbeans.impl.common;

import org.apache.xmlbeans.SystemProperties;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.SchemaTypeLoader;

public class SystemCache
{
    private static SystemCache INSTANCE;
    private ThreadLocal tl_saxLoaders;
    
    public SystemCache() {
        this.tl_saxLoaders = new ThreadLocal();
    }
    
    public static final synchronized void set(final SystemCache instance) {
        SystemCache.INSTANCE = instance;
    }
    
    public static final SystemCache get() {
        return SystemCache.INSTANCE;
    }
    
    public SchemaTypeLoader getFromTypeLoaderCache(final ClassLoader cl) {
        return null;
    }
    
    public void addToTypeLoaderCache(final SchemaTypeLoader stl, final ClassLoader cl) {
    }
    
    public void clearThreadLocals() {
        this.tl_saxLoaders.remove();
    }
    
    public Object getSaxLoader() {
        final SoftReference s = this.tl_saxLoaders.get();
        if (s == null) {
            return null;
        }
        return s.get();
    }
    
    public void setSaxLoader(final Object saxLoader) {
        this.tl_saxLoaders.set(new SoftReference(saxLoader));
    }
    
    static {
        SystemCache.INSTANCE = new SystemCache();
        final String cacheClass = SystemProperties.getProperty("xmlbean.systemcacheimpl");
        Object impl = null;
        if (cacheClass != null) {
            try {
                impl = Class.forName(cacheClass).newInstance();
                if (!(impl instanceof SystemCache)) {
                    throw new ClassCastException("Value for system property \"xmlbean.systemcacheimpl\" points to a class (" + cacheClass + ") which does not derive from SystemCache");
                }
            }
            catch (final ClassNotFoundException cnfe) {
                throw new RuntimeException("Cache class " + cacheClass + " specified by \"xmlbean.systemcacheimpl\" was not found.", cnfe);
            }
            catch (final InstantiationException ie) {
                throw new RuntimeException("Could not instantiate class " + cacheClass + " as specified by \"xmlbean.systemcacheimpl\"." + " An empty constructor may be missing.", ie);
            }
            catch (final IllegalAccessException iae) {
                throw new RuntimeException("Could not instantiate class " + cacheClass + " as specified by \"xmlbean.systemcacheimpl\"." + " A public empty constructor may be missing.", iae);
            }
        }
        if (impl != null) {
            SystemCache.INSTANCE = (SystemCache)impl;
        }
    }
}
