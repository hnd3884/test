package io.opencensus.stats;

final class AutoValue_AggregationData_SumDataLong extends SumDataLong
{
    private final long sum;
    
    AutoValue_AggregationData_SumDataLong(final long sum) {
        this.sum = sum;
    }
    
    @Override
    public long getSum() {
        return this.sum;
    }
    
    @Override
    public String toString() {
        return "SumDataLong{sum=" + this.sum + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SumDataLong) {
            final SumDataLong that = (SumDataLong)o;
            return this.sum == that.getSum();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (this.sum >>> 32 ^ this.sum));
        return h;
    }
}
