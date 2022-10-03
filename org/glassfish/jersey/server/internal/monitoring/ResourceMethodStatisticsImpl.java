package org.glassfish.jersey.server.internal.monitoring;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;
import org.glassfish.jersey.server.monitoring.ResourceMethodStatistics;

final class ResourceMethodStatisticsImpl implements ResourceMethodStatistics
{
    private final ExecutionStatistics resourceMethodExecutionStatistics;
    private final ExecutionStatistics requestExecutionStatistics;
    private final ResourceMethod resourceMethod;
    
    private ResourceMethodStatisticsImpl(final ResourceMethod resourceMethod, final ExecutionStatistics resourceMethodExecutionStatistics, final ExecutionStatistics requestExecutionStatistics) {
        this.resourceMethod = resourceMethod;
        this.resourceMethodExecutionStatistics = resourceMethodExecutionStatistics;
        this.requestExecutionStatistics = requestExecutionStatistics;
    }
    
    @Override
    public ExecutionStatistics getRequestStatistics() {
        return this.requestExecutionStatistics;
    }
    
    @Override
    public ExecutionStatistics getMethodStatistics() {
        return this.resourceMethodExecutionStatistics;
    }
    
    @Override
    public ResourceMethod getResourceMethod() {
        return this.resourceMethod;
    }
    
    @Override
    public ResourceMethodStatistics snapshot() {
        return this;
    }
    
    static class Factory
    {
        private final ConcurrentMap<String, Builder> stringToMethodsBuilders;
        
        Factory() {
            this.stringToMethodsBuilders = new ConcurrentHashMap<String, Builder>();
        }
        
        Builder getOrCreate(final ResourceMethod resourceMethod) {
            final String methodUniqueId = MonitoringUtils.getMethodUniqueId(resourceMethod);
            if (!this.stringToMethodsBuilders.containsKey(methodUniqueId)) {
                this.stringToMethodsBuilders.putIfAbsent(methodUniqueId, new Builder(resourceMethod));
            }
            return this.stringToMethodsBuilders.get(methodUniqueId);
        }
    }
    
    static class Builder
    {
        private final ResourceMethod resourceMethod;
        private final AtomicReference<ExecutionStatisticsImpl.Builder> resourceMethodExecutionStatisticsBuilder;
        private final AtomicReference<ExecutionStatisticsImpl.Builder> requestExecutionStatisticsBuilder;
        private volatile ResourceMethodStatisticsImpl cached;
        
        Builder(final ResourceMethod resourceMethod) {
            this.resourceMethodExecutionStatisticsBuilder = new AtomicReference<ExecutionStatisticsImpl.Builder>();
            this.requestExecutionStatisticsBuilder = new AtomicReference<ExecutionStatisticsImpl.Builder>();
            this.resourceMethod = resourceMethod;
        }
        
        ResourceMethodStatisticsImpl build() {
            final ResourceMethodStatisticsImpl cachedLocalReference = this.cached;
            if (cachedLocalReference != null) {
                return cachedLocalReference;
            }
            final ExecutionStatistics methodStats = (this.resourceMethodExecutionStatisticsBuilder.get() == null) ? ExecutionStatisticsImpl.EMPTY : this.resourceMethodExecutionStatisticsBuilder.get().build();
            final ExecutionStatistics requestStats = (this.requestExecutionStatisticsBuilder.get() == null) ? ExecutionStatisticsImpl.EMPTY : this.requestExecutionStatisticsBuilder.get().build();
            final ResourceMethodStatisticsImpl stats = new ResourceMethodStatisticsImpl(this.resourceMethod, methodStats, requestStats, null);
            if (MonitoringUtils.isCacheable(methodStats)) {
                this.cached = stats;
            }
            return stats;
        }
        
        void addResourceMethodExecution(final long methodStartTime, final long methodDuration, final long requestStartTime, final long requestDuration) {
            this.cached = null;
            if (this.resourceMethodExecutionStatisticsBuilder.get() == null) {
                this.resourceMethodExecutionStatisticsBuilder.compareAndSet(null, new ExecutionStatisticsImpl.Builder());
            }
            this.resourceMethodExecutionStatisticsBuilder.get().addExecution(methodStartTime, methodDuration);
            if (this.requestExecutionStatisticsBuilder.get() == null) {
                this.requestExecutionStatisticsBuilder.compareAndSet(null, new ExecutionStatisticsImpl.Builder());
            }
            this.requestExecutionStatisticsBuilder.get().addExecution(requestStartTime, requestDuration);
        }
    }
}
