package org.glassfish.hk2.osgiresourcelocator;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.BundleEvent;
import java.net.URL;
import java.util.Enumeration;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Scanner;
import java.io.InputStream;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleListener;
import java.util.concurrent.locks.ReadWriteLock;

public final class ServiceLoaderImpl extends ServiceLoader
{
    private ReadWriteLock rwLock;
    private BundleListener bundleTracker;
    private BundleContext bundleContext;
    private ProvidersList providersList;
    
    public ServiceLoaderImpl() {
        this.rwLock = new ReentrantReadWriteLock();
        this.providersList = new ProvidersList();
        final ClassLoader cl = this.getClass().getClassLoader();
        if (cl instanceof BundleReference) {
            this.bundleContext = this.getBundleContextSecured(BundleReference.class.cast(cl).getBundle());
        }
        if (this.bundleContext == null) {
            throw new RuntimeException("There is no bundle context available yet. Instatiate this class in STARTING or ACTIVE state only");
        }
    }
    
    private BundleContext getBundleContextSecured(final Bundle bundle) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged((PrivilegedAction<BundleContext>)new PrivilegedAction<BundleContext>() {
                @Override
                public BundleContext run() {
                    return bundle.getBundleContext();
                }
            });
        }
        return bundle.getBundleContext();
    }
    
    public void trackBundles() {
        assert this.bundleTracker == null;
        this.bundleTracker = (BundleListener)new BundleTracker();
        this.bundleContext.addBundleListener(this.bundleTracker);
        for (final Bundle bundle : this.bundleContext.getBundles()) {
            this.addProviders(bundle);
        }
    }
    
    @Override
     <T> Iterable<? extends T> lookupProviderInstances1(final Class<T> serviceClass, ProviderFactory<T> factory) {
        if (factory == null) {
            factory = new DefaultFactory<T>();
        }
        final List<T> providers = new ArrayList<T>();
        for (final Class c : this.lookupProviderClasses1(serviceClass)) {
            try {
                final T providerInstance = factory.make(c, serviceClass);
                if (providerInstance != null) {
                    providers.add(providerInstance);
                }
                else {
                    this.debug(factory + " returned null provider instance!!!");
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return (Iterable<? extends T>)providers;
    }
    
    @Override
     <T> Iterable<Class> lookupProviderClasses1(final Class<T> serviceClass) {
        final List<Class> providerClasses = new ArrayList<Class>();
        this.rwLock.readLock().lock();
        try {
            final String serviceName = serviceClass.getName();
            for (final ProvidersPerBundle providersPerBundle : this.providersList.getAllProviders()) {
                final Bundle bundle = this.bundleContext.getBundle(providersPerBundle.getBundleId());
                if (bundle == null) {
                    continue;
                }
                final List<String> providerNames = providersPerBundle.getServiceToProvidersMap().get(serviceName);
                if (providerNames == null) {
                    continue;
                }
                for (final String providerName : providerNames) {
                    try {
                        final Class providerClass = this.loadClassSecured(bundle, providerName);
                        if (!this.isCompatible(providerClass, serviceClass)) {
                            continue;
                        }
                        providerClasses.add(providerClass);
                    }
                    catch (final ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            return providerClasses;
        }
        finally {
            this.rwLock.readLock().unlock();
        }
    }
    
    private Class loadClassSecured(final Bundle bundle, final String name) throws ClassNotFoundException {
        if (System.getSecurityManager() != null) {
            try {
                return AccessController.doPrivileged((PrivilegedExceptionAction<Class>)new PrivilegedExceptionAction<Class>() {
                    @Override
                    public Class run() throws ClassNotFoundException {
                        return bundle.loadClass(name);
                    }
                });
            }
            catch (final PrivilegedActionException e) {
                throw ClassNotFoundException.class.cast(e.getException());
            }
        }
        return bundle.loadClass(name);
    }
    
    private boolean isCompatible(final Class providerClass, final Class serviceClass) {
        try {
            final Class<?> serviceClassSeenByProviderClass = Class.forName(serviceClass.getName(), false, providerClass.getClassLoader());
            final boolean isCompatible = serviceClassSeenByProviderClass == serviceClass;
            if (!isCompatible) {
                this.debug(providerClass + " loaded by " + providerClass.getClassLoader() + " sees " + serviceClass + " from " + serviceClassSeenByProviderClass.getClassLoader() + ", where as caller uses " + serviceClass + " loaded by " + serviceClass.getClassLoader());
            }
            return isCompatible;
        }
        catch (final ClassNotFoundException e) {
            this.debug("Unable to reach " + serviceClass + " from " + providerClass + ", which is loaded by " + providerClass.getClassLoader(), e);
            return true;
        }
    }
    
    private List<String> load(final InputStream is) throws IOException {
        final List<String> providerNames = new ArrayList<String>();
        try {
            final Scanner scanner = new Scanner(is);
            final String commentPattern = "#";
            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                if (!line.startsWith("#")) {
                    final StringTokenizer st = new StringTokenizer(line);
                    if (!st.hasMoreTokens()) {
                        continue;
                    }
                    providerNames.add(st.nextToken());
                }
            }
        }
        finally {
            is.close();
        }
        return providerNames;
    }
    
    private void addProviders(final Bundle bundle) {
        this.rwLock.writeLock().lock();
        try {
            final String SERVICE_LOCATION = "META-INF/services";
            if (bundle.getEntry("META-INF/services") == null) {
                return;
            }
            final Enumeration<String> entries = bundle.getEntryPaths("META-INF/services");
            if (entries != null) {
                final ProvidersPerBundle providers = new ProvidersPerBundle(bundle.getBundleId());
                while (entries.hasMoreElements()) {
                    final String entry = entries.nextElement();
                    final String serviceName = entry.substring("META-INF/services".length() + 1);
                    final URL url = bundle.getEntry(entry);
                    try {
                        final InputStream is = url.openStream();
                        final List<String> providerNames = this.load(is);
                        this.debug("Bundle = " + bundle + ", serviceName = " + serviceName + ", providerNames = " + providerNames);
                        providers.put(serviceName, providerNames);
                    }
                    catch (final IOException ex) {}
                }
                this.providersList.addProviders(providers);
            }
        }
        finally {
            this.rwLock.writeLock().unlock();
        }
    }
    
    private synchronized void removeProviders(final Bundle bundle) {
        this.rwLock.writeLock().lock();
        try {
            this.providersList.removeProviders(bundle.getBundleId());
        }
        finally {
            this.rwLock.writeLock().unlock();
        }
    }
    
    private void debug(final String s) {
        if (Boolean.valueOf(this.bundleContext.getProperty("org.glassfish.hk2.osgiresourcelocator.debug"))) {
            System.out.println("org.glassfish.hk2.osgiresourcelocator:DEBUG: " + s);
        }
    }
    
    private void debug(final String s, final Throwable t) {
        if (Boolean.valueOf(this.bundleContext.getProperty("org.glassfish.hk2.osgiresourcelocator.debug"))) {
            System.out.println("org.glassfish.hk2.osgiresourcelocator:DEBUG: " + s);
            t.printStackTrace(System.out);
        }
    }
    
    private class BundleTracker implements BundleListener
    {
        public void bundleChanged(final BundleEvent event) {
            final Bundle bundle = event.getBundle();
            switch (event.getType()) {
                case 1: {
                    ServiceLoaderImpl.this.addProviders(bundle);
                    break;
                }
                case 16: {
                    ServiceLoaderImpl.this.removeProviders(bundle);
                    break;
                }
                case 8: {
                    ServiceLoaderImpl.this.removeProviders(bundle);
                    ServiceLoaderImpl.this.addProviders(bundle);
                    break;
                }
            }
        }
    }
    
    private static class ProvidersPerBundle
    {
        private long bundleId;
        Map<String, List<String>> serviceToProvidersMap;
        
        private ProvidersPerBundle(final long bundleId) {
            this.serviceToProvidersMap = new HashMap<String, List<String>>();
            this.bundleId = bundleId;
        }
        
        public long getBundleId() {
            return this.bundleId;
        }
        
        public void put(final String serviceName, final List<String> providerNames) {
            this.serviceToProvidersMap.put(serviceName, providerNames);
        }
        
        public Map<String, List<String>> getServiceToProvidersMap() {
            return this.serviceToProvidersMap;
        }
    }
    
    private static class ProvidersList
    {
        private List<ProvidersPerBundle> allProviders;
        
        private ProvidersList() {
            this.allProviders = new LinkedList<ProvidersPerBundle>();
        }
        
        void addProviders(final ProvidersPerBundle providers) {
            final long bundleId = providers.getBundleId();
            final int idx = 0;
            for (final ProvidersPerBundle providersPerBundle : this.getAllProviders()) {
                if (providersPerBundle.getBundleId() > bundleId) {
                    this.getAllProviders().add(idx, providers);
                    return;
                }
            }
            this.getAllProviders().add(providers);
        }
        
        void removeProviders(final long bundleId) {
            final Iterator<ProvidersPerBundle> iterator = this.getAllProviders().iterator();
            while (iterator.hasNext()) {
                final ProvidersPerBundle providersPerBundle = iterator.next();
                if (providersPerBundle.getBundleId() == bundleId) {
                    iterator.remove();
                }
            }
        }
        
        public List<ProvidersPerBundle> getAllProviders() {
            return this.allProviders;
        }
    }
    
    private static class DefaultFactory<T> implements ProviderFactory<T>
    {
        @Override
        public T make(final Class providerClass, final Class<T> serviceClass) throws Exception {
            if (serviceClass.isAssignableFrom(providerClass)) {
                return providerClass.newInstance();
            }
            return null;
        }
    }
}
