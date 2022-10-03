package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.DynamicConfiguration;
import javax.inject.Inject;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.Populator;
import javax.inject.Singleton;
import org.glassfish.hk2.api.DynamicConfigurationService;

@Singleton
public class DynamicConfigurationServiceImpl implements DynamicConfigurationService
{
    private final ServiceLocatorImpl locator;
    private final Populator populator;
    
    @Inject
    private DynamicConfigurationServiceImpl(final ServiceLocator locator) {
        this.locator = (ServiceLocatorImpl)locator;
        this.populator = (Populator)new PopulatorImpl(locator, (DynamicConfigurationService)this);
    }
    
    public DynamicConfiguration createDynamicConfiguration() {
        return (DynamicConfiguration)new DynamicConfigurationImpl(this.locator);
    }
    
    public Populator getPopulator() {
        return this.populator;
    }
}
