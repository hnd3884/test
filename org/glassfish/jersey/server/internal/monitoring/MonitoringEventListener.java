package org.glassfish.jersey.server.internal.monitoring;

import java.util.stream.Collector;
import java.util.Collections;
import java.util.stream.Collectors;
import org.glassfish.jersey.uri.UriTemplate;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.lang.reflect.Type;
import org.glassfish.jersey.server.monitoring.DestroyListener;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import java.util.concurrent.ArrayBlockingQueue;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import java.util.Queue;
import javax.inject.Inject;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.logging.Logger;
import javax.annotation.Priority;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;

@Priority(1100)
public final class MonitoringEventListener implements ApplicationEventListener
{
    private static final Logger LOGGER;
    private static final int EVENT_QUEUE_SIZE = 500000;
    @Inject
    private InjectionManager injectionManager;
    private final Queue<RequestStats> requestQueuedItems;
    private final Queue<Integer> responseStatuses;
    private final Queue<RequestEvent> exceptionMapperEvents;
    private volatile MonitoringStatisticsProcessor monitoringStatisticsProcessor;
    
    public MonitoringEventListener() {
        this.requestQueuedItems = new ArrayBlockingQueue<RequestStats>(500000);
        this.responseStatuses = new ArrayBlockingQueue<Integer>(500000);
        this.exceptionMapperEvents = new ArrayBlockingQueue<RequestEvent>(500000);
    }
    
    @Override
    public ReqEventListener onRequest(final RequestEvent requestEvent) {
        switch (requestEvent.getType()) {
            case START: {
                return new ReqEventListener();
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public void onEvent(final ApplicationEvent event) {
        final ApplicationEvent.Type type = event.getType();
        switch (type) {
            case RELOAD_FINISHED:
            case INITIALIZATION_FINISHED: {
                (this.monitoringStatisticsProcessor = new MonitoringStatisticsProcessor(this.injectionManager, this)).startMonitoringWorker();
                break;
            }
            case DESTROY_FINISHED: {
                if (this.monitoringStatisticsProcessor != null) {
                    try {
                        this.monitoringStatisticsProcessor.shutDown();
                    }
                    catch (final InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new ProcessingException(LocalizationMessages.ERROR_MONITORING_SHUTDOWN_INTERRUPTED(), (Throwable)e);
                    }
                }
                final List<DestroyListener> listeners = this.injectionManager.getAllInstances((Type)DestroyListener.class);
                for (final DestroyListener listener : listeners) {
                    try {
                        listener.onDestroy();
                    }
                    catch (final Exception e2) {
                        MonitoringEventListener.LOGGER.log(Level.WARNING, LocalizationMessages.ERROR_MONITORING_STATISTICS_LISTENER_DESTROY(listener.getClass()), e2);
                    }
                }
                break;
            }
        }
    }
    
    Queue<RequestEvent> getExceptionMapperEvents() {
        return this.exceptionMapperEvents;
    }
    
    Queue<RequestStats> getRequestQueuedItems() {
        return this.requestQueuedItems;
    }
    
    Queue<Integer> getResponseStatuses() {
        return this.responseStatuses;
    }
    
    static {
        LOGGER = Logger.getLogger(MonitoringEventListener.class.getName());
    }
    
    static class TimeStats
    {
        private final long duration;
        private final long startTime;
        
        private TimeStats(final long startTime, final long requestDuration) {
            this.duration = requestDuration;
            this.startTime = startTime;
        }
        
        long getDuration() {
            return this.duration;
        }
        
        long getStartTime() {
            return this.startTime;
        }
    }
    
    static class MethodStats extends TimeStats
    {
        private final ResourceMethod method;
        
        private MethodStats(final ResourceMethod method, final long startTime, final long requestDuration) {
            super(startTime, requestDuration);
            this.method = method;
        }
        
        ResourceMethod getMethod() {
            return this.method;
        }
    }
    
    static class RequestStats
    {
        private final TimeStats requestStats;
        private final MethodStats methodStats;
        private final String requestUri;
        
        private RequestStats(final TimeStats requestStats, final MethodStats methodStats, final String requestUri) {
            this.requestStats = requestStats;
            this.methodStats = methodStats;
            this.requestUri = requestUri;
        }
        
        TimeStats getRequestStats() {
            return this.requestStats;
        }
        
        MethodStats getMethodStats() {
            return this.methodStats;
        }
        
        String getRequestUri() {
            return this.requestUri;
        }
    }
    
    private class ReqEventListener implements RequestEventListener
    {
        private final long requestTimeStart;
        private volatile long methodTimeStart;
        private volatile MethodStats methodStats;
        
        public ReqEventListener() {
            this.requestTimeStart = System.currentTimeMillis();
        }
        
        @Override
        public void onEvent(final RequestEvent event) {
            final long now = System.currentTimeMillis();
            switch (event.getType()) {
                case RESOURCE_METHOD_START: {
                    this.methodTimeStart = now;
                    break;
                }
                case RESOURCE_METHOD_FINISHED: {
                    final ResourceMethod method = event.getUriInfo().getMatchedResourceMethod();
                    this.methodStats = new MethodStats(method, this.methodTimeStart, now - this.methodTimeStart);
                    break;
                }
                case EXCEPTION_MAPPING_FINISHED: {
                    if (!MonitoringEventListener.this.exceptionMapperEvents.offer(event)) {
                        MonitoringEventListener.LOGGER.warning(LocalizationMessages.ERROR_MONITORING_QUEUE_MAPPER());
                        break;
                    }
                    break;
                }
                case FINISHED: {
                    if (event.isResponseWritten() && !MonitoringEventListener.this.responseStatuses.offer(event.getContainerResponse().getStatus())) {
                        MonitoringEventListener.LOGGER.warning(LocalizationMessages.ERROR_MONITORING_QUEUE_RESPONSE());
                    }
                    final StringBuilder sb = new StringBuilder();
                    final List<UriTemplate> orderedTemplates = event.getUriInfo().getMatchedTemplates().stream().collect((Collector<? super Object, Object, List<UriTemplate>>)Collectors.collectingAndThen((Collector<? super Object, A, List<? super Object>>)Collectors.toList(), uriTemplates -> {
                        Collections.reverse(uriTemplates);
                        return uriTemplates;
                    }));
                    for (final UriTemplate uriTemplate : orderedTemplates) {
                        sb.append(uriTemplate.getTemplate());
                        if (!uriTemplate.endsWithSlash()) {
                            sb.append("/");
                        }
                        sb.setLength(sb.length() - 1);
                    }
                    if (!MonitoringEventListener.this.requestQueuedItems.offer(new RequestStats(new TimeStats(this.requestTimeStart, now - this.requestTimeStart), this.methodStats, sb.toString()))) {
                        MonitoringEventListener.LOGGER.warning(LocalizationMessages.ERROR_MONITORING_QUEUE_REQUEST());
                        break;
                    }
                    break;
                }
            }
        }
    }
}
