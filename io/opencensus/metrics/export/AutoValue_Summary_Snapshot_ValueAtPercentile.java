package io.opencensus.metrics.export;

final class AutoValue_Summary_Snapshot_ValueAtPercentile extends Summary.Snapshot.ValueAtPercentile
{
    private final double percentile;
    private final double value;
    
    AutoValue_Summary_Snapshot_ValueAtPercentile(final double percentile, final double value) {
        this.percentile = percentile;
        this.value = value;
    }
    
    @Override
    public double getPercentile() {
        return this.percentile;
    }
    
    @Override
    public double getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "ValueAtPercentile{percentile=" + this.percentile + ", value=" + this.value + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Summary.Snapshot.ValueAtPercentile) {
            final Summary.Snapshot.ValueAtPercentile that = (Summary.Snapshot.ValueAtPercentile)o;
            return Double.doubleToLongBits(this.percentile) == Double.doubleToLongBits(that.getPercentile()) && Double.doubleToLongBits(this.value) == Double.doubleToLongBits(that.getValue());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.percentile) >>> 32 ^ Double.doubleToLongBits(this.percentile)));
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.value) >>> 32 ^ Double.doubleToLongBits(this.value)));
        return h;
    }
}
