package io.opencensus.metrics.export;

import javax.annotation.Nullable;

final class AutoValue_Summary extends Summary
{
    private final Long count;
    private final Double sum;
    private final Snapshot snapshot;
    
    AutoValue_Summary(@Nullable final Long count, @Nullable final Double sum, final Snapshot snapshot) {
        this.count = count;
        this.sum = sum;
        if (snapshot == null) {
            throw new NullPointerException("Null snapshot");
        }
        this.snapshot = snapshot;
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
    public Snapshot getSnapshot() {
        return this.snapshot;
    }
    
    @Override
    public String toString() {
        return "Summary{count=" + this.count + ", sum=" + this.sum + ", snapshot=" + this.snapshot + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Summary) {
            final Summary that = (Summary)o;
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
            if (this.snapshot.equals(that.getSnapshot())) {
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
        h ^= this.snapshot.hashCode();
        return h;
    }
}
