package org.glassfish.jersey.server.internal.monitoring;

import org.glassfish.jersey.server.model.ResourceMethod;
import java.util.Queue;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import org.glassfish.jersey.server.model.ResourceModel;
import java.util.Map;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import java.util.Collections;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.server.BackgroundSchedulerLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.server.ExtendedResourceContext;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.concurrent.ScheduledExecutorService;
import org.glassfish.jersey.server.monitoring.MonitoringStatisticsListener;
import java.util.List;
import java.util.logging.Logger;

final class MonitoringStatisticsProcessor
{
    private static final Logger LOGGER;
    private static final int DEFAULT_INTERVAL = 500;
    private static final int SHUTDOWN_TIMEOUT = 10;
    private final MonitoringEventListener monitoringEventListener;
    private final MonitoringStatisticsImpl.Builder statisticsBuilder;
    private final List<MonitoringStatisticsListener> statisticsCallbackList;
    private final ScheduledExecutorService scheduler;
    private final int interval;
    
    MonitoringStatisticsProcessor(final InjectionManager injectionManager, final MonitoringEventListener monitoringEventListener) {
        this.monitoringEventListener = monitoringEventListener;
        final ResourceModel resourceModel = ((ExtendedResourceContext)injectionManager.getInstance((Class)ExtendedResourceContext.class)).getResourceModel();
        this.statisticsBuilder = new MonitoringStatisticsImpl.Builder(resourceModel);
        this.statisticsCallbackList = injectionManager.getAllInstances((Type)MonitoringStatisticsListener.class);
        this.scheduler = (ScheduledExecutorService)injectionManager.getInstance((Class)ScheduledExecutorService.class, new Annotation[] { BackgroundSchedulerLiteral.INSTANCE });
        this.interval = (int)PropertiesHelper.getValue(((Configuration)injectionManager.getInstance((Class)Configuration.class)).getProperties(), "jersey.config.server.monitoring.statistics.refresh.interval", (Object)500, (Map)Collections.emptyMap());
    }
    
    public void startMonitoringWorker() {
        this.scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    MonitoringStatisticsProcessor.this.processRequestItems();
                    MonitoringStatisticsProcessor.this.processResponseCodeEvents();
                    MonitoringStatisticsProcessor.this.processExceptionMapperEvents();
                }
                catch (final Throwable t) {
                    MonitoringStatisticsProcessor.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_MONITORING_STATISTICS_GENERATION(), t);
                    throw new ProcessingException(LocalizationMessages.ERROR_MONITORING_STATISTICS_GENERATION(), t);
                }
                final MonitoringStatisticsImpl immutableStats = MonitoringStatisticsProcessor.this.statisticsBuilder.build();
                final Iterator<MonitoringStatisticsListener> iterator = MonitoringStatisticsProcessor.this.statisticsCallbackList.iterator();
                while (iterator.hasNext() && !Thread.currentThread().isInterrupted()) {
                    final MonitoringStatisticsListener listener = iterator.next();
                    try {
                        listener.onStatistics(immutableStats);
                    }
                    catch (final Throwable t2) {
                        MonitoringStatisticsProcessor.LOGGER.log(Level.SEVERE, LocalizationMessages.ERROR_MONITORING_STATISTICS_LISTENER(listener.getClass()), t2);
                        iterator.remove();
                    }
                }
            }
        }, 0L, this.interval, TimeUnit.MILLISECONDS);
    }
    
    private void processExceptionMapperEvents() {
        final Queue<RequestEvent> eventQueue = this.monitoringEventListener.getExceptionMapperEvents();
        final FloodingLogger floodingLogger = new FloodingLogger(eventQueue);
        while (!eventQueue.isEmpty()) {
            floodingLogger.conditionallyLogFlooding();
            final RequestEvent event = eventQueue.remove();
            final ExceptionMapperStatisticsImpl.Builder mapperStats = this.statisticsBuilder.getExceptionMapperStatisticsBuilder();
            if (event.getExceptionMapper() != null) {
                mapperStats.addExceptionMapperExecution(event.getExceptionMapper().getClass(), 1);
            }
            mapperStats.addMapping(event.isResponseSuccessfullyMapped(), 1);
        }
    }
    
    private void processRequestItems() {
        final Queue<MonitoringEventListener.RequestStats> requestQueuedItems = this.monitoringEventListener.getRequestQueuedItems();
        final FloodingLogger floodingLogger = new FloodingLogger(requestQueuedItems);
        while (!requestQueuedItems.isEmpty()) {
            floodingLogger.conditionallyLogFlooding();
            final MonitoringEventListener.RequestStats event = requestQueuedItems.remove();
            final MonitoringEventListener.TimeStats requestStats = event.getRequestStats();
            this.statisticsBuilder.addRequestExecution(requestStats.getStartTime(), requestStats.getDuration());
            final MonitoringEventListener.MethodStats methodStat = event.getMethodStats();
            if (methodStat != null) {
                final ResourceMethod method = methodStat.getMethod();
                this.statisticsBuilder.addExecution(event.getRequestUri(), method, methodStat.getStartTime(), methodStat.getDuration(), requestStats.getStartTime(), requestStats.getDuration());
            }
        }
    }
    
    private void processResponseCodeEvents() {
        final Queue<Integer> responseEvents = this.monitoringEventListener.getResponseStatuses();
        final FloodingLogger floodingLogger = new FloodingLogger(responseEvents);
        while (!responseEvents.isEmpty()) {
            floodingLogger.conditionallyLogFlooding();
            final Integer code = responseEvents.remove();
            this.statisticsBuilder.addResponseCode(code);
        }
    }
    
    void shutDown() throws InterruptedException {
        this.scheduler.shutdown();
        final boolean success = this.scheduler.awaitTermination(10L, TimeUnit.SECONDS);
        if (!success) {
            MonitoringStatisticsProcessor.LOGGER.warning(LocalizationMessages.ERROR_MONITORING_SCHEDULER_DESTROY_TIMEOUT());
        }
    }
    
    static {
        LOGGER = Logger.getLogger(MonitoringStatisticsProcessor.class.getName());
    }
    
    private static class FloodingLogger
    {
        private static final int FLOODING_WARNING_LOG_INTERVAL_MILLIS = 5000;
        private final Collection<?> collection;
        private final long startTime;
        private int i;
        private int lastSize;
        
        public FloodingLogger(final Collection<?> collection) {
            this.startTime = System.nanoTime();
            this.i = 0;
            this.collection = collection;
            this.lastSize = collection.size();
        }
        
        public void conditionallyLogFlooding() {
            if ((System.nanoTime() - this.startTime) / TimeUnit.NANOSECONDS.convert(5000L, TimeUnit.MILLISECONDS) <= this.i) {
                return;
            }
            if (this.collection.size() > this.lastSize) {
                MonitoringStatisticsProcessor.LOGGER.warning(LocalizationMessages.ERROR_MONITORING_QUEUE_FLOODED(this.collection.size()));
            }
            ++this.i;
            this.lastSize = this.collection.size();
        }
    }
}
