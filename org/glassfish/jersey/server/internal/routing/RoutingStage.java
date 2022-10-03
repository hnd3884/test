package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.Endpoint;
import java.util.Iterator;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.process.internal.Stage;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.process.internal.AbstractChainableStage;

final class RoutingStage extends AbstractChainableStage<RequestProcessingContext>
{
    private final Router routingRoot;
    
    RoutingStage(final Router routingRoot) {
        this.routingRoot = routingRoot;
    }
    
    public Stage.Continuation<RequestProcessingContext> apply(final RequestProcessingContext context) {
        final ContainerRequest request = context.request();
        context.triggerEvent(RequestEvent.Type.MATCHING_START);
        final TracingLogger tracingLogger = TracingLogger.getInstance((PropertiesDelegate)request);
        final long timestamp = tracingLogger.timestamp((TracingLogger.Event)ServerTraceEvent.MATCH_SUMMARY);
        try {
            final RoutingResult result = this._apply(context, this.routingRoot);
            Stage<RequestProcessingContext> nextStage = null;
            if (result.endpoint != null) {
                context.routingContext().setEndpoint(result.endpoint);
                nextStage = (Stage<RequestProcessingContext>)this.getDefaultNext();
            }
            return (Stage.Continuation<RequestProcessingContext>)Stage.Continuation.of((Object)result.context, (Stage)nextStage);
        }
        finally {
            tracingLogger.logDuration((TracingLogger.Event)ServerTraceEvent.MATCH_SUMMARY, timestamp, new Object[0]);
        }
    }
    
    private RoutingResult _apply(final RequestProcessingContext request, final Router router) {
        final Router.Continuation continuation = router.apply(request);
        for (final Router child : continuation.next()) {
            final RoutingResult result = this._apply(continuation.requestContext(), child);
            if (result.endpoint != null) {
                return result;
            }
        }
        final Endpoint endpoint = Routers.extractEndpoint(router);
        if (endpoint != null) {
            return from(continuation.requestContext(), endpoint);
        }
        return from(continuation.requestContext());
    }
    
    private static final class RoutingResult
    {
        private final RequestProcessingContext context;
        private final Endpoint endpoint;
        
        private static RoutingResult from(final RequestProcessingContext requestProcessingContext, final Endpoint endpoint) {
            return new RoutingResult(requestProcessingContext, endpoint);
        }
        
        private static RoutingResult from(final RequestProcessingContext requestProcessingContext) {
            return new RoutingResult(requestProcessingContext, null);
        }
        
        private RoutingResult(final RequestProcessingContext context, final Endpoint endpoint) {
            this.context = context;
            this.endpoint = endpoint;
        }
    }
}
