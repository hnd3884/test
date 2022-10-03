package io.opencensus.stats;

import io.opencensus.common.Timestamp;
import javax.annotation.concurrent.Immutable;

@Deprecated
@Immutable
final class AutoValue_ViewData_AggregationWindowData_IntervalData extends IntervalData
{
    private final Timestamp end;
    
    AutoValue_ViewData_AggregationWindowData_IntervalData(final Timestamp end) {
        if (end == null) {
            throw new NullPointerException("Null end");
        }
        this.end = end;
    }
    
    @Override
    public Timestamp getEnd() {
        return this.end;
    }
    
    @Override
    public String toString() {
        return "IntervalData{end=" + this.end + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof IntervalData) {
            final IntervalData that = (IntervalData)o;
            return this.end.equals(that.getEnd());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.end.hashCode();
        return h;
    }
}
