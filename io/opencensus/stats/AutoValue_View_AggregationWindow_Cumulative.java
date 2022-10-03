package io.opencensus.stats;

import javax.annotation.concurrent.Immutable;

@Deprecated
@Immutable
final class AutoValue_View_AggregationWindow_Cumulative extends Cumulative
{
    @Override
    public String toString() {
        return "Cumulative{}";
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || o instanceof Cumulative;
    }
    
    @Override
    public int hashCode() {
        final int h = 1;
        return h;
    }
}
