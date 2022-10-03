package io.opencensus.metrics.export;

import javax.annotation.Nullable;
import java.util.List;

final class AutoValue_Distribution extends Distribution
{
    private final long count;
    private final double sum;
    private final double sumOfSquaredDeviations;
    private final BucketOptions bucketOptions;
    private final List<Bucket> buckets;
    
    AutoValue_Distribution(final long count, final double sum, final double sumOfSquaredDeviations, @Nullable final BucketOptions bucketOptions, final List<Bucket> buckets) {
        this.count = count;
        this.sum = sum;
        this.sumOfSquaredDeviations = sumOfSquaredDeviations;
        this.bucketOptions = bucketOptions;
        if (buckets == null) {
            throw new NullPointerException("Null buckets");
        }
        this.buckets = buckets;
    }
    
    @Override
    public long getCount() {
        return this.count;
    }
    
    @Override
    public double getSum() {
        return this.sum;
    }
    
    @Override
    public double getSumOfSquaredDeviations() {
        return this.sumOfSquaredDeviations;
    }
    
    @Nullable
    @Override
    public BucketOptions getBucketOptions() {
        return this.bucketOptions;
    }
    
    @Override
    public List<Bucket> getBuckets() {
        return this.buckets;
    }
    
    @Override
    public String toString() {
        return "Distribution{count=" + this.count + ", sum=" + this.sum + ", sumOfSquaredDeviations=" + this.sumOfSquaredDeviations + ", bucketOptions=" + this.bucketOptions + ", buckets=" + this.buckets + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Distribution) {
            final Distribution that = (Distribution)o;
            if (this.count == that.getCount() && Double.doubleToLongBits(this.sum) == Double.doubleToLongBits(that.getSum()) && Double.doubleToLongBits(this.sumOfSquaredDeviations) == Double.doubleToLongBits(that.getSumOfSquaredDeviations())) {
                if (this.bucketOptions == null) {
                    if (that.getBucketOptions() != null) {
                        return false;
                    }
                }
                else if (!this.bucketOptions.equals(that.getBucketOptions())) {
                    return false;
                }
                if (this.buckets.equals(that.getBuckets())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (this.count >>> 32 ^ this.count));
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.sum) >>> 32 ^ Double.doubleToLongBits(this.sum)));
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.sumOfSquaredDeviations) >>> 32 ^ Double.doubleToLongBits(this.sumOfSquaredDeviations)));
        h *= 1000003;
        h ^= ((this.bucketOptions == null) ? 0 : this.bucketOptions.hashCode());
        h *= 1000003;
        h ^= this.buckets.hashCode();
        return h;
    }
}
