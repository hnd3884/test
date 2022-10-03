package io.opencensus.metrics;

import java.util.Iterator;
import java.util.HashSet;
import io.opencensus.internal.Utils;
import java.util.LinkedHashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class MetricOptions
{
    public abstract String getDescription();
    
    public abstract String getUnit();
    
    public abstract List<LabelKey> getLabelKeys();
    
    public abstract Map<LabelKey, LabelValue> getConstantLabels();
    
    public static Builder builder() {
        return new AutoValue_MetricOptions.Builder().setDescription("").setUnit("1").setLabelKeys(Collections.emptyList()).setConstantLabels(Collections.emptyMap());
    }
    
    MetricOptions() {
    }
    
    public abstract static class Builder
    {
        public abstract Builder setDescription(final String p0);
        
        public abstract Builder setUnit(final String p0);
        
        public abstract Builder setLabelKeys(final List<LabelKey> p0);
        
        public abstract Builder setConstantLabels(final Map<LabelKey, LabelValue> p0);
        
        abstract Map<LabelKey, LabelValue> getConstantLabels();
        
        abstract List<LabelKey> getLabelKeys();
        
        abstract MetricOptions autoBuild();
        
        public MetricOptions build() {
            this.setLabelKeys(Collections.unmodifiableList((List<? extends LabelKey>)new ArrayList<LabelKey>(this.getLabelKeys())));
            this.setConstantLabels(Collections.unmodifiableMap((Map<? extends LabelKey, ? extends LabelValue>)new LinkedHashMap<LabelKey, LabelValue>(this.getConstantLabels())));
            final MetricOptions options = this.autoBuild();
            Utils.checkListElementNotNull(options.getLabelKeys(), "labelKeys elements");
            Utils.checkMapElementNotNull(options.getConstantLabels(), "constantLabels elements");
            final HashSet<String> labelKeyNamesMap = new HashSet<String>();
            for (final LabelKey labelKey : options.getLabelKeys()) {
                if (labelKeyNamesMap.contains(labelKey.getKey())) {
                    throw new IllegalArgumentException("Invalid LabelKey in labelKeys");
                }
                labelKeyNamesMap.add(labelKey.getKey());
            }
            for (final Map.Entry<LabelKey, LabelValue> constantLabel : options.getConstantLabels().entrySet()) {
                if (labelKeyNamesMap.contains(constantLabel.getKey().getKey())) {
                    throw new IllegalArgumentException("Invalid LabelKey in constantLabels");
                }
                labelKeyNamesMap.add(constantLabel.getKey().getKey());
            }
            return options;
        }
        
        Builder() {
        }
    }
}
