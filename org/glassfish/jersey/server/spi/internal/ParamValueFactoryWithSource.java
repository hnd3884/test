package org.glassfish.jersey.server.spi.internal;

import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;

public final class ParamValueFactoryWithSource<T> implements Function<ContainerRequest, T>
{
    private final Function<ContainerRequest, T> parameterFunction;
    private final Parameter.Source parameterSource;
    
    public ParamValueFactoryWithSource(final Function<ContainerRequest, T> paramFunction, final Parameter.Source parameterSource) {
        this.parameterFunction = paramFunction;
        this.parameterSource = parameterSource;
    }
    
    @Override
    public T apply(final ContainerRequest request) {
        return this.parameterFunction.apply(request);
    }
    
    public Parameter.Source getSource() {
        return this.parameterSource;
    }
}
