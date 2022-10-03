package io.opencensus.metrics.export;

import java.util.List;

final class AutoValue_Distribution_BucketOptions_ExplicitOptions extends ExplicitOptions
{
    private final List<Double> bucketBoundaries;
    
    AutoValue_Distribution_BucketOptions_ExplicitOptions(final List<Double> bucketBoundaries) {
        if (bucketBoundaries == null) {
            throw new NullPointerException("Null bucketBoundaries");
        }
        this.bucketBoundaries = bucketBoundaries;
    }
    
    @Override
    public List<Double> getBucketBoundaries() {
        return this.bucketBoundaries;
    }
    
    @Override
    public String toString() {
        return "ExplicitOptions{bucketBoundaries=" + this.bucketBoundaries + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ExplicitOptions) {
            final ExplicitOptions that = (ExplicitOptions)o;
            return this.bucketBoundaries.equals(that.getBucketBoundaries());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.bucketBoundaries.hashCode();
        return h;
    }
}
