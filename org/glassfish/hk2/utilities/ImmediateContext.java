package org.glassfish.hk2.utilities;

import java.util.LinkedHashSet;
import java.util.Collections;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import java.lang.annotation.Annotation;
import javax.inject.Inject;
import org.glassfish.hk2.internal.ImmediateLocalLocatorFilter;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.internal.HandleAndService;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.util.HashMap;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Visibility;
import javax.inject.Singleton;
import org.glassfish.hk2.api.Immediate;
import org.glassfish.hk2.api.Context;

@Singleton
@Visibility(DescriptorVisibility.LOCAL)
public class ImmediateContext implements Context<Immediate>
{
    private final HashMap<ActiveDescriptor<?>, HandleAndService> currentImmediateServices;
    private final HashMap<ActiveDescriptor<?>, Long> creating;
    private final ServiceLocator locator;
    private final Filter validationFilter;
    
    @Inject
    private ImmediateContext(final ServiceLocator locator) {
        this.currentImmediateServices = new HashMap<ActiveDescriptor<?>, HandleAndService>();
        this.creating = new HashMap<ActiveDescriptor<?>, Long>();
        this.locator = locator;
        this.validationFilter = new ImmediateLocalLocatorFilter(locator.getLocatorId());
    }
    
    @Override
    public Class<? extends Annotation> getScope() {
        return Immediate.class;
    }
    
    @Override
    public <U> U findOrCreate(final ActiveDescriptor<U> activeDescriptor, final ServiceHandle<?> root) {
        U retVal = null;
        synchronized (this) {
            HandleAndService has = this.currentImmediateServices.get(activeDescriptor);
            if (has != null) {
                return (U)has.getService();
            }
            while (this.creating.containsKey(activeDescriptor)) {
                final long alreadyCreatingThread = this.creating.get(activeDescriptor);
                if (alreadyCreatingThread == Thread.currentThread().getId()) {
                    throw new MultiException(new IllegalStateException("A circular dependency involving Immediate service " + activeDescriptor.getImplementation() + " was found.  Full descriptor is " + activeDescriptor));
                }
                try {
                    this.wait();
                }
                catch (final InterruptedException ie) {
                    throw new MultiException(ie);
                }
            }
            has = this.currentImmediateServices.get(activeDescriptor);
            if (has != null) {
                return (U)has.getService();
            }
            this.creating.put(activeDescriptor, Thread.currentThread().getId());
        }
        try {
            retVal = activeDescriptor.create(root);
        }
        finally {
            synchronized (this) {
                ServiceHandle<?> discoveredRoot = null;
                if (root != null && root.getActiveDescriptor().equals(activeDescriptor)) {
                    discoveredRoot = root;
                }
                if (retVal != null) {
                    this.currentImmediateServices.put(activeDescriptor, new HandleAndService(discoveredRoot, retVal));
                }
                this.creating.remove(activeDescriptor);
                this.notifyAll();
            }
        }
        return retVal;
    }
    
    @Override
    public synchronized boolean containsKey(final ActiveDescriptor<?> descriptor) {
        return this.currentImmediateServices.containsKey(descriptor);
    }
    
    @Override
    public void destroyOne(final ActiveDescriptor<?> descriptor) {
        this.destroyOne(descriptor, null);
    }
    
    private void destroyOne(final ActiveDescriptor<?> descriptor, List<ImmediateErrorHandler> errorHandlers) {
        if (errorHandlers == null) {
            errorHandlers = this.locator.getAllServices(ImmediateErrorHandler.class, new Annotation[0]);
        }
        synchronized (this) {
            final HandleAndService has = this.currentImmediateServices.remove(descriptor);
            final Object instance = has.getService();
            try {
                descriptor.dispose(instance);
            }
            catch (final Throwable th) {
                for (final ImmediateErrorHandler ieh : errorHandlers) {
                    try {
                        ieh.preDestroyFailed(descriptor, th);
                    }
                    catch (final Throwable t) {}
                }
            }
        }
    }
    
    @Override
    public boolean supportsNullCreation() {
        return false;
    }
    
    @Override
    public boolean isActive() {
        return true;
    }
    
    @Override
    public void shutdown() {
        final List<ImmediateErrorHandler> errorHandlers = this.locator.getAllServices(ImmediateErrorHandler.class, new Annotation[0]);
        synchronized (this) {
            for (final Map.Entry<ActiveDescriptor<?>, HandleAndService> entry : new HashSet(this.currentImmediateServices.entrySet())) {
                final HandleAndService has = entry.getValue();
                final ServiceHandle<?> handle = has.getHandle();
                if (handle != null) {
                    handle.destroy();
                }
                else {
                    this.destroyOne(entry.getKey(), errorHandlers);
                }
            }
        }
    }
    
    private List<ActiveDescriptor<?>> getImmediateServices() {
        List<ActiveDescriptor<?>> inScopeAndInThisLocator;
        try {
            inScopeAndInThisLocator = this.locator.getDescriptors(this.validationFilter);
        }
        catch (final IllegalStateException ise) {
            inScopeAndInThisLocator = Collections.emptyList();
        }
        return inScopeAndInThisLocator;
    }
    
    public Filter getValidationFilter() {
        return this.validationFilter;
    }
    
    public void doWork() {
        final List<ActiveDescriptor<?>> inScopeAndInThisLocator = this.getImmediateServices();
        List<ImmediateErrorHandler> errorHandlers;
        try {
            errorHandlers = this.locator.getAllServices(ImmediateErrorHandler.class, new Annotation[0]);
        }
        catch (final IllegalStateException ise) {
            return;
        }
        final LinkedHashSet<ActiveDescriptor<?>> newFullSet = new LinkedHashSet<ActiveDescriptor<?>>(inScopeAndInThisLocator);
        final LinkedHashSet<ActiveDescriptor<?>> addMe = new LinkedHashSet<ActiveDescriptor<?>>();
        synchronized (this) {
            while (this.creating.size() > 0) {
                try {
                    this.wait();
                    continue;
                }
                catch (final InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
                break;
            }
            final LinkedHashSet<ActiveDescriptor<?>> oldSet = new LinkedHashSet<ActiveDescriptor<?>>(this.currentImmediateServices.keySet());
            for (final ActiveDescriptor<?> ad : inScopeAndInThisLocator) {
                if (!oldSet.contains(ad)) {
                    addMe.add(ad);
                }
            }
            oldSet.removeAll(newFullSet);
            for (final ActiveDescriptor<?> gone : oldSet) {
                final HandleAndService has = this.currentImmediateServices.get(gone);
                final ServiceHandle<?> handle = has.getHandle();
                if (handle != null) {
                    handle.destroy();
                }
                else {
                    this.destroyOne(gone, errorHandlers);
                }
            }
        }
        for (final ActiveDescriptor<?> ad2 : addMe) {
            try {
                this.locator.getServiceHandle(ad2).getService();
            }
            catch (final Throwable th) {
                for (final ImmediateErrorHandler ieh : errorHandlers) {
                    try {
                        ieh.postConstructFailed(ad2, th);
                    }
                    catch (final Throwable t) {}
                }
            }
        }
    }
}
