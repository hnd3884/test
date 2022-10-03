package org.jvnet.hk2.internal;

import java.util.Iterator;
import java.util.List;
import org.glassfish.hk2.api.PerLookup;
import java.util.Collection;
import java.util.ArrayList;
import org.glassfish.hk2.api.Context;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.api.Injectee;
import java.util.LinkedList;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;

public class ServiceHandleImpl<T> implements ServiceHandle<T>
{
    private ActiveDescriptor<T> root;
    private final ServiceLocatorImpl locator;
    private final LinkedList<Injectee> injectees;
    private final Object lock;
    private boolean serviceDestroyed;
    private boolean serviceSet;
    private T service;
    private Object serviceData;
    private final LinkedList<ServiceHandleImpl<?>> subHandles;
    
    ServiceHandleImpl(final ServiceLocatorImpl locator, final ActiveDescriptor<T> root, final Injectee injectee) {
        this.injectees = new LinkedList<Injectee>();
        this.lock = new Object();
        this.serviceDestroyed = false;
        this.serviceSet = false;
        this.subHandles = new LinkedList<ServiceHandleImpl<?>>();
        this.root = root;
        this.locator = locator;
        if (injectee != null) {
            this.injectees.add(injectee);
        }
    }
    
    public T getService() {
        return this.getService((ServiceHandle<T>)this);
    }
    
    private Injectee getLastInjectee() {
        synchronized (this.lock) {
            return this.injectees.isEmpty() ? null : this.injectees.getLast();
        }
    }
    
    T getService(final ServiceHandle<T> handle) {
        if (this.root instanceof Closeable) {
            final Closeable closeable = (Closeable)this.root;
            if (closeable.isClosed()) {
                throw new IllegalStateException("This service has been unbound: " + this.root);
            }
        }
        synchronized (this.lock) {
            if (this.serviceDestroyed) {
                throw new IllegalStateException("Service has been disposed");
            }
            if (this.serviceSet) {
                return this.service;
            }
            final Injectee injectee = this.getLastInjectee();
            final Class<?> requiredClass = (injectee == null) ? null : ReflectionHelper.getRawClass(injectee.getRequiredType());
            this.service = Utilities.createService(this.root, injectee, this.locator, handle, requiredClass);
            this.serviceSet = true;
            return this.service;
        }
    }
    
    public ActiveDescriptor<T> getActiveDescriptor() {
        return this.root;
    }
    
    public boolean isActive() {
        if (this.serviceDestroyed) {
            return false;
        }
        if (this.serviceSet) {
            return true;
        }
        try {
            final Context<?> context = this.locator.resolveContext(this.root.getScopeAnnotation());
            return context.containsKey((ActiveDescriptor)this.root);
        }
        catch (final IllegalStateException ise) {
            return false;
        }
    }
    
    public void destroy() {
        if (!this.root.isReified()) {
            return;
        }
        final boolean serviceActive;
        final boolean localServiceSet;
        final List<ServiceHandleImpl<?>> localSubHandles;
        synchronized (this.lock) {
            serviceActive = this.isActive();
            if (this.serviceDestroyed) {
                return;
            }
            this.serviceDestroyed = true;
            localServiceSet = this.serviceSet;
            localSubHandles = new ArrayList<ServiceHandleImpl<?>>(this.subHandles);
            this.subHandles.clear();
        }
        if (this.root.getScopeAnnotation().equals(PerLookup.class)) {
            if (localServiceSet) {
                this.root.dispose((Object)this.service);
            }
        }
        else if (serviceActive) {
            Context<?> context;
            try {
                context = this.locator.resolveContext(this.root.getScopeAnnotation());
            }
            catch (final Throwable th) {
                return;
            }
            context.destroyOne((ActiveDescriptor)this.root);
        }
        for (final ServiceHandleImpl<?> subHandle : localSubHandles) {
            subHandle.destroy();
        }
    }
    
    public void setServiceData(final Object serviceData) {
        synchronized (this.lock) {
            this.serviceData = serviceData;
        }
    }
    
    public Object getServiceData() {
        synchronized (this.lock) {
            return this.serviceData;
        }
    }
    
    public List<ServiceHandle<?>> getSubHandles() {
        synchronized (this.lock) {
            return new ArrayList<ServiceHandle<?>>((Collection<? extends ServiceHandle<?>>)this.subHandles);
        }
    }
    
    public void pushInjectee(final Injectee push) {
        synchronized (this.lock) {
            this.injectees.add(push);
        }
    }
    
    public void popInjectee() {
        synchronized (this.lock) {
            this.injectees.removeLast();
        }
    }
    
    public void addSubHandle(final ServiceHandleImpl<?> subHandle) {
        synchronized (this.lock) {
            this.subHandles.add(subHandle);
        }
    }
    
    public Injectee getOriginalRequest() {
        final Injectee injectee = this.getLastInjectee();
        return injectee;
    }
    
    @Override
    public String toString() {
        return "ServiceHandle(" + this.root + "," + System.identityHashCode(this) + ")";
    }
}
