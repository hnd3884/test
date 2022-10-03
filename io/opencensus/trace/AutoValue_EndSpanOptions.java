package io.opencensus.trace;

import javax.annotation.Nullable;

final class AutoValue_EndSpanOptions extends EndSpanOptions
{
    private final boolean sampleToLocalSpanStore;
    private final Status status;
    
    private AutoValue_EndSpanOptions(final boolean sampleToLocalSpanStore, @Nullable final Status status) {
        this.sampleToLocalSpanStore = sampleToLocalSpanStore;
        this.status = status;
    }
    
    @Override
    public boolean getSampleToLocalSpanStore() {
        return this.sampleToLocalSpanStore;
    }
    
    @Nullable
    @Override
    public Status getStatus() {
        return this.status;
    }
    
    @Override
    public String toString() {
        return "EndSpanOptions{sampleToLocalSpanStore=" + this.sampleToLocalSpanStore + ", status=" + this.status + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof EndSpanOptions) {
            final EndSpanOptions that = (EndSpanOptions)o;
            return this.sampleToLocalSpanStore == that.getSampleToLocalSpanStore() && ((this.status != null) ? this.status.equals(that.getStatus()) : (that.getStatus() == null));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= (this.sampleToLocalSpanStore ? 1231 : 1237);
        h *= 1000003;
        h ^= ((this.status == null) ? 0 : this.status.hashCode());
        return h;
    }
    
    static final class Builder extends EndSpanOptions.Builder
    {
        private Boolean sampleToLocalSpanStore;
        private Status status;
        
        @Override
        public EndSpanOptions.Builder setSampleToLocalSpanStore(final boolean sampleToLocalSpanStore) {
            this.sampleToLocalSpanStore = sampleToLocalSpanStore;
            return this;
        }
        
        @Override
        public EndSpanOptions.Builder setStatus(@Nullable final Status status) {
            this.status = status;
            return this;
        }
        
        @Override
        public EndSpanOptions build() {
            String missing = "";
            if (this.sampleToLocalSpanStore == null) {
                missing += " sampleToLocalSpanStore";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new AutoValue_EndSpanOptions(this.sampleToLocalSpanStore, this.status, null);
        }
    }
}
