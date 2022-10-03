package io.opencensus.metrics.export;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import io.opencensus.internal.Utils;
import io.opencensus.metrics.LabelKey;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class MetricDescriptor
{
    MetricDescriptor() {
    }
    
    public static MetricDescriptor create(final String name, final String description, final String unit, final Type type, final List<LabelKey> labelKeys) {
        Utils.checkListElementNotNull((List<Object>)Utils.checkNotNull((List<T>)labelKeys, "labelKeys"), "labelKey");
        return new AutoValue_MetricDescriptor(name, description, unit, type, Collections.unmodifiableList((List<? extends LabelKey>)new ArrayList<LabelKey>(labelKeys)));
    }
    
    public abstract String getName();
    
    public abstract String getDescription();
    
    public abstract String getUnit();
    
    public abstract Type getType();
    
    public abstract List<LabelKey> getLabelKeys();
    
    public enum Type
    {
        GAUGE_INT64, 
        GAUGE_DOUBLE, 
        GAUGE_DISTRIBUTION, 
        CUMULATIVE_INT64, 
        CUMULATIVE_DOUBLE, 
        CUMULATIVE_DISTRIBUTION, 
        SUMMARY;
    }
}
