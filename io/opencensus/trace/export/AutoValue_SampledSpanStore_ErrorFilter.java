package io.opencensus.trace.export;

import javax.annotation.Nullable;
import io.opencensus.trace.Status;

final class AutoValue_SampledSpanStore_ErrorFilter extends SampledSpanStore.ErrorFilter
{
    private final String spanName;
    private final Status.CanonicalCode canonicalCode;
    private final int maxSpansToReturn;
    
    AutoValue_SampledSpanStore_ErrorFilter(final String spanName, @Nullable final Status.CanonicalCode canonicalCode, final int maxSpansToReturn) {
        if (spanName == null) {
            throw new NullPointerException("Null spanName");
        }
        this.spanName = spanName;
        this.canonicalCode = canonicalCode;
        this.maxSpansToReturn = maxSpansToReturn;
    }
    
    @Override
    public String getSpanName() {
        return this.spanName;
    }
    
    @Nullable
    @Override
    public Status.CanonicalCode getCanonicalCode() {
        return this.canonicalCode;
    }
    
    @Override
    public int getMaxSpansToReturn() {
        return this.maxSpansToReturn;
    }
    
    @Override
    public String toString() {
        return "ErrorFilter{spanName=" + this.spanName + ", canonicalCode=" + this.canonicalCode + ", maxSpansToReturn=" + this.maxSpansToReturn + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SampledSpanStore.ErrorFilter) {
            final SampledSpanStore.ErrorFilter that = (SampledSpanStore.ErrorFilter)o;
            if (this.spanName.equals(that.getSpanName())) {
                if (this.canonicalCode == null) {
                    if (that.getCanonicalCode() != null) {
                        return false;
                    }
                }
                else if (!this.canonicalCode.equals(that.getCanonicalCode())) {
                    return false;
                }
                if (this.maxSpansToReturn == that.getMaxSpansToReturn()) {
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
        h ^= this.spanName.hashCode();
        h *= 1000003;
        h ^= ((this.canonicalCode == null) ? 0 : this.canonicalCode.hashCode());
        h *= 1000003;
        h ^= this.maxSpansToReturn;
        return h;
    }
}
