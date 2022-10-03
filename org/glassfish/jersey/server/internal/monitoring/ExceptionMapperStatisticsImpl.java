package org.glassfish.jersey.server.internal.monitoring;

import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ExceptionMapperStatistics;

final class ExceptionMapperStatisticsImpl implements ExceptionMapperStatistics
{
    private final Map<Class<?>, Long> exceptionMapperExecutionCount;
    private final long successfulMappings;
    private final long unsuccessfulMappings;
    private final long totalMappings;
    
    private ExceptionMapperStatisticsImpl(final Map<Class<?>, Long> exceptionMapperExecutionCount, final long successfulMappings, final long unsuccessfulMappings, final long totalMappings) {
        this.exceptionMapperExecutionCount = Collections.unmodifiableMap((Map<? extends Class<?>, ? extends Long>)exceptionMapperExecutionCount);
        this.successfulMappings = successfulMappings;
        this.unsuccessfulMappings = unsuccessfulMappings;
        this.totalMappings = totalMappings;
    }
    
    @Override
    public Map<Class<?>, Long> getExceptionMapperExecutions() {
        return this.exceptionMapperExecutionCount;
    }
    
    @Override
    public long getSuccessfulMappings() {
        return this.successfulMappings;
    }
    
    @Override
    public long getUnsuccessfulMappings() {
        return this.unsuccessfulMappings;
    }
    
    @Override
    public long getTotalMappings() {
        return this.totalMappings;
    }
    
    @Override
    public ExceptionMapperStatistics snapshot() {
        return this;
    }
    
    static class Builder
    {
        private Map<Class<?>, Long> exceptionMapperExecutionCountMap;
        private long successfulMappings;
        private long unsuccessfulMappings;
        private long totalMappings;
        private ExceptionMapperStatisticsImpl cached;
        
        Builder() {
            this.exceptionMapperExecutionCountMap = new HashMap<Class<?>, Long>();
        }
        
        void addMapping(final boolean success, final int count) {
            this.cached = null;
            ++this.totalMappings;
            if (success) {
                this.successfulMappings += count;
            }
            else {
                this.unsuccessfulMappings += count;
            }
        }
        
        void addExceptionMapperExecution(final Class<?> mapper, final int count) {
            this.cached = null;
            Long cnt = this.exceptionMapperExecutionCountMap.get(mapper);
            cnt = ((cnt == null) ? count : (cnt + count));
            this.exceptionMapperExecutionCountMap.put(mapper, cnt);
        }
        
        public ExceptionMapperStatisticsImpl build() {
            if (this.cached == null) {
                this.cached = new ExceptionMapperStatisticsImpl(new HashMap(this.exceptionMapperExecutionCountMap), this.successfulMappings, this.unsuccessfulMappings, this.totalMappings, null);
            }
            return this.cached;
        }
    }
}
