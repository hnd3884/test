package io.opencensus.metrics;

import io.opencensus.internal.Utils;
import io.opencensus.common.ToLongFunction;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class DerivedLongCumulative
{
    public abstract <T> void createTimeSeries(final List<LabelValue> p0, final T p1, final ToLongFunction<T> p2);
    
    public abstract void removeTimeSeries(final List<LabelValue> p0);
    
    public abstract void clear();
    
    static DerivedLongCumulative newNoopDerivedLongCumulative(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
        return NoopDerivedLongCumulative.create(name, description, unit, labelKeys);
    }
    
    private static final class NoopDerivedLongCumulative extends DerivedLongCumulative
    {
        private final int labelKeysSize;
        
        static NoopDerivedLongCumulative create(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
            return new NoopDerivedLongCumulative(name, description, unit, labelKeys);
        }
        
        NoopDerivedLongCumulative(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
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
