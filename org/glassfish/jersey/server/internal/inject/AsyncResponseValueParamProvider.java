package org.glassfish.jersey.server.internal.inject;

import javax.ws.rs.container.AsyncResponse;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.function.Function;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.AsyncContext;
import javax.inject.Provider;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

final class AsyncResponseValueParamProvider implements ValueParamProvider
{
    private final Provider<AsyncContext> asyncContextProvider;
    
    public AsyncResponseValueParamProvider(final Provider<AsyncContext> asyncContextProvider) {
        this.asyncContextProvider = asyncContextProvider;
    }
    
    @Override
    public Function<ContainerRequest, AsyncResponse> getValueProvider(final Parameter parameter) {
        if (parameter.getSource() != Parameter.Source.SUSPENDED) {
            return null;
        }
        if (!AsyncResponse.class.isAssignableFrom(parameter.getRawType())) {
            return null;
        }
        return (Function<ContainerRequest, AsyncResponse>)(containerRequest -> this.asyncContextProvider.get());
    }
    
    @Override
    public PriorityType getPriority() {
        return Priority.NORMAL;
    }
}
