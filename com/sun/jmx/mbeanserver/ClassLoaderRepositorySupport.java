package com.sun.jmx.mbeanserver;

import java.security.Permission;
import javax.management.MBeanPermission;
import javax.management.loading.PrivateClassLoader;
import sun.reflect.misc.ReflectUtil;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import javax.management.ObjectName;
import java.util.List;
import java.util.Map;

final class ClassLoaderRepositorySupport implements ModifiableClassLoaderRepository
{
    private static final LoaderEntry[] EMPTY_LOADER_ARRAY;
    private LoaderEntry[] loaders;
    private final Map<String, List<ClassLoader>> search;
    private final Map<ObjectName, ClassLoader> loadersWithNames;
    
    ClassLoaderRepositorySupport() {
        this.loaders = ClassLoaderRepositorySupport.EMPTY_LOADER_ARRAY;
        this.search = new Hashtable<String, List<ClassLoader>>(10);
        this.loadersWithNames = new Hashtable<ObjectName, ClassLoader>(10);
    }
    
    private synchronized boolean add(final ObjectName objectName, final ClassLoader classLoader) {
        final ArrayList list = new ArrayList((Collection<? extends E>)Arrays.asList(this.loaders));
        list.add(new LoaderEntry(objectName, classLoader));
        this.loaders = (LoaderEntry[])list.toArray(ClassLoaderRepositorySupport.EMPTY_LOADER_ARRAY);
        return true;
    }
    
    private synchronized boolean remove(final ObjectName objectName, final ClassLoader classLoader) {
        for (int length = this.loaders.length, i = 0; i < length; ++i) {
            final LoaderEntry loaderEntry = this.loaders[i];
            if ((objectName == null) ? (classLoader == loaderEntry.loader) : objectName.equals(loaderEntry.name)) {
                final LoaderEntry[] loaders = new LoaderEntry[length - 1];
                System.arraycopy(this.loaders, 0, loaders, 0, i);
                System.arraycopy(this.loaders, i + 1, loaders, i, length - 1 - i);
                this.loaders = loaders;
                return true;
            }
        }
        return false;
    }
    
    @Override
    public final Class<?> loadClass(final String s) throws ClassNotFoundException {
        return this.loadClass(this.loaders, s, null, null);
    }
    
    @Override
    public final Class<?> loadClassWithout(final ClassLoader classLoader, final String s) throws ClassNotFoundException {
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClassWithout", s + " without " + classLoader);
        }
        if (classLoader == null) {
            return this.loadClass(this.loaders, s, null, null);
        }
        this.startValidSearch(classLoader, s);
        try {
            return this.loadClass(this.loaders, s, classLoader, null);
        }
        finally {
            this.stopValidSearch(classLoader, s);
        }
    }
    
    @Override
    public final Class<?> loadClassBefore(final ClassLoader classLoader, final String s) throws ClassNotFoundException {
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClassBefore", s + " before " + classLoader);
        }
        if (classLoader == null) {
            return this.loadClass(this.loaders, s, null, null);
        }
        this.startValidSearch(classLoader, s);
        try {
            return this.loadClass(this.loaders, s, null, classLoader);
        }
        finally {
            this.stopValidSearch(classLoader, s);
        }
    }
    
    private Class<?> loadClass(final LoaderEntry[] array, final String s, final ClassLoader classLoader, final ClassLoader classLoader2) throws ClassNotFoundException {
        ReflectUtil.checkPackageAccess(s);
        for (int length = array.length, i = 0; i < length; ++i) {
            try {
                final ClassLoader loader = array[i].loader;
                if (loader == null) {
                    return Class.forName(s, false, null);
                }
                if (loader != classLoader) {
                    if (loader == classLoader2) {
                        break;
                    }
                    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "loadClass", "Trying loader = " + loader);
                    }
                    return Class.forName(s, false, loader);
                }
            }
            catch (final ClassNotFoundException ex) {}
        }
        throw new ClassNotFoundException(s);
    }
    
    private synchronized void startValidSearch(final ClassLoader classLoader, final String s) throws ClassNotFoundException {
        List list = this.search.get(s);
        if (list != null && list.contains(classLoader)) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "startValidSearch", "Already requested loader = " + classLoader + " class = " + s);
            }
            throw new ClassNotFoundException(s);
        }
        if (list == null) {
            list = new ArrayList(1);
            this.search.put(s, list);
        }
        list.add(classLoader);
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "startValidSearch", "loader = " + classLoader + " class = " + s);
        }
    }
    
    private synchronized void stopValidSearch(final ClassLoader classLoader, final String s) {
        final List list = this.search.get(s);
        if (list != null) {
            list.remove(classLoader);
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class.getName(), "stopValidSearch", "loader = " + classLoader + " class = " + s);
            }
        }
    }
    
    @Override
    public final void addClassLoader(final ClassLoader classLoader) {
        this.add(null, classLoader);
    }
    
    @Override
    public final void removeClassLoader(final ClassLoader classLoader) {
        this.remove(null, classLoader);
    }
    
    @Override
    public final synchronized void addClassLoader(final ObjectName objectName, final ClassLoader classLoader) {
        this.loadersWithNames.put(objectName, classLoader);
        if (!(classLoader instanceof PrivateClassLoader)) {
            this.add(objectName, classLoader);
        }
    }
    
    @Override
    public final synchronized void removeClassLoader(final ObjectName objectName) {
        final ClassLoader classLoader = this.loadersWithNames.remove(objectName);
        if (!(classLoader instanceof PrivateClassLoader)) {
            this.remove(objectName, classLoader);
        }
    }
    
    @Override
    public final ClassLoader getClassLoader(final ObjectName objectName) {
        final ClassLoader classLoader = this.loadersWithNames.get(objectName);
        if (classLoader != null) {
            final SecurityManager securityManager = System.getSecurityManager();
            if (securityManager != null) {
                securityManager.checkPermission(new MBeanPermission(classLoader.getClass().getName(), null, objectName, "getClassLoader"));
            }
        }
        return classLoader;
    }
    
    static {
        EMPTY_LOADER_ARRAY = new LoaderEntry[0];
    }
    
    private static class LoaderEntry
    {
        ObjectName name;
        ClassLoader loader;
        
        LoaderEntry(final ObjectName name, final ClassLoader loader) {
            this.name = name;
            this.loader = loader;
        }
    }
}
