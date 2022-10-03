package io.opencensus.trace;

import java.util.List;

final class AutoValue_Tracestate extends Tracestate
{
    private final List<Entry> entries;
    
    AutoValue_Tracestate(final List<Entry> entries) {
        if (entries == null) {
            throw new NullPointerException("Null entries");
        }
        this.entries = entries;
    }
    
    @Override
    public List<Entry> getEntries() {
        return this.entries;
    }
    
    @Override
    public String toString() {
        return "Tracestate{entries=" + this.entries + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Tracestate) {
            final Tracestate that = (Tracestate)o;
            return this.entries.equals(that.getEntries());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.entries.hashCode();
        return h;
    }
}
