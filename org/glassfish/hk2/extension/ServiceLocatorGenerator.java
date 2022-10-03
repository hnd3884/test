package org.glassfish.hk2.extension;

import org.glassfish.hk2.api.ServiceLocator;

public interface ServiceLocatorGenerator
{
    ServiceLocator create(final String p0, final ServiceLocator p1);
}
