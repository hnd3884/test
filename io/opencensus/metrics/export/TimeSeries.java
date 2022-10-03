package io.opencensus.metrics.export;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import io.opencensus.internal.Utils;
import javax.annotation.Nullable;
import io.opencensus.common.Timestamp;
import io.opencensus.metrics.LabelValue;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class TimeSeries
{
    TimeSeries() {
    }
    
    public static TimeSeries create(final List<LabelValue> labelValues, final List<Point> points, @Nullable final Timestamp startTimestamp) {
        Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)points, "points"), "point");
        return createInternal(labelValues, Collections.unmodifiableList((List<? extends Point>)new ArrayList<Point>(points)), startTimestamp);
    }
    
    public static TimeSeries create(final List<LabelValue> labelValues) {
        return createInternal(labelValues, Collections.emptyList(), null);
    }
    
    public static TimeSeries createWithOnePoint(final List<LabelValue> labelValues, final Point point, @Nullable final Timestamp startTimestamp) {
        Utils.checkNotNull(point, "point");
        return createInternal(labelValues, Collections.singletonList(point), startTimestamp);
    }
    
    public TimeSeries setPoint(final Point point) {
        Utils.checkNotNull(point, "point");
        return new AutoValue_TimeSeries(this.getLabelValues(), Collections.singletonList(point), null);
    }
    
    private static TimeSeries createInternal(final List<LabelValue> labelValues, final List<Point> points, @Nullable final Timestamp startTimestamp) {
        Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)labelValues, "labelValues"), "labelValue");
        return new AutoValue_TimeSeries(Collections.unmodifiableList((List<? extends LabelValue>)new ArrayList<LabelValue>(labelValues)), points, startTimestamp);
    }
    
    public abstract List<LabelValue> getLabelValues();
    
    public abstract List<Point> getPoints();
    
    @Nullable
    public abstract Timestamp getStartTimestamp();
}
