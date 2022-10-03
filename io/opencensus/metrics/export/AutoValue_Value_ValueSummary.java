package io.opencensus.metrics.export;

final class AutoValue_Value_ValueSummary extends ValueSummary
{
    private final Summary value;
    
    AutoValue_Value_ValueSummary(final Summary value) {
        if (value == null) {
            throw new NullPointerException("Null value");
        }
        this.value = value;
    }
    
    @Override
    Summary getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "ValueSummary{value=" + this.value + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ValueSummary) {
            final ValueSummary that = (ValueSummary)o;
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
