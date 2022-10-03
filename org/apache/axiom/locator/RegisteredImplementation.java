package org.apache.axiom.locator;

import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

class RegisteredImplementation
{
    private final Implementation implementation;
    private final ServiceRegistration registration;
    private final ServiceReference reference;
    
    RegisteredImplementation(final Implementation implementation, final ServiceRegistration registration, final ServiceReference reference) {
        this.implementation = implementation;
        this.registration = registration;
        this.reference = reference;
    }
    
    Implementation getImplementation() {
        return this.implementation;
    }
    
    ServiceRegistration getRegistration() {
        return this.registration;
    }
    
    ServiceReference getReference() {
        return this.reference;
    }
}
