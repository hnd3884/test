package io.opencensus.stats;

final class AutoValue_AggregationData_LastValueDataLong extends LastValueDataLong
{
    private final long lastValue;
    
    AutoValue_AggregationData_LastValueDataLong(final long lastValue) {
        this.lastValue = lastValue;
    }
    
    @Override
    public long getLastValue() {
        return this.lastValue;
    }
    
    @Override
    public String toString() {
        return "LastValueDataLong{lastValue=" + this.lastValue + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof LastValueDataLong) {
            final LastValueDataLong that = (LastValueDataLong)o;
            return this.lastValue == that.getLastValue();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (this.lastValue >>> 32 ^ this.lastValue));
        return h;
    }
}
