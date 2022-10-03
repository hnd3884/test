package io.opencensus.metrics;

import io.opencensus.internal.Utils;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class DoubleGauge
{
    public abstract DoublePoint getOrCreateTimeSeries(final List<LabelValue> p0);
    
    public abstract DoublePoint getDefaultTimeSeries();
    
    public abstract void removeTimeSeries(final List<LabelValue> p0);
    
    public abstract void clear();
    
    static DoubleGauge newNoopDoubleGauge(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
        return NoopDoubleGauge.create(name, description, unit, labelKeys);
    }
    
    public abstract static class DoublePoint
    {
        public abstract void add(final double p0);
        
        public abstract void set(final double p0);
    }
    
    private static final class NoopDoubleGauge extends DoubleGauge
    {
        private final int labelKeysSize;
        
        static NoopDoubleGauge create(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
            return new NoopDoubleGauge(name, description, unit, labelKeys);
        }
        
        NoopDoubleGauge(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
            Utils.checkNotNull(name, "name");
            Utils.checkNotNull(description, "description");
            Utils.checkNotNull(unit, "unit");
            Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)labelKeys, "labelKeys"), "labelKey");
            this.labelKeysSize = labelKeys.size();
        }
        
        @Override
        public NoopDoublePoint getOrCreateTimeSeries(final List<LabelValue> labelValues) {
            Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)labelValues, "labelValues"), "labelValue");
            Utils.checkArgument(this.labelKeysSize == labelValues.size(), (Object)"Label Keys and Label Values don't have same size.");
            return NoopDoublePoint.INSTANCE;
        }
        
        @Override
        public NoopDoublePoint getDefaultTimeSeries() {
            return NoopDoublePoint.INSTANCE;
        }
        
        @Override
        public void removeTimeSeries(final List<LabelValue> labelValues) {
            Utils.checkNotNull(labelValues, "labelValues");
        }
        
        @Override
        public void clear() {
        }
        
        private static final class NoopDoublePoint extends DoublePoint
        {
            private static final NoopDoublePoint INSTANCE;
            
            @Override
            public void add(final double amt) {
            }
            
            @Override
            public void set(final double val) {
            }
            
            static {
                INSTANCE = new NoopDoublePoint();
            }
        }
    }
}
