package org.glassfish.jersey.server.internal.routing;

import java.util.regex.MatchResult;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;

final class MatchResultInitializerRouter implements Router
{
    private final Router rootRouter;
    
    MatchResultInitializerRouter(final Router rootRouter) {
        this.rootRouter = rootRouter;
    }
    
    @Override
    public Continuation apply(final RequestProcessingContext processingContext) {
        final RoutingContext rc = processingContext.routingContext();
        rc.pushMatchResult(new SingleMatchResult("/" + processingContext.request().getPath(false)));
        return Continuation.of(processingContext, this.rootRouter);
    }
}
