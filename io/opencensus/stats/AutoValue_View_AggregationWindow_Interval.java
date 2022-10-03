package io.opencensus.stats;

import io.opencensus.common.Duration;
import javax.annotation.concurrent.Immutable;

@Deprecated
@Immutable
final class AutoValue_View_AggregationWindow_Interval extends Interval
{
    private final Duration duration;
    
    AutoValue_View_AggregationWindow_Interval(final Duration duration) {
        if (duration == null) {
            throw new NullPointerException("Null duration");
        }
        this.duration = duration;
    }
    
    @Override
    public Duration getDuration() {
        return this.duration;
    }
    
    @Override
    public String toString() {
        return "Interval{duration=" + this.duration + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Interval) {
            final Interval that = (Interval)o;
            return this.duration.equals(that.getDuration());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.duration.hashCode();
        return h;
    }
}
