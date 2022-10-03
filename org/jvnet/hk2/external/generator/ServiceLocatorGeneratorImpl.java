package org.jvnet.hk2.external.generator;

import org.jvnet.hk2.internal.InstantiationServiceImpl;
import org.jvnet.hk2.internal.ServiceLocatorRuntimeImpl;
import org.jvnet.hk2.internal.DefaultClassAnalyzer;
import javax.inject.Singleton;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.jvnet.hk2.internal.DynamicConfigurationServiceImpl;
import org.glassfish.hk2.api.Descriptor;
import org.jvnet.hk2.internal.Utilities;
import org.jvnet.hk2.internal.DynamicConfigurationImpl;
import org.jvnet.hk2.internal.ServiceLocatorImpl;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;

public class ServiceLocatorGeneratorImpl implements ServiceLocatorGenerator
{
    private ServiceLocatorImpl initialize(final String name, final ServiceLocator parent) {
        if (parent != null && !(parent instanceof ServiceLocatorImpl)) {
            throw new AssertionError((Object)("parent must be a " + ServiceLocatorImpl.class.getName() + " instead it is a " + parent.getClass().getName()));
        }
        final ServiceLocatorImpl sli = new ServiceLocatorImpl(name, (ServiceLocatorImpl)parent);
        final DynamicConfigurationImpl dci = new DynamicConfigurationImpl(sli);
        dci.bind((Descriptor)Utilities.getLocatorDescriptor((ServiceLocator)sli));
        dci.addActiveDescriptor(Utilities.getThreeThirtyDescriptor(sli));
        dci.bind((Descriptor)BuilderHelper.link((Class)DynamicConfigurationServiceImpl.class, false).to((Class)DynamicConfigurationService.class).in(Singleton.class.getName()).localOnly().build());
        dci.bind((Descriptor)BuilderHelper.createConstantDescriptor((Object)new DefaultClassAnalyzer(sli)));
        dci.bind((Descriptor)BuilderHelper.createDescriptorFromClass((Class)ServiceLocatorRuntimeImpl.class));
        dci.bind((Descriptor)BuilderHelper.createConstantDescriptor((Object)new InstantiationServiceImpl()));
        dci.commit();
        return sli;
    }
    
    public ServiceLocator create(final String name, final ServiceLocator parent) {
        final ServiceLocatorImpl retVal = this.initialize(name, parent);
        return (ServiceLocator)retVal;
    }
    
    @Override
    public String toString() {
        return "ServiceLocatorGeneratorImpl(hk2-locator, " + System.identityHashCode(this) + ")";
    }
}
