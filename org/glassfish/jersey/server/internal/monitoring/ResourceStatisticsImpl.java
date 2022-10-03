package org.glassfish.jersey.server.internal.monitoring;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import org.glassfish.jersey.server.model.Resource;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentMap;
import java.util.Collections;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;
import org.glassfish.jersey.server.monitoring.ResourceMethodStatistics;
import org.glassfish.jersey.server.model.ResourceMethod;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;

final class ResourceStatisticsImpl implements ResourceStatistics
{
    private final Map<ResourceMethod, ResourceMethodStatistics> resourceMethods;
    private final ExecutionStatistics resourceExecutionStatistics;
    private final ExecutionStatistics requestExecutionStatistics;
    
    private ResourceStatisticsImpl(final Map<ResourceMethod, ResourceMethodStatistics> resourceMethods, final ExecutionStatistics resourceExecutionStatistics, final ExecutionStatistics requestExecutionStatistics) {
        this.resourceMethods = Collections.unmodifiableMap((Map<? extends ResourceMethod, ? extends ResourceMethodStatistics>)resourceMethods);
        this.resourceExecutionStatistics = resourceExecutionStatistics;
        this.requestExecutionStatistics = requestExecutionStatistics;
    }
    
    @Override
    public ExecutionStatistics getResourceMethodExecutionStatistics() {
        return this.resourceExecutionStatistics;
    }
    
    @Override
    public ExecutionStatistics getRequestExecutionStatistics() {
        return this.requestExecutionStatistics;
    }
    
    @Override
    public Map<ResourceMethod, ResourceMethodStatistics> getResourceMethodStatistics() {
        return this.resourceMethods;
    }
    
    @Override
    public ResourceStatistics snapshot() {
        return this;
    }
    
    static class Builder
    {
        private final ConcurrentMap<ResourceMethodStatisticsImpl.Builder, Boolean> methodsBuilders;
        private final ResourceMethodStatisticsImpl.Factory methodFactory;
        private final AtomicReference<ExecutionStatisticsImpl.Builder> resourceExecutionStatisticsBuilder;
        private final AtomicReference<ExecutionStatisticsImpl.Builder> requestExecutionStatisticsBuilder;
        private volatile ResourceStatisticsImpl cached;
        
        Builder(final Resource resource, final ResourceMethodStatisticsImpl.Factory methodFactory) {
            this(methodFactory);
            for (final ResourceMethod method : resource.getResourceMethods()) {
                this.getOrCreate(method);
            }
        }
        
        Builder(final ResourceMethodStatisticsImpl.Factory methodFactory) {
            this.methodsBuilders = new ConcurrentHashMap<ResourceMethodStatisticsImpl.Builder, Boolean>();
            this.resourceExecutionStatisticsBuilder = new AtomicReference<ExecutionStatisticsImpl.Builder>();
            this.requestExecutionStatisticsBuilder = new AtomicReference<ExecutionStatisticsImpl.Builder>();
            this.methodFactory = methodFactory;
        }
        
        ResourceStatisticsImpl build() {
            final ResourceStatisticsImpl cachedReference = this.cached;
            if (cachedReference != null) {
                return cachedReference;
            }
            final Map<ResourceMethod, ResourceMethodStatistics> resourceMethods = new HashMap<ResourceMethod, ResourceMethodStatistics>();
            for (final ResourceMethodStatisticsImpl.Builder builder : this.methodsBuilders.keySet()) {
                final ResourceMethodStatisticsImpl stats = builder.build();
                resourceMethods.put(stats.getResourceMethod(), stats);
            }
            final ExecutionStatistics resourceStats = (this.resourceExecutionStatisticsBuilder.get() == null) ? ExecutionStatisticsImpl.EMPTY : this.resourceExecutionStatisticsBuilder.get().build();
            final ExecutionStatistics requestStats = (this.requestExecutionStatisticsBuilder.get() == null) ? ExecutionStatisticsImpl.EMPTY : this.requestExecutionStatisticsBuilder.get().build();
            final ResourceStatisticsImpl stats2 = new ResourceStatisticsImpl(resourceMethods, resourceStats, requestStats, null);
            if (MonitoringUtils.isCacheable(requestStats)) {
                this.cached = stats2;
            }
            return stats2;
        }
        
        void addExecution(final ResourceMethod resourceMethod, final long methodStartTime, final long methodDuration, final long requestStartTime, final long requestDuration) {
            this.cached = null;
            if (this.resourceExecutionStatisticsBuilder.get() == null) {
                this.resourceExecutionStatisticsBuilder.compareAndSet(null, new ExecutionStatisticsImpl.Builder());
            }
            this.resourceExecutionStatisticsBuilder.get().addExecution(methodStartTime, methodDuration);
            if (this.requestExecutionStatisticsBuilder.get() == null) {
                this.requestExecutionStatisticsBuilder.compareAndSet(null, new ExecutionStatisticsImpl.Builder());
            }
            this.requestExecutionStatisticsBuilder.get().addExecution(requestStartTime, requestDuration);
            this.addMethod(resourceMethod);
        }
        
        void addMethod(final ResourceMethod resourceMethod) {
            this.cached = null;
            this.getOrCreate(resourceMethod);
        }
        
        private ResourceMethodStatisticsImpl.Builder getOrCreate(final ResourceMethod resourceMethod) {
            final ResourceMethodStatisticsImpl.Builder methodStats = this.methodFactory.getOrCreate(resourceMethod);
            this.methodsBuilders.putIfAbsent(methodStats, Boolean.TRUE);
            return methodStats;
        }
    }
}
