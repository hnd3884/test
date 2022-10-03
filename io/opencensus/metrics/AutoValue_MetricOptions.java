package io.opencensus.metrics;

import java.util.Map;
import java.util.List;

final class AutoValue_MetricOptions extends MetricOptions
{
    private final String description;
    private final String unit;
    private final List<LabelKey> labelKeys;
    private final Map<LabelKey, LabelValue> constantLabels;
    
    private AutoValue_MetricOptions(final String description, final String unit, final List<LabelKey> labelKeys, final Map<LabelKey, LabelValue> constantLabels) {
        this.description = description;
        this.unit = unit;
        this.labelKeys = labelKeys;
        this.constantLabels = constantLabels;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    @Override
    public String getUnit() {
        return this.unit;
    }
    
    @Override
    public List<LabelKey> getLabelKeys() {
        return this.labelKeys;
    }
    
    @Override
    public Map<LabelKey, LabelValue> getConstantLabels() {
        return this.constantLabels;
    }
    
    @Override
    public String toString() {
        return "MetricOptions{description=" + this.description + ", unit=" + this.unit + ", labelKeys=" + this.labelKeys + ", constantLabels=" + this.constantLabels + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MetricOptions) {
            final MetricOptions that = (MetricOptions)o;
            return this.description.equals(that.getDescription()) && this.unit.equals(that.getUnit()) && this.labelKeys.equals(that.getLabelKeys()) && this.constantLabels.equals(that.getConstantLabels());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.description.hashCode();
        h *= 1000003;
        h ^= this.unit.hashCode();
        h *= 1000003;
        h ^= this.labelKeys.hashCode();
        h *= 1000003;
        h ^= this.constantLabels.hashCode();
        return h;
    }
    
    static final class Builder extends MetricOptions.Builder
    {
        private String description;
        private String unit;
        private List<LabelKey> labelKeys;
        private Map<LabelKey, LabelValue> constantLabels;
        
        @Override
        public MetricOptions.Builder setDescription(final String description) {
            if (description == null) {
                throw new NullPointerException("Null description");
            }
            this.description = description;
            return this;
        }
        
        @Override
        public MetricOptions.Builder setUnit(final String unit) {
            if (unit == null) {
                throw new NullPointerException("Null unit");
            }
            this.unit = unit;
            return this;
        }
        
        @Override
        public MetricOptions.Builder setLabelKeys(final List<LabelKey> labelKeys) {
            if (labelKeys == null) {
                throw new NullPointerException("Null labelKeys");
            }
            this.labelKeys = labelKeys;
            return this;
        }
        
        @Override
        List<LabelKey> getLabelKeys() {
            if (this.labelKeys == null) {
                throw new IllegalStateException("Property \"labelKeys\" has not been set");
            }
            return this.labelKeys;
        }
        
        @Override
        public MetricOptions.Builder setConstantLabels(final Map<LabelKey, LabelValue> constantLabels) {
            if (constantLabels == null) {
                throw new NullPointerException("Null constantLabels");
            }
            this.constantLabels = constantLabels;
            return this;
        }
        
        @Override
        Map<LabelKey, LabelValue> getConstantLabels() {
            if (this.constantLabels == null) {
                throw new IllegalStateException("Property \"constantLabels\" has not been set");
            }
            return this.constantLabels;
        }
        
        @Override
        MetricOptions autoBuild() {
            String missing = "";
            if (this.description == null) {
                missing += " description";
            }
            if (this.unit == null) {
                missing += " unit";
            }
            if (this.labelKeys == null) {
                missing += " labelKeys";
            }
            if (this.constantLabels == null) {
                missing += " constantLabels";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_MetricOptions(this.description, this.unit, this.labelKeys, this.constantLabels, null);
        }
    }
}
