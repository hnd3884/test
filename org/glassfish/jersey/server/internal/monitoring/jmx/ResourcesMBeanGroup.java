package org.glassfish.jersey.server.internal.monitoring.jmx;

import java.util.Iterator;
import java.util.HashMap;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;
import java.util.Map;

public class ResourcesMBeanGroup
{
    private final Map<String, ResourceMxBeanImpl> exposedResourceMBeans;
    private final String parentName;
    private final boolean uriResource;
    private final MBeanExposer exposer;
    
    public ResourcesMBeanGroup(final Map<String, ResourceStatistics> resourceStatistics, final boolean uriResource, final MBeanExposer mBeanExposer, final String parentName) {
        this.exposedResourceMBeans = new HashMap<String, ResourceMxBeanImpl>();
        this.uriResource = uriResource;
        this.exposer = mBeanExposer;
        this.parentName = parentName;
        this.updateResourcesStatistics(resourceStatistics);
    }
    
    public void updateResourcesStatistics(final Map<String, ResourceStatistics> resourceStatistics) {
        for (final Map.Entry<String, ResourceStatistics> entry : resourceStatistics.entrySet()) {
            ResourceMxBeanImpl resourceMxBean = this.exposedResourceMBeans.get(entry.getKey());
            if (resourceMxBean == null) {
                resourceMxBean = new ResourceMxBeanImpl(entry.getValue(), entry.getKey(), this.uriResource, this.exposer, this.parentName);
                this.exposedResourceMBeans.put(entry.getKey(), resourceMxBean);
            }
            resourceMxBean.updateResourceStatistics(entry.getValue());
        }
    }
}
