package org.glassfish.jersey.internal;

import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import javax.ws.rs.ProcessingException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.jar.JarEntry;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.io.IOException;
import java.util.logging.Level;
import java.util.jar.JarInputStream;
import java.util.LinkedList;
import java.net.URL;
import java.util.Enumeration;
import org.osgi.framework.BundleListener;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.BundleReference;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.osgi.framework.Bundle;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.Map;
import org.osgi.framework.BundleContext;
import java.util.logging.Logger;
import org.osgi.framework.SynchronousBundleListener;

public final class OsgiRegistry implements SynchronousBundleListener
{
    private static final String WEB_INF_CLASSES = "WEB-INF/classes/";
    private static final String CoreBundleSymbolicNAME = "org.glassfish.jersey.core.jersey-common";
    private static final Logger LOGGER;
    private final BundleContext bundleContext;
    private final Map<Long, Map<String, Callable<List<Class<?>>>>> factories;
    private final ReadWriteLock lock;
    private static OsgiRegistry instance;
    private final Map<String, Bundle> classToBundleMapping;
    
    public static synchronized OsgiRegistry getInstance() {
        if (OsgiRegistry.instance == null) {
            final ClassLoader classLoader = AccessController.doPrivileged(ReflectionHelper.getClassLoaderPA(ReflectionHelper.class));
            if (classLoader instanceof BundleReference) {
                final BundleContext context = FrameworkUtil.getBundle((Class)OsgiRegistry.class).getBundleContext();
                if (context != null) {
                    OsgiRegistry.instance = new OsgiRegistry(context);
                }
            }
        }
        return OsgiRegistry.instance;
    }
    
    public void bundleChanged(final BundleEvent event) {
        if (event.getType() == 32) {
            this.register(event.getBundle());
        }
        else if (event.getType() == 64 || event.getType() == 16) {
            final Bundle unregisteredBundle = event.getBundle();
            this.lock.writeLock().lock();
            try {
                this.factories.remove(unregisteredBundle.getBundleId());
                if (unregisteredBundle.getSymbolicName().equals("org.glassfish.jersey.core.jersey-common")) {
                    this.bundleContext.removeBundleListener((BundleListener)this);
                    this.factories.clear();
                }
            }
            finally {
                this.lock.writeLock().unlock();
            }
        }
    }
    
    public static String bundleEntryPathToClassName(String packagePath, String bundleEntryPath) {
        packagePath = normalizedPackagePath(packagePath);
        if (bundleEntryPath.contains("WEB-INF/classes/")) {
            bundleEntryPath = bundleEntryPath.substring(bundleEntryPath.indexOf("WEB-INF/classes/") + "WEB-INF/classes/".length());
        }
        final int packageIndex = bundleEntryPath.indexOf(packagePath);
        final String normalizedClassNamePath = (packageIndex > -1) ? bundleEntryPath.substring(packageIndex) : (packagePath + bundleEntryPath.substring(bundleEntryPath.lastIndexOf(47) + 1));
        return (normalizedClassNamePath.startsWith("/") ? normalizedClassNamePath.substring(1) : normalizedClassNamePath).replace('/', '.').replace(".class", "");
    }
    
    public static boolean isPackageLevelEntry(String packagePath, final String entryPath) {
        packagePath = normalizedPackagePath(packagePath);
        final String entryWithoutPackagePath = entryPath.contains(packagePath) ? entryPath.substring(entryPath.indexOf(packagePath) + packagePath.length()) : entryPath;
        return !(entryWithoutPackagePath.startsWith("/") ? entryWithoutPackagePath.substring(1) : entryWithoutPackagePath).contains("/");
    }
    
    public static String normalizedPackagePath(String packagePath) {
        packagePath = (packagePath.startsWith("/") ? packagePath.substring(1) : packagePath);
        packagePath = (packagePath.endsWith("/") ? packagePath : (packagePath + "/"));
        packagePath = ("/".equals(packagePath) ? "" : packagePath);
        return packagePath;
    }
    
