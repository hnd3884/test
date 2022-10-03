package io.opencensus.stats;

import io.opencensus.metrics.data.Exemplar;
import java.util.List;

final class AutoValue_AggregationData_DistributionData extends DistributionData
{
    private final double mean;
    private final long count;
    private final double sumOfSquaredDeviations;
    private final List<Long> bucketCounts;
    private final List<Exemplar> exemplars;
    
    AutoValue_AggregationData_DistributionData(final double mean, final long count, final double sumOfSquaredDeviations, final List<Long> bucketCounts, final List<Exemplar> exemplars) {
        this.mean = mean;
        this.count = count;
        this.sumOfSquaredDeviations = sumOfSquaredDeviations;
        if (bucketCounts == null) {
            throw new NullPointerException("Null bucketCounts");
        }
        this.bucketCounts = bucketCounts;
        if (exemplars == null) {
            throw new NullPointerException("Null exemplars");
        }
        this.exemplars = exemplars;
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
    public double getSumOfSquaredDeviations() {
        return this.sumOfSquaredDeviations;
    }
    
    @Override
    public List<Long> getBucketCounts() {
        return this.bucketCounts;
    }
    
    @Override
    public List<Exemplar> getExemplars() {
        return this.exemplars;
    }
    
    @Override
    public String toString() {
        return "DistributionData{mean=" + this.mean + ", count=" + this.count + ", sumOfSquaredDeviations=" + this.sumOfSquaredDeviations + ", bucketCounts=" + this.bucketCounts + ", exemplars=" + this.exemplars + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DistributionData) {
            final DistributionData that = (DistributionData)o;
            return Double.doubleToLongBits(this.mean) == Double.doubleToLongBits(that.getMean()) && this.count == that.getCount() && Double.doubleToLongBits(this.sumOfSquaredDeviations) == Double.doubleToLongBits(that.getSumOfSquaredDeviations()) && this.bucketCounts.equals(that.getBucketCounts()) && this.exemplars.equals(that.getExemplars());
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
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.sumOfSquaredDeviations) >>> 32 ^ Double.doubleToLongBits(this.sumOfSquaredDeviations)));
        h *= 1000003;
        h ^= this.bucketCounts.hashCode();
        h *= 1000003;
        h ^= this.exemplars.hashCode();
        return h;
    }
}
