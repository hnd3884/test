package io.opencensus.metrics.export;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import io.opencensus.internal.Utils;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Summary
{
    Summary() {
    }
    
    public static Summary create(@Nullable final Long count, @Nullable final Double sum, final Snapshot snapshot) {
        checkCountAndSum(count, sum);
        Utils.checkNotNull(snapshot, "snapshot");
        return new AutoValue_Summary(count, sum, snapshot);
    }
    
    @Nullable
    public abstract Long getCount();
    
    @Nullable
    public abstract Double getSum();
    
    public abstract Snapshot getSnapshot();
    
    private static void checkCountAndSum(@Nullable final Long count, @Nullable final Double sum) {
        Utils.checkArgument(count == null || count >= 0L, (Object)"count must be non-negative.");
        Utils.checkArgument(sum == null || sum >= 0.0, (Object)"sum must be non-negative.");
        if (count != null && count == 0L) {
            Utils.checkArgument(sum == null || sum == 0.0, (Object)"sum must be 0 if count is 0.");
        }
    }
    
    @Immutable
    public abstract static class Snapshot
    {
        @Nullable
        public abstract Long getCount();
        
        @Nullable
        public abstract Double getSum();
        
        public abstract List<ValueAtPercentile> getValueAtPercentiles();
        
        public static Snapshot create(@Nullable final Long count, @Nullable final Double sum, final List<ValueAtPercentile> valueAtPercentiles) {
            checkCountAndSum(count, sum);
            Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)valueAtPercentiles, "valueAtPercentiles"), "valueAtPercentile");
            return new AutoValue_Summary_Snapshot(count, sum, Collections.unmodifiableList((List<? extends ValueAtPercentile>)new ArrayList<ValueAtPercentile>(valueAtPercentiles)));
        }
        
        @Immutable
        public abstract static class ValueAtPercentile
        {
            public abstract double getPercentile();
            
            public abstract double getValue();
            
            public static ValueAtPercentile create(final double percentile, final double value) {
                Utils.checkArgument(0.0 < percentile && percentile <= 100.0, (Object)"percentile must be in the interval (0.0, 100.0]");
                Utils.checkArgument(value >= 0.0, (Object)"value must be non-negative");
                return new AutoValue_Summary_Snapshot_ValueAtPercentile(percentile, value);
            }
        }
    }
}
