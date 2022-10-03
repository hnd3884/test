package org.glassfish.jersey.server.internal.monitoring.jmx;

import javax.management.AttributeList;
import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanConstructorInfo;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import org.glassfish.jersey.internal.util.collection.Value;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;
import javax.management.DynamicMBean;

public class ExecutionStatisticsDynamicBean implements DynamicMBean
{
    private volatile ExecutionStatistics executionStatistics;
    private final Map<String, Value<Object>> attributeValues;
    private final MBeanInfo mBeanInfo;
    
    private MBeanInfo initMBeanInfo(final ExecutionStatistics initialStatistics) {
        final Map<Long, TimeWindowStatistics> statsMap = initialStatistics.getTimeWindowStatistics();
        final MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[statsMap.size() * 5];
        int i = 0;
        for (final TimeWindowStatistics stats : statsMap.values()) {
            final long interval = stats.getTimeWindow();
            final String postfix = this.convertIntervalToString((int)interval);
            String name = "MinTime[ms]_" + postfix;
            attrs[i++] = new MBeanAttributeInfo(name, "long", "Minimum request processing time in milliseconds in last " + postfix + ".", true, false, false);
            this.attributeValues.put(name, (Value<Object>)new Value<Object>() {
                public Object get() {
                    return ExecutionStatisticsDynamicBean.this.executionStatistics.getTimeWindowStatistics().get(interval).getMinimumDuration();
                }
            });
            name = "MaxTime[ms]_" + postfix;
            attrs[i++] = new MBeanAttributeInfo(name, "long", "Minimum request processing time  in milliseconds in last " + postfix + ".", true, false, false);
            this.attributeValues.put(name, (Value<Object>)new Value<Object>() {
                public Object get() {
                    return ExecutionStatisticsDynamicBean.this.executionStatistics.getTimeWindowStatistics().get(interval).getMaximumDuration();
                }
            });
            name = "AverageTime[ms]_" + postfix;
            attrs[i++] = new MBeanAttributeInfo(name, "long", "Average request processing time in milliseconds in last " + postfix + ".", true, false, false);
            this.attributeValues.put(name, (Value<Object>)new Value<Object>() {
                public Object get() {
                    return ExecutionStatisticsDynamicBean.this.executionStatistics.getTimeWindowStatistics().get(interval).getAverageDuration();
                }
            });
            name = "RequestRate[requestsPerSeconds]_" + postfix;
            attrs[i++] = new MBeanAttributeInfo(name, "double", "Average requests per second in last " + postfix + ".", true, false, false);
            this.attributeValues.put(name, (Value<Object>)new Value<Object>() {
                public Object get() {
                    return ExecutionStatisticsDynamicBean.this.executionStatistics.getTimeWindowStatistics().get(interval).getRequestsPerSecond();
                }
            });
            name = "RequestCount_" + postfix;
            attrs[i++] = new MBeanAttributeInfo(name, "double", "Request count in last " + postfix + ".", true, false, false);
            this.attributeValues.put(name, (Value<Object>)new Value<Object>() {
                public Object get() {
                    return ExecutionStatisticsDynamicBean.this.executionStatistics.getTimeWindowStatistics().get(interval).getRequestCount();
                }
            });
        }
        return new MBeanInfo(this.getClass().getName(), "Execution statistics", attrs, null, null, null);
    }
    
    private String convertIntervalToString(int interval) {
        final int hours = interval / 3600000;
        interval -= hours * 3600000;
        final int minutes = interval / 60000;
        interval -= minutes * 60000;
        final int seconds = interval / 1000;
        final StringBuffer sb = new StringBuffer();
        if (hours > 0) {
            sb.append(hours).append("h_");
        }
        if (minutes > 0) {
            sb.append(minutes).append("m_");
        }
        if (seconds > 0) {
            sb.append(seconds).append("s_");
        }
        if (sb.length() == 0) {
            sb.append("total");
        }
        else {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
    
    public ExecutionStatisticsDynamicBean(final ExecutionStatistics executionStatistics, final MBeanExposer mBeanExposer, final String parentBeanName, final String beanName) {
        this.attributeValues = new HashMap<String, Value<Object>>();
        this.executionStatistics = executionStatistics;
        this.mBeanInfo = this.initMBeanInfo(executionStatistics);
        mBeanExposer.registerMBean(this, parentBeanName + ",executionTimes=" + beanName);
    }
    
    public void updateExecutionStatistics(final ExecutionStatistics executionStatistics) {
        this.executionStatistics = executionStatistics;
    }
    
    @Override
    public Object getAttribute(final String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return this.attributeValues.get(attribute).get();
    }
    
    @Override
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    }
    
    @Override
    public AttributeList getAttributes(final String[] attributes) {
        return null;
    }
    
    @Override
    public AttributeList setAttributes(final AttributeList attributes) {
        return null;
    }
    
    @Override
    public Object invoke(final String actionName, final Object[] params, final String[] signature) throws MBeanException, ReflectionException {
        return null;
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        return this.mBeanInfo;
    }
}
