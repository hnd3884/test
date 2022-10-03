package org.jvnet.hk2.external.runtime;

import org.glassfish.hk2.api.Descriptor;
import java.util.Collection;
import java.util.Arrays;
import org.jvnet.hk2.internal.InstantiationServiceImpl;
import org.jvnet.hk2.internal.ServiceLocatorRuntimeImpl;
import org.jvnet.hk2.internal.DefaultClassAnalyzer;
import org.jvnet.hk2.internal.DynamicConfigurationServiceImpl;
import org.jvnet.hk2.internal.ThreeThirtyResolver;
import org.jvnet.hk2.internal.ServiceLocatorImpl;
import java.util.HashSet;
import java.util.List;
import org.glassfish.hk2.api.Filter;

public class Hk2LocatorUtilities
{
    private static final Filter NO_INITIAL_SERVICES_FILTER;
    
    public static Filter getNoInitialServicesFilter() {
        return Hk2LocatorUtilities.NO_INITIAL_SERVICES_FILTER;
    }
    
    static {
        NO_INITIAL_SERVICES_FILTER = (Filter)new Filter() {
            private final List<String> INITIAL_SERVICES = Arrays.asList(ServiceLocatorImpl.class.getName(), ThreeThirtyResolver.class.getName(), DynamicConfigurationServiceImpl.class.getName(), DefaultClassAnalyzer.class.getName(), ServiceLocatorRuntimeImpl.class.getName(), InstantiationServiceImpl.class.getName());
            private final HashSet<String> INITIAL_SERVICE_SET = new HashSet<String>(this.INITIAL_SERVICES);
            
            public boolean matches(final Descriptor d) {
                return !this.INITIAL_SERVICE_SET.contains(d.getImplementation());
            }
        };
    }
}
