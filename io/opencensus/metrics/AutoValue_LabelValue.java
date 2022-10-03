package io.opencensus.metrics;

import javax.annotation.Nullable;

final class AutoValue_LabelValue extends LabelValue
{
    private final String value;
    
    AutoValue_LabelValue(@Nullable final String value) {
        this.value = value;
    }
    
    @Nullable
    @Override
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "LabelValue{value=" + this.value + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof LabelValue) {
            final LabelValue that = (LabelValue)o;
            return (this.value == null) ? (that.getValue() == null) : this.value.equals(that.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= ((this.value == null) ? 0 : this.value.hashCode());
        return h;
    }
}
