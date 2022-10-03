package org.glassfish.hk2.osgiresourcelocator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;

public class Activator implements BundleActivator
{
    public void start(final BundleContext context) throws Exception {
        final ServiceLoaderImpl serviceLoader = new ServiceLoaderImpl();
        serviceLoader.trackBundles();
        ServiceLoader.initialize(serviceLoader);
        final ResourceFinderImpl resourceFinder = new ResourceFinderImpl();
        ResourceFinder.initialize(resourceFinder);
    }
    
    public void stop(final BundleContext context) throws Exception {
        ServiceLoader.reset();
    }
}
