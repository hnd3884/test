package io.opencensus.stats;

final class AutoValue_Aggregation_Count extends Count
{
    @Override
    public String toString() {
        return "Count{}";
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || o instanceof Count;
    }
    
    @Override
    public int hashCode() {
        final int h = 1;
        return h;
    }
}
