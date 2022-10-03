package io.opencensus.metrics;

import java.util.logging.Level;
import io.opencensus.internal.Provider;
import javax.annotation.Nullable;
import io.opencensus.metrics.export.ExportComponent;
import java.util.logging.Logger;

public final class Metrics
{
    private static final Logger logger;
    private static final MetricsComponent metricsComponent;
    
    public static ExportComponent getExportComponent() {
        return Metrics.metricsComponent.getExportComponent();
    }
    
    public static MetricRegistry getMetricRegistry() {
        return Metrics.metricsComponent.getMetricRegistry();
    }
    
    static MetricsComponent loadMetricsComponent(@Nullable final ClassLoader classLoader) {
        try {
            return Provider.createInstance(Class.forName("io.opencensus.impl.metrics.MetricsComponentImpl", true, classLoader), MetricsComponent.class);
        }
        catch (final ClassNotFoundException e) {
            Metrics.logger.log(Level.FINE, "Couldn't load full implementation for MetricsComponent, now trying to load lite implementation.", e);
            try {
                return Provider.createInstance(Class.forName("io.opencensus.impllite.metrics.MetricsComponentImplLite", true, classLoader), MetricsComponent.class);
            }
            catch (final ClassNotFoundException e) {
                Metrics.logger.log(Level.FINE, "Couldn't load lite implementation for MetricsComponent, now using default implementation for MetricsComponent.", e);
                return MetricsComponent.newNoopMetricsComponent();
            }
        }
    }
    
    private Metrics() {
    }
    
    static {
        logger = Logger.getLogger(Metrics.class.getName());
        metricsComponent = loadMetricsComponent(MetricsComponent.class.getClassLoader());
    }
}
