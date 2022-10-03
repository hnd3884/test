package io.opencensus.stats;

final class AutoValue_AggregationData_CountData extends CountData
{
    private final long count;
    
    AutoValue_AggregationData_CountData(final long count) {
        this.count = count;
    }
    
    @Override
    public long getCount() {
        return this.count;
    }
    
    @Override
    public String toString() {
        return "CountData{count=" + this.count + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof CountData) {
            final CountData that = (CountData)o;
            return this.count == that.getCount();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (this.count >>> 32 ^ this.count));
        return h;
    }
}
