package io.opencensus.metrics.export;

final class AutoValue_Value_ValueLong extends ValueLong
{
    private final long value;
    
    AutoValue_Value_ValueLong(final long value) {
        this.value = value;
    }
    
    @Override
    long getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "ValueLong{value=" + this.value + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ValueLong) {
            final ValueLong that = (ValueLong)o;
            return this.value == that.getValue();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (this.value >>> 32 ^ this.value));
        return h;
    }
}
