package org.glassfish.jersey.inject.hk2;

import org.glassfish.hk2.api.ServiceLocator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Bindings;
import java.util.Iterator;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import javax.inject.Singleton;
import org.glassfish.jersey.internal.inject.Binding;
import java.util.ArrayList;
import org.glassfish.hk2.utilities.Binder;
import java.util.List;
import org.glassfish.jersey.internal.inject.AbstractBinder;

public class DelayedHk2InjectionManager extends AbstractHk2InjectionManager
{
    private final AbstractBinder bindings;
    private final List<Binder> providers;
    private boolean completed;
    
    DelayedHk2InjectionManager(final Object parent) {
        super(parent);
        this.bindings = new AbstractBinder() {
            protected void configure() {
            }
        };
        this.providers = new ArrayList<Binder>();
        this.completed = false;
    }
    
    public void register(final Binding binding) {
        if (this.completed && (binding.getScope() == Singleton.class || binding instanceof InstanceBinding)) {
            Hk2Helper.bind(this.getServiceLocator(), binding);
        }
        else {
            this.bindings.bind(binding);
        }
    }
    
    public void register(final Iterable<Binding> bindings) {
        for (final Binding binding : bindings) {
            this.bindings.bind(binding);
        }
    }
    
    public void register(final org.glassfish.jersey.internal.inject.Binder binder) {
        for (final Binding binding : Bindings.getBindings((InjectionManager)this, binder)) {
            this.bindings.bind(binding);
        }
    }
    
    public void register(final Object provider) throws IllegalArgumentException {
        if (this.isRegistrable(provider.getClass())) {
            this.providers.add((Binder)provider);
            return;
        }
        throw new IllegalArgumentException(LocalizationMessages.HK_2_PROVIDER_NOT_REGISTRABLE(provider.getClass()));
    }
    
    public void completeRegistration() throws IllegalStateException {
        Hk2Helper.bind(this, (org.glassfish.jersey.internal.inject.Binder)this.bindings);
        ServiceLocatorUtilities.bind(this.getServiceLocator(), (Binder[])this.providers.toArray(new Binder[0]));
        this.completed = true;
    }
}
