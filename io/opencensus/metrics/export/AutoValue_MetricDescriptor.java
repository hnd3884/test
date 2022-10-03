package io.opencensus.metrics.export;

import io.opencensus.metrics.LabelKey;
import java.util.List;

final class AutoValue_MetricDescriptor extends MetricDescriptor
{
    private final String name;
    private final String description;
    private final String unit;
    private final Type type;
    private final List<LabelKey> labelKeys;
    
    AutoValue_MetricDescriptor(final String name, final String description, final String unit, final Type type, final List<LabelKey> labelKeys) {
        if (name == null) {
            throw new NullPointerException("Null name");
        }
        this.name = name;
        if (description == null) {
            throw new NullPointerException("Null description");
        }
        this.description = description;
        if (unit == null) {
            throw new NullPointerException("Null unit");
        }
        this.unit = unit;
        if (type == null) {
            throw new NullPointerException("Null type");
        }
        this.type = type;
        if (labelKeys == null) {
            throw new NullPointerException("Null labelKeys");
        }
        this.labelKeys = labelKeys;
    }
    
    @Override
    public String getName() {
        return this.name;
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
    public Type getType() {
        return this.type;
    }
    
    @Override
    public List<LabelKey> getLabelKeys() {
        return this.labelKeys;
    }
    
    @Override
    public String toString() {
        return "MetricDescriptor{name=" + this.name + ", description=" + this.description + ", unit=" + this.unit + ", type=" + this.type + ", labelKeys=" + this.labelKeys + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MetricDescriptor) {
            final MetricDescriptor that = (MetricDescriptor)o;
            return this.name.equals(that.getName()) && this.description.equals(that.getDescription()) && this.unit.equals(that.getUnit()) && this.type.equals(that.getType()) && this.labelKeys.equals(that.getLabelKeys());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.name.hashCode();
        h *= 1000003;
        h ^= this.description.hashCode();
        h *= 1000003;
        h ^= this.unit.hashCode();
        h *= 1000003;
        h ^= this.type.hashCode();
        h *= 1000003;
        h ^= this.labelKeys.hashCode();
        return h;
    }
}
