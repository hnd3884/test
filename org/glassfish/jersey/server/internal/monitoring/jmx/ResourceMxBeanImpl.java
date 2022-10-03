package org.glassfish.jersey.server.internal.monitoring.jmx;

import java.util.Iterator;
import org.glassfish.jersey.server.internal.monitoring.MonitoringUtils;
import org.glassfish.jersey.server.monitoring.ResourceMethodStatistics;
import org.glassfish.jersey.server.model.ResourceMethod;
import java.util.HashMap;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ResourceMXBean;

public class ResourceMxBeanImpl implements ResourceMXBean
{
    private final String name;
    private volatile ExecutionStatisticsDynamicBean methodsExecutionStatisticsBean;
    private volatile ExecutionStatisticsDynamicBean requestExecutionStatisticsBean;
    private final Map<String, ResourceMethodMXBeanImpl> resourceMethods;
    private final String resourcePropertyName;
    private final boolean uriResource;
    private final MBeanExposer mBeanExposer;
    
    public ResourceMxBeanImpl(final ResourceStatistics resourceStatistics, final String name, final boolean uriResource, final MBeanExposer mBeanExposer, final String parentName) {
        this.resourceMethods = new HashMap<String, ResourceMethodMXBeanImpl>();
        this.name = name;
        this.uriResource = uriResource;
        (this.mBeanExposer = mBeanExposer).registerMBean(this, this.resourcePropertyName = parentName + ",resource=" + MBeanExposer.convertToObjectName(name, uriResource));
        this.methodsExecutionStatisticsBean = new ExecutionStatisticsDynamicBean(resourceStatistics.getResourceMethodExecutionStatistics(), mBeanExposer, this.resourcePropertyName, "MethodTimes");
        this.requestExecutionStatisticsBean = new ExecutionStatisticsDynamicBean(resourceStatistics.getRequestExecutionStatistics(), mBeanExposer, this.resourcePropertyName, "RequestTimes");
        this.updateResourceStatistics(resourceStatistics);
    }
    
    public void updateResourceStatistics(final ResourceStatistics resourceStatistics) {
        this.methodsExecutionStatisticsBean.updateExecutionStatistics(resourceStatistics.getResourceMethodExecutionStatistics());
        this.requestExecutionStatisticsBean.updateExecutionStatistics(resourceStatistics.getRequestExecutionStatistics());
        for (final Map.Entry<ResourceMethod, ResourceMethodStatistics> entry : resourceStatistics.getResourceMethodStatistics().entrySet()) {
            final ResourceMethodStatistics methodStats = entry.getValue();
            final ResourceMethod method = entry.getKey();
            final String methodId = MonitoringUtils.getMethodUniqueId(method);
            ResourceMethodMXBeanImpl methodMXBean = this.resourceMethods.get(methodId);
            if (methodMXBean == null) {
                methodMXBean = new ResourceMethodMXBeanImpl(methodStats, this.uriResource, this.mBeanExposer, this.resourcePropertyName, methodId);
                this.resourceMethods.put(methodId, methodMXBean);
            }
            methodMXBean.updateResourceMethodStatistics(methodStats);
        }
    }
    
    @Override
    public String getName() {
        return this.name;
    }
}
