package io.opencensus.metrics.export;

import javax.annotation.Nullable;
import io.opencensus.common.Timestamp;
import io.opencensus.metrics.LabelValue;
import java.util.List;

final class AutoValue_TimeSeries extends TimeSeries
{
    private final List<LabelValue> labelValues;
    private final List<Point> points;
    private final Timestamp startTimestamp;
    
    AutoValue_TimeSeries(final List<LabelValue> labelValues, final List<Point> points, @Nullable final Timestamp startTimestamp) {
        if (labelValues == null) {
            throw new NullPointerException("Null labelValues");
        }
        this.labelValues = labelValues;
        if (points == null) {
            throw new NullPointerException("Null points");
        }
        this.points = points;
        this.startTimestamp = startTimestamp;
    }
    
    @Override
    public List<LabelValue> getLabelValues() {
        return this.labelValues;
    }
    
    @Override
    public List<Point> getPoints() {
        return this.points;
    }
    
    @Nullable
    @Override
    public Timestamp getStartTimestamp() {
        return this.startTimestamp;
    }
    
    @Override
    public String toString() {
        return "TimeSeries{labelValues=" + this.labelValues + ", points=" + this.points + ", startTimestamp=" + this.startTimestamp + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TimeSeries) {
            final TimeSeries that = (TimeSeries)o;
            return this.labelValues.equals(that.getLabelValues()) && this.points.equals(that.getPoints()) && ((this.startTimestamp != null) ? this.startTimestamp.equals(that.getStartTimestamp()) : (that.getStartTimestamp() == null));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.labelValues.hashCode();
        h *= 1000003;
        h ^= this.points.hashCode();
        h *= 1000003;
        h ^= ((this.startTimestamp == null) ? 0 : this.startTimestamp.hashCode());
        return h;
    }
}
