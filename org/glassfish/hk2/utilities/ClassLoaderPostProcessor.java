package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.PopulatorPostProcessor;

public class ClassLoaderPostProcessor implements PopulatorPostProcessor
{
    private final HK2Loader loader;
    private final boolean force;
    
    public ClassLoaderPostProcessor(final ClassLoader classloader, final boolean force) {
        this.loader = new HK2LoaderImpl(classloader);
        this.force = force;
    }
    
    public ClassLoaderPostProcessor(final ClassLoader classloader) {
        this(classloader, false);
    }
    
    @Override
    public DescriptorImpl process(final ServiceLocator serviceLocator, final DescriptorImpl descriptorImpl) {
        if (this.force) {
            descriptorImpl.setLoader(this.loader);
            return descriptorImpl;
        }
        if (descriptorImpl.getLoader() != null) {
            return descriptorImpl;
        }
        descriptorImpl.setLoader(this.loader);
        return descriptorImpl;
    }
}
