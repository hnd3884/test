package org.jvnet.hk2.internal;

import java.io.Serializable;
import org.glassfish.hk2.utilities.reflection.Logger;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import java.util.TreeSet;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.api.ServiceHandle;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.utilities.cache.ComputationErrorException;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.utilities.cache.Computable;
import org.glassfish.hk2.utilities.ContextualInput;
import org.glassfish.hk2.utilities.cache.Cache;
import javax.inject.Singleton;
import org.glassfish.hk2.api.Context;

@Singleton
public class SingletonContext implements Context<Singleton>
{
    private int generationNumber;
    private final ServiceLocatorImpl locator;
    private final Cache<ContextualInput<Object>, Object> valueCache;
    
    SingletonContext(final ServiceLocatorImpl impl) {
        this.generationNumber = Integer.MIN_VALUE;
        this.valueCache = (Cache<ContextualInput<Object>, Object>)new Cache((Computable)new Computable<ContextualInput<Object>, Object>() {
            public Object compute(final ContextualInput<Object> a) {
                final ActiveDescriptor<Object> activeDescriptor = (ActiveDescriptor<Object>)a.getDescriptor();
                final Object cachedVal = activeDescriptor.getCache();
                if (cachedVal != null) {
                    return cachedVal;
                }
                final Object createdVal = activeDescriptor.create(a.getRoot());
                activeDescriptor.setCache(createdVal);
                if (activeDescriptor instanceof SystemDescriptor) {
                    ((SystemDescriptor)activeDescriptor).setSingletonGeneration(SingletonContext.this.generationNumber++);
                }
                return createdVal;
            }
        }, (Cache.CycleHandler)new Cache.CycleHandler<ContextualInput<Object>>() {
            public void handleCycle(final ContextualInput<Object> key) {
                throw new MultiException((Throwable)new IllegalStateException("A circular dependency involving Singleton service " + key.getDescriptor().getImplementation() + " was found.  Full descriptor is " + key.getDescriptor()));
            }
        });
        this.locator = impl;
    }
    
    public Class<? extends Annotation> getScope() {
        return (Class<? extends Annotation>)Singleton.class;
    }
    
    public <T> T findOrCreate(final ActiveDescriptor<T> activeDescriptor, final ServiceHandle<?> root) {
        try {
            return (T)this.valueCache.compute((Object)new ContextualInput((ActiveDescriptor)activeDescriptor, (ServiceHandle)root));
        }
        catch (final Throwable th) {
            if (th instanceof MultiException) {
                throw (MultiException)th;
            }
            throw new MultiException(th);
        }
    }
    
    public boolean containsKey(final ActiveDescriptor<?> descriptor) {
        return this.valueCache.containsKey((Object)new ContextualInput((ActiveDescriptor)descriptor, (ServiceHandle)null));
    }
    
    public boolean isActive() {
        return true;
    }
    
    public boolean supportsNullCreation() {
        return false;
    }
    
    public void shutdown() {
        final List<ActiveDescriptor<?>> all = this.locator.getDescriptors(BuilderHelper.allFilter());
        final long myLocatorId = this.locator.getLocatorId();
        final TreeSet<SystemDescriptor<Object>> singlesOnly = new TreeSet<SystemDescriptor<Object>>(new GenerationComparator());
        for (final ActiveDescriptor<?> one : all) {
            if (one.getScope() != null) {
                if (!one.getScope().equals(Singleton.class.getName())) {
                    continue;
                }
                synchronized (this) {
                    if (one.getCache() == null) {
                        continue;
                    }
                }
                if (one.getLocatorId() == null) {
                    continue;
                }
                if (one.getLocatorId() != myLocatorId) {
                    continue;
                }
                final SystemDescriptor<Object> oneAsObject = (SystemDescriptor)one;
                singlesOnly.add(oneAsObject);
            }
        }
        for (final SystemDescriptor<Object> one2 : singlesOnly) {
            this.destroyOne((ActiveDescriptor<?>)one2);
        }
    }
    
    public void destroyOne(final ActiveDescriptor<?> one) {
        this.valueCache.remove((Object)new ContextualInput((ActiveDescriptor)one, (ServiceHandle)null));
        final Object value = one.getCache();
        one.releaseCache();
        if (value == null) {
            return;
        }
        try {
            one.dispose(value);
        }
        catch (final Throwable th) {
            Logger.getLogger().debug("SingletonContext", "releaseOne", th);
        }
    }
    
    private static class GenerationComparator implements Comparator<SystemDescriptor<Object>>, Serializable
    {
        private static final long serialVersionUID = -6931828935035131179L;
        
        @Override
        public int compare(final SystemDescriptor<Object> o1, final SystemDescriptor<Object> o2) {
            if (o1.getSingletonGeneration() > o2.getSingletonGeneration()) {
                return -1;
            }
            if (o1.getSingletonGeneration() == o2.getSingletonGeneration()) {
                return 0;
            }
            return 1;
        }
    }
}
