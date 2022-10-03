package org.glassfish.jersey.server.internal.monitoring.jmx;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ExceptionMapperStatistics;
import org.glassfish.jersey.server.monitoring.ExceptionMapperMXBean;

public class ExceptionMapperMXBeanImpl implements ExceptionMapperMXBean
{
    private volatile ExceptionMapperStatistics mapperStatistics;
    private volatile Map<String, Long> mapperExcecutions;
    
    public ExceptionMapperMXBeanImpl(final ExceptionMapperStatistics mapperStatistics, final MBeanExposer mBeanExposer, final String parentName) {
        this.mapperExcecutions = new HashMap<String, Long>();
        mBeanExposer.registerMBean(this, parentName + ",exceptions=ExceptionMapper");
        this.updateExceptionMapperStatistics(mapperStatistics);
    }
    
    public void updateExceptionMapperStatistics(final ExceptionMapperStatistics mapperStatistics) {
        this.mapperStatistics = mapperStatistics;
        for (final Map.Entry<Class<?>, Long> entry : mapperStatistics.getExceptionMapperExecutions().entrySet()) {
            this.mapperExcecutions.put(entry.getKey().getName(), entry.getValue());
        }
    }
    
    @Override
    public Map<String, Long> getExceptionMapperCount() {
        return this.mapperExcecutions;
    }
    
    @Override
    public long getSuccessfulMappings() {
        return this.mapperStatistics.getSuccessfulMappings();
    }
    
    @Override
    public long getUnsuccessfulMappings() {
        return this.mapperStatistics.getUnsuccessfulMappings();
    }
    
    @Override
    public long getTotalMappings() {
        return this.mapperStatistics.getTotalMappings();
    }
}
