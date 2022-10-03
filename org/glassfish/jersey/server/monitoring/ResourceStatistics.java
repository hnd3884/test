package org.glassfish.jersey.server.monitoring;

import org.glassfish.jersey.server.model.ResourceMethod;
import java.util.Map;

public interface ResourceStatistics
{
    ExecutionStatistics getResourceMethodExecutionStatistics();
    
    ExecutionStatistics getRequestExecutionStatistics();
    
    Map<ResourceMethod, ResourceMethodStatistics> getResourceMethodStatistics();
    
    @Deprecated
    ResourceStatistics snapshot();
}
