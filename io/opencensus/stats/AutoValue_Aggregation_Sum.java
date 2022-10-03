package io.opencensus.stats;

final class AutoValue_Aggregation_Sum extends Sum
{
    @Override
    public String toString() {
        return "Sum{}";
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || o instanceof Sum;
    }
    
    @Override
    public int hashCode() {
        final int h = 1;
        return h;
    }
}
