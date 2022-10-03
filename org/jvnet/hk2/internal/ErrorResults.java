package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ActiveDescriptor;

public class ErrorResults
{
    private final ActiveDescriptor<?> descriptor;
    private final Injectee injectee;
    private final MultiException me;
    
    ErrorResults(final ActiveDescriptor<?> descriptor, final Injectee injectee, final MultiException me) {
        this.descriptor = descriptor;
        this.injectee = injectee;
        this.me = me;
    }
    
    ActiveDescriptor<?> getDescriptor() {
        return this.descriptor;
    }
    
    Injectee getInjectee() {
        return this.injectee;
    }
    
    MultiException getMe() {
        return this.me;
    }
    
    @Override
    public String toString() {
        return "ErrorResult(" + this.descriptor + "," + this.injectee + "," + this.me + "," + System.identityHashCode(this) + ")";
    }
}
