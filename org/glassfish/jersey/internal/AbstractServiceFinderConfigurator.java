package org.glassfish.jersey.internal;

import java.util.Collections;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.glassfish.jersey.CommonProperties;
import java.util.List;
import java.util.Map;
import javax.ws.rs.RuntimeType;

public abstract class AbstractServiceFinderConfigurator<T> implements BootstrapConfigurator
{
    private final Class<T> contract;
    private final RuntimeType runtimeType;
    
    protected AbstractServiceFinderConfigurator(final Class<T> contract, final RuntimeType runtimeType) {
        this.contract = contract;
        this.runtimeType = runtimeType;
    }
    
    protected List<Class<T>> loadImplementations(final Map<String, Object> applicationProperties) {
        boolean disableMetaInfServicesLookup;
        final boolean METAINF_SERVICES_LOOKUP_DISABLE_DEFAULT = disableMetaInfServicesLookup = false;
        if (applicationProperties != null) {
            disableMetaInfServicesLookup = CommonProperties.getValue(applicationProperties, this.runtimeType, "jersey.config.disableMetainfServicesLookup", METAINF_SERVICES_LOOKUP_DISABLE_DEFAULT, Boolean.class);
        }
        if (!disableMetaInfServicesLookup) {
            return Stream.of(ServiceFinder.find(this.contract, true).toClassArray()).collect((Collector<? super Class<T>, ?, List<Class<T>>>)Collectors.toList());
        }
        return Collections.emptyList();
    }
}
