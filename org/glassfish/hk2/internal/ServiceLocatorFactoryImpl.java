package org.glassfish.hk2.internal;

import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import org.glassfish.hk2.osgiresourcelocator.ServiceLoader;
import java.security.AccessController;
import org.glassfish.hk2.utilities.reflection.Logger;
import java.security.PrivilegedAction;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.api.ServiceLocatorListener;
import java.util.HashSet;
import org.glassfish.hk2.api.ServiceLocator;
import java.util.HashMap;
import org.glassfish.hk2.api.ServiceLocatorFactory;

public class ServiceLocatorFactoryImpl extends ServiceLocatorFactory
{
    private static final String DEBUG_SERVICE_LOCATOR_PROPERTY = "org.jvnet.hk2.properties.debug.service.locator.lifecycle";
    private static final boolean DEBUG_SERVICE_LOCATOR_LIFECYCLE;
    private static final Object sLock;
    private static int name_count;
    private static final String GENERATED_NAME_PREFIX = "__HK2_Generated_";
    private final Object lock;
    private final HashMap<String, ServiceLocator> serviceLocators;
    private final HashSet<ServiceLocatorListener> listeners;
    
    private static ServiceLocatorGenerator getGeneratorSecure() {
        return AccessController.doPrivileged((PrivilegedAction<ServiceLocatorGenerator>)new PrivilegedAction<ServiceLocatorGenerator>() {
            @Override
            public ServiceLocatorGenerator run() {
                try {
                    return getGenerator();
                }
                catch (final Throwable th) {
                    Logger.getLogger().warning("Error finding implementation of hk2:", th);
                    return null;
                }
            }
        });
    }
    
    public ServiceLocatorFactoryImpl() {
        this.lock = new Object();
        this.serviceLocators = new HashMap<String, ServiceLocator>();
        this.listeners = new HashSet<ServiceLocatorListener>();
    }
    
    private static Iterable<? extends ServiceLocatorGenerator> getOSGiSafeGenerators() {
        try {
            return ServiceLoader.lookupProviderInstances((Class)ServiceLocatorGenerator.class);
        }
        catch (final Throwable th) {
            return null;
        }
    }
    
    private static ServiceLocatorGenerator getGenerator() {
        final Iterable<? extends ServiceLocatorGenerator> generators = getOSGiSafeGenerators();
        if (generators != null) {
            final Iterator<? extends ServiceLocatorGenerator> iterator = generators.iterator();
            return iterator.hasNext() ? ((ServiceLocatorGenerator)iterator.next()) : null;
        }
        final ClassLoader classLoader = ServiceLocatorFactoryImpl.class.getClassLoader();
        final Iterator<ServiceLocatorGenerator> providers = java.util.ServiceLoader.load(ServiceLocatorGenerator.class, classLoader).iterator();
        while (providers.hasNext()) {
            try {
                return providers.next();
            }
            catch (final ServiceConfigurationError sce) {
                Logger.getLogger().debug("ServiceLocatorFactoryImpl", "getGenerator", (Throwable)sce);
                continue;
            }
            break;
        }
        Logger.getLogger().warning("Cannot find a default implementation of the HK2 ServiceLocatorGenerator");
        return null;
    }
    
    @Override
    public ServiceLocator create(final String name) {
        return this.create(name, null, null, CreatePolicy.RETURN);
    }
    
    @Override
    public ServiceLocator find(final String name) {
        synchronized (this.lock) {
            return this.serviceLocators.get(name);
        }
    }
    
    @Override
    public void destroy(final String name) {
        this.destroy(name, null);
    }
    
    private void destroy(final String name, final ServiceLocator locator) {
        ServiceLocator killMe = null;
        synchronized (this.lock) {
            if (name != null) {
                killMe = this.serviceLocators.remove(name);
            }
            if (ServiceLocatorFactoryImpl.DEBUG_SERVICE_LOCATOR_LIFECYCLE) {
                Logger.getLogger().debug("ServiceFactoryImpl destroying locator with name " + name + " and locator " + locator + " with found locator " + killMe, new Throwable());
            }
            if (killMe == null) {
                killMe = locator;
            }
            if (killMe != null) {
                for (final ServiceLocatorListener listener : this.listeners) {
                    try {
                        listener.locatorDestroyed(killMe);
                    }
                    catch (final Throwable th) {
                        Logger.getLogger().debug(this.getClass().getName(), "destroy " + listener, th);
                    }
                }
            }
        }
        if (killMe != null) {
            killMe.shutdown();
        }
    }
    