    public Enumeration<URL> getPackageResources(final String packagePath, final ClassLoader classLoader, final boolean recursive) {
        final List<URL> result = new LinkedList<URL>();
        for (final Bundle bundle : this.bundleContext.getBundles()) {
            for (final String bundlePackagePath : new String[] { packagePath, "WEB-INF/classes/" + packagePath }) {
                final Enumeration<URL> enumeration = findEntries(bundle, bundlePackagePath, "*.class", recursive);
                if (enumeration != null) {
                    while (enumeration.hasMoreElements()) {
                        final URL url = enumeration.nextElement();
                        final String path = url.getPath();
                        this.classToBundleMapping.put(bundleEntryPathToClassName(packagePath, path), bundle);
                        result.add(url);
                    }
                }
            }
            final Enumeration<URL> jars = findEntries(bundle, "/", "*.jar", true);
            if (jars != null) {
                while (jars.hasMoreElements()) {
                    final URL jar = jars.nextElement();
                    final InputStream inputStream = classLoader.getResourceAsStream(jar.getPath());
                    if (inputStream == null) {
                        OsgiRegistry.LOGGER.config(LocalizationMessages.OSGI_REGISTRY_ERROR_OPENING_RESOURCE_STREAM(jar));
                    }
                    else {
                        JarInputStream jarInputStream;
                        try {
                            jarInputStream = new JarInputStream(inputStream);
                        }
                        catch (final IOException ex) {
                            OsgiRegistry.LOGGER.log(Level.CONFIG, LocalizationMessages.OSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(jar), ex);
                            try {
                                inputStream.close();
                            }
                            catch (final IOException ex3) {}
                            continue;
                        }
                        try {
                            JarEntry jarEntry;
                            while ((jarEntry = jarInputStream.getNextJarEntry()) != null) {
                                final String jarEntryName = jarEntry.getName();
                                final String jarEntryNameLeadingSlash = jarEntryName.startsWith("/") ? jarEntryName : ("/" + jarEntryName);
                                if (jarEntryName.endsWith(".class") && jarEntryNameLeadingSlash.contains("/" + normalizedPackagePath(packagePath))) {
                                    if (!recursive && !isPackageLevelEntry(packagePath, jarEntryName)) {
                                        continue;
                                    }
                                    this.classToBundleMapping.put(jarEntryName.replace(".class", "").replace('/', '.'), bundle);
                                    result.add(bundle.getResource(jarEntryName));
                                }
                            }
                        }
                        catch (final Exception ex2) {
                            OsgiRegistry.LOGGER.log(Level.CONFIG, LocalizationMessages.OSGI_REGISTRY_ERROR_PROCESSING_RESOURCE_STREAM(jar), ex2);
                        }
                        finally {
                            try {
                                jarInputStream.close();
                            }
                            catch (final IOException ex4) {}
                        }
                    }
                }
            }
        }
        return Collections.enumeration(result);
    }
    
    public Class<?> classForNameWithException(final String className) throws ClassNotFoundException {
        final Bundle bundle = this.classToBundleMapping.get(className);
        if (bundle == null) {
            throw new ClassNotFoundException(className);
        }
        return loadClass(bundle, className);
    }
    
    public ResourceBundle getResourceBundle(final String bundleName) {
        final int lastDotIndex = bundleName.lastIndexOf(46);
        final String path = bundleName.substring(0, lastDotIndex).replace('.', '/');
        final String propertiesName = bundleName.substring(lastDotIndex + 1, bundleName.length()) + ".properties";
        for (final Bundle bundle : this.bundleContext.getBundles()) {
            final Enumeration<URL> entries = findEntries(bundle, path, propertiesName, false);
            if (entries != null && entries.hasMoreElements()) {
                final URL entryUrl = entries.nextElement();
                try {
                    return new PropertyResourceBundle(entryUrl.openStream());
                }
                catch (final IOException ex) {
                    if (OsgiRegistry.LOGGER.isLoggable(Level.FINE)) {
                        OsgiRegistry.LOGGER.fine("Exception caught when tried to load resource bundle in OSGi");
                    }
                    return null;
                }
            }
        }
        return null;
    }
    
