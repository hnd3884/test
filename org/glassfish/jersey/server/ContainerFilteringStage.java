package org.glassfish.jersey.server;

import javax.ws.rs.container.ContainerResponseContext;
import java.util.Iterator;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.process.internal.Stages;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.server.internal.process.MappableException;
import javax.ws.rs.container.ContainerRequestContext;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.process.internal.ChainableStage;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.internal.RankedComparator;
import java.util.ArrayList;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.process.internal.Stage;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerRequestFilter;
import org.glassfish.jersey.model.internal.RankedProvider;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.process.internal.AbstractChainableStage;

class ContainerFilteringStage extends AbstractChainableStage<RequestProcessingContext>
{
    private final Iterable<RankedProvider<ContainerRequestFilter>> requestFilters;
    private final Iterable<RankedProvider<ContainerResponseFilter>> responseFilters;
    
    ContainerFilteringStage(final Iterable<RankedProvider<ContainerRequestFilter>> requestFilters, final Iterable<RankedProvider<ContainerResponseFilter>> responseFilters) {
        this.requestFilters = requestFilters;
        this.responseFilters = responseFilters;
    }
    
    public Stage.Continuation<RequestProcessingContext> apply(final RequestProcessingContext context) {
        final boolean postMatching = this.responseFilters == null;
        final ContainerRequest request = context.request();
        final TracingLogger tracingLogger = TracingLogger.getInstance((PropertiesDelegate)request);
        Iterable<ContainerRequestFilter> sortedRequestFilters;
        if (postMatching) {
            final ArrayList<Iterable<RankedProvider<ContainerRequestFilter>>> rankedProviders = new ArrayList<Iterable<RankedProvider<ContainerRequestFilter>>>(2);
            rankedProviders.add(this.requestFilters);
            rankedProviders.add(request.getRequestFilters());
            sortedRequestFilters = Providers.mergeAndSortRankedProviders(new RankedComparator(), (Iterable)rankedProviders);
            context.monitoringEventBuilder().setContainerRequestFilters(sortedRequestFilters);
            context.triggerEvent(RequestEvent.Type.REQUEST_MATCHED);
        }
        else {
            context.push((ChainableStage<ContainerResponse>)new ResponseFilterStage(context, (Iterable)this.responseFilters, tracingLogger));
            sortedRequestFilters = Providers.sortRankedProviders(new RankedComparator(), (Iterable)this.requestFilters);
        }
        final TracingLogger.Event summaryEvent = (TracingLogger.Event)(postMatching ? ServerTraceEvent.REQUEST_FILTER_SUMMARY : ServerTraceEvent.PRE_MATCH_SUMMARY);
        final long timestamp = tracingLogger.timestamp(summaryEvent);
        int processedCount = 0;
        try {
            final TracingLogger.Event filterEvent = (TracingLogger.Event)(postMatching ? ServerTraceEvent.REQUEST_FILTER : ServerTraceEvent.PRE_MATCH);
            for (final ContainerRequestFilter filter : sortedRequestFilters) {
                final long filterTimestamp = tracingLogger.timestamp(filterEvent);
                try {
                    filter.filter((ContainerRequestContext)request);
                }
                catch (final Exception exception) {
                    throw new MappableException(exception);
                }
                finally {
                    ++processedCount;
                    tracingLogger.logDuration(filterEvent, filterTimestamp, new Object[] { filter });
                }
                final Response abortResponse = request.getAbortResponse();
                if (abortResponse != null) {
                    return (Stage.Continuation<RequestProcessingContext>)Stage.Continuation.of((Object)context, Stages.asStage((Inflector)new Endpoint() {
                        public ContainerResponse apply(final RequestProcessingContext requestContext) {
                            return new ContainerResponse(requestContext.request(), abortResponse);
                        }
                    }));
                }
            }
        }
        finally {
            if (postMatching) {
                context.triggerEvent(RequestEvent.Type.REQUEST_FILTERED);
            }
            tracingLogger.logDuration(summaryEvent, timestamp, new Object[] { processedCount });
        }
        return (Stage.Continuation<RequestProcessingContext>)Stage.Continuation.of((Object)context, this.getDefaultNext());
    }
    
    private static class ResponseFilterStage extends AbstractChainableStage<ContainerResponse>
    {
        private final RequestProcessingContext processingContext;
        private final Iterable<RankedProvider<ContainerResponseFilter>> filters;
        private final TracingLogger tracingLogger;
        
        private ResponseFilterStage(final RequestProcessingContext processingContext, final Iterable<RankedProvider<ContainerResponseFilter>> filters, final TracingLogger tracingLogger) {
            this.processingContext = processingContext;
            this.filters = filters;
            this.tracingLogger = tracingLogger;
        }
        
        public Stage.Continuation<ContainerResponse> apply(final ContainerResponse responseContext) {
            final ArrayList<Iterable<RankedProvider<ContainerResponseFilter>>> rankedProviders = new ArrayList<Iterable<RankedProvider<ContainerResponseFilter>>>(2);
            rankedProviders.add(this.filters);
            rankedProviders.add(responseContext.getRequestContext().getResponseFilters());
            final Iterable<ContainerResponseFilter> sortedResponseFilters = Providers.mergeAndSortRankedProviders(new RankedComparator(RankedComparator.Order.DESCENDING), (Iterable)rankedProviders);
            final ContainerRequest request = responseContext.getRequestContext();
            this.processingContext.monitoringEventBuilder().setContainerResponseFilters(sortedResponseFilters);
            this.processingContext.triggerEvent(RequestEvent.Type.RESP_FILTERS_START);
            final long timestamp = this.tracingLogger.timestamp((TracingLogger.Event)ServerTraceEvent.RESPONSE_FILTER_SUMMARY);
            int processedCount = 0;
            try {
                for (final ContainerResponseFilter filter : sortedResponseFilters) {
                    final long filterTimestamp = this.tracingLogger.timestamp((TracingLogger.Event)ServerTraceEvent.RESPONSE_FILTER);
                    try {
                        filter.filter((ContainerRequestContext)request, (ContainerResponseContext)responseContext);
                    }
                    catch (final Exception ex) {
                        throw new MappableException(ex);
                    }
                    finally {
                        ++processedCount;
                        this.tracingLogger.logDuration((TracingLogger.Event)ServerTraceEvent.RESPONSE_FILTER, filterTimestamp, new Object[] { filter });
                    }
                }
            }
            finally {
                this.processingContext.triggerEvent(RequestEvent.Type.RESP_FILTERS_FINISHED);
                this.tracingLogger.logDuration((TracingLogger.Event)ServerTraceEvent.RESPONSE_FILTER_SUMMARY, timestamp, new Object[] { processedCount });
            }
            return (Stage.Continuation<ContainerResponse>)Stage.Continuation.of((Object)responseContext, this.getDefaultNext());
        }
    }
}
