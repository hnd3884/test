package org.glassfish.jersey.server.internal.process;

import org.glassfish.jersey.server.spi.RequestScopedInitializer;
import javax.inject.Provider;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.function.Function;

public final class ReferencesInitializer implements Function<RequestProcessingContext, RequestProcessingContext>
{
    private final InjectionManager injectionManager;
    private final Provider<RequestProcessingContextReference> processingContextRefProvider;
    
    public ReferencesInitializer(final InjectionManager injectionManager, final Provider<RequestProcessingContextReference> processingContextRefProvider) {
        this.injectionManager = injectionManager;
        this.processingContextRefProvider = processingContextRefProvider;
    }
    
    @Override
    public RequestProcessingContext apply(final RequestProcessingContext context) {
        ((RequestProcessingContextReference)this.processingContextRefProvider.get()).set(context);
        final RequestScopedInitializer requestScopedInitializer = context.request().getRequestScopedInitializer();
        if (requestScopedInitializer != null) {
            requestScopedInitializer.initialize(this.injectionManager);
        }
        return context;
    }
}
