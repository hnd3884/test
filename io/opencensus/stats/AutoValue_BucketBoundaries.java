package io.opencensus.stats;

import java.util.List;

final class AutoValue_BucketBoundaries extends BucketBoundaries
{
    private final List<Double> boundaries;
    
    AutoValue_BucketBoundaries(final List<Double> boundaries) {
        if (boundaries == null) {
            throw new NullPointerException("Null boundaries");
        }
        this.boundaries = boundaries;
    }
    
    @Override
    public List<Double> getBoundaries() {
        return this.boundaries;
    }
    
    @Override
    public String toString() {
        return "BucketBoundaries{boundaries=" + this.boundaries + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof BucketBoundaries) {
            final BucketBoundaries that = (BucketBoundaries)o;
            return this.boundaries.equals(that.getBoundaries());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.boundaries.hashCode();
        return h;
    }
}
