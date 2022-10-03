package org.glassfish.jersey.client;

import javax.ws.rs.client.ResponseProcessingException;
import org.glassfish.jersey.process.internal.RequestScope;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.io.IOException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.ClientRequestContext;
import org.glassfish.jersey.process.internal.Stage;
import org.glassfish.jersey.process.internal.AbstractChainableStage;
import javax.ws.rs.client.ClientResponseFilter;
import org.glassfish.jersey.internal.inject.Providers;
import javax.ws.rs.client.ClientRequestFilter;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.process.internal.ChainableStage;
import org.glassfish.jersey.internal.inject.InjectionManager;

class ClientFilteringStages
{
    private ClientFilteringStages() {
    }
    
    static ChainableStage<ClientRequest> createRequestFilteringStage(final InjectionManager injectionManager) {
        final RankedComparator<ClientRequestFilter> comparator = (RankedComparator<ClientRequestFilter>)new RankedComparator(RankedComparator.Order.ASCENDING);
        final Iterable<ClientRequestFilter> requestFilters = Providers.getAllProviders(injectionManager, (Class)ClientRequestFilter.class, (RankedComparator)comparator);
        return (ChainableStage<ClientRequest>)(requestFilters.iterator().hasNext() ? new RequestFilteringStage((Iterable)requestFilters) : null);
    }
    
    static ChainableStage<ClientResponse> createResponseFilteringStage(final InjectionManager injectionManager) {
        final RankedComparator<ClientResponseFilter> comparator = (RankedComparator<ClientResponseFilter>)new RankedComparator(RankedComparator.Order.DESCENDING);
        final Iterable<ClientResponseFilter> responseFilters = Providers.getAllProviders(injectionManager, (Class)ClientResponseFilter.class, (RankedComparator)comparator);
        return (ChainableStage<ClientResponse>)(responseFilters.iterator().hasNext() ? new ResponseFilterStage((Iterable)responseFilters) : null);
    }
    
    private static final class RequestFilteringStage extends AbstractChainableStage<ClientRequest>
    {
        private final Iterable<ClientRequestFilter> requestFilters;
        
        private RequestFilteringStage(final Iterable<ClientRequestFilter> requestFilters) {
            this.requestFilters = requestFilters;
        }
        
        public Stage.Continuation<ClientRequest> apply(final ClientRequest requestContext) {
            for (final ClientRequestFilter filter : this.requestFilters) {
                try {
                    filter.filter((ClientRequestContext)requestContext);
                    final Response abortResponse = requestContext.getAbortResponse();
                    if (abortResponse != null) {
                        throw new AbortException(new ClientResponse(requestContext, abortResponse));
                    }
                    continue;
                }
                catch (final IOException ex) {
                    throw new ProcessingException((Throwable)ex);
                }
            }
            return (Stage.Continuation<ClientRequest>)Stage.Continuation.of((Object)requestContext, this.getDefaultNext());
        }
    }
    
    private static class ResponseFilterStage extends AbstractChainableStage<ClientResponse>
    {
        private final Iterable<ClientResponseFilter> filters;
        
        private ResponseFilterStage(final Iterable<ClientResponseFilter> filters) {
            this.filters = filters;
        }
        
        public Stage.Continuation<ClientResponse> apply(final ClientResponse responseContext) {
            try {
                for (final ClientResponseFilter filter : this.filters) {
                    filter.filter((ClientRequestContext)responseContext.getRequestContext(), (ClientResponseContext)responseContext);
                }
            }
            catch (final IOException ex) {
                final InboundJaxrsResponse response = new InboundJaxrsResponse(responseContext, null);
                throw new ResponseProcessingException((Response)response, (Throwable)ex);
            }
            return (Stage.Continuation<ClientResponse>)Stage.Continuation.of((Object)responseContext, this.getDefaultNext());
        }
    }
}
