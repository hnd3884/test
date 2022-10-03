package io.opencensus.metrics.export;

import javax.annotation.Nullable;
import java.util.List;

final class AutoValue_Summary_Snapshot extends Summary.Snapshot
{
    private final Long count;
    private final Double sum;
    private final List<ValueAtPercentile> valueAtPercentiles;
    
    AutoValue_Summary_Snapshot(@Nullable final Long count, @Nullable final Double sum, final List<ValueAtPercentile> valueAtPercentiles) {
        this.count = count;
        this.sum = sum;
        if (valueAtPercentiles == null) {
            throw new NullPointerException("Null valueAtPercentiles");
        }
        this.valueAtPercentiles = valueAtPercentiles;
    }
    
    @Nullable
    @Override
    public Long getCount() {
        return this.count;
    }
    
    @Nullable
    @Override
    public Double getSum() {
        return this.sum;
    }
    
    @Override
    public List<ValueAtPercentile> getValueAtPercentiles() {
        return this.valueAtPercentiles;
    }
    
    @Override
    public String toString() {
        return "Snapshot{count=" + this.count + ", sum=" + this.sum + ", valueAtPercentiles=" + this.valueAtPercentiles + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Summary.Snapshot) {
            final Summary.Snapshot that = (Summary.Snapshot)o;
            if (this.count == null) {
                if (that.getCount() != null) {
                    return false;
                }
            }
            else if (!this.count.equals(that.getCount())) {
                return false;
            }
            if (this.sum == null) {
                if (that.getSum() != null) {
                    return false;
                }
            }
            else if (!this.sum.equals(that.getSum())) {
                return false;
            }
            if (this.valueAtPercentiles.equals(that.getValueAtPercentiles())) {
                return true;
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= ((this.count == null) ? 0 : this.count.hashCode());
        h *= 1000003;
        h ^= ((this.sum == null) ? 0 : this.sum.hashCode());
        h *= 1000003;
        h ^= this.valueAtPercentiles.hashCode();
        return h;
    }
}
