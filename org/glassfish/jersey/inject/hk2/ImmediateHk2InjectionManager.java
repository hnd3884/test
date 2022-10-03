package org.glassfish.jersey.inject.hk2;

import org.glassfish.hk2.api.ServiceLocator;
import java.lang.annotation.Annotation;
import java.util.List;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.inject.ForeignDescriptor;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.Binding;

public class ImmediateHk2InjectionManager extends AbstractHk2InjectionManager
{
    ImmediateHk2InjectionManager(final Object parent) {
        super(parent);
    }
    
    public void completeRegistration() throws IllegalStateException {
    }
    
    public void register(final Binding binding) {
        Hk2Helper.bind(this.getServiceLocator(), binding);
    }
    
    public void register(final Iterable<Binding> descriptors) {
        Hk2Helper.bind(this.getServiceLocator(), descriptors);
    }
    
    public void register(final Binder binder) {
        Hk2Helper.bind(this, binder);
    }
    
    public void register(final Object provider) {
        if (this.isRegistrable(provider.getClass())) {
            ServiceLocatorUtilities.bind(this.getServiceLocator(), new org.glassfish.hk2.utilities.Binder[] { (org.glassfish.hk2.utilities.Binder)provider });
            return;
        }
        throw new IllegalArgumentException(LocalizationMessages.HK_2_PROVIDER_NOT_REGISTRABLE(provider.getClass()));
    }
}
