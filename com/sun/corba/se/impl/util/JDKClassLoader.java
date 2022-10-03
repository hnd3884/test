package com.sun.corba.se.impl.util;

import java.util.Collections;
import java.util.WeakHashMap;
import java.util.Map;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.corba.Bridge;

class JDKClassLoader
{
    private static final JDKClassLoaderCache classCache;
    private static final Bridge bridge;
    
    static Class loadClass(final Class clazz, final String s) throws ClassNotFoundException {
        if (s == null) {
            throw new NullPointerException();
        }
        if (s.length() == 0) {
            throw new ClassNotFoundException();
        }
        ClassLoader classLoader;
        if (clazz != null) {
            classLoader = clazz.getClassLoader();
        }
        else {
            classLoader = JDKClassLoader.bridge.getLatestUserDefinedLoader();
        }
        final Object key = JDKClassLoader.classCache.createKey(s, classLoader);
        if (JDKClassLoader.classCache.knownToFail(key)) {
            throw new ClassNotFoundException(s);
        }
        try {
            return Class.forName(s, false, classLoader);
        }
        catch (final ClassNotFoundException ex) {
            JDKClassLoader.classCache.recordFailure(key);
            throw ex;
        }
    }
    
    static {
        classCache = new JDKClassLoaderCache();
        bridge = AccessController.doPrivileged((PrivilegedAction<Bridge>)new PrivilegedAction() {
            @Override
            public Object run() {
                return Bridge.get();
            }
        });
    }
    
    private static class JDKClassLoaderCache
    {
        private final Map cache;
        private static final Object KNOWN_TO_FAIL;
        
        private JDKClassLoaderCache() {
            this.cache = Collections.synchronizedMap(new WeakHashMap<Object, Object>());
        }
        
        public final void recordFailure(final Object o) {
            this.cache.put(o, JDKClassLoaderCache.KNOWN_TO_FAIL);
        }
        
        public final Object createKey(final String s, final ClassLoader classLoader) {
            return new CacheKey(s, classLoader);
        }
        
        public final boolean knownToFail(final Object o) {
            return this.cache.get(o) == JDKClassLoaderCache.KNOWN_TO_FAIL;
        }
        
        static {
            KNOWN_TO_FAIL = new Object();
        }
        
        private static class CacheKey
        {
            String className;
            ClassLoader loader;
            
            public CacheKey(final String className, final ClassLoader loader) {
                this.className = className;
                this.loader = loader;
            }
            
            @Override
            public int hashCode() {
                if (this.loader == null) {
                    return this.className.hashCode();
                }
                return this.className.hashCode() ^ this.loader.hashCode();
            }
            
            @Override
            public boolean equals(final Object o) {
                try {
                    if (o == null) {
                        return false;
                    }
                    final CacheKey cacheKey = (CacheKey)o;
                    return this.className.equals(cacheKey.className) && this.loader == cacheKey.loader;
                }
                catch (final ClassCastException ex) {
                    return false;
                }
            }
        }
    }
}
