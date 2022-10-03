package io.opencensus.metrics;

import io.opencensus.internal.Utils;
import io.opencensus.common.ToDoubleFunction;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class DerivedDoubleGauge
{
    public abstract <T> void createTimeSeries(final List<LabelValue> p0, final T p1, final ToDoubleFunction<T> p2);
    
    public abstract void removeTimeSeries(final List<LabelValue> p0);
    
    public abstract void clear();
    
    static DerivedDoubleGauge newNoopDerivedDoubleGauge(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
        return NoopDerivedDoubleGauge.create(name, description, unit, labelKeys);
    }
    
    private static final class NoopDerivedDoubleGauge extends DerivedDoubleGauge
    {
        private final int labelKeysSize;
        
        static NoopDerivedDoubleGauge create(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
            return new NoopDerivedDoubleGauge(name, description, unit, labelKeys);
        }
        
        NoopDerivedDoubleGauge(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
            Utils.checkNotNull(name, "name");
            Utils.checkNotNull(description, "description");
            Utils.checkNotNull(unit, "unit");
            Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)labelKeys, "labelKeys"), "labelKey");
            this.labelKeysSize = labelKeys.size();
        }
        
        @Override
        public <T> void createTimeSeries(final List<LabelValue> labelValues, final T obj, final ToDoubleFunction<T> function) {
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
