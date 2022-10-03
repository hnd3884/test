package io.opencensus.metrics;

import io.opencensus.internal.Utils;
import java.util.List;

public abstract class MetricRegistry
{
    @Deprecated
    public LongGauge addLongGauge(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
        return this.addLongGauge(name, MetricOptions.builder().setDescription(description).setUnit(unit).setLabelKeys(labelKeys).build());
    }
    
    public abstract LongGauge addLongGauge(final String p0, final MetricOptions p1);
    
    @Deprecated
    public DoubleGauge addDoubleGauge(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
        return this.addDoubleGauge(name, MetricOptions.builder().setDescription(description).setUnit(unit).setLabelKeys(labelKeys).build());
    }
    
    public abstract DoubleGauge addDoubleGauge(final String p0, final MetricOptions p1);
    
    @Deprecated
    public DerivedLongGauge addDerivedLongGauge(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
        return this.addDerivedLongGauge(name, MetricOptions.builder().setDescription(description).setUnit(unit).setLabelKeys(labelKeys).build());
    }
    
    public abstract DerivedLongGauge addDerivedLongGauge(final String p0, final MetricOptions p1);
    
    @Deprecated
    public DerivedDoubleGauge addDerivedDoubleGauge(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
        return this.addDerivedDoubleGauge(name, MetricOptions.builder().setDescription(description).setUnit(unit).setLabelKeys(labelKeys).build());
    }
    
    public abstract DerivedDoubleGauge addDerivedDoubleGauge(final String p0, final MetricOptions p1);
    
    public abstract LongCumulative addLongCumulative(final String p0, final MetricOptions p1);
    
    public abstract DoubleCumulative addDoubleCumulative(final String p0, final MetricOptions p1);
    
    public abstract DerivedLongCumulative addDerivedLongCumulative(final String p0, final MetricOptions p1);
    
    public abstract DerivedDoubleCumulative addDerivedDoubleCumulative(final String p0, final MetricOptions p1);
    
    static MetricRegistry newNoopMetricRegistry() {
        return new NoopMetricRegistry();
    }
    
    private static final class NoopMetricRegistry extends MetricRegistry
    {
        @Override
        public LongGauge addLongGauge(final String name, final MetricOptions options) {
            return LongGauge.newNoopLongGauge(Utils.checkNotNull(name, "name"), options.getDescription(), options.getUnit(), options.getLabelKeys());
        }
        
        @Override
        public DoubleGauge addDoubleGauge(final String name, final MetricOptions options) {
            return DoubleGauge.newNoopDoubleGauge(Utils.checkNotNull(name, "name"), options.getDescription(), options.getUnit(), options.getLabelKeys());
        }
        
        @Override
        public DerivedLongGauge addDerivedLongGauge(final String name, final MetricOptions options) {
            return DerivedLongGauge.newNoopDerivedLongGauge(Utils.checkNotNull(name, "name"), options.getDescription(), options.getUnit(), options.getLabelKeys());
        }
        
        @Override
        public DerivedDoubleGauge addDerivedDoubleGauge(final String name, final MetricOptions options) {
            return DerivedDoubleGauge.newNoopDerivedDoubleGauge(Utils.checkNotNull(name, "name"), options.getDescription(), options.getUnit(), options.getLabelKeys());
        }
        
        @Override
        public LongCumulative addLongCumulative(final String name, final MetricOptions options) {
            return LongCumulative.newNoopLongCumulative(Utils.checkNotNull(name, "name"), options.getDescription(), options.getUnit(), options.getLabelKeys());
        }
        
        @Override
        public DoubleCumulative addDoubleCumulative(final String name, final MetricOptions options) {
            return DoubleCumulative.newNoopDoubleCumulative(Utils.checkNotNull(name, "name"), options.getDescription(), options.getUnit(), options.getLabelKeys());
        }
        
        @Override
        public DerivedLongCumulative addDerivedLongCumulative(final String name, final MetricOptions options) {
            return DerivedLongCumulative.newNoopDerivedLongCumulative(Utils.checkNotNull(name, "name"), options.getDescription(), options.getUnit(), options.getLabelKeys());
        }
        
        @Override
        public DerivedDoubleCumulative addDerivedDoubleCumulative(final String name, final MetricOptions options) {
            return DerivedDoubleCumulative.newNoopDerivedDoubleCumulative(Utils.checkNotNull(name, "name"), options.getDescription(), options.getUnit(), options.getLabelKeys());
        }
    }
}
