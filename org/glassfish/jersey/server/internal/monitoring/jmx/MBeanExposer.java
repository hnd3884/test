package org.glassfish.jersey.server.internal.monitoring.jmx;

import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import java.util.Set;
import javax.management.QueryExp;
import javax.management.MBeanServer;
import javax.management.JMException;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.HashMap;
import org.glassfish.jersey.server.monitoring.ResourceStatistics;
import java.util.Map;
import javax.inject.Inject;
import org.glassfish.jersey.server.monitoring.ApplicationInfo;
import javax.inject.Provider;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.glassfish.jersey.server.monitoring.MonitoringStatisticsListener;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;

public class MBeanExposer extends AbstractContainerLifecycleListener implements MonitoringStatisticsListener
{
    private static final Logger LOGGER;
    private static final String PROPERTY_SUBTYPE_GLOBAL = "Global";
    static final String PROPERTY_EXECUTION_TIMES_REQUESTS = "RequestTimes";
    static final String PROPERTY_EXECUTION_TIMES_METHODS = "MethodTimes";
    private volatile ExecutionStatisticsDynamicBean requestMBean;
    private volatile ResponseMXBeanImpl responseMXBean;
    private volatile ResourcesMBeanGroup uriStatsGroup;
    private volatile ResourcesMBeanGroup resourceClassStatsGroup;
    private volatile ExceptionMapperMXBeanImpl exceptionMapperMXBean;
    private final AtomicBoolean destroyed;
    private final Object LOCK;
    private volatile String domain;
    @Inject
    private Provider<ApplicationInfo> applicationInfoProvider;
    
    public MBeanExposer() {
        this.destroyed = new AtomicBoolean(false);
        this.LOCK = new Object();
    }
    
    private Map<String, ResourceStatistics> transformToStringKeys(final Map<Class<?>, ResourceStatistics> stats) {
        final Map<String, ResourceStatistics> newMap = new HashMap<String, ResourceStatistics>();
        for (final Map.Entry<Class<?>, ResourceStatistics> entry : stats.entrySet()) {
            newMap.put(entry.getKey().getName(), entry.getValue());
        }
        return newMap;
    }
    
    static String convertToObjectName(final String name, final boolean isUri) {
        if (!isUri) {
            return name;
        }
        String str = name.replace("\\", "\\\\");
        str = str.replace("?", "\\?");
        str = str.replace("*", "\\*");
        return "\"" + str + "\"";
    }
    
    void registerMBean(final Object mbean, final String namePostfix) {
        final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        final String name = this.domain + namePostfix;
        try {
            synchronized (this.LOCK) {
                if (this.destroyed.get()) {
                    return;
                }
                final ObjectName objectName = new ObjectName(name);
                if (mBeanServer.isRegistered(objectName)) {
                    MBeanExposer.LOGGER.log(Level.WARNING, LocalizationMessages.WARNING_MONITORING_MBEANS_BEAN_ALREADY_REGISTERED(objectName));
                    mBeanServer.unregisterMBean(objectName);
                }
                mBeanServer.registerMBean(mbean, objectName);
            }
        }
        catch (final JMException e) {
            throw new ProcessingException(LocalizationMessages.ERROR_MONITORING_MBEANS_REGISTRATION(name), (Throwable)e);
        }
    }
    
    private void unregisterJerseyMBeans(final boolean destroy) {
        final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            synchronized (this.LOCK) {
                if (destroy) {
                    this.destroyed.set(true);
                }
                if (this.domain == null) {
                    return;
                }
                final Set<ObjectName> names = mBeanServer.queryNames(new ObjectName(this.domain + ",*"), null);
                for (final ObjectName name : names) {
                    mBeanServer.unregisterMBean(name);
                }
            }
        }
        catch (final Exception e) {
            throw new ProcessingException(LocalizationMessages.ERROR_MONITORING_MBEANS_UNREGISTRATION_DESTROY(), (Throwable)e);
        }
    }
    
    @Override
    public void onStatistics(final MonitoringStatistics statistics) {
        if (this.domain == null) {
            final String globalSubType = ",subType=Global";
            final ApplicationInfo appStats = (ApplicationInfo)this.applicationInfoProvider.get();
            String appName = appStats.getResourceConfig().getApplicationName();
            if (appName == null) {
                appName = "App_" + Integer.toHexString(appStats.getResourceConfig().hashCode());
            }
            this.domain = "org.glassfish.jersey:type=" + appName;
            this.unregisterJerseyMBeans(false);
            this.uriStatsGroup = new ResourcesMBeanGroup(statistics.getUriStatistics(), true, this, ",subType=Uris");
            final Map<String, ResourceStatistics> newMap = this.transformToStringKeys(statistics.getResourceClassStatistics());
            this.resourceClassStatsGroup = new ResourcesMBeanGroup(newMap, false, this, ",subType=Resources");
            this.registerMBean(this.responseMXBean = new ResponseMXBeanImpl(), ",subType=Global,global=Responses");
            this.requestMBean = new ExecutionStatisticsDynamicBean(statistics.getRequestStatistics(), this, ",subType=Global", "AllRequestTimes");
            this.exceptionMapperMXBean = new ExceptionMapperMXBeanImpl(statistics.getExceptionMapperStatistics(), this, ",subType=Global");
            new ApplicationMXBeanImpl(appStats, this, ",subType=Global");
        }
        this.requestMBean.updateExecutionStatistics(statistics.getRequestStatistics());
        this.uriStatsGroup.updateResourcesStatistics(statistics.getUriStatistics());
        this.responseMXBean.updateResponseStatistics(statistics.getResponseStatistics());
        this.exceptionMapperMXBean.updateExceptionMapperStatistics(statistics.getExceptionMapperStatistics());
        this.resourceClassStatsGroup.updateResourcesStatistics(this.transformToStringKeys(statistics.getResourceClassStatistics()));
    }
    
    @Override
    public void onShutdown(final Container container) {
        this.unregisterJerseyMBeans(true);
    }
    
    static {
        LOGGER = Logger.getLogger(MBeanExposer.class.getName());
    }
}
