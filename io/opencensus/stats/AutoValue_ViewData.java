package io.opencensus.stats;

import io.opencensus.common.Timestamp;
import io.opencensus.tags.TagValue;
import java.util.List;
import java.util.Map;
import javax.annotation.concurrent.Immutable;

@Immutable
final class AutoValue_ViewData extends ViewData
{
    private final View view;
    private final Map<List<TagValue>, AggregationData> aggregationMap;
    private final AggregationWindowData windowData;
    private final Timestamp start;
    private final Timestamp end;
    
    AutoValue_ViewData(final View view, final Map<List<TagValue>, AggregationData> aggregationMap, final AggregationWindowData windowData, final Timestamp start, final Timestamp end) {
        if (view == null) {
            throw new NullPointerException("Null view");
        }
        this.view = view;
        if (aggregationMap == null) {
            throw new NullPointerException("Null aggregationMap");
        }
        this.aggregationMap = aggregationMap;
        if (windowData == null) {
            throw new NullPointerException("Null windowData");
        }
        this.windowData = windowData;
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
    public View getView() {
        return this.view;
    }
    
    @Override
    public Map<List<TagValue>, AggregationData> getAggregationMap() {
        return this.aggregationMap;
    }
    
    @Deprecated
    @Override
    public AggregationWindowData getWindowData() {
        return this.windowData;
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
        return "ViewData{view=" + this.view + ", aggregationMap=" + this.aggregationMap + ", windowData=" + this.windowData + ", start=" + this.start + ", end=" + this.end + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ViewData) {
            final ViewData that = (ViewData)o;
            return this.view.equals(that.getView()) && this.aggregationMap.equals(that.getAggregationMap()) && this.windowData.equals(that.getWindowData()) && this.start.equals(that.getStart()) && this.end.equals(that.getEnd());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.view.hashCode();
        h *= 1000003;
        h ^= this.aggregationMap.hashCode();
        h *= 1000003;
        h ^= this.windowData.hashCode();
        h *= 1000003;
        h ^= this.start.hashCode();
        h *= 1000003;
        h ^= this.end.hashCode();
        return h;
    }
}
