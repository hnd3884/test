package org.jvnet.hk2.internal;

import java.util.Collection;
import org.glassfish.hk2.utilities.reflection.Pretty;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.FactoryDescriptorsImpl;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.FactoryDescriptors;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.TwoPhaseResource;
import org.glassfish.hk2.api.Filter;
import java.util.LinkedList;
import org.glassfish.hk2.api.DynamicConfiguration;

public class DynamicConfigurationImpl implements DynamicConfiguration
{
    private final ServiceLocatorImpl locator;
    private final LinkedList<SystemDescriptor<?>> allDescriptors;
    private final LinkedList<Filter> allUnbindFilters;
    private final LinkedList<Filter> allIdempotentFilters;
    private final LinkedList<TwoPhaseResource> allResources;
    private final Object lock;
    private boolean committed;
    
    public DynamicConfigurationImpl(final ServiceLocatorImpl locator) {
        this.allDescriptors = new LinkedList<SystemDescriptor<?>>();
        this.allUnbindFilters = new LinkedList<Filter>();
        this.allIdempotentFilters = new LinkedList<Filter>();
        this.allResources = new LinkedList<TwoPhaseResource>();
        this.lock = new Object();
        this.committed = false;
        this.locator = locator;
    }
    
    public <T> ActiveDescriptor<T> bind(final Descriptor key) {
        return this.bind(key, true);
    }
    
    public <T> ActiveDescriptor<T> bind(final Descriptor key, final boolean requiresDeepCopy) {
        this.checkState();
        checkDescriptor(key);
        final SystemDescriptor<T> sd = new SystemDescriptor<T>(key, requiresDeepCopy, this.locator, this.locator.getNextServiceId());
        this.allDescriptors.add(sd);
        return (ActiveDescriptor<T>)sd;
    }
    
    public FactoryDescriptors bind(final FactoryDescriptors factoryDescriptors) {
        return this.bind(factoryDescriptors, true);
    }
    
    public FactoryDescriptors bind(final FactoryDescriptors factoryDescriptors, final boolean requiresDeepCopy) {
        if (factoryDescriptors == null) {
            throw new IllegalArgumentException("factoryDescriptors is null");
        }
        final Descriptor asService = factoryDescriptors.getFactoryAsAService();
        final Descriptor asFactory = factoryDescriptors.getFactoryAsAFactory();
        checkDescriptor(asService);
        checkDescriptor(asFactory);
        final String implClassService = asService.getImplementation();
        final String implClassFactory = asFactory.getImplementation();
        if (!implClassService.equals(implClassFactory)) {
            throw new IllegalArgumentException("The implementation classes must match (" + implClassService + "/" + implClassFactory + ")");
        }
        if (!asService.getDescriptorType().equals((Object)DescriptorType.CLASS)) {
            throw new IllegalArgumentException("The getFactoryAsService descriptor must be of type CLASS");
        }
        if (!asFactory.getDescriptorType().equals((Object)DescriptorType.PROVIDE_METHOD)) {
            throw new IllegalArgumentException("The getFactoryAsFactory descriptor must be of type PROVIDE_METHOD");
        }
        final SystemDescriptor<?> boundAsService = new SystemDescriptor<Object>(asService, requiresDeepCopy, this.locator, this.locator.getNextServiceId());
        final SystemDescriptor<?> boundAsFactory = new SystemDescriptor<Object>(asFactory, requiresDeepCopy, this.locator, this.locator.getNextServiceId());
        if (asService instanceof ActiveDescriptor) {
            boundAsFactory.setFactoryIds(boundAsService.getLocatorId(), boundAsService.getServiceId());
        }
        this.allDescriptors.add(boundAsFactory);
        this.allDescriptors.add(boundAsService);
        return (FactoryDescriptors)new FactoryDescriptorsImpl((Descriptor)boundAsService, (Descriptor)boundAsFactory);
    }
    
    public <T> ActiveDescriptor<T> addActiveDescriptor(final ActiveDescriptor<T> activeDescriptor) throws IllegalArgumentException {
        return this.addActiveDescriptor(activeDescriptor, true);
    }
    
    public <T> ActiveDescriptor<T> addActiveDescriptor(final ActiveDescriptor<T> activeDescriptor, final boolean requiresDeepCopy) throws IllegalArgumentException {
        this.checkState();
        checkDescriptor((Descriptor)activeDescriptor);
        if (!activeDescriptor.isReified()) {
            throw new IllegalArgumentException();
        }
        checkReifiedDescriptor(activeDescriptor);
        final SystemDescriptor<T> retVal = new SystemDescriptor<T>((Descriptor)activeDescriptor, requiresDeepCopy, this.locator, this.locator.getNextServiceId());
        this.allDescriptors.add(retVal);
        return (ActiveDescriptor<T>)retVal;
    }
    
