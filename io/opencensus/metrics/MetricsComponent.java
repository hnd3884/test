package io.opencensus.metrics;

import io.opencensus.metrics.export.ExportComponent;

public abstract class MetricsComponent
{
    public abstract ExportComponent getExportComponent();
    
    public abstract MetricRegistry getMetricRegistry();
    
    static MetricsComponent newNoopMetricsComponent() {
        return new NoopMetricsComponent();
    }
    
    private static final class NoopMetricsComponent extends MetricsComponent
    {
        private static final ExportComponent EXPORT_COMPONENT;
        private static final MetricRegistry METRIC_REGISTRY;
        
        @Override
        public ExportComponent getExportComponent() {
            return NoopMetricsComponent.EXPORT_COMPONENT;
        }
        
        @Override
        public MetricRegistry getMetricRegistry() {
            return NoopMetricsComponent.METRIC_REGISTRY;
        }
        
        static {
            EXPORT_COMPONENT = ExportComponent.newNoopExportComponent();
            METRIC_REGISTRY = MetricRegistry.newNoopMetricRegistry();
        }
    }
}
