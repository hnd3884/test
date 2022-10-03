package org.glassfish.jersey.server.internal.monitoring;

import java.util.Collections;
import org.glassfish.jersey.internal.util.collection.Views;
import org.glassfish.jersey.server.model.ResourceMethod;
import java.util.Iterator;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceModel;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.function.Function;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ExceptionMapperStatistics;
import org.glassfish.jersey.server.monitoring.ResponseStatistics;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;

final class MonitoringStatisticsImpl implements MonitoringStatistics
{
    private final ExecutionStatistics requestStatistics;
    private final ResponseStatistics responseStatistics;
    private final ExceptionMapperStatistics exceptionMapperStatistics;
    private final Map<String, ResourceStatistics> uriStatistics;
    private final Map<Class<?>, ResourceStatistics> resourceClassStatistics;
    
    private MonitoringStatisticsImpl(final Map<String, ResourceStatistics> uriStatistics, final Map<Class<?>, ResourceStatistics> resourceClassStatistics, final ExecutionStatistics requestStatistics, final ResponseStatistics responseStatistics, final ExceptionMapperStatistics exceptionMapperStatistics) {
        this.uriStatistics = uriStatistics;
        this.resourceClassStatistics = resourceClassStatistics;
        this.requestStatistics = requestStatistics;
        this.responseStatistics = responseStatistics;
        this.exceptionMapperStatistics = exceptionMapperStatistics;
    }
    
    @Override
    public ExecutionStatistics getRequestStatistics() {
        return this.requestStatistics;
    }
    
    @Override
    public ResponseStatistics getResponseStatistics() {
        return this.responseStatistics;
    }
    
    @Override
    public Map<String, ResourceStatistics> getUriStatistics() {
        return this.uriStatistics;
    }
    
    @Override
    public Map<Class<?>, ResourceStatistics> getResourceClassStatistics() {
        return this.resourceClassStatistics;
    }
    
    @Override
    public ExceptionMapperStatistics getExceptionMapperStatistics() {
        return this.exceptionMapperStatistics;
    }
    
    @Override
    public MonitoringStatistics snapshot() {
        return this;
    }
    
    static class Builder
    {
        private static final Function<ResourceStatisticsImpl.Builder, ResourceStatistics> BUILDING_FUNCTION;
        private final ResponseStatisticsImpl.Builder responseStatisticsBuilder;
        private final ExceptionMapperStatisticsImpl.Builder exceptionMapperStatisticsBuilder;
        private final ResourceMethodStatisticsImpl.Factory methodFactory;
        private final SortedMap<String, ResourceStatisticsImpl.Builder> uriStatistics;
        private final SortedMap<Class<?>, ResourceStatisticsImpl.Builder> resourceClassStatistics;
        private ExecutionStatisticsImpl.Builder executionStatisticsBuilder;
        
        Builder() {
            this.methodFactory = new ResourceMethodStatisticsImpl.Factory();
            this.uriStatistics = new TreeMap<String, ResourceStatisticsImpl.Builder>();
            this.resourceClassStatistics = new TreeMap<Class<?>, ResourceStatisticsImpl.Builder>((o1, o2) -> o1.getName().compareTo(o2.getName()));
            this.responseStatisticsBuilder = new ResponseStatisticsImpl.Builder();
            this.exceptionMapperStatisticsBuilder = new ExceptionMapperStatisticsImpl.Builder();
        }
        
        Builder(final ResourceModel resourceModel) {
            this();
            for (final Resource resource : resourceModel.getRootResources()) {
                this.processResource(resource, "");
                for (final Resource child : resource.getChildResources()) {
                    final String path = resource.getPath();
                    this.processResource(child, path.startsWith("/") ? path : ("/" + path));
                }
            }
        }
        
        private void processResource(final Resource resource, final String pathPrefix) {
            final StringBuilder pathSB = new StringBuilder(pathPrefix);
            if (!pathPrefix.endsWith("/") && !resource.getPath().startsWith("/")) {
                pathSB.append("/");
            }
            pathSB.append(resource.getPath());
            this.uriStatistics.put(pathSB.toString(), new ResourceStatisticsImpl.Builder(resource, this.methodFactory));
            for (final ResourceMethod resourceMethod : resource.getResourceMethods()) {
                this.getOrCreateResourceBuilder(resourceMethod).addMethod(resourceMethod);
            }
        }
        
        private ResourceStatisticsImpl.Builder getOrCreateResourceBuilder(final ResourceMethod resourceMethod) {
            final Class<?> clazz = resourceMethod.getInvocable().getHandler().getHandlerClass();
            ResourceStatisticsImpl.Builder builder = this.resourceClassStatistics.get(clazz);
            if (builder == null) {
                builder = new ResourceStatisticsImpl.Builder(this.methodFactory);
                this.resourceClassStatistics.put(clazz, builder);
            }
            return builder;
        }
        
        ExceptionMapperStatisticsImpl.Builder getExceptionMapperStatisticsBuilder() {
            return this.exceptionMapperStatisticsBuilder;
        }
        
        void addRequestExecution(final long startTime, final long duration) {
            if (this.executionStatisticsBuilder == null) {
                this.executionStatisticsBuilder = new ExecutionStatisticsImpl.Builder();
            }
            this.executionStatisticsBuilder.addExecution(startTime, duration);
        }
        
        void addExecution(final String uri, final ResourceMethod resourceMethod, final long methodTime, final long methodDuration, final long requestTime, final long requestDuration) {
            ResourceStatisticsImpl.Builder uriStatsBuilder = this.uriStatistics.get(uri);
            if (uriStatsBuilder == null) {
                uriStatsBuilder = new ResourceStatisticsImpl.Builder(resourceMethod.getParent(), this.methodFactory);
                this.uriStatistics.put(uri, uriStatsBuilder);
            }
            uriStatsBuilder.addExecution(resourceMethod, methodTime, methodDuration, requestTime, requestDuration);
            final ResourceStatisticsImpl.Builder classStatsBuilder = this.getOrCreateResourceBuilder(resourceMethod);
            classStatsBuilder.addExecution(resourceMethod, methodTime, methodDuration, requestTime, requestDuration);
            this.methodFactory.getOrCreate(resourceMethod).addResourceMethodExecution(methodTime, methodDuration, requestTime, requestDuration);
        }
        
        void addResponseCode(final int responseCode) {
            this.responseStatisticsBuilder.addResponseCode(responseCode);
        }
        
        MonitoringStatisticsImpl build() {
            final Map<String, ResourceStatistics> uriStats = Collections.unmodifiableMap((Map<? extends String, ? extends ResourceStatistics>)Views.mapView((Map)this.uriStatistics, (Function)Builder.BUILDING_FUNCTION));
            final Map<Class<?>, ResourceStatistics> classStats = Collections.unmodifiableMap((Map<? extends Class<?>, ? extends ResourceStatistics>)Views.mapView((Map)this.resourceClassStatistics, (Function)Builder.BUILDING_FUNCTION));
            final ExecutionStatistics requestStats = (this.executionStatisticsBuilder == null) ? ExecutionStatisticsImpl.EMPTY : this.executionStatisticsBuilder.build();
            return new MonitoringStatisticsImpl(uriStats, classStats, requestStats, this.responseStatisticsBuilder.build(), this.exceptionMapperStatisticsBuilder.build(), null);
        }
        
        static {
            BUILDING_FUNCTION = ResourceStatisticsImpl.Builder::build;
        }
    }
}
