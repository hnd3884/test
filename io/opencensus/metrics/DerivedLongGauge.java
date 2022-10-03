package io.opencensus.metrics;

import io.opencensus.internal.Utils;
import io.opencensus.common.ToLongFunction;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class DerivedLongGauge
{
    public abstract <T> void createTimeSeries(final List<LabelValue> p0, final T p1, final ToLongFunction<T> p2);
    
    public abstract void removeTimeSeries(final List<LabelValue> p0);
    
    public abstract void clear();
    
    static DerivedLongGauge newNoopDerivedLongGauge(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
        return NoopDerivedLongGauge.create(name, description, unit, labelKeys);
    }
    
    private static final class NoopDerivedLongGauge extends DerivedLongGauge
    {
        private final int labelKeysSize;
        
        static NoopDerivedLongGauge create(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
            return new NoopDerivedLongGauge(name, description, unit, labelKeys);
        }
        
        NoopDerivedLongGauge(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
            Utils.checkNotNull(name, "name");
            Utils.checkNotNull(description, "description");
            Utils.checkNotNull(unit, "unit");
            Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)labelKeys, "labelKeys"), "labelKey");
            this.labelKeysSize = labelKeys.size();
        }
        
        @Override
        public <T> void createTimeSeries(final List<LabelValue> labelValues, final T obj, final ToLongFunction<T> function) {
            Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)labelValues, "labelValues"), "labelValue");
            Utils.checkArgument(this.labelKeysSize == labelValues.size(), (Object)"Label Keys and Label Values don't have same size.");
            Utils.checkNotNull(function, "function");
        }
        
        @Override
        public void removeTimeSeries(final List<LabelValue> labelValues) {
            Utils.checkNotNull(labelValues, "labelValues");
        }
        
        @Override
        public void clear() {
        }
    }
}
