package io.opencensus.stats;

import javax.annotation.concurrent.Immutable;

@Deprecated
@Immutable
final class AutoValue_AggregationData_MeanData extends MeanData
{
    private final double mean;
    private final long count;
    
    AutoValue_AggregationData_MeanData(final double mean, final long count) {
        this.mean = mean;
        this.count = count;
    }
    
    @Override
    public double getMean() {
        return this.mean;
    }
    
    @Override
    public long getCount() {
        return this.count;
    }
    
    @Override
    public String toString() {
        return "MeanData{mean=" + this.mean + ", count=" + this.count + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof MeanData) {
            final MeanData that = (MeanData)o;
            return Double.doubleToLongBits(this.mean) == Double.doubleToLongBits(that.getMean()) && this.count == that.getCount();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.mean) >>> 32 ^ Double.doubleToLongBits(this.mean)));
        h *= 1000003;
        h = (int)((long)h ^ (this.count >>> 32 ^ this.count));
        return h;
    }
}
