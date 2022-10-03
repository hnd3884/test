package io.opencensus.stats;

final class AutoValue_AggregationData_LastValueDataDouble extends LastValueDataDouble
{
    private final double lastValue;
    
    AutoValue_AggregationData_LastValueDataDouble(final double lastValue) {
        this.lastValue = lastValue;
    }
    
    @Override
    public double getLastValue() {
        return this.lastValue;
    }
    
    @Override
    public String toString() {
        return "LastValueDataDouble{lastValue=" + this.lastValue + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof LastValueDataDouble) {
            final LastValueDataDouble that = (LastValueDataDouble)o;
            return Double.doubleToLongBits(this.lastValue) == Double.doubleToLongBits(that.getLastValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.lastValue) >>> 32 ^ Double.doubleToLongBits(this.lastValue)));
        return h;
    }
}
