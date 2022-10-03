package org.glassfish.jersey.server.internal.monitoring;

import java.util.Iterator;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;

public final class MonitoringUtils
{
    private static final double CACHEABLE_REQUEST_RATE_LIMIT = 0.001;
    
    public static String getMethodUniqueId(final ResourceMethod method) {
        final String path = (method.getParent() != null) ? createPath(method.getParent()) : "null";
        return method.getProducedTypes().toString() + "|" + method.getConsumedTypes().toString() + "|" + method.getHttpMethod() + "|" + path + "|" + method.getInvocable().getHandlingMethod().getName();
    }
    
    private static String createPath(final Resource resource) {
        return appendPath(resource, new StringBuilder()).toString();
    }
    
    private static StringBuilder appendPath(final Resource resource, final StringBuilder path) {
        return (resource.getParent() == null) ? path.append(resource.getPath()) : appendPath(resource.getParent(), path).append(".").append(resource.getPath());
    }
    
    static boolean isCacheable(final ExecutionStatistics stats) {
        for (final TimeWindowStatistics window : stats.getTimeWindowStatistics().values()) {
            if (window.getRequestsPerSecond() >= 0.001) {
                return false;
            }
        }
        return true;
    }
    
    private MonitoringUtils() {
    }
}
