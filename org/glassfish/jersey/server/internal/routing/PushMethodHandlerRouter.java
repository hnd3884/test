package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.model.MethodHandler;

final class PushMethodHandlerRouter implements Router
{
    private final MethodHandler methodHandler;
    private final Router next;
    
    PushMethodHandlerRouter(final MethodHandler methodHandler, final Router next) {
        this.methodHandler = methodHandler;
        this.next = next;
    }
    
    @Override
    public Continuation apply(final RequestProcessingContext context) {
        final RoutingContext routingContext = context.routingContext();
        final Object storedResource = routingContext.peekMatchedResource();
        if (storedResource == null || !storedResource.getClass().equals(this.methodHandler.getHandlerClass())) {
            final Object handlerInstance = this.methodHandler.getInstance(context.injectionManager());
            routingContext.pushMatchedResource(handlerInstance);
        }
        return Continuation.of(context, this.next);
    }
}
