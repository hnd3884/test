package io.opencensus.metrics.export;

import java.util.Collections;
import io.opencensus.internal.Utils;
import java.util.Set;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class MetricProducerManager
{
    public abstract void add(final MetricProducer p0);
    
    public abstract void remove(final MetricProducer p0);
    
    public abstract Set<MetricProducer> getAllMetricProducer();
    
    static MetricProducerManager newNoopMetricProducerManager() {
        return new NoopMetricProducerManager();
    }
    
    private static final class NoopMetricProducerManager extends MetricProducerManager
    {
        @Override
        public void add(final MetricProducer metricProducer) {
            Utils.checkNotNull(metricProducer, "metricProducer");
        }
        
        @Override
        public void remove(final MetricProducer metricProducer) {
            Utils.checkNotNull(metricProducer, "metricProducer");
        }
        
        @Override
        public Set<MetricProducer> getAllMetricProducer() {
            return Collections.emptySet();
        }
    }
}
