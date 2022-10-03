package io.opencensus.metrics.export;

public abstract class ExportComponent
{
    public static ExportComponent newNoopExportComponent() {
        return new NoopExportComponent();
    }
    
    public abstract MetricProducerManager getMetricProducerManager();
    
    private static final class NoopExportComponent extends ExportComponent
    {
        private static final MetricProducerManager METRIC_PRODUCER_MANAGER;
        
        @Override
        public MetricProducerManager getMetricProducerManager() {
            return NoopExportComponent.METRIC_PRODUCER_MANAGER;
        }
        
        static {
            METRIC_PRODUCER_MANAGER = MetricProducerManager.newNoopMetricProducerManager();
        }
    }
}