    @Override
    public void destroy(final ServiceLocator locator) {
        if (locator == null) {
            return;
        }
        this.destroy(locator.getName(), locator);
    }
    
    @Override
    public ServiceLocator create(final String name, final ServiceLocator parent) {
        return this.create(name, parent, null, CreatePolicy.RETURN);
    }
    
    private static String getGeneratedName() {
        synchronized (ServiceLocatorFactoryImpl.sLock) {
            return "__HK2_Generated_" + ServiceLocatorFactoryImpl.name_count++;
        }
    }
    
    @Override
    public ServiceLocator create(final String name, final ServiceLocator parent, final ServiceLocatorGenerator generator) {
        return this.create(name, parent, generator, CreatePolicy.RETURN);
    }
    
    private void callListenerAdded(final ServiceLocator added) {
        for (final ServiceLocatorListener listener : this.listeners) {
            try {
                listener.locatorAdded(added);
            }
            catch (final Throwable th) {
                Logger.getLogger().debug(this.getClass().getName(), "create " + listener, th);
            }
        }
    }
    
    @Override
    public ServiceLocator create(String name, final ServiceLocator parent, final ServiceLocatorGenerator generator, final CreatePolicy policy) {
        if (ServiceLocatorFactoryImpl.DEBUG_SERVICE_LOCATOR_LIFECYCLE) {
            Logger.getLogger().debug("ServiceFactoryImpl given create of " + name + " with parent " + parent + " with generator " + generator + " and policy " + policy, new Throwable());
        }
        synchronized (this.lock) {
            if (name == null) {
                name = getGeneratedName();
                final ServiceLocator added = this.internalCreate(name, parent, generator);
                this.callListenerAdded(added);
                if (ServiceLocatorFactoryImpl.DEBUG_SERVICE_LOCATOR_LIFECYCLE) {
                    Logger.getLogger().debug("ServiceFactoryImpl added untracked listener " + added);
                }
                return added;
            }
            ServiceLocator retVal = this.serviceLocators.get(name);
            if (retVal != null) {
                if (policy == null || CreatePolicy.RETURN.equals(policy)) {
                    if (ServiceLocatorFactoryImpl.DEBUG_SERVICE_LOCATOR_LIFECYCLE) {
                        Logger.getLogger().debug("ServiceFactoryImpl added found listener under RETURN policy of " + retVal);
                    }
                    return retVal;
                }
                if (!policy.equals(CreatePolicy.DESTROY)) {
                    throw new IllegalStateException("A ServiceLocator named " + name + " already exists");
                }
                this.destroy(retVal);
            }
            retVal = this.internalCreate(name, parent, generator);
            this.serviceLocators.put(name, retVal);
            this.callListenerAdded(retVal);
            if (ServiceLocatorFactoryImpl.DEBUG_SERVICE_LOCATOR_LIFECYCLE) {
                Logger.getLogger().debug("ServiceFactoryImpl created locator " + retVal);
            }
            return retVal;
        }
    }
    
    private ServiceLocator internalCreate(final String name, final ServiceLocator parent, ServiceLocatorGenerator generator) {
        if (generator == null) {
            if (DefaultGeneratorInitializer.defaultGenerator == null) {
                throw new IllegalStateException("No generator was provided and there is no default generator registered");
            }
            generator = DefaultGeneratorInitializer.defaultGenerator;
        }
        return generator.create(name, parent);
    }
    
    @Override
    public void addListener(final ServiceLocatorListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        synchronized (this.lock) {
            if (this.listeners.contains(listener)) {
                return;
            }
            try {
                final HashSet<ServiceLocator> currentLocators = new HashSet<ServiceLocator>(this.serviceLocators.values());
                listener.initialize(Collections.unmodifiableSet((Set<? extends ServiceLocator>)currentLocators));
            }
            catch (final Throwable th) {
                Logger.getLogger().debug(this.getClass().getName(), "addListener " + listener, th);
                return;
            }
            this.listeners.add(listener);
        }
    }
    
    @Override
    public void removeListener(final ServiceLocatorListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException();
        }
        synchronized (this.lock) {
            this.listeners.remove(listener);
        }
    }
    
    static {
        DEBUG_SERVICE_LOCATOR_LIFECYCLE = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.parseBoolean(System.getProperty("org.jvnet.hk2.properties.debug.service.locator.lifecycle", "false"));
            }
        });
        sLock = new Object();
        ServiceLocatorFactoryImpl.name_count = 0;
    }
    
    private static final class DefaultGeneratorInitializer
    {
        private static final ServiceLocatorGenerator defaultGenerator;
        
        static {
            defaultGenerator = getGeneratorSecure();
        }
    }
}
