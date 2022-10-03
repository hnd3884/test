package org.glassfish.jersey.server;

import javax.inject.Inject;
import org.glassfish.jersey.message.MessageBodyWorkers;
import javax.inject.Provider;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import java.util.function.Function;

public class ContainerMessageBodyWorkersInitializer implements Function<RequestProcessingContext, RequestProcessingContext>
{
    private final Provider<MessageBodyWorkers> workersFactory;
    
    @Inject
    public ContainerMessageBodyWorkersInitializer(final Provider<MessageBodyWorkers> workersFactory) {
        this.workersFactory = workersFactory;
    }
    
    @Override
    public RequestProcessingContext apply(final RequestProcessingContext requestContext) {
        requestContext.request().setWorkers((MessageBodyWorkers)this.workersFactory.get());
        return requestContext;
    }
}
