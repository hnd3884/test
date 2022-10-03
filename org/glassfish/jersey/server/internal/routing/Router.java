package org.glassfish.jersey.server.internal.routing;

import java.util.Collections;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;

interface Router
{
    Continuation apply(final RequestProcessingContext p0);
    
    public static final class Continuation
    {
        private final RequestProcessingContext requestProcessingContext;
        private final Iterable<Router> next;
        
        static Continuation of(final RequestProcessingContext result) {
            return new Continuation(result, null);
        }
        
        static Continuation of(final RequestProcessingContext result, final Iterable<Router> next) {
            return new Continuation(result, next);
        }
        
        static Continuation of(final RequestProcessingContext request, final Router next) {
            return new Continuation(request, Collections.singletonList(next));
        }
        
        private Continuation(final RequestProcessingContext request, final Iterable<Router> next) {
            this.requestProcessingContext = request;
            this.next = (Iterable<Router>)((next == null) ? Collections.emptyList() : next);
        }
        
        RequestProcessingContext requestContext() {
            return this.requestProcessingContext;
        }
        
        Iterable<Router> next() {
            return this.next;
        }
    }
}
