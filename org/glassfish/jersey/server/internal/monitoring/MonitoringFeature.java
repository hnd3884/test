package org.glassfish.jersey.server.internal.monitoring;

import javax.inject.Inject;
import javax.inject.Provider;
import org.glassfish.jersey.server.internal.monitoring.jmx.MBeanExposer;
import org.glassfish.jersey.server.monitoring.MonitoringStatisticsListener;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import java.lang.reflect.Type;
import javax.inject.Singleton;
import org.glassfish.jersey.server.monitoring.ApplicationInfo;
import org.glassfish.jersey.internal.util.collection.Ref;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.inject.ReferencingFactory;
import org.glassfish.jersey.internal.inject.SupplierInstanceBinding;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import java.util.Map;
import org.glassfish.jersey.server.ServerProperties;
import javax.ws.rs.core.FeatureContext;
import java.util.logging.Logger;
import javax.ws.rs.core.Feature;

public final class MonitoringFeature implements Feature
{
    private static final Logger LOGGER;
    private boolean monitoringEnabled;
    private boolean statisticsEnabled;
    private boolean mBeansEnabled;
    
    public MonitoringFeature() {
        this.monitoringEnabled = true;
        this.statisticsEnabled = true;
    }
    
    public boolean configure(final FeatureContext context) {
        final Boolean monitoringEnabledProperty = ServerProperties.getValue(context.getConfiguration().getProperties(), "jersey.config.server.monitoring.enabled", null, Boolean.class);
        final Boolean statisticsEnabledProperty = ServerProperties.getValue(context.getConfiguration().getProperties(), "jersey.config.server.monitoring.statistics.enabled", null, Boolean.class);
        final Boolean mbeansEnabledProperty = ServerProperties.getValue(context.getConfiguration().getProperties(), "jersey.config.server.monitoring.statistics.mbeans.enabled", null, Boolean.class);
        if (monitoringEnabledProperty != null) {
            this.monitoringEnabled = monitoringEnabledProperty;
            this.statisticsEnabled = this.monitoringEnabled;
        }
        if (statisticsEnabledProperty != null) {
            this.monitoringEnabled = (this.monitoringEnabled || statisticsEnabledProperty);
            this.statisticsEnabled = statisticsEnabledProperty;
        }
        if (mbeansEnabledProperty != null) {
            this.monitoringEnabled = (this.monitoringEnabled || mbeansEnabledProperty);
            this.statisticsEnabled = (this.statisticsEnabled || mbeansEnabledProperty);
            this.mBeansEnabled = mbeansEnabledProperty;
        }
        if (statisticsEnabledProperty != null && !statisticsEnabledProperty) {
            if (mbeansEnabledProperty != null && this.mBeansEnabled) {
                MonitoringFeature.LOGGER.log(Level.WARNING, LocalizationMessages.WARNING_MONITORING_FEATURE_ENABLED("jersey.config.server.monitoring.statistics.enabled"));
            }
            else {
                MonitoringFeature.LOGGER.log(Level.WARNING, LocalizationMessages.WARNING_MONITORING_FEATURE_DISABLED("jersey.config.server.monitoring.statistics.enabled"));
            }
        }
        if (this.monitoringEnabled) {
            context.register((Class)ApplicationInfoListener.class);
            context.register((Object)new AbstractBinder() {
                protected void configure() {
                    ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to((GenericType)new GenericType<Ref<ApplicationInfo>>() {})).in((Class)Singleton.class);
                    this.bindFactory((Class)ApplicationInfoInjectionFactory.class).to((Type)ApplicationInfo.class);
                }
            });
        }
        if (this.statisticsEnabled) {
            context.register((Class)MonitoringEventListener.class);
            context.register((Object)new AbstractBinder() {
                protected void configure() {
                    ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to((GenericType)new GenericType<Ref<MonitoringStatistics>>() {})).in((Class)Singleton.class);
                    this.bindFactory((Class)StatisticsInjectionFactory.class).to((Type)MonitoringStatistics.class);
                    ((ClassBinding)this.bind((Class)StatisticsListener.class).to((Class)MonitoringStatisticsListener.class)).in((Class)Singleton.class);
                }
            });
        }
        if (this.mBeansEnabled) {
            context.register((Object)new MBeanExposer());
        }
        return this.monitoringEnabled;
    }
    
    public void setmBeansEnabled(final boolean mBeansEnabled) {
        this.mBeansEnabled = mBeansEnabled;
    }
    
    static {
        LOGGER = Logger.getLogger(MonitoringFeature.class.getName());
    }
    
    private static class ApplicationInfoInjectionFactory extends ReferencingFactory<ApplicationInfo>
    {
        @Inject
        public ApplicationInfoInjectionFactory(final Provider<Ref<ApplicationInfo>> referenceFactory) {
            super((Provider)referenceFactory);
        }
    }
    
    private static class StatisticsInjectionFactory extends ReferencingFactory<MonitoringStatistics>
    {
        @Inject
        public StatisticsInjectionFactory(final Provider<Ref<MonitoringStatistics>> referenceFactory) {
            super((Provider)referenceFactory);
        }
        
        public MonitoringStatistics get() {
            return (MonitoringStatistics)super.get();
        }
    }
    
    private static class StatisticsListener implements MonitoringStatisticsListener
    {
        @Inject
        Provider<Ref<MonitoringStatistics>> statisticsFactory;
        
        @Override
        public void onStatistics(final MonitoringStatistics statistics) {
            ((Ref)this.statisticsFactory.get()).set((Object)statistics);
        }
    }
}
