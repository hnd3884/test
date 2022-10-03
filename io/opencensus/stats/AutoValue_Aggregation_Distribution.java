package io.opencensus.stats;

final class AutoValue_Aggregation_Distribution extends Distribution
{
    private final BucketBoundaries bucketBoundaries;
    
    AutoValue_Aggregation_Distribution(final BucketBoundaries bucketBoundaries) {
        if (bucketBoundaries == null) {
            throw new NullPointerException("Null bucketBoundaries");
        }
        this.bucketBoundaries = bucketBoundaries;
    }
    
    @Override
    public BucketBoundaries getBucketBoundaries() {
        return this.bucketBoundaries;
    }
    
    @Override
    public String toString() {
        return "Distribution{bucketBoundaries=" + this.bucketBoundaries + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Distribution) {
            final Distribution that = (Distribution)o;
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
