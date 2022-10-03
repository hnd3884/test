package org.glassfish.jersey.internal.spi;

import javax.ws.rs.core.FeatureContext;

public interface AutoDiscoverable
{
    public static final int DEFAULT_PRIORITY = 2000;
    
    void configure(final FeatureContext p0);
}
