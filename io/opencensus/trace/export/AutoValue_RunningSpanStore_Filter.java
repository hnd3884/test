package io.opencensus.trace.export;

final class AutoValue_RunningSpanStore_Filter extends RunningSpanStore.Filter
{
    private final String spanName;
    private final int maxSpansToReturn;
    
    AutoValue_RunningSpanStore_Filter(final String spanName, final int maxSpansToReturn) {
        if (spanName == null) {
            throw new NullPointerException("Null spanName");
        }
        this.spanName = spanName;
        this.maxSpansToReturn = maxSpansToReturn;
    }
    
    @Override
    public String getSpanName() {
        return this.spanName;
    }
    
    @Override
    public int getMaxSpansToReturn() {
        return this.maxSpansToReturn;
    }
    
    @Override
    public String toString() {
        return "Filter{spanName=" + this.spanName + ", maxSpansToReturn=" + this.maxSpansToReturn + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RunningSpanStore.Filter) {
            final RunningSpanStore.Filter that = (RunningSpanStore.Filter)o;
            return this.spanName.equals(that.getSpanName()) && this.maxSpansToReturn == that.getMaxSpansToReturn();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.spanName.hashCode();
        h *= 1000003;
        h ^= this.maxSpansToReturn;
        return h;
    }
}