    public <T> ActiveDescriptor<T> addActiveDescriptor(final Class<T> rawClass) throws IllegalArgumentException {
        final AutoActiveDescriptor<T> ad = Utilities.createAutoDescriptor(rawClass, this.locator);
        checkReifiedDescriptor((ActiveDescriptor<?>)ad);
        final ActiveDescriptor<T> retVal = this.addActiveDescriptor((org.glassfish.hk2.api.ActiveDescriptor<T>)ad, false);
        ad.resetSelfDescriptor(retVal);
        return retVal;
    }
    
    public <T> FactoryDescriptors addActiveFactoryDescriptor(final Class<? extends Factory<T>> rawFactoryClass) throws MultiException, IllegalArgumentException {
        final Collector collector = new Collector();
        Utilities.checkFactoryType(rawFactoryClass, collector);
        collector.throwIfErrors();
        final ActiveDescriptor<?> factoryDescriptor = this.addActiveDescriptor((Class<?>)rawFactoryClass);
        final ActiveDescriptor<?> userMethodDescriptor = (ActiveDescriptor<?>)Utilities.createAutoFactoryDescriptor(rawFactoryClass, factoryDescriptor, this.locator);
        final ActiveDescriptor<?> methodDescriptor = this.addActiveDescriptor(userMethodDescriptor);
        return (FactoryDescriptors)new FactoryDescriptorsImpl((Descriptor)factoryDescriptor, (Descriptor)methodDescriptor);
    }
    
    public void addUnbindFilter(final Filter unbindFilter) throws IllegalArgumentException {
        if (unbindFilter == null) {
            throw new IllegalArgumentException();
        }
        this.checkState();
        this.allUnbindFilters.add(unbindFilter);
    }
    
    public void addIdempotentFilter(final Filter... idempotentFilter) throws IllegalArgumentException {
        if (idempotentFilter == null) {
            throw new IllegalArgumentException();
        }
        this.checkState();
        for (final Filter iFilter : idempotentFilter) {
            if (iFilter == null) {
                throw new IllegalArgumentException();
            }
        }
        for (final Filter iFilter : idempotentFilter) {
            this.allIdempotentFilters.add(iFilter);
        }
    }
    
    public void registerTwoPhaseResources(final TwoPhaseResource... resources) {
        this.checkState();
        if (resources == null) {
            return;
        }
        for (int lcv = 0; lcv < resources.length; ++lcv) {
            final TwoPhaseResource resource = resources[lcv];
            if (resource != null) {
                this.allResources.add(resource);
            }
        }
    }
    
    public void commit() throws MultiException {
        synchronized (this.lock) {
            this.checkState();
            this.committed = true;
        }
        this.locator.addConfiguration(this);
    }
    
    private void checkState() {
        synchronized (this.lock) {
            if (this.committed) {
                throw new IllegalStateException();
            }
        }
    }
    
    private static void checkDescriptor(final Descriptor d) {
        if (d == null) {
            throw new IllegalArgumentException();
        }
        if (d.getImplementation() == null) {
            throw new IllegalArgumentException();
        }
        if (d.getAdvertisedContracts() == null) {
            throw new IllegalArgumentException();
        }
        if (d.getDescriptorType() == null) {
            throw new IllegalArgumentException();
        }
        if (d.getDescriptorVisibility() == null) {
            throw new IllegalArgumentException();
        }
        if (d.getMetadata() == null) {
            throw new IllegalArgumentException();
        }
        if (d.getQualifiers() == null) {
            throw new IllegalArgumentException();
        }
    }
    
    private static void checkReifiedDescriptor(final ActiveDescriptor<?> d) {
        if (d.isProxiable() == null) {
            return;
        }
        if (!d.isProxiable()) {
            return;
        }
        if (Utilities.isUnproxiableScope(d.getScopeAnnotation())) {
            throw new IllegalArgumentException();
        }
    }
    
    LinkedList<SystemDescriptor<?>> getAllDescriptors() {
        return this.allDescriptors;
    }
    
    LinkedList<Filter> getUnbindFilters() {
        return this.allUnbindFilters;
    }
    
    LinkedList<Filter> getIdempotentFilters() {
        return this.allIdempotentFilters;
    }
    
    LinkedList<TwoPhaseResource> getResources() {
        return this.allResources;
    }
    
    @Override
    public String toString() {
        return "DynamicConfigurationImpl(" + this.locator + "," + Pretty.collection((Collection)this.allDescriptors) + "," + Pretty.collection((Collection)this.allUnbindFilters) + "," + Pretty.collection((Collection)this.allResources) + "," + System.identityHashCode(this) + ")";
    }
}
