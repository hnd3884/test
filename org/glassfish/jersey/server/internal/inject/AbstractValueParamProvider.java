package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.glassfish.jersey.server.model.Parameter;
import java.util.Set;
import javax.inject.Provider;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

public abstract class AbstractValueParamProvider implements ValueParamProvider
{
    private final Provider<MultivaluedParameterExtractorProvider> mpep;
    private final Set<Parameter.Source> compatibleSources;
    
    protected AbstractValueParamProvider(final Provider<MultivaluedParameterExtractorProvider> mpep, final Parameter.Source... compatibleSources) {
        this.mpep = mpep;
        this.compatibleSources = new HashSet<Parameter.Source>(Arrays.asList(compatibleSources));
    }
    
    protected final MultivaluedParameterExtractor<?> get(final Parameter parameter) {
        return ((MultivaluedParameterExtractorProvider)this.mpep.get()).get(parameter);
    }
    
    protected abstract Function<ContainerRequest, ?> createValueProvider(final Parameter p0);
    
    @Override
    public final Function<ContainerRequest, ?> getValueProvider(final Parameter parameter) {
        if (!this.compatibleSources.contains(parameter.getSource())) {
            return null;
        }
        return this.createValueProvider(parameter);
    }
    
    @Override
    public PriorityType getPriority() {
        return Priority.NORMAL;
    }
}
