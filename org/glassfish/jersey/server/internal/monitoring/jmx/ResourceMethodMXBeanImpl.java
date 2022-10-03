package org.glassfish.jersey.server.internal.monitoring.jmx;

import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.server.monitoring.ResourceMethodStatistics;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.ResourceMethodMXBean;

public class ResourceMethodMXBeanImpl implements ResourceMethodMXBean
{
    private volatile ExecutionStatisticsDynamicBean methodExecutionStatisticsMxBean;
    private volatile ExecutionStatisticsDynamicBean requestExecutionStatisticsMxBean;
    private final String path;
    private final String name;
    private final ResourceMethod resourceMethod;
    private final String methodBeanName;
    
    public ResourceMethodMXBeanImpl(final ResourceMethodStatistics methodStatistics, final boolean uriResource, final MBeanExposer mBeanExposer, final String parentName, final String methodUniqueId) {
        this.resourceMethod = methodStatistics.getResourceMethod();
        final Class<?> handlerClass = this.resourceMethod.getInvocable().getHandler().getHandlerClass();
        final Class<?>[] paramTypes = this.resourceMethod.getInvocable().getHandlingMethod().getParameterTypes();
        this.name = this.resourceMethod.getInvocable().getHandlingMethod().getName();
        final StringBuilder params = new StringBuilder();
        for (final Class<?> type : paramTypes) {
            params.append(type.getSimpleName()).append(";");
        }
        if (params.length() > 0) {
            params.setLength(params.length() - 1);
        }
        if (uriResource) {
            this.path = "N/A";
        }
        else {
            this.path = ((this.resourceMethod.getParent().getParent() == null) ? "" : this.resourceMethod.getParent().getPath());
        }
        final String hash = Integer.toHexString(methodUniqueId.hashCode());
        String beanName = this.resourceMethod.getHttpMethod() + "->";
        if (uriResource) {
            beanName = beanName + handlerClass.getSimpleName() + "." + this.name + "(" + params.toString() + ")#" + hash;
        }
        else {
            beanName = beanName + this.name + "(" + params.toString() + ")#" + hash;
        }
        mBeanExposer.registerMBean(this, this.methodBeanName = parentName + ",detail=methods,method=" + beanName);
        this.methodExecutionStatisticsMxBean = new ExecutionStatisticsDynamicBean(methodStatistics.getMethodStatistics(), mBeanExposer, this.methodBeanName, "MethodTimes");
        this.requestExecutionStatisticsMxBean = new ExecutionStatisticsDynamicBean(methodStatistics.getRequestStatistics(), mBeanExposer, this.methodBeanName, "RequestTimes");
    }
    
    public void updateResourceMethodStatistics(final ResourceMethodStatistics resourceMethodStatisticsImpl) {
        this.methodExecutionStatisticsMxBean.updateExecutionStatistics(resourceMethodStatisticsImpl.getMethodStatistics());
        this.requestExecutionStatisticsMxBean.updateExecutionStatistics(resourceMethodStatisticsImpl.getRequestStatistics());
    }
    
    @Override
    public String getPath() {
        return this.path;
    }
    
    @Override
    public String getHttpMethod() {
        return this.resourceMethod.getHttpMethod();
    }
    
    @Override
    public String getDeclaringClassName() {
        return this.resourceMethod.getInvocable().getHandlingMethod().getDeclaringClass().getName();
    }
    
    @Override
    public String getConsumesMediaType() {
        return MediaTypes.convertToString((Iterable)this.resourceMethod.getConsumedTypes());
    }
    
    @Override
    public String getProducesMediaType() {
        return MediaTypes.convertToString((Iterable)this.resourceMethod.getProducedTypes());
    }
    
    @Override
    public String getMethodName() {
        return this.name;
    }
}
