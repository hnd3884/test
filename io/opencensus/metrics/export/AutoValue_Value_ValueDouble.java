package io.opencensus.metrics.export;

final class AutoValue_Value_ValueDouble extends ValueDouble
{
    private final double value;
    
    AutoValue_Value_ValueDouble(final double value) {
        this.value = value;
    }
    
    @Override
    double getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "ValueDouble{value=" + this.value + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ValueDouble) {
            final ValueDouble that = (ValueDouble)o;
            return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.value) >>> 32 ^ Double.doubleToLongBits(this.value)));
        return h;
    }
}
