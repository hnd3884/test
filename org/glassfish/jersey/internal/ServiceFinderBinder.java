package org.glassfish.jersey.internal;

import org.glassfish.jersey.CommonProperties;
import javax.ws.rs.RuntimeType;
import java.util.Map;
import org.glassfish.jersey.internal.inject.AbstractBinder;

public class ServiceFinderBinder<T> extends AbstractBinder
{
    private final Class<T> contract;
    private final Map<String, Object> applicationProperties;
    private final RuntimeType runtimeType;
    
    public ServiceFinderBinder(final Class<T> contract, final Map<String, Object> applicationProperties, final RuntimeType runtimeType) {
        this.contract = contract;
        this.applicationProperties = applicationProperties;
        this.runtimeType = runtimeType;
    }
    
    @Override
    protected void configure() {
        final boolean METAINF_SERVICES_LOOKUP_DISABLE_DEFAULT = false;
        boolean disableMetainfServicesLookup = false;
        if (this.applicationProperties != null) {
            disableMetainfServicesLookup = CommonProperties.getValue(this.applicationProperties, this.runtimeType, "jersey.config.disableMetainfServicesLookup", false, Boolean.class);
        }
        if (!disableMetainfServicesLookup) {
            for (final Class<T> t : ServiceFinder.find(this.contract, true).toClassArray()) {
                this.bind(t).to((Class<? super Object>)this.contract);
            }
        }
    }
}
