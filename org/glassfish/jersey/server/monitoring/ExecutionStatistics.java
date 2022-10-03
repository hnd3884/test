package org.glassfish.jersey.server.monitoring;

import java.util.Map;
import java.util.Date;

public interface ExecutionStatistics
{
    Date getLastStartTime();
    
    Map<Long, TimeWindowStatistics> getTimeWindowStatistics();
    
    @Deprecated
    ExecutionStatistics snapshot();
}
