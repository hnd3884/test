package io.opencensus.metrics.export;

final class AutoValue_Value_ValueDistribution extends ValueDistribution
{
    private final Distribution value;
    
    AutoValue_Value_ValueDistribution(final Distribution value) {
        if (value == null) {
            throw new NullPointerException("Null value");
        }
        this.value = value;
    }
    
    @Override
    Distribution getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "ValueDistribution{value=" + this.value + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ValueDistribution) {
            final ValueDistribution that = (ValueDistribution)o;
            return this.value.equals(that.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.value.hashCode();
        return h;
    }
}
