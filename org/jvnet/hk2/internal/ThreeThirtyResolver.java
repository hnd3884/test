package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.UnsatisfiedDependencyException;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Injectee;
import javax.inject.Named;
import javax.inject.Inject;
import org.glassfish.hk2.api.InjectionResolver;

@Named("SystemInjectResolver")
public class ThreeThirtyResolver implements InjectionResolver<Inject>
{
    private final ServiceLocatorImpl locator;
    
    ThreeThirtyResolver(final ServiceLocatorImpl locator) {
        this.locator = locator;
    }
    
    public Object resolve(final Injectee injectee, final ServiceHandle<?> root) {
        final ActiveDescriptor<?> ad = this.locator.getInjecteeDescriptor(injectee);
        if (ad != null) {
            return this.locator.getService(ad, root, injectee);
        }
        if (injectee.isOptional()) {
            return null;
        }
        throw new MultiException((Throwable)new UnsatisfiedDependencyException(injectee));
    }
    
    public boolean isConstructorParameterIndicator() {
        return false;
    }
    
    public boolean isMethodParameterIndicator() {
        return false;
    }
    
    @Override
    public String toString() {
        return "ThreeThirtyResolver(" + this.locator + "," + System.identityHashCode(this) + ")";
    }
}
