package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.internal.process.Endpoint;

final class Routers
{
    private static final Router IDENTITY_ROUTER;
    
    private Routers() {
        throw new AssertionError((Object)"No instances of this class.");
    }
    
    public static Router noop() {
        return Routers.IDENTITY_ROUTER;
    }
    
    public static Router endpoint(final Endpoint endpoint) {
        return new EndpointRouter(endpoint);
    }
    
    public static Endpoint extractEndpoint(final Router router) {
        if (router instanceof EndpointRouter) {
            return ((EndpointRouter)router).endpoint;
        }
        return null;
    }
    
    static {
        IDENTITY_ROUTER = new Router() {
            @Override
            public Continuation apply(final RequestProcessingContext data) {
                return Continuation.of(data);
            }
        };
    }
    
    private static class EndpointRouter implements Router
    {
        private final Endpoint endpoint;
        
        public EndpointRouter(final Endpoint endpoint) {
            this.endpoint = endpoint;
        }
        
        @Override
        public Continuation apply(final RequestProcessingContext context) {
            return Continuation.of(context);
        }
    }
}
