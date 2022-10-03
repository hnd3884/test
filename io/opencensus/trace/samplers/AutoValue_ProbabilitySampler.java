package io.opencensus.trace.samplers;

final class AutoValue_ProbabilitySampler extends ProbabilitySampler
{
    private final double probability;
    private final long idUpperBound;
    
    AutoValue_ProbabilitySampler(final double probability, final long idUpperBound) {
        this.probability = probability;
        this.idUpperBound = idUpperBound;
    }
    
    @Override
    double getProbability() {
        return this.probability;
    }
    
    @Override
    long getIdUpperBound() {
        return this.idUpperBound;
    }
    
    @Override
    public String toString() {
        return "ProbabilitySampler{probability=" + this.probability + ", idUpperBound=" + this.idUpperBound + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ProbabilitySampler) {
            final ProbabilitySampler that = (ProbabilitySampler)o;
            return Double.doubleToLongBits(this.probability) == Double.doubleToLongBits(that.getProbability()) && this.idUpperBound == that.getIdUpperBound();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (Double.doubleToLongBits(this.probability) >>> 32 ^ Double.doubleToLongBits(this.probability)));
        h *= 1000003;
        h = (int)((long)h ^ (this.idUpperBound >>> 32 ^ this.idUpperBound));
        return h;
    }
}
