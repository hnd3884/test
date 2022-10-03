package org.jvnet.hk2.internal;

import java.util.Collection;
import org.glassfish.hk2.utilities.reflection.Pretty;
import java.util.HashSet;
import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.ServiceHandle;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.InstantiationService;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.utilities.general.ThreadSpecificObject;
import java.util.concurrent.ConcurrentHashMap;

public class FactoryCreator<T> implements Creator<T>
{
    private static final Object MAP_VALUE;
    private final ConcurrentHashMap<ThreadSpecificObject<ActiveDescriptor<?>>, Object> cycleFinder;
    private final ServiceLocator locator;
    private final ActiveDescriptor<?> factoryDescriptor;
    private final InstantiationServiceImpl instantiationService;
    
    FactoryCreator(final ServiceLocator locator, ActiveDescriptor<?> factoryDescriptor) {
        this.cycleFinder = new ConcurrentHashMap<ThreadSpecificObject<ActiveDescriptor<?>>, Object>();
        this.locator = locator;
        this.factoryDescriptor = factoryDescriptor;
        if (!factoryDescriptor.isReified()) {
            factoryDescriptor = (ActiveDescriptor<?>)locator.reifyDescriptor((Descriptor)factoryDescriptor);
        }
        InstantiationServiceImpl found = null;
        for (final Injectee factoryInjectee : factoryDescriptor.getInjectees()) {
            if (InstantiationService.class.equals(factoryInjectee.getRequiredType())) {
                found = (InstantiationServiceImpl)locator.getService((Class)InstantiationServiceImpl.class, new Annotation[0]);
                break;
            }
        }
        this.instantiationService = found;
    }
    
    @Override
    public List<Injectee> getInjectees() {
        return Collections.emptyList();
    }
    
    private ServiceHandle<Factory<T>> getFactoryHandle() {
        try {
            return (ServiceHandle<Factory<T>>)this.locator.getServiceHandle((ActiveDescriptor)this.factoryDescriptor);
        }
        catch (final Throwable th) {
            throw new MultiException(th);
        }
    }
    
    @Override
    public T create(final ServiceHandle<?> root, final SystemDescriptor<?> eventThrower) throws MultiException {
        final ServiceHandle<Factory<T>> handle = this.getFactoryHandle();
        eventThrower.invokeInstanceListeners((InstanceLifecycleEvent)new InstanceLifecycleEventImpl(InstanceLifecycleEventType.PRE_PRODUCTION, null, (ActiveDescriptor<?>)eventThrower));
        final ThreadSpecificObject<ActiveDescriptor<?>> tso = (ThreadSpecificObject<ActiveDescriptor<?>>)new ThreadSpecificObject((Object)handle.getActiveDescriptor());
        if (this.cycleFinder.containsKey(tso)) {
            final HashSet<String> impls = new HashSet<String>();
            for (final ThreadSpecificObject<ActiveDescriptor<?>> candidate : this.cycleFinder.keySet()) {
                if (candidate.getThreadIdentifier() != tso.getThreadIdentifier()) {
                    continue;
                }
                impls.add(((ActiveDescriptor)candidate.getIncomingObject()).getImplementation());
            }
            throw new AssertionError((Object)("A cycle was detected involving these Factory implementations: " + Pretty.collection((Collection)impls)));
        }
        this.cycleFinder.put(tso, FactoryCreator.MAP_VALUE);
        Factory<T> retValFactory;
        try {
            retValFactory = (Factory<T>)handle.getService();
        }
        finally {
            this.cycleFinder.remove(tso);
        }
        if (this.instantiationService != null) {
            Injectee parentInjectee = null;
            if (root != null && root instanceof ServiceHandleImpl) {
                parentInjectee = ((ServiceHandleImpl)root).getOriginalRequest();
            }
            this.instantiationService.pushInjecteeParent(parentInjectee);
        }
        T retVal;
        try {
            retVal = (T)retValFactory.provide();
        }
        finally {
            if (this.instantiationService != null) {
                this.instantiationService.popInjecteeParent();
            }
        }
        eventThrower.invokeInstanceListeners((InstanceLifecycleEvent)new InstanceLifecycleEventImpl(InstanceLifecycleEventType.POST_PRODUCTION, retVal, (ActiveDescriptor<?>)eventThrower));
        return retVal;
    }
    
    @Override
    public void dispose(final T instance) {
        try {
            final ServiceHandle<Factory<T>> handle = this.getFactoryHandle();
            final Factory<T> factory = (Factory<T>)handle.getService();
            factory.dispose((Object)instance);
        }
        catch (final Throwable th) {
            if (th instanceof MultiException) {
                throw (MultiException)th;
            }
            throw new MultiException(th);
        }
    }
    
    @Override
    public String toString() {
        return "FactoryCreator(" + this.locator + "," + this.factoryDescriptor + "," + System.identityHashCode(this) + ")";
    }
    
    static {
        MAP_VALUE = new Object();
    }
}
