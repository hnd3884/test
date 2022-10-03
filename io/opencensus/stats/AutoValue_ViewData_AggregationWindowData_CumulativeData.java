package io.opencensus.stats;

import io.opencensus.common.Timestamp;
import javax.annotation.concurrent.Immutable;

@Deprecated
@Immutable
final class AutoValue_ViewData_AggregationWindowData_CumulativeData extends CumulativeData
{
    private final Timestamp start;
    private final Timestamp end;
    
    AutoValue_ViewData_AggregationWindowData_CumulativeData(final Timestamp start, final Timestamp end) {
        if (start == null) {
            throw new NullPointerException("Null start");
        }
        this.start = start;
        if (end == null) {
            throw new NullPointerException("Null end");
        }
        this.end = end;
    }
    
    @Override
    public Timestamp getStart() {
        return this.start;
    }
    
    @Override
    public Timestamp getEnd() {
        return this.end;
    }
    
    @Override
    public String toString() {
        return "CumulativeData{start=" + this.start + ", end=" + this.end + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CumulativeData) {
            final CumulativeData that = (CumulativeData)o;
            return this.start.equals(that.getStart()) && this.end.equals(that.getEnd());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.start.hashCode();
        h *= 1000003;
        h ^= this.end.hashCode();
        return h;
    }
}
