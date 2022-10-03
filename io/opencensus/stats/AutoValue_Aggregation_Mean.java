package io.opencensus.stats;

import javax.annotation.concurrent.Immutable;

@Deprecated
@Immutable
final class AutoValue_Aggregation_Mean extends Mean
{
    @Override
    public String toString() {
        return "Mean{}";
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || o instanceof Mean;
    }
    
    @Override
    public int hashCode() {
        final int h = 1;
        return h;
    }
}
