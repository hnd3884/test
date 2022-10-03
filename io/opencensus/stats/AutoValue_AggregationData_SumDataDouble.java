package io.opencensus.stats;

final class AutoValue_AggregationData_SumDataDouble extends SumDataDouble
{
    private final double sum;
    
    AutoValue_AggregationData_SumDataDouble(final double sum) {
        this.sum = sum;
    }
    
    @Override
    public double getSum() {
        return this.sum;
    }
    
    @Override
    public String toString() {
        return "SumDataDouble{sum=" + this.sum + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SumDataDouble) {
            final SumDataDouble that = (SumDataDouble)o;
            return Double.doubleToLongBits(this.sum) == Double.doubleToLongBits(that.getSum());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.sum) >>> 32 ^ Double.doubleToLongBits(this.sum)));
        return h;
    }
}
