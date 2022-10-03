package org.glassfish.hk2.api;

import java.util.Set;

public interface ServiceLocatorListener
{
    void initialize(final Set<ServiceLocator> p0);
    
    void locatorAdded(final ServiceLocator p0);
    
    void locatorDestroyed(final ServiceLocator p0);
}
