package io.opencensus.metrics;

import io.opencensus.internal.Utils;
import java.util.List;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class LongCumulative
{
    public abstract LongPoint getOrCreateTimeSeries(final List<LabelValue> p0);
    
    public abstract LongPoint getDefaultTimeSeries();
    
    public abstract void removeTimeSeries(final List<LabelValue> p0);
    
    public abstract void clear();
    
    static LongCumulative newNoopLongCumulative(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
        return NoopLongCumulative.create(name, description, unit, labelKeys);
    }
    
    public abstract static class LongPoint
    {
        public abstract void add(final long p0);
    }
    
    private static final class NoopLongCumulative extends LongCumulative
    {
        private final int labelKeysSize;
        
        static NoopLongCumulative create(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
            return new NoopLongCumulative(name, description, unit, labelKeys);
        }
        
        NoopLongCumulative(final String name, final String description, final String unit, final List<LabelKey> labelKeys) {
            this.labelKeysSize = labelKeys.size();
        }
        
        @Override
        public NoopLongPoint getOrCreateTimeSeries(final List<LabelValue> labelValues) {
            Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)labelValues, "labelValues"), "labelValue");
            Utils.checkArgument(this.labelKeysSize == labelValues.size(), (Object)"Label Keys and Label Values don't have same size.");
            return NoopLongPoint.INSTANCE;
        }
        
        @Override
        public NoopLongPoint getDefaultTimeSeries() {
            return NoopLongPoint.INSTANCE;
        }
        
        @Override
        public void removeTimeSeries(final List<LabelValue> labelValues) {
            Utils.checkNotNull(labelValues, "labelValues");
        }
        
        @Override
        public void clear() {
        }
        
        private static final class NoopLongPoint extends LongPoint
        {
            private static final NoopLongPoint INSTANCE;
            
            @Override
            public void add(final long delta) {
            }
            
            static {
                INSTANCE = new NoopLongPoint();
            }
        }
    }
}
