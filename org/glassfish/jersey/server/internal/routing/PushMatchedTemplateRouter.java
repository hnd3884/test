package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.uri.UriTemplate;

final class PushMatchedTemplateRouter implements Router
{
    private final UriTemplate resourceTemplate;
    private final UriTemplate methodTemplate;
    
    PushMatchedTemplateRouter(final UriTemplate resourceTemplate, final UriTemplate methodTemplate) {
        this.resourceTemplate = resourceTemplate;
        this.methodTemplate = methodTemplate;
    }
    
    PushMatchedTemplateRouter(final UriTemplate resourceTemplate) {
        this.resourceTemplate = resourceTemplate;
        this.methodTemplate = null;
    }
    
    @Override
    public Continuation apply(final RequestProcessingContext context) {
        context.routingContext().pushTemplates(this.resourceTemplate, this.methodTemplate);
        return Continuation.of(context);
    }
}
