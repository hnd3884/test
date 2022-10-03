package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.RequestProcessingContext;

final class PushMatchedUriRouter implements Router
{
    @Override
    public Continuation apply(final RequestProcessingContext context) {
        context.routingContext().pushLeftHandPath();
        return Continuation.of(context);
    }
}
