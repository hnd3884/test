package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.process.internal.Stages;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.process.internal.Stage;

final class MatchedEndpointExtractorStage implements Stage<RequestProcessingContext>
{
    public Stage.Continuation<RequestProcessingContext> apply(final RequestProcessingContext processingContext) {
        final Endpoint endpoint = processingContext.routingContext().getEndpoint();
        return (Stage.Continuation<RequestProcessingContext>)((endpoint != null) ? Stage.Continuation.of((Object)processingContext, Stages.asStage((Inflector)endpoint)) : Stage.Continuation.of((Object)processingContext));
    }
}