    private OsgiRegistry(final BundleContext bundleContext) {
        this.factories = new HashMap<Long, Map<String, Callable<List<Class<?>>>>>();
        this.lock = new ReentrantReadWriteLock();
        this.classToBundleMapping = new HashMap<String, Bundle>();
        this.bundleContext = bundleContext;
    }
    
    void hookUp() {
        this.setOSGiServiceFinderIteratorProvider();
        this.bundleContext.addBundleListener((BundleListener)this);
        this.registerExistingBundles();
    }
    
    private void registerExistingBundles() {
        for (final Bundle bundle : this.bundleContext.getBundles()) {
            if (bundle.getState() == 4 || bundle.getState() == 8 || bundle.getState() == 32 || bundle.getState() == 16) {
                this.register(bundle);
            }
        }
    }
    
    private void setOSGiServiceFinderIteratorProvider() {
        ServiceFinder.setIteratorProvider(new OsgiServiceFinder());
    }
    
    private void register(final Bundle bundle) {
        if (OsgiRegistry.LOGGER.isLoggable(Level.FINEST)) {
            OsgiRegistry.LOGGER.log(Level.FINEST, "checking bundle {0}", bundle.getBundleId());
        }
        this.lock.writeLock().lock();
        Map<String, Callable<List<Class<?>>>> map;
        try {
            map = this.factories.get(bundle.getBundleId());
            if (map == null) {
                map = new ConcurrentHashMap<String, Callable<List<Class<?>>>>();
                this.factories.put(bundle.getBundleId(), map);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
        final Enumeration<URL> e = findEntries(bundle, "META-INF/services/", "*", false);
        if (e != null) {
            while (e.hasMoreElements()) {
                final URL u = e.nextElement();
                final String url = u.toString();
                if (url.endsWith("/")) {
                    continue;
                }
                final String factoryId = url.substring(url.lastIndexOf("/") + 1);
                map.put(factoryId, new BundleSpiProvidersLoader(factoryId, u, bundle));
            }
        }
    }
    
    private List<Class<?>> locateAllProviders(final String serviceName) {
        this.lock.readLock().lock();
        try {
            final List<Class<?>> result = new LinkedList<Class<?>>();
            for (final Map<String, Callable<List<Class<?>>>> value : this.factories.values()) {
                if (value.containsKey(serviceName)) {
                    try {
                        result.addAll((Collection<? extends Class<?>>)value.get(serviceName).call());
                    }
                    catch (final Exception ex) {}
                }
            }
            return result;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    private static Class<?> loadClass(final Bundle bundle, final String className) throws ClassNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>)new PrivilegedExceptionAction<Class<?>>() {
                @Override
                public Class<?> run() throws ClassNotFoundException {
                    return bundle.loadClass(className);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            final Exception originalException = ex.getException();
            if (originalException instanceof ClassNotFoundException) {
                throw (ClassNotFoundException)originalException;
            }
            if (originalException instanceof RuntimeException) {
                throw (RuntimeException)originalException;
            }
            throw new ProcessingException((Throwable)originalException);
        }
    }
    
    private static Enumeration<URL> findEntries(final Bundle bundle, final String path, final String fileNamePattern, final boolean recursive) {
        return AccessController.doPrivileged((PrivilegedAction<Enumeration<URL>>)new PrivilegedAction<Enumeration<URL>>() {
            @Override
            public Enumeration<URL> run() {
                return bundle.findEntries(path, fileNamePattern, recursive);
            }
        });
    }
    
    static {
        LOGGER = Logger.getLogger(OsgiRegistry.class.getName());
    }
    
    private final class OsgiServiceFinder extends ServiceFinder.ServiceIteratorProvider
    {
        final ServiceFinder.ServiceIteratorProvider defaultIterator;
        
        private OsgiServiceFinder() {
            this.defaultIterator = new ServiceFinder.DefaultServiceIteratorProvider();
        }
        
        @Override
        public <T> Iterator<T> createIterator(final Class<T> serviceClass, final String serviceName, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
            final List<Class<?>> providerClasses = OsgiRegistry.this.locateAllProviders(serviceName);
            if (!providerClasses.isEmpty()) {
                return new Iterator<T>() {
                    Iterator<Class<?>> it = providerClasses.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.it.hasNext();
                    }
                    
                    @Override
                    public T next() {
                        final Class<T> nextClass = (Class<T>)this.it.next();
                        try {
                            return nextClass.newInstance();
                        }
                        catch (final Exception ex) {
                            final ServiceConfigurationError sce = new ServiceConfigurationError(serviceName + ": " + LocalizationMessages.PROVIDER_COULD_NOT_BE_CREATED(nextClass.getName(), serviceClass, ex.getLocalizedMessage()));
                            sce.initCause(ex);
                            throw sce;
                        }
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            return this.defaultIterator.createIterator(serviceClass, serviceName, loader, ignoreOnClassNotFound);
        }
        
        @Override
        public <T> Iterator<Class<T>> createClassIterator(final Class<T> service, final String serviceName, final ClassLoader loader, final boolean ignoreOnClassNotFound) {
            final List<Class<?>> providerClasses = OsgiRegistry.this.locateAllProviders(serviceName);
            if (!providerClasses.isEmpty()) {
                return new Iterator<Class<T>>() {
                    Iterator<Class<?>> it = providerClasses.iterator();
                    
                    @Override
                    public boolean hasNext() {
                        return this.it.hasNext();
                    }
                    
                    @Override
                    public Class<T> next() {
                        return (Class)this.it.next();
                    }
                    
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            return this.defaultIterator.createClassIterator(service, serviceName, loader, ignoreOnClassNotFound);
        }
    }
    
    private static class BundleSpiProvidersLoader implements Callable<List<Class<?>>>
    {
        private final String spi;
        private final URL spiRegistryUrl;
        private final String spiRegistryUrlString;
        private final Bundle bundle;
        
        BundleSpiProvidersLoader(final String spi, final URL spiRegistryUrl, final Bundle bundle) {
            this.spi = spi;
            this.spiRegistryUrl = spiRegistryUrl;
            this.spiRegistryUrlString = spiRegistryUrl.toExternalForm();
            this.bundle = bundle;
        }
        
        @Override
        public List<Class<?>> call() throws Exception {
            BufferedReader reader = null;
            try {
                if (OsgiRegistry.LOGGER.isLoggable(Level.FINEST)) {
                    OsgiRegistry.LOGGER.log(Level.FINEST, "Loading providers for SPI: {0}", this.spi);
                }
                reader = new BufferedReader(new InputStreamReader(this.spiRegistryUrl.openStream(), "UTF-8"));
                final List<Class<?>> providerClasses = new ArrayList<Class<?>>();
                String providerClassName;
                while ((providerClassName = reader.readLine()) != null) {
                    if (providerClassName.trim().length() == 0) {
                        continue;
                    }
                    if (OsgiRegistry.LOGGER.isLoggable(Level.FINEST)) {
                        OsgiRegistry.LOGGER.log(Level.FINEST, "SPI provider: {0}", providerClassName);
                    }
                    providerClasses.add(loadClass(this.bundle, providerClassName));
                }
                return providerClasses;
            }
            catch (final Exception e) {
                OsgiRegistry.LOGGER.log(Level.WARNING, LocalizationMessages.EXCEPTION_CAUGHT_WHILE_LOADING_SPI_PROVIDERS(), e);
                throw e;
            }
            catch (final Error e2) {
                OsgiRegistry.LOGGER.log(Level.WARNING, LocalizationMessages.ERROR_CAUGHT_WHILE_LOADING_SPI_PROVIDERS(), e2);
                throw e2;
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (final IOException ioe) {
                        OsgiRegistry.LOGGER.log(Level.FINE, "Error closing SPI registry stream:" + this.spiRegistryUrl, ioe);
                    }
                }
            }
        }
        
        @Override
        public String toString() {
            return this.spiRegistryUrlString;
        }
        
        @Override
        public int hashCode() {
            return this.spiRegistryUrlString.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof BundleSpiProvidersLoader && this.spiRegistryUrlString.equals(((BundleSpiProvidersLoader)obj).spiRegistryUrlString);
        }
    }
}
